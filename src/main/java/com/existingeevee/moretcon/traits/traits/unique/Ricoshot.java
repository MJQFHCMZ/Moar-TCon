package com.existingeevee.moretcon.traits.traits.unique;

import java.util.List;

import com.existingeevee.moretcon.client.actions.RicoshotEffectAction;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.events.TinkerProjectileImpactEvent;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.traits.IProjectileTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class Ricoshot extends AbstractTrait implements IProjectileTrait {

	public Ricoshot() {
		super(MiscUtils.createNonConflictiveName("ricoshot"), 0xb2a1ff);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onLaunch(EntityProjectileBase projectile, World world, EntityLivingBase shooter) {
		projectile.bounceOnNoDamage = false;
	}

	@Override
	public void onProjectileUpdate(EntityProjectileBase projectile, World world, ItemStack toolStack) {
		projectile.setIsCritical(false);

		NBTTagCompound comp = projectile.getEntityData().getCompoundTag(this.getIdentifier());
		
		if (!world.isRemote && (comp.getInteger("Bounces") < 4 || !projectile.inGround)) {

			int color = getToolMaterials(projectile.tinkerProjectile.getItemStack()).get(0).materialTextColor;

			if (comp.hasKey("prevX", NBT.TAG_DOUBLE)) {

				double pX = comp.getDouble("prevX");
				double pY = comp.getDouble("prevY");
				double pZ = comp.getDouble("prevZ");

				RicoshotEffectAction.INSTANCE.run(world, pX, pY, pZ, RicoshotEffectAction.encode(projectile.posX, projectile.posY, projectile.posZ, color));
			}

			comp.setDouble("prevX", projectile.posX);
			comp.setDouble("prevY", projectile.posY);
			comp.setDouble("prevZ", projectile.posZ);
		}

		if (comp.getInteger("Bounces") >= 4) {
			return;
		}
		
		if (projectile.inGround) {

			Vec3d vel = new Vec3d(comp.getDouble("LastMotX"), comp.getDouble("LastMotY"), comp.getDouble("LastMotZ"));
			
			RayTraceResult result = world.rayTraceBlocks(projectile.getPositionVector(), projectile.getPositionVector().add(vel));
			
			if (result != null) {
				
				double bounceCoeff = 0.8;

				EntityLivingBase closest = null;
				double closestLenSq = Double.POSITIVE_INFINITY;

				double maxDist = 15;
				
				for (Entity ent : world.getEntitiesWithinAABBExcludingEntity(projectile.shootingEntity, projectile.getEntityBoundingBox().grow(maxDist))) {
					if (ent instanceof EntityTameable && ((EntityTameable) ent).getOwner() == projectile.shootingEntity) {
						continue;
					}
					Team team = projectile.shootingEntity == null ? null : projectile.shootingEntity.getTeam();
					if (team != null && !team.getAllowFriendlyFire()) {
						if (ent.getTeam() == team) {
							continue;
						}
					}

					if (ent instanceof EntityLivingBase && ((EntityLivingBase) ent).canEntityBeSeen(projectile) && ((EntityLivingBase) ent).isEntityAlive() && ((EntityLivingBase) ent).getHealth() > 0) {
						double dSq = ent.getDistanceSq(projectile);
						if (dSq < maxDist * maxDist && dSq < closestLenSq) {
							closest = (EntityLivingBase) ent;
							closestLenSq = dSq;
						}
					}
				}

				if (closest != null) {
					Vec3d target = MiscUtils.getCenter(closest.getEntityBoundingBox());
					vel = target.subtract(projectile.getPositionVector()).normalize().scale(vel.lengthVector()).scale(bounceCoeff);
				} else {
					switch (result.sideHit.getAxis()) {
					case X:
						vel = new Vec3d(vel.x * -bounceCoeff, vel.y, vel.z);
						break;
					case Y:
						vel = new Vec3d(vel.x, vel.y * -bounceCoeff, vel.z);
						break;
					case Z:
						vel = new Vec3d(vel.x, vel.y, vel.z * -bounceCoeff);
						break;
					}
				}
										
				projectile.inGround = false; 
				projectile.defused = false; // we back to shootin
				projectile.arrowShake = 0;
								
				if (!world.isRemote) {
					projectile.motionX = vel.x;
					projectile.motionY = vel.y + 0.25;
					projectile.motionZ = vel.z;
				}
								
				comp.setDouble("LastMotX", projectile.motionX);
				comp.setDouble("LastMotY", projectile.motionY);
				comp.setDouble("LastMotZ", projectile.motionZ);

				comp.setInteger("Bounces", comp.getInteger("Bounces") + 1);
			}
		} else {

			comp.setDouble("LastMotX", projectile.motionX);
			comp.setDouble("LastMotY", projectile.motionY);
			comp.setDouble("LastMotZ", projectile.motionZ);
		}
		 
		projectile.getEntityData().setTag(this.getIdentifier(), comp);
	}

	public static List<Material> getToolMaterials(ItemStack stack) {
		return TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
	}

	@Override
	public void onMovement(EntityProjectileBase projectile, World world, double slowdown) {

	}

	@Override
	public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammoStack, EntityLivingBase attacker, Entity target, double impactSpeed) {
		NBTTagCompound comp = projectile.getEntityData().getCompoundTag(this.getIdentifier());

		if (!world.isRemote) {
			int color = getToolMaterials(projectile.tinkerProjectile.getItemStack()).get(0).materialTextColor;

			Vec3d box = MiscUtils.getCenter(target.getEntityBoundingBox());

			RicoshotEffectAction.INSTANCE.run(world, projectile.posX, projectile.posY, projectile.posZ, RicoshotEffectAction.encode(box.x, box.y, box.z, color));
		}

		int bounces = comp.getInteger("Bounces");
		if (bounces > 0)
			attackEntitySecondary(DamageSource.causeIndirectDamage(projectile, attacker), (float) Math.pow(1.65, bounces), target, true, false);
	}

	@SubscribeEvent
	public void onEntityHurt(TinkerProjectileImpactEvent ev) {
		if (ev.getEntity() instanceof EntityProjectileBase && this.isToolWithTrait(ev.getTool())) {
			NBTTagCompound comp = ev.getEntity().getEntityData().getCompoundTag(this.getIdentifier());

			if (comp.getInteger("Bounces") > 0 && ((EntityProjectileBase) ev.getEntity()).shootingEntity == ev.getRayTraceResult().entityHit) {
				ev.setCanceled(true);
			}
		}
	}
}

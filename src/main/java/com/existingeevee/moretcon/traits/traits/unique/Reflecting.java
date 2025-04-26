package com.existingeevee.moretcon.traits.traits.unique;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.traits.IProjectileTrait;

public class Reflecting extends AbstractTrait implements IProjectileTrait {

	public Reflecting() {
		super(MiscUtils.createNonConflictiveName("reflecting"), 0xffffff);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onLaunch(EntityProjectileBase projectile, World world, EntityLivingBase shooter) {
		projectile.bounceOnNoDamage = false;
	}

	@Override
	public void onProjectileUpdate(EntityProjectileBase projectile, World world, ItemStack toolStack) {
		NBTTagCompound comp = projectile.getEntityData().getCompoundTag(this.getIdentifier());

		if (projectile.inGround) {
			Vec3d vel = new Vec3d(comp.getDouble("LastMotX"), comp.getDouble("LastMotY"), comp.getDouble("LastMotZ"));
			
			RayTraceResult result = world.rayTraceBlocks(projectile.getPositionVector(), projectile.getPositionVector().add(vel));
			if (result != null) {
				
				double bounceCoeff = 0.8;
				
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
				
				if (comp.getInteger("Bounces") >= 4) {
					return;
				} else {
					projectile.inGround = false;
					projectile.defused = false; //we back to shootin
					projectile.arrowShake = 0;
					
					projectile.motionX = vel.x;
					projectile.motionY = vel.y;
					projectile.motionZ = vel.z;
					
					comp.setDouble("LastMotX", projectile.motionX);
					comp.setDouble("LastMotY", projectile.motionY);
					comp.setDouble("LastMotZ", projectile.motionZ);
					
					comp.setInteger("Bounces", comp.getInteger("Bounces") + 1);
					projectile.setIsCritical(comp.getBoolean("Crit"));
				}
			}
		} else {
			comp.setDouble("LastMotX", projectile.motionX);
			comp.setDouble("LastMotY", projectile.motionY);
			comp.setDouble("LastMotZ", projectile.motionZ);
			comp.setBoolean("Crit", projectile.getIsCritical());
		}
		
		projectile.getEntityData().setTag(this.getIdentifier(), comp);
	}

	@Override
	public void onMovement(EntityProjectileBase projectile, World world, double slowdown) {
		
	}

	@Override
	public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammoStack, EntityLivingBase attacker, Entity target, double impactSpeed) {
		NBTTagCompound comp = projectile.getEntityData().getCompoundTag(this.getIdentifier());
		int bounces = comp.getInteger("Bounces");
		if (bounces > 0)
			attackEntitySecondary(DamageSource.causeIndirectDamage(projectile, attacker), (float) Math.pow(2, bounces), target, true, false);
	}
	
	@SubscribeEvent
	public void onEntityHurt(LivingHurtEvent ev) {
		
	}
}

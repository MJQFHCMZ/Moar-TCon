package com.existingeevee.moretcon.traits.traits.unique;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

import com.existingeevee.moretcon.client.actions.ColoredDustAction;
import com.existingeevee.moretcon.other.StaticVars;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.events.ProjectileEvent;
import slimeknights.tconstruct.library.events.ProjectileEvent.OnLaunch;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.tools.ranged.BowCore;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class Dematerializing extends AbstractTrait {

	public Dematerializing() {
		super(MiscUtils.createNonConflictiveName("dematerializing"), 0);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean isToolWithTrait(ItemStack itemStack) {
		return super.isToolWithTrait(itemStack);
	}

	public static final Method onHit$EntityArrow = ObfuscationReflectionHelper.findMethod(EntityArrow.class, "func_184549_a", void.class, RayTraceResult.class);
	public static final Method baseProjectileSpeed$BowCore = ObfuscationReflectionHelper.findMethod(BowCore.class, "baseProjectileSpeed", float.class);

	@SubscribeEvent
	public void onLaunch(OnLaunch event) {
		EntityArrow arrow = event.projectileEntity instanceof EntityArrow ? (EntityArrow) event.projectileEntity : null;
		EntityLivingBase shooter = event.shooter;
		World world = arrow.world;

		if (!isToolWithTrait(event.launcher) || arrow == null || event.shooter == null || !(event.launcher.getItem() instanceof BowCore) || !(event.shooter instanceof EntityPlayer)) {
			return;
		}

		BowCore bow = (BowCore) event.launcher.getItem();
		float progress = bow.getDrawbackProgress(event.launcher, event.shooter);

		boolean fullyDrawn = progress >= 1;

		if (!fullyDrawn) {
			return;
		}

		if (!((EntityPlayer) shooter).capabilities.isCreativeMode) {
			ToolHelper.damageTool(event.launcher, 1, shooter);
		}
		event.setCanceled(true);
		
		ProjectileLauncherNBT launcherData = new ProjectileLauncherNBT(TagUtil.getToolTag(event.launcher));
		double dist = launcherData.range * 20;
		
		double posX = arrow.posX;
		double posY = arrow.posY;
		double posZ = arrow.posZ;
		
		arrow.onUpdate();
		Vec3d heading = new Vec3d(arrow.motionX, arrow.motionY, arrow.motionZ).normalize();
		arrow.motionX = heading.x * dist;
		arrow.motionY = heading.y * dist;
		arrow.motionZ = heading.z * dist;
		
		arrow.posX = posX;
		arrow.posY = posY;
		arrow.posZ = posZ;
		
		this.shoot(world, shooter, arrow, dist, event.launcher);
	}

	public void shoot(World world, EntityLivingBase shooter, EntityArrow arrow, double dist, ItemStack bow) {
		if (dist < 5)
			return;
		Vec3d heading = new Vec3d(arrow.motionX, arrow.motionY, arrow.motionZ).normalize();
		ProjectileLauncherNBT launcherData = new ProjectileLauncherNBT(TagUtil.getToolTag(bow));

		float progress = ((BowCore) bow.getItem()).getDrawbackProgress(bow, shooter);
		
		Vec3d start = arrow.getPositionVector();
		Vec3d end = start.add(heading.scale(dist));
		RayTraceResult firstTrace = world.rayTraceBlocks(start, end, false, true, true);
		world.playSound(null, shooter.getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 1, 2);

		boolean hitBlock = false;

		if (firstTrace != null) {
			end = firstTrace.hitVec;
			hitBlock = true;
		}

		AxisAlignedBB area = new AxisAlignedBB(start, end);
		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(shooter, area);
		
		float baseSpeed = 3;
		try {
			baseSpeed = (Float) baseProjectileSpeed$BowCore.invoke(bow);
		} catch (Exception e) {

		}
		
		float power = ItemBow.getArrowVelocity(20) * baseSpeed;
		power *= launcherData.range;

		Function<Double, Double> aimAssist = d -> Math.max(0, d < 36.3636 ? 0.0125 * d : -0.125 * (d - 40));

		for (Entity e : entities) {
			if (!(e instanceof EntityLivingBase) || e == shooter || e == shooter.getRidingEntity()) {
				continue;
			}

			RayTraceResult intercept = e.getEntityBoundingBox().grow(aimAssist.apply(e.getDistance(start.x, start.y, start.z))).calculateIntercept(start, end);

			if (intercept != null) {
				EntityArrow arrowToHit = ((BowCore) bow.getItem()).getProjectileEntity(StaticVars.lastArrowFired.get().copy(), bow, world, (EntityPlayer) shooter, power, 0, progress, false);
				arrowToHit.setPosition(intercept.hitVec.x, intercept.hitVec.y, intercept.hitVec.z);
				arrowToHit.setSilent(true);
				arrow.getTags().forEach(arrowToHit::addTag);

				if (arrowToHit instanceof EntityProjectileBase) {
					((EntityProjectileBase) arrowToHit).onHitEntity(new RayTraceResult(e));
				} else {
					try {
						onHit$EntityArrow.invoke(arrowToHit, new RayTraceResult(e));
					} catch (Exception er) {

					}
				}
			}
		}

		if (!arrow.world.isRemote) {
			if (hitBlock) {
				arrow.setPosition(end.x, end.y, end.z);
				arrow.xTile = firstTrace.getBlockPos().getX();
				arrow.yTile = firstTrace.getBlockPos().getY();
				arrow.zTile = firstTrace.getBlockPos().getZ();
				arrow.inGround = true;
				arrow.motionX = heading.x * dist;
				arrow.motionY = heading.y * dist;
				arrow.motionZ = heading.z * dist;
				
				double posX = arrow.posX;
				double posY = arrow.posY;
				double posZ = arrow.posZ;
				arrow.onUpdate();
				arrow.posX = posX;
				arrow.posY = posY;
				arrow.posZ = posZ;
				
				if (arrow instanceof EntityProjectileBase)
					ProjectileEvent.OnHitBlock.fireEvent((EntityProjectileBase) arrow, launcherData.range * 20, firstTrace.getBlockPos(), world.getBlockState(firstTrace.getBlockPos()));
								
				if (!arrow.inGround) {
					System.out.println("bbb");
					this.shoot(world, shooter, arrow, Math.max(0, dist - start.distanceTo(end)), bow);
				} else {
					world.createExplosion(shooter, end.x, end.y, end.z, 0.5f, false);	
				}
			}

			int arrowColor = 0xffffff;
			if (arrow instanceof EntityProjectileBase) {
				arrowColor = getToolMaterials(((EntityProjectileBase) arrow).tinkerProjectile.getItemStack()).get(1).materialTextColor;
			} else if (arrow instanceof EntityTippedArrow) {
				arrowColor = ((EntityTippedArrow) arrow).getColor();
			} else if (arrow instanceof EntitySpectralArrow) {
				arrowColor = 0xffcc40;
			}

			NBTTagInt data = new NBTTagInt(arrowColor);

			Vec3d pos = start.add(heading.scale(0.5));

			double actualDist = start.distanceTo(end);

			for (double i = 0.5; i < actualDist; i += 0.1) {
				ColoredDustAction.INSTANCE.run(pos.x, pos.y, pos.z, data);
				pos = pos.add(heading.scale(0.1));
			}
		}
	}
	
	public static List<Material> getToolMaterials(ItemStack stack) {
		return TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
	}
}

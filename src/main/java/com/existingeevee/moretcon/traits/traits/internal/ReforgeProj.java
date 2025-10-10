package com.existingeevee.moretcon.traits.traits.internal;

import java.util.List;

import javax.annotation.Nullable;

import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.reforges.reforges.BasicReforge;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.events.TinkerProjectileImpactEvent;
import slimeknights.tconstruct.library.traits.AbstractProjectileTrait;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class ReforgeProj extends AbstractProjectileTrait {

	public ReforgeProj() {
		super(MiscUtils.createNonConflictiveName("reforged_projectile"), 0);
		MinecraftForge.EVENT_BUS.register(this);
		TinkerRegistry.addTrait(this);
	}

	public static final ThreadLocal<EntityProjectileBase> LAST_PROJ = ThreadLocal.withInitial(() -> null);

	@Override
	public void onLaunch(EntityProjectileBase projectileBase, World world, @Nullable EntityLivingBase shooter) {
		List<ITrait> traits = TinkerUtil.getTraitsOrdered(projectileBase.tinkerProjectile.getLaunchingStack());

		double vel = 0;
		
		for (ITrait t : traits) {
			if (t instanceof BasicReforge && ((BasicReforge) t).isRanged()) {
				BasicReforge.IS_FROM_FIRED_PROJECTILE.set(true);
				vel += ((BasicReforge) t).getVelocity();
				BasicReforge.IS_FROM_FIRED_PROJECTILE.set(false);
			}
		}
				
		projectileBase.motionX *= 1 + vel;
		projectileBase.motionY *= 1 + vel;
		projectileBase.motionZ *= 1 + vel;
	}

	@SubscribeEvent
	public void onTinkerProjectileImpactEvent(TinkerProjectileImpactEvent event) {
		if (event.getEntity() instanceof EntityProjectileBase) {
			ItemStack stack = ((EntityProjectileBase) event.getEntity()).tinkerProjectile.getItemStack();

			if (!this.isToolWithTrait(stack)) {
				return;
			}

			RayTraceResult result = event.getRayTraceResult();
			if (result != null && result.entityHit != null) {
				LAST_PROJ.set((EntityProjectileBase) event.getEntity());
			}
		}
	}

	@Override
	public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammoStack, EntityLivingBase attacker, Entity target, double impactSpeed) {
		if (LAST_PROJ.get() != null) {
			LAST_PROJ.set(null);
		}
	}

	@Override
	public boolean isCriticalHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target) {
		if (LAST_PROJ.get() != null) {
			List<ITrait> traits = TinkerUtil.getTraitsOrdered(LAST_PROJ.get().tinkerProjectile.getLaunchingStack());

			for (ITrait t : traits) {
				if (t instanceof BasicReforge && ((BasicReforge) t).isRanged()) {
					BasicReforge.IS_FROM_FIRED_PROJECTILE.set(true);
					boolean ret = t.isCriticalHit(tool, player, target);
					BasicReforge.IS_FROM_FIRED_PROJECTILE.set(false);
					if (ret)
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		if (LAST_PROJ.get() != null) {
			List<ITrait> traits = TinkerUtil.getTraitsOrdered(LAST_PROJ.get().tinkerProjectile.getLaunchingStack());

			for (ITrait t : traits) {
				if (t instanceof BasicReforge && ((BasicReforge) t).isRanged()) {
					BasicReforge.IS_FROM_FIRED_PROJECTILE.set(true);
					newDamage = t.damage(tool, player, target, damage, newDamage, isCritical);
					BasicReforge.IS_FROM_FIRED_PROJECTILE.set(false);
				}
			}
		}
		return newDamage;
	}

	@Override
	public float knockBack(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float knockback, float newKnockback, boolean isCritical) {
		if (LAST_PROJ.get() != null) {
			List<ITrait> traits = TinkerUtil.getTraitsOrdered(LAST_PROJ.get().tinkerProjectile.getLaunchingStack());

			for (ITrait t : traits) {
				if (t instanceof BasicReforge && ((BasicReforge) t).isRanged()) {
					BasicReforge.IS_FROM_FIRED_PROJECTILE.set(true);
					newKnockback = t.knockBack(tool, player, target, damage, knockback, newKnockback, isCritical);
					BasicReforge.IS_FROM_FIRED_PROJECTILE.set(false);
				}
			}
		}
		return newKnockback;
	}

	@Override
	public int getPriority() {
		return 140;
	}
}

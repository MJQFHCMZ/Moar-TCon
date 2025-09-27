package com.existingeevee.moretcon.traits.traits.internal;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.capability.projectile.TinkerProjectileHandler;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.events.TinkerProjectileImpactEvent;
import slimeknights.tconstruct.library.tools.ranged.ProjectileCore;
import slimeknights.tconstruct.library.traits.AbstractProjectileTrait;

public class PolyshotProj extends AbstractProjectileTrait {

	public PolyshotProj() {
		super(MiscUtils.createNonConflictiveName("polyshot_projectile"), 0);
		MinecraftForge.EVENT_BUS.register(this);
		TinkerRegistry.addTrait(this);
	}

	public static final ThreadLocal<Boolean> IS_ALREADY_PROCING = ThreadLocal.withInitial(() -> false);

	@Override
	public void onLaunch(EntityProjectileBase projectileBase, World world, EntityLivingBase shooter) {
		if (IS_ALREADY_PROCING.get()) {
			return;
		}

		if (projectileBase != null && shooter != null) {
			TinkerProjectileHandler ticProjectile = projectileBase.tinkerProjectile;

			float speed = ticProjectile.getPower();

			if (!world.isRemote && ticProjectile.getItemStack().getItem() instanceof ProjectileCore) {
				IS_ALREADY_PROCING.set(true);

				int toShoot = random.nextInt(3) + 4;

				modifyProjectile(projectileBase, shooter, speed);
				for (int i = 0; i < toShoot - 1; i++) {
					spawnProjectile(projectileBase, shooter, speed);
				}
				IS_ALREADY_PROCING.set(false);
			}
		}
	}

	@Override
	public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammoStack, EntityLivingBase attacker, Entity target, double impactSpeed) {
		target.hurtResistantTime = 0;
	}

	public static void spawnProjectile(EntityProjectileBase projectileBase, EntityLivingBase shooter, float power) {
		ItemStack stack = projectileBase.tinkerProjectile.getItemStack();
		ItemStack launcher = projectileBase.tinkerProjectile.getLaunchingStack();

		EntityProjectileBase proj = ((ProjectileCore) stack.getItem()).getProjectile(stack, launcher, shooter.world, shooter instanceof EntityPlayer ? (EntityPlayer) shooter : null, 2.1f, 0f, 1f, false);

		proj.setIsCritical(power >= 1);
		shooter.world.spawnEntity(proj);
		proj.pickupStatus = PickupStatus.CREATIVE_ONLY;
		modifyProjectile(proj, shooter, power);
	}

	public static void modifyProjectile(EntityProjectileBase projectileBase, EntityLivingBase shooter, float power) {
		// Reset projectile motion
		projectileBase.motionX = 0;
		projectileBase.motionY = 0;
		projectileBase.motionZ = 0;

		float velo = power;

		// Reshoot
		projectileBase.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0, velo, 25f); // MASSIVE inaccuracy, low speed
	}

	@Override
	public void onProjectileUpdate(EntityProjectileBase projectile, World world, ItemStack toolStack) {
		NBTTagCompound comp = projectile.getEntityData().getCompoundTag(this.getModifierIdentifier());

		if (projectile.pickupStatus == PickupStatus.CREATIVE_ONLY) {
			if (projectile.ticksExisted > 20 * 20)
				projectile.setDead();
			if (projectile.inGround || projectile.onGround) {
				int tick = comp.getInteger("TickOnGround");
				if (tick > 60)
					projectile.setDead();

				comp.setInteger("TickOnGround", tick + 1);
			} else {
				comp.setInteger("TickOnGround", 0);
			}

		}

		Vec3d vel = new Vec3d(projectile.motionX, projectile.motionY, projectile.motionZ);

		if (!projectile.inGround)
			comp.setDouble("DistTraveled", comp.getDouble("DistTraveled") + vel.lengthVector());

		projectile.getEntityData().setTag(this.getModifierIdentifier(), comp);
	}

	public static final ThreadLocal<EntityProjectileBase> LAST_PROJ = ThreadLocal.withInitial(() -> null);

	@SubscribeEvent
	public void onTinkerProjectileImpactEvent(TinkerProjectileImpactEvent event) {
		if (event.getEntity() instanceof EntityProjectileBase)
			LAST_PROJ.set((EntityProjectileBase) event.getEntity());
	}

	private static final double DSQ_SCALAR = 0.35 / Math.sqrt(-Math.log(0.5));

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		// no arrow? we use 1/4 damage
		float mult = 0.25f;
		if (LAST_PROJ.get() != null) {
			EntityProjectileBase proj = LAST_PROJ.get();
			LAST_PROJ.set(null);
			NBTTagCompound comp = proj.getEntityData().getCompoundTag(this.getModifierIdentifier());
			double distTraveled = comp.getDouble("DistTraveled");

			mult = (float) (2 * Math.exp(-(DSQ_SCALAR * distTraveled) * (DSQ_SCALAR * distTraveled)));
		}

		return newDamage * 0.5f * mult;
	}

	@Override
	public int getPriority() {
		return -6942069; //we want this to run asap
	}
}

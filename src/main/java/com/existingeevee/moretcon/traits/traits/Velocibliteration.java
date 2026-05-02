package com.existingeevee.moretcon.traits.traits;

import com.existingeevee.moretcon.client.actions.ImpactFrameAction;
import com.existingeevee.moretcon.inits.ModPotions;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class Velocibliteration extends AbstractTrait {

	public static final ThreadLocal<VelocibliterationData> VELO_HIT = ThreadLocal.withInitial(() -> null);

	public Velocibliteration() {
		super(MiscUtils.createNonConflictiveName("velocibliteration"), 0xffffff);
	}

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		VelocibliterationData velohit = VELO_HIT.get();
		if (velohit != null && velohit.getProgress() > 0.75 && !player.world.isRemote) {
			newDamage *= 1 + Math.min(3, 1f * velohit.getVelSq() * 4) + 0.5 * (velohit.getProgress() - 0.75f);
			if (Math.min(3, 1f * velohit.getVelSq() * 4) + 0.5 * (velohit.getProgress() - 0.75f) > 2)
				MiscUtils.trueDamage(target, newDamage * 0.15f, DamageSource.causeMobDamage(player), true);
		}
		return newDamage;
	}

	@Override
	public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (entity instanceof EntityLivingBase && world.isRemote && isSelected) { 
			EntityLivingBase living = (EntityLivingBase) entity;
			if (living.getActiveItemStack() == tool) {
				float progress = Math.min(1f, (tool.getMaxItemUseDuration() - living.getItemInUseCount()) / 30f);
				if (progress < 0.75)
					return; 
				
				Vec3d eye = entity.getPositionEyes(0.5f);
				Vec3d pos = eye.add(entity.getLookVec().scale(1.75));
				entity.world.spawnParticle(EnumParticleTypes.REDSTONE, true, pos.x, pos.y + 0.05, pos.z, 0, 0, 0);
			}
		}
		
	}

	@Override
	public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
		VelocibliterationData velohit = VELO_HIT.get();
		if (velohit != null && velohit.getProgress() > 0.75 && !player.world.isRemote) {
			player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ANVIL_FALL, SoundCategory.PLAYERS, 1, 0);

			if (player.world instanceof WorldServer) {
				SPacketParticles spacketparticles = new SPacketParticles(EnumParticleTypes.EXPLOSION_LARGE, true, (float) target.getPositionVector().x, (float) target.getPositionVector().y, (float) target.getPositionVector().z, 0, 0, 0, 0, 1);
				for (EntityPlayerMP p : player.world.getPlayers(EntityPlayerMP.class, p -> true)) {
					if (p.getPositionVector().squareDistanceTo(target.getPositionVector()) < 100 * 100) {
						p.connection.sendPacket(spacketparticles);
					}
				}
			}
			
			if (Math.min(3, 1f * velohit.getVelSq() * 4) + 0.5 * (velohit.getProgress() - 0.75f) > 2) {
				player.addPotionEffect(new PotionEffect(ModPotions.invulnerability, 60));
				ImpactFrameAction.INSTANCE.run(player.world, player.posX, player.posY, player.posZ, ImpactFrameAction.build(player, 0, true));
			}
		}
	}

	@Override
	public float knockBack(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float knockback, float newKnockback, boolean isCritical) {
		VelocibliterationData velohit = VELO_HIT.get();
		if (velohit != null && velohit.getProgress() > 0.75 && !player.world.isRemote) {
			newKnockback += 4f * Math.min(3, 1f * velohit.getVelSq() * 4f);
		}
		return newKnockback;
	}

	@Override
	public boolean isToolWithTrait(ItemStack is) {
		return super.isToolWithTrait(is);
	}

	@Override
	public int getPriority() {
		return 20; // we want this to run lastish.
	}

	public static class VelocibliterationData {
		public VelocibliterationData(double velSq, double progress) {
			this.velSq = velSq;
			this.progress = progress;
		}

		final double velSq;
		final double progress;

		public double getVelSq() {
			return velSq;
		}

		public double getProgress() {
			return progress;
		}
	}
}

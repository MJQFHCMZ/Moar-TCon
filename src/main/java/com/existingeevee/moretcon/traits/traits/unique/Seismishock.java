package com.existingeevee.moretcon.traits.traits.unique;

import java.util.List;

import com.existingeevee.moretcon.entity.entities.EntityDecayingEffect;
import com.existingeevee.moretcon.entity.entities.EntityDecayingEffect.EnumDecayingEffectType;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class Seismishock extends AbstractTrait {

	public Seismishock() {
		super(MiscUtils.createNonConflictiveName("seismishock"), 0);
	}

	@Override
	public void onHit(ItemStack stack, EntityLivingBase playerIn, EntityLivingBase target, float damage, boolean isCritical) {
		if (isCritical) {
			if (!target.world.isRemote && target.world instanceof WorldServer) {
				float multiplier = Math.min(3, 1 + playerIn.fallDistance * 0.1f);
				if (multiplier >= 3) {
					((WorldServer) target.world).spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, target.posX, target.posY, target.posZ, 1, 0, 0, 0, 0, new int[0]);
				}
				EntityDecayingEffect decayingEffect = new EntityDecayingEffect(target.getEntityWorld(), EnumDecayingEffectType.SHOCKWAVE, 0, 2f * multiplier, playerIn.getUniqueID());
				decayingEffect.setPosition(target.posX, target.posY, target.posZ);
				target.getEntityWorld().spawnEntity(decayingEffect);

				// TraitSpiky

//				System.out.pr
				for (Entity e : decayingEffect.getAffectedEntities()) {

					if (!(e instanceof EntityLivingBase)) {
						continue;
					}

					EntityLivingBase entity = (EntityLivingBase) e;

					List<ITrait> traits = TinkerUtil.getTraitsOrdered(stack);

					float dmg = damage * 0.5f * multiplier;
					float dmgOrig = dmg;

					for (ITrait t : traits) {
						if (t == this)
							continue;
						dmg = t.damage(stack, playerIn, entity, dmgOrig, dmg, false);
					}

					int hurtResistantTime = e.hurtResistantTime;

					float hpBefore = entity.getHealth();

					for (ITrait t : traits) {
						if (t == this)
							continue;
						t.onHit(stack, playerIn, entity, dmg, false);
						e.hurtResistantTime = hurtResistantTime;
					}

					boolean wasHit = false;

					DamageSource source = playerIn instanceof EntityPlayer ? DamageSource.causePlayerDamage((EntityPlayer) playerIn) : DamageSource.causeMobDamage(playerIn);

					wasHit = entity.attackEntityFrom(source, dmg);
					e.hurtResistantTime = hurtResistantTime;

					for (ITrait t : traits) {
						if (t == this)
							continue;
						t.afterHit(stack, playerIn, entity, hpBefore - entity.getHealth(), false, wasHit);
					}
				}
			}
			target.world.playSound(null, target.getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 1, 2);
			if (target.getHealth() <= 0) {
				playerIn.fallDistance = 3;
				target.world.playSound(null, target.getPosition(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1, 1);
			}
		}
	}

	@Override
	public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
		if (wasCritical) {
			player.fallDistance = 3;
		}
	}
}

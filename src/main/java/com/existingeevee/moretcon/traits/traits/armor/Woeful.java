package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class Woeful extends AbstractArmorTrait {

	public Woeful() {
		super(MiscUtils.createNonConflictiveName("woeful"), TextFormatting.WHITE);
	}

	@Override
	public float onDamaged(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingDamageEvent evt) {
		if (source.getTrueSource() instanceof EntityLivingBase && random.nextInt(2) == 0) {
			int prevLevel = 0;
			if (((EntityLivingBase) source.getTrueSource()).isPotionActive(MobEffects.WITHER)) {
				prevLevel = ((EntityLivingBase) source.getTrueSource()).getActivePotionEffect(MobEffects.WITHER).getAmplifier();
			}
			if (!((EntityLivingBase) source.getTrueSource()).world.isRemote) {
				if (((EntityLivingBase) source.getTrueSource()).world.rand.nextInt(6) == 0) {
					((EntityLivingBase) source.getTrueSource()).addPotionEffect(new PotionEffect(MobEffects.WITHER, 5 * 20, Math.min(prevLevel + 1, 2)));
				} else {
					if (prevLevel > 0) {
						((EntityLivingBase) source.getTrueSource()).addPotionEffect(new PotionEffect(MobEffects.WITHER, 5 * 20, prevLevel));
					}
				}
			}
		}
		return newDamage;
	}
}
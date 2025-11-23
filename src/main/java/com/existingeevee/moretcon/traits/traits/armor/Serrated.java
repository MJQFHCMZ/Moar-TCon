package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.inits.ModPotions;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class Serrated extends AbstractArmorTrait {

	public Serrated() {
		super(MiscUtils.createNonConflictiveName("serrated"), TextFormatting.WHITE);
	}

	@Override
	public float onDamaged(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingDamageEvent evt) {
		if (source.getTrueSource() instanceof EntityLivingBase && random.nextInt(2) == 0) {
			((EntityLivingBase) source.getTrueSource()).addPotionEffect(new PotionEffect(ModPotions.bleeding, 4 * 20, 0));
		}
		return newDamage;
	}
}
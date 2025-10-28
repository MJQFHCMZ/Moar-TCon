package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class BurningThorns extends AbstractArmorTrait {

	public BurningThorns() {
		super(MiscUtils.createNonConflictiveName("burning_thorns"), TextFormatting.WHITE);
	}

	@Override
	public float onDamaged(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingDamageEvent evt) {
		if (source.getTrueSource() instanceof EntityLivingBase && random.nextInt(8) == 0)
			source.getTrueSource().setFire(6);
		return newDamage;
	}
}
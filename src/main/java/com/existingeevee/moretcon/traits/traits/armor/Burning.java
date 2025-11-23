package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.fires.CustomFireEffect;
import com.existingeevee.moretcon.other.fires.CustomFireHelper;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class Burning extends AbstractArmorTrait {

	public Burning() {
		super(MiscUtils.createNonConflictiveName("cold_fire"), TextFormatting.WHITE);
	}

	@Override
	public float onDamaged(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingDamageEvent evt) {
		if (source.getTrueSource() instanceof EntityLivingBase && random.nextInt(8) == 0)
			CustomFireHelper.setAblaze((EntityLivingBase) source.getTrueSource(), CustomFireEffect.COLD_FIRE, 120);
		return newDamage;
	}
}
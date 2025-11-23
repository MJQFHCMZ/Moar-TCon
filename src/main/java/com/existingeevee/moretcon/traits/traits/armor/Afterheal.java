package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class Afterheal extends AbstractArmorTrait {

	public Afterheal() {
		super(MiscUtils.createNonConflictiveName("afterheal"), TextFormatting.WHITE);
	}

	@Override
	public float onHurt(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingHurtEvent evt) {
		if (source != DamageSource.OUT_OF_WORLD)
			MiscUtils.executeInNTicks(() -> {
				if (player.world.playerEntities.contains(player)) {
					if (player.getFoodStats().getFoodLevel() >= 6) {
						player.heal(1);
						player.getFoodStats().addExhaustion(0.1f);
					}
				}
			}, 30);
		return newDamage;
	}
}
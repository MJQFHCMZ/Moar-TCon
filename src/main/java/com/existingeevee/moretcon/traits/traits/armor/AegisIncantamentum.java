package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class AegisIncantamentum extends AbstractArmorTrait {

	public AegisIncantamentum() {
		super(MiscUtils.createNonConflictiveName("aegis_incantamentum"), 0xffffff);
	}

	@Override
	public void onAbilityTick(int level, World world, EntityPlayer player) {
		int amp = level >= 2 ? 1 : 0;

		if (!world.isRemote && world.getTotalWorldTime() % (20 * 20) == 0) {
			if (!player.isPotionActive(MobEffects.RESISTANCE))
				player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 10 * 20, amp));
		}
	}
}

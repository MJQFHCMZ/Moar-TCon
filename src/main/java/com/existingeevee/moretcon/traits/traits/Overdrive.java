package com.existingeevee.moretcon.traits.traits;

import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.traits.ModTraits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class Overdrive extends AbstractTrait {

	public Overdrive() {
		super(MiscUtils.createNonConflictiveName("overdrive"), 0);
	}

	@Override
	public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
		if (wasHit && (ToolHelper.getTraits(tool).contains(ModTraits.polyshotProj) ? random.nextDouble() < 0.25 : true)) {
			target.hurtResistantTime /= 2;
		}
	}
}

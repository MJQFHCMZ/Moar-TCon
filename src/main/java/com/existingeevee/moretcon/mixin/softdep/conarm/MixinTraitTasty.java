package com.existingeevee.moretcon.mixin.softdep.conarm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.existingeevee.moretcon.traits.ModTraits;
import com.llamalad7.mixinextras.sugar.Local;

import c4.conarm.common.armor.traits.TraitTasty;
import net.minecraft.item.ItemStack;

@Mixin(TraitTasty.class)
public class MixinTraitTasty {

	@ModifyConstant(method = "eatToRepair", constant = @Constant(intValue = 1), remap = false)
	private int moretcon$ModifyConstant$eatToRepair$int1(int value, @Local ItemStack tool) {
		if (ModTraits.saturpigting.isToolWithTrait(tool)) {
			value += 4;
		}
		return value;
	}
	
	@ModifyConstant(method = "eatToRepair", constant = @Constant(floatValue = 0), remap = false)
	private float moretcon$ModifyConstant$eatToRepair$float0(float value, @Local ItemStack tool) {
		if (ModTraits.saturpigting.isToolWithTrait(tool)) {
			value += 0.8;
		}
		return value;
	}
}

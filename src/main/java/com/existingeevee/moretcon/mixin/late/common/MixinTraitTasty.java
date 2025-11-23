package com.existingeevee.moretcon.mixin.late.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.existingeevee.moretcon.traits.ModTraits;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.tools.traits.TraitTasty;

@Mixin(TraitTasty.class)
public class MixinTraitTasty {

	@ModifyConstant(method = "nom", constant = @Constant(intValue = 1), remap = false)
	private int moretcon$ModifyConstant$nom$int1(int value, @Local ItemStack tool) {
		if (ModTraits.saturpigting.isToolWithTrait(tool)) {
			value += 4;
		}
		return value;
	}
	
	@ModifyConstant(method = "nom", constant = @Constant(floatValue = 0), remap = false)
	private float moretcon$ModifyConstant$nom$float0(float value, @Local ItemStack tool) {
		if (ModTraits.saturpigting.isToolWithTrait(tool)) {
			value += 0.8;
		}
		return value;
	}
}

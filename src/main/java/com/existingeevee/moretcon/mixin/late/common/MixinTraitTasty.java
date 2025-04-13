package com.existingeevee.moretcon.mixin.late.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.existingeevee.moretcon.traits.ModTraits;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.tools.traits.TraitTasty;

@Mixin(TraitTasty.class)
public class MixinTraitTasty {

	@Inject(at = @At("TAIL"), method = "nom", remap = false)
	private void moretcon$HEAD_Inject$nom(ItemStack tool, EntityPlayer player, CallbackInfo info) {
		if (ModTraits.saturpigting.isToolWithTrait(tool)) {
			player.getFoodStats().addStats(4, 1f); // 4 because vanilla tinkers already adds 1. this totals to 5, which is 2 and a half bars
		}
	}

}

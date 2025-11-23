package com.existingeevee.moretcon.mixin.softdep.thebetweenlands;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.existingeevee.moretcon.traits.ModTraits;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import slimeknights.tconstruct.library.utils.ToolHelper;
import thebetweenlands.common.block.structure.BlockEnergyBarrier;

@Mixin(BlockEnergyBarrier.class)
public class MixinBlockEnergyBarrier {

	@ModifyVariable(method = { "onEntityCollidedWithBlock", "onEntityCollision", "func_180634_a" }, at = @At("STORE"), ordinal = 0, remap = false)
	public EnumHand moretcon$STORE_ModifyVariable$onEntityCollidedWithBlock(EnumHand handOrig, @Local Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			if (ToolHelper.getTraits(stack).contains(ModTraits.shockwaving)) {
				return EnumHand.MAIN_HAND;
			}
		}
		return handOrig;
	}
}

package com.existingeevee.moretcon.mixin.early.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.existingeevee.moretcon.block.BlockCustomFire;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;

@Mixin(BlockFalling.class)
public class MixinBlockFalling {

	@Inject(method = "canFallThrough", at = @At("HEAD"), cancellable = true)
	private static void moretcon$HEAD_Inject$canFallThrough(IBlockState state, CallbackInfoReturnable<Boolean> ci) {
		if (state.getBlock() instanceof BlockCustomFire)
			ci.setReturnValue(true); 
	}
}

package com.existingeevee.moretcon.mixin.early.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

@Mixin(ExtendedBlockStorage.class)
public class MixinDynSurroundPleaseStopThisNonesense {

	@Shadow
    private NibbleArray skyLight;
	
	//For some fucking reason dsurround has this most schizophrienic bug with nether weather that hates MY spawned particles SPECIFICALLY
	//GRAAAAJHHHHH i want to maim
	@Inject(method = "getSkyLight(III)I", at = @At(value = "HEAD"), cancellable = true)
    public void moretcon$HEAD_Inject$getSkyLight(int x, int y, int z, CallbackInfoReturnable<Integer> ci) {
		if (this.skyLight == null)
			ci.setReturnValue(9);
    }
	
}

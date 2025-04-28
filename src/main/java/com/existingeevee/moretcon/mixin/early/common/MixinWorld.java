package com.existingeevee.moretcon.mixin.early.common;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.existingeevee.moretcon.block.BlockCustomFire;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class MixinWorld {

	@Inject(method = "extinguishFire", at = @At("HEAD"), cancellable = true)
	void moretcon$HEAD_Inject$extinguishFire(@Nullable EntityPlayer player, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> ci) {
		World $this = (World) (Object) this;
		if ($this.getBlockState(pos.offset(side)).getBlock() instanceof BlockCustomFire) {
			$this.playEvent(player, 1009, pos.offset(side), 0);
			$this.setBlockToAir(pos.offset(side));
			
			ci.setReturnValue(true); 
		}
	}
}

package com.existingeevee.moretcon.mixin.late.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.existingeevee.moretcon.traits.ModTraits;
import com.existingeevee.moretcon.traits.traits.Velocibliteration;
import com.existingeevee.moretcon.traits.traits.Velocibliteration.VelocibliterationData;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.tools.melee.item.FryPan;

@Mixin(FryPan.class)
public class MixinFryPan extends Item {

	@Inject(at = @At(value = "HEAD"), method = "onPlayerStoppedUsing")
	public void moretcon$HEAD_Inject$onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase player, int timeLeft, CallbackInfo ci) {
		if (ModTraits.velocibliteration.isToolWithTrait(stack)) {
			double velSq = player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ;
		    float progress = Math.min(1f, (getMaxItemUseDuration(stack) - timeLeft) / 30f);

			Velocibliteration.VELO_HIT.set(new VelocibliterationData(velSq, progress));
		}
	}
	
	@Inject(at = @At(value = "RETURN"), method = "onPlayerStoppedUsing")
	public void moretcon$RETURN_Inject$onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase player, int timeLeft, CallbackInfo ci) {
		if (Velocibliteration.VELO_HIT.get() != null) {
			Velocibliteration.VELO_HIT.remove();
		}
	}

}

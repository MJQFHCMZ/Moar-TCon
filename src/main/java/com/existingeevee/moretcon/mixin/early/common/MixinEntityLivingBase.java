package com.existingeevee.moretcon.mixin.early.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.existingeevee.moretcon.inits.ModPotions;

import net.minecraft.entity.EntityLivingBase;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase {

	@Inject(method = "setHealth", at = @At("HEAD"), cancellable = true)
	public void moretcon$HEAD_Inject$setHealth(float health, CallbackInfo ci) {
		EntityLivingBase $this = (EntityLivingBase) (Object) this;
		if ($this.getHealth() > health && $this.isPotionActive(ModPotions.invulnerability))
			ci.cancel();
	}

}

package com.existingeevee.moretcon.mixin.softdep.thebetweenlands;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.existingeevee.moretcon.traits.ModTraits;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tools.ranged.IProjectile;
import slimeknights.tconstruct.library.utils.TagUtil;
import thebetweenlands.common.entity.EntityBLLightningBolt;

@Mixin(EntityBLLightningBolt.class)
public abstract class MixinEntityBLLightningBolt extends Entity {

	public MixinEntityBLLightningBolt(World worldIn) {
		super(worldIn);
	}

	@Inject(method = "onUpdate()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", shift = At.Shift.AFTER))
	public void moretcon$INVOKE_Inject$onUpdate(CallbackInfo ci, @Local ItemStack stack) {
		if (stack.getItem() instanceof IProjectile) {
			if (ModTraits.modShocked.canApplyCustom(stack) && TagUtil.getToolStats(stack).modifiers >= 1) {
				ModTraits.modShocked.apply(stack);
			}
		}
	}
}

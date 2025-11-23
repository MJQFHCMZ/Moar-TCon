package com.existingeevee.moretcon.mixin.late.common;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TagUtil;

@Mixin(ToolCore.class)
public class MixinToolCore {

	@Inject(at = @At(value = "HEAD"), method = "onUpdateTraits", remap = false)
	protected void moretcon$HEAD_Inject$onUpdateTraits(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected, CallbackInfo ci) {
		NBTTagCompound comp = TagUtil.getTagSafe(stack);
		if (!comp.hasKey("UniqueToolID"))
			comp.setString("UniqueToolID", UUID.randomUUID().toString());
	}
}

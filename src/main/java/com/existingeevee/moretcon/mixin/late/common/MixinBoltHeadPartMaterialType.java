package com.existingeevee.moretcon.mixin.late.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.existingeevee.moretcon.materials.UniqueMaterial;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IToolPart;

@Mixin(targets = "slimeknights.tconstruct.tools.ranged.item.Bolt$BoltHeadPartMaterialType")
public abstract class MixinBoltHeadPartMaterialType extends PartMaterialType {

	public MixinBoltHeadPartMaterialType(IToolPart part, String[] statIDs) {
		super(part, statIDs);
	}

	@Inject(at = @At(value = "HEAD"), method = "isValidMaterial", remap = false, cancellable = true)
	public void moretcon$HEAD_Inject$isValidMaterial(Material material, CallbackInfoReturnable<Boolean> cir) {
		if (material instanceof UniqueMaterial) {
			UniqueMaterial uMat = (UniqueMaterial) material;
			if (uMat.getPartResLoc().equals("tconstruct:bolt_core")) {
				cir.setReturnValue(super.isValidMaterial(material));
			}
		}
	}
}

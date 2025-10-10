package com.existingeevee.moretcon.mixin.late.common;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.existingeevee.moretcon.other.StaticVars;
import com.existingeevee.moretcon.traits.traits.abst.IAdditionalTraitMethods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.mantle.util.TagHelper;
import slimeknights.tconstruct.library.capability.projectile.TinkerProjectileHandler;
import slimeknights.tconstruct.library.tools.ranged.IAmmo;
import slimeknights.tconstruct.library.traits.IProjectileTrait;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

@Mixin(TinkerProjectileHandler.class)
public abstract class MixinTinkerProjectileHandler {

	@Shadow(remap = false)
	private ItemStack parent;

	@Unique
	private ItemStack parentOrig;

	@Shadow(remap = false)
	private ItemStack launcher;

	@Shadow(remap = false)
	private List<IProjectileTrait> projectileTraitList;

	@Shadow(remap = false)
	abstract void updateTraits();

	@Inject(at = @At(value = "RETURN"), method = "pickup", remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
	public void moretcon$RETURN_Inject$pickup(EntityLivingBase entity, boolean simulate, CallbackInfoReturnable<Boolean> ci, ItemStack stack) {
		if (!simulate && stack.getItem() instanceof IAmmo) {
			StaticVars.lastArrowPickup.set(stack);
		}
	}

	@Inject(at = @At(value = "RETURN"), method = "setItemStack", remap = false)
	public void moretcon$RETURN_Inject$setItemStack(ItemStack stack, CallbackInfo ci) {
		if (parent == null || StaticVars.flagTinkerProjectileHandlerDirectSet.get())
			return;
		parentOrig = parent.copy();
		((TinkerProjectileHandler) (Object) this).setLaunchingStack(launcher);
	}

	@Inject(at = @At(value = "RETURN"), method = "setLaunchingStack", remap = false)
	public void moretcon$RETURN_Inject$setLaunchingStack(ItemStack launchingStack, CallbackInfo ci) {
		if (launchingStack == null || parent == null || StaticVars.flagTinkerProjectileHandlerDirectSet.get())
			return;

		boolean modified = false;
		this.parent = parentOrig.copy();

		for (ITrait t : ToolHelper.getTraits(parent)) {
			if (t instanceof IAdditionalTraitMethods) {
				IAdditionalTraitMethods trait = (IAdditionalTraitMethods) t;
				modified = trait.modifyProjectileParent(launchingStack, parent, parentOrig.copy(), (TinkerProjectileHandler) (Object) this) | modified;
			}
		}

		for (ITrait t : ToolHelper.getTraits(launchingStack)) {
			if (t instanceof IAdditionalTraitMethods) {
				IAdditionalTraitMethods trait = (IAdditionalTraitMethods) t;
				modified = trait.modifyLauncherProjectile(launchingStack, parent, parentOrig.copy(), (TinkerProjectileHandler) (Object) this) | modified;
			}
		}
		
		if (modified) {
			NBTTagCompound tag = TagHelper.getTagSafe(parent);
			tag.setBoolean("PreventInvLink", true);
			parent.setTagCompound(tag);
			
			updateTraits();
		}
	}
}

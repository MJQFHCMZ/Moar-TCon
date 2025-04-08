package com.existingeevee.moretcon.mixin.softdep.tinkertoolleveling;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.existingeevee.moretcon.other.utils.ArrowReferenceHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import slimeknights.toolleveling.ModToolLeveling;
import slimeknights.toolleveling.capability.CapabilityDamageXp;

@Mixin(ModToolLeveling.class)
public abstract class MixinModToolLeveling {

	@Inject(at = @At(value = "HEAD"), method = { "afterHit" }, remap = false, cancellable = true)
	public void moretcon$HEAD_Inject$afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit, CallbackInfo ci) {
		ItemStack dirRef = ArrowReferenceHelper.getLinkedItemstackFromInventory(tool, player);
		
		if (!dirRef.isEmpty()) { // Oh boy... guess were handling this one
			
			if (!target.getEntityWorld().isRemote && wasHit && player instanceof EntityPlayer) {
				// if we killed it the event for distributing xp was already fired and we just do it manually here
				EntityPlayer entityPlayer = (EntityPlayer) player;
				if (!target.isEntityAlive()) {
					addXp(dirRef, Math.round(damageDealt), entityPlayer);
				} else if (target.hasCapability(CapabilityDamageXp.CAPABILITY, null)) {
					target.getCapability(CapabilityDamageXp.CAPABILITY, null).addDamageFromTool(damageDealt, dirRef, entityPlayer);
				}
				
				ci.cancel();
			}
		}
	}

	@Shadow(remap = false)
	abstract void addXp(ItemStack tool, int amount, EntityPlayer player);
}
//Arrow
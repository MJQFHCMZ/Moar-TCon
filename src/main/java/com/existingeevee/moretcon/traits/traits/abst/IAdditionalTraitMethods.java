package com.existingeevee.moretcon.traits.traits.abst;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import slimeknights.tconstruct.library.capability.projectile.TinkerProjectileHandler;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;

public interface IAdditionalTraitMethods {

	default void onPickup(EntityProjectileBase projectileBase, ItemStack ammo, EntityLivingBase entity) {

	}

	default void onAmmoConsumed(ItemStack ammo, @Nullable EntityLivingBase entity) {

	}

	default void modifyTooltip(ItemStack tool, ItemTooltipEvent event) { //this is called for both the tool AND the part.
	}
	
    default void onLeftClick(EntityPlayer player, ItemStack stack) {

    }

    //NOTE!!!! DO NOT CALL SET ON THE TICPROJ
	default boolean modifyProjectileParent(ItemStack launchingStack, ItemStack projectileParent, ItemStack originalParent, TinkerProjectileHandler tinkerProjectileHandler) {
		return false;
	}

	default boolean modifyLauncherProjectile(ItemStack launchingStack, ItemStack projectileParent, ItemStack originalParent, TinkerProjectileHandler tinkerProjectileHandler) {
		return false;
	}
	
	//note this is called for broken items too. do your own check
	default void onEntityItemTick(ItemStack tool, EntityItem entity) {
	}
}

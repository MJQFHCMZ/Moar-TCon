package com.existingeevee.moretcon.other.utils;

import com.existingeevee.moretcon.other.StaticVars;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import slimeknights.tconstruct.library.events.TinkerToolEvent.OnBowShoot;

/*
 * Class 2 make projectiles interact wiht traits better grahhhh
 */

public class ArrowReferenceHelper {

	@SubscribeEvent
	public static void onOnBowShoot(OnBowShoot event) {
		StaticVars.lastArrowFired.set(event.ammo);
	}

	public static ItemStack getLinkedItemstackFromInventory(ItemStack stack, Entity entity) {
		if (stack == null || !entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) || !stack.hasTagCompound()) {
			return ItemStack.EMPTY;
		}

		// try main and off hand first, because priority (yes they're also covered in the loop below.)
		if (entity instanceof EntityLivingBase) {
			ItemStack in = ((EntityLivingBase) entity).getHeldItemMainhand();
			if (in.hasTagCompound() && in.getTagCompound().getString("UniqueToolID").equalsIgnoreCase(stack.getTagCompound().getString("UniqueToolID"))) {
				return in;
			}

			in = ((EntityLivingBase) entity).getHeldItemOffhand();
			if (in.hasTagCompound() && in.getTagCompound().getString("UniqueToolID").equalsIgnoreCase(stack.getTagCompound().getString("UniqueToolID"))) {
				return in;
			}
		}

		IItemHandler itemHandler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		// find an itemstack that matches our input
		assert itemHandler != null;
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			ItemStack in = itemHandler.getStackInSlot(i);
			if (in.hasTagCompound() && in.getTagCompound().getString("UniqueToolID").equalsIgnoreCase(stack.getTagCompound().getString("UniqueToolID"))) {
				return in;
			}
		}

		return ItemStack.EMPTY;
	}
}
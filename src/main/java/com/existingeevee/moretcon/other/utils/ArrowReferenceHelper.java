package com.existingeevee.moretcon.other.utils;

import java.util.UUID;

import com.existingeevee.moretcon.other.StaticVars;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import slimeknights.tconstruct.library.events.TinkerToolEvent.OnBowShoot;

/*
 * A simple class designed to store the direct reference of the arrow stack such that they can be interacted with later.
 */

public class ArrowReferenceHelper {

//	private static final Map<ItemStack, ItemStack> PROJECTILE_STACKS = new HashMap<>();
//
//	@Deprecated // Nuh uh dont even think about it
//	public static void saveProjectileStack(ItemStack ammoCopy, ItemStack ammo) {
//		PROJECTILE_STACKS.put(ammoCopy, ammo);
//	}
//
//	public static ItemStack getProjectileStack(TinkerProjectileHandler proj) {
//		return PROJECTILE_STACKS.getOrDefault(proj.getItemStack(), ItemStack.EMPTY);
//	}
//
//	public static ItemStack getProjectileStack(ItemStack ammoCopy) {
//		System.out.println(PROJECTILE_STACKS);
//		return PROJECTILE_STACKS.getOrDefault(ammoCopy, ItemStack.EMPTY);
//	}
//
	@SubscribeEvent
	public static void onOnBowShoot(OnBowShoot event) {
		StaticVars.lastArrowFired.set(event.ammo);
	}
	
	public static void linkItems(ItemStack source, ItemStack... copies) {
		if (!source.hasTagCompound()) {
			source.setTagCompound(new NBTTagCompound());
		}
		String id;
		if (!source.getTagCompound().hasKey("UniqueToolID", NBT.TAG_STRING)) {
			id = UUID.randomUUID().toString();
			source.getTagCompound().setString("UniqueToolID", id);
		} else {
			id = source.getTagCompound().getString("UniqueToolID");
		}
		for (ItemStack stack : copies) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			stack.getTagCompound().setString("UniqueToolID", id);
		}
		
		System.out.println(copies[0] + " " + copies[1]);
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
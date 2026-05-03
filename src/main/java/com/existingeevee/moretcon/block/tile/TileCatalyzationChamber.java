package com.existingeevee.moretcon.block.tile;

import com.existingeevee.moretcon.item.ItemCatalyst;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileCatalyzationChamber extends TileEntity implements IInventory {

	public final ItemStackHandler inventory = new ItemStackHandler(15) {
		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return canPlaceInSlot(slot, stack);
		}
	};

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("Inventory", inventory.serializeNBT());
		return tag;
	}

	public boolean canPlaceInSlot(int slot, ItemStack stack) {
		return stack.getItem() instanceof ItemCatalyst;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.deserializeNBT(tag.getCompoundTag("Inventory"));
	}

	public IItemHandler getInventory() {
		return inventory;
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < inventory.getSlots(); i++) {
			if (!inventory.getStackInSlot(i).isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inventory.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = inventory.getStackInSlot(index);
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		ItemStack result = stack.splitStack(count);

		if (stack.isEmpty())
			inventory.setStackInSlot(index, ItemStack.EMPTY);

		markDirty();
		return result;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = inventory.getStackInSlot(index);
		inventory.setStackInSlot(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack.setCount(getInventoryStackLimit());

		inventory.setStackInSlot(index, stack);
	}

	@Override
	public String getName() {
		return "container.catalyzation_chamber";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return world.getTileEntity(pos) == this &&
				player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return canPlaceInSlot(index, stack);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < inventory.getSlots(); i++) {
			inventory.setStackInSlot(i, ItemStack.EMPTY);
		}
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return true;

		return super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) inventory;

		return super.getCapability(capability, facing);
	}
}
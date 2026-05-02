package com.existingeevee.moretcon.block.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileCatalyzationChamber extends TileEntity {

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
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.deserializeNBT(tag.getCompoundTag("Inventory"));
	}

	public IItemHandler getInventory() {
		return inventory;
	}
}
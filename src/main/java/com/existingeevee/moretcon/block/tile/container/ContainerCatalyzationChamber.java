package com.existingeevee.moretcon.block.tile.container;

import com.existingeevee.moretcon.block.tile.TileCatalyzationChamber;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCatalyzationChamber extends Container {

	private final TileCatalyzationChamber tile;

	public ContainerCatalyzationChamber(InventoryPlayer playerInv, TileCatalyzationChamber tile) {
		this.tile = tile;

		IItemHandler handler = tile.getInventory();

		int startX = 44;
		int startY = 17;

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 5; col++) {
				int slot = col + row * 5;
				addSlotToContainer(new SlotItemHandler(handler, slot, startX + col * 18, startY + row * 18));
			}
		}

		int playerInvY = 84;

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, playerInvY + row * 18));
			}
		}

		int hotbarY = playerInvY + 58;

		for (int col = 0; col < 9; col++) {
			addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, hotbarY));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tile.getWorld().getTileEntity(tile.getPos()) == tile && player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack original = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			original = stack.copy();

			int tileStart = 0;
			int tileEnd = 15;
			int playerStart = 15;
//			int playerEnd = 42;
//			int hotbarStart = 42;
			int hotbarEnd = 51;

			if (index >= tileStart && index < tileEnd) {
				if (!mergeItemStack(stack, playerStart, hotbarEnd, true))
					return ItemStack.EMPTY;
			} else {
				if (!mergeItemStack(stack, tileStart, tileEnd, false))
					return ItemStack.EMPTY;
			}

			if (stack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (stack.getCount() == original.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, stack);
		}

		return original;
	}

}
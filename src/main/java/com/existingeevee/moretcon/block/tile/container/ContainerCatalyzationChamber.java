package com.existingeevee.moretcon.block.tile.container;

import com.existingeevee.moretcon.block.tile.TileCatalyzationChamber;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
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
}
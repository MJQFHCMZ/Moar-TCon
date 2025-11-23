package com.existingeevee.moretcon.block.tile;

import com.existingeevee.moretcon.ModInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.shared.inventory.ConfigurableInvWrapperCapability;
import slimeknights.tconstruct.shared.tileentity.TileTable;

public class TileReforgeStation extends TileTable implements IInventoryGui {

	public TileReforgeStation() {
		super("gui." + ModInfo.MODID + ".reforgestation.name", 5);
		this.itemHandler = new ConfigurableInvWrapperCapability(this, false, false);
	}

	@Override
	public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new ContainerReforgeStation(inventoryplayer, this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
		return new GuiReforgeStation(inventoryplayer, world, pos, this);
	}

	@Override
	protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
		PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();
		float c = 0.2125f;
		float[] x = new float[] { c, -c, c, -c };
		float[] y = new float[] { -c, -c, c, c };
		for (int i = 0; i < 4; i++) {
			//thanks compiler
			ItemStack stackInSlot = ((IInventory) this).getStackInSlot(i);
			PropertyTableItem.TableItem item = getTableItem(stackInSlot, this.getWorld(), null);
			if (item != null) {
				item.x += x[i];
				item.z += y[i];
				item.s *= 0.46875f;

				// correct itemblock because scaling
				if (stackInSlot.getItem() instanceof ItemBlock && !(Block.getBlockFromItem(stackInSlot.getItem()) instanceof BlockPane)) {
					item.y = -(1f - item.s) / 2f;
				}

				// item.s *= 2/5f;
				toDisplay.items.add(item);
			}
		}

		// add inventory if needed
		return state.withProperty(BlockTable.INVENTORY, toDisplay);
	}
}

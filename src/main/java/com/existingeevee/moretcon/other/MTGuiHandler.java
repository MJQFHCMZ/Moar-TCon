package com.existingeevee.moretcon.other;

import com.existingeevee.moretcon.block.tile.ContainerReforgeStation;
import com.existingeevee.moretcon.block.tile.GuiReforgeStation;
import com.existingeevee.moretcon.block.tile.TileReforgeStation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class MTGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if (tile instanceof TileReforgeStation)
			return new ContainerReforgeStation(player.inventory, (TileReforgeStation) tile);
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if (tile instanceof TileReforgeStation)
			return new GuiReforgeStation(player.inventory, world, new BlockPos(x, y, z), (TileReforgeStation) tile);
		return null;
	}

}

package com.existingeevee.moretcon.block.blocktypes;

import javax.annotation.Nonnull;

import com.existingeevee.moretcon.ModInfo;
import com.existingeevee.moretcon.MoreTCon;
import com.existingeevee.moretcon.block.tile.TileReforgeStation;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.tools.common.block.ITinkerStationBlock;

public class BlockReforgeStation extends BlockTable implements ITinkerStationBlock {

	public BlockReforgeStation() {
	    super(Material.WOOD);

	    this.setSoundType(SoundType.WOOD);
	    this.setResistance(5f);
	    this.setHardness(1f);

	    this.setHarvestLevel("axe", 0);
	}

	@Override
	public boolean openGui(EntityPlayer player, World world, BlockPos pos) {
		try {
			player.openGui(MoreTCon.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			if (player.openContainer instanceof BaseContainer) {
				((BaseContainer<?>) player.openContainer).syncOnOpen((EntityPlayerMP) player);
			}
		} catch (Throwable t) {
			
		}
		return true;
	}

	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
		return new TileReforgeStation();
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] {}, new IUnlistedProperty[] { TEXTURE, INVENTORY, FACING });
	}

	@Override
	public int getGuiNumber(IBlockState state) {
		return ModInfo.MODID.hashCode() + 1;
	}
}

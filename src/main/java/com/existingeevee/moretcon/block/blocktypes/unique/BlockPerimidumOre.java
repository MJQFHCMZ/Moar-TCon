package com.existingeevee.moretcon.block.blocktypes.unique;

import com.existingeevee.moretcon.block.ore.BlockOre;
import com.existingeevee.moretcon.inits.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockPerimidumOre extends BlockOre {

	public BlockPerimidumOre() {
		super("orePerimidum", 5, Items.BEEF, 0, 1);
	}

	public static final PropertyBool COVERED = PropertyBool.create("covered");

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos.down()).getBlock();
		return state.withProperty(COVERED, Boolean.valueOf(block == ModBlocks.blockDarkBrinkstone));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, COVERED);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}
}
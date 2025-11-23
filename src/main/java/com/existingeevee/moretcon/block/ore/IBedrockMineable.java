package com.existingeevee.moretcon.block.ore;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBedrockMineable {
	
	default boolean isBedrockLike(IBlockState blockState, World worldIn, BlockPos pos) {
		return true;
	}
	
	default boolean isSoftBedrock(IBlockState blockState, World worldIn, BlockPos pos) {
		return false;
	}
}

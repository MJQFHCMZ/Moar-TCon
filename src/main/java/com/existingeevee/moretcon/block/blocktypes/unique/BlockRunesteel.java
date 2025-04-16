package com.existingeevee.moretcon.block.blocktypes.unique;

import java.time.LocalDate;
import java.time.Month;

import com.existingeevee.moretcon.block.blocktypes.BlockBase;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRunesteel extends BlockBase {

	public static final PropertyBool ALT_TEXTURE = PropertyBool.create("alt");

	public BlockRunesteel(String itemName, Material material, int harvestLevel) {
		super(itemName, material, harvestLevel);
		this.setDefaultState(blockState.getBaseState().withProperty(ALT_TEXTURE, false));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ALT_TEXTURE, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ALT_TEXTURE) ? 1 : 0;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
		boolean useAltTexture = meta == 1;

		if (!world.isRemote) {
			LocalDate date = LocalDate.now();
			useAltTexture = useAltTexture || world.rand.nextInt(1000) == 0 || date.getMonth() == Month.APRIL && date.getDayOfMonth() == 1;
		}
		if (useAltTexture)
			state = state.withProperty(ALT_TEXTURE, true);
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ALT_TEXTURE);
	}
}

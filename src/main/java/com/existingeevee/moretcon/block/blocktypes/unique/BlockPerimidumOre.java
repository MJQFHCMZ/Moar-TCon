package com.existingeevee.moretcon.block.blocktypes.unique;

import com.existingeevee.moretcon.block.ore.BlockOre;
import com.existingeevee.moretcon.client.actions.PerimidumAuraAction;
import com.existingeevee.moretcon.inits.ModItems;
import com.existingeevee.moretcon.other.ClusterTickingHandler.ISimpleClusterable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPerimidumOre extends BlockOre implements ISimpleClusterable {

	public BlockPerimidumOre() {
		super("orePerimidum", 5, ModItems.gemPerimidum, 1, 1);
		this.setDefaultState(this.getDefaultState().withProperty(COVERED, true));
	}

	public static final PropertyBool COVERED = PropertyBool.create("covered");

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos.down()).getBlock();
		return state.withProperty(COVERED, block != this);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, COVERED);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public int minClusterSize() {
		return 7;
	}

	@Override
	public int maxClusterRadius() {
		return 6;
	}

	@Override
	public void onClusterTick(Vec3d clusterCenter, World worldIn, double mass, AxisAlignedBB bound) {
		
		NBTTagCompound payload = new NBTTagCompound();
		payload.setDouble("Mass", mass);
		payload.setDouble("Height", bound.maxY - bound.minY);
		PerimidumAuraAction.INSTANCE.run(worldIn, bound.getCenter().x, bound.minY, bound.getCenter().z, payload);
	}
}
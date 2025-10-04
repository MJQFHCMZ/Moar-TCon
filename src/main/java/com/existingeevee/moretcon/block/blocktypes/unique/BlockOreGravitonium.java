package com.existingeevee.moretcon.block.blocktypes.unique;

import com.existingeevee.moretcon.block.ore.BlockOreMetal;
import com.existingeevee.moretcon.other.ClusterTickingHandler.ISimpleClusterable;

import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockOreGravitonium extends BlockOreMetal implements ISimpleClusterable {

	public BlockOreGravitonium(String name, int harvest, Item toDrop) {
		super(name, harvest, toDrop);
	}

	@Override
	public int minClusterSize() {
		return 12;
	}

	@Override
	public void onClusterTick(Vec3d clusterAvgCenter, World world, double mass, AxisAlignedBB bounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int maxClusterRadius() {
		return 12;
	}
}

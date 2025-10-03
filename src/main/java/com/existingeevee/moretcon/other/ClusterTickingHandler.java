package com.existingeevee.moretcon.other;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ClusterTickingHandler {

	protected static final Map<World, Set<BlockPos>> CLUSTERABLE_POSITIONS = new WeakHashMap<>();

	public static void addTick(World world, BlockPos pos) {
		if (world.isRemote)
			return;
		Set<BlockPos> set = CLUSTERABLE_POSITIONS.computeIfAbsent(world, w -> new HashSet<>());
		set.add(pos);
	}

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote || event.world.getTotalWorldTime() % 10 != 0)
			return;
						
		try {
			if (event.phase == Phase.START) {
				List<Cluster> clusters = new ArrayList<>();
				Cluster lastCluster = null; // chances are, the next blockpos is gonna be attached to the last cluster

				if (!CLUSTERABLE_POSITIONS.containsKey(event.world))
					return;
								
				for (BlockPos pos : CLUSTERABLE_POSITIONS.get(event.world)) {
					if (!event.world.isBlockLoaded(pos))
						continue;
										
					IBlockState state = event.world.getBlockState(pos);
					if (state.getBlock() instanceof IClusterable) {
						IClusterable cluster = (IClusterable) state.getBlock();
						
						if (!cluster.isClusterable(state, event.world, pos))
							continue;
						
						if (lastCluster != null) {
							if (lastCluster.getClusterType() == cluster && lastCluster.isInRange(pos)) {
								lastCluster.addToCluster(pos);
								continue;
							}
						}

						boolean found = false;
						for (Cluster c : clusters) {
							if (c == lastCluster)
								continue;

							if (c.getClusterType() == cluster && c.isInRange(pos)) {
								c.addToCluster(pos);
								found = true;
								break;
							}
						}
						if (found)
							continue;

						clusters.add(new Cluster(cluster, pos));
					}
				}

				for (Cluster c : clusters) {
					if (c.getCount() >= c.getClusterType().minClusterSize())
					c.getClusterType().onClusterTick(c.getLocation(), event.world, c.getCount(), c.getBounds());
				}
				
				CLUSTERABLE_POSITIONS.remove(event.world);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static interface IClusterable {
		default boolean isClusterable(IBlockState state, World world, BlockPos pos) {			
			return state.getBlock() == this;
		}

		int minClusterSize();

		int maxClusterRadius();

		void onClusterTick(Vec3d clusterCenter, World world, int clusterCount, AxisAlignedBB axisAlignedBB);
	}

	public static class Cluster {
		int count = 0;
		Vec3d location = Vec3d.ZERO;

		final Vec3d origin;
		final IClusterable clusterType;
		
		AxisAlignedBB bounds;
		
		public Cluster(IClusterable cluster, BlockPos origin) {
			this.clusterType = cluster;
			this.origin = new Vec3d(origin).addVector(0.5, 0.5, 0.5);
			bounds = new AxisAlignedBB(origin, origin);
			addToCluster(origin);
		}

		public void addToCluster(BlockPos pos) {
			int total = count + 1;

			location = location.scale(count).add(new Vec3d(pos).addVector(0.5, 0.5, 0.5)).scale(1d / total);
			count = total;
			
			bounds = new AxisAlignedBB(
					Math.min(bounds.minX, pos.getX()), Math.min(bounds.minY, pos.getY()), Math.min(bounds.minZ, pos.getZ()),
					Math.max(bounds.maxX, pos.getX() + 1), Math.max(bounds.maxY, pos.getY() + 1), Math.max(bounds.maxZ, pos.getZ() + 1)
					);
		}

		public boolean isInRange(BlockPos pos) {
			Vec3d center = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
			Vec3d newVec = location.scale(count).add(center).scale(1d / (count + 1));
			double maxDist = getClusterType().maxClusterRadius();
			if (newVec.squareDistanceTo(origin) > maxDist * maxDist || center.squareDistanceTo(location) > maxDist * maxDist) {
				return false;
			}

			return true;
		}

		public IClusterable getClusterType() {
			return clusterType;
		}

		public Vec3d getOrigin() {
			return origin;
		}

		public Vec3d getLocation() {
			return location;
		}

		public int getCount() {
			return count;
		}

		public AxisAlignedBB getBounds() {
			return bounds;
		}
	}
}

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
						IClusterType type = cluster.getType();

						if (type == null)
							 continue;
						
						double mass = cluster.getBlockMass(state, event.world, pos);
						
						//see above
						if (lastCluster != null) {
							if (lastCluster.getClusterType() == cluster.getType() && lastCluster.isInRange(pos, mass)) {
								lastCluster.addToCluster(pos, mass);
								continue;
							}
						}

						boolean found = false;
						for (Cluster c : clusters) {
							if (c == lastCluster)
								continue;

							if (c.getClusterType() == cluster.getType() && c.isInRange(pos, mass)) {
								c.addToCluster(pos, mass);
								found = true;
								break;
							}
						}
						if (found)
							continue;

						clusters.add(new Cluster(cluster, pos, event.world));
					}
				}

				for (Cluster c : clusters) {
					if (c.getMass() >= c.getClusterType().minClusterSize())
					c.getClusterType().onClusterTick(c.getLocation(), event.world, c.getMass(), c.getBounds());
				}
				
				CLUSTERABLE_POSITIONS.remove(event.world);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public static interface IClusterType { 
		
		int minClusterSize();

		void onClusterTick(Vec3d clusterAvgCenter, World world, double mass, AxisAlignedBB bounds);

		int maxClusterRadius();
		
	}

	public static interface ISimpleClusterable extends IClusterable, IClusterType {
		default IClusterType getType() {
			return this;
		}
	}

	public static interface IClusterable {
		default double getBlockMass(IBlockState state, World world, BlockPos pos) {		
			return 1;
		}

		IClusterType getType();
	}

	public static class Cluster {
		double mass = 0;
		Vec3d location = Vec3d.ZERO;

		final Vec3d origin;
		final IClusterType clusterType;
		
		AxisAlignedBB bounds;
		
		public Cluster(IClusterable cluster, BlockPos origin, World world) {
			this.clusterType = cluster.getType();
			this.origin = new Vec3d(origin).addVector(0.5, 0.5, 0.5);
			bounds = new AxisAlignedBB(origin, origin);
			addToCluster(origin, cluster.getBlockMass(world.getBlockState(origin), world, origin));
		}

		public void addToCluster(BlockPos pos, double additionalMass) {
			double total = mass + additionalMass;

			location = location.scale(mass).add(new Vec3d(pos).addVector(0.5, 0.5, 0.5).scale(additionalMass)).scale(1d / total);
			mass = total;
			
			bounds = new AxisAlignedBB(
					Math.min(bounds.minX, pos.getX()), Math.min(bounds.minY, pos.getY()), Math.min(bounds.minZ, pos.getZ()),
					Math.max(bounds.maxX, pos.getX() + 1), Math.max(bounds.maxY, pos.getY() + 1), Math.max(bounds.maxZ, pos.getZ() + 1)
					);
		}
													  
		public boolean isInRange(BlockPos pos, double additionalMass) {
			Vec3d center = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
			//location = location.scale(mass).add(center.scale(additionalMass)).scale(1d / (mass + additionalMass));
			Vec3d newVec = location.scale(mass).add(center.scale(additionalMass)).scale(1d / (mass + additionalMass));
			double maxDist = getClusterType().maxClusterRadius();
			if (newVec.squareDistanceTo(origin) > maxDist * maxDist || center.squareDistanceTo(location) > maxDist * maxDist) {
				return false;
			}

			return true;
		}

		public IClusterType getClusterType() {
			return clusterType;
		}

		public Vec3d getOrigin() {
			return origin;
		}

		public Vec3d getLocation() {
			return location;
		}

		public double getMass() {
			return mass;
		}

		public AxisAlignedBB getBounds() {
			return bounds;
		}
	}
}

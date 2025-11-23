package com.existingeevee.moretcon.block.ore;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBedrockOreMetal extends BlockOreMetal implements IBedrockMineable {

	public BlockBedrockOreMetal(String name, int harvest, Item toDrop) {
		super(name, harvest, toDrop);
		this.setBlockUnbreakable();
	}

	@Override
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
		if (entity instanceof net.minecraft.entity.boss.EntityDragon) {
			return false;
		} else if ((entity instanceof EntityWither) || (entity instanceof EntityWitherSkull)) {
			return false;
		}
		return true;
	}

	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state) {
		return EnumPushReaction.BLOCK;
	}
	
	@Override
	public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, net.minecraft.entity.EntityLiving.SpawnPlacementType type) {
		return false;
	}
	
	@Override
	public boolean isBedrockLike(IBlockState blockState, World worldIn, BlockPos pos) {
		return true;
	}
	
	@Override
	public boolean isSoftBedrock(IBlockState blockState, World worldIn, BlockPos pos) {
		return false;
	}
}

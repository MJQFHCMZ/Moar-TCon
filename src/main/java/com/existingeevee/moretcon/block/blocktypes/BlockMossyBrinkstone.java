package com.existingeevee.moretcon.block.blocktypes;

import java.util.Random;

import com.existingeevee.moretcon.inits.ModBlocks;
import com.existingeevee.moretcon.inits.ModItems;

import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMossyBrinkstone extends BlockBase implements IGrowable { // BlockTallGrass

	public BlockMossyBrinkstone(String itemName, Material material, int harvestLevel) {
		super(itemName, material, harvestLevel);
		this.setTickRandomly(true);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!worldIn.isRemote) {
			if (!worldIn.isAreaLoaded(pos, 3))
				return;
			if (worldIn.getLightFromNeighbors(pos.up()) < 4 && worldIn.getBlockState(pos.up()).getLightOpacity(worldIn, pos.up()) > 2) {
				worldIn.setBlockState(pos, ModBlocks.blockBrinkstone.getDefaultState());
			} else {
				if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
					for (int i = 0; i < 4; ++i) {
						BlockPos blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);

						if (blockpos.getY() >= 0 && blockpos.getY() < 256 && !worldIn.isBlockLoaded(blockpos)) {
							return;
						}

						IBlockState iblockstate = worldIn.getBlockState(blockpos.up());
						IBlockState iblockstate1 = worldIn.getBlockState(blockpos);

						if (iblockstate1.getBlock() == ModBlocks.blockBrinkstone && iblockstate.getLightOpacity(worldIn, pos.up()) <= 2) {
							worldIn.setBlockState(blockpos, this.getDefaultState());
						}
					}
				}
			}
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ModItems.perimimoss == null ? Item.getItemFromBlock(this) : ModItems.perimimoss;
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random random) {
		return random.nextInt(2 + fortune) + 1;
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		BlockPos blockpos = pos.up();

		for (int i = 0; i < 128; ++i) {
			BlockPos blockpos1 = blockpos;
			int j = 0;

			while (true) {
				if (j >= i / 16) {
					if (worldIn.isAirBlock(blockpos1)) {
						if (rand.nextInt(8) == 0) {
							worldIn.getBiome(blockpos1).plantFlower(worldIn, rand, blockpos1);
						} else {
							IBlockState iblockstate1 = Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);

							if (Blocks.TALLGRASS.canBlockStay(worldIn, blockpos1, iblockstate1)) {
								worldIn.setBlockState(blockpos1, iblockstate1, 3);
							}
						}
					}

					break;
				}

				blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

				if (worldIn.getBlockState(blockpos1.down()).getBlock() != Blocks.GRASS || worldIn.getBlockState(blockpos1).isNormalCube()) {
					break;
				}

				++j;
			}
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(new ItemStack(ModBlocks.blockBrinkstone));
		super.getDrops(drops, world, pos, state, fortune);
	}
	
	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable) {
		return plantable.getPlant(world, pos).getBlock() instanceof IBrinkstonePlant;
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
		return true;
	}
	
	public static interface IBrinkstonePlant {
		
	}
}

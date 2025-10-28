package com.existingeevee.moretcon.block.blocktypes;

import com.existingeevee.moretcon.traits.ModTraits;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEtherealBase extends BlockBase {

	public static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(-100.0D, -100.0D, -100.0D, -100.0D, -100.0D, -100.0D);

	protected boolean canBeReplaced = true;

	public BlockEtherealBase(String itemName, Material material, int harvestLevel) {
		super(itemName, material, harvestLevel);
		MinecraftForge.EVENT_BUS.register(this);
		this.translucent = true;
		this.setResistance(Float.POSITIVE_INFINITY);
	}

	@Override
	public boolean isTopSolid(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean causesSuffocation(IBlockState state) {
		return false;
	}

	protected static final ThreadLocal<Boolean> USED_ETHERAL_BLOCK = ThreadLocal.withInitial(() -> false);

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		if (USED_ETHERAL_BLOCK.get()) //we want it so that ethereal blocks can be placed on ethereal blocks
			return false;
		return canBeReplaced;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canSilkHarvest() {
		return true;
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		return -1;// Float.MAX_VALUE * 0.99f;
	}

	public float getTrueHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		return super.getBlockHardness(blockState, worldIn, pos);
	}

	@Override
	public ItemBlock createBlockItem() {
		return new ItemBlock(this) {

			@Override
			public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
				USED_ETHERAL_BLOCK.set(true);
				EnumActionResult ret = super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
				USED_ETHERAL_BLOCK.remove();
				return ret;
			}
		};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if (source instanceof WorldClient) {
			return this.getBoundingBoxClient(state, source, pos);
		}
		return FULL_BLOCK_AABB;
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getBoundingBoxClient(IBlockState state, IBlockAccess source, BlockPos pos) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player != null) {
			ItemStack stack = player.getHeldItemMainhand();
			if (ModTraits.etheralHarvest.isToolWithTrait(stack) || Block.getBlockFromItem(stack.getItem()) instanceof BlockEtherealBase) {
				return FULL_BLOCK_AABB;
			}
		}
		return EMPTY_AABB;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
}

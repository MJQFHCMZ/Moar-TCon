package com.existingeevee.moretcon.block.blocktypes.unique;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.existingeevee.moretcon.MoreTCon;
import com.existingeevee.moretcon.block.ISimpleBlockItemProvider;
import com.existingeevee.moretcon.block.tile.TileCatalyzationChamber;
import com.existingeevee.moretcon.other.ModTabs;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

public class BlockCatalyzationChamber extends BlockContainer implements ISimpleBlockItemProvider {

	public static final PropertyBool VALID = PropertyBool.create("valid");

	public BlockCatalyzationChamber() {
		super(Material.ROCK);
		this.setCreativeTab(ModTabs.moarTConMisc);
		this.setUnlocalizedName(MiscUtils.createNonConflictiveName("blockcatalyzationchamber"));
	}

	public boolean isValid(IBlockAccess worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos.up());
		return te instanceof TileSmeltery && ((TileSmeltery) te).isActive();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(VALID, isValid(worldIn, pos));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VALID);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
		return new TileCatalyzationChamber(); 
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.translateToLocal(this.getUnlocalizedName() + ".desc"));
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		int amnt = 0;
		if (tile instanceof TileCatalyzationChamber) {
			TileCatalyzationChamber cata = (TileCatalyzationChamber) tile;
			ItemStackHandler inv = cata.inventory;
			for (int i = 0; i < inv.getSlots(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (!stack.isEmpty()) //BlockDispenser
					amnt++;
			}
		}
		return Math.min(15, amnt);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof TileCatalyzationChamber) {
				playerIn.openGui(MoreTCon.instance, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());
				if (playerIn.openContainer instanceof BaseContainer<?>) {
					((BaseContainer<?>) playerIn.openContainer).syncOnOpen((EntityPlayerMP) playerIn);
				}
			}

			return true;
		}
	}
}

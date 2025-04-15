package com.existingeevee.moretcon.other.utils;

import java.util.HashMap;
import java.util.Map;

import com.existingeevee.moretcon.ModInfo;
import com.existingeevee.moretcon.devtools.DevEnvHandler;
import com.existingeevee.moretcon.inits.misc.ModSponges;
import com.existingeevee.moretcon.other.sponge.SpongeRegistry.GravitoniumSpongeItem;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelRegistryHelper {

	@SideOnly(Side.CLIENT)
	public static void registerFluidCustomMeshesAndStates(Block blockIn) {
		ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(blockIn), new ItemMeshDefinition() {

			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				return new ModelResourceLocation(blockIn.getRegistryName(), "fluid");
			}

		});

		ModelLoader.setCustomStateMapper(blockIn, new StateMapperBase() {

			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(blockIn.getRegistryName(), "fluid");
			}

		});
	}

	@SideOnly(Side.CLIENT)
	public static void registerSponge(GravitoniumSpongeItem item) {
		ResourceLocation gsLocation = ModSponges.gravitoniumSponge.getRegistryName();

		for (int i = 2; i <= item.recipe.steps.length; i++) {
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(new ResourceLocation(gsLocation.getResourceDomain(), "partial" + gsLocation.getResourcePath()), "inventory"));
		}
		ModelLoader.setCustomModelResourceLocation(item, 1, new ModelResourceLocation(new ResourceLocation(gsLocation.getResourceDomain(), gsLocation.getResourcePath()), "inventory"));
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(new ResourceLocation(gsLocation.getResourceDomain(), "complete" + gsLocation.getResourcePath()), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemModel(Item item) {
		String itemName = item.getUnlocalizedName().replaceFirst("item." + ModInfo.MODID + ".", "");
		ModelResourceLocation model = new ModelResourceLocation(ModInfo.MODID + ":" + itemName, "inventory");
		ModelLoader.setCustomModelResourceLocation(item, 0, model);

		Map<String, String> map = new HashMap<>();
		map.put("texturePath", ModInfo.MODID + ":items/" + itemName);
		DevEnvHandler.copyAssetTemplate("items/default_item", model, "models/item", map);
	}

	@SideOnly(Side.CLIENT)
	public static void registerBlockModel(Block block) {
		String itemName = block.getUnlocalizedName().replaceFirst("tile." + ModInfo.MODID + ".", "");
		ModelResourceLocation model = new ModelResourceLocation(ModInfo.MODID + ":" + itemName, "inventory");
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, model);

		//this one is a lot more complicated
		//we only want to continue if the blockstate is missing
		Map<String, String> map = new HashMap<>();
		String template;
		if (block instanceof IFluidBlock) {
			IFluidBlock fluidBlock = (IFluidBlock) block;
			map.put("fluidName", fluidBlock.getFluid().getName());
			template = "fluid_state";
		} else {
			map.put("modelLocation", ModInfo.MODID + ":" + itemName);
			template = "default_state";
		}
		
		boolean blockStateMissing = DevEnvHandler.copyAssetTemplate("blockstates/" + template, block.getRegistryName(), "blockstates", map);

		if (blockStateMissing) {
			map = new HashMap<>();

			//The actual block model
			if (!(block instanceof IFluidBlock)) {
				map.put("texturePath", ModInfo.MODID + ":blocks/" + itemName);
				DevEnvHandler.copyAssetTemplate("blocks/block_cube_all", model, "models/block", map);	
			}
		}
		
		map = new HashMap<>();

		//the item model tho, we register regardless of the blockmodel, or at least tries its best to
		if (block instanceof IFluidBlock) {
			IFluidBlock fluidBlock = (IFluidBlock) block;
			map.put("texturePath", ModInfo.MODID + ":blocks/fluids/" + fluidBlock.getFluid().getName() + "_still");
			DevEnvHandler.copyAssetTemplate("items/default_item", model, "models/item", map);		
		} else {
			map.put("parentBlockModel", ModInfo.MODID + ":block/" + itemName);
			DevEnvHandler.copyAssetTemplate("items/block_item", model, "models/item", map);	
		}
	}
}

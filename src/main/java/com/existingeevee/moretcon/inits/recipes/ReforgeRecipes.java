package com.existingeevee.moretcon.inits.recipes;

import org.apache.commons.lang3.tuple.Pair;

import com.existingeevee.moretcon.config.ConfigHandler;
import com.existingeevee.moretcon.inits.ModItems;
import com.existingeevee.moretcon.other.RecipeHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.oredict.OreIngredient;

public class ReforgeRecipes {

	public static void init(Register<IRecipe> event) {
		if (ConfigHandler.shouldAllowMainContent) {
			event.getRegistry().register(
					RecipeHelper.createRecipe("reforgeheavy", new ItemStack(ModItems.reforgeHeavy),
							new String[] {
									"CSC",
									"SOS",
									"CSC"
							},
							Pair.of('C', new OreIngredient("cobblestone")),
							Pair.of('S', new OreIngredient("stone")),
							Pair.of('O', new OreIngredient("obsidian"))));

			event.getRegistry().register(
					RecipeHelper.createRecipe("reforgesharpened", new ItemStack(ModItems.reforgeSharpened),
							new String[] {
									" D ",
									"DSD",
									"QDQ"
							},
							Pair.of('Q', new OreIngredient("gemQuartz")),
							Pair.of('S', new OreIngredient("ingotSteel")),
							Pair.of('D', new OreIngredient("gemDiamond"))));
			
			event.getRegistry().register(
					RecipeHelper.createRecipe("reforgeconsistant", new ItemStack(ModItems.reforgeConsistant),
							new String[] {
									" B ",
									"RPR",
									" B "
							},
							Pair.of('R', new OreIngredient("ingotRunesteel")),
							Pair.of('B', new OreIngredient("brinkstone")),
							Pair.of('P', new OreIngredient("gemPerimidum"))));
		}
	}
}

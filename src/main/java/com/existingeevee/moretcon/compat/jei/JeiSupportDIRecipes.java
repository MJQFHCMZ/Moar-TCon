package com.existingeevee.moretcon.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.existingeevee.moretcon.other.DynamicInputRecipe;
import com.google.common.collect.Lists;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class JeiSupportDIRecipes extends JeiCustomContainer {

	public JeiSupportDIRecipes() {
		super(null, () -> true);
	}

	@Override
	public void onRun(IModRegistry r) {
		r.handleRecipes(DynamicInputRecipe.class, rc -> new Wrapper(rc, r.getJeiHelpers()), VanillaRecipeCategoryUid.CRAFTING);
	}

	public static class Wrapper implements IRecipeWrapper, IShapedCraftingRecipeWrapper, ICustomCraftingRecipeWrapper {

		private final IJeiHelpers jeiHelpers;

		private final DynamicInputRecipe recipe;
		
		public Wrapper(DynamicInputRecipe rc, IJeiHelpers jeiHelpers) {
			this.jeiHelpers = jeiHelpers;
			this.recipe = rc;
		}

		@Override
		public int getHeight() {
			return recipe.getHeight();
		}

		@Override
		public int getWidth() {
			return recipe.getWidth();
		}

		@Override
		public void getIngredients(IIngredients arg0) {
			IStackHelper stackHelper = jeiHelpers.getStackHelper();

			List<Ingredient> inputs = new ArrayList<>();
			
			for (Ingredient i : recipe.getIngredients()) {
				if (i == DynamicInputRecipe.PLACEHOLDER) {
					Ingredient.fromStacks(recipe.getInputSamples().stream().toArray(ItemStack[]::new));
					continue;
				}
				inputs.add(i);
			}

			List<ItemStack> listOutput = recipe.getInputSamples().stream().map(recipe::output).collect(Collectors.toCollection(Lists::newArrayList));
			
			arg0.setInputLists(VanillaTypes.ITEM, stackHelper.expandRecipeItemStackInputs(inputs));
			arg0.setOutputLists(VanillaTypes.ITEM, stackHelper.expandRecipeItemStackInputs(Lists.newArrayList(Ingredient.fromStacks(listOutput.toArray(new ItemStack[0])))));
		}

		@Override
		public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
		   
		}
	}
}

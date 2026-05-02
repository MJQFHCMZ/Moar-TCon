package com.existingeevee.moretcon.compat.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.existingeevee.moretcon.ModInfo;
import com.existingeevee.moretcon.compat.jei.CatalyzedAlloyRecipeCategory.CatalyzedAlloyRecipeWrapper;
import com.existingeevee.moretcon.item.ItemCatalyst;
import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.google.common.collect.ImmutableList;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;

public class CatalyzedAlloyRecipeCategory implements IRecipeCategory<CatalyzedAlloyRecipeWrapper> {

	public static String CATEGORY = MiscUtils.createNonConflictiveName("cata_alloy");
	public static ResourceLocation background_loc = Util.getResource("textures/gui/jei/smeltery.png");

	protected final IDrawable background;
	protected final IDrawableAnimated arrow;

	public CatalyzedAlloyRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(background_loc, 0, 60, 160, 60);

		IDrawableStatic arrowDrawable = guiHelper.createDrawable(background_loc, 160, 60, 24, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Nonnull
	@Override
	public String getUid() {
		return CATEGORY;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return Util.translate("gui.jei.cata_alloy.title");
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {
		arrow.draw(minecraft, 76, 22);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CatalyzedAlloyRecipeWrapper recipe, IIngredients ingredients) {
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();

		List<FluidStack> inputs = recipe.inputs;
		List<FluidStack> outputs = ingredients.getOutputs(FluidStack.class).get(0);

		float w = 36f / inputs.size();

		// find maximum used amount in the recipe so relations are correct
		int max_amount = 0;
		for (FluidStack fs : inputs) {
			if (fs.amount > max_amount) {
				max_amount = fs.amount;
			}
		}
		for (FluidStack fs : outputs) {
			if (fs.amount > max_amount) {
				max_amount = fs.amount;
			}
		}

		// inputs
		for (int i = 0; i < inputs.size(); i++) {
			int x = 21 + (int) (i * w);
			int _w = (int) ((i + 1) * w - i * w);
			fluids.init(i + 1, true, x, 11, _w, 32, max_amount, false, null);
		}

		// output
		fluids.init(0, false, 118, 11, 18, 32, max_amount, false, null);
		fluids.set(ingredients);
		
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		
		stacks.init(0, true, 78, 38);
		stacks.set(ingredients);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return ImmutableList.of();
	}

	@Override
	public IDrawable getIcon() {
		// use the default icon
		return null;
	}

	@Override
	public String getModName() {
		return ModInfo.NAME;
	}

	public static List<CatalyzedAlloyRecipe> getCatalyzedAlloyRecipes() {
		List<CatalyzedAlloyRecipe> recipes = new ArrayList<>();

		for (Item item : ForgeRegistries.ITEMS) {
			if (item instanceof ItemCatalyst) {
				ItemCatalyst cat = (ItemCatalyst) item;
				for (AlloyRecipe alloy : cat.getCatalyzedAlloys()) {
					if (alloy.getFluids() != null && alloy.getFluids().size() > 0 && alloy.getResult() != null && alloy.getResult().amount > 0) {
						recipes.add(new CatalyzedAlloyRecipe(alloy, cat));
					}
				}
			}
		}

		return recipes;
	}

	public static class CatalyzedAlloyRecipe {
		public CatalyzedAlloyRecipe(AlloyRecipe alloy, ItemCatalyst catalyst) {
			super();
			this.alloy = alloy;
			this.catalyst = catalyst;
		}

		final AlloyRecipe alloy;
		final ItemCatalyst catalyst;

		public AlloyRecipe getAlloy() {
			return alloy;
		}

		public ItemCatalyst getCatalyst() {
			return catalyst;
		}
	}

	public static class CatalyzedAlloyRecipeHandler implements IRecipeWrapperFactory<CatalyzedAlloyRecipe> {
		@Nonnull
		@Override
		public IRecipeWrapper getRecipeWrapper(@Nonnull CatalyzedAlloyRecipe recipe) {
			return new CatalyzedAlloyRecipeWrapper(recipe);
		}
	}

	public static class CatalyzedAlloyRecipeWrapper implements IRecipeWrapper {
		protected final List<ItemStack> catalyst;

		protected final List<FluidStack> inputs;
		protected final List<FluidStack> outputs;

		public CatalyzedAlloyRecipeWrapper(CatalyzedAlloyRecipe recipe) {
			this.catalyst = ImmutableList.of(new ItemStack(recipe.getCatalyst()));
			this.inputs = recipe.getAlloy().getFluids();
			this.outputs = ImmutableList.of(recipe.getAlloy().getResult());
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputs(FluidStack.class, inputs);
			ingredients.setOutputs(FluidStack.class, outputs);
			ingredients.setInputs(ItemStack.class, catalyst);
		}
	}
}

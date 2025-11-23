package com.existingeevee.moretcon.other;

import java.util.Collection;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class DynamicInputRecipe extends ShapedRecipes {

	private static final ThreadLocal<DynamicInputRecipe> CURRENT_RECIPE = ThreadLocal.withInitial(() -> null);

	public static final Ingredient PLACEHOLDER = new Ingredient() {
		@Override
		public boolean apply(@Nullable ItemStack p_apply_1_) {
			if (CURRENT_RECIPE.get() != null) {
				boolean valid = CURRENT_RECIPE.get().isValid.apply(p_apply_1_);
				return valid;
			}
			return false;
		}
	};

	final Function<ItemStack, Boolean> isValid;
	final Function<ItemStack, ItemStack> getOutput;

	final Collection<ItemStack> inputSamples;

	public DynamicInputRecipe(String group, int width, int height, NonNullList<Ingredient> ingredients, Function<ItemStack, Boolean> isValid, Function<ItemStack, ItemStack> getOutput, Collection<ItemStack> sampleInputs) {
		super(group, width, height, ingredients, ItemStack.EMPTY);
		this.isValid = isValid;
		this.getOutput = getOutput;
		this.inputSamples = sampleInputs;
	}

	public ItemStack output(ItemStack stack) {
		return getOutput.apply(stack);
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		CURRENT_RECIPE.set(this);
		super.matches(inv, worldIn);
		CURRENT_RECIPE.set(null);
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		for (int i = 0; i <= inv.getWidth() - this.recipeWidth; ++i) {
			for (int j = 0; j <= inv.getHeight() - this.recipeHeight; ++j) {
				ItemStack stack = getOutput(inv, i, j, true);
				if (stack != null) {
					return stack;
				}
				stack = getOutput(inv, i, j, false);
				if (stack != null) {
					return stack;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	private ItemStack getOutput(InventoryCrafting inv, int p_77573_2_, int p_77573_3_, boolean mirrored) {
		for (int i = 0; i < inv.getWidth(); ++i) {
			for (int j = 0; j < inv.getHeight(); ++j) {
				int k = i - p_77573_2_;
				int l = j - p_77573_3_;
				Ingredient ingredient = Ingredient.EMPTY;

				if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {
					if (mirrored) {
						ingredient = this.recipeItems.get(this.recipeWidth - k - 1 + l * this.recipeWidth);
					} else {
						ingredient = this.recipeItems.get(k + l * this.recipeWidth);
					}
				}

				if (ingredient == PLACEHOLDER) {
					return output(inv.getStackInRowAndColumn(i, j));
				}
			}
		}
		return null;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}
	
	public Collection<ItemStack> getInputSamples() {
		return this.inputSamples;
	}
}

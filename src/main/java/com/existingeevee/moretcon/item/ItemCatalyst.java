package com.existingeevee.moretcon.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.TinkerAPIException;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;

public class ItemCatalyst extends ItemBase {

	final List<AlloyRecipe> catalyzedRecipes = new ArrayList<>();

	public ItemCatalyst(String itemName) {
		super(itemName);
		commonSetup();
	}

	public ItemCatalyst(String itemName, int hex) {
		super(itemName, hex);
		commonSetup();
	}

	public ItemCatalyst(String itemName, GlowType type, int hex) {
		super(itemName, type, hex);
		commonSetup();
	}


	private void commonSetup() {
		this.setMaxStackSize(1);
	}
	
	public Collection<AlloyRecipe> getCatalyzedAlloys() {
		return catalyzedRecipes;
	}
	
	//TODO crafttweaker

	public void registerAlloy(FluidStack result, FluidStack... inputs) {
		if (result.amount < 1) {
			error("Alloy Recipe: Resulting alloy %s has to have an amount (%d)", result.getLocalizedName(), result.amount);
		}
		if (inputs.length < 2) {
			error("Alloy Recipe: Alloy for %s must consist of at least 2 liquids", result.getLocalizedName());
		}

		registerAlloy(new AlloyRecipe(result, inputs));
	}

	public void registerAlloy(AlloyRecipe recipe) {
		//TODO add events and cancelling and such
		//prob will not be in this update.
		//sowwyyy ;-;
		
		catalyzedRecipes.add(recipe);
		//waow
	}
	
	  private static void error(String message, Object... params) {
		    throw new TinkerAPIException(String.format(message, params));
		  }
}

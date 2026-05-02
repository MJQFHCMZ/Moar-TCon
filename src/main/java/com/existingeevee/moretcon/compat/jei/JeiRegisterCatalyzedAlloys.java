package com.existingeevee.moretcon.compat.jei;

import com.existingeevee.moretcon.compat.jei.CatalyzedAlloyRecipeCategory.CatalyzedAlloyRecipe;
import com.existingeevee.moretcon.compat.jei.CatalyzedAlloyRecipeCategory.CatalyzedAlloyRecipeHandler;
import com.existingeevee.moretcon.inits.ModBlocks;

import mezz.jei.api.IModRegistry;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class JeiRegisterCatalyzedAlloys extends JeiCustomContainer {

	public JeiRegisterCatalyzedAlloys() {
		super(null, () -> true);
	}

	@Override
	public void onRun(IModRegistry r) {
		r.addRecipeCatalyst(new ItemStack(ModBlocks.blockCatalyzationChamber), CatalyzedAlloyRecipeCategory.CATEGORY);
		r.addRecipeCatalyst(new ItemStack(TinkerSmeltery.smelteryController), CatalyzedAlloyRecipeCategory.CATEGORY);
		r.handleRecipes(CatalyzedAlloyRecipe.class, new CatalyzedAlloyRecipeHandler(), CatalyzedAlloyRecipeCategory.CATEGORY);

		r.addRecipes(CatalyzedAlloyRecipeCategory.getCatalyzedAlloyRecipes(), CatalyzedAlloyRecipeCategory.CATEGORY);
	}
}

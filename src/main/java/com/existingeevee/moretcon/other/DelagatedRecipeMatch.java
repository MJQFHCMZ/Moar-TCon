package com.existingeevee.moretcon.other;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.util.RecipeMatch;

public class DelagatedRecipeMatch extends RecipeMatch {

	public DelagatedRecipeMatch(RecipeMatch delagate, int amountMatched, int amountNeeded) {
		this(() -> delagate, amountMatched, amountNeeded);
	}
	
	public DelagatedRecipeMatch(Supplier<RecipeMatch> delagate, int amountMatched, int amountNeeded) {
		super(1, 1);
		this.delagate = delagate;
	}

	final Supplier<RecipeMatch> delagate;

	@Override
	public List<ItemStack> getInputs() {
		return delagate.get().getInputs();
	}

	@Override
	public Optional<Match> matches(NonNullList<ItemStack> stacks) {
		return delagate.get().matches(stacks);
	}
	
}

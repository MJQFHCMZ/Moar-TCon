package com.existingeevee.moretcon.itemsets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class ItemSet {

	private static final List<ItemSet> ITEMSETS = new ArrayList<>();
	
	public final void register() {
		if (!ITEMSETS.contains(this))
			ITEMSETS.add(this);
	}
	
	public void registerFluids() {
	}

	public void registerBlocks(IForgeRegistry<Block> registry) {
	}

	public void registerItems(IForgeRegistry<Item> registry) {
	}

	public void registerRecipes(IForgeRegistry<IRecipe> registry) {
	}

	public void preInit() {
	}

	public void init() {
	}

	public void postInit() {
	}

	public void loadComplete() {
	}

}

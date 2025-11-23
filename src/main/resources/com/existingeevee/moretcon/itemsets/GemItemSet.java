package com.existingeevee.moretcon.itemsets;

import com.existingeevee.moretcon.item.ItemBase;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class GemItemSet extends ItemSet {

	public GemItemSet(String name, Block... ores) {
		gem = new ItemBase("gem" + name);
	}

	protected Item gem;
	protected Block block;

}

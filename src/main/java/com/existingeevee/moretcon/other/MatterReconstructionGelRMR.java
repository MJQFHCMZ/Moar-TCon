package com.existingeevee.moretcon.other;

import java.util.PriorityQueue;

import com.existingeevee.moretcon.inits.ModItems;
import com.existingeevee.moretcon.other.utils.MirrorUtils;
import com.existingeevee.moretcon.other.utils.MirrorUtils.IField;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.mantle.util.RecipeMatchRegistry;
import slimeknights.tconstruct.library.materials.Material;

public class MatterReconstructionGelRMR extends RecipeMatchRegistry {
	
	public static final IField<PriorityQueue<RecipeMatch>> items$RecipeMatchRegistry = MirrorUtils.reflectField(RecipeMatchRegistry.class, "items");
	
	public MatterReconstructionGelRMR(RecipeMatchRegistry source, int amountToMatch) {	
		if (source != null)
			this.items.addAll(items$RecipeMatchRegistry.get(source));
		this.addItem(ModItems.matterReconstructionGel, amountToMatch, Material.VALUE_Ingot);
	}
	
}

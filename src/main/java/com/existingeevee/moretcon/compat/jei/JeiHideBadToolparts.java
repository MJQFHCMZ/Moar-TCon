package com.existingeevee.moretcon.compat.jei;

import com.existingeevee.moretcon.materials.IUniqueMaterial;
import com.existingeevee.moretcon.materials.UniqueMaterial;

import mezz.jei.api.IModRegistry;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;

public class JeiHideBadToolparts extends JeiCustomContainer {

	public JeiHideBadToolparts() {
		super(null, () -> true);
	}

	@Override
	public void onRun(IModRegistry r) {
		IIngredientBlacklist blacklist = r.getJeiHelpers().getIngredientBlacklist();
		for (IUniqueMaterial mat : IUniqueMaterial.uniqueMaterials) {
			for (IToolPart part : TinkerRegistry.getToolParts()) {
				blacklist.addIngredientToBlacklist(part.getItemstackWithMaterial((Material) mat));
			}

			ToolPart part = UniqueMaterial.getToolPartFromResourceLocation(mat.getPartResLoc());
			if (part != null) {
				blacklist.removeIngredientFromBlacklist(part.getItemstackWithMaterial((Material) mat));
			}

			for (ToolCore tool : TinkerRegistry.getTools()) {
				NonNullList<ItemStack> stacks = NonNullList.create();
				tool.getSubItems(TinkerRegistry.tabTools, stacks);
				for (ItemStack s : stacks) {
					NBTTagList list = s.getTagCompound().getCompoundTag("TinkerData").getTagList("Materials",
							NBT.TAG_STRING);
					boolean found = false;
					for (NBTBase base : list) {
						String id = ((NBTTagString) base).getString();
						if (((Material) mat).getIdentifier().equals(id)) {
							found = true;
							blacklist.addIngredientToBlacklist(s);
							break;
						}
					}
					if (found) {
						break;
					}
				}
			}
		}
	}
}

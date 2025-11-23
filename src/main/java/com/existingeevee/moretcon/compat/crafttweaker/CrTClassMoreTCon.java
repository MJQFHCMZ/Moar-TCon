package com.existingeevee.moretcon.compat.crafttweaker;

import java.util.NoSuchElementException;

import com.existingeevee.moretcon.item.tooltypes.Bomb.ExplosiveMaterialStats;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.moretcon.MoreTCon")
public class CrTClassMoreTCon {

	@ZenMethod
	public static void setExplosiveMaterialStats(String materialId, double radius, int fuseTime) {
		CraftTweakerAPI.apply(new IAction() {
			@Override
			public void apply() {
				Material mat = TinkerRegistry.getMaterial(materialId);
				if (mat == null) {
					throw new NoSuchElementException("Unknown material: " + materialId);
				}
				TinkerRegistry.addMaterialStats(mat, new ExplosiveMaterialStats(radius, fuseTime));
			}

			@Override
			public String describe() {
				return String.format("Setting explosive material stats for %s to {radius=%.2f, fuseTime=%d}",
						materialId, radius, fuseTime);
			}
		});
	}

}

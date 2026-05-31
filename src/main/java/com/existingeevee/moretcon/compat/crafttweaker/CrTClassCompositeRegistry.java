package com.existingeevee.moretcon.compat.crafttweaker;

import java.util.NoSuchElementException;

import com.existingeevee.moretcon.materials.CompositeRegistry;
import com.existingeevee.moretcon.materials.CompositeRegistry.CompositeData;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.moretcon.composite.CompositeRegistry")
public class CrTClassCompositeRegistry {

	@ZenMethod
	public static void registerComposite(String fromMaterial, String toMaterial, ILiquidStack fluid, @Optional boolean onlyOne) {
		CraftTweakerAPI.apply(new IAction() {
			@Override
			public void apply() {
				Material from = TinkerRegistry.getMaterial(fromMaterial);
				if (from == null) {
					throw new NoSuchElementException("Unknown material: " + fromMaterial);
				}
				Material to = TinkerRegistry.getMaterial(toMaterial);
				if (to == null) {
					throw new NoSuchElementException("Unknown material: " + toMaterial);
				}

				CompositeRegistry.registerComposite(new CompositeData(() -> from, () -> to, () -> CraftTweakerMC.getFluid(fluid.getDefinition()), onlyOne));
			}

			@Override
			public String describe() {
				return String.format("Registered composite material recipes for %s using %s and %s.", toMaterial, fromMaterial, fluid.getName());
			}
		});	    
	}
	
//	@ZenClass("mods.moretcon.composite.CompositeData")
//	public static class CrTClassCompositeData {
//		CompositeData data;
//	}
}

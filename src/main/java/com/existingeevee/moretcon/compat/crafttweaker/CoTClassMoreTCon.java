package com.existingeevee.moretcon.compat.crafttweaker;

import com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.ItemCoTCatalyst;
import com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.material.CoTTConUniqueMaterialBuilder;
import com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.misc.IIngredientSupplier;
import com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.misc.IItemStackSupplier;
import com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.sponge.CoTSpongeStep;
import com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.sponge.ItemCoTGravitoniumSponge;
import com.existingeevee.moretcon.other.ICustomSlotRenderer.GlowType;
import com.existingeevee.moretcon.other.sponge.SpongeRegistry;
import com.existingeevee.moretcon.other.sponge.SpongeRegistry.ItemGravitoniumSponge;
import com.existingeevee.moretcon.other.sponge.SpongeRegistry.SpongeRecipe;
import com.teamacronymcoders.base.registrysystem.ItemRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.modules.tinkers.materials.CoTTConMaterialBuilder;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.moretcon.MoreTConCoT")
@ModOnly("contenttweaker")
public class CoTClassMoreTCon {
	
	@ZenMethod
	public static CoTTConMaterialBuilder createUniqueMaterial(String identifier, String tool, String toolpart, @Optional String station, @Optional String stationDesc) {
		return new CoTTConUniqueMaterialBuilder(identifier, tool, toolpart, station, stationDesc);
	}
	
    @ZenMethod
    public static void createCatalyst(String name, @Optional(value = "-1") int glowColor, @Optional String glowType) {
    	ItemCoTCatalyst instance;
    	
    	if (glowColor >= 0) {
    		if (glowType == null || GlowType.valueOf(glowType.toUpperCase()) == null) {
    			instance = new ItemCoTCatalyst(name, glowColor);
    		} else {
    			instance = new ItemCoTCatalyst(name, GlowType.valueOf(glowType.toUpperCase()), glowColor);
    		}
    	} else {
        	instance = new ItemCoTCatalyst(name);
    	}
    	
    	ContentTweaker.instance.getRegistry(ItemRegistry.class, "ITEM").register(instance);
    }
    
    @ZenMethod
    public static void addGravitoniumSpongeAlloy(String name, IIngredient seed, String oreDict, IItemStack smeltResult, CoTSpongeStep[] steps) {
    	SpongeRecipe recipe = SpongeRegistry.createSpongeRecipe(name, oreDict, () -> CraftTweakerMC.getItemStack(smeltResult), () -> CraftTweakerMC.getIngredient(seed), steps);
    	ItemGravitoniumSponge sponge = new ItemCoTGravitoniumSponge(recipe);
    	
    	ContentTweaker.instance.getRegistry(ItemRegistry.class, "ITEM").register(sponge);
    }
    
    @ZenMethod
    public static void addGravitoniumSpongeAlloy(String name, IIngredientSupplier seed, String oreDict, IItemStackSupplier smeltResult, CoTSpongeStep[] steps) {
    	SpongeRecipe recipe = SpongeRegistry.createSpongeRecipe(name, oreDict, () -> CraftTweakerMC.getItemStack(smeltResult.get()), () -> CraftTweakerMC.getIngredient(seed.get()), steps);
    	ItemGravitoniumSponge sponge = new ItemCoTGravitoniumSponge(recipe);
    	
    	ContentTweaker.instance.getRegistry(ItemRegistry.class, "ITEM").register(sponge);
    }
}

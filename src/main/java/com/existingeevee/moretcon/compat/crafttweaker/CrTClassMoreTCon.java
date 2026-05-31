package com.existingeevee.moretcon.compat.crafttweaker;

import java.util.NoSuchElementException;

import com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.ItemCoTCatalyst;
import com.existingeevee.moretcon.item.ItemCatalyst;
import com.existingeevee.moretcon.materials.CompositeRegistry;
import com.existingeevee.moretcon.materials.CompositeRegistry.CompositeData;
import com.existingeevee.moretcon.other.ICustomSlotRenderer.GlowType;
import com.teamacronymcoders.base.registrysystem.ItemRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.moretcon.MoreTCon")
public class CrTClassMoreTCon {

    private static boolean init = false;
    
    private static void init() {
        if(!init) {
            MinecraftForge.EVENT_BUS.register(new CrTClassMoreTCon());
            init = true;
        }
    }
	
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
				return String.format("Registered composite material recipe for %s.", toMaterial);
			}
		});	    
	}
	
	//TODO removals of composites
	
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
    public static void addCatalyzedAlloy(IItemStack catalyst, ILiquidStack output, ILiquidStack[] inputs) {
        init();
		CraftTweakerAPI.apply(new IAction() {
			@Override
			public void apply() {
				Item item = CraftTweakerMC.getItemStack(catalyst).getItem();
				if (!(item instanceof ItemCatalyst)) {
					throw new NoSuchElementException("Not a catalyst item: " + item.getRegistryName());
				}
				
				ItemCatalyst catalyst = (ItemCatalyst) item;
				AlloyRecipe recipe = new AlloyRecipe(CraftTweakerMC.getLiquidStack(output), CraftTweakerMC.getLiquidStacks(inputs));
				catalyst.registerAlloy(recipe);
			}

			@Override
			public String describe() {
				return String.format("Registered catalyzed alloying recipe for %s.", CraftTweakerMC.getLiquidStack(output).getFluid());
			}
		});	 
    }
    
    @ZenMethod
    public static void removeCatalyzedAlloy(IItemStack catalyst, ILiquidStack output, @Optional ILiquidStack[] input) {
        init();
		CraftTweakerAPI.apply(new IAction() {
			@Override
			public void apply() {
				Item item = CraftTweakerMC.getItemStack(catalyst).getItem();
				if (!(item instanceof ItemCatalyst)) {
					throw new NoSuchElementException("Not a catalyst item: " + item.getRegistryName());
				}
				//TODO make you work
				((ItemCatalyst) item).removeAlloy(CraftTweakerMC.getLiquidStack(output), CraftTweakerMC.getLiquidStacks(input));				
			}

			@Override
			public String describe() {
				return String.format("Removed catalyzed alloying recipe for %s.", CraftTweakerMC.getLiquidStack(output).getFluid());
			}
		});	 
    }
}

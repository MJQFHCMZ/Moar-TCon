package com.existingeevee.moretcon.compat.crafttweaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.ItemCoTCatalyst;
import com.existingeevee.moretcon.item.ItemCatalyst;
import com.existingeevee.moretcon.item.ItemCatalyst.CatalyzedAlloyRegisterEvent;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
            MinecraftForge.EVENT_BUS.register(CrTClassMoreTCon.class);
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
    
	public static final Map<ItemCatalyst, Map<ILiquidStack, List<ILiquidStack>>> REMOVED_RECIPES = new HashMap<>();
    
    @ZenMethod
    public static void removeCatalyzedAlloy(IItemStack catalyst, ILiquidStack output, @Optional ILiquidStack[] input) {
        init();
        
        CraftTweakerAPI.apply(new IAction() {
			@Override
			public void apply() {
		        List<ILiquidStack> in = new ArrayList<>();
		        if(input == null || input.length == 0) {
		            in = null;
		        } else {
		            Collections.addAll(in, input);
		        }
				
				Item item = CraftTweakerMC.getItemStack(catalyst).getItem();
				if (!(item instanceof ItemCatalyst)) {
					throw new NoSuchElementException("Not a catalyst item: " + item.getRegistryName());
				}

	            REMOVED_RECIPES.computeIfAbsent((ItemCatalyst) item, i -> new LinkedHashMap<>()).put(output, in);
			}

			@Override
			public String describe() {
				return String.format("Removed catalyzed alloying recipe for %s.", CraftTweakerMC.getLiquidStack(output).getFluid());
			}
		});	 
    }
    
    @SubscribeEvent
    public static void onTinkerRegister(CatalyzedAlloyRegisterEvent event) {
        if(event.getRecipe() instanceof CrTAlloyRecipe) {
            return;
        }
        
        for(Map.Entry<ILiquidStack, List<ILiquidStack>> entry : REMOVED_RECIPES.getOrDefault(event.catalyst, new LinkedHashMap<>()).entrySet()) {
            
            if(event.getRecipe().getResult().isFluidEqual(((FluidStack) entry.getKey().getInternal()))) {
                if(entry.getValue() != null) {
                    List<ILiquidStack> in = entry.getValue();
                    List<FluidStack> rin = event.getRecipe().getFluids();
                    if(rin.size() == in.size()) {
                        boolean valid = true;
                        for(int i = 0; i < in.size(); i++) {
                            ILiquidStack stack = in.get(i);
                            FluidStack lStack = rin.get(i);
                            if(!lStack.isFluidEqual(((FluidStack) stack.getInternal()))) {
                                valid = false;
                                
                            }
                        }
                        if(valid) {
                            event.setCanceled(true);
                        }
                    }
                } else {
                    event.setCanceled(true);
                }
            }
        }
    }
}

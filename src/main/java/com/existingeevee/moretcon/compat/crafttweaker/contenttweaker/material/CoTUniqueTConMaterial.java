package com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.material;

import java.util.List;
import java.util.function.Supplier;

import com.existingeevee.moretcon.materials.IUniqueMaterial;
import com.teamacronymcoders.contenttweaker.modules.tinkers.materials.CoTTConMaterial;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import stanhebben.zenscript.util.Pair;

public class CoTUniqueTConMaterial extends CoTTConMaterial implements IUniqueMaterial {
	
	private ResourceLocation toolResLoc;
	private ResourceLocation partResLoc;

	private Supplier<ItemStack> crafterSupplier;
	private String craftingDescKey;
	
    public CoTUniqueTConMaterial(String identifier, int color, List<Pair<String, String>> traits, ResourceLocation partResLoc, ResourceLocation toolResLoc, ResourceLocation crafterResLoc, String craftingDescKey) {
        super(identifier, color, traits);
        
        this.toolResLoc = toolResLoc;
        this.partResLoc = partResLoc;
        this.crafterSupplier = () -> new ItemStack(ForgeRegistries.ITEMS.getValue(crafterResLoc));
        this.craftingDescKey = craftingDescKey;
    }

    @Override
    public boolean hasFluid() {
        return false;
    }
    
	@Override
	public String getLocalizedName() {
		return this.getUniqueLocName(localizedName);
	}      

	@Override
	public String getLocalizedItemName(String itemName) {
        if(itemLocalizer != null) {
            return super.getLocalizedItemName(itemName);
        }
		return this.getUniqueLocItemName(localizedName, itemName);
	}
	
    @Override
    public Fluid getFluid() {
        return null;
    }

	@Override
	public ItemStack getRepresentativeItem() {
		return this.getUniqueToolPart();
	}

	@Override
	public final boolean isCraftable() {
		return false;
	}

	@Override
	public final boolean isCastable() {
		return false;
	}

	@Override
	public ItemStack getCrafter() {
		return crafterSupplier.get();
	}

	@Override
	public String getCrafterString() {
		return craftingDescKey;
	}

	@Override
	public ResourceLocation getToolResLoc() {
		return toolResLoc;
	}

	@Override
	public ResourceLocation getPartResLoc() {
		return partResLoc;
	}
}

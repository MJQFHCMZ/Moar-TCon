package com.existingeevee.moretcon.materials;

import java.util.function.Supplier;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;

public class UniqueMaterial extends Material implements IUniqueMaterial {

	private ResourceLocation toolResLoc;
	private ResourceLocation partResLoc;

	private Supplier<ItemStack> crafterSupplier = () -> new ItemStack(Blocks.CRAFTING_TABLE);
	private String craftingDescKey = "crafting_table";

	public UniqueMaterial(String identifier, int color, ToolPart part, ToolCore tool, Supplier<ItemStack> crafterSupplier, String craftingDescKey) {
		this(identifier, color, part, tool);
		this.crafterSupplier = crafterSupplier;
		this.craftingDescKey = craftingDescKey;
	}

	public UniqueMaterial(String identifier, int color, String part, String tool, Supplier<ItemStack> crafterSupplier, String craftingDescKey) {
		this(identifier, color, part, tool);
		this.crafterSupplier = crafterSupplier;
		this.craftingDescKey = craftingDescKey;
	}

	public UniqueMaterial(String identifier, int color, ToolPart part, ToolCore tool) {
		this(identifier, color);
		this.partResLoc = part.getRegistryName();
		this.toolResLoc = tool.getRegistryName();
	}

	public UniqueMaterial(String identifier, int color, String part, String tool) {
		this(identifier, color);
		this.partResLoc = new ResourceLocation(part);
		this.toolResLoc = new ResourceLocation(tool);
	}

	private UniqueMaterial(String identifier, int color) {
		super(identifier, color, false);
		this.setCastable(false);
		this.setCraftable(false);
	}

	@Override
	public String getLocalizedName() {
		return this.getUniqueLocName(null);
	}

	public static ToolPart getToolPartFromResourceLocation(ResourceLocation res) {
		for (IToolPart part : TinkerRegistry.getToolParts()) {
			if (part instanceof ToolPart) {
				if (((ToolPart) part).getRegistryName().equals(res)) {
					return (ToolPart) part;
				}
			}
		}
		return null;
	}

	public static ToolCore getToolFromResourceLocation(ResourceLocation res) {
		for (ToolCore tool : TinkerRegistry.getTools()) {
			if (tool.getRegistryName().equals(res)) {
				return tool;
			}
		}
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

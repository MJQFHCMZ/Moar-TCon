package com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.material;

import com.teamacronymcoders.contenttweaker.modules.tinkers.materials.CoTTConMaterialBuilder;

import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenMethod;

//@ZenClass("mods.moretcon.UniqueMaterialBuilder")
//@ZenRegister
//@ModOnly("contenttweaker")
public class CoTTConUniqueMaterialBuilder extends CoTTConMaterialBuilder {

	protected final String toolpart;
	protected final String tool;
	protected final String station;
	protected final String stationDesc;

	public CoTTConUniqueMaterialBuilder(String identifier, String toolpart, String tool, String station, String stationDesc) {
		super(identifier);
		this.toolpart = new ResourceLocation(toolpart).toString();
		this.tool = new ResourceLocation(tool).toString();
		if (station == null) {
			this.station = "minecraft:crafting_table";
		} else {
			this.station = new ResourceLocation(station).toString();
		}
		if (stationDesc == null) {
			this.stationDesc = "crafting_table";
		} else {
			this.stationDesc = stationDesc;
		}
	}

	@ZenMethod
	public String getToolpart() {
		return toolpart;
	}

	@ZenMethod
	public String getTool() {
		return tool;
	}

	@ZenMethod
	public String getStation() {
		return station;
	}

	@ZenMethod
	public String getStationDesc() {
		return stationDesc;
	}

	// we do a small edit in the register method. using mixins. as its easier. lol.
}

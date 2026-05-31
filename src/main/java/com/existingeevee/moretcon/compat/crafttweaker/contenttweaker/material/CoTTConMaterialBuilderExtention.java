package com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.material;

import com.teamacronymcoders.contenttweaker.modules.tinkers.materials.CoTTConMaterialBuilder;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;

@ZenExpansion("mods.contenttweaker.tconstruct.MaterialBuilder")
@ZenRegister
public class CoTTConMaterialBuilderExtention {

	@ZenMethodStatic
	public static CoTTConMaterialBuilder createUnique(String identifier, String tool, String toolpart, @Optional String station, @Optional String stationDesc) {
		return new CoTTConUniqueMaterialBuilder(identifier, tool, toolpart, station, stationDesc);
	}
}


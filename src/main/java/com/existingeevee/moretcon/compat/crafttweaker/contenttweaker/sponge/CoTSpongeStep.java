package com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.sponge;

import com.existingeevee.moretcon.other.sponge.SpongeRegistry.SpongeStep;

import crafttweaker.annotations.ZenRegister;
import net.minecraftforge.fluids.FluidRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.moretcon.sponge.SpongeStep")
public class CoTSpongeStep extends SpongeStep {

	@ZenMethod
	public static CoTSpongeStep create(String fluid, int amount) {
		return new CoTSpongeStep(fluid, amount);
	}

	public CoTSpongeStep(String fluid, int amount) {
		super(() -> FluidRegistry.getFluid(fluid), amount);
	}

}

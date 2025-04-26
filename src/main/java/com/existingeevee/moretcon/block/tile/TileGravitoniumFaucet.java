package com.existingeevee.moretcon.block.tile;

import java.util.Optional;

import com.existingeevee.moretcon.other.utils.MirrorUtils;
import com.existingeevee.moretcon.other.utils.MirrorUtils.IField;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.smeltery.tileentity.TileFaucet;

public class TileGravitoniumFaucet extends TileFaucet {
	
	protected static final Optional<IField<Integer>> LIQUID_TRANSFER$TileFaucet = MirrorUtils.reflectFieldOptional(TileFaucet.class, "LIQUID_TRANSFER");
	protected static final Optional<IField<Integer>> LIQUID_TRANSFER$Config = MirrorUtils.reflectFieldOptional(Config.class, "liquidTransferRate");
	
	@Override
	protected void pour() {
		if (drained == null) {
			return;
		}

		IFluidHandler toFill = getFluidHandler(pos.down(), EnumFacing.UP);
		if (toFill != null) {
			FluidStack fillStack = drained.copy();
			
			int normalDrainAmount = 6; //set this to the default val.
			if (LIQUID_TRANSFER$TileFaucet.isPresent()) {
				normalDrainAmount = LIQUID_TRANSFER$TileFaucet.get().get(null);
			} else if (LIQUID_TRANSFER$Config.isPresent()){
				normalDrainAmount = LIQUID_TRANSFER$Config.get().get(null);
			}
			
			fillStack.amount = Math.min(drained.amount, normalDrainAmount * 6);

			// can we fill?
			int filled = toFill.fill(fillStack, false);
			if (filled > 0) {
				// transfer it
				this.drained.amount -= filled;
				fillStack.amount = filled;
				toFill.fill(fillStack, true);
			}
		} else {
			// filling TE got lost. reset. all liquid buffered is lost.
			reset();
		}
	}
}
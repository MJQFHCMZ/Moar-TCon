package com.existingeevee.moretcon.mixin.late.common;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.existingeevee.moretcon.block.tile.TileCatalyzationChamber;
import com.existingeevee.moretcon.item.ItemCatalyst;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.library.utils.FluidUtil;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

@Mixin(TileSmeltery.class)
public class MixinTileSmeltery extends TileEntity {

	@Shadow(remap = false)
	@Final
	protected static int ALLOYING_PER_TICK;
	
	@Shadow(remap = false)
	protected SmelteryTank liquids;

	@Shadow(remap = false)
	@Final
	static Logger log;

	
	@Inject(at = @At(value = "TAIL"), method = "alloyAlloys", remap = false)
	void moretcon$TAIL_Inject$alloyAlloys(CallbackInfo ci) {
		TileEntity tileBelow = world.getTileEntity(pos.down());
		if (tileBelow instanceof TileCatalyzationChamber) {
			TileCatalyzationChamber cata = (TileCatalyzationChamber) tileBelow;

			Set<ItemCatalyst> finishedProcessingCatalysts = new HashSet<>();
			for (int i = 0; i < cata.inventory.getSlots(); i++) {
				ItemStack stack = cata.inventory.getStackInSlot(i);
				if (stack.getItem() instanceof ItemCatalyst) {
					finishedProcessingCatalysts.add((ItemCatalyst) stack.getItem());
					//below is mostly copied from base tinkers lol
					for (AlloyRecipe recipe : ((ItemCatalyst) stack.getItem()).getCatalyzedAlloys()) {
						if (!recipe.isValid()) {
							continue;
						}
						// find out how often we can apply the recipe
						int matched = recipe.matches(liquids.getFluids());
						if (matched > ALLOYING_PER_TICK) {
							matched = ALLOYING_PER_TICK;
						}
						while (matched > 0) {
							// remove all liquids from the tank
							for (FluidStack liquid : recipe.getFluids()) {
								FluidStack toDrain = liquid.copy();
								FluidStack drained = liquids.drain(toDrain, true);
								// error logging
								assert drained != null;
								if (!drained.isFluidEqual(toDrain) || drained.amount != toDrain.amount) {
									log.error("Catalyzed smeltery alloy creation drained incorrect amount: was {}:{}, should be {}:{}", drained
											.getUnlocalizedName(), drained.amount, toDrain.getUnlocalizedName(), toDrain.amount);
								}
							}

							// and insert the alloy
							FluidStack toFill = FluidUtil.getValidFluidStackOrNull(recipe.getResult().copy());
							int filled = liquids.fill(toFill, true);
							if (filled != recipe.getResult().amount) {
								log.error("Catalyzed smeltery alloy creation filled incorrect amount: was {}, should be {} ({})", filled,
										recipe.getResult().amount * matched, recipe.getResult().getUnlocalizedName() + " of " + stack.getItem().getRegistryName());
								break;
							}
							matched -= filled;
						}
					}
				}
			}
		}
	}

}

package com.existingeevee.moretcon.mixin.softdep.thebetweenlands;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.existingeevee.moretcon.traits.ModTraits;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import slimeknights.tconstruct.library.tools.ToolCore;
import thebetweenlands.common.item.tools.ItemGreataxe;

//This is a hack to allow tool to break effective blocks

@Mixin(ItemGreataxe.class)
public class MixinItemGreataxe {

	@Unique
	private final static int CUTOFF = 115; //fuck this is janky

	@ModifyVariable(method = { "onUpdate", "func_77663_a" }, at = @At("STORE"), ordinal = 0, remap = false)
	public IBlockState moretcon$STORE_ModifyVariable$onUpdate(IBlockState state, @Local ItemStack stack, @Local EntityPlayerMP player, @Local BlockPos pos, @Local List<BlockPos> toCheck) {

		if (stack != null && stack.getItem() instanceof ToolCore) {
			if (Thread.currentThread().getStackTrace()[2].getLineNumber() > CUTOFF) {
				return state;
			}

			ToolCore core = (ToolCore) stack.getItem();
			boolean canHarvest = ModTraits.inertia.canSweepBreak(stack, core, state, player, pos);
			if (canHarvest) {
				toCheck.add(pos);
			}
			return Blocks.BARRIER.getDefaultState(); //we handle these seperately regardless
		}
		return state;
	}

}

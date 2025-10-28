package com.existingeevee.moretcon.traits.traits;

import com.existingeevee.moretcon.config.ConfigHandler;
import com.existingeevee.moretcon.inits.ModBlocks;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class BottomsEnd extends AbstractTrait {
	public BottomsEnd() {
		super(MiscUtils.createNonConflictiveName("bottomsend"), 0xffffff);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void bedrockHarvestEvent(BlockEvent.HarvestDropsEvent event) {
		IBlockState state = event.getState();

		if (event.getHarvester() != null) {

			ItemStack tool = event.getHarvester().getHeldItemMainhand();
			int harvestLevel = tool.getItem().getHarvestLevel(tool, "pickaxe", event.getHarvester(), state);
			boolean silkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0;

			if (!this.isToolWithTrait(tool) || ToolHelper.isBroken(tool)) {
				return;
			}

			if (state.getBlock() == Blocks.BEDROCK) {
				if (harvestLevel < 4) {
					event.getDrops().clear();
				}

				if (!silkTouch || !ConfigHandler.unfracturedBedrockObtainable) {
					event.getDrops().clear();
					event.getDrops().add(new ItemStack(ModBlocks.blockCobbledBedrock));
				}
			} else if (state.getBlock().getRegistryName().toString().equals("thebetweenlands:betweenlands_bedrock")) {
				if (harvestLevel < 4) {
					event.getDrops().clear();
				}

				if (!silkTouch || !ConfigHandler.unfracturedBedrockObtainable) {
					event.getDrops().clear();
					event.getDrops().add(new ItemStack(ModBlocks.blockCobbledBetweenBedrock));
				}
			}
		}
	}

	@Override
	public boolean isToolWithTrait(ItemStack is) {
		return super.isToolWithTrait(is);
	}
}

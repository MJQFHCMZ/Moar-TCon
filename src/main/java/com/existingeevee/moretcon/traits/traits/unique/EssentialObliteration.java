package com.existingeevee.moretcon.traits.traits.unique;

import java.util.UUID;

import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.other.utils.ReequipHack;
import com.existingeevee.moretcon.traits.traits.abst.NumberTrackerTrait;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.gadgets.Exploder;
import slimeknights.tconstruct.gadgets.entity.ExplosionEFLN;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class EssentialObliteration extends NumberTrackerTrait {

	public EssentialObliteration() {
		super(MiscUtils.createNonConflictiveName("essential_obliteration"), 0);
		ReequipHack.registerIgnoredKey(this.getModifierIdentifier());
	}

	@Override
	public int getNumberMax(ItemStack stack) {
		return 60;
	}

	@Override
	public boolean isToolWithTrait(ItemStack itemStack) {
		return super.isToolWithTrait(itemStack);
	}

	@Override
	public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
		if (event.getEntityLiving().isSneaking()) {
			event.setNewSpeed(event.getNewSpeed() * 0.0625f);
		}
	}

	@Override
	public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
		if (player.isSneaking() && this.getNumber(tool) >= 10) {
			if (player instanceof EntityPlayer && ((EntityPlayer) player).getCooldownTracker().hasCooldown(tool.getItem()))
				return;
			
			double hardness = state.getBlockHardness(world, pos);
			
			if (hardness < 0.2)
				return;
			
			if (!world.isRemote) {
				float str = (float) Math.sqrt(2 * ToolHelper.getActualMiningSpeed(tool));
				ExplosionEFLN explosion = new ExplosionEFLN(world, player, pos.getX(), pos.getY(), pos.getZ(), str, false, false);
				if (!ForgeEventFactory.onExplosionStart(world, explosion)) {
					Exploder.startExplosion(world, explosion, player, pos, str, str);
					this.removeNumber(tool, 10);
					if (player instanceof EntityPlayer)
						((EntityPlayer) player).getCooldownTracker().setCooldown(tool.getItem(), 100);;

				}
			}
		}
	}

	@Override
	public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (!world.isRemote && (entity.getUniqueID().getLeastSignificantBits() + world.getWorldTime()) % 100 == 0) {
			this.addNumber(tool, 1);
		}
	}

	protected static final AttributeModifier ESSENCORE_MINING_REACH = new AttributeModifier(UUID.fromString("AB3F34D3-645C-4F38-A497-9C13A34DB5CF"), "reach modifier", 20, 0);

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent e) {
		if ((e.getEntityLiving() instanceof EntityPlayer)) {
			ItemStack stack = e.getEntityLiving().getHeldItemMainhand();
			boolean cooldown = ((EntityPlayer) e.getEntityLiving()).getCooldownTracker().hasCooldown(stack.getItem());
			if (this.isToolWithTrait(stack) && e.getEntityLiving().isSneaking() && this.getNumber(stack) >= 10 && !ToolHelper.isBroken(stack) && !cooldown) {
				if (!e.getEntityLiving().getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE).hasModifier(ESSENCORE_MINING_REACH)) {
					e.getEntityLiving().getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE).applyModifier(ESSENCORE_MINING_REACH);
				}
			} else {
				if (e.getEntityLiving().getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE).hasModifier(ESSENCORE_MINING_REACH)) {
					e.getEntityLiving().getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE).removeModifier(ESSENCORE_MINING_REACH);
				}
			}
		}
	}
}

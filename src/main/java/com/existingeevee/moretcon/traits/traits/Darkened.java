package com.existingeevee.moretcon.traits.traits;

import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.traits.traits.abst.ISimpleArmorTrait;

import c4.conarm.lib.armor.ArmorModifications;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Optional;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

@Optional.Interface(iface = "com.existingeevee.moretcon.traits.traits.abst.ISimpleArmorTrait", modid = "conarm")
public class Darkened extends AbstractTrait implements ISimpleArmorTrait {

	public Darkened() {
		super(MiscUtils.createNonConflictiveName("darkened"), 0);
	}

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		float multiplier = (16f - player.world.getLight(player.getPosition())) / 16f;
		float multExtraDmg = newDamage * multiplier;
		float addExtraDmg = 8f - player.world.getLight(player.getPosition()) / 2f;
		return newDamage + Math.max(multExtraDmg, addExtraDmg);
	}

	@Override
	public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
		if (ToolHelper.isToolEffective2(tool, event.getState())) {
			float multiplier = 2f - event.getEntity().world.getLight(event.getEntity().getPosition()) / 16f;
			event.setNewSpeed(event.getNewSpeed() * multiplier);
		}
	}

	@Override
	@Optional.Method(modid = "conarm")
	public ArmorModifications getModifications(EntityPlayer player, ArmorModifications mods, ItemStack armor, DamageSource source, double damage, int slot) {
		float light = player.world.getLight(player.getPosition()) / 16f;

		mods.addArmor(4 - 0.25f * light);
		if (light <= 6. / 16) {
			float lightScaled = light / (6.f / 16);
			mods.addToughness(3 - 0.3333f * lightScaled);
		}

		return mods;
	}
}

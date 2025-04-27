package com.existingeevee.moretcon.traits.modifiers;

import java.text.DecimalFormat;
import java.util.List;

import com.existingeevee.moretcon.inits.ModItems;
import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class Crushing extends ModifierTrait {

	public Crushing() {
		super(MiscUtils.createNonConflictiveName("modcrushing"), 0x555555, 3, 3);
		this.addItem(ModItems.crushingModifier);
	}

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		float actualDMG = newDamage;
		if (player instanceof EntityPlayer) {
			actualDMG *= ((EntityPlayer) player).getCooledAttackStrength(0.5f);
		}

		target.getEntityData().setFloat(getModifierIdentifier(), actualDMG);

		return newDamage * (1 - getPercentage(TinkerUtil.getModifierTag(tool, getModifierIdentifier())));
	}

	@Override
	public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
		if (wasHit) {
			float multiplier = getPercentage(TinkerUtil.getModifierTag(tool, getModifierIdentifier()));
			float origDamage = target.getEntityData().getFloat(getModifierIdentifier());
			DamageSource source = player instanceof EntityPlayer ? DamageSource.causePlayerDamage((EntityPlayer) player) : DamageSource.causeMobDamage(player);
			MiscUtils.trueDamage(target, origDamage * multiplier, source, false);
		}
	}

	public static float getPercentage(NBTTagCompound tag) {
		ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(tag);
		int level = data.current / 3;
		return 0.01f * data.current + 0.01f * level / 3;
	}

	@Override
	public int getPriority() {
		return Integer.MAX_VALUE / 2; // we need this to run last. divide by 2 to prevent wierd overflow issues
	}

	public static final DecimalFormat format = new DecimalFormat("#.##%");

	@Override
	public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
		String loc = String.format(LOC_Extra, getModifierIdentifier());
		float amount = getPercentage(modifierTag);
		return ImmutableList.of(Util.translateFormatted(loc, format.format(amount)));
	}
}

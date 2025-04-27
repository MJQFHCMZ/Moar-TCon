package com.existingeevee.moretcon.traits.modifiers;

import java.text.DecimalFormat;
import java.util.List;

import com.existingeevee.moretcon.inits.ModItems;
import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class Autocrit extends ModifierTrait {

	public Autocrit() {
		super(MiscUtils.createNonConflictiveName("modautocrit"), 0xffbc00, 4, 4);
		this.addItem(ModItems.autocritModifier);
	}

	public static float getPercentage(NBTTagCompound tag) {
		ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(tag);
		int level = data.current / 4;
		return 0.05f * data.current + 0.2f * level / 4;
	}

	public static final DecimalFormat format = new DecimalFormat("%");

	@Override
	public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
		String loc = String.format(LOC_Extra, getModifierIdentifier());
		float amount = getPercentage(modifierTag);
		return ImmutableList.of(Util.translateFormatted(loc, format.format(amount)));
	}

	@Override
	public boolean isCriticalHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target) {
		if (!player.world.isRemote) {
			float chance = getPercentage(TinkerUtil.getModifierTag(tool, getModifierIdentifier()));
			return Math.random() < chance;
		}
		return false;
	}
}

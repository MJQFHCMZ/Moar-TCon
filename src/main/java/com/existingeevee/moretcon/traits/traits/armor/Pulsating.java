package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class Pulsating extends AbstractArmorTrait {

	public Pulsating() {
		super(MiscUtils.createNonConflictiveName("pulsating"), TextFormatting.WHITE);
	}

	@Override
	public float onDamaged(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingDamageEvent evt) {
		NBTTagCompound comp = armor.getOrCreateSubCompound(this.getModifierIdentifier());

		float x = comp.getFloat("x");
		float ed = (Math.round(25 * Math.sin(x += 0.2)) + 25) / 100;
		comp.setFloat("x", Math.round(x * 10f) / 10f);

		return newDamage - ed;		
	}
}
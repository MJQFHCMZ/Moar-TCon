package com.existingeevee.moretcon.traits.traits.abst;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "com.existingeevee.moretcon.traits.traits.abst.ISimpleArmorTrait", modid = "conarm")
public abstract class DurabilityShieldTrait extends NumberTrackerTrait implements ISimpleArmorTrait {

	public DurabilityShieldTrait(String identifier, int color) {
		super(identifier, color);
	}

	@Override
	public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
		int damageNegated = Math.min(getNumber(tool), newDamage);
		removeNumber(tool, damageNegated);
		return newDamage - damageNegated;
	}
	
	@Override
	public int getPriority() {
		return /*its over*/ 9000; //!!!!!
	}

	@Override
	@Optional.Method(modid = "conarm")
	public int onArmorDamage(ItemStack armor, DamageSource source, int damage, int newDamage, EntityPlayer player, int slot) {
		int damageNegated = Math.min(getNumber(armor), newDamage);
		removeNumber(armor, damageNegated);
		return newDamage - damageNegated;
	}
}

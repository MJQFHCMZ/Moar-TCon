package com.existingeevee.moretcon.traits.traits.abst;

import c4.conarm.lib.armor.ArmorModifications;
import c4.conarm.lib.traits.IArmorTrait;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Optional.Interface;

@Interface(iface = "c4.conarm.lib.traits.IArmorTrait", modid = "conarm")
public abstract class DurabilityShieldTrait extends NumberTrackerTrait implements IArmorTrait {

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
	public ArmorModifications getModifications(EntityPlayer player, ArmorModifications mods, ItemStack armor, DamageSource source, double damage, int slot) {
		return mods;
	}

	@Override
	public void onItemPickup(ItemStack armor, EntityItem item, EntityItemPickupEvent evt) {
		
	}

	@Override
	public float onHeal(ItemStack armor, EntityPlayer player, float amount, float newAmount, LivingHealEvent evt) {
		return newAmount;
	}

	@Override
	public float onHurt(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingHurtEvent evt) {
		return newDamage;
	}

	@Override
	public float onDamaged(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingDamageEvent evt) {
		return newDamage;
	}

	@Override
	public void onKnockback(ItemStack armor, EntityPlayer player, LivingKnockBackEvent evt) {		
	}

	@Override
	public void onFalling(ItemStack armor, EntityPlayer player, LivingFallEvent evt) {		
	}

	@Override
	public void onJumping(ItemStack armor, EntityPlayer player, LivingJumpEvent evt) {		
	}

	@Override
	public void onAbilityTick(int level, World world, EntityPlayer player) {		
	}

	@Override
	public void onArmorEquipped(ItemStack armor, EntityPlayer player, int slot) {		
	}

	@Override
	public void onArmorRemoved(ItemStack armor, EntityPlayer player, int slot) {		
	}

	@Override
	public int onArmorDamage(ItemStack armor, DamageSource source, int damage, int newDamage, EntityPlayer player, int slot) {
		int damageNegated = Math.min(getNumber(armor), newDamage);
		removeNumber(armor, damageNegated);
		return newDamage - damageNegated;
	}

	@Override
	public int onArmorHeal(ItemStack armor, DamageSource source, int amount, int newAmount, EntityPlayer player, int slot) {
		return newAmount;
	}

	@Override
	public boolean disableRendering(ItemStack armor, EntityLivingBase entityLivingBase) {
		return false;
	}
}

package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.lib.armor.ArmorModifications;
import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class Unmeltable extends AbstractArmorTrait {

    public Unmeltable() {
        super(MiscUtils.createNonConflictiveName("unmeltable"), TextFormatting.WHITE);
    }
    
    @Override
    public ArmorModifications getModifications(EntityPlayer player, ArmorModifications mods, ItemStack armor, DamageSource source, double damage, int slot) {
    	mods.effective += 0.75f;
        return mods;
    }

    @Override
    public void onAbilityTick(int level, World world, EntityPlayer player) {    	
    	if (!player.isBurning() && !world.isRemote) {
    		player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 10 * level + 1, 0));
    	}
    }
}
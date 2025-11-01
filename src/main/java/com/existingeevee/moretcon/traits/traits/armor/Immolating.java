package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.lib.armor.ArmorModifications;
import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class Immolating extends AbstractArmorTrait {

    public Immolating() {
        super(MiscUtils.createNonConflictiveName("immolating"), TextFormatting.WHITE);
    }
    
    @Override
    public ArmorModifications getModifications(EntityPlayer player, ArmorModifications mods, ItemStack armor, DamageSource source, double damage, int slot) {
    	mods.effective += 0.75f;
        return mods;
    }

    @Override
    public void onAbilityTick(int level, World world, EntityPlayer player) {
    	long tick = player.getUniqueID().getLeastSignificantBits() + world.getTotalWorldTime();
    	
    	if (tick % 100 == 0) {
    		if (!player.isImmuneToFire() && !player.isInWater()) {
                player.attackEntityFrom(DamageSource.LAVA, 4.0F);
            }
            player.setFire(15);
    	}
    }
    
    @Override
    public void onArmorEquipped(ItemStack armor, EntityPlayer player, int slot) {
		if (!player.isImmuneToFire() && !player.isInWater()) {
            player.attackEntityFrom(DamageSource.LAVA, 4.0F);
        }
        player.setFire(15);
	}
}
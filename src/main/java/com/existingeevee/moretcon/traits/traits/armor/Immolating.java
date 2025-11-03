package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.common.armor.utils.ArmorTagUtil;
import c4.conarm.lib.armor.ArmorNBT;
import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class Immolating extends AbstractArmorTrait {

    public Immolating() {
        super(MiscUtils.createNonConflictiveName("immolating"), TextFormatting.WHITE);
    }
    
    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        if(!TinkerUtil.hasTrait(rootCompound, identifier)) {
            ArmorNBT data = ArmorTagUtil.getArmorStats(rootCompound);
            ArmorNBT original = ArmorTagUtil.getOriginalArmorStats(rootCompound);
            data.defense += original.defense * 0.75;
            data.toughness += original.toughness * 0.75;
            TagUtil.setToolTag(rootCompound, data.get());
        }
        super.applyEffect(rootCompound, modifierTag);
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
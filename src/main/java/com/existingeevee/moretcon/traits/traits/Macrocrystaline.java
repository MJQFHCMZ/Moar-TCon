package com.existingeevee.moretcon.traits.traits;

import java.util.List;

import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.traits.traits.abst.ISimpleArmorTrait;
import com.google.common.collect.ImmutableList;

import c4.conarm.lib.armor.ArmorModifications;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Optional;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

@Optional.Interface(iface = "com.existingeevee.moretcon.traits.traits.abst.ISimpleArmorTrait", modid = "conarm")
public class Macrocrystaline extends AbstractTrait implements ISimpleArmorTrait {

	public Macrocrystaline() {
		super(MiscUtils.createNonConflictiveName("macrocrystaline"), 0xffffff);
	}

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		return newDamage + damage * getPerfection(tool) * 0.05f;
	}

	@Override
	public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
		event.setNewSpeed(event.getNewSpeed() + event.getOriginalSpeed() * getPerfection(tool) * 0.2f);
	} 
	
	@Override
	@Optional.Method(modid = "conarm")
	public ArmorModifications getModifications(EntityPlayer player, ArmorModifications mods, ItemStack armor, DamageSource source, double damage, int slot) {
		mods.addEffectiveness(getPerfection(armor) * 0.05f);
		return mods;
	}

    @Override
    public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
        String loc = String.format(LOC_Extra, getModifierIdentifier());
        int perfection = getPerfection(tool);
        return ImmutableList.of(Util.translateFormatted(loc, "2^" + perfection));
    }
	
	private int getPerfection(ItemStack tool) {
		int i = 0;				
		while (Math.pow(2, i) <= ToolHelper.getCurrentDurability(tool)) { // the + 0.5 is just used for rounding lul
			i++;
		}

		return i - 1;
	}
}

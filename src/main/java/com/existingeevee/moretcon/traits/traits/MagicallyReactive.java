package com.existingeevee.moretcon.traits.traits;

import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.traits.traits.abst.ISimpleArmorTrait;

import c4.conarm.lib.armor.ArmorModifications;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Optional;
import slimeknights.tconstruct.library.traits.AbstractTrait;

@Optional.Interface(iface = "com.existingeevee.moretcon.traits.traits.abst.ISimpleArmorTrait", modid = "conarm")
public class MagicallyReactive extends AbstractTrait implements ISimpleArmorTrait {

	public MagicallyReactive() {
		super(MiscUtils.createNonConflictiveName("magically_reactive"), 0x9d763f);
	}

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		int amount = 0;

		for (ItemStack stack : target.getEquipmentAndArmor()) {
			amount += stack.isItemEnchanted() ? 1 : 0;
		}

		return newDamage * (1 + 0.5f * amount);
	}
	
	@Override
	@Optional.Method(modid = "conarm")
	public ArmorModifications getModifications(EntityPlayer player, ArmorModifications mods, ItemStack armor, DamageSource source, double damage, int slot) {
		int amount = source.isMagicDamage() ? 2 : 0;
		
		if (source.getTrueSource() instanceof EntityLivingBase)
			for (ItemStack stack : ((EntityLivingBase) source.getTrueSource()).getEquipmentAndArmor()) {
				amount += stack.isItemEnchanted() ? 1 : 0;
			}
		
		mods.addArmor(amount);
		if (amount > 2) {
			mods.addToughness(0.5f * (amount - 2));
		}
		
		return mods;
	}
}

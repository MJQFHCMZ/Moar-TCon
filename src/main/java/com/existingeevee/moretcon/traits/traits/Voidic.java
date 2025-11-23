package com.existingeevee.moretcon.traits.traits;

import com.existingeevee.moretcon.other.utils.CompatManager;
import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.traits.traits.abst.ISimpleArmorTrait;

import c4.conarm.common.armor.utils.ArmorHelper;
import c4.conarm.lib.armor.ArmorModifications;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

@Optional.Interface(iface = "com.existingeevee.moretcon.traits.traits.abst.ISimpleArmorTrait", modid = "conarm")
public class Voidic extends AbstractTrait implements ISimpleArmorTrait {

	public Voidic() {
		super(MiscUtils.createNonConflictiveName("voidic"), 0);
	}

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		if (player.getPosition().getY() < 7) {
			newDamage += damage * 0.25;
		}
		return super.damage(tool, player, target, damage, newDamage, isCritical);
	}

	@Override
	public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (Math.random() < 0.015 && !world.isRemote && entity instanceof EntityLivingBase) {
			if (entity.getPosition().getY() < 4) {
				if (entity instanceof EntityPlayer) {
					boolean worn = false;

					if (CompatManager.conarm && itemSlot < 4)
						for (ItemStack armor : ((EntityLivingBase) entity).getArmorInventoryList()) {
							worn = tool == armor;
							if (worn)
								break;
						}

					if (worn) {
						ArmorHelper.healArmor(tool, 1, (EntityPlayer) entity, EntityLiving.getSlotForItemStack(tool).getIndex());
					} else {
						ToolHelper.healTool(tool, 1, (EntityLivingBase) entity);
					}
				}
			}
		}
		super.onUpdate(tool, world, entity, itemSlot, isSelected);
	}

	@Override
	@Optional.Method(modid = "conarm")
	public ArmorModifications getModifications(EntityPlayer player, ArmorModifications mods, ItemStack armor, DamageSource source, double damage, int slot) {
		if (player.getPosition().getY() < 7) {
			mods.addArmor(4);
		}
		if (player.getPosition().getY() < 4) {
			mods.addArmor(3);
			mods.addToughness(2);
		}

		return mods;
	}
}

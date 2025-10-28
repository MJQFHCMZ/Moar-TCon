package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.common.armor.utils.ArmorHelper;
import c4.conarm.lib.armor.ArmorModifications;
import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class Liquid extends AbstractArmorTrait {

	public Liquid() {
		super(MiscUtils.createNonConflictiveName("liquid"), TextFormatting.WHITE);
	}

	@Override
	public ArmorModifications getModifications(EntityPlayer player, ArmorModifications mods, ItemStack armor, DamageSource source, double damage, int slot) {
		double temperature = getTemp(player.world, player.getPosition());
		if (temperature <= 0.5) {
			mods.addArmor(4 * (1 - 2 * (float) temperature));
			if (temperature <= 0.25) {
				mods.addToughness(2 * (1 - (float) temperature * 4));
			}
		}

		return mods;
	}

	public static double getTemp(World world, BlockPos pos) {
		double temperature = world.getBiome(pos).getTemperature(pos);
		if (world.getBiome(pos).getEnableSnow() && world.isRainingAt(pos)) {
			temperature -= 1;
		}
		return temperature;
	}

	@Override
	public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
		double temp = getTemp(world, entity.getPosition());

		if (entity instanceof EntityPlayer) {
			boolean worn = false;

			if (itemSlot >= 4)
				return; //it is not being worn
			
			for (ItemStack armor : ((EntityLivingBase) entity).getArmorInventoryList()) {
				worn = tool == armor;
				if (worn)
					break;
			}
			
			if (!world.isRemote && worn && temp > 0.8) {
				double hotness = (temp - 0.8) / 1.2 / 40;
				if (random.nextDouble() < hotness)
					ArmorHelper.damageArmor(tool, DamageSource.IN_FIRE, 1, (EntityPlayer) entity);
			}
		}
	}
}
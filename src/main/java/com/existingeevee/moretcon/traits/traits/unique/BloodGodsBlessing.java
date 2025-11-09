package com.existingeevee.moretcon.traits.traits.unique;

import java.util.List;

import com.existingeevee.moretcon.inits.ModPotions;
import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.traits.traits.abst.IAdditionalTraitMethods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import slimeknights.tconstruct.library.client.CustomFontColor;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class BloodGodsBlessing extends AbstractTrait implements IAdditionalTraitMethods {

	public BloodGodsBlessing() {
		super(MiscUtils.createNonConflictiveName("bloodgodsblessing"), 0);
	}

	@Override
	public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
		if (!wasHit) {
			return;
		}

		int lvl = -1;

		if (player.isPotionActive(ModPotions.bloodgodsblessing)) {
			lvl = player.getActivePotionEffect(ModPotions.bloodgodsblessing).getAmplifier();
			player.removeActivePotionEffect(ModPotions.bloodgodsblessing);
		}

		player.addPotionEffect(new PotionEffect(ModPotions.bloodgodsblessing, 5 * 20, lvl + 1, false, false));
	}

	@Override
	public void modifyTooltip(ItemStack tool, ItemTooltipEvent event) {

		List<String> list = event.getToolTip();

		list.add("");
		list.add(CustomFontColor.encodeColor(0xea8f8c) + "He may be gone, but he will always live in our hearts."); 
		list.add(CustomFontColor.encodeColor(0xea8f8c) + "RIP Technoblade (1999-2022)");
		list.add("");
	}
}

//A1D6A357A09288ED92D8018E6586A87BAAF66B6B56719473A5BA2482376DF90F191BB4F72ADF119557A4A7936B08624B2E7032107924FC2CEBD6BA8E92F06B2A3C88C38A08615535383E0C0EE5E4FA1715F9BCB104DE8635BD8EE518EC98CF52CC1A89D464FEA8D702E3CB2E9073776C17F85D2422075722C9891F86BFE79167
//AES1290381287201735
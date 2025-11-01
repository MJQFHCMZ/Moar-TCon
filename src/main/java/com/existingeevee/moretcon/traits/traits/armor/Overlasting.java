package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.common.armor.utils.ArmorHelper;
import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Overlasting extends AbstractArmorTrait {

	public Overlasting() {
		super(MiscUtils.createNonConflictiveName("overlasting"), TextFormatting.WHITE);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingHurtEvent(LivingHurtEvent event) {
		if (event.getSource().getDamageType().equals("echo") || event.getSource() == DamageSource.OUT_OF_WORLD || event.getSource().isDamageAbsolute() || !(event.getEntity() instanceof EntityPlayer))
			return;

		int level = (int) Math.round(ArmorHelper.getArmorAbilityLevel((EntityPlayer) event.getEntityLiving(), this.identifier));

		if (level <= 0)
			return;
		
		event.getEntityLiving().hurtResistantTime += Math.round(5 * level);
	}
}
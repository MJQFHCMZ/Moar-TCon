package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.common.armor.utils.ArmorHelper;
import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sapping extends AbstractArmorTrait {

	public Sapping() {
		super(MiscUtils.createNonConflictiveName("sapping"), TextFormatting.WHITE);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingHurtEvent(LivingDamageEvent event) {
		for (Entity e : event.getEntity().getEntityWorld().getEntitiesInAABBexcluding(event.getEntity(), event.getEntity().getEntityBoundingBox().grow(2), e -> e instanceof EntityPlayer)) {
			EntityPlayer player = (EntityPlayer) e;
			double level = ArmorHelper.getArmorAbilityLevel(player, identifier);
			if (level > 0.01) {
				float healAmnt = Math.min(8, Math.max(Math.min(2, event.getAmount() * 0.25f), (float) (level * 0.125f * event.getAmount())));
				player.heal(healAmnt);
			}
		}
	}
}
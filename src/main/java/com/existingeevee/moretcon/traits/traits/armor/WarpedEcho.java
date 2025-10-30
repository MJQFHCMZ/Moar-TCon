package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.common.armor.utils.ArmorHelper;
import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WarpedEcho extends AbstractArmorTrait {

	public WarpedEcho() {
		super(MiscUtils.createNonConflictiveName("warped_echo"), TextFormatting.WHITE);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static final DamageSource ECHO = new DamageSource("echo");

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingHurtEvent(LivingHurtEvent event) {
		if (event.getSource() == ECHO || event.getSource() == DamageSource.OUT_OF_WORLD || !(event.getEntity() instanceof EntityPlayer))
			return; 
		
		int level = (int) Math.round(ArmorHelper.getArmorAbilityLevel((EntityPlayer) event.getEntityLiving(), this.identifier));
				
		if (level <= 0) 
			return;
		
		int splitAmount = level + 1;
		
		float damageSplit = (float) (event.getAmount() / splitAmount);
		
		for (int i = 1; i <= level + 0.0001; i++) { //i hate doubles:tm:
			MiscUtils.executeInNTicks(() -> {
				if (event.getEntity().world.playerEntities.contains(event.getEntity())) {
					int hrt = event.getEntityLiving().hurtResistantTime;
					event.getEntityLiving().hurtResistantTime = 0;
					event.getEntityLiving().attackEntityFrom(ECHO, damageSplit);
					event.getEntityLiving().hurtResistantTime = hrt;
				}
			}, 20 + 10 * i);
		}
		
		event.setAmount(damageSplit);
	}
	
	@Override
	public float onHurt(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingHurtEvent evt) {				
		return newDamage;
	}

	@Override
	public int getPriority() {
		return 99999;
	}
}
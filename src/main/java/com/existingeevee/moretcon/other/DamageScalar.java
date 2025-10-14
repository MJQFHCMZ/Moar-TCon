package com.existingeevee.moretcon.other;

import com.existingeevee.moretcon.ModInfo;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = ModInfo.MODID)
public class DamageScalar {

	private static final ThreadLocal<Float> SCALAR = ThreadLocal.withInitial(() -> 1f);

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onHurt(LivingHurtEvent event) {
		event.setAmount(event.getAmount() * SCALAR.get());
	}	
	
	public static void set(float val) {
		SCALAR.set(val);
	}
	
	public static void reset() {
		SCALAR.remove();
	}
}

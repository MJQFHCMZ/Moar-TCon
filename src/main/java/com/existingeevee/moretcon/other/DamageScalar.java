package com.existingeevee.moretcon.other;

import java.util.EmptyStackException;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import org.apache.logging.log4j.Level;

import com.existingeevee.moretcon.ModInfo;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

@EventBusSubscriber(modid = ModInfo.MODID)
public class DamageScalar {

	private static Map<Thread, StackTraceElement[]> filled = new WeakHashMap<>();
	private static ThreadLocal<Stack<Float>> stack = ThreadLocal.withInitial(() -> new Stack<>());

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onHurt(LivingHurtEvent event) {
		event.setAmount(event.getAmount() * getMult());
	}
	
	public static float getMult() {
		float mult = 1;
		for (float f : stack.get()) {
			mult *= f;
		}
		return mult;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onTick(ServerTickEvent event) {
		if (event.phase != Phase.START)
			return;

		if (!filled.isEmpty()) {
			//uh oh..
//			int i = 0;
//			MoreTConLogger.log("Damage scalar stack was not cleared last tick!!!", Level.WARN);
//			for (StackTraceElement[] trace : filled.values()) {
//				System.out.println("Potential Culprit #" + ++i + ":");
//				for (StackTraceElement traceElement : trace)
//					System.out.println("\tat " + traceElement);
//			}
			stack = ThreadLocal.withInitial(() -> new Stack<>());
			filled = new WeakHashMap<>();
		}
	}

	public static void push(float val) {
		stack.get().push(val);
		filled.put(Thread.currentThread(), Thread.currentThread().getStackTrace());
	}

	public static void pop() {
		boolean empty;
		try {
			stack.get().pop();
			empty = stack.get().isEmpty();
		} catch (EmptyStackException e) {
			MoreTConLogger.log("Tried to pop the damage scalar while it is empty!! Thread: " + Thread.currentThread().getId(), Level.WARN);
			e.printStackTrace();
			empty = true;
		}

		if (empty) {
			reset();
		}
	}

	public static void reset() {
		stack.remove();
		filled.remove(Thread.currentThread());
	}
}

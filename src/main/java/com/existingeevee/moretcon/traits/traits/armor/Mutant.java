package com.existingeevee.moretcon.traits.traits.armor;

import java.util.Random;
import java.util.UUID;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.utils.ToolHelper;

//mining fatigue -> swing -
//weakness -> weak strength -
//slowness -> speed

// poison/wither -> slow regen 
// nausea/glowing -> swingspeed + str -
// hunger -> strength+ -

//TODO add configurability? (AAAA needs autoconfig)

public class Mutant extends AbstractArmorTrait {

	public Mutant() {
		super(MiscUtils.createNonConflictiveName("mutant"), TextFormatting.WHITE);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void handleDmg(LivingHurtEvent event) {
		EntityLivingBase living = event.getEntityLiving(); 

		if (!(living instanceof EntityPlayer || living.getTags().contains("MutantProc")) && (event.getSource() == DamageSource.WITHER || event.getSource() == DamageSource.MAGIC)) { //Potion
			return;
		}

		boolean equipped = false;
		for (ItemStack stack : living.getArmorInventoryList()) {
			if (this.isToolWithTrait(stack) && !ToolHelper.isBroken(stack)) {
				equipped = true;
				break;
			}
		}
		
		if (equipped && (living.isPotionActive(MobEffects.POISON) || living.isPotionActive(MobEffects.WITHER))) {
			boolean isPoisonOrWither = false;
			for (StackTraceElement elem : Thread.currentThread().getStackTrace()) {
				if (elem.getClassName().equals(Potion.class.getName()) && (elem.getMethodName().equals("performEffect") || elem.getMethodName().equals("func_76394_a"))) {
					isPoisonOrWither = true;
					break;
				}	
			}
			
			if (isPoisonOrWither) {
				event.setCanceled(true);
				event.getEntityLiving().heal(event.getAmount() / 2);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void handleAttr(LivingUpdateEvent event) {
		EntityLivingBase living = event.getEntityLiving();

		if (!(living instanceof EntityPlayer || living.getTags().contains("MutantProc"))) {
			return;
		}

		IAttributeInstance damage = living.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
		IAttributeInstance speed = living.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
		IAttributeInstance swing = living.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);

		boolean equipped = false;
		for (ItemStack stack : living.getArmorInventoryList()) {
			if (this.isToolWithTrait(stack) && !ToolHelper.isBroken(stack)) {
				equipped = true;
				break; 
			}
		}
		
		int miningFatigue = 0;
		int weakness = 0;
		int slowness = 0;
		int hunger = 0;

		boolean nausea = false;
		boolean glowing = false;

		if (equipped) {
			miningFatigue = living.isPotionActive(MobEffects.MINING_FATIGUE) ? living.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier() + 1 : 0; // Potion
			weakness = living.isPotionActive(MobEffects.WEAKNESS) ? living.getActivePotionEffect(MobEffects.WEAKNESS).getAmplifier() + 1 : 0;
			slowness = living.isPotionActive(MobEffects.SLOWNESS) ? living.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier() + 1 : 0;
			hunger = living.isPotionActive(MobEffects.HUNGER) ? living.getActivePotionEffect(MobEffects.HUNGER).getAmplifier() + 1 : 0;
			
			nausea = living.isPotionActive(MobEffects.NAUSEA);
			glowing = living.isPotionActive(MobEffects.GLOWING);
		}


		if (swing != null) {
			UUID uuidMiningFatigue = getUnique(0);
			if (swing.getModifier(uuidMiningFatigue) != null) {
				swing.removeModifier(uuidMiningFatigue);
			}

			if (miningFatigue > 0) {
				//-y = 1 / (1 - 0.1x) - 1

				swing.applyModifier(new AttributeModifier(uuidMiningFatigue, "mining fatigue mutant buff", 1 / (1 - 0.1 * miningFatigue) - 1 + miningFatigue * 1d / 18d, 2));
			}

			UUID uuidNausea = getUnique(1);
			if (nausea) {
				if (swing.getModifier(uuidNausea) == null) {
					swing.applyModifier(new AttributeModifier(uuidNausea, "nausea mutant buff", 0.5, 1));
				}
			} else {
				if (swing.getModifier(uuidNausea) != null) {
					swing.removeModifier(uuidNausea);
				}
			}
			
			UUID uuidGlowing = getUnique(2);
			if (glowing) {
				if (swing.getModifier(uuidGlowing) == null) {
					swing.applyModifier(new AttributeModifier(uuidGlowing, "glow mutant buff", 0.1, 1));
				}
			} else {
				if (swing.getModifier(uuidGlowing) != null) {
					swing.removeModifier(uuidGlowing);
				}
			}
		}
		
		if (speed != null) {
			UUID uuidSlowness = getUnique(0);
			if (speed.getModifier(uuidSlowness) != null) {
				speed.removeModifier(uuidSlowness);
			}
			//Potion
			if (slowness > 0) {
				
				//(1 - 0.15x)(1 - y) = 1
				//1 - y = 1 / (1 - 0.15x)
				//-y = 1 / (1 - 0.15x) - 1
								
				speed.applyModifier(new AttributeModifier(uuidSlowness, "slowness mutant buff", 1 / (1 - 0.15 * slowness) - 1 + 0.0375 * slowness, 2));
			}
		}
		
		if (damage != null) {
			UUID uuidWeakness = getUnique(0);
			if (damage.getModifier(uuidWeakness) != null) {
				damage.removeModifier(uuidWeakness);
			}

			if (weakness > 0) {
				damage.applyModifier(new AttributeModifier(uuidWeakness, "weakness mutant buff", 4 * weakness + weakness, 0));
			}
			
			UUID uuidNausea = getUnique(1);
			if (nausea) {
				if (damage.getModifier(uuidNausea) == null) {
					damage.applyModifier(new AttributeModifier(uuidNausea, "nausea mutant buff", 1, 0));
				}
			} else {
				if (damage.getModifier(uuidNausea) != null) {
					damage.removeModifier(uuidNausea);
				}
			}
			
			UUID uuidGlowing = getUnique(2);
			if (glowing) {
				if (damage.getModifier(uuidGlowing) == null) {
					damage.applyModifier(new AttributeModifier(uuidGlowing, "nausea mutant buff", 3, 0));
				}
			} else {
				if (damage.getModifier(uuidGlowing) != null) {
					damage.removeModifier(uuidGlowing);
				}
			}
			
			UUID uuidHunger = getUnique(3);
			if (damage.getModifier(uuidHunger) != null) {
				damage.removeModifier(uuidHunger);
			}

			if (hunger > 0) {
				damage.applyModifier(new AttributeModifier(uuidHunger, "hunger mutant buff", hunger * 4, 0));
			}
		}
	}

	protected static final UUID getUnique(int type) {
		Random rand = new Random(type * 31);
		return new UUID(rand.nextLong(), rand.nextLong());
	}
}
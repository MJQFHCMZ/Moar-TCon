package com.existingeevee.moretcon.traits.traits;

import java.util.Random;

import com.existingeevee.moretcon.config.ConfigHandler;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class Tricromatic extends AbstractTrait {

	public Tricromatic() {
		super(MiscUtils.createNonConflictiveName("trichromatic"), 0);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private static final ThreadLocal<EntityLivingBase> LAST_PROC = ThreadLocal.withInitial(() -> null);

	@Override
	public boolean isCriticalHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target) {
		LAST_PROC.set(player);
		return false;
	}

	@Override
	public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
		LAST_PROC.set(null);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingDamage(LivingHurtEvent event) {
		if (LAST_PROC.get() != null) {
			int col = calculateChunkColor(LAST_PROC.get());

			if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() == LAST_PROC.get() && col == 0) {
				event.setAmount(event.getAmount() * 1.5f);
			} 
		} else {
			Entity trueSrc = event.getSource().getTrueSource();
			if (trueSrc instanceof EntityLivingBase) {
				EntityLivingBase trueLivingSrc = (EntityLivingBase) trueSrc;

				if (this.isToolWithTrait(trueLivingSrc.getHeldItemMainhand()) || this.isToolWithTrait(trueLivingSrc.getHeldItemOffhand())) {
					int col = calculateChunkColor(trueSrc);
					if (col == 0) event.setAmount(event.getAmount() * 1.5f);
				}
			}
		}

		if (this.isToolWithTrait(event.getEntityLiving().getHeldItemMainhand()) || this.isToolWithTrait(event.getEntityLiving().getHeldItemOffhand())) {
			int col = calculateChunkColor(event.getEntityLiving());
			if (col == 2)
				event.setAmount(event.getAmount() * 0.5f);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingDamage(LivingHealEvent event) {
		if (this.isToolWithTrait(event.getEntityLiving().getHeldItemMainhand()) || this.isToolWithTrait(event.getEntityLiving().getHeldItemOffhand())) {
			int col = calculateChunkColor(event.getEntityLiving());
			if (col == 1)
				event.setAmount(event.getAmount() * 3f);
		}
	}

	@Override
	public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (entity instanceof EntityPlayer && isSelected) {
			int color = calculateChunkColor(entity);
			if (color == 0) {
				red((EntityPlayer) entity);
			} else if (color == 1) {
				green((EntityPlayer) entity);
			} else if (color == 2) {
				blue((EntityPlayer) entity);
			}
		}
	}

	private void red(EntityPlayer entity) {
		if (entity.isSneaking()) {
			entity.sendStatusMessage(new TextComponentString(ChatFormatting.RED + "Red"), true);
		}
		if (entity.world.isRemote) {
			entity.world.spawnParticle(EnumParticleTypes.REDSTONE, true, entity.getPositionVector().x, entity.getPositionVector().y + 0.05, entity.getPositionVector().z, 0, 0, 0);
		}

		Potion effect = Potion.REGISTRY.getObject(ConfigHandler.trichromicRed);

		if (effect != null && !entity.isPotionActive(effect)) {
			entity.addPotionEffect(new PotionEffect(effect, 100, ConfigHandler.trichromicRedLvl - 1, true, true));
		}
	}

	private void green(EntityPlayer entity) {
		if (entity.isSneaking()) {
			entity.sendStatusMessage(new TextComponentString(ChatFormatting.GREEN + "Green"), true);
		}
		if (entity.world.isRemote) {
			entity.world.spawnParticle(EnumParticleTypes.REDSTONE, true, entity.getPositionVector().x, entity.getPositionVector().y + 0.05, entity.getPositionVector().z, -1, 1, 0);
		}

		Potion effect = Potion.REGISTRY.getObject(ConfigHandler.trichromicGreen);

		if (effect != null && !entity.isPotionActive(effect)) {
			entity.addPotionEffect(new PotionEffect(effect, 100, ConfigHandler.trichromicGreenLvl - 1, true, true));
		}
	}

	private void blue(EntityPlayer entity) {
		if (entity.isSneaking()) {
			entity.sendStatusMessage(new TextComponentString(ChatFormatting.BLUE + "Blue"), true);
		}
		if (entity.world.isRemote) {
			entity.world.spawnParticle(EnumParticleTypes.REDSTONE, true, entity.getPositionVector().x, entity.getPositionVector().y + 0.05, entity.getPositionVector().z, -1, 0, 1);
		}

		Potion effect = Potion.REGISTRY.getObject(ConfigHandler.trichromicBlue);

		if (effect != null && !entity.isPotionActive(effect)) {
			entity.addPotionEffect(new PotionEffect(effect, 100, ConfigHandler.trichromicBlueLvl - 1, true, true));
		}
	}

	private int calculateChunkColor(Entity entity) {
		long seed = new Random(Integer.toString(((int) entity.posX) / 16).hashCode() + Integer.toString(((int) entity.posZ) / 16).hashCode()).nextInt(10000);
		float rngNum = new Random(seed).nextFloat();
		if (rngNum < (1f / 3f)) {
			return 0;
		}
		if (rngNum < (2f / 3f)) {
			return 1;
		}
		return 2;
	}
}

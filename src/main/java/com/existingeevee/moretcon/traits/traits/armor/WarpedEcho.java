package com.existingeevee.moretcon.traits.traits.armor;

import javax.annotation.Nullable;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.common.armor.utils.ArmorHelper;
import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WarpedEcho extends AbstractArmorTrait {

	public WarpedEcho() {
		super(MiscUtils.createNonConflictiveName("warped_echo"), TextFormatting.WHITE);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public int onArmorDamage(ItemStack armor, DamageSource source, int damage, int newDamage, EntityPlayer player, int slot) {
		if (source instanceof EchoedDamageSource)
			return 0;
		return newDamage;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingHurtEvent(LivingHurtEvent event) {
		if (event.getSource() instanceof EchoedDamageSource || event.getSource() == DamageSource.OUT_OF_WORLD || event.getSource().isDamageAbsolute() || !(event.getEntity() instanceof EntityPlayer))
			return;

		int level = (int) Math.round(ArmorHelper.getArmorAbilityLevel((EntityPlayer) event.getEntityLiving(), this.identifier));

		if (level <= 0)
			return;

		int splitAmount = level + 1;

		float damageSplit = (float) (event.getAmount() / splitAmount);

		for (int i = 1; i <= level; i++) { // i hate doubles:tm:
			MiscUtils.executeInNTicks(() -> {
				if (event.getEntity().world.playerEntities.contains(event.getEntity())) {
					int hrt = event.getEntityLiving().hurtResistantTime;
					double motionX = event.getEntityLiving().motionX;
					double motionY = event.getEntityLiving().motionY;
					double motionZ = event.getEntityLiving().motionZ;

					event.getEntityLiving().hurtResistantTime = 0;
					event.getEntityLiving().attackEntityFrom(new EchoedDamageSource(event.getSource()), damageSplit);
					event.getEntityLiving().hurtResistantTime = hrt;

					event.getEntityLiving().motionX = motionX;
					event.getEntityLiving().motionY = motionY;
					event.getEntityLiving().motionZ = motionZ;
				}
			}, 20 + 10 * i);
		}

		event.setAmount(damageSplit);
	}

	public static class EchoedDamageSource extends DamageSource {
		public EchoedDamageSource(DamageSource source) {
			super("echo");
			this.delagate = source;
		}

		DamageSource delagate; // EntityDamageSource

		@Override
		public boolean isProjectile() {
			return delagate.isProjectile();
		}

		@Override
		public boolean isExplosion() {
			return delagate.isExplosion();
		}

		@Override
		public boolean isUnblockable() {
			return delagate.isUnblockable();
		}

		@Override
		public float getHungerDamage() {
			return delagate.getHungerDamage();
		}

		@Override
		public boolean canHarmInCreative() {
			return delagate.canHarmInCreative();
		}

		@Override
		public boolean isDamageAbsolute() {
			return delagate.isDamageAbsolute();
		}

		@Override
		@Nullable
		public Entity getImmediateSource() {
			return delagate.getImmediateSource();
		}

		@Override
		@Nullable
		public Entity getTrueSource() {
			return delagate.getTrueSource();
		}

		@Override
		public ITextComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
			ItemStack itemstack = this.getTrueSource() instanceof EntityLivingBase ? ((EntityLivingBase) this.getTrueSource()).getHeldItemMainhand() : ItemStack.EMPTY;
			String s = "death.attack." + this.damageType;

			if (this.getTrueSource() == null) {
				s += ".noentity";
				return new TextComponentTranslation(s, new Object[] { entityLivingBaseIn.getDisplayName() });
			}

			String s1 = s + ".item";
			return !itemstack.isEmpty() && itemstack.hasDisplayName() && I18n.canTranslate(s1) ? new TextComponentTranslation(s1, new Object[] { entityLivingBaseIn.getDisplayName(), this.getTrueSource().getDisplayName(), itemstack.getTextComponent() }) : new TextComponentTranslation(s, new Object[] { entityLivingBaseIn.getDisplayName(), this.getTrueSource().getDisplayName() });
		}

		@Override
		public String getDamageType() {
			return "echo";
		}

		@Override
		public boolean isDifficultyScaled() {
			return delagate.isDifficultyScaled();
		}

		@Override
		public boolean isMagicDamage() {
			return delagate.isMagicDamage();
		}

		@Override
		public boolean isCreativePlayer() {
			return delagate.isCreativePlayer();
		}
	}
}
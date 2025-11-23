package com.existingeevee.moretcon.traits.modifiers;

import java.util.UUID;

import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.other.utils.ReequipHack;
import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.player.PlayerEvent;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;

public class Valonite extends ModifierTrait {

	public static final UUID SPEED_MODIFIER = UUID.fromString("ae3dabe7-ffff-7d26-9039-002f90370738");
	
	public Valonite() {
		super(MiscUtils.createNonConflictiveName("modValonite"), 0xcab1ca);
		this.addItem("gemValonite");
		this.addAspects(new ModifierAspect.SingleAspect(this));

		ReequipHack.registerIgnoredKey(this.getModifierIdentifier());
	}

	@Override
	public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
		if (!entity.world.isRemote && random.nextBoolean()) {
			if (newDamage > 0) {
				boostToolStats(tool);
			}
			return 0;
		}
		return newDamage; //TinkerModifiers
	}
	
	@Override
	public int getPriority() {
		return Integer.MAX_VALUE / 2; // we need this to run last. divide by 2 to prevent wierd overflow issues
	}
	
	public void boostToolStats(ItemStack tool) {
		if (isOnCooldown(tool))
			return;

		NBTTagCompound data = tool.getOrCreateSubCompound(this.getModifierIdentifier());
		data.setInteger("HappyTime", 5 * 20);
		data.setInteger("CooldownTime", 10 * 20);
	}
	
	@Override
	public void getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack, Multimap<String, AttributeModifier> attributeMap) {
		if (slot != EntityEquipmentSlot.MAINHAND || !this.isBoosted(stack)) {
			return;
		}

		attributeMap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SPEED_MODIFIER, "atk speed modifier", 1, 1));
	}

	@Override
	public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
		if (this.isBoosted(tool)) {
			event.setNewSpeed(event.getNewSpeed() + event.getOriginalSpeed() * 0.5f + 2);
		}
	}

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		if (this.isBoosted(tool)) {
			return newDamage + damage * .25f + 2;
		}
		return newDamage;
	}

	@Override
	public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
		if (this.isBoosted(tool)) {
			player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
		}
	}

	@Override
	public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
		this.update(tool);

		if (!isSelected || (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getActiveItemStack() == tool) || world.isRemote) {
			return; // we only want things to execute if theyre holding it.
		}

		if (this.isBoosted(tool)) {
			if (world instanceof WorldServer && world.rand.nextInt(6) == 0) {
				//TODO custom particles
				Vec3d center = MiscUtils.getCenter(entity.getEntityBoundingBox());
				((WorldServer) world).spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, center.x, center.y, center.z, 1, 0.5, 0.5, 0.5, 0.01);
			}
		}
	}

	public boolean isOnCooldown(ItemStack tool) {
		if (!tool.hasTagCompound() || !tool.getTagCompound().hasKey(this.getModifierIdentifier(), NBT.TAG_COMPOUND)) {
			return false;
		}
		
		NBTTagCompound data = tool.getOrCreateSubCompound(this.getModifierIdentifier());
		return data.getInteger("CooldownTime") > 0;
	}

	public boolean isBoosted(ItemStack tool) {
		if (!tool.hasTagCompound() || !tool.getTagCompound().hasKey(this.getModifierIdentifier(), NBT.TAG_COMPOUND)) {
			return false;
		}
		NBTTagCompound data = tool.getOrCreateSubCompound(this.getModifierIdentifier());
		return data.getInteger("HappyTime") > 0;
	}

	public void update(ItemStack tool) {
		NBTTagCompound data = tool.getOrCreateSubCompound(this.getModifierIdentifier());

		if (data.getInteger("HappyTime") > 0) {
			data.setInteger("HappyTime", data.getInteger("HappyTime") - 1);
		}

		if (data.getInteger("CooldownTime") > 0) {
			data.setInteger("CooldownTime", data.getInteger("CooldownTime") - 1);
		}
	}
}
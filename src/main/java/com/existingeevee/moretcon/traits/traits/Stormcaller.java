package com.existingeevee.moretcon.traits.traits;

import java.util.UUID;

import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.other.utils.ReequipHack;
import com.existingeevee.moretcon.traits.traits.abst.AttributeTrait;
import com.existingeevee.moretcon.traits.traits.abst.ISimpleArmorTrait;

import c4.conarm.lib.armor.ArmorModifications;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.utils.ToolHelper;

@Optional.Interface(iface = "com.existingeevee.moretcon.traits.traits.abst.ISimpleArmorTrait", modid = "conarm")
public class Stormcaller extends AttributeTrait implements ISimpleArmorTrait {

	public static final UUID SPEED_MODIFIER = UUID.fromString("ae3dabe7-faaf-0bb0-9cc9-006969370738");

	public Stormcaller() {
		super("stormcaller", 0xffffff, new AttributeModifier(SPEED_MODIFIER, "atk speed modifier", 3, 1), SharedMonsterAttributes.ATTACK_SPEED);
		ReequipHack.registerIgnoredKey(this.getModifierIdentifier());
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	@Optional.Method(modid = "conarm")
	public ArmorModifications getModifications(EntityPlayer player, ArmorModifications mods, ItemStack armor, DamageSource source, double damage, int slot) {
		if (this.isElectrified(armor)) {
			mods.addEffectiveness(.75f);
		}
		return mods;
	}

	@Override
	@Optional.Method(modid = "conarm")
	public void onAbilityTick(int level, World world, EntityPlayer player) {
		if (world instanceof WorldServer) {
			for (ItemStack stack : player.getArmorInventoryList()) {
				if (this.isElectrified(stack)) {
					Vec3d center = MiscUtils.getCenter(player.getEntityBoundingBox());
					((WorldServer) world).spawnParticle(EnumParticleTypes.END_ROD, center.x, center.y, center.z, 5, 0.5, 0.5, 0.5, 0.01);
					return;
				}
			}
		}
	}

	@Override
	public boolean shouldApply(ItemStack tool, World world, EntityLivingBase entity) {
		return this.isElectrified(tool) && super.shouldApply(tool, world, entity);
	}

	@Override
	public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
		if (this.isElectrified(tool)) {
			event.setNewSpeed(event.getNewSpeed() + event.getOriginalSpeed() * 6);
		}
	}

	@SubscribeEvent
	public void onHarvestCheck(HarvestCheck event) {
		ItemStack tool = event.getEntityPlayer().getHeldItemMainhand();
		if (!this.isElectrified(tool)) {
			return;
		}
		int toolLevel = tool.getItem().getHarvestLevel(tool, "pickaxe", event.getEntityPlayer(), event.getTargetBlock()) + 2; // We get a bonus!! WAHOO
		event.setCanHarvest(event.canHarvest() || toolLevel >= event.getTargetBlock().getBlock().getHarvestLevel(event.getTargetBlock()));
	}

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		if (this.isElectrified(tool)) {
			return newDamage + damage * 1.25f;
		}
		return newDamage;
	}

	@Override
	public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
		if (this.isElectrified(tool)) {
			player.world.playSound(null, target.getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 1, 2);
		}
	}

	@Override
	public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
		this.update(tool);

		if (!isSelected || (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getActiveItemStack() == tool) || world.isRemote) {
			return; // we only want things to execute if theyre holding it.
		}

		if (this.isElectrified(tool)) {
			if (world instanceof WorldServer) {
				boolean worn = false;

				if (itemSlot < 4)
					for (ItemStack armor : ((EntityLivingBase) entity).getArmorInventoryList()) {
						worn = tool == armor;
						if (worn)
							break;
					}
				if (!worn) {
					Vec3d center = MiscUtils.getCenter(entity.getEntityBoundingBox());
					((WorldServer) world).spawnParticle(EnumParticleTypes.END_ROD, center.x, center.y, center.z, 5, 0.5, 0.5, 0.5, 0.01);
				}
			}
		}

		if (random.nextInt(600) == 0 && !isElectrified(tool) && !isOnCooldown(tool) &&
				world.canSeeSky(entity.getPosition()) && world.isRaining() && world.getBiome(entity.getPosition()).canRain()) {
			electrify(tool);
			EntityLightningBolt lightning = new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, true);
			world.addWeatherEffect(lightning);
		}
	}

	public void electrify(ItemStack tool) {
		NBTTagCompound data = tool.getOrCreateSubCompound(this.getModifierIdentifier());
		data.setInteger("ElectrifiedTime", 300);
		data.setInteger("CooldownTime", 600);
	}

	public boolean isOnCooldown(ItemStack tool) {
		if (!tool.hasTagCompound() || !tool.getTagCompound().hasKey(this.getModifierIdentifier(), NBT.TAG_COMPOUND)) {
			return false;
		}
		NBTTagCompound data = tool.getOrCreateSubCompound(this.getModifierIdentifier());
		return data.getInteger("CooldownTime") > 0;
	}

	public boolean isElectrified(ItemStack tool) {
		if (!tool.hasTagCompound() || !tool.getTagCompound().hasKey(this.getModifierIdentifier(), NBT.TAG_COMPOUND)) {
			return false;
		}
		NBTTagCompound data = tool.getOrCreateSubCompound(this.getModifierIdentifier());
		return data.getInteger("ElectrifiedTime") > 0 && !ToolHelper.isBroken(tool);
	}

	public void update(ItemStack tool) {
		NBTTagCompound data = tool.getOrCreateSubCompound(this.getModifierIdentifier());

		if (data.getInteger("ElectrifiedTime") > 0) {
			data.setInteger("ElectrifiedTime", data.getInteger("ElectrifiedTime") - 1);
		}

		if (data.getInteger("CooldownTime") > 0) {
			data.setInteger("CooldownTime", data.getInteger("CooldownTime") - 1);
		}
	}
}

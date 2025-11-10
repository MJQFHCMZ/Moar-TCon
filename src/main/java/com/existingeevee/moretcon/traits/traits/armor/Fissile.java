package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

public class Fissile extends AbstractArmorTrait {

	public Fissile() {
		super(MiscUtils.createNonConflictiveName("fissile"), TextFormatting.WHITE);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onAbilityTick(int level, World world, EntityPlayer player) {
		NBTTagCompound data = player.getEntityData().getCompoundTag(this.getModifierIdentifier());

		if (data.getInteger("Cooldown") > 0) {
			data.setInteger("Cooldown", data.getInteger("Cooldown") - 1);
		} else if (data.getBoolean("IsDetonating")) {
			int chargeup = data.getInteger("Chargeup");
			if (chargeup > 100) {
				detonate(player, level);
				data.setBoolean("IsDetonating", false);
				data.setInteger("Chargeup", 0);
				data.setInteger("Cooldown", 15 * 20);
			} else {
				data.setInteger("Chargeup", chargeup + 1);
				if (player.world instanceof WorldServer) {
					SPacketParticles spacketparticles = new SPacketParticles(EnumParticleTypes.FIREWORKS_SPARK, true, (float) player.posX, (float) player.posY + 0.5f, (float) player.posZ, 0.1f, 0.3f, 0.1f, 0.1f + Math.round(chargeup / 300f), Math.round(chargeup / 30f));
					for (EntityPlayerMP p : player.world.getPlayers(EntityPlayerMP.class, p -> true)) {
						if (p.getPositionVector().squareDistanceTo(player.getPositionVector()) < 100 * 100) {
							p.connection.sendPacket(spacketparticles);
						}
					}
				}
			}
		} else if (player.getHealth() < player.getMaxHealth() * 0.25) {
			data.setBoolean("IsDetonating", true);
		}

		player.getEntityData().setTag(this.getModifierIdentifier(), data);
	}

	private void detonate(EntityPlayer player, int level) {
		player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1, 0);

		if (player.world instanceof WorldServer) {
			SPacketParticles spacketparticles = new SPacketParticles(EnumParticleTypes.EXPLOSION_HUGE, true, (float) player.posX, (float) player.posY + 0.5f, (float) player.posZ, 0, 0, 0, 0, 1);
			for (EntityPlayerMP p : player.world.getPlayers(EntityPlayerMP.class, p -> true)) {
				if (p.getPositionVector().squareDistanceTo(player.getPositionVector()) < 100 * 100) {
					p.connection.sendPacket(spacketparticles);
				}
			}
		}

		Team team = player.getTeam();
		
		for (Entity e : player.world.getEntitiesInAABBexcluding(player, MiscUtils.pointBound(player.getPositionVector()).grow(level * 5), e -> e instanceof EntityLivingBase)) {
			if (team == null || team.getAllowFriendlyFire() || e.getTeam() != team) {
				e.attackEntityFrom(DamageSource.causePlayerDamage(player), (float) ((level * 5 + 7.5f) * Math.exp(1 / (level * 5 * level * 5) * -player.getDistanceSq(e))));
			}
		}

	}
}
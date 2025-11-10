package com.existingeevee.moretcon.traits.traits.armor;

import java.util.IdentityHashMap;

import com.existingeevee.moretcon.inits.ModBlocks;
import com.existingeevee.moretcon.other.WorldGravityUtils;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.common.armor.utils.ArmorHelper;
import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class Gravitating extends AbstractArmorTrait {

	public Gravitating() {
		super(MiscUtils.createNonConflictiveName("gravitating"), TextFormatting.WHITE);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onLivingUpdateEvent(LivingUpdateEvent e) {
		EntityLivingBase entity = e.getEntityLiving();

		if (entity.isElytraFlying() || (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying))
			return;
		
		IdentityHashMap<Entity, SlimeBounceHandler> bouncingEntities = ObfuscationReflectionHelper.getPrivateValue(SlimeBounceHandler.class, null, "bouncingEntities");
		SlimeBounceHandler handler = bouncingEntities.get(entity);

		if (handler != null) {
			double bounce = ObfuscationReflectionHelper.getPrivateValue(SlimeBounceHandler.class, handler, "bounce");

			if (Math.abs(bounce) > 1e-6)
				return;

		}

		double gravity = WorldGravityUtils.getWorldGravitiationalAcceleration(e.getEntityLiving().world, entity.getPositionVector());
		
		if (entity.motionY < 0) {
			for (ItemStack s : e.getEntityLiving().getArmorInventoryList()) {
				if (this.isToolWithTrait(s) && !ToolHelper.isBroken(s)) {
					entity.motionY += gravity * 2.5; //~3.5x gravity
					entity.motionY *= 0.98;
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingFallEvent(LivingFallEvent e) {
		double level = 0;
		if (e.getEntityLiving() instanceof EntityPlayer)
			level = ArmorHelper.getArmorAbilityLevel((EntityPlayer) e.getEntityLiving(), this.identifier);

		if (level > 1e-6 && e.getDistance() > 6) {

			Team team = e.getEntityLiving().getTeam();

			for (Entity ent : e.getEntityLiving().world.getEntitiesInAABBexcluding(e.getEntityLiving(), MiscUtils.vectorBound(e.getEntityLiving().getPositionVector().subtract(level * 2, 0, level * 2), e.getEntityLiving().getPositionVector().addVector(level * 2, 1, level * 2)), en -> en instanceof EntityLivingBase)) {
				if (team == null || team.getAllowFriendlyFire() || ent.getTeam() != team) {
					ent.attackEntityFrom(
							DamageSource.causePlayerDamage((EntityPlayer) e.getEntityLiving()),
							(float) (Math.min(12 * level, e.getDistance() * 2) * Math.exp(-1. / (level * level) * e.getEntityLiving().getDistanceSq(ent))));
				}
			}

			if (e.getEntityLiving().world instanceof WorldServer) {
				SPacketParticles spacketparticles1 = new SPacketParticles(EnumParticleTypes.EXPLOSION_LARGE, true, (float) e.getEntity().posX, (float) e.getEntity().posY + 0.1f, (float) e.getEntity().posZ, 0, 0, 0, 0, 1);
				SPacketParticles spacketparticles2 = new SPacketParticles(EnumParticleTypes.BLOCK_DUST, true, (float) e.getEntity().posX, (float) e.getEntity().posY + 0.1f, (float) e.getEntity().posZ, 0, 0, 0, 0.4f, 200, Block.getStateId(ModBlocks.blockGravitonium.getDefaultState()));
				SPacketCustomSound spacketsound = new SPacketCustomSound(Sounds.frypan_boing.getRegistryName().toString(), SoundCategory.PLAYERS, e.getEntity().posX, e.getEntity().posY + 0.5f, e.getEntity().posZ, 3, 0);
				for (EntityPlayerMP p : e.getEntityLiving().world.getPlayers(EntityPlayerMP.class, p -> true)) {
					if (p.getDistanceSq(e.getEntity()) < 100 * 100) {
						p.connection.sendPacket(spacketparticles1);
						p.connection.sendPacket(spacketparticles2);
		                p.connection.sendPacket(spacketsound);
					}
				}

				((WorldServer) e.getEntityLiving().world).playSound((float) e.getEntity().posX, (float) e.getEntity().posY + 0.5f, (float) e.getEntity().posZ, Sounds.frypan_boing, SoundCategory.PLAYERS, 3, 0, false);
			}
						
			e.setDamageMultiplier(e.getDamageMultiplier() * 0.2f);			
		}
	}
}
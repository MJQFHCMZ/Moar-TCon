package com.existingeevee.moretcon.mixin.early.common;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

@Mixin(EntityFireworkRocket.class)
public abstract class MixinEntityFireworkRocket extends Entity {
	
	MixinEntityFireworkRocket(World worldIn) {
		super(worldIn);
	}

	@WrapWithCondition(method = "dealExplosionDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", ordinal = 1))
	private boolean moretcon$WrapWithCondition_attackEntityFrom_o1$dealExplosionDamage(EntityLivingBase entitylivingbase, DamageSource source, float amount) {
		UUID shooterUUID = this.getEntityData().getUniqueId("moretcon.modcelebratory_uuid");
		if (shooterUUID == null) {
			return true;
		}
		
		if (entitylivingbase.getUniqueID().equals(shooterUUID)) {
			return false;
		}
		
		EntityPlayer shooter = this.world.getPlayerEntityByUUID(shooterUUID);
		
		if (shooter != null) {
			Team team = shooter.getTeam();
			if (team != null && !team.getAllowFriendlyFire()) {
				return team != entitylivingbase.getTeam();
			}
		}
		
		return true;
	}
}
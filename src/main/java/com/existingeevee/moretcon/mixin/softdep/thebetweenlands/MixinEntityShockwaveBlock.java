package com.existingeevee.moretcon.mixin.softdep.thebetweenlands;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.existingeevee.moretcon.traits.traits.unique.Shockwaving;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import thebetweenlands.common.entity.EntityShockwaveBlock;

@Mixin(EntityShockwaveBlock.class)
public abstract class MixinEntityShockwaveBlock extends Entity {

	public MixinEntityShockwaveBlock(World worldIn) {
		super(worldIn);
	}

	@WrapOperation(method = "onUpdate()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z"))
	boolean moretcon$INVOKE_WrapOperation$onUpdate(EntityLivingBase target, DamageSource source, float damage, Operation<Boolean> original) {
		EntityShockwaveBlock $this = (EntityShockwaveBlock) (Object) this;		
		
		if (Shockwaving.shouldHandle($this)) {
			return Shockwaving.handle($this, target, source, damage);
		}
		
		return original.call(target, source, damage);
	}
}

package com.existingeevee.moretcon.traits.traits.nyi;

import com.existingeevee.moretcon.other.fires.CustomFireEffect;
import com.existingeevee.moretcon.other.fires.CustomFireHelper;
import com.existingeevee.moretcon.other.fires.CustomFireInfo;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import slimeknights.tconstruct.library.traits.AbstractTrait;

//for Alkinium? or maybe should gate it past to metallic hydrogen or ste'lantium
public class Catalyzing extends AbstractTrait {

	//Your tool will catalyze fires and cause burning enemies to instantly take all of the fire damage at once.
	public Catalyzing() {
		super(MiscUtils.createNonConflictiveName("catalyzing"), 0x00ed00);
	}

	@Override
	public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
		if (!wasCritical)
			return;
		
		int hurtResist = target.getEntityData().getInteger(this.getModifierIdentifier() + ".HRT");
		
		if (target.isBurning()) {
			int burning = ObfuscationReflectionHelper.getPrivateValue(Entity.class, target, "field_190534_ay");
			target.hurtResistantTime = hurtResist;
			target.attackEntityFrom(DamageSource.ON_FIRE, burning / 17.5f);
			return;
		} 
		CustomFireInfo info = CustomFireHelper.getBurningInfo(target);
		if (info == null)
			return;
		
		if (info.getEffect() == CustomFireEffect.COLD_FIRE) {
			float damage = Math.max(4, target.getHealth() / 10) * info.getTimeRemaining() / 30f;
			target.hurtResistantTime = hurtResist;
			target.attackEntityFrom(new DamageSource("coldfire").setFireDamage(), damage);
			
		} else if (info.getEffect() == CustomFireEffect.SPIRIT_FIRE) {
			float damage = 4 * info.getTimeRemaining() / 15f;
			target.hurtResistantTime = 0;
			target.attackEntityFrom(new DamageSource("haunted").setFireDamage(), damage);
			
		}
	}

	@Override
	public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
		target.getEntityData().setInteger(this.getModifierIdentifier() + ".HRT", target.hurtResistantTime);
	}
	
	@Override
	public int getPriority() {
		return -2; //we need this to run last.
	}
}
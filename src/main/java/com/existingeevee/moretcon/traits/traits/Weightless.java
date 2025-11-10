package com.existingeevee.moretcon.traits.traits;

import java.util.UUID;

import com.existingeevee.moretcon.other.WorldGravityUtils;
import com.existingeevee.moretcon.traits.traits.abst.AttributeTrait;
import com.existingeevee.moretcon.traits.traits.abst.IAdditionalTraitMethods;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

public class Weightless extends AttributeTrait implements IAdditionalTraitMethods {

	public Weightless() {
		super("weightless", 0, new AttributeModifier(UUID.fromString("aed073df-79af-4de9-b62c-44b5fcc4df1d"), "weightless", 1.00, 2), SharedMonsterAttributes.ATTACK_SPEED);
	}
	
	@Override
	public void onEntityItemTick(ItemStack tool, EntityItem entity) {
		if (WorldGravityUtils.getWorldGravitiationalAcceleration(entity.world, entity.getPositionVector()) == -0.08)
			entity.motionY += 0.039f;
	}
}

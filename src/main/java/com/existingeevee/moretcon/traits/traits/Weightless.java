package com.existingeevee.moretcon.traits.traits;

import java.util.UUID;

import com.existingeevee.moretcon.traits.traits.abst.AttributeTrait;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;

public class Weightless extends AttributeTrait {

	public Weightless() {
		super("weightless", 0, new AttributeModifier(UUID.fromString("aed073df-79af-4de9-b62c-44b5fcc4df1d"), "weightless", 1.00, 2), SharedMonsterAttributes.ATTACK_SPEED);
	}
}

package com.existingeevee.moretcon.traits.traits.armor;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.google.common.collect.Multimap;

import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class Galvanized extends AbstractArmorTrait {

    public Galvanized() {
        super(MiscUtils.createNonConflictiveName("galvanized"), TextFormatting.WHITE);
    }

    @Override
    public void getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, ItemStack stack, Multimap<String, AttributeModifier> attributeMap) {
    	if (EntityLiving.getSlotForItemStack(stack) == slot) {
    		attributeMap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(new UUID(slot.getIndex(), this.getIdentifier().hashCode()), "atk modifier galvanized", 0.05, 1));
    	}
    }
}
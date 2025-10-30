package com.existingeevee.moretcon.traits.traits.abst;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class AttributeTrait extends AbstractTrait {

	private final AttributeModifier modifier;
	private final IAttribute type;

	private boolean worksInOffhand = false;
	private boolean worksInArmor = false;

	public AttributeTrait(String id, int color, AttributeModifier modifier, IAttribute type) {
		super(MiscUtils.createNonConflictiveName(id), color);
		this.modifier = modifier.setSaved(false);
		this.type = type;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void handleAttr(LivingUpdateEvent event) {
		IAttributeInstance attr = event.getEntityLiving().getAttributeMap().getAttributeInstance(type);
		if (attr == null) {
			return;
		}
		boolean inLeft = this.isToolWithTrait(event.getEntityLiving().getHeldItemOffhand()) && this.shouldApply(event.getEntityLiving().getHeldItemOffhand(), event.getEntity().world, event.getEntityLiving());
		boolean inRight = this.isToolWithTrait(event.getEntityLiving().getHeldItemMainhand()) && this.shouldApply(event.getEntityLiving().getHeldItemMainhand(), event.getEntity().world, event.getEntityLiving());
		
		boolean armorApplicable = false;
		
		if (worksInArmor) {
			for (ItemStack stack : event.getEntityLiving().getArmorInventoryList()) {
				if (this.isToolWithTrait(stack) && this.shouldApply(stack, event.getEntity().world, event.getEntityLiving())) {
					armorApplicable = true;
					break;
				}
			}
		}
		
		if (inRight || inLeft && worksInOffhand() || armorApplicable) {
			if (!attr.hasModifier(modifier)) {
				attr.applyModifier(modifier);
			}
		} else {
			if (attr.hasModifier(modifier)) {
				attr.removeModifier(modifier);
			}
		}
	}

	public boolean shouldApply(ItemStack tool, World world, EntityLivingBase entity) {
		return !ToolHelper.isBroken(tool);
	}

	public boolean worksInOffhand() {
		return worksInOffhand;
	}

	public AttributeTrait setWorksInOffhand(boolean worksInOffhand) {
		this.worksInOffhand = worksInOffhand;
		return this;
	}
	
	public boolean worksInArmor() {
		return worksInArmor;
	}
	
	public AttributeTrait setWorksInArmor(boolean worksInArmor) {
		this.worksInArmor = worksInArmor;
		return this;
	}
}

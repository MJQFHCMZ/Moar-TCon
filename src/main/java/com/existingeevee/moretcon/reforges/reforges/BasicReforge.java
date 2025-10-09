package com.existingeevee.moretcon.reforges.reforges;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.reforges.AbstractReforge;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class BasicReforge extends AbstractReforge {

	protected final Random rand;

	protected final Map<IAttribute, AttributeModifier> attributes = new HashMap<>();
	protected final Multimap<Category, Pair<Function<NBTTagCompound, ? extends ToolNBT>, Consumer<? extends ToolNBT>>> modifiers = ArrayListMultimap.create();
	boolean mainhandOnly = false;

	float critChance = 0;
	float critDamage = 0;

	float knockback = 0;
	float damage = 0;

	public BasicReforge(String name, int color) {
		super(MiscUtils.createNonConflictiveName(name), color);
		rand = new Random(name.hashCode());
	}

	@Override
	public float knockBack(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float knockback, float newKnockback, boolean isCritical) {
		return newKnockback + knockback * knockback;
	}

	@Override
	public void getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, ItemStack stack, Multimap<String, AttributeModifier> attributeMap) {
		if (slot == EntityEquipmentSlot.MAINHAND || !mainhandOnly && slot == EntityEquipmentSlot.OFFHAND) {
			for (Entry<IAttribute, AttributeModifier> e : attributes.entrySet()) {
				attributeMap.put(e.getKey().getName(), e.getValue());
			}
		}
	}

	@Override
	public boolean isCriticalHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target) {
		return Math.random() < critChance;
	}

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		newDamage += damage * this.damage;

		if (isCritical)
			newDamage += damage * critDamage;

		return newDamage;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
		super.applyEffect(rootCompound, modifierTag);

		for (Entry<Category, Collection<Pair<Function<NBTTagCompound, ? extends ToolNBT>, Consumer<? extends ToolNBT>>>> entry : modifiers.asMap().entrySet()) {
			if (entry.getKey() == null || TinkerUtil.hasCategory(rootCompound, entry.getKey())) {
				for (Pair<Function<NBTTagCompound, ? extends ToolNBT>, Consumer<? extends ToolNBT>> p : entry.getValue()) {
					ToolNBT nbt = p.getLeft().apply(rootCompound);
					if (nbt != null) {
						((Consumer<ToolNBT>) p.getRight()).accept(nbt);
						TagUtil.setToolTag(rootCompound, nbt.get());
					}
				}
			}
		}

		if (TinkerUtil.hasCategory(rootCompound, Category.LAUNCHER)) {
			ProjectileLauncherNBT launcherData = new ProjectileLauncherNBT(TagUtil.getToolTag(rootCompound));
			launcherData.drawSpeed = Float.MAX_VALUE; // YES
			launcherData.bonusDamage -= 6; // Very large penalty
			TagUtil.setToolTag(rootCompound, launcherData.get());
		}
	}

	public BasicReforge withAddedAttributeMult(IAttribute attr, double mod) {
		UUID uuid = new UUID(rand.nextLong(), rand.nextLong());
		return withAddedAttribute(attr, new AttributeModifier(uuid, this.getModifierIdentifier() + "_" + attr.getName() + "_" + uuid, mod, 2));
	}

	public BasicReforge withAddedAttribute(IAttribute attr, AttributeModifier mod) {
		attributes.put(attr, mod);
		return this;
	}

	public BasicReforge withCritChance(float critChance) {
		this.critChance = critChance;
		return this;
	}

	public BasicReforge withKnockback(float knockback) {
		this.knockback = knockback;
		return this;
	}

	public BasicReforge withCritDamage(float critDamage) {
		this.critDamage = critDamage;
		return this;
	}

	public <T extends ToolNBT> BasicReforge withAddedModifier(Category cat, Function<NBTTagCompound, T> fromRoot, Consumer<T> applyToNBT) {
		modifiers.put(cat, Pair.of(fromRoot, applyToNBT));
		return this;
	}

	@Override
	public int getPriority() {
		return 140;
	}

	public BasicReforge withDamage(float damage) {
		this.damage = damage;
		return this;
	}
}

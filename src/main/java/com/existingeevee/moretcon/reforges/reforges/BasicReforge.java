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
import com.existingeevee.moretcon.traits.ModTraits;
import com.existingeevee.moretcon.traits.traits.abst.IAdditionalTraitMethods;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import slimeknights.tconstruct.library.capability.projectile.TinkerProjectileHandler;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class BasicReforge extends AbstractReforge implements IAdditionalTraitMethods {

	protected final Random rand;

	protected final Map<IAttribute, AttributeModifier> attributes = new HashMap<>();
	protected final Multimap<Category, Pair<Function<NBTTagCompound, ? extends ToolNBT>, Consumer<? extends ToolNBT>>> modifiers = ArrayListMultimap.create();
	boolean mainhandOnly = false;

	float critChance = 0;
	float critDamage = 0;

	float knockback = 0;
	float damage = 0;

	boolean isRanged = true;
	boolean isMelee = true;
	float speed = 0;
	float reach = 0;

	float velocity = 0; 
	
	public static final ThreadLocal<Boolean> IS_FROM_FIRED_PROJECTILE = ThreadLocal.withInitial(() -> false);

	public BasicReforge(String name, int color) {
		super(MiscUtils.createNonConflictiveName(name), color);
		rand = new Random(name.hashCode());
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public float knockBack(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float knockback, float newKnockback, boolean isCritical) {
		if (isMelee && !IS_FROM_FIRED_PROJECTILE.get() || isRanged && IS_FROM_FIRED_PROJECTILE.get()) {
			return newKnockback + knockback * knockback;
		}
		return super.knockBack(tool, player, target, damage, knockback, newKnockback, isCritical);
	}

	protected static final UUID ARK_SPD = MathHelper.getRandomUUID(ThreadLocalRandom.current());
	
	@Override
	public void getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, ItemStack stack, Multimap<String, AttributeModifier> attributeMap) {
		if (slot == EntityEquipmentSlot.MAINHAND || !mainhandOnly && slot == EntityEquipmentSlot.OFFHAND) {
			for (Entry<IAttribute, AttributeModifier> e : attributes.entrySet()) {
				attributeMap.put(e.getKey().getName(), e.getValue());
			}
		}
		
		if (isMelee && slot == EntityEquipmentSlot.MAINHAND) {
			attributeMap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ARK_SPD, identifier + "_atkspd", speed, 1));
		}
	}

	@Override
	public boolean isCriticalHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target) {
		if (isMelee && !IS_FROM_FIRED_PROJECTILE.get() || isRanged && IS_FROM_FIRED_PROJECTILE.get()) {
			return Math.random() < critChance;
		}
		return super.isCriticalHit(tool, player, target);
	}

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		if (isMelee && !IS_FROM_FIRED_PROJECTILE.get() || isRanged && IS_FROM_FIRED_PROJECTILE.get()) {
			newDamage += damage * this.damage;

			if (isCritical)
				newDamage += damage * critDamage;
		}
		return newDamage;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
		super.applyEffect(rootCompound, modifierTag);

		for (Entry<Category, Collection<Pair<Function<NBTTagCompound, ? extends ToolNBT>, Consumer<? extends ToolNBT>>>> entry : modifiers.asMap().entrySet()) {
			if (entry.getKey() == null || TinkerUtil.hasCategory(rootCompound, entry.getKey())) {
				for (Pair<Function<NBTTagCompound, ? extends ToolNBT>, Consumer<? extends ToolNBT>> p : entry.getValue()) {
					ToolNBT nbt = p.getLeft().apply(rootCompound);
					if (nbt != null) {
						((Consumer) p.getRight()).accept(nbt);
						TagUtil.setToolTag(rootCompound, nbt.get());
					}
				}
			}
		}
		
		if (TinkerUtil.hasCategory(rootCompound, Category.LAUNCHER) && this.isRanged) {
			ProjectileLauncherNBT launcherData = new ProjectileLauncherNBT(TagUtil.getToolTag(rootCompound));
			launcherData.drawSpeed *= 1 + speed;
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
	
	public BasicReforge withSpeed(float speed) {
		this.speed = speed;
		return this;
	}
	
	@Override
	public boolean modifyLauncherProjectile(ItemStack launchingStack, ItemStack parent, ItemStack copy, TinkerProjectileHandler tinkerProjectileHandler) {
		if (isRanged) {
			ModTraits.reforgeProj.apply(parent);
			return true;
		}
		
		return false;
	}

	@Override
	public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
		if (isMelee) {
			event.setNewSpeed(event.getNewSpeed() + event.getOriginalSpeed() * speed);
		}
	}
	
	public BasicReforge withTypes(boolean melee, boolean ranged) {
		this.isMelee = melee;
		this.isRanged = ranged;
		return this;
	}
	
	public boolean isMelee() {
		return isMelee;
	}

	public boolean isRanged() {
		return isRanged;
	}
	
	@Override
	public boolean canApplyCustom(ItemStack stack) throws TinkerGuiException {
		NBTTagCompound rootCompound = TagUtil.getTagSafe(stack);
		if ((!TinkerUtil.hasCategory(rootCompound, Category.NO_MELEE) || TinkerUtil.hasCategory(rootCompound, Category.PROJECTILE)) && this.isMelee) {
			return super.canApplyCustom(stack);
		}
		
		if (TinkerUtil.hasCategory(rootCompound, Category.LAUNCHER) && this.isRanged) {
			return super.canApplyCustom(stack);
		}
		
		return false;
	}

	public BasicReforge withVelocity(float velocity) {
		this.velocity = velocity;
		return this;
	}

	public float getVelocity() {
		return velocity;
	}
}

//@SubscribeEvent
//public void onLivingEntityUseItemEventStop(LivingEntityUseItemEvent.Stop event) {		
//	if (!(event.getEntityLiving() instanceof EntityPlayer) || event.getEntity().world.isRemote) {
//		return;
//	}
//	
//	reduceCD((EntityPlayer) event.getEntityLiving(), event.getItem());
//}
//
//@SubscribeEvent
//public void onRightClick(OverrideItemUseEvent event) {		
//	System.out.println("aaa");
//	
//	if (!(event.getEntityLiving() instanceof EntityPlayer) || event.getEntity().world.isRemote) {
//		return;
//	}
//	MiscUtils.executeInNTicks(() -> {
//		reduceCD((EntityPlayer) event.getEntityLiving(), event.getItemStack());
//	}, 1);
//}
//
//@SuppressWarnings("unchecked")
//protected void reduceCD(EntityPlayer player, ItemStack stack) {
//	if (ReforgeHelper.getReforge(stack) == this && this.isRanged) {
//					
//		Map<Item, Object> map = (Map<Item, Object>) ObfuscationReflectionHelper.getPrivateValue(CooldownTracker.class, player.getCooldownTracker(), "field_185147_a");
//		Object cooldown = map.get(stack.getItem());
//		//	createTicks field_185137_a
//		//  expireTicks field_185138_b
//		if (cooldown != null) {
//			
//			int createTicks = (Integer) ObfuscationReflectionHelper.getPrivateValue((Class<? super Object>) cooldown.getClass(), cooldown, "field_185147_a");
//			int expireTicks = (Integer) ObfuscationReflectionHelper.getPrivateValue((Class<? super Object>) cooldown.getClass(), cooldown, "field_185138_b");
//			
//			int time = expireTicks - createTicks;
//							
//			if (time > 0) {
//				player.getCooldownTracker().setCooldown(stack.getItem(), Math.round(time * (1 - speed)));
//			}
//		}
//	}
//}

package com.existingeevee.moretcon.entity.entities;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.existingeevee.moretcon.other.DamageScalar;
import com.existingeevee.moretcon.traits.traits.unique.Gaseous;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ranged.ILauncher;
import slimeknights.tconstruct.library.tools.ranged.IProjectile;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class EntityGasCloud extends Entity {
	protected static final UUID POWER_MODIFIER = UUID.fromString("c6aefc22-081a-4c4a-b076-8f9d6cef9122");

	private float radiusPrev = 0;
	private static final DataParameter<Boolean> DEFUSED = EntityDataManager.createKey(EntityGasCloud.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Float> RADIUS = EntityDataManager.createKey(EntityGasCloud.class, DataSerializers.FLOAT);
	private static final DataParameter<ItemStack> STACK = EntityDataManager.createKey(EntityGasCloud.class, DataSerializers.ITEM_STACK);

	private int age;
	private int duration = 200;
	private int attackCooldown = 10;

	private EntityLivingBase attacker;
	private UUID attackerUUID;

	private static final GameProfile GAS_PROFILE = new GameProfile(UUID.fromString("b8c3a9de-13f6-4b63-9e55-37fd3b7e9a11"), "Gas Cloud");

	public EntityGasCloud(World world) {
		super(world);
		setSize(1.0F, 1.0F);
		noClip = true;
		isImmuneToFire = true;
	}

	public EntityGasCloud(World world, double x, double y, double z, float radius, ItemStack stack, @Nullable EntityLivingBase attacker2) {
		this(world);
		setPosition(x, y, z);
		setRadius(radius);
		setStack(stack);
		this.attacker = attacker2;
		if (attacker != null)
			this.attackerUUID = attacker.getUniqueID();
	}

	@Override
	protected void entityInit() {
		dataManager.register(RADIUS, 3.0F);
		dataManager.register(STACK, ItemStack.EMPTY);
		dataManager.register(DEFUSED, false);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (attackerUUID != null && attacker == null) {
			attacker = (EntityLivingBase) this.world.getLoadedEntityList().stream().filter(e -> e instanceof EntityLivingBase && e.getUniqueID().equals(attackerUUID)).findAny().orElse(null);
		}

		if (!world.isRemote) {
			if (age >= duration) {
				this.setDefused(true);
			}

			if (age >= duration + 20) {
				this.setDead();
			}

			if (age % attackCooldown == 0 && !this.isDefused()) {
				Gaseous.GAS.set(true);
				try {
					attackEntitiesInside();
				} finally {
					Gaseous.GAS.remove();
				}
			} else if (this.isDefused()) {
				this.setRadius(this.getRadius() + 0.025f);
			}
		} else {
			this.radiusPrev = this.getRadius();
		}

		age++;
	}

	private void attackEntitiesInside() {
		float radius = getRadius();
		double radiusSq = radius * radius;

		AxisAlignedBB box = getEntityBoundingBox().grow(radius + 5);

		List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, box, entity -> {
			if (entity == null || entity.isDead)
				return false;

			double dx = entity.posX - posX;
			double dy = entity.getEyeHeight() + entity.posY - posY;
			double dz = entity.posZ - posZ;

			if (attacker != null) {
				if (entity == attacker)
					return false;

				Team team = attacker.getTeam();
				if (team != null && !team.getAllowFriendlyFire() && entity.getTeam() == team) {
					return false;
				}
			}

			return dx * dx + dy * dy + dz * dz <= radiusSq;
		});

		for (EntityLivingBase entity : list) {
			attackEntity(entity);
		}
	}

	protected void attackEntity(EntityLivingBase entity) {
		if (!entity.world.isRemote && entity.world instanceof WorldServer) {
			WorldServer world = (WorldServer) entity.world;

			ItemStack stack = getStack().copy();

			FakePlayer fp = FakePlayerFactory.get(world, GAS_PROFILE);

			unequip(fp, EntityEquipmentSlot.MAINHAND);
			fp.setHeldItem(EnumHand.MAIN_HAND, stack);
			equip(fp, EntityEquipmentSlot.MAINHAND);
			Multimap<String, AttributeModifier> attr = null;
			Item item = stack.getItem();
			if (item instanceof IProjectile) {
				IProjectile proj = (IProjectile) item;
				attr = proj.getProjectileAttributeModifier(stack);
				if (item instanceof ILauncher)
					((ILauncher) item).modifyProjectileAttributes(attr, stack, stack, 1);

				attr.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(POWER_MODIFIER, "Weapon damage multiplier", 1, 2));
				fp.getAttributeMap().applyAttributeModifiers(attr);
			}

			double motX = entity.motionX;
			double motY = entity.motionY;
			double motZ = entity.motionZ;

			DamageScalar.push(0.05f);
			EntityLivingBase revengeTarget = entity.getRevengeTarget();
			int hrt = entity.hurtResistantTime;
			entity.hurtResistantTime = 0;
			try {
				if (stack.getItem() instanceof ToolCore) {
					fp.resetCooldown();
					ToolHelper.attackEntity(stack, (ToolCore) stack.getItem(), fp, entity, null, false);

				} else {
					fp.attackTargetEntityWithCurrentItem(entity);
				}
			} catch (Exception e) {
				// even if a buggy hit happens i think its okay to skip it.
			} finally {
				if (attr != null)
					fp.getAttributeMap().removeAttributeModifiers(attr);
			}
			entity.hurtResistantTime = hrt;
			entity.setRevengeTarget(this.attacker != null ? this.attacker : revengeTarget);
			DamageScalar.pop();

			entity.motionX = motX;
			entity.motionY = motY;
			entity.motionZ = motZ;
		}
	}

	public float getRadius() {
		return dataManager.get(RADIUS);
	}

	public float getRadiusInterp(float partialTicks) {
		return radiusPrev + (getRadius() - radiusPrev) * partialTicks;
	}

	public void setRadius(float radius) {
		dataManager.set(RADIUS, radius);
	}

	public boolean isDefused() {
		return dataManager.get(DEFUSED);
	}

	public void setDefused(boolean defused) {
		dataManager.set(DEFUSED, defused);
	}

	public ItemStack getStack() {
		return dataManager.get(STACK);
	}

	public void setStack(ItemStack stack) {
		dataManager.set(STACK, stack == null ? ItemStack.EMPTY : stack.copy());
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		age = tag.getInteger("Age");
		duration = tag.getInteger("Duration");
		setRadius(tag.getFloat("Radius"));

		if (tag.hasKey("Stack"))
			setStack(new ItemStack(tag.getCompoundTag("Stack")));

		if (age >= duration) {
			this.setDefused(true);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		tag.setInteger("Age", age);
		tag.setInteger("Duration", duration);
		tag.setFloat("Radius", getRadius());

		ItemStack stack = getStack();
		if (!stack.isEmpty()) {
			NBTTagCompound stackTag = new NBTTagCompound();
			stack.writeToNBT(stackTag);
			tag.setTag("Stack", stackTag);
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	protected void dealFireDamage(int amount) {
	}

	public float getRadiusPrev() {
		return radiusPrev;
	}

	private void unequip(EntityLivingBase entity, EntityEquipmentSlot slot) {
		ItemStack stack = entity.getItemStackFromSlot(slot);
		if (!stack.isEmpty()) {
			entity.getAttributeMap().removeAttributeModifiers(stack.getAttributeModifiers(slot));
		}
	}

	private void equip(EntityLivingBase entity, EntityEquipmentSlot slot) {
		ItemStack stack = entity.getItemStackFromSlot(slot);
		if (!stack.isEmpty()) {
			entity.getAttributeMap().applyAttributeModifiers(stack.getAttributeModifiers(slot));
		}
	}

	public int getAge() {
		return age;
	}
}
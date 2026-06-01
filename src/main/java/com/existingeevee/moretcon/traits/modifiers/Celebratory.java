package com.existingeevee.moretcon.traits.modifiers;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.existingeevee.moretcon.entity.entities.EntityBomb;
import com.existingeevee.moretcon.inits.ModItems;
import com.existingeevee.moretcon.other.DamageScalar;
import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.other.utils.ReequipHack;
import com.existingeevee.moretcon.traits.ModTraits;
import com.existingeevee.moretcon.traits.traits.abst.IAdditionalTraitMethods;
import com.existingeevee.moretcon.traits.traits.abst.IBombTrait;
import com.existingeevee.moretcon.traits.traits.internal.PolyshotProj;
import com.existingeevee.moretcon.traits.traits.unique.Dematerializing;
import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.traits.IProjectileTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class Celebratory extends ModifierTrait implements IBombTrait, IProjectileTrait, IAdditionalTraitMethods {

	private static final Field lifeTime$EntityFireworkRocket = ObfuscationReflectionHelper.findField(EntityFireworkRocket.class, "field_92055_b"); // lifeTime

	public Celebratory() {
		super(MiscUtils.createNonConflictiveName("modcelebratory"), 0xD82E1A, 8, 1);
		this.addRecipeMatch(new RecipeMatch.ItemCombination(1, new ItemStack(Items.PAPER), new ItemStack(Items.PAPER), new ItemStack(ModItems.ingotZracohlium), new ItemStack(Blocks.TNT), new ItemStack(Items.GUNPOWDER)));
		ReequipHack.registerIgnoredKey(this.getModifierIdentifier());
	}

	@Override
	public void onProjectileUpdate(EntityProjectileBase projectile, World world, ItemStack toolStack) {
		if (!world.isRemote && projectile.inGround && !(projectile instanceof EntityBomb) && !projectile.getTags().contains("CelebratoryDefused")) {
			if (!projectile.getTags().contains("CelebratoryBoom")) {
				return;
			}

			boolean pushed = false;

			if (ModTraits.polyshotProj.isToolWithTrait(toolStack)) {
				NBTTagCompound comp = projectile.getEntityData().getCompoundTag(ModTraits.polyshotProj.getModifierIdentifier());
				double distTraveled = comp.getDouble("DistTraveled");
				float mult = (float) (2 * Math.exp(-(PolyshotProj.DSQ_SCALAR * distTraveled) * (PolyshotProj.DSQ_SCALAR * distTraveled)));
				DamageScalar.push(mult);
				pushed = true;
			}
			try {
				explode(toolStack, world, projectile.shootingEntity == null ? null : projectile.shootingEntity.getUniqueID(), null, projectile.getPositionVector(), true);
			} finally {
				if (pushed) {
					DamageScalar.pop();
				}
			}

			projectile.getTags().add("CelebratoryDefused");
		}
	}

	@Override
	public void onDetonate(ItemStack tool, World world, EntityBomb bomb, EntityLivingBase attacker) {
		if (world.isRemote) {
			return;
		}
		if (!bomb.getTags().contains("CelebratoryBoom")) {
			return;
		}

		boolean pushed = false;

		if (ModTraits.polyshotProj.isToolWithTrait(tool)) {
			NBTTagCompound comp = bomb.getEntityData().getCompoundTag(ModTraits.polyshotProj.getModifierIdentifier());
			double distTraveled = comp.getDouble("DistTraveled");
			float mult = (float) (2 * Math.exp(-(PolyshotProj.DSQ_SCALAR * distTraveled) * (PolyshotProj.DSQ_SCALAR * distTraveled)));
			DamageScalar.push(mult);
			pushed = true;
		}
		try {
			explode(tool, world, attacker == null ? null : attacker.getUniqueID(), null, bomb.getPositionVector(), true);
		} finally {
			if (pushed) {
				DamageScalar.pop();
			}
		}
	}

	@Override
	public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
		if (ToolHelper.hasCategory(tool, Category.PROJECTILE))
			return; // we handle projectiles ( + bombs) seperately

		if (wasHit) {

			explode(tool, target.world, player == null ? null : player.getUniqueID(), target, null, false);
		}
	}

	@Override
	public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
		NBTTagCompound celebratoryData = tool.getOrCreateSubCompound(this.getModifierIdentifier());
		if (celebratoryData.getInteger("CelebratoryCooldown") > 0) {
			celebratoryData.setInteger("CelebratoryCooldown", celebratoryData.getInteger("CelebratoryCooldown") - 1);
		}
	}

	public void explode(ItemStack tool, World world, @Nullable UUID attacker, @Nullable Entity target, @Nullable Vec3d cent, boolean bypass) {
		if (world.isRemote || (target == null && cent == null))
			return;

		NBTTagCompound celebratoryData = tool.getOrCreateSubCompound(this.getModifierIdentifier());

		if (!bypass) {
			if (celebratoryData.getInteger("CelebratoryCooldown") > 0) {
				return;
			}
			celebratoryData.setInteger("CelebratoryCooldown", 40);
		}

		ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(TinkerUtil.getModifierTag(tool, getModifierIdentifier()));
		int amount = data.level;

		if (cent == null)
			cent = MiscUtils.getCenter(target.getEntityBoundingBox());
		EntityFireworkRocket rocket;

		int color = Dematerializing.getToolMaterials(tool).get(1).materialTextColor;

		if (!(target instanceof EntityLivingBase)) {
			rocket = new EntityFireworkRocket(world, cent.x, cent.y, cent.z, makeFirework(world.rand, color, amount));
		} else {
			rocket = new EntityFireworkRocket(world, makeFirework(world.rand, color, amount), (EntityLivingBase) target);
		}

		if (attacker != null) {
			rocket.getEntityData().setUniqueId("moretcon.modcelebratory_uuid", attacker);
		}

		rocket.motionX = 0;
		rocket.motionY = -0.04; // it ticks upwards by 0.04
		rocket.motionZ = 0;

		try {
			lifeTime$EntityFireworkRocket.set(rocket, 0); // instant kapow
		} catch (IllegalAccessException e) {
		}

		int hrt = 0;
		if (target != null) {
			hrt = target.hurtResistantTime;
			target.hurtResistantTime = 0;
		}
		DamageScalar.push(0.5f);
		world.spawnEntity(rocket);
		rocket.onUpdate(); // immidiate death
		DamageScalar.pop();
		if (target != null) {
			target.hurtResistantTime = hrt;
		}
	}

	@Override
	public int getPriority() {
		return 1000; // somewhat last
	}

	public static final DecimalFormat format = new DecimalFormat("#.##");

	@Override
	public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
		String loc = String.format(LOC_Extra, getModifierIdentifier());
		ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);
		int amount = data.level;

		float damage = 0.5f * (5 + amount * 2);

		return ImmutableList.of(Util.translateFormatted(loc, format.format(damage)));
	}

	public static ItemStack makeFirework(Random rand, int color, int explosions) {
		ItemStack stack = new ItemStack(Items.FIREWORKS);
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound fireworks = new NBTTagCompound();
		NBTTagList explosionList = new NBTTagList();

		for (int i = 0; i < explosions; i++) {
			NBTTagCompound explosion = new NBTTagCompound();

			explosion.setByte("Type", (byte) rand.nextInt(5));
			explosion.setBoolean("Flicker", rand.nextBoolean());
			explosion.setBoolean("Trail", rand.nextInt(3) == 0);

			int mainColor = shiftBrightness(color, 0.65F + rand.nextFloat() * 0.7F);
			int altColor = rand.nextInt(4) == 0 ? 0xFFFFFF : shiftBrightness(color, 0.9F + rand.nextFloat() * 0.5F);

			if (i > 4 && rand.nextInt(4) == 0) {
				altColor = rand.nextBoolean() ? 0xFF7777 : 0x7777FF;
			}

			explosion.setIntArray("Colors", new int[] { mainColor, altColor });

			if (rand.nextBoolean()) {
				int fadeColor = rand.nextInt(3) == 0 ? 0xFFFFFF : shiftBrightness(color, 0.4F + rand.nextFloat() * 0.8F);
				explosion.setIntArray("FadeColors", new int[] { fadeColor });
			}

			explosionList.appendTag(explosion);
		}

		fireworks.setTag("Explosions", explosionList);
		fireworks.setByte("Flight", (byte) (1 + rand.nextInt(3)));
		tag.setTag("Fireworks", fireworks);
		stack.setTagCompound(tag);

		return stack;
	}

	public static int shiftBrightness(int color, float mult) {
		int r = (color >> 16) & 255;
		int g = (color >> 8) & 255;
		int b = color & 255;

		r = Math.min(255, Math.max(0, (int) (r * mult)));
		g = Math.min(255, Math.max(0, (int) (g * mult)));
		b = Math.min(255, Math.max(0, (int) (b * mult)));

		return (r << 16) | (g << 8) | b;
	}

	@Override
	public void onLaunch(EntityProjectileBase projectileBase, World world, @Nullable EntityLivingBase shooter) {
		NBTTagCompound celebratoryData = projectileBase.tinkerProjectile.getItemStack().getOrCreateSubCompound(this.getModifierIdentifier());
		if (celebratoryData.getInteger("CelebratoryCooldown") <= 0 || celebratoryData.getInteger("CelebratoryCooldown") == 40) {
			projectileBase.getTags().add("CelebratoryBoom");
		}
	}

	@Override
	public void onAmmoUsed(ItemStack ammo, @Nullable EntityLivingBase entity, boolean consumed) {
		NBTTagCompound celebratoryData = ammo.getOrCreateSubCompound(this.getModifierIdentifier());
		if (celebratoryData.getInteger("CelebratoryCooldown") <= 0) {
			celebratoryData.setInteger("CelebratoryCooldown", 40);
		}
	}

	@Override
	public void onMovement(EntityProjectileBase projectile, World world, double slowdown) {
	}

	@Override
	public void afterHit(EntityProjectileBase projectile, World world, ItemStack ammoStack, EntityLivingBase attacker, Entity target, double impactSpeed) {
		if (!(projectile instanceof EntityBomb) && !world.isRemote) {
			if (!projectile.getTags().contains("CelebratoryBoom")) {
				return;
			}

			explode(ammoStack, world, attacker == null ? null : attacker.getUniqueID(), target, null, true);
		}
	}
}

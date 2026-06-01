package com.existingeevee.moretcon.mixin.softdep.thebetweenlands;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.existingeevee.moretcon.other.DamageScalar;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ranged.ILauncher;
import slimeknights.tconstruct.library.tools.ranged.IProjectile;
import slimeknights.tconstruct.library.utils.ToolHelper;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWorm;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWormHelper;

@Mixin(EntityTinySludgeWorm.class)
public abstract class MixinEntityTinySludgeWorm extends Entity {
	@Unique
	private static final GameProfile SL_WORM_PROFILE = new GameProfile(UUID.fromString("b8c3a22e-1316-4b63-9e25-37fd3b7e9a11"), "Gas Cloud");
	@Unique
	private static final UUID POWER_MODIFIER = UUID.fromString("c6aefc22-081a-4c4a-b076-8f9d6cef9122");

	public MixinEntityTinySludgeWorm(World worldIn) {
		super(worldIn);
	}

	@Inject(method = "attackEntityAsMob", at = @At(value = "HEAD"), cancellable = true)
	public void moretcon$HEAD_Inject$attackEntityAsMob(Entity entity, CallbackInfoReturnable<Boolean> ci) {
		EntityTinySludgeWorm $this = (EntityTinySludgeWorm) (Object) this;
		if ($this.canEntityBeSeen(entity) && entity.onGround && $this instanceof EntityTinySludgeWormHelper && entity instanceof EntityLivingBase) {
			EntityTinySludgeWormHelper worm = (EntityTinySludgeWormHelper) $this;
			if (worm.getOwner() instanceof EntityPlayer && worm.getEntityData().getBoolean("moretcon.wormed")) {
				ItemStack stack = new ItemStack(worm.getEntityData().getCompoundTag("moretcon.wormed.tool"));				
				ci.setReturnValue(attackEntityCustom(worm, (EntityLivingBase) entity, stack));
			}
		}
	}
	
	@Unique
	private static boolean attackEntityCustom(EntityTinySludgeWorm worm, EntityLivingBase entity, ItemStack stack) {
		if (!entity.world.isRemote && entity.world instanceof WorldServer) {
			WorldServer world = (WorldServer) entity.world;

			FakePlayer fp = FakePlayerFactory.get(world, SL_WORM_PROFILE);

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

			DamageScalar.push(0.5f);

			boolean hit = false;
			
			try {
				if (stack.getItem() instanceof ToolCore) {
					fp.resetCooldown();
					hit = ToolHelper.attackEntity(stack, (ToolCore) stack.getItem(), fp, entity, null, false);
				} else {
					fp.attackTargetEntityWithCurrentItem(entity);
					hit = true; //ig?
				}
			} catch (Exception e) {
				// even if a buggy hit happens i think its okay to skip it.
			} finally {
				if (attr != null)
					fp.getAttributeMap().removeAttributeModifiers(attr);
			}
			entity.setRevengeTarget(worm);
			DamageScalar.pop();
			return hit;
		}
		return false;
	}

	@Unique
	private static void unequip(EntityLivingBase entity, EntityEquipmentSlot slot) {
		ItemStack stack = entity.getItemStackFromSlot(slot);
		if (!stack.isEmpty()) {
			entity.getAttributeMap().removeAttributeModifiers(stack.getAttributeModifiers(slot));
		}
	}

	@Unique
	private static void equip(EntityLivingBase entity, EntityEquipmentSlot slot) {
		ItemStack stack = entity.getItemStackFromSlot(slot);
		if (!stack.isEmpty()) {
			entity.getAttributeMap().applyAttributeModifiers(stack.getAttributeModifiers(slot));
		}
	}
}

package com.existingeevee.moretcon.traits.traits.unique;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.existingeevee.moretcon.other.OverrideItemUseEvent;
import com.existingeevee.moretcon.other.utils.ArrowReferenceHelper;
import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.other.utils.ReequipHack;
import com.existingeevee.moretcon.traits.traits.abst.NumberTrackerTrait;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.registries.SoundRegistry;

public class Shockwaving extends NumberTrackerTrait {

	protected static Shockwaving instance;

	public Shockwaving() {
		super(MiscUtils.createNonConflictiveName("shockwaving"), 0x0066ff);
		ReequipHack.registerIgnoredKey(this.getModifierIdentifier());

		MinecraftForge.EVENT_BUS.register(this);
		instance = this;
	}

	@SubscribeEvent
	public void onRightClick(OverrideItemUseEvent event) {
		ItemStack stack = event.getItemStack();

		if (!isToolWithTrait(stack) || ToolHelper.isBroken(stack)) {
			return;
		}

		NBTTagCompound itemTag = stack.getOrCreateSubCompound(this.getModifierIdentifier());

		if (ToolHelper.isBroken(stack)) {
			itemTag.setInteger("cooldown", 0);
			event.setResult(Result.DENY);
			return;
		}

		if (this.getNumber(stack) > 0) {
			if (!event.getWorld().isRemote) {
				stack.damageItem(2, event.getEntityPlayer());
				event.getWorld().playSound(null, event.getEntityPlayer().posX, event.getEntityPlayer().posY,
						event.getEntityPlayer().posZ, SoundRegistry.SHOCKWAVE_SWORD, SoundCategory.BLOCKS, 1.25F,
						1.0F + event.getWorld().rand.nextFloat() * 0.1F);
				double direction = Math.toRadians(event.getEntityPlayer().rotationYaw);
				Vec3d diag = new Vec3d(Math.sin(direction + Math.PI / 2.0D), 0, Math.cos(direction + Math.PI / 2.0D))
						.normalize();
				List<BlockPos> spawnedPos = new ArrayList<>();
				for (int distance = -1; distance <= 16; distance++) {
					for (int distance2 = -distance; distance2 <= distance; distance2++) {
						for (int yo = -1; yo <= 1; yo++) {
							int originX = MathHelper.floor(event.getPos().getX() + 0.5D - Math.sin(direction) * distance
									- diag.x * distance2 * 0.25D);
							int originY = event.getPos().getY() + yo;
							int originZ = MathHelper.floor(event.getPos().getZ() + 0.5D + Math.cos(direction) * distance
									+ diag.z * distance2 * 0.25D);
							BlockPos origin = new BlockPos(originX, originY, originZ);

							if (spawnedPos.contains(origin)) {
								continue;
							}

							spawnedPos.add(origin);

							IBlockState block = event.getWorld().getBlockState(new BlockPos(originX, originY, originZ));

							if (block.isNormalCube() && !block.getBlock().hasTileEntity(block)
									&& block.getBlockHardness(event.getWorld(), origin) <= 5.0F
									&& block.getBlockHardness(event.getWorld(), origin) >= 0.0F
									&& !event.getWorld().getBlockState(origin.up()).isOpaqueCube()) {
								itemTag.setInteger("blockID",
										Block.getIdFromBlock(event.getWorld().getBlockState(origin).getBlock()));
								itemTag.setInteger("blockMeta", event.getWorld().getBlockState(origin)
										.getBlock().getMetaFromState(event.getWorld().getBlockState(origin)));

								EntityShockwaveBlock shockwaveBlock = new EntityShockwaveBlock(event.getWorld()); // ToolHelper
								shockwaveBlock.setOrigin(origin,
										MathHelper.floor(Math.sqrt(distance * distance + distance2 * distance2)),
										event.getPos().getX() + 0.5D, event.getPos().getZ() + 0.5D,
										event.getEntityPlayer());
								shockwaveBlock.setLocationAndAngles(originX + 0.5D, originY, originZ + 0.5D, 0.0F,
										0.0F);
								shockwaveBlock.setBlock(
										Block.getBlockById(itemTag.getInteger("blockID")),
										itemTag.getInteger("blockMeta"));

								// Custom bit of extra data to store in info about the tool :3
								NBTTagCompound data = shockwaveBlock.getEntityData().getCompoundTag(this.getModifierIdentifier());
								data.setTag("Tool", stack.serializeNBT());
								shockwaveBlock.getEntityData().setTag(this.getModifierIdentifier(), data);

								event.getWorld().spawnEntity(shockwaveBlock);
								break;
							}
						}
					}
				}

				this.removeNumber(stack, 1);
				
				if (this.getNumber(stack) <= 0) {
					this.setNumber(stack, 0);
					itemTag.setInteger("cooldown", 0);
				}
			}
			event.getEntityPlayer().swingArm(event.getHand());
			event.setResult(Result.ALLOW);
			return;
		}
		
		event.setResult(Result.DENY);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		NBTTagCompound itemTag = stack.getOrCreateSubCompound(this.getModifierIdentifier());

		if (!itemTag.hasKey("cooldown")) {
			itemTag.setInteger("cooldown", 0);
		}

		if (this.getNumber(stack) <= 0) {
			if (itemTag.getInteger("cooldown") < 60) {
				itemTag.setInteger("cooldown", itemTag.getInteger("cooldown") + 1);
				if (isSelected) {
					world.spawnParticle(EnumParticleTypes.REDSTONE, entity.posX + random.nextDouble() - 0.5, entity.posY + random.nextDouble() * 2, entity.posZ + random.nextDouble() - 0.5, 0.0001, 1, 1);
				}
			}
			if (itemTag.getInteger("cooldown") >= 60) {
				itemTag.setInteger("cooldown", 60);
				this.setNumber(stack, this.getNumberMax(stack));
				for (int i = 0; i < 20; i++)
					world.spawnParticle(EnumParticleTypes.END_ROD, entity.posX, entity.posY + 1, entity.posZ, 0.25 * MiscUtils.randomN1T1(), 0.25 * MiscUtils.randomN1T1(), 0.25 * MiscUtils.randomN1T1());
			}
		}
	}

	@Override
	public int getNumberMax(ItemStack stack) {
		return 3;
	}

	@Override
	public int getNumber(ItemStack stack) {
		return super.getNumber(stack);
	}

	@Override
	public int setNumber(ItemStack stack, int amount) {
		return super.setNumber(stack, amount);
	}

	public static boolean shouldHandle(EntityShockwaveBlock shockwave) {
		if (instance == null)
			return false;

		if (!shockwave.getEntityData().getCompoundTag(instance.getModifierIdentifier()).hasKey("Tool", NBT.TAG_COMPOUND))
			return false;

		ItemStack stack = new ItemStack(shockwave.getEntityData().getCompoundTag(instance.getModifierIdentifier()).getCompoundTag("Tool"));

		return stack.getItem() instanceof ToolCore;
	}

	protected static final AttributeModifier MODIFIER = new AttributeModifier(UUID.fromString("aeee8ee7-f22f-0100-9389-005f97370738"), "atk dmg modifier", 4, 0);

	public static boolean handle(EntityShockwaveBlock shockwave, EntityLivingBase target, DamageSource source, float damage) {
		ItemStack stack = new ItemStack(shockwave.getEntityData().getCompoundTag(instance.getModifierIdentifier()).getCompoundTag("Tool"));
		EntityLivingBase attacker = null;

		if (shockwave.getOwner() instanceof EntityLivingBase) {
			attacker = (EntityLivingBase) shockwave.getOwner();
		}

		if (attacker != null && !target.world.isRemote) {
			unequip(attacker, EntityEquipmentSlot.OFFHAND);
			unequip(attacker, EntityEquipmentSlot.MAINHAND);

			Multimap<String, AttributeModifier> projectileAttributes = stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
			projectileAttributes.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), MODIFIER);

			attacker.getAttributeMap().applyAttributeModifiers(projectileAttributes);

			boolean onGround = attacker.onGround;
			attacker.onGround = false; // no sweep attack grr

			ItemStack linked = ArrowReferenceHelper.getLinkedItemstackFromInventory(stack, attacker);
			if (!linked.isEmpty())
				stack = linked;
			int atk = ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, attacker, "field_184617_aD");

			ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, attacker, Integer.MAX_VALUE, "field_184617_aD");
			boolean ret = ToolHelper.attackEntity(stack, (ToolCore) stack.getItem(), attacker, target, shockwave, false);

			ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, attacker, atk, "field_184617_aD");

			attacker.onGround = onGround;

			attacker.getAttributeMap().removeAttributeModifiers(projectileAttributes);

			equip(attacker, EntityEquipmentSlot.MAINHAND);
			equip(attacker, EntityEquipmentSlot.OFFHAND);

			return ret; // EntityBolt
		}

		return false;
	}

	private static void unequip(EntityLivingBase entity, EntityEquipmentSlot slot) {
		ItemStack stack = entity.getItemStackFromSlot(slot);
		if (!stack.isEmpty()) {
			entity.getAttributeMap().removeAttributeModifiers(stack.getAttributeModifiers(slot));
		}
	}

	private static void equip(EntityLivingBase entity, EntityEquipmentSlot slot) {
		ItemStack stack = entity.getItemStackFromSlot(slot);
		if (!stack.isEmpty()) {
			entity.getAttributeMap().applyAttributeModifiers(stack.getAttributeModifiers(slot));
		}
	}
}
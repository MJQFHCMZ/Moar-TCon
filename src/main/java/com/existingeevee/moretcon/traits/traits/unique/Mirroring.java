package com.existingeevee.moretcon.traits.traits.unique;

import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.traits.traits.abst.IAdditionalTraitMethods;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.capability.projectile.TinkerProjectileHandler;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.traits.AbstractProjectileTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class Mirroring extends AbstractProjectileTrait implements IAdditionalTraitMethods {

	public Mirroring() {
		super(MiscUtils.createNonConflictiveName("mirroring"), 0);
	}

	@Override
	public void onProjectileUpdate(EntityProjectileBase entity, World world, ItemStack toolStat) {
		if (entity.world.isRemote) {
			Vec3d position = entity.getPositionVector();
			boolean inGround = entity.inGround;
			int particles = Math.min(4, inGround ? 1 : (int) new Vec3d(entity.motionX, entity.motionY, entity.motionZ).lengthSquared() + 1);
			for (int i = 0; i < particles; i++) {
				entity.world.spawnParticle(EnumParticleTypes.REDSTONE, true, position.x + (random.nextDouble() * 0.5 - 0.25), (position.y + (random.nextDouble() * 0.5 - 0.25)) - 0.05, position.z + (random.nextDouble() * 0.5 - 0.25), 0, 1, 1);
			}
		}
	}

	@Override
	public boolean isToolWithTrait(ItemStack itemStack) {
		return super.isToolWithTrait(itemStack);
	}

	@Override
	public boolean modifyProjectileParent(ItemStack launchingStack, ItemStack parent, ItemStack copy, TinkerProjectileHandler tinkerProjectileHandler) {
		boolean modified = false; // ToolCore

		NBTTagList tagList = TagUtil.getModifiersTagList(parent);

		NBTTagList launcherModifiers = TagUtil.getModifiersTagList(launchingStack);
		for (int i = 0; i < launcherModifiers.tagCount(); i++) {
			NBTTagCompound tag = launcherModifiers.getCompoundTagAt(i);

			ModifierNBT data = new ModifierNBT(tag);
			String identifier = data.identifier;

			// get matching modifier
			IModifier modifier = TinkerRegistry.getModifier(identifier);
			if (modifier == null) {
				continue;
			}

			NBTTagCompound toWrite = TinkerUtil.getModifierTag(parent, identifier);

			boolean isNew = toWrite.getKeySet().isEmpty();

			for (String key : tag.getKeySet()) {
				if (key.equals("level"))
					toWrite.setInteger(key, toWrite.getInteger(key) + tag.getInteger(key));

				if (key.equals("current"))
					toWrite.setInteger(key, toWrite.getInteger(key) + tag.getInteger(key));

				if (!toWrite.hasKey(key))
					toWrite.setTag(key, tag.getTag(key));
			}

			if (isNew) {
				tagList.appendTag(toWrite);
			}

			modifier.apply(parent);
			modified = true;
			
		}


		return modified;
	}
}
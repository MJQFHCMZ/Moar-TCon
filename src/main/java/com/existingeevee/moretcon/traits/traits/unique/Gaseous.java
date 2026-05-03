package com.existingeevee.moretcon.traits.traits.unique;

import com.existingeevee.moretcon.entity.entities.EntityBomb;
import com.existingeevee.moretcon.entity.entities.EntityGasCloud;
import com.existingeevee.moretcon.item.tooltypes.Bomb.BombNBT;
import com.existingeevee.moretcon.other.DamageScalar;
import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.traits.traits.abst.IBombTrait;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

public class Gaseous extends AbstractTrait implements IBombTrait {
	
	public static final ThreadLocal<Boolean> GAS = ThreadLocal.withInitial(() -> false);
	
	public Gaseous() {
		super(MiscUtils.createNonConflictiveName("gaseous"), 0xffffff);
	}

	@Override
	public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
		super.applyEffect(rootCompound, modifierTag);
		NBTTagCompound tag = TagUtil.getToolTag(rootCompound);
		if (tag.hasKey("FuseTime", NBT.TAG_INT)) {
			BombNBT nbt = new BombNBT(TagUtil.getToolTag(rootCompound));
			nbt.radius += 2;
			TagUtil.setToolTag(rootCompound, nbt.get());
		}
	}
	
	@Override
	public void onDetonate(ItemStack tool, World world, EntityBomb bomb, EntityLivingBase attacker) {
		if (world.isRemote)
			return;

		EntityGasCloud gas = new EntityGasCloud(world, bomb.posX, bomb.posY, bomb.posZ, (float) new BombNBT(TagUtil.getToolTag(tool)).radius, tool, attacker);
		world.spawnEntity(gas);
	}

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		if (!GAS.get())
			DamageScalar.push(0.5f);
		return newDamage;
	}

	@Override
	public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
		if (!GAS.get())
			DamageScalar.pop();
	}

}
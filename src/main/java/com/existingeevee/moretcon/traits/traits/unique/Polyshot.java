package com.existingeevee.moretcon.traits.traits.unique;

import com.existingeevee.moretcon.traits.ModTraits;
import com.existingeevee.moretcon.traits.traits.abst.DummyTrait;
import com.existingeevee.moretcon.traits.traits.abst.IAdditionalTraitMethods;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.library.capability.projectile.TinkerProjectileHandler;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class Polyshot extends DummyTrait implements IAdditionalTraitMethods {

	public Polyshot() {
		super("polyshot", 0x545454);
		MinecraftForge.EVENT_BUS.register(this); 
	}

	@Override
	public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
		super.applyEffect(rootCompound, modifierTag);
		if (TinkerUtil.hasCategory(rootCompound, Category.LAUNCHER)) {
			ProjectileLauncherNBT launcherData = new ProjectileLauncherNBT(TagUtil.getToolTag(rootCompound));
			launcherData.drawSpeed -= 0.25;
			TagUtil.setToolTag(rootCompound, launcherData.get());
		}
	} 
	
	@Override
	public boolean modifyLauncherProjectile(ItemStack launchingStack, ItemStack parent, ItemStack copy, TinkerProjectileHandler tinkerProjectileHandler) {
		ModTraits.polyshotProj.apply(parent);
		return true;
	}
}
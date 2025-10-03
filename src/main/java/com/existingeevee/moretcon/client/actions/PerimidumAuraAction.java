package com.existingeevee.moretcon.client.actions;

import com.existingeevee.moretcon.client.particle.ParticlePerimidumAura;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PerimidumAuraAction extends ClientAction {

	public static final PerimidumAuraAction INSTANCE = new PerimidumAuraAction();

	private PerimidumAuraAction() {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void runAsClient(World world, double x, double y, double z, NBTBase data) {
		int amount = 7;
		double height = 4;
		if (data instanceof NBTTagCompound) {
			if (((NBTTagCompound) data).hasKey("Amount", NBT.TAG_ANY_NUMERIC))
				amount = ((NBTTagCompound) data).getInteger("Amount");
			if (((NBTTagCompound) data).hasKey("Height", NBT.TAG_ANY_NUMERIC))
				height = ((NBTTagCompound) data).getDouble("Height");
		}
		if (amount > 32) {
			amount = 32;
		}
		if (height > 8) {
			height = 8;
		}
		Minecraft.getMinecraft().effectRenderer.addEffect(new ParticlePerimidumAura(world, x, y, z, Math.random() * amount / 8, height, amount));
	}
}

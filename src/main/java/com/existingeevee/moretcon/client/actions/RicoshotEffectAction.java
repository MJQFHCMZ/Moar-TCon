package com.existingeevee.moretcon.client.actions;

import com.existingeevee.moretcon.client.particle.ParticleRicoshotBeam;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RicoshotEffectAction extends ClientAction {

	public static final RicoshotEffectAction INSTANCE = new RicoshotEffectAction();

	private RicoshotEffectAction() {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void runAsClient(World world, double x, double y, double z, NBTBase data) {
		if (data instanceof NBTTagCompound) {
			double endX = ((NBTTagCompound) data).getDouble("endX");
			double endY = ((NBTTagCompound) data).getDouble("endY");
			double endZ = ((NBTTagCompound) data).getDouble("endZ");
			int color = ((NBTTagCompound) data).getInteger("color");

			ParticleRicoshotBeam particle = new ParticleRicoshotBeam(world, x, y, z, endX, endY, endZ, color);
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		}
	}

	public static NBTTagCompound encode(double endX, double endY, double endZ, int color) {
		NBTTagCompound ret = new NBTTagCompound();
		ret.setDouble("endX", endX);
		ret.setDouble("endY", endY);
		ret.setDouble("endZ", endZ);
		ret.setInteger("color", color);
		return ret;
	}
}

package com.existingeevee.moretcon.client.actions;

import java.lang.reflect.Constructor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRedstone;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.client.RenderUtil;

public class ColoredDustAction extends ClientAction {

	public static final ColoredDustAction INSTANCE = new ColoredDustAction();

	private ColoredDustAction() {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void runAsClient(World world, double x, double y, double z, NBTBase data) {
		int color = 0xffffff;
		if (data instanceof NBTTagInt) {
			color = ((NBTTagInt) data).getInt();
		}

		float r = RenderUtil.red(color) / 255f;
		float g = RenderUtil.green(color) / 255f;
		float b = RenderUtil.blue(color) / 255f;

		try {
			Constructor<ParticleRedstone> constructor = ParticleRedstone.class.getDeclaredConstructor(World.class, double.class, double.class, double.class, float.class, float.class, float.class, float.class);
			constructor.setAccessible(true);
			
			ParticleRedstone particle = constructor.newInstance(world, x, y, z, (float) (0.5 + 0.25 * Math.random()), r + 0.00001f, g, b);
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

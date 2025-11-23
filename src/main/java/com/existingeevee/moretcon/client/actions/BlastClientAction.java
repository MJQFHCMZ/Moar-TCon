package com.existingeevee.moretcon.client.actions;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleExplosionLarge;
import net.minecraft.client.particle.ParticleLava;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlastClientAction extends ClientAction {

	public static final BlastClientAction INSTANCE = new BlastClientAction();

	@SideOnly(Side.CLIENT)
	private static class ClientStuff {
		private static final ParticleSmokeNormal.Factory SMOKE_FACTORY = new ParticleSmokeNormal.Factory();
		private static final ParticleLava.Factory LAVA_FACTORY = new ParticleLava.Factory();
		private static final ParticleExplosionLarge.Factory EXPL_FACTORY = new ParticleExplosionLarge.Factory();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void runAsClient(World world, double x, double y, double z, NBTBase data) {
		double dx = 0, dy = 0, dz = 0;

		if (data instanceof NBTTagCompound) {
			NBTTagCompound nbt = (NBTTagCompound) data;
			dx = nbt.getDouble("dx");
			dy = nbt.getDouble("dy");
			dz = nbt.getDouble("dz");
		}
		
		Particle explosion = ClientStuff.EXPL_FACTORY.createParticle(0, world, x + dx * 0.3, y + dy * 0.3, z + dz * 0.3, 1.75, 0, 0);
		Minecraft.getMinecraft().effectRenderer.addEffect(explosion);
		for (int i = 0; i < 10; i++) {
			Minecraft.getMinecraft().effectRenderer.addEffect(ClientStuff.SMOKE_FACTORY.createParticle(0, world, x, y, z, genRandMot() + dx / 2, genRandMot() + dy / 2, genRandMot() + dz / 2));

			Particle lava = ClientStuff.LAVA_FACTORY.createParticle(0, world, x, y, z, 0, 0, 0);
			
			ObfuscationReflectionHelper.setPrivateValue(Particle.class, lava, genRandMot() + dx, "field_187129_i"); //motionX
			ObfuscationReflectionHelper.setPrivateValue(Particle.class, lava, genRandMot() + dy, "field_187130_j"); //motionX
			ObfuscationReflectionHelper.setPrivateValue(Particle.class, lava, genRandMot() + dz, "field_187131_k"); //motionX
			
			Minecraft.getMinecraft().effectRenderer.addEffect(lava);
		}
	}

	private static final Random rand = new Random();

	public static double genRandMot() {
		return 0.1 * (rand.nextGaussian() * 2 - 1);
	}

	public static NBTBase fromVec3d(Vec3d vec) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("dx", vec.x);
		nbt.setDouble("dy", vec.y);
		nbt.setDouble("dz", vec.z);
		return nbt;
	}

}

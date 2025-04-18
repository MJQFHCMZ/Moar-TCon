package com.existingeevee.moretcon.client.actions;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleBlockDust;
import net.minecraft.client.particle.ParticleExplosionLarge;
import net.minecraft.client.particle.ParticleFallingDust;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;

public class GlacialParticleAction extends ClientAction {

	public static final GlacialParticleAction INSTANCE = new GlacialParticleAction();

	private static class ClientStuff {
		private static final ParticleExplosionLarge.Factory EXPL_FACTORY = new ParticleExplosionLarge.Factory();
	}

	@Override
	public void runAsClient(World world, double x, double y, double z, NBTBase data) {
		for (int i = 0; i < 50; i++) {
			float randMotX = (float) (2 * (Math.random() - 0.5));
			float randMotY = (float) (2 * (Math.random() - 0.5));
			float randMotZ = (float) (2 * (Math.random() - 0.5));

			switch (world.rand.nextInt(6)) {
			case 0: 
				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBlockDust.Factory().createParticle(0, world, x, y, z, randMotX * 0.5f, randMotY * 0.5f, randMotZ * 0.5f, Block.getStateId(Blocks.ICE.getDefaultState())));
				break;
			case 1: 
				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBlockDust.Factory().createParticle(0, world, x, y, z, randMotX * 0.5f, randMotY * 0.5f, randMotZ * 0.5f, Block.getStateId(Blocks.SNOW.getDefaultState())));
				break;
			default:
				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleFallingDust.Factory().createParticle(0, world, x + randMotX * 1.5, y + randMotY * 1.5, z + randMotZ * 1.5, 0, 0, 0, Block.getStateId(Blocks.SNOW.getDefaultState())));
				break;
			}
		}
		Minecraft.getMinecraft().effectRenderer.addEffect(ClientStuff.EXPL_FACTORY.createParticle(0, world, x, y, z, 5, 0, 0));
	}

}

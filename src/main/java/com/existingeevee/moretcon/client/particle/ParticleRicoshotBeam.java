package com.existingeevee.moretcon.client.particle;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticleRicoshotBeam extends Particle {
	
	double endX, endY, endZ;

	Color dark, light;
	
	public ParticleRicoshotBeam(World worldIn, double x, double y, double z, double endX, double endY, double endZ, int color) {
		super(worldIn, x, y, z, 0, 0, 0);
		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;
		
		this.particleMaxAge = 7;

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		
		light = new Color(color);
	}

	@Override
	public int getFXLayer() {
		return 3;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 0, 0);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		
		float interpAge = this.particleAge - partialTicks;
		
		GlStateManager.color(light.getRed() / 255f, light.getGreen() / 255f, light.getBlue() / 255f,  1f - interpAge * 1f / this.particleMaxAge);
		
        bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		GL11.glLineWidth(5);
		
		float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
		float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
		float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        
		float xE = (float) (endX - interpPosX);
		float yE = (float) (endY - interpPosY);
		float zE = (float) (endZ - interpPosZ);
				
		
        bufferbuilder.pos(x, y, z).endVertex();
        bufferbuilder.pos(xE, yE, zE).endVertex();

		Tessellator.getInstance().draw();

        //bufferbuilder.pos(x, y, z).color(dark.getRed(), dark.getGreen(), dark.getBlue(), 1.0F).endVertex();
        //bufferbuilder.pos(xE, yE, zE).color(light.getRed(), light.getGreen(), light.getBlue(), 1.0F).endVertex();
        
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);
		GlStateManager.color(1, 1, 1, 1);
		GL11.glLineWidth(1);
	}
}

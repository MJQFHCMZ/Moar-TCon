package com.existingeevee.moretcon.client.particle;

import com.existingeevee.moretcon.ModInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticlePerimidumAura extends Particle {

	private static final ResourceLocation AURA = new ResourceLocation(ModInfo.MODID, "textures/other/particle/perimidum_aura.png");

	double rOff = 0;

	double radius = 5;
	
	double height = 3;
	int amount = 7;
	
	public ParticlePerimidumAura(World worldIn, double x, double y, double z, double radiusBonus, double height, int amount) {
		super(worldIn, x, y, z, 0, 0, 0);
		this.particleMaxAge = 20 + (int) (Math.random() * 20);

		this.rOff = Math.random() * 20;

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	
		this.radius = radiusBonus;
		
		this.height = height;
		this.amount = amount;
	}

	@Override
	public int getFXLayer() {
		return 3;
	}

	@Override
	public int getBrightnessForRender(float partialTick) {
		float f = 1;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		int i = 10;
		int j = i & 255;
		int k = i >> 16 & 255;
		j = j + (int) (f * 15.0F * 16.0F);

		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		try {
			double interpEntPosX = entityIn.prevPosX + partialTicks * (entityIn.posX - entityIn.prevPosX);
			double interpEntPosY = entityIn.prevPosY + partialTicks * (entityIn.posY - entityIn.prevPosY);
			double interpEntPosZ = entityIn.prevPosZ + partialTicks * (entityIn.posZ - entityIn.prevPosZ);

			double interpParPosX = prevPosX + partialTicks * (posX - prevPosX);
			double interpParPosY = prevPosY + partialTicks * (posY - prevPosY);
			double interpParPosZ = prevPosZ + partialTicks * (posZ - prevPosZ);

			double dx = interpParPosX - interpEntPosX;
			double dy = interpParPosY - interpEntPosY;
			double dz = interpParPosZ - interpEntPosZ;

			double ageRatio = (this.particleAge + partialTicks) / this.particleMaxAge;
			double ageRatioInv = 1 - (this.particleAge + partialTicks) / this.particleMaxAge;
			double mult = Math.max(0, ageRatioInv * ageRatioInv);
			double size = 2 * radius * ageRatio + 12 * ageRatio * ageRatioInv;
			//double sizeShockwave = 2 * radius + 100 * ageRatio;

			int lightCoords = this.getBrightnessForRender(partialTicks);
			
			Minecraft.getMinecraft().getTextureManager().bindTexture(AURA);
			renderPillar(dx, dy + Math.exp(2 * ageRatio) * ((height + 1) / 7), dz, partialTicks, posX, posY, posZ, this.particleAge, 0.6, 0.6 * size, 0, lightCoords, (float) mult, rOff);

			//renderPillar(dx, dy, dz, partialTicks, posX, 100, posZ, this.particleAge, 1, 0.6 * sizeShockwave, 0, lightCoords, (float) mult, rOff);

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void renderPillar(double dx, double dy, double dz, float partial, double posX1, double posY1, double posZ1, int age, double speedMult, double radMult, double posY2, int lightCoord, float alpha, double rOffset) {
		float length = (float) (posY2 - posY1);

		int lj = lightCoord >> 16 & 65535;
		int lk = lightCoord & 65535;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(516, 0.003921569F);
		GlStateManager.disableFog();
		
		float f5 = (float) -((age + partial + rOffset) * 0.01F * speedMult);
		float f6 = (float) (length / 32.0F / 6 - (age + partial + rOffset) * 0.01F * speedMult);

		bufferbuilder.begin(5, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		final float i = 100; 
		
		for (int j = 0; j <= i; ++j) {
			float cirX = MathHelper.sin(j % i * ((float) Math.PI * 2F) / i) * 0.75F;
			float cirZ = MathHelper.cos(j % i * ((float) Math.PI * 2F) / i) * 0.75F;

			float f9 = j % i / i;
			bufferbuilder.pos(dx + cirX * radMult, dy - 2, dz + cirZ * radMult).tex(f9, f5).color(1f, 1f, 1f, alpha).lightmap(lj, lk).endVertex();
			bufferbuilder.pos(dx + cirX * radMult, dy - 2 + (2 + height) * (this.particleAge + partial) / this.particleMaxAge, dz + cirZ * radMult).tex(f9, f6).color(1f, 1f, 1f, alpha).lightmap(lj, lk).endVertex();
		}

		tessellator.draw();

		GlStateManager.enableFog();
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(516, 0.1F);

	}
}

package com.existingeevee.moretcon.entity.renderers;

import org.lwjgl.opengl.GL11;

import com.existingeevee.moretcon.entity.entities.EntityGasCloud;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderGasCloud extends Render<EntityGasCloud> {

	public RenderGasCloud(RenderManager renderManager) {
		super(renderManager);
		shadowSize = 0.0F;
	}

	@Override
	public boolean shouldRender(EntityGasCloud livingEntity, ICamera camera, double camX, double camY, double camZ) {
		return true;
	}

	@Override
	public void doRender(EntityGasCloud entity, double x, double y, double z, float entityYaw, float partialTicks) {
		try {
			GlStateManager.pushMatrix();

			GlStateManager.translate(x, y, z);

			float radius = entity.getRadiusInterp(partialTicks);

			GlStateManager.disableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			GlStateManager.depthMask(false);

			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			float m = 1;
			if (entity.isDefused()) {
				m = (entity.getAge() - entity.getDuration() + partialTicks) / 20f;
				m = 1 - Math.max(0, m);
			}

			GlStateManager.color(0.0F, 0.58F, 0.0F, 0.28F * m);
			GlStateManager.pushMatrix();
			GlStateManager.scale(radius, radius, radius);
			renderSphere(24, 16);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(-radius, -radius, -radius);
			renderSphere(24, 16);
			GlStateManager.popMatrix();
			GlStateManager.depthMask(true);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.enableTexture2D();

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		} catch (Exception e) {

		}
	}

	private void renderSphere(int slices, int stacks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		for (int i = 0; i < stacks; i++) {
			double lat0 = Math.PI * (-0.5D + (double) i / stacks);
			double lat1 = Math.PI * (-0.5D + (double) (i + 1) / stacks);

			double y0 = Math.sin(lat0);
			double r0 = Math.cos(lat0);
			double y1 = Math.sin(lat1);
			double r1 = Math.cos(lat1);

			buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION);

			for (int j = 0; j <= slices; j++) {
				double lon = 2.0D * Math.PI * (double) j / slices;
				double x = Math.cos(lon);
				double z = Math.sin(lon);

				buffer.pos(x * r0, y0, z * r0).endVertex();
				buffer.pos(x * r1, y1, z * r1).endVertex();
			}

			tessellator.draw();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGasCloud entity) {
		return null;
	}
}
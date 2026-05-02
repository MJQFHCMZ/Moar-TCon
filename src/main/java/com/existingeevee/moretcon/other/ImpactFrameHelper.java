package com.existingeevee.moretcon.other;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.existingeevee.moretcon.ModInfo;
import com.existingeevee.moretcon.MoreTCon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ImpactFrameHelper {

	public static final ImpactFrameHelper INSTANCE = MoreTCon.proxy.isClient() ? new ImpactFrameHelperClient() : new ImpactFrameHelper();

	public void initiateImpactFrame(int timeMs, boolean red) {
	}

	public static void init() {
		//empty method to init the class
	}
	
	public static class ImpactFrameHelperClient extends ImpactFrameHelper {

		private ImpactFrameHelperClient() {
			MinecraftForge.EVENT_BUS.register(this);
		}

		private DynamicTexture impactTexture;
		private ResourceLocation impactTextureLocation;

		private int impactWidth;
		private int impactHeight;

		private long timeHit = -1;

		private long queueTime = -1;
		private boolean shouldQueueRed = true;
		
		private static boolean preheated = false;
		
		@Override
		public void initiateImpactFrame(int timeMs, boolean red) {
			this.shouldQueueRed = red;
			this.queueTime = System.currentTimeMillis() + timeMs;
		}
		
		private void captureImpactFrame(boolean red) {
			if (!Minecraft.getMinecraft().isCallingFromMinecraftThread())
				return; //nope
			
			Minecraft mc = Minecraft.getMinecraft();

			timeHit = System.currentTimeMillis();

			Framebuffer fb = mc.getFramebuffer();

			int scale = 3;

			int fullWidth = mc.displayWidth;
			int fullHeight = mc.displayHeight;

			impactWidth = fullWidth / scale;
			impactHeight = fullHeight / scale;

			IntBuffer pixels = BufferUtils.createIntBuffer(fullWidth * fullHeight);

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, fb.framebufferTexture);
			GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixels);

			int[] fullData = new int[fullWidth * fullHeight];
			int[] data = new int[impactWidth * impactHeight];

			pixels.get(fullData);

			for (int y = 0; y < impactHeight; y++) {
				for (int x = 0; x < impactWidth; x++) {
					int srcX = x * scale;
					int srcY = y * scale;
					int src = srcY * fullWidth + srcX;
					int dst = y * impactWidth + x;

					data[dst] = fullData[src];
				}
			}

			applyImpactFrameEffect(data, red);

			clearOldImpactFrameTexture();

			impactTexture = new DynamicTexture(impactWidth, impactHeight);

			int[] texData = impactTexture.getTextureData();
			System.arraycopy(data, 0, texData, 0, data.length);
			impactTexture.updateDynamicTexture();

			impactTextureLocation = mc.getTextureManager().getDynamicTextureLocation(ModInfo.MODID + "_impact_frame", impactTexture);
		}

		private void applyImpactFrameEffect(int[] data, boolean red) {
			int size = data.length;
			
			int width = this.impactWidth;
			int height = this.impactHeight;
			
			int centerX = width / 2;
			int centerY = height / 2;
			
			int[] gray = new int[size];

			for (int i = 0; i < size; i++) {
				int argb = data[i];

				int r = (argb >> 16) & 255;
				int g = (argb >> 8) & 255;
				int b = argb & 255;

				gray[i] = (r * 299 + g * 587 + b * 114) / 1000;
			}

			int[] out = new int[size];

			for (int y = 1; y < height - 1; y++) {
				for (int x = 1; x < width - 1; x++) {
					int i = y * width + x;

					int tl = gray[(y - 1) * width + (x - 1)];
					int tc = gray[(y - 1) * width + x];
					int tr = gray[(y - 1) * width + (x + 1)];

					int ml = gray[y * width + (x - 1)];
					int mr = gray[y * width + (x + 1)];

					int bl = gray[(y + 1) * width + (x - 1)];
					int bc = gray[(y + 1) * width + x];
					int br = gray[(y + 1) * width + (x + 1)];

					int gx = -tl + tr - 2 * ml + 2 * mr - bl + br;
					int gy = -tl - 2 * tc - tr + bl + 2 * bc + br;

					int edge = Math.min(255, Math.abs(gx) + Math.abs(gy));

					int base = 255 - gray[i];
					int v = Math.max(base / 3, edge);

					boolean col = v >= 70;
					v = col ? 255 : 0;

					int argb = data[i];
					int a = (argb >> 24) & 255;
					if (red) {
						if (edge > 160) {
							out[i] = 0xffff0000;
						} else {
							int r = col ? 255 - ((argb >> 16) & 255) : 0;
							
							int dx = x - centerX;
							int dy = y - centerY;
							
							double sqSum = dx * dx + dy * dy * 0.5;
							
							double mult = Math.exp(-sqSum / (height * 35));
							
							int rC = (int) Math.round(v + (r - v) * mult);
							int nrC = (int) Math.round(v - v * mult);
							
							out[i] = (a << 24) | (rC << 16) | (nrC << 8) | nrC;
						}
					} else {
						out[i] = (a << 24) | (v << 16) | (v << 8) | v;
					}
				}
			}

			System.arraycopy(out, 0, data, 0, size);
		}

		private void clearOldImpactFrameTexture() {
			if (impactTextureLocation != null) {
				Minecraft.getMinecraft().getTextureManager().deleteTexture(impactTextureLocation);
				impactTextureLocation = null;
			}

			if (impactTexture != null) {
				impactTexture.deleteGlTexture();
				impactTexture = null;
			}
		}
		
		@SubscribeEvent
		public void onOverlay(RenderGameOverlayEvent.Post event) {
			if (event.getType() != ElementType.VIGNETTE) {
				return;
			}
			Minecraft mc = Minecraft.getMinecraft();

			if (!preheated) {
				preheated = true;
				this.captureImpactFrame(true);
				mc.getTextureManager().bindTexture(impactTextureLocation);
				timeHit = -1;
				return;
			}
			
			long time = System.currentTimeMillis();

			if (queueTime > 0 && time >= queueTime) {
				this.captureImpactFrame(shouldQueueRed);
				shouldQueueRed = true;
				queueTime = -1;
				return;
			}
			
			long timeMax = 300;
			if (impactTextureLocation == null || mc.world == null || time > (timeHit + timeMax)) {
				clearOldImpactFrameTexture();
				return;
			}

			ScaledResolution res = new ScaledResolution(mc);

			GlStateManager.pushMatrix();
			GlStateManager.disableDepth();
			GlStateManager.depthMask(false);
			GlStateManager.enableTexture2D();
			GlStateManager.color(1f, 1f, 1f, 1f);

			mc.getTextureManager().bindTexture(impactTextureLocation);

			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buf = tess.getBuffer();

			buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			buf.pos(-1, res.getScaledHeight() + 1, 0).tex(0, 0).endVertex();
			buf.pos(res.getScaledWidth() + 1, res.getScaledHeight() + 1, 0).tex(1, 0).endVertex();
			buf.pos(res.getScaledWidth() + 1, -1, 0).tex(1, 1).endVertex();
			buf.pos(-1, -1, 0).tex(0, 1).endVertex();
			tess.draw();

			GlStateManager.depthMask(true);
			GlStateManager.enableDepth();
			GlStateManager.popMatrix();
		}
	}
}

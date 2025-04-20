package com.existingeevee.moretcon.client.textures.unique;

import java.awt.image.DirectColorModel;
import java.util.Random;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import scala.actors.threadpool.Arrays;
import slimeknights.tconstruct.library.client.MaterialRenderInfo.AbstractMaterialRenderInfo;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.client.texture.AbstractColoredTexture;

//This is hell im sure theres a better way to do this
//buuuuut idk what im doing lmfao
public class GeodesiumTexture extends AbstractColoredTexture {
	boolean[] blank;
	boolean[] border;
	boolean[] onTop;
	int minBrightness;
	int maxBrightness;
	int brightnessData[];
	Random rand;

	protected GeodesiumTexture(ResourceLocation baseTextureLocation, String spriteName) {
		super(baseTextureLocation, spriteName);
	}

	public static class RenderInfo extends AbstractMaterialRenderInfo {

		@Override
		public TextureAtlasSprite getTexture(ResourceLocation baseTexture, String location) {
			GeodesiumTexture texture = new GeodesiumTexture(baseTexture, location);
			return texture;
		}
	}

	@Override
	protected void preProcess(int[] data) {
		DirectColorModel color = new DirectColorModel(32, 16711680, '\uff00', 255, -16777216);

		border = new boolean[width * height];
		blank = new boolean[width * height];
		onTop = new boolean[width * height];

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
					border[coord(x, y)] = true;
				}

				int c = data[coord(x, y)];
				if (c == 0 || color.getAlpha(c) < 64) {
					blank[coord(x, y)] = true;
					if (x > 0) {
						border[coord(x - 1, y)] = true;
					}

					if (y > 0) {
						border[coord(x, y - 1)] = true;
					}

					if (x < width - 1) {
						border[coord(x + 1, y)] = true;
						onTop[coord(x + 1, y)] = true;
					}

					if (y < height - 1) {
						border[coord(x, y + 1)] = true;
						onTop[coord(x, y + 1)] = true;
					}
				}
			}
		}

		for (int x = 0; x < width; ++x) {
			if (border[coord(x, 0)]) {
				onTop[coord(x, 0)] = true;
			}
		}
		for (int y = 0; y < height; ++y) {
			if (border[coord(0, y)]) {
				onTop[coord(0, y)] = true;
			}
		}

		// setup brigthness data
		int max = 0;
		int min = 255;
		brightnessData = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			int pixel = data[i];
			if (RenderUtil.alpha(pixel) == 0) {
				continue;
			}
			int brightness = getPerceptualBrightness(pixel);
			if (brightness < min) {
				min = brightness;
			}
			if (brightness > max) {
				max = brightness;
			}
			brightnessData[i] = brightness;
		}

		rand = new Random(Arrays.hashCode(data));

		// calculate the actual limits where we change color
		int brightnessDiff = max - min;
		brightnessDiff /= 2;
		minBrightness = Math.max(min + 1, min + (int) (brightnessDiff * 0.4f));
		maxBrightness = Math.min(max - 1, max - (int) (brightnessDiff * 0.3f));
	}

	private static final int[] RAND_BORD = { 0xfff6d2, 0xfffff3, 0xfffbe1 };

	@Override
	protected int colorPixel(int pixel, int pxCoord) {
		if (!blank[pxCoord]) {
			int a = RenderUtil.alpha(pixel);
			if (border[pxCoord]) {
				return RenderUtil.compose(255, 224, 175, a);
			} else {

				if (a == 0) {
					return pixel;
				}
				int brightness = brightnessData[pxCoord];
				int r = 255, g = 255, b = 255;

				if (brightness < minBrightness) {
					int c = RAND_BORD[rand.nextInt(RAND_BORD.length)];

					r = RenderUtil.red(c);
					b = RenderUtil.blue(c);
					g = RenderUtil.green(c);
				}

				// put it back together
				return RenderUtil.compose(r, g, b, a);
			}
		}

		return pixel;
	}
}

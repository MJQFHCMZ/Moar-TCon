package com.existingeevee.moretcon.client.textures.unique;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.existingeevee.moretcon.ModInfo;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.client.MaterialRenderInfo.AbstractMaterialRenderInfo;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.client.texture.AbstractColoredTexture;
import slimeknights.tconstruct.library.client.texture.TinkerTexture;

public class PerimidumTexture extends AbstractColoredTexture {

	public static class RenderInfo extends AbstractMaterialRenderInfo {

		@Override
		public TextureAtlasSprite getTexture(ResourceLocation baseTexture, String location) {

			String base = new ResourceLocation(ModInfo.MODID + ":other/material/materialperimidum").toString();

			TextureAtlasSprite blockTexture1 = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(base + "1");
			TextureAtlasSprite blockTexture2 = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(base + "2");

			if (blockTexture1 == null) {
				blockTexture1 = TinkerTexture.loadManually(new ResourceLocation(base + 1));
			}
			if (blockTexture2 == null) {
				blockTexture2 = TinkerTexture.loadManually(new ResourceLocation(base + 2));
			}

			PerimidumTexture sprite = new PerimidumTexture(new ResourceLocation(blockTexture1.getIconName()), new ResourceLocation(blockTexture2.getIconName()), baseTexture, location);
			sprite.stencil = false;
			return sprite;
		}
	}

	protected final ResourceLocation addTextureLocation;
	protected final ResourceLocation addTextureLocation2;
	protected TextureAtlasSprite addTexture;
	protected TextureAtlasSprite addTexture2;
	protected int[] textureData;
	protected int[] texture2Data;
	protected int addTextureWidth;
	protected int addTextureHeight;
	protected float scale;
	protected int offsetX = 0;
	protected int offsetY = 0;

	public boolean stencil = false;

	private ResourceLocation backupTextureLocation;

	public PerimidumTexture(ResourceLocation addTextureLocation, ResourceLocation addTextureLocation2, ResourceLocation baseTexture, String spriteName) {
		super(baseTexture, spriteName);

		this.backupTextureLocation = baseTexture;

		this.addTextureLocation = addTextureLocation;
		this.addTextureLocation2 = addTextureLocation2;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableList.<ResourceLocation>builder().addAll(super.getDependencies()).add(addTextureLocation).add(addTextureLocation2)
				.build();
	}

	@Override
	public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		addTexture = textureGetter.apply(addTextureLocation);
		addTexture2 = textureGetter.apply(addTextureLocation2);

		this.framesTextureData = Lists.newArrayList();
		this.frameCounter = 0;
		this.tickCounter = 0;

		TextureAtlasSprite baseTexture = textureGetter.apply(backupTextureLocation);
		if (baseTexture == null || baseTexture.getFrameCount() <= 0) {
			this.width = 1; // needed so we don't crash
			this.height = 1;
			// failure
			return false;
		}

		// copy data from base texture - we have the same properties/sizes as the base

		this.copyFrom(baseTexture);
		// todo: do this for every frame for animated textures and remove the old animation classes
		// get the base texture to work on - aka copy the texture data into this texture;

		ArrayList<int[][]> datas = Lists.newArrayList();

		for (boolean b : new boolean[] {true, false}) {
			this.firstIter = b;
			
			int[][] original = baseTexture.getFrameTextureData(0);
			int[][] data = new int[original.length][];
			data[0] = Arrays.copyOf(original[0], original[0].length);

			// do the transformation on the data for mipmap level 0
			// looks like other mipmaps are generated correctly
			processData(data[0]);
			
			datas.add(data);
		}
		
		if (this.framesTextureData.isEmpty()) {
			datas.forEach(this.framesTextureData::add);
		}
		this.animationMetadata = new AnimationMetadataSection(IntStream.range(0, 2).mapToObj(AnimationFrame::new).collect(Collectors.toList()), width, height, 10, true);

		return false;
	}

	@Override
	protected void preProcess(int[] data) {
		textureData = addTexture.getFrameTextureData(0)[0];
		texture2Data = addTexture2.getFrameTextureData(0)[0];
		// we need to keep the sizes, otherwise the secondary texture might set our size
		// to a different value
		// since it uses the same loading code as the main texture
		// read: 32x32 block textures with 16x16 tool textures = stuff goes boom
		addTextureWidth = addTexture.getIconWidth();
		addTextureHeight = addTexture.getIconHeight();
		scale = (float) addTextureHeight / (float) width;
	}

	@Override
	protected void postProcess(int[] data) {
		// not needed anymore, release memory
		textureData = null;
		texture2Data = null;
	}

	private boolean firstIter = true;

	@Override
	protected int colorPixel(int pixel, int pxCoord) {
		int a = RenderUtil.alpha(pixel);
		if (a == 0) {
			return pixel;
		}

		int texCoord = pxCoord;
		if (width > addTextureWidth) {
			int texX = (pxCoord % width) % addTextureWidth;
			int texY = (pxCoord / height) % addTextureHeight;
			texCoord = texY * addTextureWidth + texX;
		}
		int c = firstIter ? textureData[texCoord] : texture2Data[texCoord];

		// multiply in the color
		int r = RenderUtil.red(c);
		int b = RenderUtil.blue(c);
		int g = RenderUtil.green(c);

		if (!stencil) {
			r = mult(r, RenderUtil.red(pixel) / 2 + 0xff / 2);
			g = mult(g, RenderUtil.green(pixel) / 2 + 0xff / 2);
			b = mult(b, RenderUtil.blue(pixel) / 2 + 0xff / 2);
		}
		return RenderUtil.compose(r, g, b, a);
	}

	public void setOffset(int x, int y) {
		offsetX = x;
		offsetY = y;
	}

	protected int coord2(int x, int y) {
		return y * addTextureWidth + x;
	}
}

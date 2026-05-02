package com.existingeevee.moretcon.block.tile.gui;

import com.existingeevee.moretcon.ModInfo;
import com.existingeevee.moretcon.block.tile.TileCatalyzationChamber;
import com.existingeevee.moretcon.block.tile.container.ContainerCatalyzationChamber;
import com.existingeevee.moretcon.inits.ModBlocks;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiCatalyzationChamber extends GuiContainer {
	private static final ResourceLocation LOCATION = new ResourceLocation(ModInfo.MODID, "textures/other/catalyzation_chamber.png");
	private final InventoryPlayer playerInventory;
	public TileCatalyzationChamber tile;

	public GuiCatalyzationChamber(InventoryPlayer playerInv, TileCatalyzationChamber tile) {
		super(new ContainerCatalyzationChamber(playerInv, tile));
		this.playerInventory = playerInv;
		this.tile = tile;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = ModBlocks.blockCatalyzationChamber.getLocalizedName();
		this.fontRenderer.drawString(s, 8, 6, 4210752);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(LOCATION);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}
}

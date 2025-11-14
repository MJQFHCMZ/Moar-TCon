package com.existingeevee.moretcon.block.tile;

import java.util.List;
import java.util.Optional;

import com.existingeevee.moretcon.ModInfo;
import com.existingeevee.moretcon.inits.ModItems;
import com.existingeevee.moretcon.inits.ModReforges;
import com.existingeevee.moretcon.other.MatterReconstructionGelRMR;
import com.existingeevee.moretcon.reforges.AbstractReforge;
import com.existingeevee.moretcon.reforges.ReforgeHelper;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.gui.GuiModule;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.ListUtil;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.tools.common.client.GuiTinkerStation;
import slimeknights.tconstruct.tools.common.client.module.GuiButtonsPartCrafter;
import slimeknights.tconstruct.tools.common.client.module.GuiInfoPanel;
import slimeknights.tconstruct.tools.common.client.module.GuiSideInventory;
import slimeknights.tconstruct.tools.common.inventory.ContainerPatternChest;

@SideOnly(Side.CLIENT)
public class GuiReforgeStation extends GuiTinkerStation {

	private static final ResourceLocation BACKGROUND = new ResourceLocation(ModInfo.MODID, "textures/other/reforgestation.png");

	public static final int Column_Count = 4;

	protected GuiButtonsPartCrafter buttons;
	protected GuiInfoPanel info;
	protected GuiSideInventory sideInventory;
	protected ContainerPatternChest.DynamicChestInventory chestContainer;

	public GuiReforgeStation(InventoryPlayer playerInv, World world, BlockPos pos, TileReforgeStation tile) {
		super(world, pos, (ContainerReforgeStation) tile.createContainer(playerInv, world, pos));

		if (inventorySlots instanceof ContainerReforgeStation) {
			ContainerReforgeStation container = (ContainerReforgeStation) inventorySlots;

			info = new GuiInfoPanel(this, container);
			info.ySize = this.ySize;
			this.addModule(info);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawBackground(BACKGROUND);

		if (sideInventory != null) {
			sideInventory.updateSlotCount(chestContainer.getSizeInventory());
		}

		if (!(container instanceof ContainerReforgeStation)) {
			return;
		}

		// draw slot icons
		drawIconEmpty(container.getSlot(1), Icons.ICON_Shard);
		drawIconEmpty(container.getSlot(2), Icons.ICON_Gem);
		drawIconEmpty(container.getSlot(3), Icons.ICON_Pickaxe);
		drawIconEmpty(container.getSlot(4), Icons.ICON_Ingot);
		drawIconEmpty(container.getSlot(5), Icons.ICON_Block);

		// System.out.println(container.inventorySlots);

		// draw material info
		String amount = null;
		Material material = getMaterial(container.getSlot(4).getStack(), container.getSlot(5).getStack());

		if (material == null && (container.getSlot(4).getStack().getItem() == ModItems.matterReconstructionGel || container.getSlot(5).getStack().getItem() == ModItems.matterReconstructionGel)) {
			if (container.getSlot(3).getStack().getItem() instanceof TinkersItem) {

				List<Material> mats = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(container.getSlot(3).getStack()));
				List<PartMaterialType> components = ((TinkersItem) container.getSlot(3).getStack().getItem()).getRequiredComponents();

				for (int i = 0; i < components.size() && i < mats.size(); i++) {
					if (components.get(i).usesStat(MaterialTypes.HEAD)) {
						material = mats.get(i);
						break;
					}
				}
			}
		}

		if (material != null) {
			// int count = 0;
			Optional<RecipeMatch.Match> matchOptional = new MatterReconstructionGelRMR(material, 4).matchesRecursively(ListUtil.getListFrom(container.getSlot(4).getStack(), container.getSlot(5).getStack()));
			if (matchOptional.isPresent()) {
				int matchAmount = matchOptional.get().amount;
				amount = Util.df.format(matchAmount / (float) Material.VALUE_Ingot);

				if (matchAmount < Material.VALUE_Ingot) {
					amount = TextFormatting.DARK_RED + amount + TextFormatting.RESET;
				}
			}
		}

		if (amount != null) {
			int x = this.cornerX + this.realWidth / 2;
			int y = this.cornerY + 63;
			String text = Util.translateFormatted("gui.partbuilder.material_value", amount, material.getLocalizedName());
			x -= fontRenderer.getStringWidth(text) / 2;
			fontRenderer.renderString(text, x, y, 0x777777, false);
		}

		for (GuiModule module : modules) {
			module.handleDrawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		}
	}

	@Override
	public void updateDisplay() {

		ContainerReforgeStation container = (ContainerReforgeStation) this.container;
		
		info.setCaption(container.getInventoryDisplayName());	
		
		boolean toolPresent = container.getSlot(3).getHasStack();
		if (toolPresent) {
			AbstractReforge reforge = ReforgeHelper.getReforge(container.getSlot(3).getStack());
			
			String reforgeName = reforge == null ? (I18n.translateToLocal("text.reforge_marker") + ": " + I18n.translateToLocal("modifier.reforgenone.name")) : reforge.getLocalizedName();
			if (reforge == ModReforges.reforgePlaceholder) {
				reforgeName = I18n.translateToLocal("text.reforge_marker") + ": ?????";
			}
			String reforgeFlavor = Util.translate(AbstractReforge.LOC_Flav, reforge == null ? "reforgenone" : reforge.getIdentifier());
			String reforgeDesc = Util.translate(AbstractReforge.LOC_Desc, reforge == null ? "reforgenone" : reforge.getIdentifier());
						
			String infoDesc = "" + TextFormatting.UNDERLINE + CustomFontColor.encodeColor(reforge == null ? 0xaaaaaa : reforge.getColor()) + reforgeName + TextFormatting.RESET + "\n" + reforgeFlavor + "\n\n" + TextFormatting.RESET + reforgeDesc;
						
			if (container.getSlot(0).getHasStack()) {
				reforge = ReforgeHelper.getReforge(container.getSlot(0).getStack());
				
				reforgeName = reforge == null ? I18n.translateToLocal("modifier.reforgenone.prefix") : reforge.getLocalizedPrefix();
				if (reforge == ModReforges.reforgePlaceholder) {
					reforgeName = "?????";
				}
				reforgeFlavor = Util.translate(AbstractReforge.LOC_Flav, reforge == null ? "reforgenone" : reforge.getIdentifier());
				reforgeDesc = Util.translate(AbstractReforge.LOC_Desc, reforge == null ? "reforgenone" : reforge.getIdentifier());
				
				infoDesc += "\n\n" + TextFormatting.UNDERLINE + CustomFontColor.encodeColor(reforge == null ? 0xaaaaaa : reforge.getColor()) + I18n.translateToLocal("text.new_reforge_marker") + ": " + reforgeName + TextFormatting.RESET + "\n" + reforgeFlavor + "\n\n" + TextFormatting.RESET + reforgeDesc;
			} 
			
			info.setText(infoDesc);
		} else {
			info.setText(I18n.translateToLocal("gui." + ModInfo.MODID + ".reforgestation.info"));
		}
	}

	@Override
	public void error(String message) {
		info.setCaption(I18n.translateToLocal("gui.error"));
		info.setText(message);
	}

	@Override
	public void warning(String message) {
		info.setCaption(I18n.translateToLocal("gui.warning"));
		info.setText(message);
	}

	public void updateButtons() {
		if (buttons != null) {
			// this needs to be done threadsafe, since the buttons may be getting rendered currently
			Minecraft.getMinecraft().addScheduledTask(() -> buttons.updatePosition(cornerX, cornerY, realWidth, realHeight));
		}
	}

	protected void setDisplayForMaterial(Material material) {
		info.setCaption(material.getLocalizedNameColored());

		List<String> stats = Lists.newLinkedList();
		List<String> tips = Lists.newArrayList();
		for (IMaterialStats stat : material.getAllStats()) {
			List<String> info = stat.getLocalizedInfo();
			if (!info.isEmpty()) {
				stats.add(TextFormatting.UNDERLINE + stat.getLocalizedName());
				stats.addAll(info);
				stats.add(null);
				tips.add(null);
				tips.addAll(stat.getLocalizedDesc());
				tips.add(null);
			}
		}

		// Traits
		for (ITrait trait : material.getAllTraits()) {
			if (!trait.isHidden()) {
				stats.add(material.getTextColor() + trait.getLocalizedName());
				tips.add(material.getTextColor() + trait.getLocalizedDesc());
			}
		}

		if (!stats.isEmpty() && stats.get(stats.size() - 1) == null) {
			// last empty line
			stats.remove(stats.size() - 1);
			tips.remove(tips.size() - 1);
		}

		info.setText(stats, tips);
	}

	protected Material getMaterial(ItemStack... stacks) {
		for (ItemStack stack : stacks) {
			if (stack.isEmpty()) {
				continue;
			}
			// material-item?
			if (stack.getItem() instanceof IMaterialItem) {
				return ((IMaterialItem) stack.getItem()).getMaterial(stack);
			}
		}

		// regular item, check if it belongs to a material
		for (Material material : TinkerRegistry.getAllMaterials()) {
			if (material.matches(stacks).isPresent()) {
				return material;
			}
		}

		// no material found
		return null;
	}
}

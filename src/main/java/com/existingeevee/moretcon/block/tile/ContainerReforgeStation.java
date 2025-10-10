package com.existingeevee.moretcon.block.tile;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.existingeevee.moretcon.inits.ModItems;
import com.existingeevee.moretcon.inits.ModReforges;
import com.existingeevee.moretcon.item.ItemReforgeStone;
import com.existingeevee.moretcon.other.MatterReconstructionGelRMR;
import com.existingeevee.moretcon.reforges.AbstractReforge;
import com.existingeevee.moretcon.reforges.ReforgeHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.inventory.IContainerCraftingCustom;
import slimeknights.mantle.inventory.SlotCraftingCustom;
import slimeknights.mantle.inventory.SlotOut;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.ListUtil;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.shared.inventory.InventoryCraftingPersistent;
import slimeknights.tconstruct.tools.common.client.GuiPartBuilder;
import slimeknights.tconstruct.tools.common.inventory.ContainerTinkerStation;

public class ContainerReforgeStation extends ContainerTinkerStation<TileReforgeStation> implements IContainerCraftingCustom {

	public IInventory craftResult;
	// public IInventory craftResultSecondary;

	private final Slot toolSlot;
	private final Slot stoneSlot;
	private final Slot secondarySlot;
	private final Slot input1;
	private final Slot input2;

	protected final EntityPlayer player;

	public ContainerReforgeStation(InventoryPlayer playerInventory, TileReforgeStation tile) {
		super(tile);

		InventoryCraftingPersistent craftMatrix = new InventoryCraftingPersistent(this, tile, 1, 5);
		this.craftResult = new InventoryCraftResult();
		this.player = playerInventory.player;
		// InventoryCrafting craftMatrixSecondary = new InventoryCrafting(this, 1, 1);
		// this.craftResultSecondary = new InventoryCrafting(this, 1, 1);

		// output slots
		this.addSlotToContainer(new SlotCraftingCustom(this, playerInventory.player, craftMatrix, craftResult, 0, 114, 35));
		this.addSlotToContainer(secondarySlot = new SlotOut(tile, 3, 140, 35));

		// stone slot
		this.addSlotToContainer(stoneSlot = new Slot(craftMatrix, 4, 50, 26) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				if (stack == null || !(stack.getItem() instanceof ItemReforgeStone)) {
					return false;
				}

				return true;
			}
		});

		// tool
		this.addSlotToContainer(toolSlot = new Slot(craftMatrix, 2, 20, 35) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				if (stack == null || !(stack.getItem() instanceof TinkersItem)) {
					return false;
				}

				return true;
			}
		});

		// material slots
		this.addSlotToContainer(input1 = new Slot(craftMatrix, 0, 42, 44));
		this.addSlotToContainer(input2 = new Slot(craftMatrix, 1, 60, 44));

		this.addPlayerInventory(playerInventory, 8, 84);

		onCraftMatrixChanged(playerInventory);
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		updateResult();
	}

	// Sets the result in the output slot depending on the input!
	public void updateResult() {

		if (!toolSlot.getHasStack() || (!input1.getHasStack() && !input2.getHasStack() && !secondarySlot.getHasStack() && !stoneSlot.getHasStack())) {
			craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
			updateGUI();
		} else {

			int matchAmount;
			boolean foundSufficient = false;

			Material material = getMaterial(input1.getStack(), input2.getStack());
			TinkerGuiException throwable = null;

			boolean mrgPresent = (this.input1.getStack().getItem() == ModItems.matterReconstructionGel || this.input2.getStack().getItem() == ModItems.matterReconstructionGel);
			boolean validHead = false;
			ItemStack stack = toolSlot.getStack();

			if (stack.getItem() instanceof TinkersItem) {
				TinkersItem tool = (TinkersItem) stack.getItem();

				List<Material> mats = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
				List<PartMaterialType> components = tool.getRequiredComponents();

				for (int i = 0; i < components.size() && i < mats.size(); i++) {
					if (components.get(i).usesStat(MaterialTypes.HEAD)) {
						if (material == null && mrgPresent) {
							validHead = true;
							material = mats.get(i);
							break;
						} else if (material != null && material == mats.get(i)) {
							validHead = true;
							break;
						}
					}
				}
			}

			if (material != null) {
				// int count = 0;
				Optional<RecipeMatch.Match> matchOptional = new MatterReconstructionGelRMR(material, 4).matchesRecursively(ListUtil.getListFrom(input1.getStack(), input2.getStack()));
				if (matchOptional.isPresent()) {
					matchAmount = matchOptional.get().amount;
					if (matchAmount >= Material.VALUE_Ingot) {
						foundSufficient = true;
					}
				}
			}

			ItemStack reforged = null;
			ItemStack remains = ItemStack.EMPTY;

			try {
				//we use the placeholder on client as we want it to be obfuscated. (not that itd matter the random isnt synced)
				AbstractReforge reforge = this.world.isRemote ? ModReforges.reforgePlaceholder : ReforgeHelper.getRandomReforge(stack);

				ItemStack stone = stoneSlot.getStack();

				if (!stone.isEmpty() && stone.getItem() instanceof ItemReforgeStone) {
					reforge = ((ItemReforgeStone) stone.getItem()).getReforge(stone);

					if (ReforgeHelper.getReforge(stack) == reforge)
						reforge = null;

				}

				if (foundSufficient && reforge != null) {
					ItemStack potentialReforge = stack.copy();

					remains = getPartialLeftoverAmount(material, ListUtil.getListFrom(input1.getStack(), input2.getStack()), false);

					reforge.apply(potentialReforge);
					reforged = potentialReforge;

					if (potentialReforge != null) {
						TinkerCraftingEvent.ToolModifyEvent.fireEvent(reforged, player, toolSlot.getStack().copy());
					}
				}

				if (!validHead && (input1.getHasStack() || input2.getHasStack())) {
					throw new TinkerGuiException(I18n.translateToLocalFormatted("gui.error.invalid_reforge_material", material.getLocalizedName()));
				}
			} catch (TinkerGuiException e) {
				reforged = null;
				remains = ItemStack.EMPTY;
				throwable = e;
			}

			ItemStack secondary = secondarySlot.getStack();

			// got output?
			if (reforged != null &&
			// got no secondary output or does it stack with the current one?
					(secondary.isEmpty() || remains.isEmpty() || ItemStack.areItemsEqual(secondary, remains) && ItemStack.areItemStackTagsEqual(secondary, remains))) {
				craftResult.setInventorySlotContents(0, reforged);
			} else {
				craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
			}

			if (throwable != null) {
				error(throwable.getMessage());
			} else {
				updateGUI();
			}
		}
	}

	public static ItemStack getPartialLeftoverAmount(Material material, NonNullList<ItemStack> materialItems, boolean removeItems) throws TinkerGuiException {
		// find the material from the input
		if (!removeItems) {
			materialItems = Util.deepCopyFixedNonNullList(materialItems);
		}

		Optional<RecipeMatch.Match> match = new MatterReconstructionGelRMR(material, 4).matches(materialItems, Material.VALUE_Ingot);

		if (match.isPresent()) {
			RecipeMatch.removeMatch(materialItems, match.get());

			// check if we have secondary output
			ItemStack secondary = ItemStack.EMPTY;
			int leftover = (match.get().amount - Material.VALUE_Ingot) / Material.VALUE_Shard;
			if (leftover > 0) {
				secondary = TinkerRegistry.getShard(material);
				secondary.setCount(leftover);
			}
			// build an item with this
			return secondary;
		}
		return ItemStack.EMPTY;
	}

	protected static Material getMaterial(ItemStack... stacks) {
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

	@Override
	public void onCrafting(EntityPlayer player, ItemStack output, IInventory craftMatrix) {
		ItemStack remains = ItemStack.EMPTY;
		try {
			Material material = getMaterial(input1.getStack(), input2.getStack());
			remains = getPartialLeftoverAmount(material, ListUtil.getListFrom(input1.getStack(), input2.getStack()), true);
		} catch (Exception e) {
			// don't need any user information at this stage
		}

		this.toolSlot.getStack().shrink(1);
		this.stoneSlot.getStack().shrink(1);

		ItemStack secondOutput = remains;
		ItemStack secondary = secondarySlot.getStack();
		if (secondary.isEmpty()) {
			putStackInSlot(secondarySlot.slotNumber, secondOutput);
		} else if (!secondOutput.isEmpty() && ItemStack.areItemsEqual(secondary, secondOutput) && ItemStack.areItemStackTagsEqual(secondary, secondOutput)) {
			secondary.grow(secondOutput.getCount());
		}
		
		updateResult();
	}

	@Override
	public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
		// prevents that doubleclicking on an item pulls the same out of the crafting slot
		return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(p_94530_1_, p_94530_2_);
	}

	@Override
	public String getInventoryDisplayName() {
		return super.getInventoryDisplayName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void putStackInSlot(int p_75141_1_, @Nonnull ItemStack p_75141_2_) {
		super.putStackInSlot(p_75141_1_, p_75141_2_);

		// this is called solely to update the gui buttons
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen instanceof GuiPartBuilder) {
			((GuiPartBuilder) mc.currentScreen).updateButtons();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack slotClick(int slotId, int dragType, ClickType type, EntityPlayer player) {
		ItemStack ret = super.slotClick(slotId, dragType, type, player);
		// this is called solely to update the gui buttons
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen instanceof GuiPartBuilder) {
			((GuiPartBuilder) mc.currentScreen).updateButtons();
		}

		return ret;
	}
}

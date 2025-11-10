package com.existingeevee.moretcon.other;

import com.existingeevee.moretcon.ModInfo;
import com.existingeevee.moretcon.devtools.DevEnvHandler;
import com.existingeevee.moretcon.traits.traits.abst.IAdditionalTraitMethods;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent;
import slimeknights.tconstruct.library.tinkering.ITinkerable;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class EventWatcherMain {

	public static boolean sent = false;

	@SubscribeEvent
	@SideOnly(value = Side.CLIENT)
	public void sendBeta(WorldTickEvent e) {
		if (!sent && Minecraft.getMinecraft().player != null) {
			sent = true;
			TextComponentString linkComponent = new TextComponentString(ModInfo.ISSUE_TRACKER);
			linkComponent.setStyle(linkComponent.getStyle().setUnderlined(true).setColor(TextFormatting.BLUE).setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ModInfo.ISSUE_TRACKER)));

			if (DevEnvHandler.inDevMode()) {
				String[] strings = ("[" + "\u00A7" + ChatFormatting.BLUE.getChar() + ModInfo.NAME + "\u00A7"
						+ ChatFormatting.RED.getChar() + " " + I18n.translateToLocal("text.dev.name") + "\u00A7"
						+ ChatFormatting.WHITE.getChar() + "] " + I18n.translateToLocal("text.dev.desc")).split("__n__");

				for (int i = 0; i < strings.length - 1; i++) {
					Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(strings[i]), false);
				}
				if (strings.length >= 1) {
					Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(strings[strings.length - 1]).appendSibling(linkComponent), false);
				}
			} else if (ModInfo.BETA) {
				String[] strings = ("[" + "\u00A7" + ChatFormatting.BLUE.getChar() + ModInfo.NAME + "\u00A7"
						+ ChatFormatting.RED.getChar() + " " + I18n.translateToLocal("text.beta.name") + "\u00A7"
						+ ChatFormatting.WHITE.getChar() + "] " + I18n.translateToLocal("text.beta.desc")).split("__n__");

				for (int i = 0; i < strings.length - 1; i++) {
					Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(strings[i]), false);
				}
				if (strings.length >= 1) {
					Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(strings[strings.length - 1]).appendSibling(linkComponent), false);
				}
			}
		}
	}

	@SubscribeEvent
	public void updateItemStackEntities(WorldTickEvent e) {
		if (e.phase != Phase.START)
			return;
		for (EntityItem entity : e.world.getEntities(EntityItem.class, en -> en.getItem().getItem() instanceof ITinkerable)) {
			ItemStack tool = entity.getItem();
			for (ITrait t : ToolHelper.getTraits(tool)) {
				if (t instanceof IAdditionalTraitMethods) {
					((IAdditionalTraitMethods) t).onEntityItemTick(tool, entity);
				}
			}
		}
	}
	
	@SubscribeEvent
	@SideOnly(value = Side.CLIENT)
	public void updateItemStackEntities(ClientTickEvent e) {
		if (e.phase != Phase.START || Minecraft.getMinecraft().world == null)
			return;
		for (EntityItem entity : Minecraft.getMinecraft().world.getEntities(EntityItem.class, en -> en.getItem().getItem() instanceof ITinkerable)) {
			ItemStack tool = entity.getItem();
			for (ITrait t : ToolHelper.getTraits(tool)) {
				if (t instanceof IAdditionalTraitMethods) {
					((IAdditionalTraitMethods) t).onEntityItemTick(tool, entity);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onTinkerCraftingEvent(TinkerCraftingEvent event) {
		if (event.getItemStack().getItem() instanceof ITinkerable) { 
			NBTTagCompound comp = TagUtil.getTagSafe(event.getItemStack());
			if (comp.hasKey("UniqueToolID"))
				comp.removeTag("UniqueToolID");
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@SideOnly(value = Side.CLIENT)
	public void handleToolTips(ItemTooltipEvent event) {
		if (event.getItemStack().getItem() instanceof ITinkerable) {
			for (ITrait t : ToolHelper.getTraits(event.getItemStack())) {
				if (t instanceof IAdditionalTraitMethods) {
					((IAdditionalTraitMethods) t).modifyTooltip(event.getItemStack(), event);
				}
			}
		}
	}
	
	//UUID wair c6ed1dbb-9089-4b36-82ef-c2cd6eea2aa4
}

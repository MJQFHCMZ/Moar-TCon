package com.existingeevee.moretcon.other;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tinkering.ITinkerable;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class MixinEarlyAccessor {

	public static Class<ToolCore> getToolCoreClass() {
		return ToolCore.class;
	}
	
	public static boolean isStackBroken(ItemStack is) {
		return ToolHelper.isBroken(is);
	}
	
	public static boolean isITinkerable(ItemStack is) {
		return is.getItem() instanceof ITinkerable;
	}
}

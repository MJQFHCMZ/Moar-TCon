package com.existingeevee.moretcon.item;

import net.minecraft.entity.item.EntityItem;

public class ItemNoGravity extends ItemBase {

	public ItemNoGravity(String itemName) {
		super(itemName);
	}

    public ItemNoGravity(String itemName, GlowType glowtype, int color) {
		super(itemName, glowtype, color);
	}

	@Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        entityItem.motionY += 0.039f;
        return false;
    }


}

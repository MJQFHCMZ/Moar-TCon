package com.existingeevee.moretcon.item;

import com.existingeevee.moretcon.other.WorldGravityUtils;

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
		if (WorldGravityUtils.getWorldGravitiationalAcceleration(entityItem.world, entityItem.getPositionVector()) == -0.08)
			entityItem.motionY += 0.039f;
        return false;
    }


}

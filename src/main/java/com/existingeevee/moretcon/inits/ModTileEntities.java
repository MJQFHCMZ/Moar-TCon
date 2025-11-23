package com.existingeevee.moretcon.inits;

import com.existingeevee.moretcon.block.tile.TileGravitoniumFaucet;
import com.existingeevee.moretcon.block.tile.TileReforgeStation;
import com.existingeevee.moretcon.other.utils.CompatManager;
import com.existingeevee.moretcon.other.utils.RegisterHelper;

public class ModTileEntities {

	public static void init() {
		RegisterHelper.registerTileEntity(TileReforgeStation.class);
		RegisterHelper.registerTileEntity(TileGravitoniumFaucet.class);
		
		if (CompatManager.loadMain) {
		}
	}
	
}

package com.existingeevee.moretcon.client.actions;

import com.existingeevee.moretcon.other.ImpactFrameHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ImpactFrameAction extends ClientAction {

	public static final ImpactFrameAction INSTANCE = new ImpactFrameAction();

	private ImpactFrameAction() {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void runAsClient(World world, double x, double y, double z, NBTBase data) {
		int freezeIn = 0;
		boolean red = true;
		if (data instanceof NBTTagCompound) {
			NBTTagCompound comp = (NBTTagCompound) data;
			if (comp.hasKey("FreezeIn", NBT.TAG_ANY_NUMERIC)) {
				freezeIn = comp.getInteger("FreezeIn");
			}
			if (comp.hasKey("Red", NBT.TAG_BYTE)) {
				red = comp.getBoolean("Red");
			}
			
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if (player == null || !((NBTTagCompound) data).getUniqueId("Player").equals(player.getUniqueID()))
				return;
		} else {
			return;
		}
		
		ImpactFrameHelper.INSTANCE.initiateImpactFrame(freezeIn, red);
	}

	public static NBTBase build(EntityLivingBase player, int wait, boolean red) {
		NBTTagCompound comp = new NBTTagCompound();
		comp.setBoolean("Red", red);
		comp.setInteger("FreezeIn", wait);
		comp.setUniqueId("Player", player.getUniqueID());
		return comp;
	}
}

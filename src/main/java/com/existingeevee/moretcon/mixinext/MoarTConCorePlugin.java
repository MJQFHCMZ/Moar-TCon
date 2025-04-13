package com.existingeevee.moretcon.mixinext;

import java.util.Map;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import com.existingeevee.moretcon.ModInfo;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class MoarTConCorePlugin implements IFMLLoadingPlugin {

	public MoarTConCorePlugin() {

		System.out.println("Go my mixin...");
		System.out.println("Cause havoc as you see fit...");
		
		MixinBootstrap.init();
		MixinExtrasBootstrap.init();
		
        Mixins.addConfiguration("mixins." + ModInfo.MODID + ".loader.json");
        Mixins.addConfiguration("mixins." + ModInfo.MODID + ".json");
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[0];
	}
	
	@Override
	public String getModContainerClass()
	{
		return null;
	}
	
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) { }
	
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}
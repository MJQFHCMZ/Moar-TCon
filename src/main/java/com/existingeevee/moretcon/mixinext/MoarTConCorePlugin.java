package com.existingeevee.moretcon.mixinext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import com.existingeevee.moretcon.ModInfo;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class MoarTConCorePlugin implements IFMLLoadingPlugin {

	private static final String vanillaMixins = "mixins." + ModInfo.MODID + ".json"; // Things that change vanilla code
	private static final String loaderMixins = "mixins." + ModInfo.MODID + ".loader.json"; // The one mixin that loads the late mixins
	private static final String nonvanillaMixins = "mixins." + ModInfo.MODID + ".late.json"; // All of the modded stuff.

	public MoarTConCorePlugin() {
		System.out.println("Go my mixin...");

		try {
			Class<?> clazz = Class.forName("fermiumbooter.FermiumRegistryAPI");
			Method method = clazz.getDeclaredMethod("enqueueMixin", boolean.class, String.class);

			System.out.println("Take its hand, and achieve greatness...");

			MixinBootstrap.init();
			MixinExtrasBootstrap.init();
			
			method.invoke(null, true, nonvanillaMixins);
			method.invoke(null, false, vanillaMixins);
			return;
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {

		}

		System.out.println("With no one left to guide you, cause havoc as you see fit...");

		MixinBootstrap.init();
		MixinExtrasBootstrap.init();

		Mixins.addConfiguration(loaderMixins); // Welp ig we need to do it ourselves
		Mixins.addConfiguration(vanillaMixins);
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[0];
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
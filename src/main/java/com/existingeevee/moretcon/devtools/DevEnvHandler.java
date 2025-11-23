package com.existingeevee.moretcon.devtools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.existingeevee.moretcon.other.MoreTConLogger;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;

public class DevEnvHandler {

	public static boolean inDevMode() {
		return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	}

	private static File projectLoc;
	private static File templateFolder;
	private static File assetsFolder;

	public static boolean copyAssetTemplate(String template, ResourceLocation destPath, String assetType, Map<String, String> values) {
		if (!inDevMode()) {
			return false;
		}

		try {
			if (projectLoc == null)
				projectLoc = new File(new File(".").getCanonicalFile().getParentFile(), "src/main/resources");

			if (templateFolder == null)
				templateFolder = new File(projectLoc, "asset_templates");

			if (assetsFolder == null)
				assetsFolder = new File(projectLoc, "assets");

			File destination = new File(assetsFolder, destPath.getResourceDomain() + "/" + assetType + "/" + destPath.getResourcePath() + ".json");
			File destinationObj = new File(assetsFolder, destPath.getResourceDomain() + "/" + assetType + "/" + destPath.getResourcePath() + ".obj"); //for the 2 obj models lmfao
			
			if (destination.exists() || destinationObj.exists()) {
				return false;
			} else {	
				MoreTConLogger.log("Missing resource for \"" + destination + "\". Since this is in dev mode this file will be automatically created.");
				destination.getParentFile().mkdirs();
				destination.createNewFile();
				String text = MiscUtils.readTextFile(new File(templateFolder, template + ".json"));
				for (Entry<String, String> ent : values.entrySet()) {
					text = text.replace("${" + ent.getKey() + "}", ent.getValue());
				}
				
				FileWriter writer = new FileWriter(destination);
				writer.append(text);
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}

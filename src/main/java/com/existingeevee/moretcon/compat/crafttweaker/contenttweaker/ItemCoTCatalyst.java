package com.existingeevee.moretcon.compat.crafttweaker.contenttweaker;

import java.util.List;
import java.util.Map;

import com.existingeevee.moretcon.item.ItemCatalyst;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.client.models.IHasModel;
import com.teamacronymcoders.base.client.models.generator.IHasGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;

import net.minecraft.item.Item;

@SuppressWarnings("rawtypes")
public class ItemCoTCatalyst extends ItemCatalyst implements IHasGeneratedModel, IHasModel {

    private IBaseMod mod;
	
	public ItemCoTCatalyst(String itemName) {
		super(itemName);
		this.setUnlocalizedName(itemName);
	}

	public ItemCoTCatalyst(String itemName, int hex) {
		super(itemName, hex);
		this.setUnlocalizedName(itemName);
	}

	public ItemCoTCatalyst(String itemName, GlowType type, int hex) {
		super(itemName, type, hex);
		this.setUnlocalizedName(itemName);
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public IBaseMod getMod() {
		return mod;
	}

	@Override
	public void setMod(IBaseMod mod) {
		this.mod = mod;
	}

    @Override
    public List<IGeneratedModel> getGeneratedModels() {
        List<IGeneratedModel> models = Lists.newArrayList();
        TemplateFile templateFile = TemplateManager.getTemplateFile("item_model");
        Map<String, String> replacements = Maps.newHashMap();

        replacements.put("texture", "contenttweaker:items/" + this.getRegistryName().getResourcePath());
        templateFile.replaceContents(replacements);
        models.add(new GeneratedModel(this.getRegistryName().getResourcePath(), ModelType.ITEM_MODEL, templateFile.getFileContents()));
        return models;
    }

    @Override
    public List<String> getModelNames(List<String> modelNames) {
        modelNames.add(this.getUnlocalizedName().substring(5));
        return modelNames;
    }

    
}

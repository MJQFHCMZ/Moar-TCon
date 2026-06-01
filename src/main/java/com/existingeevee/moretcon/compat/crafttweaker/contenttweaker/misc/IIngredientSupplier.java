package com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.misc;

import java.util.function.Supplier;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.moretcon.IIngredientSupplier")
@ZenRegister
public interface IIngredientSupplier extends Supplier<IIngredient> {
	
	@Override
	@ZenMethod
	IIngredient get();
}
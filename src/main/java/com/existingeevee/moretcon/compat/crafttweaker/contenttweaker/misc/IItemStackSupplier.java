package com.existingeevee.moretcon.compat.crafttweaker.contenttweaker.misc;

import java.util.function.Supplier;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.moretcon.IItemStackSupplier")
@ZenRegister
public interface IItemStackSupplier extends Supplier<IItemStack> {
	
	@Override
	@ZenMethod
	IItemStack get();
}
package com.existingeevee.moretcon.other.slotrender;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class ColoredGlowTicRender {

	public static final Map<Material, Integer> MATERIAL_COLORS = new HashMap<>();
	
	private static final Map<Integer, Integer> map = new HashMap<>();

	public static boolean isTool(ItemStack stack) {
		if (MiscUtils.getEmbossments(stack).stream().anyMatch(MATERIAL_COLORS.keySet()::contains)) {
			return true;
		}
		if (TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack)).stream().anyMatch(MATERIAL_COLORS.keySet()::contains)) {
			return true;
		}
		return false;
	}
	
	public static boolean isPart(ItemStack stack) {
		return stack.getItem() instanceof MaterialItem && MATERIAL_COLORS.containsKey(((MaterialItem) stack.getItem()).getMaterial(stack));
	}
	
	public static int calculateColor(ItemStack stack) {
		NBTTagCompound nbt = stack.serializeNBT();
		if (stack.getItem() instanceof MaterialItem) {
			Material m = ((MaterialItem) stack.getItem()).getMaterial(stack);
			int color = MATERIAL_COLORS.get(m) != null ? MATERIAL_COLORS.get(m) : m.materialTextColor;
			map.put(nbt.hashCode(), color);
		}
		if (map.containsKey(nbt.hashCode())) {
			return map.get(nbt.hashCode());
		} else {
			int sumRed = 0;
			int sumGreen = 0;
			int sumBlue = 0;
			int sumTotal = 0;
			Set<Material> materials = new HashSet<>();

			materials.addAll(MiscUtils.getMaterials(stack));
			materials.addAll(MiscUtils.getEmbossments(stack));

			materials.retainAll(MATERIAL_COLORS.keySet());

			for (Material m : materials) {
				int col = MATERIAL_COLORS.get(m) != null ? MATERIAL_COLORS.get(m) : m.materialTextColor;
				sumRed += (col & 0xFF0000) >> 16;
				sumGreen += (col & 0xFF00) >> 8;
				sumBlue += (col & 0xFF);
				sumTotal += 1;
			}

			if (sumTotal == 0) {
				map.put(nbt.hashCode(), Integer.MIN_VALUE);
			} else {
				Color color = new Color(sumRed / 255f / sumTotal, sumGreen / 255f / sumTotal, sumBlue / 255f / sumTotal);
				map.put(nbt.hashCode(), color.getRGB());
			}
			return map.get(nbt.hashCode());
		}
	}
}

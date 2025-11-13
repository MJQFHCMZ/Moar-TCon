package com.existingeevee.moretcon.reforges;

import com.existingeevee.moretcon.other.WeightedItem;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

//A lot of this is stolen out of AbstractTrait :giddy:
public abstract class AbstractReforge extends Modifier implements ITrait {

	public static final String LOC_Name = Modifier.LOC_Name;
	public static final String LOC_Flav = "modifier.%s.flavor";
	public static final String LOC_Desc = Modifier.LOC_Desc;
	protected final int color;

	public AbstractReforge(String identifier, TextFormatting color) {
		this(identifier, Util.enumChatFormattingToColor(color));
	}

	public AbstractReforge(String identifier, int color) {
		super(Util.sanitizeLocalizationString(identifier));
		this.color = color;

		TinkerRegistry.addTrait(this);
		this.addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this, color));
	}

	public AbstractReforge setRandomReforge(double weight) {
		if (ReforgeHelper.RANDOM_REFORGES.contains(this.getIdentifier()))
			return this;
		
		ReforgeHelper.RANDOM_REFORGES.add(this.getIdentifier());
		ReforgeHelper.REFORGE_WEIGHTS.add(new WeightedItem<>(this, weight));
		
		return this;
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}

	public String getLocalizedPrefix() {
		return Util.translate(LOC_Name, getIdentifier());
	}

	@Override
	public String getLocalizedName() {
		return I18n.translateToLocal("text.reforge_marker") + ": " + getLocalizedPrefix();
	}

	@Override
	public String getLocalizedDesc() {
		return Util.translate(LOC_Flav, getIdentifier()) + "\n" + ChatFormatting.RESET + getLocalizedDescWithoutFlavor();
	}

	public String getLocalizedDescWithoutFlavor() {
		return Util.translate(LOC_Desc, getIdentifier());
	}

	@Override
	public boolean isHidden() {
		// This is janky, but we need the thing to only show in the tool station
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		try {
			if (trace[2].getClassName().endsWith("GuiToolStation") && trace[2].getMethodName().equals("updateDisplay")) {
				return false;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		return true;
	}

	/* Updating */

	@Override
	public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
	}

	@Override
	public void onArmorTick(ItemStack tool, World world, EntityPlayer player) {
	}

	/* Mining & Harvesting */

	@Override
	public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
	}

	@Override
	public void beforeBlockBreak(ItemStack tool, BlockEvent.BreakEvent event) {
	}

	@Override
	public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
	}

	@Override
	public void blockHarvestDrops(ItemStack tool, BlockEvent.HarvestDropsEvent event) {
	}

	/* Attacking */

	@Override
	public boolean isCriticalHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target) {
		return false;
	}

	@Override
	public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
		return newDamage;
	}

	@Override
	public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
	}

	@Override
	public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
	}

	@Override
	public float knockBack(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float knockback, float newKnockback, boolean isCritical) {
		return newKnockback;
	}

	@Override
	public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
	}

	/* Durability and repairing */

	@Override
	public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
		return newDamage;
	}

	@Override
	public int onToolHeal(ItemStack tool, int amount, int newAmount, EntityLivingBase entity) {
		return newAmount;
	}

	@Override
	public void onRepair(ItemStack tool, int amount) {
	}

	/* Modifier things */

	// The name the modifier tag is saved under
	public String getModifierIdentifier() {
		return identifier;
	}

	@Override
	public boolean canApplyCustom(ItemStack stack) throws TinkerGuiException {
		// can only apply if the trait isn't present already
		NBTTagList tagList = TagUtil.getTraitsTagList(stack);
		int index = TinkerUtil.getIndexInList(tagList, this.getIdentifier());

		// not present yet
		return index < 0;
	}

	@Override
	public void updateNBT(NBTTagCompound modifierTag) {
		updateNBTforTrait(modifierTag, color);
	}

	public void updateNBTforTrait(NBTTagCompound modifierTag, int newColor) {
		ModifierNBT data = ModifierNBT.readTag(modifierTag);
		data.identifier = getModifierIdentifier();
		data.color = newColor;
		// we ensure at least lvl1 for compatibility with the level-aspect
		if (data.level == 0) {
			data.level = 1;
		}
		data.write(modifierTag);
	}

	@Override
	public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
		// Set the key for easy lookup
		rootCompound.setString(ReforgeHelper.REFORGE_KEY, getModifierIdentifier());
		
		// add the trait to the traitlist so it gets processed
		NBTTagList traits = TagUtil.getTraitsTagList(rootCompound);
		// if it's not already present
		for (int i = 0; i < traits.tagCount(); i++) {
			if (identifier.equals(traits.getStringTagAt(i))) {
				return;
			}
		}

		traits.appendTag(new NBTTagString(identifier));
		TagUtil.setTraitsTagList(rootCompound, traits);
	}

	@Override
	public void apply(NBTTagCompound root) {
		// remove the previous reforge
		ReforgeHelper.removeReforge(root);

		// do the normal stuff
		super.apply(root);
	}

	public void cleanUp(NBTTagCompound root) {

	}

	public int getColor() {
		return color;
	}
}
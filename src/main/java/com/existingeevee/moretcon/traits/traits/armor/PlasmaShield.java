package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.ModInfo;
import com.existingeevee.moretcon.client.MTRenderLayerHelper;
import com.existingeevee.moretcon.client.MTRenderLayerHelper.MTRenderer;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.lib.armor.ArmorModifications;
import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class PlasmaShield extends AbstractArmorTrait {

	protected static PlasmaShield instance;

	public PlasmaShield() {
		super(MiscUtils.createNonConflictiveName("plasma_shield"), TextFormatting.WHITE);
		instance = this;

		MTRenderLayerHelper.INSTANCE.registerRenderer(new PlasmaShieldRenderer());
	}

	@Override
	public ArmorModifications getModifications(EntityPlayer player, ArmorModifications mods, ItemStack armor, DamageSource source, double damage, int slot) {
		mods.effective += 0.05f * player.getAbsorptionAmount();
		return mods;
	}

	@Override
	public void onArmorRemoved(ItemStack armor, EntityPlayer player, int slot) {
		ItemStack newStack = player.inventory.armorInventory.get(slot);
		if (ToolHelper.isBroken(newStack) || !this.isToolWithTrait(newStack))
			player.setAbsorptionAmount(Math.max(0, player.getAbsorptionAmount() - 2.5f));
	}

	@Override
	public void onAbilityTick(int level, World world, EntityPlayer player) {
		long lastAttackTime = ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, player, "field_189751_bG");
		long timeStinceLastAttack = world.getTotalWorldTime() - lastAttackTime;

		if (timeStinceLastAttack > 15 * 20) {
			int absorptionLevel = player.isPotionActive(MobEffects.ABSORPTION) ? player.getActivePotionEffect(MobEffects.ABSORPTION).getAmplifier() + 1 : 0;

			float maxAbsorption = absorptionLevel * 4 + level * 2.5f;
			if (player.getAbsorptionAmount() < maxAbsorption)
				player.setAbsorptionAmount(Math.min(maxAbsorption, player.getAbsorptionAmount() + 1));
		}
	}

	public static class PlasmaShieldRenderer extends MTRenderer {

		@SideOnly(Side.CLIENT)
		private static final ModelPlayer playerModelThick = new ModelPlayer(1.2F, false);
		@SideOnly(Side.CLIENT)
		private static final ResourceLocation SHIELD = new ResourceLocation(ModInfo.MODID, "textures/other/plasma_armor.png");

		@Override
		@SideOnly(Side.CLIENT)
		public void doRenderLayer(RenderPlayer render, double state, AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
			GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());
			render.bindTexture(SHIELD);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float f = (float) entitylivingbaseIn.ticksExisted + partialTicks;
			float mult = (float) Math.min(2, state / 10f);
			if (mult > 1) {
				mult = (float) -Math.exp(-0.25 * (mult - 1)) + 2;
			}
			float f2 = f * 0.03F * mult;
			
			GlStateManager.translate(0, f2, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			float f3 = 0.5F * (float) mult;
			GlStateManager.color(f3, f3, f3, 1f);
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			playerModelThick.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
			playerModelThick.setModelAttributes(render.getMainModel());
			Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
			playerModelThick.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.color(1f, 1f, 1f, 1f);
		}

		@Override
		protected String getID() {
			return instance.getIdentifier();
		}

		@Override
		@SideOnly(Side.CLIENT)
		public double calcRenderState(AbstractClientPlayer entitylivingbaseIn) {
			if (entitylivingbaseIn.getAbsorptionAmount() <= 0)
				return -1;

			boolean worn = false;

			for (ItemStack stack : entitylivingbaseIn.getArmorInventoryList()) {
				if (instance.isToolWithTrait(stack) && !ToolHelper.isBroken(stack)) {
					worn = true;
					break;
				}
			}

			return worn ? entitylivingbaseIn.getAbsorptionAmount() : -1;
		}

	}
}
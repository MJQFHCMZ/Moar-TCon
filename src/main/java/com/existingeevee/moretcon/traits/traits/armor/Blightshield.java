package com.existingeevee.moretcon.traits.traits.armor;

import com.existingeevee.moretcon.MoreTCon;
import com.existingeevee.moretcon.client.MTRenderLayerHelper;
import com.existingeevee.moretcon.client.MTRenderLayerHelper.MTRenderer;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.common.armor.utils.ArmorHelper;
import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class Blightshield extends AbstractArmorTrait {

	protected static Blightshield instance;

	public Blightshield() {
		super(MiscUtils.createNonConflictiveName("blightshield"), TextFormatting.WHITE);
		MinecraftForge.EVENT_BUS.register(this);
		instance = this;
		
		MTRenderLayerHelper.INSTANCE.registerRenderer(new BlightshieldRenderer());
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingHurtEvent(LivingAttackEvent event) {
		if (!event.getSource().isProjectile() || event.getEntityLiving().getHealth() > event.getEntityLiving().getMaxHealth() * 0.5 || !(event.getEntityLiving() instanceof EntityPlayer))
			return;

		int level = (int) Math.round(ArmorHelper.getArmorAbilityLevel((EntityPlayer) event.getEntityLiving(), this.identifier));

		if (level <= 0)
			return;

		event.setCanceled(true);
	}

	public static class BlightshieldRenderer extends MTRenderer {

		@SideOnly(Side.CLIENT)
		private static ModelPlayer playerModelThick;
		@SideOnly(Side.CLIENT)
		private static ResourceLocation witherArmor;

		@Override
		@SideOnly(Side.CLIENT)
		public void doRenderLayer(RenderPlayer render, double state, AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
			GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());
			render.bindTexture(witherArmor);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float f = (float) entitylivingbaseIn.ticksExisted + partialTicks;
			float f1 = MathHelper.cos(f * 0.02F) * 3.0F;
			float f2 = f * 0.01F;
			GlStateManager.translate(f1, f2, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			float f3 = 0.5F;
			GlStateManager.color(f3, f3, f3	, 1.0F);
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
			if (entitylivingbaseIn.getHealth() > entitylivingbaseIn.getMaxHealth() * 0.5)
				return -1;

			boolean worn = false;

			for (ItemStack stack : entitylivingbaseIn.getArmorInventoryList()) {
				if (instance.isToolWithTrait(stack) && !ToolHelper.isBroken(stack)) {
					worn = true;
					break;
				}
			}
			
			return worn ? 1 : -1;
		}
		
		static {
			if (MoreTCon.proxy.isClient()) {
				playerModelThick = new ModelPlayer(1.25F, false);
				witherArmor = new ResourceLocation("textures/entity/wither/wither_armor.png");
			}
		}
		
	}
}
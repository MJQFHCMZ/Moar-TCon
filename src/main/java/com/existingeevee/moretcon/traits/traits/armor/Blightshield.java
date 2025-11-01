package com.existingeevee.moretcon.traits.traits.armor;

import java.util.List;

import com.existingeevee.moretcon.other.utils.MirrorUtils;
import com.existingeevee.moretcon.other.utils.MirrorUtils.IField;
import com.existingeevee.moretcon.other.utils.MirrorUtils.IMethod;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import c4.conarm.common.armor.utils.ArmorHelper;
import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
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
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingHurtEvent(LivingAttackEvent event) {
		if (!event.getSource().isProjectile() || event.getEntityLiving().getHealth() > event.getEntityLiving().getMaxHealth() * 0.5)
			return;

		int level = (int) Math.round(ArmorHelper.getArmorAbilityLevel((EntityPlayer) event.getEntityLiving(), this.identifier));

		if (level <= 0)
			return;

		event.setCanceled(true);
	}

	@SideOnly(Side.CLIENT)
	public static final IMethod<ItemStack> getArrowStack$EntityArrow = MirrorUtils.reflectObfusMethod(EntityArrow.class, "func_184550_j", ItemStack.class);
	@SideOnly(Side.CLIENT)
	public static final IField<List<LayerRenderer<?>>> renderLivingBase$layerRenderers = MirrorUtils.reflectObfusField(RenderLivingBase.class, "field_177097_h");

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onJoinWorld(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();

		if (entity.world.isRemote) {

			RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
			Render<? extends Entity> render = renderManager.getEntityRenderObject(entity);

			if (!(render instanceof RenderPlayer)) {
				return;
			}

			RenderPlayer entityRender = (RenderPlayer) render;
			List<LayerRenderer<?>> layerRenderers = renderLivingBase$layerRenderers.get(entityRender);
			for (LayerRenderer<?> layerRenderer : layerRenderers) {
				if (layerRenderer instanceof LayerWitherShieldPlayer) {
					return;
				}
			}
			entityRender.addLayer(new LayerWitherShieldPlayer(entityRender));
		}
	}

	@SideOnly(Side.CLIENT)
	public static class LayerWitherShieldPlayer implements LayerRenderer<AbstractClientPlayer> {

		private static final ModelPlayer playerModelThick = new ModelPlayer(1.25F, false);

		private static final ResourceLocation WITHER_ARMOR = new ResourceLocation("textures/entity/wither/wither_armor.png");

		protected final RenderPlayer render;
		public LayerWitherShieldPlayer(RenderPlayer render) {
	    	this.render = render;
	    }

		@Override
		public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

			if (entitylivingbaseIn.getHealth() > entitylivingbaseIn.getMaxHealth() * 0.5)
				return;

			boolean worn = false;

			for (ItemStack stack : entitylivingbaseIn.getArmorInventoryList()) {
				if (instance.isToolWithTrait(stack) && !ToolHelper.isBroken(stack)) {
					worn = true;
					break;
				}
			}

			if (!worn)
				return;
			
			GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());
			render.bindTexture(WITHER_ARMOR);
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
		}

		@Override
		public boolean shouldCombineTextures() {
			return false;
		}

	}
}
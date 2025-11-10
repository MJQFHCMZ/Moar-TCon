package com.existingeevee.moretcon.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.existingeevee.moretcon.ModInfo;
import com.existingeevee.moretcon.MoreTCon;
import com.existingeevee.moretcon.other.utils.MirrorUtils;
import com.existingeevee.moretcon.other.utils.MirrorUtils.IField;
import com.existingeevee.moretcon.other.utils.MirrorUtils.IMethod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MTRenderLayerHelper {

	public static final MTRenderLayerHelper INSTANCE = MoreTCon.proxy.isClient() ? new MTRenderLayerHelperImpl() : new MTRenderLayerHelper();
	
	public void registerRenderer(MTRenderer renderer) {
		
	}
	
	@EventBusSubscriber(modid = ModInfo.MODID)
	protected static class MTRenderLayerHelperImpl extends MTRenderLayerHelper {
		protected static final Map<EntityPlayer, Map<MTRenderer, Double>> RENDER_STATE_TRACKER = new WeakHashMap<>();
		protected static final Map<String, MTRenderer> RENDERER = new HashMap<>();

		@SideOnly(Side.CLIENT)
		private static final IMethod<ItemStack> getArrowStack$EntityArrow = MirrorUtils.reflectObfusMethod(EntityArrow.class, "func_184550_j", ItemStack.class);
		@SideOnly(Side.CLIENT)
		private static final IField<List<LayerRenderer<?>>> renderLivingBase$layerRenderers = MirrorUtils.reflectObfusField(RenderLivingBase.class, "field_177097_h");

		public void registerRenderer(MTRenderer renderer) {
			RENDERER.put(renderer.getID(), renderer);
		}
		
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
			if (event.phase != Phase.START || event.side.isServer() || !(event.player instanceof AbstractClientPlayer))
				return;
			
			Map<MTRenderer, Double> map = RENDER_STATE_TRACKER.computeIfAbsent(event.player, p -> new HashMap<>());
			
			for (MTRenderer renderer : RENDERER.values()) {
				double state = renderer.calcRenderState((AbstractClientPlayer) event.player);
				map.put(renderer, state);
			}
		}
		
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public static void onJoinWorld(EntityJoinWorldEvent event) {
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
					if (layerRenderer instanceof LayerMTRender) {
						return;
					}
				}
				entityRender.addLayer(new LayerMTRender(entityRender));
			}
		}
		
		@SideOnly(Side.CLIENT)
		public static class LayerMTRender implements LayerRenderer<AbstractClientPlayer> {

			protected final RenderPlayer render;

			public LayerMTRender(RenderPlayer render) {
				this.render = render;
			}

			@Override
			public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				Map<MTRenderer, Double> map = RENDER_STATE_TRACKER.computeIfAbsent(entitylivingbaseIn, p -> new HashMap<>());

				for (MTRenderer renderer : RENDERER.values()) {
					double state = map.get(renderer);
					if (renderer.shouldRender(entitylivingbaseIn, state)) {
						renderer.doRenderLayer(render, state, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
					}
				}
			}

			@Override
			public boolean shouldCombineTextures() {
				return false;
			}
		}
	}

	public abstract static class MTRenderer {

		@SideOnly(Side.CLIENT)
		public abstract void doRenderLayer(RenderPlayer render, double renderState, AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale);
		
		protected abstract String getID();

		@SideOnly(Side.CLIENT)
		public boolean shouldRender(AbstractClientPlayer entitylivingbaseIn, double renderState) {
			return renderState > 0;
		}
		
		@SideOnly(Side.CLIENT)
		public abstract double calcRenderState(AbstractClientPlayer entitylivingbaseIn); 
	}
}

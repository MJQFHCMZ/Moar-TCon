package com.existingeevee.moretcon.world.generators;

import java.awt.Color;

import com.existingeevee.math.noise.SimplexNoiseGenerator;
import com.existingeevee.moretcon.NetworkHandler;
import com.existingeevee.moretcon.inits.ModBlocks;
import com.existingeevee.moretcon.other.utils.MiscUtils;
import com.existingeevee.moretcon.world.WorldGenModifier;
import com.existingeevee.moretcon.world.WorldgenContext;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HelltopIslandsGenerator extends WorldGenModifier {

	private static boolean registeredEventHandler = false;

	public HelltopIslandsGenerator() {
		if (!registeredEventHandler) {
			MinecraftForge.EVENT_BUS.register(HelltopIslandsGenerator.class);
		}
	}

	public static final SimplexNoiseGenerator HELLTOP_ISLANDS_GENERATOR = new SimplexNoiseGenerator(7, 0.6f, 0.00275f, -0.8);

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase != Phase.END || event.world.getWorldTime() % 20 != 0) {
			return;
		}

		for (EntityPlayer player : event.world.playerEntities) {

			if (!(player instanceof EntityPlayerMP))
				return;

			double x = player.posX, z = player.posZ;

			double noise = HELLTOP_ISLANDS_GENERATOR.generateOctavedSimplexNoise(x, z, event.world.getSeed()) * 1d;

			double expFactor = -1.0e-6; // -(2/1000)^2
			double mulFactor = -50;

			noise += mulFactor * Math.pow(Math.E, expFactor * (x * x + z * z));

			boolean inHelltop = event.world.provider.getDimensionType().getId() == DimensionType.NETHER.getId() && player.posY >= 128 && player.posY < 170 && noise > -0.4;

			NetworkHandler.HANDLER.sendTo(new HelltopStatusMessage(inHelltop), (EntityPlayerMP) player);
		}
	}

	public static final Color FOG_COLOR = new Color(0xe8e0ff);
	private static boolean inHelltopIslandRegion = false;
	private static int curFogP = 0;
	private static int prevFogP = 0;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onClientTick(ClientTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();

		prevFogP = curFogP;

		curFogP = Math.max(0, Math.min(60, curFogP + (inHelltopIslandRegion ? 1 : -1)));

		if (event.phase != Phase.END || mc.world == null || mc.player == null || !inHelltopIslandRegion)
			return;

		for (int i = 0; i < 10; i++) {
			double rX = MiscUtils.randomN1T1() * 15;
			double rY = MiscUtils.randomN1T1() * 15;
			double rZ = MiscUtils.randomN1T1() * 15;

			double x = mc.player.posX + rX, y = Math.max(128, mc.player.posY + rY), z = mc.player.posZ + rZ;

			mc.world.spawnParticle(EnumParticleTypes.END_ROD, x, y, z, 0, 0, 0);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		inHelltopIslandRegion = false;
		prevFogP = 0;
		curFogP = 0;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onFogColors(FogColors event) {
		float renderFogP = curFogP + (curFogP - prevFogP) * Minecraft.getMinecraft().getRenderPartialTicks();

		renderFogP /= 60;
		
		if (renderFogP < 1e-6)
			return;

		float r = FOG_COLOR.getRed() * renderFogP / 255f + event.getRed() * (1 - renderFogP);
		float g = FOG_COLOR.getGreen() * renderFogP / 255f + event.getGreen() * (1 - renderFogP);
		float b = FOG_COLOR.getBlue() * renderFogP / 255f + event.getBlue() * (1 - renderFogP);

		event.setRed(r * 1.2f);
		event.setGreen(g * 1.2f);
		event.setBlue(b * 1.2f);
	}

	@Override
	public void generate(IChunkGenerator chunkGenerator, IChunkProvider chunkProvider, WorldgenContext ctx) {
		World world = ctx.world;
		int chunkX = ctx.chunkX;
		int chunkZ = ctx.chunkZ;

		if (world.provider.getDimensionType().getId() != DimensionType.NETHER.getId()) {
			return;
		}

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int x = i + chunkX * 16 + 8;
				int z = j + chunkZ * 16 + 8;

				double noise = HELLTOP_ISLANDS_GENERATOR.generateOctavedSimplexNoise(x, z, world.getSeed()) * 1d;

				double expFactor = -1.0e-6; // -(2/1000)^2
				double mulFactor = -50;

				noise += mulFactor * Math.pow(Math.E, expFactor * (x * x + z * z));

				if (noise < -0.5)
					continue;

				int height = noiseToHeight(noise);
				for (int y = 128; y < 128 + height; y++) {
					BlockPos pos = new BlockPos(x, y, z);
					if (!canReplace(world, pos))
						continue;
					if (noise < -0.2) {
						world.setBlockState(pos, ModBlocks.blockBrokenSand.getDefaultState(), 2);
					} else {
						if (y == 128 + height - 1) {
							world.setBlockState(pos, ModBlocks.blockMossyBrinkstone.getDefaultState(), 2);
						} else {
							world.setBlockState(pos, ModBlocks.blockBrinkstone.getDefaultState(), 2);
						}
					}
				}
			}
		}
	}

	public static class HelltopStatusMessage implements IMessage, IMessageHandler<HelltopStatusMessage, IMessage> {
		boolean inHelltop;

		public HelltopStatusMessage() {
			this(false);
		}

		public HelltopStatusMessage(boolean inHelltop) {
			this.inHelltop = inHelltop;
		}

		@Override
		public IMessage onMessage(HelltopStatusMessage message, MessageContext ctx) {
			inHelltopIslandRegion = message.inHelltop;
			return null;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			inHelltop = buf.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeBoolean(inHelltop);
		}
	}

	private static int noiseToHeight(double noise) { // TODO mess with this
		return (int) Math.ceil((noise + 0.5) * 15 + 0.01);
	}

	private static boolean canReplace(World world, BlockPos pos) {
		IBlockState iblockstate = world.getBlockState(pos);
		Block block = iblockstate.getBlock();

		return block.isAir(iblockstate, world, pos) || block instanceof BlockBush; // i hate the stupid mushrooms grahhhh
	}
}

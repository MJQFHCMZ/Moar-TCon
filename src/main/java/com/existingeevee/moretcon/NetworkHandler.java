package com.existingeevee.moretcon;

import com.existingeevee.moretcon.client.actions.ClientAction.SentClientActionMessage;
import com.existingeevee.moretcon.effects.PotionBleeding.BleedingEffectMessage;
import com.existingeevee.moretcon.other.ExtendedAttackMessage;
import com.existingeevee.moretcon.other.fires.CustomFireHelper.SyncCustomFiresMessage;
import com.existingeevee.moretcon.other.recoil.MessageSendRecoil;
import com.existingeevee.moretcon.traits.traits.Afterimage.AfterimageMessage;
import com.existingeevee.moretcon.world.generators.HelltopIslandsGenerator.HelltopStatusMessage;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

	public static final SimpleNetworkWrapper HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MODID);
	private static int i = 0;

	public static void init() {
		HANDLER.registerMessage(BleedingEffectMessage.class, BleedingEffectMessage.class, i++, Side.CLIENT);
		HANDLER.registerMessage(AfterimageMessage.class, AfterimageMessage.class, i++, Side.CLIENT);
		HANDLER.registerMessage(SyncCustomFiresMessage.class, SyncCustomFiresMessage.class, i++, Side.CLIENT);
		HANDLER.registerMessage(SentClientActionMessage.class, SentClientActionMessage.class, i++, Side.CLIENT);
		HANDLER.registerMessage(ExtendedAttackMessage.class, ExtendedAttackMessage.class, i++, Side.SERVER);
		HANDLER.registerMessage(HelltopStatusMessage.class, HelltopStatusMessage.class, i++, Side.CLIENT);
		HANDLER.registerMessage(MessageSendRecoil.class, MessageSendRecoil.class, i++, Side.CLIENT);
	}

}

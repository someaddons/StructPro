package com.ternsip.structpro.universe.general.network;

import com.ternsip.structpro.Structpro;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Network {

    private static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Structpro.MODID);
    private static int DISCRIMINATOR = 0;

    public static <T extends Packet> void registerNetworkHandler(IMessageHandler<T, ?> messageHandler, Class<T> packetClazz, Side side) {
        NETWORK.registerMessage(messageHandler, packetClazz, DISCRIMINATOR++, side);
    }

    public static void sendTo(Packet packet, EntityPlayerMP entityPlayerMP) {
        NETWORK.sendTo(packet, entityPlayerMP);
    }

    public static void sendToAllAround(Packet packet, NetworkRegistry.TargetPoint point) {
        NETWORK.sendToAllAround(packet, point);
    }

    public static void sendToServer(Packet packet) {
        NETWORK.sendToServer(packet);
    }

}

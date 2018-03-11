package com.ternsip.structpro.universe.general.proxy;

import com.ternsip.structpro.universe.general.network.Packet;
import com.ternsip.structpro.universe.general.network.ServerHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import static com.ternsip.structpro.universe.general.network.Network.registerNetworkHandler;

@SuppressWarnings("unused")
public class ProxyServer extends ProxyBase {

    @Override
    public void preInitialization(FMLPreInitializationEvent event) {
        super.preInitialization(event);
        registerNetworkHandler(new ServerHandler(), Packet.class, Side.SERVER);
    }

}

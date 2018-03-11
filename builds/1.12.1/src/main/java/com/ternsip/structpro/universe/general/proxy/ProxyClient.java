package com.ternsip.structpro.universe.general.proxy;

import com.ternsip.structpro.universe.general.gui.Renderer;
import com.ternsip.structpro.universe.general.network.ClientHandler;
import com.ternsip.structpro.universe.general.network.Packet;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import static com.ternsip.structpro.universe.general.network.Network.registerNetworkHandler;

@SuppressWarnings("unused")
public class ProxyClient extends ProxyBase {

    @Override
    public void preInitialization(FMLPreInitializationEvent event) {
        super.preInitialization(event);
        registerEventBusHandler(new Renderer());
        registerNetworkHandler(new ClientHandler(), Packet.class, Side.CLIENT);
    }

}

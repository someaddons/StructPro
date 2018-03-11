package com.ternsip.structpro.universe.general.network;

import com.ternsip.structpro.universe.general.gui.Renderer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class ClientHandler implements IMessageHandler<Packet, IMessage> {

    @Override
    public IMessage onMessage(Packet packet, MessageContext ctx) {

        if (packet.getType() == Packet.Type.RELOAD_CHUNKS) {
            Renderer.RELOAD_CHUNKS = true;
        }

        // No response packet
        return null;
    }


}

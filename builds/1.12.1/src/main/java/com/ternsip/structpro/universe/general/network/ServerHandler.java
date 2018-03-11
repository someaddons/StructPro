package com.ternsip.structpro.universe.general.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerHandler implements IMessageHandler<Packet, IMessage> {

    @Override
    public IMessage onMessage(Packet packet, MessageContext ctx) {

        // No response packet
        return null;
    }

}

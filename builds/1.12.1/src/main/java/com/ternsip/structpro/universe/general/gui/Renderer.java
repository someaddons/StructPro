package com.ternsip.structpro.universe.general.gui;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Renderer {

    public static boolean RELOAD_CHUNKS = false;

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        if (RELOAD_CHUNKS) {
            event.getContext().loadRenderers();
            RELOAD_CHUNKS = false;
        }
    }

}

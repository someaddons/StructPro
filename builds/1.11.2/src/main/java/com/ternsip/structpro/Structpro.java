package com.ternsip.structpro;

import com.ternsip.structpro.Logic.Pregen;
import com.ternsip.structpro.Universe.Commands.Commands;
import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Universe.Generation.Decorator;
import com.ternsip.structpro.Universe.Items.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;

/**
 * Main mod class. Forge will handle all registered events
 * @author Ternsip
 * @since JDK 1.6
 */
@Mod(   modid = Structpro.MODID,
        name = Structpro.MODNAME,
        version = Structpro.VERSION,
        acceptableRemoteVersions = "*")
@SuppressWarnings({"WeakerAccess", "unused"})
public class Structpro {

    public static final String MODID = "structpro";
    public static final String MODNAME = "StructPro";
    public static final String VERSION = "3.8";
    public static final String AUTHOR = "Ternsip";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Configurator.configure(new File("config/structpro.cfg"));
        GameRegistry.registerWorldGenerator(new Decorator(), 4096);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new Commands());
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        Pregen.tick();
    }

    @SubscribeEvent
    @SuppressWarnings({"ConstantConditions"})
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (    event.getPlayer().getHeldItemMainhand() != null &&
                event.getPlayer().getHeldItemMainhand().getItem() == Items.WOODEN_AXE &&
                event.getPlayer().isCreative()) {
            event.setCanceled(true);
            Commands.touch(event.getPlayer(), event.getPos());
        }
    }

}

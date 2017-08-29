package com.ternsip.structpro;

import com.ternsip.structpro.logic.Configurator;
import com.ternsip.structpro.logic.generation.Pregen;
import com.ternsip.structpro.universe.blocks.UBlockPos;
import com.ternsip.structpro.universe.commands.Commands;
import com.ternsip.structpro.universe.commands.Evaluator;
import com.ternsip.structpro.universe.generation.Decorator;
import com.ternsip.structpro.universe.items.UItem;
import com.ternsip.structpro.universe.items.UItems;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import java.io.File;

/**
 * Main mod class. Forge will handle all registered events
 * @author Ternsip
 */
@Mod(   modid = Structpro.MODID,
        name = Structpro.MODNAME,
        version = Structpro.VERSION,
        acceptedMinecraftVersions = "*",
        acceptableRemoteVersions = "*")
@SuppressWarnings({"WeakerAccess", "unused"})
public class Structpro {

    public static final String MODID = "structpro";
    public static final String MODNAME = "StructPro";
    public static final String VERSION = "4.0";
    public static final String AUTHOR = "Ternsip";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Configurator.configure(new File("config/" + MODID + ".cfg"));
        GameRegistry.registerWorldGenerator(new Decorator(), 4096);
        FMLCommonHandler.instance().bus().register(this);
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
        if (    event.getPlayer().getHeldItem() != null &&
                new UItem(event.getPlayer().getHeldItem().getItem()).getId() == UItems.WOODEN_HOE.getId() &&
                event.getPlayer().capabilities.isCreativeMode) {
            event.setCanceled(true);
            Evaluator.touchBlock(event.getPlayer(), new UBlockPos(event.x, event.y, event.z));
        }
    }

}

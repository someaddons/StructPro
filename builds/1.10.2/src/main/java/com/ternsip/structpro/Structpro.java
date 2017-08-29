package com.ternsip.structpro;

import com.ternsip.structpro.logic.Configurator;
import com.ternsip.structpro.logic.generation.Pregen;
import com.ternsip.structpro.universe.blocks.UBlockPos;
import com.ternsip.structpro.universe.commands.Commands;
import com.ternsip.structpro.universe.commands.Evaluator;
import com.ternsip.structpro.universe.generation.Decorator;
import com.ternsip.structpro.universe.items.UItem;
import com.ternsip.structpro.universe.items.UItems;
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
                new UItem(event.getPlayer().getHeldItemMainhand().getItem()).getId() == UItems.WOODEN_HOE.getId() &&
                event.getPlayer().isCreative()) {
            event.setCanceled(true);
            Evaluator.touchBlock(event.getPlayer(), new UBlockPos(event.getPos()));
        }
    }

}

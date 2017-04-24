package com.ternsip.structpro;

import com.ternsip.structpro.Universe.Commands.Commands;
import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Universe.Generation.Decorator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
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
    public static final String VERSION = "3.0";
    public static final String AUTHOR = "Ternsip";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Configurator.configure(new File("config/structpro.cfg"));
        GameRegistry.registerWorldGenerator(new Decorator(), 4096);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new Commands());
    }

}

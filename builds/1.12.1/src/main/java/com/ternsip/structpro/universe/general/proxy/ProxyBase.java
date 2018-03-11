package com.ternsip.structpro.universe.general.proxy;

import com.ternsip.structpro.Structpro;
import com.ternsip.structpro.logic.Configurator;
import com.ternsip.structpro.logic.generation.Pregen;
import com.ternsip.structpro.universe.commands.Commands;
import com.ternsip.structpro.universe.commands.Evaluator;
import com.ternsip.structpro.universe.generation.Decorator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;

public abstract class ProxyBase {

    public ProxyBase() {
        registerEventBusHandler(this);
    }

    /* ---- Basic events ---- */

    public void construction(FMLConstructionEvent event) {
    }

    public void fingerPrintViolation(FMLFingerprintViolationEvent event) {
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
    }

    /* ---- Initialization events ---- */

    public void preInitialization(FMLPreInitializationEvent event) {
        registerEventBusHandler(new Evaluator());
        registerEventBusHandler(new Pregen());
    }

    public void initialization(FMLInitializationEvent event) {
        Configurator.configure(new File("config/" + Structpro.MODID + ".cfg"));
        GameRegistry.registerWorldGenerator(new Decorator(), 4096);
    }

    public void postInitialization(FMLPostInitializationEvent event) {
    }

     /* ---- Mod control events ---- */

    public void modDisable(FMLModDisabledEvent event) {
    }

    public void modIdMapping(FMLModIdMappingEvent event) {
    }

     /* ---- Server events ---- */

    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new Commands());
    }

    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
    }

    public void serverStarted(FMLServerStartedEvent event) {
    }

    public void serverStopping(FMLServerStoppingEvent event) {
    }

    public void serverStopped(FMLServerStoppedEvent event) {
    }

     /* ---- Custom methods ---- */

    static void registerEventBusHandler(Object object) {
        MinecraftForge.EVENT_BUS.register(object);
    }
}

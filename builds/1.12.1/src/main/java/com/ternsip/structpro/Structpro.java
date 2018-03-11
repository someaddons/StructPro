package com.ternsip.structpro;

import com.ternsip.structpro.universe.general.proxy.ProxyBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

/**
 * Main mod class. Forge will handle all registered events
 *
 * @author Ternsip
 */
@Mod(modid = Structpro.MODID,
        name = Structpro.MODNAME,
        version = Structpro.VERSION,
        acceptedMinecraftVersions = Structpro.MC_VERSION,
        acceptableRemoteVersions = Structpro.VERSION)
@SuppressWarnings({"WeakerAccess", "unused"})
public class Structpro {

    public static final String MODID = "structpro";
    public static final String MODNAME = "StructPro";
    public static final String MC_VERSION = "1.12";
    public static final String VERSION = "4.2";
    public static final String AUTHOR = "Ternsip";

    @SidedProxy(clientSide = "com.ternsip.structpro.universe.general.proxy.ProxyClient", serverSide = "com.ternsip.structpro.universe.general.proxy.ProxyServer")
    public static ProxyBase proxy;

     /*---- Basic events ----*/

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        proxy.construction(event);
    }

    @Mod.EventHandler
    public void fingerPrintViolation(FMLFingerprintViolationEvent event) {
        proxy.fingerPrintViolation(event);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete(event);
    }

    /*---- Initialization events ----*/

    @Mod.EventHandler
    public void preInitialization(FMLPreInitializationEvent event) {
        proxy.preInitialization(event);
    }

    @Mod.EventHandler
    public void initialization(FMLInitializationEvent event) {
        proxy.initialization(event);
    }

    @Mod.EventHandler
    public void postInitialization(FMLPostInitializationEvent event) {
        proxy.postInitialization(event);
    }

    /*---- Mod events ----*/

    @Mod.EventHandler
    public void modDisable(FMLModDisabledEvent event) {
        proxy.modDisable(event);
    }

    @Mod.EventHandler
    public void modIdMapping(FMLModIdMappingEvent event) {
        proxy.modIdMapping(event);
    }

    /*---- Server events ----*/

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        proxy.serverAboutToStart(event);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        proxy.serverStarted(event);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        proxy.serverStopping(event);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        proxy.serverStopped(event);
    }

}

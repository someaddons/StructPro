package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Projector;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import com.ternsip.structpro.WorldCache.WorldCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.Random;

/* Evaluates commands */
class Evaluator {

    /* Paste schematic that has most similar name */
    static String cmdPaste(World world,
                           final String name,
                           int posX, int posY, int posZ,
                           int rotateX, int rotateY, int rotateZ,
                           boolean flipX, boolean flipY, boolean flipZ,
                           boolean village) {
        long seed = System.currentTimeMillis();
        if (village) {
            ArrayList<Projector> projectors = Utils.select(Structures.selectVillages(name), seed);
            if (projectors == null || projectors.size() == 0) {
                return "No matching villages";
            }
            Report report = Distributor.spawnVillage(world, projectors, posX / 16, posZ / 16, new Random(seed));
            report.print();
            return report.toString();
        } else {
            ArrayList<Projector> candidates = new ArrayList<Projector>(){{
                addAll(Structures.select(name));
                addAll(Structures.selectSaves(name));
            }};
            Projector projector = Utils.select(candidates, seed);
            if (projector == null) {
                return "No matching structures";
            }
            Report report = projector.paste(world, posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, 0);
            report.print();
            return report.toString();
        }
    }

    /* Save schematic */
    static String cmdSave(World world, String name, int posX, int posY, int posZ, int width, int height, int length) {
        Report report = Distributor.saveStructure(world, name, posX, posY, posZ, width, height, length);
        report.print();
        return report.toString();
    }

    static String cmdGenerate(World world, int radius, int step) {
        int total = (2 * radius + 1) * (2 * radius + 1), completed = 0;
        if (!(world.getChunkProvider() instanceof ChunkProviderServer)) {
            return "You can't use this command on client";
        }
        ChunkProviderServer cps = (ChunkProviderServer) world.getChunkProvider();
        for (int scx = -radius; scx <= radius; scx += step) {
            for (int scz = -radius; scz <= radius; scz += step) {
                int ecx = Math.min(scx + step - 1, radius);
                int ecz = Math.min(scz + step - 1, radius);
                completed += (ecx + 1 - scx) * (ecz + 1 - scz);
                new Report().add("Generating", (100.0 * completed) / total + "%").print();
                for (int cx = scx; cx <= ecx; ++cx) {
                    for (int cz = scz; cz <= ecz; ++cz) {
                        if (Distributor.getDrops(world, cx, cz, false) > 0 ||
                                Distributor.getDrops(world, cx, cz, true) > 0) {
                            Chunk chunk = cps.provideChunk(cx, cz);
                            if (!chunk.isTerrainPopulated()) {
                                chunk.checkLight();
                                cps.chunkGenerator.populate(cx, cz);
                                GameRegistry.generateWorld(cx, cz, world, cps.chunkGenerator, world.getChunkProvider());
                                chunk.setChunkModified();
                            }
                        }
                    }
                }
                WorldCache.unload();
            }
        }
        Report report = new Report().add("Generation", "Complete").add("Chunks", String.valueOf(completed));
        report.print();
        return report.toString();
    }

    /* Print command help information */
    static String cmdHelp() {
        return "You can pass arguments by name" +
                "\n" +
                "PASTE SCHEMATIC: /structpro paste " +
                "name=<string> posX=<int> posY=<int> posZ=<int> rotateX=<int> " +
                "rotateY=<int> rotateZ=<int> flipX=<bool> flipY=<bool> flipZ=<bool> village=<bool>" +
                "\n" +
                "SAVE SCHEMATIC: /structpro save " +
                "name=<string> posX=<int> posY=<int> posZ=<int> width=<int> height=<int> length=<int>" +
                "\n" +
                "GENERATE WORLD FRAGMENT: /structpro generate step=<int> radius=<int>";
    }

}

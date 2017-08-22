package com.ternsip.structpro.universe.commands;

import com.ternsip.structpro.logic.*;
import com.ternsip.structpro.logic.generation.Construction;
import com.ternsip.structpro.logic.generation.Pregen;
import com.ternsip.structpro.logic.generation.Village;
import com.ternsip.structpro.structure.*;
import com.ternsip.structpro.universe.blocks.UBlockPos;
import com.ternsip.structpro.universe.world.UWorld;
import com.ternsip.structpro.universe.utils.Report;
import com.ternsip.structpro.universe.utils.Utils;
import net.minecraft.client.audio.SoundCategory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Commands evaluator
 * @author  Ternsip
 */
class Evaluator {

    /** Undo projections */
    private static final ArrayList<Projection> undo = new ArrayList<>();

    /**
     * Paste schematic that has most similar name
     * @param uWorld Target world
     * @param name Structure name
     * @param posX X starting position
     * @param posY Y starting position
     * @param posZ Z starting position
     * @param rotateX X axis rotation
     * @param rotateY Y axis rotation
     * @param rotateZ Z axis rotation
     * @param flipX X axis flip
     * @param flipY Y axis flip
     * @param flipZ Z axis flip
     * @param isVillage Paste entire village
     * @param isInsecure Projection will be insecure
     * @return Execution status
     */
    static String cmdPaste(UWorld uWorld,
                           String name,
                           int posX, int posY, int posZ,
                           int rotateX, int rotateY, int rotateZ,
                           boolean flipX, boolean flipY, boolean flipZ,
                           boolean isVillage, boolean isInsecure) {
        final Pattern nPattern = Pattern.compile(".*" + Pattern.quote(name) + ".*", Pattern.CASE_INSENSITIVE);
        undo.clear();
        if (isVillage) {
            ArrayList<Structure> town = Utils.select(Structures.villages.select(nPattern));
            if (town == null) {
                return "§4No matching villages";
            }
            ArrayList<Projection> projections = Village.combine(uWorld, town, posX >> 4, posZ >> 4, System.currentTimeMillis());
            for (Projection projection : projections) {
                saveUndo(projection);
            }
            for (Projection projection : projections) {
                Report report = projection.project(isInsecure);
                report.print();
            }
            return "§2Total spawned:§1 " + projections.size();
        } else {
            ArrayList<Structure> candidates = new ArrayList<Structure>(){{
                addAll(Structures.structures.select(nPattern));
                addAll(Structures.saves.select(nPattern));
            }};
            Structure structure = Utils.select(candidates);
            if (structure == null) {
                return "§4No matching structures";
            }
            Posture posture = structure.getPosture(posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ);
            Projection projection = new Projection(uWorld, structure, posture, System.currentTimeMillis());
            if (posY == 0) {
                try {
                    projection = Construction.calibrate(uWorld, posX, posZ, System.currentTimeMillis(), structure);
                } catch (IOException ioe) {
                    Report report = structure.report().pref(new Report().post("NOT SPAWNED", ioe.getMessage()));
                    report.print();
                    return report.toString();
                }
            }
            saveUndo(projection);
            Report report = projection.project(isInsecure);
            report.print();
            uWorld.sound(new UBlockPos(posX, posY, posZ), "random.click", 1.0f);
            return report.toString();
        }
    }

    /**
     * Save schematic
     * @param uWorld Target world
     * @param name Structure name
     * @param posX X starting position
     * @param posY Y starting position
     * @param posZ Z starting position
     * @param width X axis size
     * @param height Y axis size
     * @param length Z axis size
     * @return Execution status
     */
    static String cmdSave(UWorld uWorld, String name, int posX, int posY, int posZ, int width, int height, int length) {
        Report report = new Report()
                .post("WORLD FRAGMENT", name)
                .post("POS", "[X=" + posX + ";Y=" + posY + ";Z=" + posZ + "]")
                .post("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]");
        try {
            Blueprint blueprint = new Blueprint(uWorld, new UBlockPos(posX, posY, posZ), new Volume(width, height, length));
            File file = new File(Configurator.getSchematicsSavesFolder(), name + ".schematic");
            blueprint.saveSchematic(file);
            Structures.load(file);
            report.post("SAVED", file.getPath());
        } catch (IOException ioe) {
            report.post("NOT SAVED", ioe.getMessage());
        }
        report.print();
        uWorld.sound(new UBlockPos(posX, posY, posZ), "random.chestclosed", 0.5f);
        return report.toString();
    }

    /**
     * Print command help information
     * @return Command execution status
     */
    static String cmdHelp(String chapter) {
        if (chapter.equalsIgnoreCase("paste")) {
            return "§a/spro paste §d\n" +
                    "wand=<bool> auto=<bool> insecure=<bool> name=<string> village=<bool> " +
                    "posX=<int> posY=<int> posZ=<int> " +
                    "rotateX=<int> rotateY=<int> rotateZ=<int> " +
                    "flipX=<bool> flipY=<bool> flipZ=<bool>";
        }
        if (chapter.equalsIgnoreCase("save")) {
            return "§a/spro save §d\n" +
                    "name=<string> " +
                    "posX=<int> posY=<int> posZ=<int> " +
                    "width=<int> height=<int> length=<int>";
        }
        if (chapter.equalsIgnoreCase("undo")) {
            return "§a/spro undo";
        }
        if (chapter.equalsIgnoreCase("gen")) {
            return "§a/spro gen §d\n" +
                    "size=<int> step=<int> skip=<bool> progress=<int> " +
                    "sx=<int> sz=<int> " +
                    "stop=<bool>";
        }
        return  "§2You can pass arguments by name \n" +
                "§9PASTE SCHEMATIC:§a /spro paste \n" +
                "§9SAVE SCHEMATIC:§a /spro save \n" +
                "§9UNDO LAST ACTION:§a /spro undo \n" +
                "§9GENERATE WORLD:§a /spro gen";
    }

    /**
     * Undo all session history
     * All projections applied insecure to restore data 1:1
     * @return Command execution status
     */
    static String cmdUndo() {
        if (undo.isEmpty()) {
            return "§4No undo data";
        }
        for (Projection projection : undo) {
            projection.project(true).print();
            Posture pst = projection.getPosture();
            projection.getWorld().sound(new UBlockPos(pst.getPosX(), pst.getPosY(), pst.getPosZ()), "mob.endermen.portal", 1.0f);
        }
        undo.clear();
        return "§2Undo done";
    }

    /**
     * Start pre-generation routine
     * @param uWorld Target world
     * @param startX Starting chunk X coordinate
     * @param startZ Starting chunk Z coordinate
     * @param step Number of chunks to process per step
     * @param size Number of chunks for x and z axis in each direction
     * @param stop Deactivate generation
     * @param skip Skip chunks with no structures
     * @param progress Start generation progress from given number
     * @return Command execution status
     */
    static String cmdGen(UWorld uWorld, int startX, int startZ,int step, int size, boolean stop, boolean skip, int progress) {
        if (stop) {
            Pregen.deactivate();
            return "§4Generation process interrupted";
        } else {
            Pregen.activate(uWorld, startX, startZ, step, size, skip, progress);
            return "§2Generation process started";
        }
    }

    /**
     * Save undo data to history for projection
     * @param projection Projection to save
     */
    private static void saveUndo(Projection projection) {
        if (projection.getBlueprint() instanceof Structure) {
            Posture mp = projection.getPosture().extend(Structure.MELT, Structure.MELT, Structure.MELT);
            UBlockPos start = new UBlockPos(mp.getPosX(), mp.getPosY(), mp.getPosZ());
            Volume volume = new Volume(mp.getSizeX(), mp.getSizeY(), mp.getSizeZ());
            Blueprint blueprint = new Blueprint(projection.getWorld(), start, volume);
            Posture posture = blueprint.getPosture(start.getX(), start.getY(), start.getZ(), 0, 0, 0, false, false, false);
            undo.add(new Projection(projection.getWorld(), blueprint, posture, 0));
        }
    }

}

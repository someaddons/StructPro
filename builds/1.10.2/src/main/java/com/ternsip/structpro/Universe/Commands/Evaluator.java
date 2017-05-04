package com.ternsip.structpro.Universe.Commands;

import com.ternsip.structpro.Logic.*;
import com.ternsip.structpro.Structure.*;
import com.ternsip.structpro.Universe.Universe;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Commands evaluator
 * @author  Ternsip
 * @since JDK 1.6
 */
class Evaluator {

    /** Undo projections */
    private static final ArrayList<Projection> undo = new ArrayList<Projection>();

    /**
     * Paste schematic that has most similar name
     * @param world Target world
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
     * @param village Paste entire village
     * @return Execution status
     */
    static String cmdPaste(World world,
                           String name,
                           int posX, int posY, int posZ,
                           int rotateX, int rotateY, int rotateZ,
                           boolean flipX, boolean flipY, boolean flipZ,
                           boolean village) {
        final Pattern nPattern = Pattern.compile(".*" + Pattern.quote(name) + ".*", Pattern.CASE_INSENSITIVE);
        undo.clear();
        if (village) {
            ArrayList<Structure> town = Utils.select(Structures.villages.select(nPattern));
            if (town == null) {
                return "No matching villages";
            }
            ArrayList<Projection> projections = Village.combine(world, town, posX >> 4, posZ >> 4, System.currentTimeMillis());
            for (Projection projection : projections) {
                saveUndo(projection);
            }
            for (Projection projection : projections) {
                Report report = projection.project();
                report.print();
            }
            return "Total spawned: " + projections.size();
        } else {
            ArrayList<Structure> candidates = new ArrayList<Structure>(){{
                addAll(Structures.structures.select(nPattern));
                addAll(Structures.saves.select(nPattern));
            }};
            Structure structure = Utils.select(candidates);
            if (structure == null) {
                return "No matching structures";
            }
            Posture posture = structure.getPosture(posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ);
            Projection projection = new Projection(world, structure, posture, System.currentTimeMillis());
            if (posY == 0) {
                try {
                    projection = Construction.calibrate(world, posX, posZ, System.currentTimeMillis(), structure);
                } catch (IOException ioe) {
                    Report report = structure.report().pref(new Report().post("NOT SPAWNED", ioe.getMessage()));
                    report.print();
                    return report.toString();
                }
            }
            saveUndo(projection);
            Report report = projection.project();
            report.print();
            Universe.sound(world, new BlockPos(posX, posY, posZ), SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0f);
            return report.toString();
        }
    }

    /**
     * Save schematic
     * @param world Target world
     * @param name Structure name
     * @param posX X starting position
     * @param posY Y starting position
     * @param posZ Z starting position
     * @param width X axis size
     * @param height Y axis size
     * @param length Z axis size
     * @return Execution status
     */
    static String cmdSave(World world, String name, int posX, int posY, int posZ, int width, int height, int length) {
        Report report = new Report()
                .post("WORLD FRAGMENT", name)
                .post("POS", "[X=" + posX + ";Y=" + posY + ";Z=" + posZ + "]")
                .post("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]");
        try {
            Blueprint blueprint = new Blueprint(world, new BlockPos(posX, posY, posZ), new Volume(width, height, length));
            File file = new File(Configurator.getSchematicsSavesFolder(), name + ".schematic");
            blueprint.saveSchematic(file);
            Structures.load(file);
            report.post("SAVED", file.getPath());
        } catch (IOException ioe) {
            report.post("NOT SAVED", ioe.getMessage());
        }
        report.print();
        Universe.sound(world, new BlockPos(posX, posY, posZ), SoundEvents.BLOCK_ENDERCHEST_CLOSE, SoundCategory.BLOCKS, 0.5f);
        return report.toString();
    }

    /**
     * Print command help information
     * @return Command execution status
     */
    static String cmdHelp() {
        return "You can pass arguments by name" +
                "\n" +
                "PASTE SCHEMATIC: /spro paste " +
                "name=<string> posX=<int> posY=<int> posZ=<int> rotateX=<int> " +
                "rotateY=<int> rotateZ=<int> flipX=<bool> flipY=<bool> flipZ=<bool> village=<bool>" +
                "\n" +
                "SAVE SCHEMATIC: /spro save " +
                "name=<string> posX=<int> posY=<int> posZ=<int> width=<int> height=<int> length=<int>" +
                "\n" +
                "UNDO LAST ACTION: /spro undo" +
                "\n" +
                "GENERATE WORLD: /spro gen size=<int> step=<int> sx=<int> sz=<int> stop=<bool> skip=<bool> progress=<int>";
    }

    /**
     * Undo all session history
     * @return Command execution status
     */
    static String cmdUndo() {
        if (undo.isEmpty()) {
            return "No undo data";
        }
        for (Projection projection : undo) {
            projection.project().print();
            Posture pst = projection.getPosture();
            Universe.sound(projection.getWorld(), new BlockPos(pst.getPosX(), pst.getPosY(), pst.getPosZ()), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0f);
        }
        undo.clear();
        return "Undo done";
    }

    /**
     * Start pre-generation routine
     * @param world Target world
     * @param startX Starting chunk X coordinate
     * @param startZ Starting chunk Z coordinate
     * @param step Number of chunks to process per step
     * @param size Number of chunks for x and z axis in each direction
     * @param stop Deactivate generation
     * @param skip Skip chunks with no structures
     * @param progress Start generation progress from given number
     * @return Command execution status
     */
    static String cmdGen(World world, int startX, int startZ,int step, int size, boolean stop, boolean skip, int progress) {
        if (stop) {
            Pregen.deactivate();
            return "Generation process interrupted";
        } else {
            Pregen.activate(world, startX, startZ, step, size, skip, progress);
            return "Generation process started";
        }
    }

    /**
     * Save undo data to history for projection
     * @param projection Projection to save
     */
    private static void saveUndo(Projection projection) {
        if (projection.getBlueprint() instanceof Structure) {
            Posture mp = projection.getPosture().extend(Structure.MELT, Structure.MELT, Structure.MELT);
            BlockPos start = new BlockPos(mp.getPosX(), mp.getPosY(), mp.getPosZ());
            Volume volume = new Volume(mp.getSizeX(), mp.getSizeY(), mp.getSizeZ());
            Blueprint blueprint = new Blueprint(projection.getWorld(), start, volume);
            Posture posture = blueprint.getPosture(start.getX(), start.getY(), start.getZ(), 0, 0, 0, false, false, false);
            undo.add(new Projection(projection.getWorld(), blueprint, posture, 0));
        }
    }

}

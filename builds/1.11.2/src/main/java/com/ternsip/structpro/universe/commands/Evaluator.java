package com.ternsip.structpro.universe.commands;

import com.ternsip.structpro.logic.Configurator;
import com.ternsip.structpro.logic.Structures;
import com.ternsip.structpro.logic.generation.Construction;
import com.ternsip.structpro.logic.generation.Pregen;
import com.ternsip.structpro.logic.generation.Village;
import com.ternsip.structpro.structure.*;
import com.ternsip.structpro.universe.blocks.UBlockPos;
import com.ternsip.structpro.universe.utils.Report;
import com.ternsip.structpro.universe.utils.Utils;
import com.ternsip.structpro.universe.utils.Variables;
import com.ternsip.structpro.universe.world.UWorld;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Commands evaluator
 * @author  Ternsip
 */
public class Evaluator {

    /** Undo projections */
    private static final ArrayList<Projection> undo = new ArrayList<>();

    /** Wand cuboid block selections for each player */
    private static final HashMap<EntityPlayer, AbstractMap.SimpleEntry<UBlockPos, UBlockPos>> wand = new HashMap<>();

    /** Showing warning message about unknown command
     * @param vars Variables to parse arguments
     * @param sender Who sends the command
     */
    static void execUnknown(Variables vars, ICommandSender sender) {
        String cmd = vars.get(new String[]{"command"}, "");
        feedback(sender, "§4Unknown command§2 " + cmd);
    }

    /** Displaying meta information for target command
     * @param vars Variables to parse arguments
     * @param sender Who sends the command
     */
    static void execHelp(Variables vars, ICommandSender sender) {
        String cmd = vars.get(new String[]{"command"}, "");
        feedback(sender, Evaluator.cmdHelp(cmd));
    }

    /** Paste something to the world
     * @param vars Variables to parse arguments
     * @param sender Who sends the command
     */
    static void execPaste(Variables vars, ICommandSender sender) {
        Random random = new Random();
        String name = vars.get(new String[]{"name"}, "");
        int posX = vars.get(new String[]{"posx", "px", "x"}, sender.getPosition().getX());
        int posY = vars.get(new String[]{"posy", "py", "y"}, sender.getPosition().getY());
        int posZ = vars.get(new String[]{"posz", "pz", "z"}, sender.getPosition().getZ());
        int rotateX = vars.get(new String[]{"rotatex", "rotx", "rx"}, 0);
        int rotateY = vars.get(new String[]{"rotatey", "roty", "ry"}, random.nextInt() % 4);
        int rotateZ = vars.get(new String[]{"rotatez", "rotz", "rz"}, 0);
        boolean flipX = vars.get(new String[]{"flipx", "fx"},  random.nextBoolean());
        boolean flipY = vars.get(new String[]{"flipy", "fy"}, false);
        boolean flipZ = vars.get(new String[]{"flipz", "fz"}, random.nextBoolean());
        boolean isVillage = vars.get(new String[]{"village", "town", "city"}, false);
        boolean isInsecure = vars.get(new String[]{"insecure"}, false);
        posY = vars.get(new String[]{"auto"}, false) ? 0 : posY;
        if (vars.get(new String[]{"wand"}, false) && sender instanceof EntityPlayer && wand.containsKey(sender)) {
            UBlockPos pos = wand.get(sender).getValue();
            posX = pos.getX(); posY = pos.getY(); posZ = pos.getZ();
        }
        String worldName = vars.get(new String[]{"world"});
        UWorld world = worldName == null ? new UWorld(sender.getEntityWorld()) : UWorld.getWorld(worldName);
        if (world == null) {
            feedback(sender, "No matching world");
            return;
        }
        feedback(sender, Evaluator.cmdPaste(world, name, posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, isVillage, isInsecure));
    }

    /** Saves fragment from the world to schematic
     * @param vars Variables to parse arguments
     * @param sender Who sends the command
     */
    static void execSave(Variables vars, ICommandSender sender) {
        String name = vars.get(new String[]{"name"}, "unnamed");
        int posX = vars.get(new String[]{"posx", "px", "x"}, sender.getPosition().getX());
        int posY = vars.get(new String[]{"posy", "py", "y"}, sender.getPosition().getY());
        int posZ = vars.get(new String[]{"posz", "pz", "z"}, sender.getPosition().getZ());
        int width = vars.get(new String[]{"width", "w"}, 64);
        int height = vars.get(new String[]{"height", "h"}, 64);
        int length = vars.get(new String[]{"length", "l"}, 64);
        if (vars.get(new String[]{"wand"}, false) && sender instanceof EntityPlayer && wand.containsKey(sender)) {
            UBlockPos posK = wand.get(sender).getKey();
            UBlockPos posV = wand.get(sender).getValue();
            posX = Math.min(posK.getX(), posV.getX());
            posY = Math.min(posK.getY(), posV.getY());
            posZ = Math.min(posK.getZ(), posV.getZ());
            width = Math.max(posK.getX(), posV.getX()) - posX + 1;
            height = Math.max(posK.getY(), posV.getY()) - posY + 1;
            length = Math.max(posK.getZ(), posV.getZ()) - posZ + 1;
        }
        String worldName = vars.get(new String[]{"world"});
        UWorld world = worldName == null ? new UWorld(sender.getEntityWorld()) : UWorld.getWorld(worldName);
        if (world == null) {
            feedback(sender, "§4No matching world");
            return;
        }
        feedback(sender, Evaluator.cmdSave(world, name, posX, posY, posZ, width, height, length));
    }

    /** Undo previous paste command
     * @param vars Variables to parse arguments
     * @param sender Who sends the command
     */
    @SuppressWarnings({"unused"})
    static void execUndo(Variables vars, ICommandSender sender) {
        feedback(sender, Evaluator.cmdUndo());
    }

    /** Generate world region. Very high-weight for cpu-calculation resources command
     * @param vars Variables to parse arguments
     * @param sender Who sends the command
     */
    static void execGen(Variables vars, ICommandSender sender) {
        int size = vars.get(new String[]{"size", "s", "length", "radius", "r"}, 16);
        int startX = vars.get(new String[]{"startx", "sx"}, 0);
        int startZ = vars.get(new String[]{"startz", "sz"}, 0);
        boolean stop = vars.get(new String[]{"stop", "end", "finish"}, false);
        boolean skip = vars.get(new String[]{"skip"}, false);
        int progress = vars.get(new String[]{"progress"}, 0);
        int step = Math.min(Math.max(1, vars.get(new String[]{"step", "delta"}, 32)), 4096);
        String worldName = vars.get(new String[]{"world"});
        UWorld world = worldName == null ? new UWorld(sender.getEntityWorld()) : UWorld.getWorld(worldName);
        if (world == null) {
            feedback(sender, "§4No matching world");
            return;
        }
        feedback(sender, Evaluator.cmdGen(world, startX, startZ, step, size, stop, skip, progress));
    }

    /**
     * Called when player touches block with wand
     * @param player Player entity instance that touches block
     * @param pos Touched block position
     */
    public static void touchBlock(EntityPlayer player, UBlockPos pos) {
        feedback(player, "§ablock §d" + pos.getX() + " " + pos.getY() + " " + pos.getZ() + "§a selected");
        wand.put(player, new AbstractMap.SimpleEntry<>(wand.containsKey(player) ? wand.get(player).getValue() : pos, pos));
    }

    /**
     * Send chat feedback to sender
     * @param sender The sender that will receive feedback
     * @param message Text constituent of feedback
     */
    private static void feedback(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }

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
     * @param isVillage Paste entire village
     * @param isInsecure Projection will be insecure
     * @return Execution status
     */
    private static String cmdPaste(UWorld world,
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
            ArrayList<Projection> projections = Village.combine(world, town, posX >> 4, posZ >> 4, System.currentTimeMillis());
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
            Report report = projection.project(isInsecure);
            report.print();
            world.sound(new UBlockPos(posX, posY, posZ), SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0f);
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
    private static String cmdSave(UWorld world,
                                  String name,
                                  int posX, int posY, int posZ,
                                  int width, int height, int length) {
        Report report = new Report()
                .post("WORLD FRAGMENT", name)
                .post("POS", "[X=" + posX + ";Y=" + posY + ";Z=" + posZ + "]")
                .post("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]");
        try {
            Blueprint blueprint = new Blueprint(world, new UBlockPos(posX, posY, posZ), new Volume(width, height, length));
            File file = new File(Configurator.getSchematicsSavesFolder(), name + ".schematic");
            blueprint.saveSchematic(file);
            Structures.load(file);
            report.post("SAVED", file.getPath());
        } catch (IOException ioe) {
            report.post("NOT SAVED", ioe.getMessage());
        }
        report.print();
        world.sound(new UBlockPos(posX, posY, posZ), SoundEvents.BLOCK_ENDERCHEST_CLOSE, SoundCategory.BLOCKS, 0.5f);
        return report.toString();
    }

    /**
     * Print command help information
     * @return Command execution status
     */
    private static String cmdHelp(String chapter) {
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
    private static String cmdUndo() {
        if (undo.isEmpty()) {
            return "§4No undo data";
        }
        for (Projection projection : undo) {
            projection.project(true).print();
            Posture pst = projection.getPosture();
            projection.getWorld().sound(new UBlockPos(pst.getPosX(), pst.getPosY(), pst.getPosZ()), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0f);
        }
        undo.clear();
        return "§2Undo done";
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
    private static String cmdGen(UWorld world,
                                 int startX, int startZ,
                                 int step,
                                 int size,
                                 boolean stop,
                                 boolean skip,
                                 int progress) {
        if (stop) {
            Pregen.deactivate();
            return "§4Generation process interrupted";
        } else {
            Pregen.activate(world, startX, startZ, step, size, skip, progress);
            return "§2Generation process started";
        }
    }

    /**
     * Save undo data to history for projection
     * @param projection Projection to save
     */
    private static void saveUndo(Projection projection) {
        if (projection.getSchema() instanceof Structure) {
            Posture mp = projection.getPosture().extend(Structure.MELT, Structure.MELT, Structure.MELT);
            UBlockPos start = new UBlockPos(mp.getPosX(), mp.getPosY(), mp.getPosZ());
            Volume volume = new Volume(mp.getSizeX(), mp.getSizeY(), mp.getSizeZ());
            Blueprint blueprint = new Blueprint(projection.getWorld(), start, volume);
            Posture posture = blueprint.getPosture(start.getX(), start.getY(), start.getZ(), 0, 0, 0, false, false, false);
            undo.add(new Projection(projection.getWorld(), blueprint, posture, 0));
        }
    }

}

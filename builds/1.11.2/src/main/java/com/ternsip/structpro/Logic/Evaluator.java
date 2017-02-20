package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Projector;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

/* Evaluates commands */
class Evaluator {

    /* Paste schematic that has most similar name */
    static String cmdPaste(World world,
                          String name, int posX, int posY, int posZ,
                          int rotateX, int rotateY, int rotateZ,
                          boolean flipX, boolean flipY, boolean flipZ,
                          boolean village) throws CommandException {
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
            Projector projector = Utils.select(Structures.select(name), seed);
            if (projector == null) {
                return "No matching structures";
            }
            Report report = projector.paste(world, posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, 0);
            report.print();
            return report.toString();
        }
    }

    /* Print command help information */
    static String cmdHelp() throws CommandException {
        return "You can pass arguments by name\n" +
                "PASTE SCHEMATIC: /structpro paste " +
                "name=<string> posX=<int> posY=<int> posZ=<int> rotateX=<int> " +
                "rotateY=<int> rotateZ=<int> flipX=<bool> flipY=<bool> flipZ=<bool> village=<bool>";
    }

}

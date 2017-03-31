package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Blueprint;
import com.ternsip.structpro.Structure.Projector;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/* Commands evaluator */
class Evaluator {

    /* Paste schematic that has most similar name */
    static String cmdPaste(World world,
                           final String name,
                           int posX, int posY, int posZ,
                           int rotateX, int rotateY, int rotateZ,
                           boolean flipX, boolean flipY, boolean flipZ,
                           boolean village) {
        final Pattern nPattern = Pattern.compile(".*" + Pattern.quote(name) + ".*", Pattern.CASE_INSENSITIVE);
        if (village) {
            ArrayList<Projector> projectors = Utils.select(Structures.villages.select(nPattern));
            if (projectors == null || projectors.size() == 0) {
                return "No matching villages";
            }
            Report report = Distributor.spawnVillage(world, projectors, posX / 16, posZ / 16, System.currentTimeMillis());
            report.print();
            return report.toString();
        } else {
            ArrayList<Projector> candidates = new ArrayList<Projector>(){{
                addAll(Structures.structures.select(nPattern));
                addAll(Structures.saves.select(nPattern));
            }};
            Projector projector = Utils.select(candidates);
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
        Report report = new Report()
                .add("WORLD FRAGMENT", name)
                .add("POS", "[X=" + posX + ";Y=" + posY + ";Z=" + posZ + "]")
                .add("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]");
        try {
            File file = new File(Configurator.getSchematicsSavesFolder(), name + ".schematic");
            Blueprint blueprint = new Blueprint(world, posX, posY, posZ, width, height, length);
            blueprint.saveSchematic(file);
            Structures.loadStructure(file);
            report.add("SAVED", file.getPath());
        } catch (IOException ioe) {
            report.add("NOT SAVED", ioe.getMessage());
        }
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
                "name=<string> posX=<int> posY=<int> posZ=<int> width=<int> height=<int> length=<int>";
    }

}

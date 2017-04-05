package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Blueprint;
import com.ternsip.structpro.Structure.Posture;
import com.ternsip.structpro.Structure.Projection;
import com.ternsip.structpro.Structure.Structure;
import com.ternsip.structpro.Universe.Cache.Universe;
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
                           String name,
                           int posX, int posY, int posZ,
                           int rotateX, int rotateY, int rotateZ,
                           boolean flipX, boolean flipY, boolean flipZ,
                           boolean village) {
        final Pattern nPattern = Pattern.compile(".*" + Pattern.quote(name) + ".*", Pattern.CASE_INSENSITIVE);
        if (village) {
            ArrayList<Structure> vlg = Utils.select(Structures.villages.select(nPattern));
            if (vlg == null) {
                return "No matching villages";
            }
            int chunkX = Universe.getStartChunkX(posX), chunkZ = Universe.getStartChunkZ(posZ);
            ArrayList<Projection> projections = Village.combine(world, vlg, chunkX, chunkZ, System.currentTimeMillis());
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
            Posture posture = new Posture(posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, structure.getWidth(), structure.getHeight(), structure.getLength());
            Projection projection = new Projection(world, structure, posture, false, System.currentTimeMillis());
            if (posY == 0) {
                try {
                    projection = Construction.spawn(world, structure, posX, posZ, System.currentTimeMillis());
                } catch (IOException ioe) {
                    Report report = structure.report().pref(new Report().post("NOT SPAWNED", ioe.getMessage()));
                    report.print();
                    return report.toString();
                }
            }
            Report report = projection.project();
            report.print();
            return report.toString();
        }
    }

    /* Save schematic */
    static String cmdSave(World world, String name, int posX, int posY, int posZ, int width, int height, int length) {
        Report report = new Report()
                .post("WORLD FRAGMENT", name)
                .post("POS", "[X=" + posX + ";Y=" + posY + ";Z=" + posZ + "]")
                .post("SIZE", "[W=" + width + ";H=" + height + ";L=" + length + "]");
        try {
            File file = new File(Configurator.getSchematicsSavesFolder(), name + ".schematic");
            Blueprint blueprint = new Blueprint(world, posX, posY, posZ, width, height, length);
            blueprint.saveSchematic(file);
            Structures.loadStructure(file);
            report.post("SAVED", file.getPath());
        } catch (IOException ioe) {
            report.post("NOT SAVED", ioe.getMessage());
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

package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Universe.Universe;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Timer;
import net.minecraft.world.World;

/**
 * World pre-generation helper
 * @author  Ternsip
 * @since JDK 1.6
 */
public class Pregen {

    private static final int STEP = 8;
    private static Timer timer = new Timer(250);
    private static World world;
    private static int progress, size;
    private static boolean active = false;

    public static void tick() {
        if (!active) {
            return;
        }
        if (!timer.isOver()) {
            return;
        }
        timer.drop();
        for (int cnt = 0; cnt < STEP && progress < size * size; ++progress, ++cnt) {
            Universe.generate(world, progress / size, progress % size);
        }
        new Report().post("WORLD GEN", progress + "/" + size * size).print();
        if (progress >= size * size) {
            new Report().post("WORLD GEN", "FINISH").print();
            active = false;
        }
    }

    public static void activate(World world, int size) {
        if (active) {
            new Report().post("WORLD GEN", "INTERRUPT").print();
        }
        Pregen.world = world;
        Pregen.size = size;
        Pregen.progress = 0;
        Pregen.active = true;
        new Report().post("WORLD GEN", "START").print();
    }

}

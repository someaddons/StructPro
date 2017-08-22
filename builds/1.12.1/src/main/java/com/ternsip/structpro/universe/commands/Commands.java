package com.ternsip.structpro.universe.commands;

import com.ternsip.structpro.universe.blocks.UBlockPos;
import com.ternsip.structpro.universe.world.UWorld;
import com.ternsip.structpro.universe.utils.Utils;
import com.ternsip.structpro.universe.utils.Variables;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.*;

/**
 * The Commands class implements default command.
 * It takes effect when someone sends game command.
 * Handlers works both for client and server.
 * @author  Ternsip
 */
@SuppressWarnings({"NullableProblems"})
public class Commands implements ICommand {

    /** General command name */
    private static final String name = "structpro";

    /** Main hint for usage */
    private static final String usage = "/structpro <help|paste|save|undo|gen>";

    /** Command can be invoked using any of this aliases */
    private static final ArrayList<String> aliases = new ArrayList<String>(){{add("structpro");add("spro");}};

    /** Wand cuboid block selections for each player */
    private static final HashMap<EntityPlayer, AbstractMap.SimpleEntry<UBlockPos, UBlockPos>> wand = new HashMap<>();

    /**
     * Check if the given ICommandSender has permission to execute this command
     * @param server The server instance
     * @param sender The ICommandSender to check permissions on
     * @return access is permitted
     */
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        boolean isServer = (sender instanceof MinecraftServer);
        boolean isAdmin = (sender instanceof EntityPlayer) && ((EntityPlayer) sender).isCreative();
        return isServer || isAdmin;
    }

    /**
     * Returns whether the specified command parameter index is a username parameter
     * @param args The arguments of the command invocation
     * @param index The index
     * @return index is a username parameter
     */
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    /**
     * Returns tab completions
     * @param server The server instance
     * @param sender The sender for getting tab completions
     * @param args command arguments
     * @param pos target position
     * @return tab completions
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    /**
     * Gets the name of the command
     * @return Command name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the usage string for the command.
     * @param sender The ICommandSender who is requesting usage details
     * @return command usage
     */
    @Override
    public String getUsage(ICommandSender sender) {
        return usage;
    }

    /**
     * Command can be invoked using any of this aliases
     * @return array of aliases
     */
    @Override
    public List<String> getAliases() {
        return aliases;
    }

    /** Overrides default comparator */
    @Override
    public int compareTo(ICommand command) {
        return 0;
    }

    /**
     * Callback for when the command is executed
     * Primary argument expected as command method
     * Secondary arguments can go in mixed order
     * @param server The server instance
     * @param sender The sender who executed the command
     * @param args The arguments that were passed
     * @throws CommandException If execution failed
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length <= 0) {
            feedback(sender, Evaluator.cmdHelp(""));
            return;
        }
        String cmd = args[0];
        Variables vars = new Variables(Utils.join(args, " "));
        Random random = new Random();
        if (vars.get(new String[]{"help"}, false)) {
            feedback(sender, Evaluator.cmdHelp(cmd));
            return;
        }
        if (cmd.equalsIgnoreCase("paste")) {
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
            UWorld uWorld = worldName == null ? new UWorld(sender.getEntityWorld()) : UWorld.getWorld(worldName);
            if (uWorld == null) {
                feedback(sender, "No matching world");
                return;
            }
            feedback(sender, Evaluator.cmdPaste(uWorld, name, posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, isVillage, isInsecure));
            return;
        }
        if (cmd.equalsIgnoreCase("save")) {
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
            UWorld uWorld = worldName == null ? new UWorld(sender.getEntityWorld()) : UWorld.getWorld(worldName);
            if (uWorld == null) {
                feedback(sender, "§4No matching world");
                return;
            }
            feedback(sender, Evaluator.cmdSave(uWorld, name, posX, posY, posZ, width, height, length));
            return;
        }
        if (cmd.equalsIgnoreCase("undo")) {
            feedback(sender, Evaluator.cmdUndo());
            return;
        }
        if (cmd.equalsIgnoreCase("gen")) {
            int size = vars.get(new String[]{"size", "s", "length", "radius", "r"}, 16);
            int startX = vars.get(new String[]{"startx", "sx"}, 0);
            int startZ = vars.get(new String[]{"startz", "sz"}, 0);
            boolean stop = vars.get(new String[]{"stop", "end", "finish"}, false);
            boolean skip = vars.get(new String[]{"skip"}, false);
            int progress = vars.get(new String[]{"progress"}, 0);
            int step = Math.min(Math.max(1, vars.get(new String[]{"step", "delta"}, 32)), 4096);
            String worldName = vars.get(new String[]{"world"});
            UWorld uWorld = worldName == null ? new UWorld(sender.getEntityWorld()) : UWorld.getWorld(worldName);
            if (uWorld == null) {
                feedback(sender, "§4No matching world");
                return;
            }
            feedback(sender, Evaluator.cmdGen(uWorld, startX, startZ, step, size, stop, skip, progress));
            return;
        }
        feedback(sender, "§4Unknown command§2 " + cmd + "§4 for §b" + name);
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
     * Called when player touches block with wand
     * @param player Player entity instance that touches block
     * @param pos Touched block position
     */
    public static void touch(EntityPlayer player, UBlockPos pos) {
        feedback(player, "§ablock §d" + pos.getX() + " " + pos.getY() + " " + pos.getZ() + "§a selected");
        wand.put(player, new AbstractMap.SimpleEntry<>(wand.containsKey(player) ? wand.get(player).getValue() : pos, pos));
    }

}

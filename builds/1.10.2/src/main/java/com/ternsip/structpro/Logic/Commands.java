package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Structure.Projector;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Chat commands class
 */
public class Commands implements ICommand {

    private static final String commandName = "structpro";
    private static final String commandUsage = "/structpro <help|paste>";
    private static final ArrayList<String> commandAliases = new ArrayList<String>(){{add("/structpro");add("/spro");}};

    private static final String cmdPasteHelp = "PASTE SCHEMATIC: /structpro paste " +
            "name=<string> posX=<int> posY=<int> posZ=<int> rotateX=<int> " +
            "rotateY=<int> rotateZ=<int> flipX=<bool> flipY=<bool> flipZ=<bool> village=<bool>";

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return (sender instanceof EntityPlayer) && ((EntityPlayer) sender).isCreative();
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return commandUsage;
    }

    @Override
    public List<String> getCommandAliases() {
        return commandAliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length <= 0) {
            cmdHelp(server, sender);
            return;
        }
        String cmd = args[0].toLowerCase();
        HashMap<String, String> vars = Utils.extractVariables(Utils.join(args, " "));
        if (cmd.equals("paste")) {
            Random random = new Random();
            String name = null;
            int posX = sender.getPosition().getX();
            int posY = sender.getPosition().getY();
            int posZ = sender.getPosition().getZ();
            int rotateX = 0;
            int rotateY = random.nextInt() % 4;
            int rotateZ = 0;
            boolean flipX = random.nextBoolean();
            boolean flipY = false;
            boolean flipZ = random.nextBoolean();
            boolean village = false;
            if (vars.containsKey("name")) name = vars.get("name");
            if (vars.containsKey("posx")) posX = Integer.parseInt(vars.get("posx"));
            if (vars.containsKey("posy")) posY = Integer.parseInt(vars.get("posy"));
            if (vars.containsKey("posz")) posY = Integer.parseInt(vars.get("posz"));
            if (vars.containsKey("x")) posX = Integer.parseInt(vars.get("x"));
            if (vars.containsKey("y")) posY = Integer.parseInt(vars.get("y"));
            if (vars.containsKey("z")) posY = Integer.parseInt(vars.get("z"));
            if (vars.containsKey("rotatex")) rotateX = Integer.parseInt(vars.get("rotatex"));
            if (vars.containsKey("rotatey")) rotateY = Integer.parseInt(vars.get("rotatey"));
            if (vars.containsKey("rotatez")) rotateY = Integer.parseInt(vars.get("rotatez"));
            if (vars.containsKey("rotx")) rotateX = Integer.parseInt(vars.get("rotx"));
            if (vars.containsKey("roty")) rotateY = Integer.parseInt(vars.get("roty"));
            if (vars.containsKey("rotz")) rotateY = Integer.parseInt(vars.get("rotz"));
            if (vars.containsKey("flipx")) flipX = Boolean.parseBoolean(vars.get("flipx"));
            if (vars.containsKey("flipy")) flipY = Boolean.parseBoolean(vars.get("flipy"));
            if (vars.containsKey("flipz")) flipZ = Boolean.parseBoolean(vars.get("flipz"));
            if (vars.containsKey("village")) village = Boolean.parseBoolean(vars.get("village"));
            cmdPaste(server, sender, name, posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, village);
        }
        if (cmd.equals("help")) {
            cmdHelp(server, sender);
        }
    }

    /* Paste schematic that has most similar name */
    private void cmdPaste(MinecraftServer server, ICommandSender sender,
                          String name, int posX, int posY, int posZ,
                          int rotateX, int rotateY, int rotateZ,
                          boolean flipX, boolean flipY, boolean flipZ,
                          boolean village) throws CommandException {
        Storage storage = Loader.storage;
        long seed = System.currentTimeMillis();
        if (village) {
            ArrayList<Projector> projectors = name == null ? Utils.select(storage.getVillages(), seed) : storage.selectVillage(name);
            if (projectors == null || projectors.size() == 0) {
                feedback(sender, "No matching villages");
                return;
            }
            Report report = Distributor.spawnVillage(sender.getEntityWorld(), projectors, posX / 16, posZ / 16, new Random(seed));
            report.print();
            feedback(sender, report.toString());
        } else {
            Projector projector = name == null ? Utils.select(storage.select(), seed) : Utils.select(storage.select(name), seed);
            if (projector == null) {
                feedback(sender, "No matching structures");
                return;
            }
            Report report = projector.paste(sender.getEntityWorld(), posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, 0);
            report.print();
            feedback(sender, report.toString());
        }
    }

    /* Print command help information */
    private void cmdHelp(MinecraftServer server, ICommandSender sender) throws CommandException {
        feedback(sender, cmdPasteHelp);
        feedback(sender, "You can pass arguments by name");
    }

    /* Send chat feedback to sender */
    private void feedback(ICommandSender sender, String message) {
        sender.addChatMessage(new TextComponentString(message));
    }

}

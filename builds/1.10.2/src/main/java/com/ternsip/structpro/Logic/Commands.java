package com.ternsip.structpro.Logic;

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

/* Chat commands class */
@SuppressWarnings({"NullableProblems"})
public class Commands implements ICommand {

    private static final String name = "structpro";
    private static final String usage = "/structpro <help|paste>";
    private static final ArrayList<String> aliases = new ArrayList<String>(){{add("/structpro");}};

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
        return name;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return usage;
    }

    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length <= 0) {
            Evaluator.cmdHelp();
            return;
        }
        String cmd = args[0].toLowerCase();
        HashMap<String, String> vars = Utils.extractVariables(Utils.join(args, " "));
        if (cmd.equals("paste")) {
            Random random = new Random();
            String name = "";
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
            feedback(sender, Evaluator.cmdPaste(sender.getEntityWorld(), name, posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, village));
        }
        if (cmd.equals("help")) {
            feedback(sender, Evaluator.cmdHelp());
        }
    }

    /* Send chat feedback to sender */
    private static void feedback(ICommandSender sender, String message) {
        sender.addChatMessage(new TextComponentString(message));
    }

}

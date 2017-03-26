package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Utils.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/* Chat commands class */
@SuppressWarnings({"NullableProblems"})
public class Commands implements ICommand {

    private static final String name = "structpro";
    private static final String usage = "/structpro <help|paste|save>";
    private static final ArrayList<String> aliases = new ArrayList<String>(){{add("/structpro");}};

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return (sender instanceof EntityPlayer) && ((EntityPlayer) sender).isCreative();
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return usage;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length <= 0) {
            Evaluator.cmdHelp();
            return;
        }
        String cmd = args[0].toLowerCase();
        HashMap<String, String> vars = Utils.extractVariables(Utils.join(args, " "));
        Random random = new Random();
        if (cmd.equals("paste")) {
            String name = Utils.parseOrDefault(vars, "name", "");
            int posX = Utils.parseOrDefault(vars, "posx", sender.getPosition().getX());
            int posY = Utils.parseOrDefault(vars, "posy", sender.getPosition().getY());
            int posZ = Utils.parseOrDefault(vars, "posz", sender.getPosition().getZ());
            posX = Utils.parseOrDefault(vars, "x", posX);
            posY = Utils.parseOrDefault(vars, "y", posY);
            posZ = Utils.parseOrDefault(vars, "z", posZ);
            int rotateX = Utils.parseOrDefault(vars, "rotatex", 0);
            int rotateY = Utils.parseOrDefault(vars, "rotatey", random.nextInt() % 4);
            int rotateZ = Utils.parseOrDefault(vars, "rotatez", 0);
            rotateX = Utils.parseOrDefault(vars, "rotx", rotateX);
            rotateY = Utils.parseOrDefault(vars, "roty", rotateY);
            rotateZ = Utils.parseOrDefault(vars, "rotz", rotateZ);
            boolean flipX = Utils.parseOrDefault(vars, "flipx",  random.nextBoolean());
            boolean flipY = Utils.parseOrDefault(vars, "flipy", false);
            boolean flipZ = Utils.parseOrDefault(vars, "flipz", random.nextBoolean());
            boolean village = Utils.parseOrDefault(vars, "village",false);
            feedback(sender, Evaluator.cmdPaste(sender.getEntityWorld(), name, posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, village));
        }
        if (cmd.equals("save")) {
            String name = Utils.parseOrDefault(vars, "name", "unnamed");
            int posX = Utils.parseOrDefault(vars, "posx", sender.getPosition().getX());
            int posY = Utils.parseOrDefault(vars, "posy", sender.getPosition().getY());
            int posZ = Utils.parseOrDefault(vars, "posz", sender.getPosition().getZ());
            posX = Utils.parseOrDefault(vars, "x", posX);
            posY = Utils.parseOrDefault(vars, "y", posY);
            posZ = Utils.parseOrDefault(vars, "z", posZ);
            int width = Utils.parseOrDefault(vars, "width", 64);
            int height = Utils.parseOrDefault(vars, "height", 64);
            int length = Utils.parseOrDefault(vars, "length", 64);
            feedback(sender, Evaluator.cmdSave(sender.getEntityWorld(), name, posX, posY, posZ, width, height, length));
        }
        if (cmd.equals("help")) {
            feedback(sender, Evaluator.cmdHelp());
        }
    }

    /* Send chat feedback to sender */
    private static void feedback(ICommandSender sender, String message) {
        sender.sendMessage(new TextComponentString(message));
    }

}

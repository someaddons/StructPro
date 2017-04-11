package com.ternsip.structpro.Logic;

import com.ternsip.structpro.Universe.Cache.Universe;
import com.ternsip.structpro.Utils.Utils;
import com.ternsip.structpro.Utils.Variables;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* Chat commands class */
@SuppressWarnings({"NullableProblems"})
public class Commands implements ICommand {

    private static final String name = "structpro";
    private static final String usage = "/structpro <help|paste|save|undo>";
    private static final ArrayList<String> aliases = new ArrayList<String>(){{add("structpro");add("spro");}};

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
        String cmd = args[0];
        Variables vars = new Variables(Utils.join(args, " "));
        Random random = new Random();
        Universe.unload();
        if (cmd.equalsIgnoreCase("paste")) {
            String name = vars.get("name", "");
            int posX = vars.get("posx", sender.getPosition().getX());
            int posY = vars.get("posy", sender.getPosition().getY());
            int posZ = vars.get("posz", sender.getPosition().getZ());
            posX = vars.get("x", posX);
            posY = vars.get("y", posY);
            posZ = vars.get("z", posZ);
            int rotateX = vars.get("rotatex", 0);
            int rotateY = vars.get("rotatey", random.nextInt() % 4);
            int rotateZ = vars.get("rotatez", 0);
            rotateX = vars.get("rotx", rotateX);
            rotateY = vars.get("roty", rotateY);
            rotateZ = vars.get("rotz", rotateZ);
            boolean flipX = vars.get("flipx",  random.nextBoolean());
            boolean flipY = vars.get("flipy", false);
            boolean flipZ = vars.get("flipz", random.nextBoolean());
            boolean village = vars.get("village",false);
            feedback(sender, Evaluator.cmdPaste(sender.getEntityWorld(), name, posX, posY, posZ, rotateX, rotateY, rotateZ, flipX, flipY, flipZ, village));
        }
        if (cmd.equalsIgnoreCase("save")) {
            String name = vars.get("name", "unnamed");
            int posX = vars.get("posx", sender.getPosition().getX());
            int posY = vars.get("posy", sender.getPosition().getY());
            int posZ = vars.get("posz", sender.getPosition().getZ());
            posX = vars.get("x", posX);
            posY = vars.get("y", posY);
            posZ = vars.get("z", posZ);
            int width = vars.get("width", 64);
            int height = vars.get("height", 64);
            int length = vars.get("length", 64);
            feedback(sender, Evaluator.cmdSave(sender.getEntityWorld(), name, posX, posY, posZ, width, height, length));
        }
        if (cmd.equalsIgnoreCase("undo")) {
            feedback(sender, Evaluator.cmdUndo());
        }
        if (cmd.equalsIgnoreCase("help")) {
            feedback(sender, Evaluator.cmdHelp());
        }
    }

    /* Send chat feedback to sender */
    private static void feedback(ICommandSender sender, String message) {
        sender.addChatMessage(new TextComponentString(message));
    }

}

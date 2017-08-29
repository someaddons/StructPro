package com.ternsip.structpro.universe.commands;

import com.ternsip.structpro.universe.utils.Utils;
import com.ternsip.structpro.universe.utils.Variables;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

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
        Variables vars = new Variables(Utils.join(args, " "));
        if (args.length <= 0) {
            Evaluator.execHelp(vars, sender);
            return;
        }
        String command = args[0];
        vars.put("command", command);
        if (vars.get(new String[]{"help"}, false)) {
            Evaluator.execHelp(vars, sender);
            return;
        }
        if (command.equalsIgnoreCase("paste")) {
            Evaluator.execPaste(vars, sender);
            return;
        }
        if (command.equalsIgnoreCase("save")) {
            Evaluator.execSave(vars, sender);
            return;
        }
        if (command.equalsIgnoreCase("undo")) {
            Evaluator.execUndo(vars, sender);
            return;
        }
        if (command.equalsIgnoreCase("gen")) {
            Evaluator.execGen(vars, sender);
            return;
        }
        Evaluator.execUnknown(vars, sender);
    }

}

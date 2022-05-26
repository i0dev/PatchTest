package com.i0dev.plugin.patchtest.template;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.SimpleConfig;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This abstract class will act as a base for all commands. Any command that is a part of the plugin must extend this class.
 *
 * @author Andrew Magnuson
 */
@Getter
@Setter
public abstract class AbstractCommand implements CommandExecutor, TabExecutor {

    protected PatchTestPlugin plugin;
    public boolean isLoaded = false;

    /**
     * This method is called when the command gets registered in the main class.
     */
    public void initialize() {

    }

    /**
     * This method gets called during shutdown of the plugin.
     */
    public void deinitialize() {

    }

    private String commandName;

    /**
     * This method will be called when the command is actually run by a {@link CommandSender}.
     *
     * @param sender The sender of the command
     * @param args   Any arguments in the command message
     */
    public abstract void execute(CommandSender sender, String[] args);

    /**
     * This method will be called when ever a {@link CommandSender} tab completes within the current command.
     *
     * @param sender The sender tab completing
     * @param args   The current arguments in the command message
     * @return A list of possible options to tab complete through.
     */
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return blank;
    }

    /**
     * This is the listener for when a {@link CommandSender} runs a command, implemented from bukkit.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if it was successful, false if it was not.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase(this.commandName)) return false;
        execute(sender, args);
        return true;
    }

    /**
     * Very basic has permission method that takes the permission prefix from the main class & adds it to the front of any checked permission
     *
     * @param sender     The sender to check permissions of
     * @param permission The permission not including the permission prefix from the main class
     * @return true if the sender has permission, false if they do not
     */
    protected boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(PatchTestPlugin.PERMISSION_PREFIX + "." + permission);
    }

    //
    //         Tab Complete Section
    //
    protected List<String> blank = new ArrayList<>();
    protected List<String> players = null;

    /**
     * This is the listener for when a {@link CommandSender} tab completes, implemented from bukkit.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   The alias used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed and command label
     * @return a list of possible options for the sender to tab complete through
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase(this.commandName)) return null;
        return tabComplete(sender, args);
    }

    /**
     * This tab complete helper acts as a utility to make sure tab complete responses only show options that pertain to the current argument being tabbed on
     *
     * @param arg     The current argument from the command message
     * @param options List of options to check from
     * @return A list of options that pertain to the current argument
     */
    public static List<String> tabCompleteHelper(String arg, List<String> options) {
        if (arg.equalsIgnoreCase("") || arg.equalsIgnoreCase(" ")) return options;
        else
            return options.stream().filter(s -> s.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
    }

    //
    //      Basic Shared Commands
    //

    /**
     * The basic reload command, all plugins have should have this & will be the shared method for reloading the plugins' configuration files.
     *
     * @param sender The sender of the command
     * @param args   Any arguments they passed through
     */
    protected void reload(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "reload")) {
            MsgUtil.msg(sender, msg().getString("noPermission"));
            return;
        }
        MsgUtil.msg(sender, msg().getString("reloadedConfig"));
        plugin.reloadConfig();
    }

    /**
     * The basic help command, all plugins should have this & will be a shared method for sending the help page.
     * <p>
     * The help page gets its commands from the method in {@link com.i0dev.plugin.patchtest.object.CorePlugin}, called setCommandsForHelp(String... commands).
     *
     * @param sender The sender of the command
     * @param args   Any arguments they passed through
     */
    protected void help(CommandSender sender, String[] args) {
        MsgUtil.msg(sender, msg().getString("helpPageTitle"));
        for (String command : PatchTestPlugin.getPlugin().commandsForHelp) {
            MsgUtil.msg(sender, msg().getString("helpPageFormat").replace("{cmd}", command));
        }
    }

    /**
     * The basic version command, all plugins have should have this & will be the shared method for getting plugin version information along with authors & the linked website
     *
     * @param sender The sender of the command
     * @param args   Any arguments they passed through
     */
    protected void version(CommandSender sender, String[] args) {
        PluginDescriptionFile desc = plugin.getDescription();
        String authors = desc.getAuthors().toString();

        MsgUtil.msg(sender, "&9&l" + desc.getName() + " Plugin");
        MsgUtil.msg(sender, "&7Version: &f" + desc.getVersion());
        MsgUtil.msg(sender, "&7Authors: &f" + authors.substring(1, authors.length() - 1));
        MsgUtil.msg(sender, "&7Website: &f" + desc.getWebsite());
    }

    //
    //          Basic Shared Configs
    //
    public SimpleConfig cnf() {
        return plugin.cnf();
    }

    public SimpleConfig msg() {
        return plugin.msg();
    }


}
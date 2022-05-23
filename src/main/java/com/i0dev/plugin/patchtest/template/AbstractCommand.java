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

@Getter
@Setter
public abstract class AbstractCommand implements CommandExecutor, TabExecutor {

    protected PatchTestPlugin plugin;
    public boolean isLoaded = false;

    public SimpleConfig cnf() {
        return plugin.cnf();
    }

    public SimpleConfig msg() {
        return plugin.msg();
    }

    public void initialize() {

    }

    public void deinitialize() {

    }

    private String commandName;

    public abstract void execute(CommandSender sender, String[] args);

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase(this.commandName)) return false;
        execute(sender, args);
        return true;
    }

    /**
     * Tab Complete Section
     */
    protected List<String> blank = new ArrayList<>();
    protected List<String> players = null;

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase(this.commandName)) return null;
        return tabComplete(sender, args);
    }

    public List<String> tabCompleteHelper(String arg, Collection<String> options) {
        if (arg.equalsIgnoreCase("") || arg.equalsIgnoreCase(" "))
            return new ArrayList<>(options);
        else
            return options.stream().filter(s -> s.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
    }

    // Permission
    protected boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(PatchTestPlugin.PERMISSION_PREFIX + "." + permission);
    }


    // Basic Commands

    protected void reload(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "reload")) {
            MsgUtil.msg(sender, msg().getString("noPermission"));
            return;
        }
        MsgUtil.msg(sender, msg().getString("reloadedConfig"));
        plugin.reloadConfig();
    }

    protected void help(CommandSender sender, String[] args) {
        MsgUtil.msg(sender, msg().getString("helpPageTitle"));

        List<String> commands = Arrays.asList(
                "patch help",
                "patch tp",
                "patch create",
                "patch leave",
                "patch start",
                "patch rejoin",
                "patch invite <player>",
                "patch join <player>",
                "patch remove <player>",
                "patch reload",
                "patch version"
        );

        for (String command : commands) {
            MsgUtil.msg(sender, msg().getString("helpPageFormat").replace("{cmd}", command));
        }

    }

    protected void version(CommandSender sender, String[] args) {
        PluginDescriptionFile desc = plugin.getDescription();
        String authors = desc.getAuthors().toString();

        MsgUtil.msg(sender, "&9&l" + desc.getName() + " Plugin");
        MsgUtil.msg(sender, "&7Version: &f" + desc.getVersion());
        MsgUtil.msg(sender, "&7Authors: &f" + authors.substring(1, authors.length() - 1));
        MsgUtil.msg(sender, "&7Website: &f" + desc.getWebsite());
    }


}
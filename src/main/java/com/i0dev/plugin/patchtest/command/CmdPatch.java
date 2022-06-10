package com.i0dev.plugin.patchtest.command;

import com.i0dev.plugin.patchtest.template.AbstractCommand;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class CmdPatch extends AbstractCommand {

    @Getter
    public static final CmdPatch instance = new CmdPatch();


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            help(sender, args);
        } else {
            switch (args[0].toLowerCase()) {
                case "reload":
                    reload(sender, args);
                    break;
                case "version":
                case "ver":
                    version(sender, args);
                    break;
                case "help":
                default:
                    help(sender, args);
            }
        }
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1)
            return tabCompleteHelper(args[0], Arrays.asList("reload", "ver", "version", "help"));
        return blank;
    }
}

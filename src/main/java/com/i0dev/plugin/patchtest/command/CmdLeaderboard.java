package com.i0dev.plugin.patchtest.command;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.manager.InventoryManager;
import com.i0dev.plugin.patchtest.template.AbstractCommand;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdLeaderboard extends AbstractCommand {

    @Getter
    public static final CmdLeaderboard instance = new CmdLeaderboard();


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "leaderboard")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }

        ((Player) sender).openInventory(InventoryManager.getInstance().getMainLeaderboardInventory());
    }
}

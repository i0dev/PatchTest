package com.i0dev.plugin.patchtest.command;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.template.AbstractCommand;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CmdObby extends AbstractCommand {

    @Getter
    public static final CmdObby instance = new CmdObby();


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "obby")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        Inventory inventory = Bukkit.createInventory(null, 54, MsgUtil.color(plugin.cnf().getString("cmdObbyInventoryTitle")));
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, new ItemStack(Material.OBSIDIAN, 64));
        }
        ((Player) sender).openInventory(inventory);

    }
}

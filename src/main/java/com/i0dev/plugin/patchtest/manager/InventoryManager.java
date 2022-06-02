package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.*;
import com.i0dev.plugin.patchtest.object.config.ConfigIndexItemStack;
import com.i0dev.plugin.patchtest.object.config.ConfigItemStack;
import com.i0dev.plugin.patchtest.template.AbstractManager;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class InventoryManager extends AbstractManager {

    @Getter
    private static final InventoryManager instance = new InventoryManager();

    @Override
    public void initialize() {
        setListener(true);
    }

    public Inventory getCreateInventory() {
        SimpleConfig cnf = PatchTestPlugin.getPlugin().cnf();
        Inventory inventory = Bukkit.createInventory(new CreateInventoryHolder(), 27, MsgUtil.color(cnf.getString("createGUI.title")));

        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, new ConfigItemStack(cnf.getConfigurationSection("createGUI.borderGlass").getValues(true)).toItemStack());
        }

        ConfigIndexItemStack ranked = new ConfigIndexItemStack(cnf.getConfigurationSection("createGUI.rankedItem").getValues(true));
        ItemStack rankedItem = ranked.toItemStack();
        NBTItem rankedNBT = new NBTItem(rankedItem);
        rankedNBT.setBoolean("createRankedSession", true);
        rankedItem = rankedNBT.getItem();
        inventory.setItem(ranked.getIndex(), rankedItem);

        ConfigIndexItemStack sandbox = new ConfigIndexItemStack(cnf.getConfigurationSection("createGUI.sandboxItem").getValues(true));
        ItemStack sandboxItem = sandbox.toItemStack();
        NBTItem sandboxNBT = new NBTItem(sandboxItem);
        sandboxNBT.setBoolean("createSandboxSession", true);
        sandboxItem = sandboxNBT.getItem();
        inventory.setItem(sandbox.getIndex(), sandboxItem);

        return inventory;
    }


    @EventHandler
    public void createClickListener(InventoryClickEvent e) {
        if (e.getInventory() == null) return;
        if (!(e.getInventory().getHolder() instanceof CreateInventoryHolder)) return;
        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey("createRankedSession")) {
            createRankedSession((Player) e.getWhoClicked());
            e.getWhoClicked().closeInventory();
        } else if (nbtItem.hasKey("createSandboxSession")) {
            createSandboxSession((Player) e.getWhoClicked());
            e.getWhoClicked().closeInventory();
        }

    }

    private void createRankedSession(Player creator) {
        SessionManager.getInstance().add(new PatchSession(creator, new SessionSettings(true, PatchTestPlugin.getPlugin().cnf().getInt("rankedSettings.cannonSpeed"), CannonType.valueOf(PatchTestPlugin.getPlugin().cnf().getString("rankedSettings.cannonType")), TeamSize.SOLO)));
        MsgUtil.msg(creator, PatchTestPlugin.getPlugin().msg().getStringList("newPatchSession"));
    }

    private void createSandboxSession(Player creator) {
        SessionManager.getInstance().add(new PatchSession(creator, new SessionSettings(false, 3, CannonType.NUKE, TeamSize.UNLIMITED)));
        MsgUtil.msg(creator, PatchTestPlugin.getPlugin().msg().getStringList("newPatchSession"));
    }


    private static class CreateInventoryHolder implements InventoryHolder {

        @Override
        public Inventory getInventory() {
            return null;
        }
    }

}

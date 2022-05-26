package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.CannonType;
import com.i0dev.plugin.patchtest.object.PatchSession;
import com.i0dev.plugin.patchtest.object.SessionSettings;
import com.i0dev.plugin.patchtest.object.TeamSize;
import com.i0dev.plugin.patchtest.template.AbstractManager;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryManager extends AbstractManager {

    @Getter
    private static final InventoryManager instance = new InventoryManager();

    @Override
    public void initialize() {
        setListener(true);
    }

    public Inventory getCreateInventory() {
        Inventory inventory = Bukkit.createInventory(new CreateInventoryHolder(), 27, "Patch Session Create");

        ItemStack ranked = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta rankedMeta = ranked.getItemMeta();
        rankedMeta.setDisplayName("Ranked Gamemode");
        ranked.setItemMeta(rankedMeta);
        NBTItem nbtItem = new NBTItem(ranked);
        nbtItem.setBoolean("createRankedSession", true);
        ranked = nbtItem.getItem();
        inventory.setItem(11, ranked);

        ItemStack sandbox = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta sandboxMeta = sandbox.getItemMeta();
        sandboxMeta.setDisplayName("Sandbox Gamemode");
        sandbox.setItemMeta(sandboxMeta);
        NBTItem nbtItemSandbox = new NBTItem(sandbox);
        nbtItemSandbox.setBoolean("createSandboxSession", true);
        sandbox = nbtItemSandbox.getItem();
        inventory.setItem(15, sandbox);

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

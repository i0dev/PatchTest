package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.*;
import com.i0dev.plugin.patchtest.object.config.ConfigIndexAmountItemStack;
import com.i0dev.plugin.patchtest.object.config.ConfigIndexItemStack;
import com.i0dev.plugin.patchtest.object.config.ConfigItemStack;
import com.i0dev.plugin.patchtest.template.AbstractManager;
import com.i0dev.plugin.patchtest.utility.APIUtil;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import com.i0dev.plugin.patchtest.utility.SkullUtil;
import com.i0dev.plugin.patchtest.utility.TimeUtil;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.Date;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * An inventory manager for the GUI's that appear throughout this plugin.
 *
 * @author Andrew Magnuson
 */
public class InventoryManager extends AbstractManager {

    @Getter
    private static final InventoryManager instance = new InventoryManager();

    @Override
    public void initialize() {
        setListener(true);
    }

    public Inventory getCreateInventory() {
        SimpleConfig cnf = PatchTestPlugin.getPlugin().cnf();
        Inventory inventory = Bukkit.createInventory(new MenuGUI(), 27, MsgUtil.color(cnf.getString("createGUI.title")));

        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, new ConfigItemStack(cnf.getConfigurationSection("createGUI.borderGlass").getValues(true)).toItemStack());
        }

        ConfigIndexItemStack ranked = new ConfigIndexItemStack(cnf.getConfigurationSection("createGUI.rankedItem").getValues(true));
        inventory.setItem(ranked.getIndex(), applyNBT(ranked, "createRankedSession"));

        ConfigIndexItemStack sandbox = new ConfigIndexItemStack(cnf.getConfigurationSection("createGUI.sandboxItem").getValues(true));
        inventory.setItem(sandbox.getIndex(), applyNBT(sandbox, "createSandboxSession"));

        ConfigIndexItemStack versus = new ConfigIndexItemStack(cnf.getConfigurationSection("createGUI.versusItem").getValues(true));
        inventory.setItem(versus.getIndex(), applyNBT(versus, "createVersusSession"));

        return inventory;
    }

    public Inventory getMainLeaderboardInventory() {
        SimpleConfig cnf = PatchTestPlugin.getPlugin().cnf();
        Inventory inventory = Bukkit.createInventory(new MenuGUI(), 27, MsgUtil.color("&c&lLeaderboards"));

        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, new ConfigItemStack(cnf.getConfigurationSection("leaderboardGUI.borderGlass").getValues(true)).toItemStack());
        }

        ConfigIndexAmountItemStack solo = new ConfigIndexAmountItemStack(cnf.getConfigurationSection("leaderboardGUI.soloLeaderboardItem").getValues(true));
        inventory.setItem(solo.getIndex(), applyNBT(solo, "leaderboard_solo"));

        ConfigIndexAmountItemStack duo = new ConfigIndexAmountItemStack(cnf.getConfigurationSection("leaderboardGUI.duoLeaderboardItem").getValues(true));
        inventory.setItem(duo.getIndex(), applyNBT(duo, "leaderboard_duo"));

        ConfigIndexAmountItemStack squad = new ConfigIndexAmountItemStack(cnf.getConfigurationSection("leaderboardGUI.squadLeaderboardItem").getValues(true));
        inventory.setItem(squad.getIndex(), applyNBT(squad, "leaderboard_squad"));

        ConfigIndexAmountItemStack team = new ConfigIndexAmountItemStack(cnf.getConfigurationSection("leaderboardGUI.teamLeaderboardItem").getValues(true));
        inventory.setItem(team.getIndex(), applyNBT(team, "leaderboard_team"));
        return inventory;
    }

    @SneakyThrows
    public Inventory getSpecificLeaderboardInventory(TeamSize teamSize) {
        SimpleConfig cnf = PatchTestPlugin.getPlugin().cnf();
        Inventory inventory = Bukkit.createInventory(new MenuGUI(), 45, MsgUtil.color(cnf.getString("leaderboardGUI.specificTitle").replace("{size}", teamSize.niceName())));

        for (int i = 0; i < 45; i++) {
            inventory.setItem(i, new ConfigItemStack(cnf.getConfigurationSection("leaderboardGUI.borderGlass").getValues(true)).toItemStack());
        }

        ResultSet results = StorageManager.getInstance().executeQuery(String.format("SELECT * FROM `ranked_scores` WHERE teamSize='%s' ORDER BY `lengthHeld` DESC;", teamSize));

        int pos = 1;
        while (results.next()) {
            if (pos >= 9) break;
            int index = Integer.parseInt(String.valueOf(cnf.getConfigurationSection("leaderboardGUI.positionIndexMap").getValues(true).get(String.valueOf(pos))));
            long date = results.getLong("timeEnded");
            String uuid = results.getString("creatorUUID");
            String sessionUuid = results.getString("sessionUUID");
            String creatorName = APIUtil.getIGNFromUUID(uuid);
            ResultSet subPlayers = StorageManager.getInstance().executeQuery(String.format("SELECT * FROM `ranked_scores_sub_users` WHERE sessionUUID='%s'", sessionUuid));
            ItemStack item = SkullUtil.itemFromBase64(SkullUtil.getDataFromUUID(uuid));
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setDisplayName(MsgUtil.color(cnf.getString("leaderboardGUI.positionTitle")
                    .replace("{pos}", String.valueOf(pos))
                    .replace("{player}", creatorName)));


            List<String> playersLore = new ArrayList<>();
            playersLore.add(MsgUtil.color(cnf.getString("leaderboardGUI.positionLorePlayerEntry").replace("{player}", creatorName)));
            while (subPlayers.next()) {
                playersLore.add(MsgUtil.color(cnf.getString("leaderboardGUI.positionLorePlayerEntry")
                        .replace("{player}", APIUtil.getIGNFromUUID(subPlayers.getString("playerUUID"))))
                );
            }

            List<String> newLore = new ArrayList<>();
            String formattedDate = new SimpleDateFormat("MM-dd-yyyy").format(Date.from(Instant.ofEpochMilli(date)));

            for (String s : cnf.getStringList("leaderboardGUI.positionLore")) {
                if (s.equalsIgnoreCase("{players}")) {
                    newLore.addAll(playersLore);
                    continue;
                }
                newLore.add(MsgUtil.color(s
                        .replace("{date}", formattedDate)
                        .replace("{time}", TimeUtil.formatTimePeriod(results.getLong("lengthHeld"))
                        )));
            }
            meta.setLore(newLore);
            item.setItemMeta(meta);
            inventory.setItem(index, item);
            pos++;
        }

        return inventory;
    }


    @EventHandler
    public void createClickListener(InventoryClickEvent e) {
        if (e.getInventory() == null) return;
        if (!(e.getInventory().getHolder() instanceof MenuGUI)) return;
        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasKey("createRankedSession")) {
            createRankedSession((Player) e.getWhoClicked());
            e.getWhoClicked().closeInventory();
        } else if (nbtItem.hasKey("createSandboxSession")) {
            //  createSandboxSession((Player) e.getWhoClicked());
            e.getWhoClicked().sendMessage(MsgUtil.color("&cSandbox is disabled for beta, please only use ranked or versus mode!"));
            e.getWhoClicked().closeInventory();
        } else if (nbtItem.hasKey("createVersusSession")) {
            createVersusSession((Player) e.getWhoClicked());
            e.getWhoClicked().closeInventory();
        } else if (nbtItem.hasKey("leaderboard_solo")) {
            e.getWhoClicked().openInventory(getSpecificLeaderboardInventory(TeamSize.SOLO));
        } else if (nbtItem.hasKey("leaderboard_duo")) {
            e.getWhoClicked().openInventory(getSpecificLeaderboardInventory(TeamSize.DUO));
        } else if (nbtItem.hasKey("leaderboard_squad")) {
            e.getWhoClicked().openInventory(getSpecificLeaderboardInventory(TeamSize.SQUAD));
        } else if (nbtItem.hasKey("leaderboard_team")) {
            e.getWhoClicked().openInventory(getSpecificLeaderboardInventory(TeamSize.TEAM));
        }
    }

    private void createRankedSession(Player creator) {
        SessionManager.getInstance().createSession(PartyManager.getInstance().getParty(creator.getUniqueId()), SessionType.RANKED);
        MsgUtil.msg(creator, PatchTestPlugin.getPlugin().msg().getStringList("newPatchSession"));
    }

    private void createSandboxSession(Player creator) {
        SessionManager.getInstance().createSession(PartyManager.getInstance().getParty(creator.getUniqueId()), SessionType.SANDBOX);
        MsgUtil.msg(creator, PatchTestPlugin.getPlugin().msg().getStringList("newPatchSession"));
    }

    private void createVersusSession(Player creator) {
        SessionManager.getInstance().createSession(PartyManager.getInstance().getParty(creator.getUniqueId()), SessionType.VERSUS);
        MsgUtil.msg(creator, PatchTestPlugin.getPlugin().msg().getStringList("newPatchSession"));
    }


    private ItemStack applyNBT(ItemStack item, String nbt) {
        NBTItem rankedNBT = new NBTItem(item);
        rankedNBT.setBoolean(nbt, true);
        return rankedNBT.getItem();
    }

    private ItemStack applyNBT(ConfigItemStack item, String nbt) {
        return applyNBT(item.toItemStack(), nbt);
    }

    private ItemStack applyNBT(String itemConfigPath, String nbt) {
        ConfigIndexItemStack cnf = new ConfigIndexItemStack(PatchTestPlugin.getPlugin().cnf().getConfigurationSection(itemConfigPath).getValues(true));
        return applyNBT(cnf.toItemStack(), nbt);
    }


    private static class MenuGUI implements InventoryHolder {

        @Override
        public Inventory getInventory() {
            return null;
        }
    }

}

package com.i0dev.plugin.patchtest.config;

import com.i0dev.plugin.patchtest.object.CannonType;
import com.i0dev.plugin.patchtest.object.TeamSize;
import com.i0dev.plugin.patchtest.object.config.ConfigIndexAmountItemStack;
import com.i0dev.plugin.patchtest.object.config.ConfigIndexItemStack;
import com.i0dev.plugin.patchtest.object.config.ConfigItemStack;
import com.i0dev.plugin.patchtest.template.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class GeneralConfig extends AbstractConfiguration {
    public GeneralConfig(String path) {
        super(path);
    }

    protected void setValues() {
        config.set("countdownLengthSeconds", 20);
        config.set("patchWorldName", "world");
        config.set("patchKitName", "patch");
        config.set("adjustTimeRandomHigherBound", 20);
        config.set("adjustTimeRandomLowerBound", 10);
        config.set("cannonSpeed", 3);

        config.set("countdownSubtitle", "&7seconds remaining...");
        config.set("countdownTitle", "&a&l{sec}");
        config.set("countdownGoTitle", "&a&lGO!!!");
        config.set("countdownGoSubtitle", "&7Patch your walls to defend your base!");

        config.set("cmdObbyInventoryTitle", "&cFree Obsidian");

        config.set("rankedSettings.cannonType", CannonType.NUKE.toString());
        Map<String, Integer> speedMap = new LinkedHashMap<>();
        speedMap.put(TeamSize.SOLO.toString(), 3);
        speedMap.put(TeamSize.DUO.toString(), 3);
        speedMap.put(TeamSize.SQUAD.toString(), 3);
        speedMap.put(TeamSize.TEAM.toString(), 2);
        config.set("rankedSettings.teamSizeCannonSpeedMap", speedMap);

        config.set("createGUI.title", "&c&lCreate a Patch Session");
        config.set("createGUI.borderGlass", new ConfigItemStack(
                Material.STAINED_GLASS_PANE,
                "&f",
                new ArrayList<>(),
                15,
                true

        ).serialize());
        config.set("createGUI.rankedItem", new ConfigIndexItemStack(
                Material.DIAMOND,
                "&c&lRanked",
                Arrays.asList(
                        "",
                        "&7This is the ranked mode of patch testing",
                        "&7Stats will be recorded at the end of the session",
                        "&7And will be added to a leaderboard!",
                        "",
                        "&c&lRanked Settings:",
                        " &4* &7Standard Nuke Cannon",
                        " &4* &710 Chunk Buffers",
                        " &4* &7Adjusting every 10-20 seconds",
                        " &4* &7Complex Intelligent Adjusting",
                        " &4* &7No Pausing",
                        " &4* &7Progressive Guardians",
                        "",
                        "&c&lTeam Sizes & Cannon Speeds",
                        " &4* &7Solo &4:&7 3 Second",
                        " &4* &7Duo &4:&7 3 Second",
                        " &4* &7Squad &4:&7 3 Second",
                        " &4* &7Team &4:&7 2 Second",
                        "",
                        "&7Depending on how many players you invite to your session,",
                        "&7it will put you in the respective team size category"
                ),
                0,
                true,
                11
        ).serialize());
        config.set("createGUI.sandboxItem", new ConfigIndexItemStack(
                Material.WOOD_SPADE,
                "&c&lSandbox",
                Arrays.asList(
                        "",
                        "&7This is the sandbox mode of patch testing",
                        "&7Stats will not be recorded. You can change",
                        "&7the settings of the plot anytime with &c/plot settings",
                        "",
                        "&7You can invite as many players to your patch session as you wish"
                ),
                0,
                true,
                15

        ).serialize());


        config.set("leaderboardGUI.borderGlass", new ConfigItemStack(
                Material.STAINED_GLASS_PANE,
                "&f",
                new ArrayList<>(),
                15,
                true

        ).serialize());

        config.set("leaderboardGUI.soloLeaderboardItem", new ConfigIndexAmountItemStack(
                Material.DIAMOND_HELMET,
                "&c&lSolos",
                Arrays.asList(
                        "",
                        "&7Click to go to the leaderboard for the &csolo&7 session size"
                ),
                0,
                true,
                10,
                1
        ).serialize());
        config.set("leaderboardGUI.duoLeaderboardItem", new ConfigIndexAmountItemStack(
                Material.DIAMOND_HELMET,
                "&c&lDuos",
                Arrays.asList(
                        "",
                        "&7Click to go to the leaderboard for the &cduo&7 session size"
                ),
                0,
                true,
                12,
                2
        ).serialize());
        config.set("leaderboardGUI.squadLeaderboardItem", new ConfigIndexAmountItemStack(
                Material.DIAMOND_HELMET,
                "&c&lSquads",
                Arrays.asList(
                        "",
                        "&7Click to go to the leaderboard for the &csquad&7 session size"
                ),
                0,
                true,
                14,
                5
        ).serialize());
        config.set("leaderboardGUI.teamLeaderboardItem", new ConfigIndexAmountItemStack(
                Material.DIAMOND_HELMET,
                "&c&lTeams",
                Arrays.asList(
                        "",
                        "&7Click to go to the leaderboard for the &cteam&7 session size"
                ),
                0,
                true,
                16,
                10
        ).serialize());

        Map<String, Integer> positionIndexMap = new LinkedHashMap<>();
        positionIndexMap.put("1", 13);
        positionIndexMap.put("2", 21);
        positionIndexMap.put("3", 22);
        positionIndexMap.put("4", 23);
        positionIndexMap.put("5", 29);
        positionIndexMap.put("6", 30);
        positionIndexMap.put("7", 31);
        positionIndexMap.put("8", 32);
        positionIndexMap.put("9", 33);
        config.set("leaderboardGUI.positionIndexMap", positionIndexMap);
        config.set("leaderboardGUI.mainTitle", "&c&lLeaderboards");
        config.set("leaderboardGUI.specificTitle", "&c&lLeaderboard for {size}");

        config.set("leaderboardGUI.positionTitle", "&4&l#{pos} &c- {player}");

        config.set("leaderboardGUI.positionLore", Arrays.asList(
                "",
                "&c&lTime Held:",
                "&f{time}",
                "",
                "&c&lTeam Members:",
                "{players}",
                "",
                "&8Date: &7{date}"
        ));
        config.set("leaderboardGUI.positionLorePlayerEntry", " &4* &7{player}");


    }
}

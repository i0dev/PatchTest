package com.i0dev.plugin.patchtest.config;

import com.i0dev.plugin.patchtest.object.CannonType;
import com.i0dev.plugin.patchtest.object.SessionSettings;
import com.i0dev.plugin.patchtest.object.TeamSize;
import com.i0dev.plugin.patchtest.template.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;

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
        config.set("adjustTimeRandomHigherBound", 50);
        config.set("adjustTimeRandomLowerBound", 20);
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

    }
}

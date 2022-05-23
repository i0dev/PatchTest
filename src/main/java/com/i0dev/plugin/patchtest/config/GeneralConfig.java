package com.i0dev.plugin.patchtest.config;

import com.i0dev.plugin.patchtest.template.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;

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
    }
}

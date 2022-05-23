package com.i0dev.plugin.patchtest;

import com.i0dev.plugin.patchtest.command.CmdObby;
import com.i0dev.plugin.patchtest.config.GeneralConfig;
import com.i0dev.plugin.patchtest.config.MessageConfig;
import com.i0dev.plugin.patchtest.command.CmdPatch;
import com.i0dev.plugin.patchtest.config.MonsterConfig;
import com.i0dev.plugin.patchtest.hook.PlaceholderAPIHook;
import com.i0dev.plugin.patchtest.manager.*;
import com.i0dev.plugin.patchtest.object.CorePlugin;
import com.i0dev.plugin.patchtest.object.config.MonsterSpawnTime;
import com.i0dev.plugin.patchtest.object.config.PotionEffect;
import com.i0dev.plugin.patchtest.template.AbstractConfiguration;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;

import java.io.*;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PatchTestPlugin extends CorePlugin {
    @Getter
    private static PatchTestPlugin plugin;
    public static final String PERMISSION_PREFIX = "patchtest";

    @SneakyThrows
    @Override
    public void startup() {
        plugin = this;

        // Managers
        registerManager(ConfigManager.getInstance());
        registerManager(PlotManager.getInstance());
        registerManager(CannonManager.getInstance());
        registerManager(SessionManager.getInstance());
        registerManager(MobManager.getInstance());

        // Hooks
        if (isPluginEnabled("PlaceholderAPI"))
            registerHook(new PlaceholderAPIHook(), "papi");

        registerConfig(new GeneralConfig("config.yml"));
        registerConfig(new MessageConfig("messages.yml"));
        registerConfig(new MonsterConfig("monsters.yml"));

        // Commands
        registerCommand(CmdPatch.getInstance(), "patch");
        registerCommand(CmdObby.getInstance(), "obby");
    }

    @Override
    public void shutdown() {

    }

    public static String getWorldName() {
        return PatchTestPlugin.getPlugin().cnf().getString("patchWorldName");
    }

    public static String getMsg(String path) {
        return PatchTestPlugin.getPlugin().msg().getString(path);
    }

}

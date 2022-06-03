package com.i0dev.plugin.patchtest;

import com.i0dev.plugin.patchtest.command.CmdLeaderboard;
import com.i0dev.plugin.patchtest.command.CmdObby;
import com.i0dev.plugin.patchtest.config.GeneralConfig;
import com.i0dev.plugin.patchtest.config.MessageConfig;
import com.i0dev.plugin.patchtest.command.CmdPatch;
import com.i0dev.plugin.patchtest.config.MonsterConfig;
import com.i0dev.plugin.patchtest.hook.PlaceholderAPIHook;
import com.i0dev.plugin.patchtest.manager.*;
import com.i0dev.plugin.patchtest.object.CorePlugin;
import lombok.Getter;
import lombok.SneakyThrows;

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
        registerManager(InventoryManager.getInstance());
        registerManager(MobManager.getInstance());
        registerManager(StorageManager.getInstance());

        // Hooks
        if (isPluginEnabled("PlaceholderAPI"))
            registerHook(new PlaceholderAPIHook(), "papi");

        registerConfig(new GeneralConfig("config.yml"));
        registerConfig(new MessageConfig("messages.yml"));
        registerConfig(new MonsterConfig("monsters.yml"));

        // Commands
        registerCommand(CmdPatch.getInstance(), "patch");
        registerCommand(CmdObby.getInstance(), "obby");
        registerCommand(CmdLeaderboard.getInstance(), "leaderboard");

        setCommandsForHelp(
                "patch help",
                "patch tp",
                "patch create",
                "patch leave",
                "patch start",
                "patch rejoin",
                "patch invite <player>",
                "patch join <player>",
                "patch remove <player>",
                "patch reload",
                "patch version",
                "obby"
        );
    }

    /**
     * @return The world name for all patch test interactions, defined in config.
     */
    public static String getWorldName() {
        return PatchTestPlugin.getPlugin().cnf().getString("patchWorldName");
    }

    /**
     * Gets a message directly from the messaging config
     *
     * @param path The path of the message.
     * @return The message that corresponds to the specified path.
     */
    public static String getMsg(String path) {
        return PatchTestPlugin.getPlugin().msg().getString(path);
    }

}

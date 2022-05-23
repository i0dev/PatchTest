package com.i0dev.plugin.patchtest.template;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.manager.ConfigManager;
import com.i0dev.plugin.patchtest.object.SimpleConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
public abstract class AbstractConfiguration {

    protected SimpleConfig config;
    String path;

    @SneakyThrows
    public AbstractConfiguration(String path) {
        this.path = path;
        this.config = ConfigManager.getInstance().getNewConfig(path, new String[]{"Plugin made by " + PatchTestPlugin.getPlugin().getDescription().getAuthors().toString().substring(1, PatchTestPlugin.getPlugin().getDescription().getAuthors().toString().length() - 1)});
        this.setValues();
    }

    protected abstract void setValues();
}

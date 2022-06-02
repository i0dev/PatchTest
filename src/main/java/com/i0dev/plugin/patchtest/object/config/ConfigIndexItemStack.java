package com.i0dev.plugin.patchtest.object.config;

import lombok.Getter;
import org.bukkit.Material;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ConfigIndexItemStack extends ConfigItemStack {

    protected int index;

    public ConfigIndexItemStack(Material material, String displayName, List<String> lore, int data, boolean glow, int index) {
        super(material, displayName, lore, data, glow);
        this.index = index;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("material", material.toString());
        data.put("displayName", displayName);
        data.put("lore", lore);
        data.put("data", this.data);
        data.put("glow", glow);
        data.put("index", index);
        return data;
    }

    public ConfigIndexItemStack(Map<String, Object> map) {
        super(map);
        this.index = (int) map.get("index");
    }
}

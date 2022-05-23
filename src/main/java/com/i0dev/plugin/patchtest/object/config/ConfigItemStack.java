package com.i0dev.plugin.patchtest.object.config;

import com.i0dev.plugin.patchtest.utility.MsgUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ConfigItemStack implements SerializableConfig {

    private Material material;
    private String displayName;
    private List<String> lore;
    private Map<String, Integer> enchantments;

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(material);
        enchantments.forEach((enchantment, level) -> stack.addUnsafeEnchantment(Enchantment.getByName(enchantment), level));
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(MsgUtil.color(displayName));
        meta.setLore(MsgUtil.color(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("material", material.toString());
        data.put("displayName", displayName);
        data.put("lore", lore);
        data.put("enchantments", enchantments);
        return data;
    }

    public ConfigItemStack(Map<String, Object> map) {
        this.material = Material.valueOf((String) map.get("material"));
        this.displayName = (String) map.get("displayName");
        this.lore = (List<String>) map.get("lore");
        this.enchantments = (Map<String, Integer>) map.get("enchantments");
    }

}

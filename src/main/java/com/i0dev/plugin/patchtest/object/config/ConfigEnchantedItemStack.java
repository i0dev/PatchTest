package com.i0dev.plugin.patchtest.object.config;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ConfigEnchantedItemStack extends ConfigItemStack {


    protected Map<String, Integer> enchantments;

    @Override
    public ItemStack toItemStack() {
        ItemStack stack = super.toItemStack();
        enchantments.forEach((enchantment, level) -> stack.addUnsafeEnchantment(Enchantment.getByName(enchantment), level));
        return stack;
    }


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("material", material.toString());
        data.put("displayName", displayName);
        data.put("lore", lore);
        data.put("enchantments", enchantments);
        data.put("data", this.data);
        data.put("glow", this.glow);
        return data;
    }

    public ConfigEnchantedItemStack(Map<String, Object> map) {
        super(map);
        this.enchantments = (Map<String, Integer>) map.get("enchantments");
    }

    public ConfigEnchantedItemStack(Material material, String displayName, List<String> lore, int data, boolean glow, Map<String, Integer> enchantments) {
        super(material, displayName, lore, data, glow);
        this.enchantments = enchantments;
    }
}

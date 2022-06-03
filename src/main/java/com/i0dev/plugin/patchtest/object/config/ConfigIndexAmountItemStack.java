package com.i0dev.plugin.patchtest.object.config;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ConfigIndexAmountItemStack extends ConfigIndexItemStack {

    protected int amount;

    public ConfigIndexAmountItemStack(Material material, String displayName, List<String> lore, int data, boolean glow, int index, int amount) {
        super(material, displayName, lore, data, glow, index);
        this.amount = amount;
    }

    public ConfigIndexAmountItemStack(Map<String, Object> map) {
        super(map);
        this.amount = (int) map.get("amount");
    }

    @Override
    public ItemStack toItemStack() {
        ItemStack stack = super.toItemStack();
        stack.setAmount(amount);
        return stack;
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
        data.put("amount", amount);
        return data;
    }


}

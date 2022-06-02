package com.i0dev.plugin.patchtest.object;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

/**
 * @author Embed CM
 */
public class EnchantGlow extends EnchantmentWrapper {
    private static Enchantment glow = null;
    private final String name;

    public EnchantGlow(int i) {
        super(i);
        name = "Glow";
    }

    public static ItemStack addGlow(ItemStack itemstack) {
        itemstack.addEnchantment(getGlow(), 1);
        return itemstack;
    }

    public static Enchantment getGlow() {
        if (glow != null) return glow;
        Field field;
        try {
            field = Enchantment.class.getDeclaredField("acceptingNew");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
            return glow;
        }
        field.setAccessible(true);
        try {
            field.set(null, true);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            glow = new EnchantGlow(Enchantment.values().length + 100);
        } catch (Exception e) {
            glow = Enchantment.getByName("Glow");
        }
        if (Enchantment.getByName("Glow") == null) Enchantment.registerEnchantment(glow);
        return glow;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Enchantment getEnchantment() {
        return Enchantment.getByName("Glow");
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return true;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }
}
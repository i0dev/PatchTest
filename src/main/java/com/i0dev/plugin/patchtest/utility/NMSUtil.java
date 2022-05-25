package com.i0dev.plugin.patchtest.utility;

import org.bukkit.Bukkit;

/**
 * Utility class for net.minecraft.server & org.bukkit.craftbukkit interaction helpers.
 * <p>
 * Credit to EmberCM for this utility class.
 */
public class NMSUtil {

    /**
     * @param name The name of the class
     * @return the org.bukkit.craftbukkit class from the active version of bukkit running on the server.
     */
    public static Class<?> getOBCClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit."
                    + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * @param name The name of the class
     * @return the net.minecraft.server class from the active version of minecraft running on the server.
     */
    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName(
                    "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * @param name The name of the class
     * @param def The default/backup class to find if the primary name could not be found.
     * @return the net.minecraft.server class from the active version of minecraft running on the server.
     */
    public static Class<?> getNMSClass(String name, String def) {
        return getNMSClass(name) != null ? getNMSClass(name) : getNMSClass(def.split("\\.")[0]).getDeclaredClasses()[0];
    }


}

package com.i0dev.plugin.patchtest.utility;

import me.clip.placeholderapi.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleUtil {

    public static Class<?> getOBCClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit."
                    + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName(
                    "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> getNMSClass(String name, String def) {
        return getNMSClass(name) != null ? getNMSClass(name) : getNMSClass(def.split("\\.")[0]).getDeclaredClasses()[0];
    }

    public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        try {
            Object entity = getOBCClass("entity.CraftPlayer").cast(player);
            Object handle = entity.getClass().getMethod("getHandle").invoke(entity);
            Object connection = handle.getClass().getField("playerConnection").get(handle);
            Class<?> enumClass = getNMSClass("EnumTitleAction", "PacketPlayOutTitle.EnumTitleAction");
            Object cbc = getNMSClass("IChatBaseComponent").getDeclaredClasses().length != 0 ? getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getDeclaredMethod("a", String.class).invoke(null, "") : null;
            Object packet = getNMSClass("PacketPlayOutTitle")
                    .getConstructor(enumClass, getNMSClass("IChatBaseComponent"), int.class, int.class, int.class)
                    .newInstance(enumClass.getDeclaredMethod("a", String.class).invoke(null, "TIMES"), cbc, fadeIn, stay, fadeOut);
            connection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(connection, packet);
            sendPacket(player, title, "TITLE", connection, enumClass);
            sendPacket(player, subtitle, "SUBTITLE", connection, enumClass);
        } catch (Exception ex) {
            try {
                player.getClass().getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class).invoke(player, MsgUtil.color(title), MsgUtil.color(subtitle), fadeIn, stay, fadeOut);
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }

    private static void sendPacket(Player player, String text, String type, Object connection, Class<?> enumClass) throws Exception {
        text = "{\"text\": \"" + MsgUtil.color(text.replaceAll("%player%", player.getDisplayName())) + "\"}";
        Object json = getNMSClass("ChatSerializer", "IChatBaseComponent.ChatSerializer").getMethod("a", String.class).invoke(null, text);
        Object titlePacket = getNMSClass("PacketPlayOutTitle").getConstructor(enumClass, getNMSClass("IChatBaseComponent"))
                .newInstance(enumClass.getDeclaredMethod("a", String.class).invoke(null, type), json);
        connection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(connection, titlePacket);
    }

}

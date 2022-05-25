package com.i0dev.plugin.patchtest.utility;

import org.bukkit.entity.Player;

/**
 * A utility class for sending titles to players.
 * <p>
 * Credit to EmberCM for these methods
 */
public class TitleUtil {


    /**
     * Sends a title to the specified player.
     *
     * @param player   The player to send the title to
     * @param fadeIn   Ticks to spend on the fade in animation
     * @param stay     Ticks to stay on screen
     * @param fadeOut  Ticks to spend on the fade out animation
     * @param title    The title to send
     * @param subtitle The subtitle to send
     */
    public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        try {
            Object entity = NMSUtil.getOBCClass("entity.CraftPlayer").cast(player);
            Object handle = entity.getClass().getMethod("getHandle").invoke(entity);
            Object connection = handle.getClass().getField("playerConnection").get(handle);
            Class<?> enumClass = NMSUtil.getNMSClass("EnumTitleAction", "PacketPlayOutTitle.EnumTitleAction");
            Object cbc = NMSUtil.getNMSClass("IChatBaseComponent").getDeclaredClasses().length != 0 ? NMSUtil.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getDeclaredMethod("a", String.class).invoke(null, "") : null;
            Object packet = NMSUtil.getNMSClass("PacketPlayOutTitle")
                    .getConstructor(enumClass, NMSUtil.getNMSClass("IChatBaseComponent"), int.class, int.class, int.class)
                    .newInstance(enumClass.getDeclaredMethod("a", String.class).invoke(null, "TIMES"), cbc, fadeIn, stay, fadeOut);
            connection.getClass().getMethod("sendPacket", NMSUtil.getNMSClass("Packet")).invoke(connection, packet);
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

    /**
     * Sends the packet off to the player, acts as a utility for sendTitle() method above.
     *
     * @param player     The player to send the packet to
     * @param text       The text to send
     * @param type       The text type to send
     * @param connection The connection to the player
     * @param enumClass  The enum class of the title action
     * @throws Exception if it fails to send the packet
     */
    private static void sendPacket(Player player, String text, String type, Object connection, Class<?> enumClass) throws Exception {
        text = "{\"text\": \"" + MsgUtil.color(text.replaceAll("%player%", player.getDisplayName())) + "\"}";
        Object json = NMSUtil.getNMSClass("ChatSerializer", "IChatBaseComponent.ChatSerializer").getMethod("a", String.class).invoke(null, text);
        Object titlePacket = NMSUtil.getNMSClass("PacketPlayOutTitle").getConstructor(enumClass, NMSUtil.getNMSClass("IChatBaseComponent"))
                .newInstance(enumClass.getDeclaredMethod("a", String.class).invoke(null, type), json);
        connection.getClass().getMethod("sendPacket", NMSUtil.getNMSClass("Packet")).invoke(connection, titlePacket);
    }

}

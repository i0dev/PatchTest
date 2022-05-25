package com.i0dev.plugin.patchtest.utility;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.hook.PlaceholderAPIHook;
import com.i0dev.plugin.patchtest.object.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A messaging utility class to send messages to players.
 *
 * @author Andrew magnuson
 */
public class MsgUtil {

    /**
     * Formats the string passed through and translates the color codes & any hex codes present.
     *
     * @param string The string to format colors for
     * @return The formatted string with color codes translated
     */
    public static String color(String string) {
        return translateHexColorCodes(ChatColor.translateAlternateColorCodes('&', string));
    }

    /**
     * Formats each string in the list passed through, works the same as the above method
     *
     * @param stringList The list of strings that need to be formatted
     * @return The formatted list of strings with color codes translated
     */
    public static List<String> color(List<String> stringList) {
        List<String> ret = new ArrayList<>();
        stringList.forEach(s -> ret.add(color(s)));
        return ret;
    }

    /**
     * Uses the Placeholder API plugin to replace any placeholders in the string.
     * If sender is not a {@link Player} then it will not perform any formatting.
     * The placeholder hook must be enabled in the startup() method of the plugin for formatting to occur.
     *
     * @param sender The sender to replace placeholders with
     * @param string The string to replace placeholders in
     * @return A String with the placeholders replaced if applicable
     */
    public static String papi(CommandSender sender, String string) {
        if (!PatchTestPlugin.getPlugin().isHookEnabled("papi") || !(sender instanceof Player)) return string;
        return PatchTestPlugin.getPlugin().getHook(PlaceholderAPIHook.class).replace((Player) sender, string);
    }

    /**
     * Will replace the msg with the pairs passed through.
     * The pairs will act as placeholders, replacing all instances of the first value of the pair with the second
     *
     * @param string The string to replace
     * @param pairs  A list of pairs to replace with.
     * @return A formatted string from the pairs passed through.
     */
    public static String pair(String string, Pair<String, String>... pairs) {
        for (Pair<String, String> pair : pairs) {
            string = string.replace(pair.getKey(), pair.getValue());
        }
        return string;
    }

    /**
     * Sends a message to the specified {@link CommandSender}
     * - Formats color
     * - Formats with Placeholder API
     * - Formats with {@link Pair} placeholders
     *
     * @param sender  The {@link CommandSender} to message
     * @param message the message to send
     * @param pairs   {@link Pair} placeholders to format
     */
    @SafeVarargs
    public static void msg(CommandSender sender, String message, Pair<String, String>... pairs) {
        sender.sendMessage(color(papi(sender, pair(message, pairs))));
    }


    /**
     * Sends multiple messages to the specified {@link CommandSender}
     * - Formats color
     * - Formats with Placeholder API
     * - Formats with {@link Pair} placeholders
     *
     * @param sender   The {@link CommandSender} to message
     * @param messages the messages to send
     * @param pairs    {@link Pair} placeholders to format
     */
    @SafeVarargs
    public static void msg(CommandSender sender, Collection<String> messages, Pair<String, String>... pairs) {
        messages.forEach(s -> sender.sendMessage(color(papi(sender, pair(s, pairs)))));
    }

    /**
     * Sends a message to every player on the server.
     * - Formats color
     * - Formats with Placeholder API
     * - Formats with {@link Pair} placeholders
     *
     * @param message the message to send
     * @param pairs   {@link Pair} placeholders to format
     */
    @SafeVarargs
    public static void msgAll(String message, Pair<String, String>... pairs) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(color(papi(player, pair(message, pairs)))));
    }

    /**
     * Sends multiple messages to every player on the server.
     * - Formats color
     * - Formats with Placeholder API
     * - Formats with {@link Pair} placeholders
     *
     * @param messages the messages to send
     * @param pairs    {@link Pair} placeholders to format
     */
    @SafeVarargs
    public static void msgAll(Collection<String> messages, Pair<String, String>... pairs) {
        messages.forEach(s -> Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(color(papi(player, pair(s, pairs))))));
    }

    /**
     * Parses a player from the specified string.
     * If the string is longer than 16 characters it will consider it as an uuid & parse it as one
     *
     * @param string The {@link String} to parse from
     * @return a player if found. null if not
     */
    public static Player getPlayer(String string) {
        if (string.length() > 16)
            return Bukkit.getPlayer(UUID.fromString(string));
        return Bukkit.getPlayer(string);
    }

    /**
     * Translates a message with new hex colors
     *
     * @param message The message to format
     * @return A formatted string
     */
    public static String translateHexColorCodes(String message) {
        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        char colorChar = ChatColor.COLOR_CHAR;

        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        while (matcher.find()) {
            String group = matcher.group(1);

            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }

}

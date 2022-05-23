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

public class MsgUtil {

    public static String color(String s) {
        return translateHexColorCodes(ChatColor.translateAlternateColorCodes('&', s));
    }

    public static List<String> color(List<String> ss) {
        List<String> ret = new ArrayList<>();
        ss.forEach(s -> ret.add(color(s)));
        return ret;
    }

    public static String papi(CommandSender sender, String s) {
        if (!PatchTestPlugin.getPlugin().isHookEnabled("papi") || !(sender instanceof Player)) return s;
        return PatchTestPlugin.getPlugin().getHook(PlaceholderAPIHook.class).replace((Player) sender, s);
    }

    public static String full(CommandSender sender, String msg, Pair<String, String>... pairs) {
        return color(papi(sender, pair(msg, pairs)));
    }

    public static String pair(String msg, Pair<String, String>... pairs) {
        for (Pair<String, String> pair : pairs) {
            msg = msg.replace(pair.getKey(), pair.getValue());
        }
        return msg;
    }

    @SafeVarargs
    public static void msg(CommandSender sender, String msg, Pair<String, String>... pairs) {
        sender.sendMessage(color(papi(sender, pair(msg, pairs))));
    }

    @SafeVarargs
    public static void msg(CommandSender sender, Collection<String> msg, Pair<String, String>... pairs) {
        msg.forEach(s -> sender.sendMessage(color(papi(sender, pair(s, pairs)))));
    }

    @SafeVarargs
    public static void msgAll(String msg, Pair<String, String>... pairs) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(color(papi(player, pair(msg, pairs)))));
    }

    @SafeVarargs
    public static void msgAll(Collection<String> msg, Pair<String, String>... pairs) {
        msg.forEach(s -> Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(color(papi(player, pair(s, pairs))))));
    }

    public static Player getPlayer(String s) {
        if (s.length() > 20)
            return Bukkit.getPlayer(UUID.fromString(s));
        return Bukkit.getPlayer(s);
    }

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

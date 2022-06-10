package com.i0dev.plugin.patchtest.hook;

import com.i0dev.plugin.patchtest.manager.SessionManager;
import com.i0dev.plugin.patchtest.object.Session;
import com.i0dev.plugin.patchtest.template.AbstractHook;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends AbstractHook {

    public String replace(Player player, String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }

    @Override
    public void initialize() {
        new PatchExpansion().register();
    }

    public static class PatchExpansion extends PlaceholderExpansion implements Relational {
        @Override
        public @NotNull String getIdentifier() {
            return "patchtest";
        }

        @Override
        public @NotNull String getAuthor() {
            return "i01";
        }

        @Override
        public @NotNull String getVersion() {
            return "1.0.0";
        }




        @Override
        public String onPlaceholderRequest(Player one, Player two, String identifier) {
            if (identifier.equalsIgnoreCase("relationColor")) {
                Session session = SessionManager.getInstance().getSession(one);
                Session session2 = SessionManager.getInstance().getSession(two);

                if (session == null) return "&f";
                if (session2 == null) return "&f";

                if (session.getParty().getMembers().contains(one.getUniqueId()) && session.getParty().getMembers().contains(two.getUniqueId()))
                    return "&a";
                if (session.getVersusParty().getMembers().contains(one.getUniqueId()) && session.getVersusParty().getMembers().contains(two.getUniqueId()))
                    return "&a";
                if (session.getVersusParty().getMembers().contains(one.getUniqueId()) && session.getParty().getMembers().contains(two.getUniqueId()))
                    return "&c";
                if (session.getParty().getMembers().contains(one.getUniqueId()) && session.getVersusParty().getMembers().contains(two.getUniqueId()))
                    return "&c";
                return "&f";
            }
            return null;
        }
    }

}

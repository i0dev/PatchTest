package com.i0dev.plugin.patchtest.hook;

import com.i0dev.plugin.patchtest.manager.SessionManager;
import com.i0dev.plugin.patchtest.object.Session;
import com.i0dev.plugin.patchtest.template.AbstractHook;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * PlaceholderAPI Hook - Adds custom placeholders from patch test to be used across the server
 *
 * @author Andrew Magnuson
 */
public class PlaceholderAPIHook extends AbstractHook {

    /**
     * This method will take in a message & a player and set the placeholders registered in PlaceholderAPI
     * @param player The player to parse to.
     * @param message The message to replace.
     * @return A formatted message with PAPI placeholders.
     */
    public String replace(Player player, String message) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }

    @Override
    public void initialize() {
        new PatchExpansion().register();
    }

    public static class PatchExpansion extends PlaceholderExpansion implements Relational {

        /**
         * @return The expansion identifier.
         */
        @Override
        public @NotNull String getIdentifier() {
            return "patchtest";
        }

        /**
         * @return The author of the expansion.
         */
        @Override
        public @NotNull String getAuthor() {
            return "i01";
        }

        /**
         * @return The version of the expansion.
         */
        @Override
        public @NotNull String getVersion() {
            return "1.0.0";
        }


        /**
         * Placeholders:
         * <p>
         * - %rel_patchtest_relationColor%
         *
         * @param one        The first player used for the placeholder.
         * @param two        The second player used for the placeholder.
         * @param identifier The text right after the expansion's name (%expansion_<b>identifier</b>%)
         * @return The formatted String.
         */
        @Override
        public String onPlaceholderRequest(Player one, Player two, String identifier) {
            if (identifier.equalsIgnoreCase("relationColor")) {
                Session session = SessionManager.getInstance().getSession(one);
                Session session2 = SessionManager.getInstance().getSession(two);

                if (session == null || session2 == null) return "";

                Set<UUID> defendingMembers = session.getDefendingParty().getMembers();
                Set<UUID> attackingMembers = session.getAttackingParty().getMembers();


                if (defendingMembers.contains(one.getUniqueId()) && defendingMembers.contains(two.getUniqueId()))
                    return "&a";
                if (attackingMembers.contains(one.getUniqueId()) && attackingMembers.contains(two.getUniqueId()))
                    return "&a";
                if (attackingMembers.contains(one.getUniqueId()) && defendingMembers.contains(two.getUniqueId()))
                    return "&c";
                if (defendingMembers.contains(one.getUniqueId()) && attackingMembers.contains(two.getUniqueId()))
                    return "&c";

                return "";
            }
            return null;
        }
    }

}

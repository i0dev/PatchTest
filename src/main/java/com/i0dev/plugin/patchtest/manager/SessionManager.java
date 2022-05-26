package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.Pair;
import com.i0dev.plugin.patchtest.object.PatchSession;
import com.i0dev.plugin.patchtest.template.AbstractManager;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

/**
 * Patch Session Manager
 *
 * @author Andrew Magnuson
 */
public class SessionManager extends AbstractManager {

    @Getter
    private static final SessionManager instance = new SessionManager();
    @Getter
    private final Set<PatchSession> sessions = new HashSet<>();

    @Override
    public void initialize() {
        setListener(true);
    }

    /**
     * @param index The index of the plot to check for.
     * @return true if the plot location is already taken, false if it is not.
     */
    public boolean isPlotLocationTaken(int index) {
        return sessions.stream().anyMatch(s -> s.getPlot().getPlotIndex() == index);
    }

    /**
     * Remove a patch session from the list.
     *
     * @param uuid The {@link UUID} of the session
     * @return true if this set contained the specified element, false if the session did not exist
     */
    public boolean remove(UUID uuid) {
        PatchSession session = getSession(uuid);
        if (session == null) return false;
        return sessions.remove(session);
    }

    /**
     * Add a patch session to the list.
     *
     * @param session the {@link PatchSession} to add
     * @return true if this set did not already contain the specified element
     */
    public boolean add(PatchSession session) {
        return sessions.add(session);
    }

    /**
     * Will retrieve the PatchSession that player is in.
     *
     * @param player The player to check sessions for membership.
     * @return The {@link PatchSession} that that player is a member in, null if they are not in any.
     */
    public PatchSession getSession(Player player) {
        return sessions.stream()
                .filter(session -> session.getPlayers().stream()
                        .filter(p -> p.getUniqueId().equals(player.getUniqueId()))
                        .findFirst().orElse(null) != null)
                .findFirst().orElse(null);
    }

    /**
     * Get a session from a sessions unique identifier
     *
     * @param uuid the {@link UUID} of the session.
     * @return The session representing the specified uuid, otherwise null if it didn't exist
     */
    public PatchSession getSession(UUID uuid) {
        return sessions.stream().filter(s -> s.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    //
    //          Invite Section
    //

    /**
     * The {@link UUID} in this map is the inviter, while the {@link List}<{@link UUID}> is the list of users the inviter has invited.
     */
    private final Map<UUID, List<UUID>> inviteMap = new HashMap<>();

    /**
     * Get the list of users the inviter has invited.
     *
     * @param inviter The inviter to get invitees
     * @return the list of players that the specified inviter has invited, otherwise null if there are none.
     */
    public List<UUID> getInvitees(Player inviter) {
        return inviteMap.getOrDefault(inviter.getUniqueId(), null);
    }

    /**
     * Check if a player has invited another player.
     *
     * @param inviter The inviter to check
     * @param invited The invitee to check
     * @return true if the inviter has invited that player to their session, false if they have not
     */
    public boolean isInvited(Player inviter, Player invited) {
        List<UUID> invitees = getInvitees(inviter);
        if (invitees == null) return false;
        return invitees.contains(invited.getUniqueId());
    }

    /**
     * Create a new invite and add it to the invite map.
     *
     * @param inviter The inviter
     * @param invited The invitee
     */
    public void newInvite(Player inviter, Player invited) {
        List<UUID> invitees = getInvitees(inviter);
        if (invitees == null) {
            invitees = new ArrayList<>();
            invitees.add(invited.getUniqueId());
            inviteMap.put(inviter.getUniqueId(), invitees);
        } else {
            invitees.add(invited.getUniqueId());
        }
    }

    /**
     * Remove an invitation from the invite map
     *
     * @param inviter The inviter
     * @param invited The invitee
     */
    public void removeInvite(Player inviter, Player invited) {
        List<UUID> invitees = getInvitees(inviter);
        if (invitees == null) return;
        invitees.remove(invited.getUniqueId());
        if (invitees.isEmpty()) inviteMap.remove(inviter.getUniqueId());
    }

    //
    //     Event Handling
    //

    /**
     * Will check if a piece of {@link org.bukkit.entity.TNTPrimed} has exploded inside the base cuboid in a session.
     * If it has exploded in a cuboid, it will end the session and count it as a loss.
     */
    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        PatchSession session = sessions.stream().filter(s -> s.getPlot().getBaseCuboid().contains(e.getLocation())).findFirst().orElse(null);
        if (session == null) return;
        session.end();
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        onLeaveExecute(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        onLeaveExecute(e.getPlayer());
    }

    public void onLeaveExecute(Player leftPlayer) {
        PatchSession session = getInstance().getSession(leftPlayer);
        if (session == null) return;
        if (!session.isCreator(leftPlayer)) {
            if (session.getPlayers().size() > 1) {
                Player newCreator = session.getPlayers().get(1);
                session.setCreator(newCreator);
                session.getPlayers().remove(leftPlayer);
                session.getPlayers().forEach(player1 -> MsgUtil.msg(player1, PatchTestPlugin.getMsg("playerLoggedOfflineTransferLeader"),
                        new Pair<>("{newPlayer}", newCreator.getName()),
                        new Pair<>("{player}", leftPlayer.getName())));
                MsgUtil.msg(newCreator, PatchTestPlugin.getMsg("youAreNewLeader"), new Pair<>("{player}", leftPlayer.getName()));
            } else {
                session.stop();
            }
        } else {
            session.getPlayers().remove(leftPlayer);
        }
        session.getRejoinList().add(leftPlayer.getUniqueId());
        leftPlayer.setHealth(0);
        session.getPlayers().forEach(player -> MsgUtil.msg(player, PatchTestPlugin.getMsg("playerLoggedOffline"), new Pair<>("{player}", leftPlayer.getName())));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PatchSession session = sessions.stream().filter(s -> s.getRejoinList().contains(e.getPlayer().getUniqueId())).findFirst().orElse(null);
        if (session == null) return;
        MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("rejoinLogin"));
    }


}

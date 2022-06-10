package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.Party;
import com.i0dev.plugin.patchtest.object.Session;
import com.i0dev.plugin.patchtest.object.SessionType;
import com.i0dev.plugin.patchtest.object.TeamSize;
import com.i0dev.plugin.patchtest.template.AbstractManager;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Patch Session Manager
 *
 * @author Andrew Magnuson
 */
public class SessionManager extends AbstractManager {

    @Getter
    private static final SessionManager instance = new SessionManager();
    @Getter
    private final Set<Session> sessions = new HashSet<>();

    @Override
    public void initialize() {
        setListener(true);
    }

    public Session createSession(Party party, SessionType sessionType) {
        Session session = new Session(party, sessionType);
        sessions.add(session);
        return session;
    }

    public TeamSize getTeamSize(Party party) {
        TeamSize size;
        if (party.getMembers().size() == 1) {
            size = TeamSize.SOLO;
        } else if (party.getMembers().size() == 2) {
            size = TeamSize.DUO;
        } else if (party.getMembers().size() > 2 && party.getMembers().size() <= TeamSize.SQUAD.getSize()) {
            size = TeamSize.SQUAD;
        } else if (party.getMembers().size() > 5 && party.getMembers().size() <= TeamSize.TEAM.getSize()) {
            size = TeamSize.TEAM;
        } else size = TeamSize.UNLIMITED;

        return size;
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
        Session session = getSession(uuid);
        if (session == null) return false;
        return sessions.remove(session);
    }

    /**
     * Add a patch session to the list.
     *
     * @param session the {@link Session} to add
     * @return true if this set did not already contain the specified element
     */
    public boolean add(Session session) {
        return sessions.add(session);
    }

    /**
     * Will retrieve the PatchSession that player is in.
     *
     * @param party The party to check uuids for.
     * @return The {@link Session} that that player is a member in, null if they are not in any.
     */
    public Session getSession(Party party) {
        return sessions.stream()
                .filter(session -> session.getDefendingParty().getUuid().equals(party.getUuid()))
                .findFirst().orElse(null);
    }

    public Session getSession(Player player) {
        return sessions.stream()
                .filter(session -> {
                    if (session.getDefendingParty().getMembers().stream().anyMatch(uuid -> uuid.equals(player.getUniqueId())))
                        return true;
                    if (session.getAttackingParty() != null) {
                        return session.getAttackingParty().getMembers().stream().anyMatch(uuid -> uuid.equals(player.getUniqueId()));
                    }
                    return false;
                })
                .findFirst().orElse(null);
    }

    /**
     * Get a session from a sessions unique identifier
     *
     * @param uuid the {@link UUID} of the session.
     * @return The session representing the specified uuid, otherwise null if it didn't exist
     */
    public Session getSession(UUID uuid) {
        return sessions.stream().filter(s -> s.getUuid().equals(uuid)).findFirst().orElse(null);
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
        Session session = sessions.stream().filter(s -> s.getPlot().getBaseCuboid().contains(e.getLocation())).findFirst().orElse(null);
        if (session == null) return;
        session.stop();
    }

    @EventHandler
    public void onFight(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof Player)) return;
        Session session = sessions.stream().filter(s -> s.getPlot().getAllowedMoveCuboid().contains(e.getDamager().getLocation())).findFirst().orElse(null);
        if (session == null) return;
        if (session.getType() == SessionType.VERSUS) {
            Session ses = SessionManager.getInstance().getSession(((Player) e.getEntity()));
            if (ses.getDefendingParty().getMembers().contains(e.getEntity().getUniqueId()) && ses.getDefendingParty().getMembers().contains(e.getDamager().getUniqueId())) {
                MsgUtil.msg(e.getDamager(), PatchTestPlugin.getMsg("session.cantFightTeammates"));
                e.setCancelled(true);
            }
            if (ses.getAttackingParty().getMembers().contains(e.getEntity().getUniqueId()) && ses.getAttackingParty().getMembers().contains(e.getDamager().getUniqueId())) {
                MsgUtil.msg(e.getDamager(), PatchTestPlugin.getMsg("session.cantFightTeammates"));
                e.setCancelled(true);
            }
            return;
        }
        e.setCancelled(true);
    }


}
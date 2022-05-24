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

public class SessionManager extends AbstractManager {

    @Getter
    private static final SessionManager instance = new SessionManager();
    @Getter
    private final Set<PatchSession> sessions = new HashSet<>();

    @Override
    public void initialize() {
        setListener(true);
    }

    public boolean isPlayerInSession(Player player) {
        for (PatchSession session : sessions) {
            for (Player sessionPlayer : session.getPlayers()) {
                if (sessionPlayer.getUniqueId().equals(player.getUniqueId())) return true;
            }
        }
        return false;
    }

    public boolean isPlotLocationTaken(int index) {
        for (PatchSession session : sessions) {
            if (session.getPlot().getPlotIndex() == index) return true;
        }
        return false;
    }

    public void remove(UUID sessionUUID) {
        PatchSession toRemove = null;
        for (PatchSession session : sessions) {
            if (session.getUuid().equals(sessionUUID))
                toRemove = session;
        }
        if (toRemove == null) return;
        sessions.remove(toRemove);
    }


    public void add(PatchSession session) {
        sessions.add(session);
    }


    public boolean isPlayerCreator(Player player) {
        for (PatchSession session : sessions) {
            if (session.getCreator().getUniqueId().equals(player.getUniqueId())) return true;
        }
        return false;
    }

    public PatchSession getPlayersSession(Player player) {
        for (PatchSession session : sessions) {
            for (Player sessionPlayer : session.getPlayers()) {
                if (sessionPlayer.getUniqueId().equals(player.getUniqueId())) return session;
            }
        }
        return null;
    }

    //              inviter : invited
    private final Map<UUID, List<UUID>> inviteMap = new HashMap<>();

    public List<UUID> getInvitees(Player inviter) {
        return inviteMap.getOrDefault(inviter.getUniqueId(), null);
    }

    public boolean isInvited(Player inviter, Player invited) {
        List<UUID> invitees = getInvitees(inviter);
        if (invitees == null) return false;
        return invitees.contains(invited.getUniqueId());
    }

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

    public void removeInvite(Player inviter, Player invited) {
        List<UUID> invitees = getInvitees(inviter);
        if (invitees == null) return;
        invitees.remove(invited.getUniqueId());
        if (invitees.isEmpty()) inviteMap.remove(inviter.getUniqueId());
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        for (PatchSession session : sessions) {
            if (session.getPlot().getBaseCuboid().contains(e.getLocation())) {
                session.endSessionLose();
            }
        }
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
        if (!getInstance().isPlayerInSession(leftPlayer)) return;
        PatchSession session = getInstance().getPlayersSession(leftPlayer);

        if (session.getCreator().getUniqueId().equals(leftPlayer.getUniqueId())) {
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
        for (PatchSession session : sessions) {
            if (session.getRejoinList().contains(e.getPlayer().getUniqueId())) {
                MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("rejoinLogin"));
                return;
            }
        }
    }


}

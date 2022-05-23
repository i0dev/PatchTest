package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.Pair;
import com.i0dev.plugin.patchtest.object.PatchSession;
import com.i0dev.plugin.patchtest.template.AbstractManager;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import com.i0dev.plugin.patchtest.utility.TimeUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

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

    private void remove(UUID sessionUUID) {
        PatchSession toRemove = null;
        for (PatchSession session : sessions) {
            if (session.getUuid().equals(sessionUUID))
                toRemove = session;
        }
        if (toRemove == null) return;
        sessions.remove(toRemove);
    }

    public void stopSession(PatchSession session) {
        cancelTasks(session);

        killMobs(session);

        remove(session.getUuid());
    }

    public void cancelTasks(PatchSession session) {
        session.getPlayers().forEach(player -> player.setHealth(0));
        if (session.getCountdown() != null)
            session.getCountdown().cancel();
        if (session.getCreate() != null)
            session.getCreate().cancel();
        if (session.getShoot() != null)
            session.getShoot().cancel();
    }

    public void killMobs(PatchSession session) {
        for (Entity entity : Bukkit.getWorld(PatchTestPlugin.getPlugin().cnf().getString("patchWorldName")).getNearbyEntities(session.getPlot().getPlotCuboid().getCenter(), 500, 500, 500)) {
            if (!(entity instanceof LivingEntity))
                continue;
            LivingEntity mob = ((LivingEntity) entity);
            if (session.getPlot().getAllowedMoveCuboid().contains(mob.getLocation()))
                mob.setHealth(0);
        }
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
                endSessionLose(session);
            }
        }
    }


    public void endSessionLose(PatchSession session) {
        session.getPlayers().forEach(player -> {
            player.setHealth(0);
            MsgUtil.msg(player, PatchTestPlugin.getPlugin().msg().getStringList("lostSession"), new Pair<>("{time}", TimeUtil.formatTimePeriod(System.currentTimeMillis() - session.getStartTime())));
        });

        stopSession(session);
    }

}

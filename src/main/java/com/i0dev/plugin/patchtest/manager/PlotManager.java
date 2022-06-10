package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.Cuboid;
import com.i0dev.plugin.patchtest.object.PatchPlot;
import com.i0dev.plugin.patchtest.object.Session;
import com.i0dev.plugin.patchtest.object.SessionType;
import com.i0dev.plugin.patchtest.template.AbstractManager;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlotManager extends AbstractManager {


    @Getter
    private static final PlotManager instance = new PlotManager();

    @Override
    public void initialize() {
        setListener(true);
    }

    public PatchPlot getNextAvailablePlot() {
        int index = -1;

        for (int i = 1; i < 100; i++) {
            if (!SessionManager.getInstance().isPlotLocationTaken(i)) {
                index = i;
                break;
            }
        }
        if (index == -1) return null;

        int blocksBetweenPlots = index == 1 ? 0 : 600;

        int minX = (index * 240) + blocksBetweenPlots;
        int maxX = minX + 239;

        return new PatchPlot(index, new Cuboid(minX, 1, 1, maxX, 255, 224, PatchTestPlugin.getPlugin().cnf().getString("patchWorldName")));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Session session = SessionManager.getInstance().getSession(e.getEntity());
        if (session == null)
            return;


        MsgUtil.msg(e.getEntity(), PatchTestPlugin.getMsg("session.tpBackOnDeath"));
        MsgUtil.msgAll(e.getEntity().getDisplayName() + " has been slain by" + e.getEntity().getKiller().getDisplayName());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
            Session session = SessionManager.getInstance().getSession(e.getPlayer());
            if (session == null) return;
            if (session.isStarted() || session.isInCountdown()) {
                if (!session.getPlot().getAllowedMoveCuboid().contains(e.getTo())) {
                    e.getPlayer().teleport(session.getPlot().getTpLocation());
                    MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("cantLeavePlotBoundaries"));
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Session session = SessionManager.getInstance().getSession(e.getPlayer());
        if (session == null) return;
        if (session.getType() == SessionType.VERSUS && session.getAttackingParty().getMembers().contains(e.getPlayer().getUniqueId())) {
            MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("session.cantDoThatWhileRaiding"));
            e.setCancelled(true);
        }

        if (!session.isStarted()) {
            MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("session.waitForSessionToStart"));
            e.setCancelled(true);

        } else if (!session.getPlot().getAllowedMoveCuboid().contains(e.getBlock().getLocation())) {
            MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("cantPlaceBlocksOutsidePlot"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFight(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Session session = SessionManager.getInstance().getSession(((Player) e.getDamager()));
        if (session == null) return;
        if (!session.isStarted()) {
            MsgUtil.msg(e.getDamager(), PatchTestPlugin.getMsg("session.waitForSessionToStart"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Session session = SessionManager.getInstance().getSession(e.getPlayer());
        if (session == null) return;

        if (session.getType() == SessionType.VERSUS && session.getAttackingParty().getMembers().contains(e.getPlayer().getUniqueId())) {
            MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("session.cantDoThatWhileRaiding"));
            e.setCancelled(true);
        }

        if (!session.isStarted()) {
            MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("session.waitForSessionToStart"));
            e.setCancelled(true);
        } else if (!session.getPlot().getAllowedMoveCuboid().contains(e.getBlock().getLocation())) {
            MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("cantPlaceBlocksOutsidePlot"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent e) {
        if (!e.getEntity().getLocation().getWorld().getName().equals(PatchTestPlugin.getWorldName())) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        e.setCancelled(true);
    }
}

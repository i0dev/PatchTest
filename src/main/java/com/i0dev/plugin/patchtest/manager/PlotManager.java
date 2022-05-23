package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.Cuboid;
import com.i0dev.plugin.patchtest.object.PatchPlot;
import com.i0dev.plugin.patchtest.object.PatchSession;
import com.i0dev.plugin.patchtest.template.AbstractManager;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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

        return new PatchPlot(index, new Cuboid(minX, 1, 1, maxX, 255, 320, PatchTestPlugin.getPlugin().cnf().getString("patchWorldName")));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
            if (!SessionManager.getInstance().isPlayerInSession(e.getPlayer())) return;
            PatchSession session = SessionManager.getInstance().getPlayersSession(e.getPlayer());
            if (!session.isStarted()) return;
            if (!session.getPlot().getAllowedMoveCuboid().contains(e.getTo())) {
                e.getPlayer().teleport(session.getPlot().getTpLocation());
                MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("cantLeavePlotBoundaries"));
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getPlayer().hasPermission(PatchTestPlugin.PERMISSION_PREFIX + ".admin")) return;
        if (SessionManager.getInstance().isPlayerInSession(e.getPlayer())) {
            PatchSession session = SessionManager.getInstance().getPlayersSession(e.getPlayer());
            if (!session.isStarted()) {
                MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("waitForSessionToStartToPlaceBlocks"));
                e.setCancelled(true);
                return;
            }
            if (!session.getPlot().getAllowedMoveCuboid().contains(e.getBlock().getLocation())) {
                MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("cantPlaceBlocksOutsidePlot"));
                e.setCancelled(true);
            }
        } else {
            MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("cantPlaceBlocksOutsidePlot"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().hasPermission(PatchTestPlugin.PERMISSION_PREFIX + ".admin")) return;
        if (SessionManager.getInstance().isPlayerInSession(e.getPlayer())) {
            PatchSession session = SessionManager.getInstance().getPlayersSession(e.getPlayer());
            if (!session.isStarted()) {
                MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("waitForSessionToStartToBreakBlocks"));
                e.setCancelled(true);
                return;
            }
            if (!session.getPlot().getAllowedMoveCuboid().contains(e.getBlock().getLocation())) {
                MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("cantPlaceBlocksOutsidePlot"));
                e.setCancelled(true);
            }
        } else {
            MsgUtil.msg(e.getPlayer(), PatchTestPlugin.getMsg("cantPlaceBlocksOutsidePlot"));
            e.setCancelled(true);
        }
    }

//    @EventHandler
//    public void onDeath(EntityDeathEvent e) {
//        if (!(e.getEntity() instanceof Player)) return;
//        if (!e.getEntity().getLocation().getWorld().getName().equals(PatchTestPlugin.getWorldName())) return;
//        Player player = (Player) e.getEntity();
//        if (SessionManager.getInstance().isPlayerInSession(player)) {
//            MsgUtil.msg(e.getEntity(), PatchTestPlugin.getMsg("goBackToSessionOnDeath"));
//        }
//    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent e) {
        if (!e.getEntity().getLocation().getWorld().getName().equals(PatchTestPlugin.getWorldName())) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        e.setCancelled(true);
    }
}

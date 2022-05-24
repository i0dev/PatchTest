package com.i0dev.plugin.patchtest.object;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.manager.CannonManager;
import com.i0dev.plugin.patchtest.manager.PlotManager;
import com.i0dev.plugin.patchtest.manager.SessionManager;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import com.i0dev.plugin.patchtest.utility.TimeUtil;
import com.i0dev.plugin.patchtest.utility.TitleUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@ToString
public class
PatchSession {

    private UUID uuid;
    private Player creator;
    private List<Player> players;
    private boolean isCreating;
    private boolean started;
    private boolean inviteAllowed;
    private int adjustHowOften;
    private PatchPlot plot;
    private BukkitTask shoot;
    private BukkitTask countdown;
    private BukkitTask create;
    private long startTime;
    private boolean inCountdown;

    private List<UUID> rejoinList;

    private boolean firstShot;


    public PatchSession(Player creator) {
        this.players = new ArrayList<>();
        this.players.add(creator);
        this.creator = creator;
        isCreating = false;
        plot = PlotManager.getInstance().getNextAvailablePlot();
        lastShotTime = 0;
        lastAdjustTime = 0;
        inviteAllowed = true;
        started = false;
        lastCountdownMessageSent = 0;
        currentShootingLocation = plot.getDefaultShootLocation();
        uuid = UUID.randomUUID();
        firstShot = true;
        newAdjustTime();
    }

    public void newAdjustTime() {
        int min = PatchTestPlugin.getPlugin().cnf().getInt("adjustTimeRandomLowerBound");
        int max = PatchTestPlugin.getPlugin().cnf().getInt("adjustTimeRandomHigherBound");
        this.adjustHowOften = ThreadLocalRandom.current().nextInt((max - min) + 1) + min;
    }

    private Location currentShootingLocation;
    private long lastShotTime;
    private long lastAdjustTime;
    public Runnable taskShoot = () -> {
        if (System.currentTimeMillis() >= lastShotTime + (PatchTestPlugin.getPlugin().cnf().getInt("cannonSpeed") * 1000L)) {
            if (System.currentTimeMillis() >= lastAdjustTime + (adjustHowOften * 1000L)) {
                currentShootingLocation = getMostEfficientLocation();
                lastAdjustTime = System.currentTimeMillis();
                newAdjustTime();
                System.out.println("Adjusted to: " + currentShootingLocation);
            }
            CannonManager.getInstance().shootCannon(currentShootingLocation);
            lastShotTime = System.currentTimeMillis();
            this.firstShot = false;
        }
    };
    private long startedCountdownTime;
    private long lastCountdownMessageSent;
    public Runnable taskCountdown = () -> {
        if (started) return;
        inCountdown = true;
        int countdownLengthSeconds = PatchTestPlugin.getPlugin().cnf().getInt("countdownLengthSeconds");
        if (System.currentTimeMillis() >= lastCountdownMessageSent * 1000L) {
            SimpleConfig cnf = PatchTestPlugin.getPlugin().cnf();
            if (System.currentTimeMillis() > (countdownLengthSeconds * 1000L) + startedCountdownTime) {
                players.forEach(player -> {
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.F));
                    TitleUtil.sendTitle(player, 3, 15, 3, cnf.getString("countdownGoTitle"), cnf.getString("countdownGoSubtitle"));
                });
                shoot = Bukkit.getScheduler().runTaskTimer(PatchTestPlugin.getPlugin(), taskShoot, 20L, 20L);
                started = true;
                startTime = System.currentTimeMillis();
                this.lastAdjustTime = System.currentTimeMillis();
                inCountdown = false;
                Bukkit.getScheduler().runTask(PatchTestPlugin.getPlugin(), () -> players.forEach(player -> PatchTestPlugin.getPlugin().getServer().dispatchCommand(Bukkit.getConsoleSender(), "kit " + PatchTestPlugin.getPlugin().cnf().get("patchKitName") + " " + player.getName())));
                countdown.cancel();
            } else {
                long secondsRemaining = (startedCountdownTime + (countdownLengthSeconds * 1000L) - System.currentTimeMillis()) / 1000L;
                players.forEach(player -> {
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.F));
                    TitleUtil.sendTitle(player, 3, 15, 3, cnf.getString("countdownTitle").replace("{sec}", secondsRemaining + ""), cnf.getString("countdownSubtitle"));
                });
            }
        }
    };
    public Runnable taskCreate = () -> {
        isCreating = true;
        plot.pasteSchematic();
        isCreating = false;
        Bukkit.getScheduler().runTask(PatchTestPlugin.getPlugin(), () -> players.forEach(player -> player.teleport(plot.getTpLocation())));
        startedCountdownTime = System.currentTimeMillis();
        countdown = Bukkit.getScheduler().runTaskTimerAsynchronously(PatchTestPlugin.getPlugin(), taskCountdown, 0L, 20L);
    };

    public void start() {
        inviteAllowed = false;
        create = Bukkit.getScheduler().runTaskAsynchronously(PatchTestPlugin.getPlugin(), taskCreate);
    }

    public Location getMostEfficientLocation() {
        if (firstShot) return plot.getDefaultShootLocation();
        List<Location> frontWallBlocks = new ArrayList<>();
        Cuboid walls = getPlot().getWallsCuboid();
        for (int y = 150; y < walls.getYMax(); y++) {
            for (int x = (int) walls.getXMin(); x < walls.getXMax(); x++) {
                frontWallBlocks.add(new Location(Bukkit.getWorld(PatchTestPlugin.getPlugin().cnf().getString("patchWorldName")), x, y, getPlot().getWallsCuboid().getZMax()));
            }
        }
        Collections.shuffle(frontWallBlocks);

        int longestHole = 0;
        Location farthestLocation = plot.getDefaultShootLocation();
        for (Location frontWallBlock : frontWallBlocks) {
            int count = 0;
            Location location = frontWallBlock;
            Material blockType = location.getBlock().getType();
            while ((blockType == Material.AIR || blockType == Material.WATER || blockType == Material.STATIONARY_WATER) && location.getZ() > plot.getWallsCuboid().getZMin()) {
                count++;
                location = location.subtract(0, 0, 1);
                blockType = location.getBlock().getType();
            }
            if (count > longestHole) {
                longestHole = count;
                farthestLocation = frontWallBlock;
            }
        }
        farthestLocation.setZ(plot.getZShootCoordinate());
        return farthestLocation;
    }

    public boolean containsPlayer(Player player) {
        for (Player p : players) {
            if (p.getUniqueId().equals(player.getUniqueId())) return true;
        }
        return false;
    }

    public void endSessionLose() {
        getPlayers().forEach(player -> {
            player.setHealth(0);
            MsgUtil.msg(player, PatchTestPlugin.getPlugin().msg().getStringList("lostSession"), new Pair<>("{time}", TimeUtil.formatTimePeriod(System.currentTimeMillis() - getStartTime())));
        });

        stop();
    }


    public void cancelTasks() {
        getPlayers().forEach(player -> player.setHealth(0));
        if (getCountdown() != null)
            getCountdown().cancel();
        if (getCreate() != null)
            getCreate().cancel();
        if (getShoot() != null)
            getShoot().cancel();
    }

    public void stop() {
        cancelTasks();
        killMobs();
        SessionManager.getInstance().remove(getUuid());
    }

    public void killMobs() {
        for (Entity entity : Bukkit.getWorld(PatchTestPlugin.getPlugin().cnf().getString("patchWorldName")).getNearbyEntities(getPlot().getPlotCuboid().getCenter(), 500, 500, 500)) {
            if (!(entity instanceof LivingEntity))
                continue;
            LivingEntity mob = ((LivingEntity) entity);
            if (getPlot().getAllowedMoveCuboid().contains(mob.getLocation()))
                mob.setHealth(0);
        }
    }

}

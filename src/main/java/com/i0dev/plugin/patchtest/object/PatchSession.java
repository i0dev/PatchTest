package com.i0dev.plugin.patchtest.object;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.manager.CannonManager;
import com.i0dev.plugin.patchtest.manager.PlotManager;
import com.i0dev.plugin.patchtest.manager.SessionManager;
import com.i0dev.plugin.patchtest.manager.StorageManager;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import com.i0dev.plugin.patchtest.utility.TimeUtil;
import com.i0dev.plugin.patchtest.utility.TitleUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class PatchSession {
    private UUID uuid;
    private Player creator;
    private List<Player> players;
    private boolean isCreating;
    private boolean started;
    private boolean inviteAllowed;
    private PatchPlot plot;
    private BukkitTask shoot;
    private BukkitTask countdown;
    private BukkitTask create;
    private long startTime;
    private boolean inCountdown;

    private List<UUID> rejoinList;
    private Adjuster adjuster;
    private long lastShotTime;

    private SessionSettings settings;

    public PatchSession(Player creator, SessionSettings settings) {
        this.players = new ArrayList<>();
        this.settings = settings;
        this.players.add(creator);
        this.creator = creator;
        isCreating = false;
        plot = PlotManager.getInstance().getNextAvailablePlot();
        inviteAllowed = true;
        started = false;
        lastCountdownMessageSent = 0;
        lastShotTime = 0;
        uuid = UUID.randomUUID();
        adjuster = new Adjuster(plot, this);
    }

    public Runnable taskShoot = () -> {
        if (System.currentTimeMillis() >= lastShotTime + (settings.getCannonSpeed() * 1000L)) {
            Bukkit.getScheduler().runTask(PatchTestPlugin.getPlugin(), () -> CannonManager.getInstance().shoot(CannonManager.getCenter(adjuster.getShotLocation()), BlockFace.NORTH, settings.getCannonType()));
            lastShotTime = System.currentTimeMillis();
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
                inCountdown = false;
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
        Bukkit.getScheduler().runTask(PatchTestPlugin.getPlugin(), () -> players.forEach(player -> PatchTestPlugin.getPlugin().getServer().dispatchCommand(Bukkit.getConsoleSender(), "kit " + PatchTestPlugin.getPlugin().cnf().get("patchKitName") + " " + player.getName())));
    };

    public void start() {
        inviteAllowed = false;
        create = Bukkit.getScheduler().runTaskAsynchronously(PatchTestPlugin.getPlugin(), taskCreate);

        if (settings.isRanked()) {
            TeamSize size;
            SimpleConfig cnf = PatchTestPlugin.getPlugin().cnf();
            if (players.size() == 1) {
                size = TeamSize.SOLO;
                settings.setCannonSpeed(cnf.getInt("rankedSettings.teamSizeCannonSpeedMap.SOLO"));
            } else if (players.size() == 2) {
                size = TeamSize.DUO;
                settings.setCannonSpeed(cnf.getInt("rankedSettings.teamSizeCannonSpeedMap.DUO"));
            } else if (players.size() > 2 && players.size() <= TeamSize.SQUAD.getSize()) {
                size = TeamSize.SQUAD;
                settings.setCannonSpeed(cnf.getInt("rankedSettings.teamSizeCannonSpeedMap.SQUAD"));
            } else if (players.size() > 5 && players.size() <= TeamSize.TEAM.getSize()) {
                size = TeamSize.TEAM;
                settings.setCannonSpeed(cnf.getInt("rankedSettings.teamSizeCannonSpeedMap.TEAM"));
            } else size = TeamSize.UNLIMITED;
            this.settings.setTeamSize(size);
        }

    }

    public boolean isCreator(Player player) {
        return getCreator().getUniqueId().equals(player.getUniqueId());
    }

    public boolean containsPlayer(Player player) {
        for (Player p : players) {
            if (p.getUniqueId().equals(player.getUniqueId())) return true;
        }
        return false;
    }

    public void end() {
        if (settings.isRanked()) {
            String playersRaw = getPlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()).toString();

            String size = settings.getTeamSize().niceName();
            String players = playersRaw.substring(1, playersRaw.length() - 1);
            String time = TimeUtil.formatTimePeriod(System.currentTimeMillis() - getStartTime());

            getPlayers().forEach(player -> {
                player.setHealth(0);
                MsgUtil.msg(player, PatchTestPlugin.getPlugin().msg().getStringList("lostSession"),
                        new Pair<>("{size}", size),
                        new Pair<>("{players}", players),
                        new Pair<>("{time}", time));
            });


            ScoreEntry entry = new ScoreEntry(getCreator().getUniqueId(), getUuid(),
                    this.players.stream()
                            .map(Entity::getUniqueId)
                            .collect(Collectors.toSet()),
                    settings.getTeamSize(),
                    System.currentTimeMillis() - getStartTime(),
                    System.currentTimeMillis());

            StorageManager.getInstance().addEntry(entry);
        }

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

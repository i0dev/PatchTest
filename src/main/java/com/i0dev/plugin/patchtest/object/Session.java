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
import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class Session {

    // main
    private UUID uuid;
    private Party defendingParty;
    private Party attackingParty;
    private SessionType type;
    private PatchPlot plot;
    private Adjuster adjuster;

    // settings
    private TeamSize teamSize;
    private int cannonSpeed;
    private CannonType cannonType;


    // Information
    private long startTimeMillis;
    private long lastShotTime;


    // States
    private boolean started;
    private boolean inCountdown;

    private boolean paused;

    private long startedCountdownTime;
    private long lastCountdownMessageSent;

    // Tasks
    private BukkitTask shoot;
    private BukkitTask countdown;

    // challenge
    Set<UUID> pendingChallenges;


    public Session(Party party, SessionType type) {
        this.uuid = UUID.randomUUID();
        this.defendingParty = party;
        this.type = type;
        this.teamSize = TeamSize.SOLO;
        this.cannonSpeed = PatchTestPlugin.getPlugin().cnf().getInt("defaultCannonSpeed");
        this.cannonType = type == SessionType.RANKED ? CannonType.valueOf(PatchTestPlugin.getPlugin().cnf().getString("rankedSettings.cannonType")) : CannonType.NUKE;

        this.attackingParty = null;
        this.startTimeMillis = 0;
        this.lastCountdownMessageSent = 0;
        this.startedCountdownTime = 0;
        this.lastShotTime = 0;
        this.started = false;
        this.inCountdown = false;
        this.paused = false;
        this.pendingChallenges = new HashSet<>();

        this.plot = PlotManager.getInstance().getNextAvailablePlot();
        this.adjuster = new Adjuster(this);
    }

    public Set<Player> getSessionPlayers() {
        Set<Player> ret = new HashSet<>();

        defendingParty.getMembers().forEach(uuid1 -> {
            Player player = Bukkit.getPlayer(uuid1);
            if (player == null) return;
            ret.add(player);
        });

        if (attackingParty != null) {
            attackingParty.getMembers().forEach(uuid1 -> {
                Player player = Bukkit.getPlayer(uuid1);
                if (player == null) return;
                ret.add(player);
            });
        }

        return ret;
    }

    public Runnable taskShoot = () -> {
        if (System.currentTimeMillis() >= lastShotTime + (cannonSpeed * 1000L)) {
            Bukkit.getScheduler().runTask(PatchTestPlugin.getPlugin(), () -> CannonManager.getInstance().shoot(CannonManager.getCenter(adjuster.getShotLocation()), BlockFace.NORTH, cannonType));
            lastShotTime = System.currentTimeMillis();
        }
    };
    public Runnable taskCountdown = () -> {
        if (started) {
            this.countdown.cancel();
            return;
        }
        inCountdown = true;
        int countdownLengthSeconds = PatchTestPlugin.getPlugin().cnf().getInt("countdownLengthSeconds");
        if (System.currentTimeMillis() >= lastCountdownMessageSent * 1000L) {
            SimpleConfig cnf = PatchTestPlugin.getPlugin().cnf();
            if (System.currentTimeMillis() > (countdownLengthSeconds * 1000L) + startedCountdownTime) {
                getSessionPlayers().forEach(player -> {
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.sharp(1, Note.Tone.F));
                    TitleUtil.sendTitle(player, 3, 15, 3, cnf.getString("countdownGoTitle"), cnf.getString("countdownGoSubTitle"));
                });
                shoot = Bukkit.getScheduler().runTaskTimer(PatchTestPlugin.getPlugin(), taskShoot, 20L, 20L);
                this.started = true;
                this.startTimeMillis = System.currentTimeMillis();
                this.inCountdown = false;
            } else {
                long secondsRemaining = (startedCountdownTime + (countdownLengthSeconds * 1000L) - System.currentTimeMillis()) / 1000L;
                if (type == SessionType.VERSUS)
                    getAttackingParty().getMembers().forEach(uuid1 -> {
                        Player player = Bukkit.getPlayer(uuid1);
                        if (player == null) return;
                        player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.F));
                        TitleUtil.sendTitle(player, 3, 15, 3, cnf.getString("countdownTitle").replace("{sec}", secondsRemaining + ""), cnf.getString("countdownSubtitleAttack"));
                    });

                getDefendingParty().getMembers().forEach(uuid1 -> {
                    Player player = Bukkit.getPlayer(uuid1);
                    if (player == null) return;
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.F));
                    TitleUtil.sendTitle(player, 3, 15, 3, cnf.getString("countdownTitle").replace("{sec}", secondsRemaining + ""), cnf.getString("countdownSubtitleDefend"));
                });
            }
        }
    };

    public void start() {
        Bukkit.getScheduler().runTaskAsynchronously(PatchTestPlugin.getPlugin(), () -> {
            this.plot.pasteSchematic();
            if (type == SessionType.VERSUS) {
                getSessionPlayers().forEach(player -> {
                    MsgUtil.msg(player, PatchTestPlugin.getMsg("session.versusSessionStarting"));
                    player.teleport(plot.getTpLocation());
                    Bukkit.getScheduler().runTask(PatchTestPlugin.getPlugin(), () -> PatchTestPlugin.getPlugin().getServer().dispatchCommand(Bukkit.getConsoleSender(), "kit " + PatchTestPlugin.getPlugin().cnf().get("patchKitName") + " " + player.getName()));
                });
            } else if (type == SessionType.RANKED) {
                this.teamSize = SessionManager.getInstance().getTeamSize(defendingParty);
                this.cannonSpeed = PatchTestPlugin.getPlugin().cnf().getInt("rankedSettings.teamSizeCannonSpeedMap." + teamSize.name());

                defendingParty.getMembers().forEach(uuid1 -> {
                    Player player = Bukkit.getPlayer(uuid1);
                    if (player == null) return;
                    MsgUtil.msg(player, PatchTestPlugin.getMsg("session.rankedSessionStarting"));
                    player.teleport(plot.getTpLocation());
                    Bukkit.getScheduler().runTask(PatchTestPlugin.getPlugin(), () -> PatchTestPlugin.getPlugin().getServer().dispatchCommand(Bukkit.getConsoleSender(), "kit " + PatchTestPlugin.getPlugin().cnf().get("patchKitName") + " " + player.getName()));
                });
            } else {
                defendingParty.getMembers().forEach(uuid1 -> {
                    Player player = Bukkit.getPlayer(uuid1);
                    if (player == null) return;
                    MsgUtil.msg(player, PatchTestPlugin.getMsg("session.sandboxSessionStarting"));
                    player.teleport(plot.getTpLocation());
                    Bukkit.getScheduler().runTask(PatchTestPlugin.getPlugin(), () -> PatchTestPlugin.getPlugin().getServer().dispatchCommand(Bukkit.getConsoleSender(), "kit " + PatchTestPlugin.getPlugin().cnf().get("patchKitName") + " " + player.getName()));
                });
            }
            this.startedCountdownTime = System.currentTimeMillis();
            this.countdown = Bukkit.getScheduler().runTaskTimerAsynchronously(PatchTestPlugin.getPlugin(), taskCountdown, 0L, 20L);
        });
    }

    public void stop() {
        SessionManager.getInstance().remove(this.uuid);
        String time = TimeUtil.formatTimePeriod(System.currentTimeMillis() - getStartTimeMillis());

        if (type == SessionType.RANKED) {
            String playersRaw = getSessionPlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()).toString();
            String size = getTeamSize().niceName();
            String players = playersRaw.substring(1, playersRaw.length() - 1);

            getSessionPlayers().forEach(player -> {
                MsgUtil.msg(player, PatchTestPlugin.getPlugin().msg().getStringList("rankedSessionOver"),
                        new Pair<>("{size}", size),
                        new Pair<>("{players}", players),
                        new Pair<>("{time}", time));
            });

            ScoreEntry entry = new ScoreEntry(this.uuid,
                    this.defendingParty.getMembers(),
                    this.teamSize,
                    System.currentTimeMillis() - startTimeMillis,
                    System.currentTimeMillis());

            StorageManager.getInstance().addEntry(entry);
        } else if (type == SessionType.VERSUS) {

            String playersRaw = getDefendingParty().getMembers().stream().map(uuid1 -> Bukkit.getPlayer(uuid1).getName()).collect(Collectors.toList()).toString();
            String players = playersRaw.substring(1, playersRaw.length() - 1);

            String enemyPlayersRaw = getAttackingParty().getMembers().stream().map(uuid1 -> Bukkit.getPlayer(uuid1).getName()).collect(Collectors.toList()).toString();
            String enemyPlayers = enemyPlayersRaw.substring(1, enemyPlayersRaw.length() - 1);


            this.defendingParty.getMembers().forEach(uuid1 -> {
                Player player = Bukkit.getPlayer(uuid1);
                if (player == null) return;
                MsgUtil.msg(player, PatchTestPlugin.getPlugin().msg().getStringList("versusSessionOverLost"),
                        new Pair<>("{yourPlayers}", players),
                        new Pair<>("{enemyPlayers}", enemyPlayers),
                        new Pair<>("{time}", time));
            });

            this.attackingParty.getMembers().forEach(uuid1 -> {
                Player player = Bukkit.getPlayer(uuid1);
                if (player == null) return;
                MsgUtil.msg(player, PatchTestPlugin.getPlugin().msg().getStringList("versusSessionOverWon"),
                        new Pair<>("{yourPlayers}", enemyPlayers),
                        new Pair<>("{enemyPlayers}", players),
                        new Pair<>("{time}", time));
            });


        }

        getSessionPlayers().forEach(player -> PatchTestPlugin.spawnPlayer(player.getUniqueId()));

        killMobs();

        if (getCountdown() != null) getCountdown().cancel();
        if (getShoot() != null) getShoot().cancel();
    }

    public void killMobs() {
        for (Entity entity : Bukkit.getWorld(PatchTestPlugin.getPlugin().cnf().getString("patchWorldName")).getNearbyEntities(getPlot().getPlotCuboid().getCenter(), 500, 500, 500)) {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity instanceof Player) continue;
            LivingEntity mob = ((LivingEntity) entity);
            if (getPlot().getAllowedMoveCuboid().contains(mob.getLocation())) mob.setHealth(0);
        }
    }

}

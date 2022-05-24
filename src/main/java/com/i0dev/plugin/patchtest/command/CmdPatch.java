package com.i0dev.plugin.patchtest.command;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.manager.PlotManager;
import com.i0dev.plugin.patchtest.manager.SessionManager;
import com.i0dev.plugin.patchtest.object.Pair;
import com.i0dev.plugin.patchtest.object.PatchSession;
import com.i0dev.plugin.patchtest.template.AbstractCommand;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CmdPatch extends AbstractCommand {

    @Getter
    public static final CmdPatch instance = new CmdPatch();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            help(sender, args);
        } else {
            switch (args[0].toLowerCase()) {
                case "reload":
                    reload(sender, args);
                    break;
                case "version":
                case "ver":
                    version(sender, args);
                    break;
                case "start":
                    start(sender, args);
                    break;
                case "create":
                    create(sender, args);
                    break;
                case "rejoin":
                    rejoin(sender, args);
                    break;
                case "invite":
                    invite(sender, args);
                    break;
                case "remove":
                    remove(sender, args);
                    break;
                case "debug":
                    debug(sender, args);
                    break;
                case "join":
                    join(sender, args);
                    break;
                case "leave":
                    leave(sender, args);
                    break;
                case "tp":
                    tp(sender, args);
                    break;
                case "help":
                default:
                    help(sender, args);
            }
        }
    }

    private void debug(CommandSender sender, String[] args) {
        System.out.println("session:" + SessionManager.getInstance().getSession(((Player) sender)));
        System.out.println("plot:" + SessionManager.getInstance().getSession(((Player) sender)).getPlot());
    }

    private void rejoin(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "rejoin")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        Player player = (Player) sender;
        for (PatchSession session : SessionManager.getInstance().getSessions()) {
            if (session.getRejoinList().contains(player.getUniqueId())) {
                session.getRejoinList().remove(player.getUniqueId());
                player.teleport(session.getPlot().getTpLocation());
                session.getPlayers().add(player);
                player.sendMessage("rejoined sesh");
                // send to all that rejoin
                return;
            }
        }
    }


    private void tp(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "tp")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        Player player = (Player) sender;
        PatchSession session = SessionManager.getInstance().getSession(player);

        if (session == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("notInSession"));
            return;
        }
        if (!session.isStarted()) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantTeleportUntilStarted"));
            return;
        }

        player.teleport(session.getPlot().getTpLocation());
        MsgUtil.msg(sender, PatchTestPlugin.getMsg("teleportedToBase"));
    }

    private void leave(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "leave")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        Player player = (Player) sender;
        PatchSession session = SessionManager.getInstance().getSession(player);

        if (session == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("notInSession"));
            return;
        }

        if (session.getCreator().getUniqueId().equals(player.getUniqueId())) {
            if (session.getPlayers().size() > 1) {
                Player newCreator = session.getPlayers().get(1);
                session.setCreator(newCreator);
                session.getPlayers().remove(player);
                MsgUtil.msg(sender, PatchTestPlugin.getMsg("leftAndTransferred"), new Pair<>("{player}", newCreator.getName()));
                session.getPlayers().forEach(player1 -> MsgUtil.msg(player1, PatchTestPlugin.getMsg("leaderHasLeft"),
                        new Pair<>("{newPlayer}", newCreator.getName()),
                        new Pair<>("{player}", player.getName())));
                MsgUtil.msg(newCreator, PatchTestPlugin.getMsg("youAreNewLeader"), new Pair<>("{player}", player.getName()));
                player.setHealth(0);
            } else {
                session.stop();
                MsgUtil.msg(sender, PatchTestPlugin.getMsg("leftCurrentSessionAndDelete"));
            }


        } else {
            session.getPlayers().remove(player);
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("leftSession"), new Pair<>("{player}", session.getCreator().getName()));
            session.getPlayers().forEach(player1 -> MsgUtil.msg(player1, PatchTestPlugin.getMsg("leftSession"), new Pair<>("{player}", player.getName())));
            player.setHealth(0);
        }

    }

    private void start(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "start")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        Player player = (Player) sender;
        PatchSession session = SessionManager.getInstance().getSession(player);

        if (session == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("notInSession"));
            return;
        }
        if (!session.getCreator().getUniqueId().equals(player.getUniqueId())) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("onlyCreatorCanStart"));
            return;
        }

        session.start();
        MsgUtil.msg(sender, PatchTestPlugin.getMsg("startedSession"));

        Bukkit.getScheduler().runTaskLaterAsynchronously(PatchTestPlugin.getPlugin(), () -> session.getPlayers().forEach(player1 -> MsgUtil.msg(player1, plugin.msg().getStringList("sessionStartingSoon"))), 40L);
    }

    private void create(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "create")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }

        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        Player player = (Player) sender;
        PatchSession session = new PatchSession(player);

        if (session == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("alreadyInSession"));
            return;
        }

        if (PlotManager.getInstance().getNextAvailablePlot() == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPlotsAvailable"));
            return;
        }

        SessionManager.getInstance().add(session);
        MsgUtil.msg(sender, PatchTestPlugin.getPlugin().msg().getStringList("newPatchSession"));
    }

    private void join(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "join")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        if (args.length != 2) {
            MsgUtil.msg(sender, "&7Usage: &c/patch join <player>");
            return;
        }
        Player creator = MsgUtil.getPlayer(args[1]);
        Player player = (Player) sender;

        if (creator == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantFindPlayer"), new Pair<>("{player}", args[1]));
            return;
        }

        if (!SessionManager.getInstance().isInvited(creator, player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noInvite"), new Pair<>("{player}", creator.getName()));
            return;
        }
        PatchSession session = SessionManager.getInstance().getSession(creator);

        if (!session.isInviteAllowed()) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("sessionAlreadyStarted"));
            return;
        }

        MsgUtil.msg(sender, PatchTestPlugin.getMsg("joinedSession"), new Pair<>("{player}", creator.getName()));
        session.getPlayers().forEach(player1 -> MsgUtil.msg(player1, PatchTestPlugin.getMsg("playerJoinedYourSession"), new Pair<>("{player}", player.getName())));
        session.getPlayers().add(player);
        SessionManager.getInstance().removeInvite(creator, player);
    }

    private void invite(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "invite")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        if (args.length != 2) {
            MsgUtil.msg(sender, "&7Usage: &c/patch invite <player>");
            return;
        }
        Player toAdd = MsgUtil.getPlayer(args[1]);
        if (toAdd == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantFindPlayer"), new Pair<>("{player}", args[1]));
            return;
        }

        Player player = (Player) sender;
        PatchSession session = SessionManager.getInstance().getSession(player);
        if (session == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("notInSession"));
            return;
        }

        if (!session.getCreator().getUniqueId().equals(player.getUniqueId())) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("onlyCreatorCanInvite"));
            return;
        }


        if (session.containsPlayer(toAdd)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("playerAlreadyInSession"), new Pair<>("{player}", toAdd.getName()));
            return;
        }

        if (SessionManager.getInstance().getSession(toAdd) != null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("inAnotherSession"), new Pair<>("{player}", toAdd.getName()));
            return;
        }

        if (SessionManager.getInstance().isInvited(player, toAdd)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("alreadySentInvite"), new Pair<>("{player}", toAdd.getName()));
            return;
        }

        if (!session.isInviteAllowed()) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("alreadyStartedSessionCantInvite"));
            return;
        }


        SessionManager.getInstance().newInvite(player, toAdd);
        MsgUtil.msg(toAdd, PatchTestPlugin.getMsg("beenInvited"), new Pair<>("{player}", player.getName()));
        MsgUtil.msg(sender, PatchTestPlugin.getMsg("youHaveInvited"), new Pair<>("{player}", toAdd.getName()));
    }

    private void remove(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "remove")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        if (args.length != 2) {
            MsgUtil.msg(sender, "&7Usage: &c/patch remove <player>");
            return;
        }
        Player toRemove = MsgUtil.getPlayer(args[1]);
        if (toRemove == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantFindPlayer"), new Pair<>("{player}", args[1]));
            return;
        }

        Player player = (Player) sender;
        PatchSession session = SessionManager.getInstance().getSession(player);
        if (session == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("notInSession"));
            return;
        }

        if (!session.getCreator().getUniqueId().equals(player.getUniqueId())) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("onlyCreatorCanRemove"));
            return;
        }


        if (!session.containsPlayer(toRemove)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRemoveNotInSession"), new Pair<>("{player}", toRemove.getName()));
            return;
        }


        session.getPlayers().remove(toRemove);
        MsgUtil.msg(sender, PatchTestPlugin.getMsg("removedFromSession"), new Pair<>("{player}", toRemove.getName()));
        session.getPlayers().forEach(player1 -> MsgUtil.msg(player1, PatchTestPlugin.getMsg("playerRemovedFromSession"), new Pair<>("{player}", toRemove.getName())));

        MsgUtil.msg(toRemove, PatchTestPlugin.getMsg("youHaveBeenRemoved"), new Pair<>("{player}", player.getName()));

        if (session.isStarted() || session.getPlot().getAllowedMoveCuboid().contains(toRemove.getLocation()))
            toRemove.setHealth(0);
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1)
            return tabCompleteHelper(args[0], Arrays.asList("start", "invite", "leave", "create", "rejoin", "remove", "tp", "join", "reload", "help", "version"));
        return blank;
    }
}

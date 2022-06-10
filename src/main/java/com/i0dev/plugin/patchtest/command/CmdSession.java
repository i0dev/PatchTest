package com.i0dev.plugin.patchtest.command;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.hook.PlaceholderAPIHook;
import com.i0dev.plugin.patchtest.manager.InventoryManager;
import com.i0dev.plugin.patchtest.manager.PartyManager;
import com.i0dev.plugin.patchtest.manager.SessionManager;
import com.i0dev.plugin.patchtest.object.Pair;
import com.i0dev.plugin.patchtest.object.Party;
import com.i0dev.plugin.patchtest.object.Session;
import com.i0dev.plugin.patchtest.object.SessionType;
import com.i0dev.plugin.patchtest.template.AbstractCommand;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.crypto.BadPaddingException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CmdSession extends AbstractCommand {

    @Getter
    public static final CmdSession instance = new CmdSession();


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
                case "create":
                    create(sender, args);
                    break;
                case "challenge":
                    challenge(sender, args);
                    break;
                case "accept":
                    accept(sender, args);
                    break;
                case "start":
                    start(sender, args);
                    break;
                case "settings":
                    settings(sender, args);
                    break;
                case "join":
                    tp(sender, args);
                    break;
                case "leave":
                    pause(sender, args);
                    break;
                case "disband":
                    stop(sender, args);
                    break;
                case "help":
                default:
                    help(sender, args);
            }
        }
    }

    private void create(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "session.create")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        Session session = SessionManager.getInstance().getSession(((Player) sender).getPlayer());
        if (session != null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("alreadyInSession"));
            return;
        }

        UUID uuid = ((Player) sender).getUniqueId();
        Party party = PartyManager.getInstance().getParty(uuid);
        if (party == null) PartyManager.getInstance().createParty(uuid);
        ((Player) sender).openInventory(InventoryManager.getInstance().getCreateInventory());
    }

    private void challenge(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "session.challenge")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        UUID uuid = ((Player) sender).getUniqueId();
        Party party = PartyManager.getInstance().getParty(uuid);
        if (party == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.notInParty"));
            return;
        }
        Session session = SessionManager.getInstance().getSession(party);

        if (session == null || session.getType() != SessionType.VERSUS) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("session.notInVersusSession"));
            return;
        }

        if (session.getVersusParty() != null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("session.alreadyChallengingParty"));
            return;
        }


        if (args.length != 2) {
            MsgUtil.msg(sender, "&7Usage: &c/session challenge <player>");
            return;
        }
        Player challenged = MsgUtil.getPlayer(args[1]);
        if (challenged == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantFindPlayer"),
                    new Pair<>("{player}", args[1])
            );
            return;
        }

        Party versusParty = PartyManager.getInstance().getParty(challenged.getUniqueId());
        if (versusParty == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.playerNotInAnyParty"));
            return;
        }

        if (session.getPendingChallenges().contains(versusParty.getUuid())) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("session.alreadyChallenged"));
            return;
        }

        session.getPendingChallenges().add(versusParty.getUuid());

        MsgUtil.msg(sender, PatchTestPlugin.getMsg("session.sentChallengeRequest"),
                new Pair<>("{player}", challenged.getName())
        );

        MsgUtil.msg(challenged, PatchTestPlugin.getMsg("session.beenChallenged"),
                new Pair<>("{player}", sender.getName())
        );
    }

    private void accept(CommandSender sender, String[] args) {

        System.out.println(new PlaceholderAPIHook.PatchExpansion().onPlaceholderRequest(Bukkit.getPlayer("ExLocki"),Bukkit.getPlayer("i01"),"relationColor"));

        if (!hasPermission(sender, "session.accept")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        UUID uuid = ((Player) sender).getUniqueId();
        Party party = PartyManager.getInstance().getParty(uuid);
        if (party == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.notInParty"));
            return;
        }
        if (!party.getLeader().equals(uuid)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("session.onlyPartyLeaderCanAcceptChallenge"));
            return;
        }

        Session session = SessionManager.getInstance().getSession(party);
        if (session != null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("session.alreadyInSession"));
            return;
        }

        if (args.length != 2) {
            MsgUtil.msg(sender, "&7Usage: &c/session accept <player>");
            return;
        }
        Player challenger = MsgUtil.getPlayer(args[1]);
        if (challenger == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantFindPlayer"));
            return;
        }

        Party versusParty = PartyManager.getInstance().getParty(challenger.getUniqueId());
        if (versusParty == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.playerNotInAnyParty"));
            return;
        }

        Session versusSession = SessionManager.getInstance().getSession(versusParty);
        if (!versusSession.getPendingChallenges().contains(party.getUuid())) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("session.notBeenChallenged"));
            return;
        }

        versusSession.getPendingChallenges().remove(party.getUuid());
        versusSession.setVersusParty(party);

        MsgUtil.msg(sender, PatchTestPlugin.getMsg("session.acceptedChallenge"),
                new Pair<>("{player}", challenger.getName())
        );

        versusParty.getMembers().forEach(uuid1 -> {
            Player player = Bukkit.getPlayer(uuid1);
            if (player == null) return;
            MsgUtil.msg(player, PatchTestPlugin.getMsg("session.nowInChallenge"),
                    new Pair<>("{player}", Bukkit.getPlayer(party.getLeader()).getName())
            );
        });

        party.getMembers().forEach(uuid1 -> {
            Player player = Bukkit.getPlayer(uuid1);
            if (player == null) return;
            MsgUtil.msg(player, PatchTestPlugin.getMsg("session.nowInChallenge"),
                    new Pair<>("{player}", Bukkit.getPlayer(versusParty.getLeader()).getName())
            );
        });

    }

    private void start(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "session.start")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }

        UUID uuid = ((Player) sender).getUniqueId();
        Party party = PartyManager.getInstance().getParty(uuid);
        if (party == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.notInParty"));
            return;
        }


        Session session = SessionManager.getInstance().getSession(party);
        if (session == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("session.notInSession"));
            return;
        }

        if (!session.getParty().getLeader().equals(uuid)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("session.onlySessionHostCanStart"));
            return;
        }

        if (session.getType() == SessionType.VERSUS && session.getVersusParty() == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("session.noOpponent"));
            return;
        }

        session.start();
    }

    private void tp(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "session.tp")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        Player player = (Player) sender;
        Session session = SessionManager.getInstance().getSession(player);

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

    private void settings(CommandSender sender, String[] args) {

    }

    private void pause(CommandSender sender, String[] args) {

    }

    private void stop(CommandSender sender, String[] args) {

    }

    @Override
    public void help(CommandSender sender, String[] args) {
        MsgUtil.msg(sender, msg().getString("session.helpPageTitle"));
        for (String command : Arrays.asList(
                "session create &f- &7Creates a new blank session.",
                "session challenge <player> &f- &7Challenges the other party to a versus battle.",
                "session accept <player> &f- &7Accepts a challenge request from that party.",
                "session start &f- &7Starts the session.",
                "session settings &f- &7Opens the settings GUI.",
                "session tp &f- &7Teleports you to the roof of the base.",
                "session pause &f- &7Pauses the session.",
                "session stop &f- &7Stops the session fully and cancels it."
        )) {
            MsgUtil.msg(sender, msg().getString("helpPageFormat").replace("{cmd}", command));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1)
            return tabCompleteHelper(args[0], Arrays.asList("reload", "ver", "version", "help", "create", "challenge", "accept", "start", "settings", "tp", "pause", "stop"));
        if (args.length == 2 && (args[1].equalsIgnoreCase("challenge") || args[1].equalsIgnoreCase("accept")))
            return tabCompleteHelper(args[1], players);
        return blank;
    }

}

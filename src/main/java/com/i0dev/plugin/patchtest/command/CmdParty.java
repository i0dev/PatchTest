package com.i0dev.plugin.patchtest.command;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.manager.PartyManager;
import com.i0dev.plugin.patchtest.object.Pair;
import com.i0dev.plugin.patchtest.object.Party;
import com.i0dev.plugin.patchtest.template.AbstractCommand;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CmdParty extends AbstractCommand {

    @Getter
    public static final CmdParty instance = new CmdParty();


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
                case "invite":
                    invite(sender, args);
                    break;
                case "remove":
                    remove(sender, args);
                    break;
                case "revoke":
                    revoke(sender, args);
                    break;
                case "join":
                    join(sender, args);
                    break;
                case "leave":
                    leave(sender, args);
                    break;
                case "disband":
                    disband(sender, args);
                    break;
//                case "setName":
//                    setName(sender, args);
//                    break;
                case "help":
                default:
                    help(sender, args);
            }
        }
    }

    private void create(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "party.create")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }

        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        UUID uuid = ((Player) sender).getUniqueId();
        Party party = PartyManager.getInstance().getParty(uuid);

        if (party != null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.alreadyInParty"));
            return;
        }
        PartyManager.getInstance().createParty(uuid);

        MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.createdParty"));
    }

    private void invite(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "party.invite")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }
        UUID uuid = ((Player) sender).getUniqueId();
        Party party = PartyManager.getInstance().getParty(uuid);
        if (party == null) party = PartyManager.getInstance().createParty(uuid);
        if (args.length != 2) {
            MsgUtil.msg(sender, "&7Usage: &c/party invite <player>");
            return;
        }
        Player invitee = MsgUtil.getPlayer(args[1]);

        if (invitee == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantFindPlayer"));
            return;
        }
        UUID inviteeUUID = invitee.getUniqueId();
        if (party.getPendingInvites().contains(inviteeUUID)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.alreadyInvited"));
            return;
        }
        if (party.getMembers().contains(inviteeUUID)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.playerAlreadyInParty"));
            return;
        }


        party.getPendingInvites().add(inviteeUUID);
        MsgUtil.msg(invitee, PatchTestPlugin.getMsg("party.beenInvited"), new Pair<>("{player}", sender.getName()));
        MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.youInvited"), new Pair<>("{player}", invitee.getName()));
    }

    private void remove(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "party.remove")) {
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


        if (args.length != 2) {
            MsgUtil.msg(sender, "&7Usage: &c/party remove <player>");
            return;
        }
        Player removed = MsgUtil.getPlayer(args[1]);

        if (removed == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantFindPlayer"));
            return;
        }

        if (!party.getMembers().contains(removed.getUniqueId())) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.playerNotInParty"));
            return;
        }

        if (!party.getLeader().equals(uuid)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.onlyLeaderCanRemove"));
            return;
        }


        party.getMembers().remove(removed.getUniqueId());

        MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.removedPlayerFromParty"),
                new Pair<>("{player}", removed.getName())
        );

        MsgUtil.msg(removed, PatchTestPlugin.getMsg("party.removedFromParty"),
                new Pair<>("{player}", sender.getName())
        );

        PatchTestPlugin.spawnPlayer(removed.getUniqueId());
    }

    private void revoke(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "party.revoke")) {
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

        if (args.length != 2) {
            MsgUtil.msg(sender, "&7Usage: &c/party revoke <player>");
            return;
        }
        Player revoked = MsgUtil.getPlayer(args[1]);

        if (revoked == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantFindPlayer"));
            return;
        }

        if (!party.getPendingInvites().contains(revoked.getUniqueId())) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.playerNotInvited"));
            return;
        }

        party.getPendingInvites().remove(revoked.getUniqueId());
        MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.playerRevokedInvite"),
                new Pair<>("{player}", revoked.getName())
        );
        MsgUtil.msg(revoked, PatchTestPlugin.getMsg("party.inviteRevoked"),
                new Pair<>("{player}", sender.getName())
        );

    }

    private void join(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "party.join")) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("noPermission"));
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantRunAsConsole"));
            return;
        }

        if (args.length != 2) {
            MsgUtil.msg(sender, "&7Usage: &c/party join <player>");
            return;
        }
        Player leader = MsgUtil.getPlayer(args[1]);
        UUID uuid = ((Player) sender).getUniqueId();

        if (leader == null) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("cantFindPlayer"),
                    new Pair<>("{player}", args[1])
            );
            return;
        }

        Party party = PartyManager.getInstance().getParty(leader.getUniqueId());
        if (!party.getPendingInvites().contains(uuid)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.notInvited"));
            return;
        }


        MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.youJoined"),
                new Pair<>("{player}", leader.getName())
        );

        party.getMembers().forEach(uuid1 -> {
            Player player = Bukkit.getPlayer(uuid1);
            if (player == null) return;
            MsgUtil.msg(player, PatchTestPlugin.getMsg("party.playerJoinedParty"),
                    new Pair<>("{player}", sender.getName())
            );
        });

        party.getMembers().add(uuid);
        party.getPendingInvites().remove(uuid);
    }

    private void leave(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "party.leave")) {
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

        if (party.getLeader().equals(uuid)) {
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.leaderCantLeave"));
            return;
        }

        party.getMembers().remove(uuid);

        MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.youLeftParty"));

        party.getMembers().forEach(uuid1 -> {
            Player player = Bukkit.getPlayer(uuid1);
            if (player == null) return;
            MsgUtil.msg(player, PatchTestPlugin.getMsg("party.playerLeftParty"),
                    new Pair<>("{player}", sender.getName())
            );
        });

        PatchTestPlugin.spawnPlayer(uuid);
    }

    private void disband(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "party.disband")) {
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
            MsgUtil.msg(sender, PatchTestPlugin.getMsg("party.onlyLeaderCanDisband"));
            return;
        }

        party.getMembers().forEach(uuid1 -> {
            Player player = Bukkit.getPlayer(uuid1);
            if (player == null) return;

            MsgUtil.msg(player, PatchTestPlugin.getMsg("party.partyDisbanded"));
            PatchTestPlugin.spawnPlayer(uuid1);
        });
    }

    @Override
    public void help(CommandSender sender, String[] args) {
        MsgUtil.msg(sender, msg().getString("party.helpPageTitle"));
        for (String command : Arrays.asList(
                "party create &f- &7Creates a party and sets you to leader.",
                "party invite <player> &f- &7Invite a player to your party.",
                "party remove <player> &f- &7Removes a player from your party.",
                "party revoke <player> &f- &7Revokes an invite to that player.",
                "party join <player> &f- &7Joins their party if they invite you.",
                "party leave &f- &7Leave your current party.",
                "party disband &f- &7Disband your current party."
        )) {
            MsgUtil.msg(sender, msg().getString("helpPageFormat").replace("{cmd}", command));
        }
    }
    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1)
            return tabCompleteHelper(args[0], Arrays.asList("reload", "ver", "version", "help", "create", "invite", "remove", "revoke", "join", "leave", "disband"));
        if (args.length == 2 && (args[1].equalsIgnoreCase("invite") || args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("revoke") || args[1].equalsIgnoreCase("join")))
            return tabCompleteHelper(args[1], players);
        return blank;
    }

}

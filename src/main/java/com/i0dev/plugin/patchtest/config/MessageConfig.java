package com.i0dev.plugin.patchtest.config;

import com.i0dev.plugin.patchtest.template.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class MessageConfig extends AbstractConfiguration {

    public MessageConfig(String path) {
        super(path);
    }

    protected void setValues() {
        config.set("reloadedConfig", "&7You have&a reloaded&7 the configuration.");
        config.set("noPermission", "&cYou don not have permission to run that command.");
        config.set("cantFindPlayer", "&cThe player: &f{player}&c cannot be found!");
        config.set("invalidNumber", "&cThe number &f{num} &cis invalid! Try again.");
        config.set("cantRunAsConsole", "&cYou cannot run this command from console.");

        config.set("helpPageTitle", "&8_______&r&8[&r &c&lPatch Help &8]_______");
        config.set("helpPageFormat", " &c* &7/{cmd}");

        config.set("notInSession", "&cYou are not in an active patch session!");
        config.set("teleportedToBase", "&7You have been teleported to the base walls.");
        config.set("cantTeleportUntilStarted", "&7You cannot teleport to the walls until the season is started.");


        config.set("leftAndTransferred", "&7You have left your session and transferred leadership to &c{player}");
        config.set("leaderHasLeft", "&c{player}&7 has left the patch session. Leader has been migrated to &c{newPlayer}");
        config.set("youAreNewLeader", "&7You have been migrated leader since &c{player}&7 has left the patch session.");
        config.set("leftCurrentSessionAndDelete", "&7You have left your current session. It as been deleted since you were the last player.");
        config.set("leftSession", "&7You have left &c{player}'s&7 patch session.");
        config.set("playerLeftSession", "&c{player}&7 has left the patch session.");

        config.set("onlyCreatorCanStart", "&7You are not the creator of this session. Only the creator can start.");
        config.set("startedSession", "&7You have started the patch session, please wait a few seconds while the plot generates.");
        config.set("alreadyStartedSession", "&7Your session is already active.");
        config.set("sessionStartingSoon", Arrays.asList("&aYour session is starting soon",
                "",
                "&7After the start countdown,",
                "&7try to defend your base as best you can!",
                "&cBeware of the guardians that wish to raid you!"));

        config.set("alreadyInSession", "&7You are already in a patch session! Use &c/patch leave&7 to leave if you wish.");
        config.set("noPlotsAvailable", "&7There are no current plots available, please wait for some to clear up.");
        config.set("newPatchSession", Arrays.asList(
                "",
                "&aYou have created a new Patch Session!",
                "",
                "&7Use &c/patch invite <player> &7to invite players to your session.",
                "&7Use &c/patch tp &7to teleport back to your plot if you die during your session.",
                "&7Use &c/obby &7to get free obsidian",
                "&7Use &c/potfill &7to fill your inventory with potions",
                "",
                "&7When you are ready to start your session,",
                "&7use the command &a/session start&7"
        ));

        config.set("noInvite", "&7You do not have any pending invites from &c{player}&7.");
        config.set("sessionAlreadyStarted", "&7That players session has already started, you can no longer join.");
        config.set("joinedSession", "&7You have successfully joined &c{player}'s&7 patch session.");
        config.set("playerJoinedYourSession", "&c{player}&7 has joined your patch session!");

        config.set("onlyCreatorCanInvite", "&7You are not the creator of this session. Only the creator can start.");
        config.set("playerAlreadyInSession", "&c{player} &7is already in your patch session.");
        config.set("inAnotherSession", "&c{player} &7is already in someone else's patch session.");
        config.set("alreadySentInvite", "&7You have already sent an invite to &c{player}");
        config.set("alreadyStartedSessionCantInvite", "&7You have already started your patch session, you cannot invite players anymore.");
        config.set("beenInvited", "You have been invited to join &c{player}'s&7 patch session. Type &c/patch join {player}&7 to join.");
        config.set("youHaveInvited", "&7You have invited &c{player}&7 to your patch session.");

        config.set("onlyCreatorCanRemove", "&7Only the creator of the session can remove players.");
        config.set("cantRemoveNotInSession", "&c{player} &7is not currently in your patch session, so you cannot remove them.");
        config.set("removedFromSession", "&7You have removed &c{player}&7 from your patch session.");
        config.set("playerRemovedFromSession", "&c{player} &7has been removed from the session.");
        config.set("youHaveBeenRemoved", "&c{player} &7has removed you from their patch session.");

        config.set("cantLeavePlotBoundaries", "&cYou cannot leave your plots boundaries, teleporting you back to the walls...");
        config.set("waitForSessionToStartToPlaceBlocks", "&7Please wait for the session to start to place blocks");
        config.set("cantPlaceBlocksOutsidePlot", "&7You cannot place blocks outside of your plot.");
        config.set("waitForSessionToStartToBreakBlocks", "&7Please wait for the session to start to break blocks.");
        config.set("cantPlaceBlocksOutsidePlot", "&7You cannot break blocks outside of your plot.");
        config.set("goBackToSessionOnDeath", "&7To go back to the walls, type &c/patch tp");

        config.set("lostSession", Arrays.asList(
                "",
                "&c&lYour base got breached!",
                "",
                "&7The guardians have breached your base, you lasted:",
                "&c&l{time}",
                "",
                "&7Session Player Size: &c{size}",
                "&7Session Players: &c{players}",
                ""
        ));


        config.set("playerLoggedOffline", "&c{player}&7 has logged offline, they were removed from the session but can rejoin at any time.");
        config.set("playerLoggedOfflineTransferLeader", "&c{player}&7 has logged offline. Leader has been transferred to &c{newPlayer}&7.");
        config.set("yourNewLeaderOldLogOff", "&7You have been transferred leadership of this session because &c{player}&7 has logged offline.");
        config.set("rejoinLogin", "&7You can still join back on the session you were in when you logged offline. Type &c/patch rejoin &7to rejoin.");
        config.set("noRejoin", "&7You are not in any sessions where you can rejoin.");

        config.set("teamFull", "&7That team is currently at its maximum members.");
        config.set("teamFullCantInvite", "&7Your team is at the maximum amount of members.");

        //party
        config.set("party.helpPageTitle", "&8_____&r&8[&r &c&lParty Help &8]_____");
        config.set("party.alreadyInParty", "&cYou are already in a party!");
        config.set("party.createdParty", "&aYou have created a new party!");
        config.set("party.notInParty", "&cYou are not in a party!");
        config.set("party.alreadyInvited", "&cYou have already invited that player to your party!");
        config.set("party.beenInvited", "&7You have been invited to join &c{player}'s&7 party");
        config.set("party.youInvited", "&7You have invited &c{player}&7 to your party.");
        config.set("party.playerNotInParty", "&7That player is not in your party.");
        config.set("party.removedPlayerFromParty", "&7You have removed &c{player}&7 from your party.");
        config.set("party.removedFromParty", "&7You have been removed from &c{player}'s &7party.");
        config.set("party.playerNotInvited", "&7That player has not been invited to your party.");
        config.set("party.playerRevokedInvite", "&7You have revoked &c{player}'s&7 invite to your party");
        config.set("party.inviteRevoked", "&7Your invite to &c{player}'s&7 party has been revoked.");
        config.set("party.notInvited", "&7You have not been invited to join that party.");
        config.set("party.youJoined", "&7You have joined &c{player}'s &7party.");
        config.set("party.playerJoinedParty", "&c{player} &7has joined your party.");
        config.set("party.playerLeftParty", "&c{player} &7has left the party.");
        config.set("party.youLeftParty", "&7You have left the current party.");
        config.set("party.leaderCantLeave", "&7You are the leader of the party, therefore you cannot leave. You can disband with /party disband.");
        config.set("party.onlyLeaderCanDisband", "&7Only the party leader can disband.");
        config.set("party.partyDisbanded", "&7Your party has been disbanded.");
        config.set("party.playerNotInAnyParty", "&7That player is not in a party.");
        config.set("party.playerAlreadyInParty", "&7That player is already in your party.");
        config.set("party.onlyLeaderCanRemove", "&7Only the party leader can remove players.");

        // session
        config.set("session.helpPageTitle", "&8_____&r&8[&r &c&lSession Help &8]_____");
        config.set("session.notInSession", "&7You are not in a session.");
        config.set("session.notInVersusSession", "&7You are not in a versus session.");
        config.set("session.alreadyChallenged", "&7You have already challenged that party.");
        config.set("session.alreadyInSession", "&7You are already in a session.");
        config.set("session.sentChallengeRequest", "&7You have sent a challenge invite to &c{player}");
        config.set("session.beenChallenged", "&c{player} &7has challenged you to a versus battle. accept with &c/session accept {player}");
        config.set("session.alreadyInSession", "&7You are already in a session, leave it first.");
        config.set("session.onlyPartyLeaderCanAcceptChallenge", "&7Only the party leader can accept challenges.");
        config.set("session.notBeenChallenged", "&7Your team has not been challenged by that party.");
        config.set("session.acceptedChallenge", "&7You have accepted the challenge invite from {player}.");
        config.set("session.nowInChallenge", "&7You are now challenging with {player}'s party.");
        config.set("session.onlySessionHostCanStart", "&7Only the session host can start.");
        config.set("session.noOpponent", "&7You have not challenged anyone!");
        config.set("session.alreadyChallengingParty", "&7You are already challenging a party!");
        config.set("session.versusSessionStarting", "&7Versus session starting now. Countdown initiating!");
        config.set("session.rankedSessionStarting", "&7Ranked session starting now. Countdown initiating!");
        config.set("session.sandboxSessionStarting", "&7Sandbox session starting now. Countdown initiating!");
        config.set("session.cantFightTeammates", "&7That player is on your team. You cannot fight them!");
        config.set("session.cantFightTeammates", "&7That player is on your team. You cannot fight them!");
        config.set("session.waitForSessionToStart", "&7Please wait for the session to start to do that!");
        config.set("session.cantDoThatWhileRaiding", "&7You cannot do that while raiding!");
        config.set("session.tpBackOnDeath", "&7You have died! Use &c/session tp&7 to get back.");

        config.set("rankedSessionOver", Arrays.asList(
                "",
                "&c&lYour base got breached by the guardians!",
                "",
                "&7You lasted: &c&l{time}",
                "&7Session Size: &c{size}",
                "&7Players: &c{players}",
                ""
        ));

        config.set("versusSessionOverLost", Arrays.asList(
                "",
                "&c&lYour base got breached by the the opposite team!",
                "",
                "&7You held for: &c&l{time}",
                "&7Your Team: &c{yourPlayers}",
                "&7Enemy Team: &c{enemyPlayers}",
                ""
        ));


        config.set("versusSessionOverWon", Arrays.asList(
                "",
                "&c&lYour raided the other players base!",
                "",
                "&7You raided for: &c&l{time}",
                "&7Your Team: &c{yourPlayers}",
                "&7Enemy Team: &c{enemyPlayers}",
                ""
        ));


    }
}
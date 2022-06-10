package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.object.Party;
import lombok.Data;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Acts as a manager and handler for the party system.
 *
 * @author Andrew Magnuson
 */
@Data
public class PartyManager {

    @Getter
    private static final PartyManager instance = new PartyManager();
    private Set<Party> parties = new HashSet<>();

    /**
     * Checks if the memberUUID passed through is in a party, and if it is, returns it.
     *
     * @param memberUUID The UUID to check for
     * @return The party that member is in, or null if none
     */
    public Party getParty(UUID memberUUID) {
        return parties.stream().filter(party -> party.getMembers().contains(memberUUID)).findFirst().orElse(null);
    }

    /**
     * Acts as an interface to create parties from, will make sure they are all created from the same location
     *
     * @param leader The leader/creator of the party.
     * @return The new party created.
     */
    public Party createParty(UUID leader) {
        Party party = new Party(leader);
        parties.add(party);
        return party;
    }

}

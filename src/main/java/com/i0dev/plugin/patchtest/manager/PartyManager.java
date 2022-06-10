package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.object.Party;
import lombok.Data;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class PartyManager {

    @Getter
    private static final PartyManager instance = new PartyManager();
    private Set<Party> parties = new HashSet<>();

    public Party getParty(UUID leader) {
        return parties.stream().filter(party -> party.getMembers().contains(leader)).findFirst().orElse(null);
    }

    public Party createParty(UUID leader) {
        Party party = new Party(leader);
        parties.add(party);
        return party;
    }

}

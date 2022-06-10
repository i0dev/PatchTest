package com.i0dev.plugin.patchtest.object;

import lombok.Data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class Party {


    private UUID uuid;
    private UUID leader;
    private Set<UUID> members;
    private String name;
    private Set<UUID> pendingInvites;

    public Party(UUID leader) {
        this.uuid = UUID.randomUUID();
        this.leader = leader;
        this.members = new HashSet<>();
        this.members.add(leader);
        this.name = "";
        this.pendingInvites = new HashSet<>();
    }
}

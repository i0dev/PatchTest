package com.i0dev.plugin.patchtest.object;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data@AllArgsConstructor
public class ScoreEntry {

    private UUID creator;
    private Set<UUID> players;
    private TeamSize teamSize;
    private long lengthHeld;
    private long timestamp;
}

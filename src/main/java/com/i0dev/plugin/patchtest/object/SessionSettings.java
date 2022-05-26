package com.i0dev.plugin.patchtest.object;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionSettings {

    private boolean ranked;
    private int cannonSpeed;
    private CannonType cannonType;
    private TeamSize teamSize;

}

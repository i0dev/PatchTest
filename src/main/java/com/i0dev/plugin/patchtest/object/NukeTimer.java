package com.i0dev.plugin.patchtest.object;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 05/24/2022
 */
@Data
public class NukeTimer {
    private final int time;
    private final TimeUnit unit;

    public int toGameticks() {
        return (int) (unit.toMillis(time) / 50);
    }
}

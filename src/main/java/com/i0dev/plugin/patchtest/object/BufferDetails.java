package com.i0dev.plugin.patchtest.object;

import lombok.Getter;

@Getter
public class BufferDetails {

    String identifier = "default";

    int chunksOfWalls = 10;
    int chunksOfPaddingOnSides = 5;
    int chunksOfBaseLength = 5;
    int chunksOfBaseWidth = 1;

    int zShootCoordinate = 200;

}

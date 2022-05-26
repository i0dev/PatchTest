package com.i0dev.plugin.patchtest.object;

public enum TeamSize {

    SOLO(1),
    DUO(2),
    SQUAD(5),
    TEAM(10),
    UNLIMITED(-1);

    final int size;

    TeamSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}

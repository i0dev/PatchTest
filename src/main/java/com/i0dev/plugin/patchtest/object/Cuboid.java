package com.i0dev.plugin.patchtest.object;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Cuboid {

    double xMin, yMin, zMin;
    double xMax, yMax, zMax;
    String worldName;

    public boolean contains(Location location) {
        if (location.getWorld() == null) return false;
        if (!location.getWorld().getName().equalsIgnoreCase(getWorldName())) return false;
        if (location.getBlockX() < getXMin()) return false;
        if (location.getBlockX() > getXMax()) return false;
        if (location.getBlockY() < getYMin()) return false;
        if (location.getBlockY() > getYMax()) return false;
        if (location.getBlockZ() < getZMin()) return false;
        if (location.getBlockZ() > getZMax()) return false;
        return true;
    }

    public Location getCenter() {
        double x = ((getXMax() - getXMin()) / 2) + getXMin();
        double y = ((getYMax() - getYMin()) / 2) + getYMin();
        double z = ((getZMax() - getZMin()) / 2) + getZMin();
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public double getXRange() {
        return getXMax() - getXMin();
    }

    public double getYRange() {
        return getYMax() - getYMin();
    }

    public double getZRange() {
        return getZMax() - getZMin();
    }

    public double getXCenter() {
        return getXMin() + getXRange() / 2;
    }

    public double getYCenter() {
        return getYMin() + getYRange() / 2;
    }

    public double getZCenter() {
        return getYMin() + getZRange() / 2;
    }
}
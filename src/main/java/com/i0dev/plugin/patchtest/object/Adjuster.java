package com.i0dev.plugin.patchtest.object;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Adjuster {

    private PatchPlot plot;
    private Session session;
    private List<Location> previousLocations;
    private Location activeLocation;
    private int adjustHowOften;
    private Location lastBrockLocation;
    private boolean firstShot;
    private long lastAdjustTime;


    public Adjuster(Session session) {
        this.plot = session.getPlot();
        this.session = session;
        firstShot = true;
        activeLocation = plot.getDefaultShootLocation();
        previousLocations = new ArrayList<>();
        lastAdjustTime = 0;
        newAdjustTime();
    }

    public void adjust() {
        activeLocation = getMostEfficientLocation().subtract(0, 1, 0);
        previousLocations.add(activeLocation);
        activeLocation.getBlock().setType(Material.AIR);
        if (lastBrockLocation != null)
            lastBrockLocation.getBlock().setType(Material.AIR);
        Location brockAdjust = activeLocation.clone();
        brockAdjust.add(0, 2, 0);
        brockAdjust.getBlock().setType(Material.BEDROCK);
        lastBrockLocation = brockAdjust;
        lastAdjustTime = System.currentTimeMillis();
        increaseAdjustCounters();
        newAdjustTime();
    }

    public Location getShotLocation() {
        if (System.currentTimeMillis() >= lastAdjustTime + (adjustHowOften * 1000L)) {
            adjust();
            System.out.println("Adjusted to: " + activeLocation);
        }
        firstShot = false;
        return activeLocation;
    }

    private int top = 253;
    private int os_offset = 5;
    private int counter_top;
    private int counter_history;


    private void increaseAdjustCounters() {
        counter_top++;
        counter_history++;
    }

    public Location getMostEfficientLocation() {
        if (firstShot) return plot.getDefaultShootLocation();
        if (session.getCannonType().equals(CannonType.OS_AP_NUKE))
            top = top >= 250 ? top - os_offset : top;

        if (counter_top >= 6) {
            Location newLoc = activeLocation.clone();
            newLoc.setY(top);
            counter_top = 0;
            top -= 2;
            return newLoc;
        }
        if (counter_history >= 4) {
            List<Location> historyCopy = new ArrayList<>(previousLocations);
            Collections.shuffle(historyCopy);
            Location newLoc = historyCopy.get(0);
            newLoc.subtract(0, 2, 0);
            counter_history = 0;
            previousLocations.clear();
            return newLoc;
        }


        List<Location> frontWallBlocks = new ArrayList<>();
        Cuboid walls = plot.getWallsCuboid();
        for (int y = 160; y < walls.getYMax(); y++) {
            for (int x = (int) walls.getXMin(); x < walls.getXMax(); x++) {
                frontWallBlocks.add(new Location(Bukkit.getWorld(PatchTestPlugin.getPlugin().cnf().getString("patchWorldName")), x, y, plot.getWallsCuboid().getZMax()));
            }
        }
        Collections.shuffle(frontWallBlocks);

        int longestHole = 0;
        Location farthestLocation = plot.getDefaultShootLocation();
        for (Location frontWallBlock : frontWallBlocks) {
            int count = 0;
            Location location = frontWallBlock;
            Material blockType = location.getBlock().getType();
            while ((blockType == Material.AIR || blockType == Material.WATER || blockType == Material.STATIONARY_WATER) && location.getZ() > plot.getWallsCuboid().getZMin()) {
                count++;
                location = location.subtract(0, 0, 1);
                blockType = location.getBlock().getType();
            }
            if (count > longestHole) {
                longestHole = count;
                farthestLocation = frontWallBlock;
            }
        }
        farthestLocation.setZ(plot.getZShootCoordinate());
        return farthestLocation;
    }

    public void newAdjustTime() {
        int min = PatchTestPlugin.getPlugin().cnf().getInt("adjustTimeRandomLowerBound");
        int max = PatchTestPlugin.getPlugin().cnf().getInt("adjustTimeRandomHigherBound");
        this.adjustHowOften = ThreadLocalRandom.current().nextInt((max - min) + 1) + min;
    }

}

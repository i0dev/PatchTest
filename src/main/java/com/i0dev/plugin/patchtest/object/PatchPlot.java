package com.i0dev.plugin.patchtest.object;


import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;

@Getter
@Setter
@ToString
public class PatchPlot {

    private int plotIndex;
    private World world;
    private Cuboid wallsCuboid;
    private Cuboid baseCuboid;
    private Cuboid allowedMoveCuboid;
    private Location tpLocation;
    private Location defaultShootLocation;
    private Cuboid plotCuboid;
    private BufferDetails details;

    private int zShootCoordinate;

    public PatchPlot(int plotIndex, Cuboid plotCuboid) {
        this.plotIndex = plotIndex;
        this.plotCuboid = plotCuboid;
        this.world = Bukkit.getWorld(plotCuboid.getWorldName());
        this.details = new BufferDetails();
        this.zShootCoordinate = details.getZShootCoordinate();
        this.allowedMoveCuboid = new Cuboid(plotCuboid.getXMin(), 0, plotCuboid.getZMin(), plotCuboid.getXMax(), 300, plotCuboid.getZMax(), world.getName());


        // Base Cuboid
        double xMin = plotCuboid.getXMin() + (details.chunksOfPaddingOnSides * 16);
        double yMin = 1;
        double zMin = plotCuboid.getZMin();

        double xMax = xMin + (details.chunksOfBaseLength * 16) - 1;
        double zMax = zMin + (details.chunksOfBaseWidth * 16) - 1;

        this.baseCuboid = new Cuboid(xMin, yMin, zMin, xMax, 253, zMax, world.getName());

        // Walls Cuboid
        zMin = zMin + (details.chunksOfBaseWidth * 16);
        zMax = zMax + (details.chunksOfWalls * 16) - 1;

        this.wallsCuboid = new Cuboid(xMin, 0, zMin, xMax, 254, zMax, world.getName());

        this.tpLocation = wallsCuboid.getCenter();
        tpLocation.setY(wallsCuboid.getYMax() + 20);

        this.defaultShootLocation = new Location(
                Bukkit.getWorld(PatchTestPlugin.getPlugin().cnf().getString("patchWorldName")),
                (int) getWallsCuboid().getXCenter(),
                254,
                zShootCoordinate
        );
    }

    @SneakyThrows
    public void pasteSchematic() {
        File file = new File("plugins/PatchTest/buffer.schematic");
        Vector point = new Vector(plotCuboid.getXMin(), plotCuboid.getYMax() + 1, plotCuboid.getZMin());
        Schematic schema = FaweAPI.load(file);
        schema.paste(new BukkitWorld(world), point, false, true, null);
    }

}

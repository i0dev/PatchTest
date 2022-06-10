package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.CannonType;
import com.i0dev.plugin.patchtest.object.NukeTimer;
import com.i0dev.plugin.patchtest.template.AbstractManager;
import com.i0dev.plugin.patchtest.utility.NMSUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * This manager will be all things relating to shooting cannons.
 *
 * @author BestBearr (crumbygames12@gmail.com)
 * @author Andrew Magnuson
 */
public class CannonManager extends AbstractManager {

    @Getter
    private static final CannonManager instance = new CannonManager();

    /**
     * Will shoot a shot of the specified type at the current location, and shooting in the specified direction
     *
     * @param location   The location to spawn in the entity
     * @param direction  The direction to shoot
     * @param cannonType The type of cannon shot to create
     * @author Andrew Magnuson
     */
    public void shoot(Location location, BlockFace direction, CannonType cannonType) {
        NukeTimer timer = new NukeTimer(1500, TimeUnit.MILLISECONDS);
        switch (cannonType) {
            case NUKE:
                shootNuke(location, direction, timer);
                break;
            case AP_NUKE:
                shootNukeAp(location, direction, timer);
                break;
            case OS_AP_NUKE:
                shootNukeOsAp(location, direction, timer);
                break;
        }
    }

    /**
     * Will shoot a normal standard web-nuke at the desired location.
     *
     * @param location  The location to spawn the shot at
     * @param direction The direction to shoot the shot
     * @param nukeTimer The nuke timer
     * @author BestBearr (crumbygames12@gmail.com)
     */
    private void shootNuke(Location location, BlockFace direction, NukeTimer nukeTimer) {
        List<Entity> first = new ArrayList<>(), second = new ArrayList<>();

        // vector to add to entities
        Vector vec = new Vector(0, 250, 0);
        vec.add(getDirection(direction).multiply(300));

        final World world = location.getWorld();

        // spawn the hammer
        this.compute(300, i -> {
            second.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + 6));
        });

        // spawn the slabbust
//        this.compute(50, i -> {
//            first.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + 6));
//        });

        // delay for ooe
        // spawn the sand
        this.compute(252, i -> first.add(world.spawnFallingBlock(location, 12, (byte) 0)));

        // spawn the webbust nuke
        final int nuke = nukeTimer.toGameticks();
        this.compute(125, i -> {
            second.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + nuke));
            if (i > 4)
                first.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + nuke));
        });

        // spawn the os sand
        second.add(world.spawnFallingBlock(location, 12, (byte) 1));

        // apply the velocity
        // send off the sand entities (first power)
        first.forEach(ent -> this.setVelocity(ent, vec));

        // send off the second power
        Bukkit.getScheduler().runTaskLater(PatchTestPlugin.getPlugin(), () -> {
            second.forEach(ent -> this.setVelocity(ent, vec));
        }, 4L);
    }

    /**
     * Will shoot a shot with Over-Stacker & AP enabled.
     * Over-Stacker is limited to 5 blocks
     *
     * @param location  The location to spawn the shot at
     * @param direction The direction to shoot the shot
     * @param nukeTimer The nuke timer
     * @author BestBearr (crumbygames12@gmail.com)
     */
    private void shootNukeOsAp(Location location, BlockFace direction, NukeTimer nukeTimer) {
        List<Entity> first = new ArrayList<>(), second = new ArrayList<>(), third = new ArrayList<>();

        // vector to add to entities
        Vector vec = new Vector(0, 250, 0);
        vec.add(getDirection(direction).multiply(300));

        final World world = location.getWorld();
        final int nuke = nukeTimer.toGameticks();

        // spawn the hammer
        this.compute(303, i -> {
            second.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + 6));
        });

        // spawn the slabbust
//        this.compute(50, i -> {
//            first.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + 6));
//        });

        // delay for ooe
        // spawn the sand
        this.compute(252, i -> {
            first.add(world.spawnFallingBlock(location, 12, (byte) 0));
        });


        // overstacker stuff

        // single splitter tnt, 3rst after hammer
        second.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + 6 + 6));

        // spawn overstack sand
        this.compute(8, i -> second.add(world.spawnFallingBlock(location, 13, (byte) 0)));

        // 8 restack tnt, 3.5 rst after hammer
        this.compute(8, i -> second.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + 6 + 7)));

        // spawn the os sand
        second.add(world.spawnFallingBlock(location, 12, (byte) 1));

        // scatter 2rst before nuke
        this.compute(8, i -> second.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + nuke - 4)));


        // nuke stuff
        // spawn the webbust nuke
        this.compute(250, i -> {
            second.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + nuke));
            if (i > 4)
                first.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + nuke));
        });

        // spawn the AP
        this.compute(125, i -> {
            third.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + 1 + nuke));
        });

        // apply the velocity
        // send off the sand entities (first power)
        first.forEach(ent -> this.setVelocity(ent, vec));

        // send off the second power
        Bukkit.getScheduler().runTaskLater(PatchTestPlugin.getPlugin(), () -> {
            second.forEach(ent -> this.setVelocity(ent, vec));
        }, 4L);

        // send off the third power
        Bukkit.getScheduler().runTaskLater(PatchTestPlugin.getPlugin(), () -> {
            third.forEach(ent -> this.setVelocity(ent, vec));
        }, 4L + 1L + nuke);
    }

    /**
     * Will shoot a shot with AP enabled.
     *
     * @param location  The location to spawn the shot at
     * @param direction The direction to shoot the shot
     * @param nukeTimer The nuke timer
     * @author BestBearr (crumbygames12@gmail.com)
     */
    private void shootNukeAp(Location location, BlockFace direction, NukeTimer nukeTimer) {
        List<Entity> first = new ArrayList<>(), second = new ArrayList<>(), third = new ArrayList<>();

        // vector to add to entities
        Vector vec = new Vector(0, 250, 0);
        vec.add(getDirection(direction).multiply(300));

        final World world = location.getWorld();

        // spawn the hammer
        this.compute(300, i -> {
            second.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + 6));
        });

        // spawn the slabbust
//        this.compute(50, i -> {
//            first.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + 6));
//        });

        // delay for ooe
        // spawn the sand
        this.compute(252, i -> {
            first.add(world.spawnFallingBlock(location, 12, (byte) 0));
        });

        // spawn the webbust nuke
        final int nuke = nukeTimer.toGameticks();
        this.compute(125, i -> {
            second.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + nuke));
            if (i > 4)
                first.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + nuke));
        });

        // spawn the os sand
        second.add(world.spawnFallingBlock(location, 12, (byte) 1));

        // spawn the AP
        this.compute(250, i -> {
            third.add(this.setFuse(world.spawnEntity(location, EntityType.PRIMED_TNT), 4 + 1 + nuke));
        });

        // apply the velocity
        // send off the sand entities (first power)
        first.forEach(ent -> this.setVelocity(ent, vec));

        // send off the second power
        Bukkit.getScheduler().runTaskLater(PatchTestPlugin.getPlugin(), () -> {
            second.forEach(ent -> this.setVelocity(ent, vec));
        }, 4L);

        // send off the third power
        Bukkit.getScheduler().runTaskLater(PatchTestPlugin.getPlugin(), () -> {
            third.forEach(ent -> this.setVelocity(ent, vec));
        }, 4L + 1L + nuke);
    }

    /**
     * Will take an entity and set the fuse ticks to the desired length
     *
     * @param entity The entity to set
     * @param fuse   The length of the fuse in ticks
     * @return The entity with modified fuse length
     * @author BestBearr (crumbygames12@gmail.com)
     */
    private Entity setFuse(Entity entity, int fuse) {
        if (entity instanceof TNTPrimed)
            ((TNTPrimed) entity).setFuseTicks(fuse);
        return entity;
    }

    /**
     * Will set the velocity of the specified entity using nms.
     *
     * @param entity The entity to set velocity to
     * @param vec    The velocity vector.
     * @author
     */
    @SneakyThrows
    private void setVelocity(Entity entity, Vector vec) {
        //Author: bear
        net.minecraft.server.v1_8_R3.Entity ent = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity) entity).getHandle();
        ent.motX += vec.getX();
        ent.motY += vec.getY();
        ent.motZ += vec.getZ();

        // Need to make it so it's not strict to this specific version of nms & craftbukkit

        //Author: i0
//        Object craftEntity = NMSUtil.getOBCClass("entity.CraftEntity").cast(entity);
//        Object handle = craftEntity.getClass().getMethod("getHandle").invoke(craftEntity);
//
//        Double motX = (Double) handle.getClass().getField("motX").get(handle);
//        Double motY = (Double) handle.getClass().getField("motY").get(handle);
//        Double motZ = (Double) handle.getClass().getField("motZ").get(handle);
//
//        handle.getClass().getField("motX").set(handle, motX + vec.getX());
//        handle.getClass().getField("motY").set(handle, motY + vec.getX());
//        handle.getClass().getField("motZ").set(handle, motZ + vec.getX());
//
    }

    /**
     * Will computer the times with the consumer
     *
     * @param times    Times
     * @param consumer Consumer
     * @author BestBearr (crumbygames12@gmail.com)
     */
    private void compute(int times, Consumer<Integer> consumer) {
        for (int i = 0; i < times; i++)
            consumer.accept(i);
    }

    /**
     * Will get the center of the block from the specified location.
     *
     * @param loc The location
     * @return The exact center of that block
     * @author BestBearr (crumbygames12@gmail.com)
     */
    public static Location getCenter(Location loc) {
        return new Location(loc.getWorld(),
                loc.getBlockX() + 0.5D,
                loc.getBlockY() + 0.5D,
                loc.getBlockZ() + 0.5D);
    }

    /**
     * Will get the direction from the blackface and turn it into a vector
     *
     * @param face The block face
     * @return A vector representation of that direction
     * @author BestBearr (crumbygames12@gmail.com)
     */
    private static Vector getDirection(BlockFace face) {
        int modX = face.getModX(), modY = face.getModY(), modZ = face.getModZ();
        Vector direction = new Vector(modX, modY, modZ);
        if (modX != 0 && modY != 0 && modZ != 0)
            direction.normalize();
        return direction;
    }

}

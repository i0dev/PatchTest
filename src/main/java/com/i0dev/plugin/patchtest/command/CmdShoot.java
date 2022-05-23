package com.i0dev.plugin.patchtest.command;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.template.AbstractCommand;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFallingSand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

import java.util.LinkedList;
import java.util.List;

public class CmdShoot extends AbstractCommand {

    private static final int add = 1;
    @Getter
    public static final CmdShoot instance = new CmdShoot();


    void shoot(double[] locIn, double[] mot, World worldIn) {
        final List<Entity> shot1 = new LinkedList<>(), shot2 = new LinkedList<>();
        org.bukkit.World world = worldIn.getWorld();
        Location loc = new Location(worldIn.getWorld(), locIn[0], locIn[1], locIn[2]);
        Location tntloc = loc.clone().add(0.5, 0, 0.5);
        TNTPrimed tnt = world.spawn(tntloc, TNTPrimed.class);// spawn 1rev tnt in
        FallingBlock sand;
        tnt.setFuseTicks(10 + add + 4);
        shot1.add(tnt);
        for (int i = 0; i < 313; ++i) { // add the hammer!
            tnt = world.spawn(tntloc, TNTPrimed.class);
            tnt.setFuseTicks(14 + add + 4);
            shot2.add(tnt);
        }
        int hammer = 14 + add + 4;
        for (int i = 0; i < 255; ++i) { // slabbust
            tnt = world.spawn(tntloc, TNTPrimed.class);
            tnt.setFuseTicks(14 + add + 4);
            shot1.add(tnt);
        }

        for (int i = 0; i < 255; ++i) { // add sand :D
            sand = world.spawnFallingBlock(loc, 12, (byte) 0);
            shot1.add(sand);
        }

        tnt = world.spawn(tntloc, TNTPrimed.class); // add 1stopper tnt for hammer
        tnt.setFuseTicks(hammer + 6); // set delay
        shot2.add(tnt);

        for (int i = 0; i < 6; ++i) { // os sand
            sand = world.spawnFallingBlock(loc, 12, (byte) 0);
            shot2.add(sand);
        }

        for (int i = 0; i < 10; ++i) { // os hammer
            tnt = world.spawn(tntloc, TNTPrimed.class);
            tnt.setFuseTicks(hammer + 7);
            shot2.add(tnt);
        }

        for (int i = 0; i < 4; ++i) { // scatter!
            tnt = world.spawn(tntloc, TNTPrimed.class);
            tnt.setFuseTicks(hammer + 7 + 10);
            shot2.add(tnt);
        }

        for (int i = 0; i < 6; ++i) { // beginning of webnuke
            tnt = world.spawn(tntloc, TNTPrimed.class);
            tnt.setFuseTicks(hammer + 7 + 10 + 3);
            shot2.add(tnt);
        }

        for (int i = 0; i < 350; ++i) { // rest of webnuke
            tnt = world.spawn(tntloc, TNTPrimed.class);
            tnt.setFuseTicks(hammer + 7 + 10 + 3);
            shot2.add(tnt);
            tnt = world.spawn(tntloc, TNTPrimed.class);
            tnt.setFuseTicks(hammer + 7 + 10 + 3);
            shot1.add(tnt);
        }

        sand = world.spawnFallingBlock(loc, 12, (byte) 0); // finally, oneshot sand!
        shot2.add(sand);


        Bukkit.getScheduler().scheduleSyncDelayedTask(PatchTestPlugin.getPlugin(), () -> { // delay by 1 cus it looks nicer to use this idk lmao
            for (org.bukkit.entity.Entity entity : shot1) {
                if (entity instanceof TNTPrimed) {
                    CraftTNTPrimed etnt = (CraftTNTPrimed) entity;
                    EntityTNTPrimed tntPrimed = etnt.getHandle();
                    tntPrimed.motY += 200;
                    tntPrimed.motZ += 200;
                } else if (entity instanceof FallingBlock) {
                    CraftFallingSand etnt = (CraftFallingSand) entity;
                    EntityFallingBlock tntPrimed = etnt.getHandle();
                    tntPrimed.motY += 200;
                    tntPrimed.motZ += 200;
                }
            }
        }, 1L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(PatchTestPlugin.getPlugin(), () -> { // delay by 4 later than first shot movement.
            for (org.bukkit.entity.Entity entity : shot2) {
                if (entity instanceof TNTPrimed) {
                    CraftTNTPrimed etnt = (CraftTNTPrimed) entity;
                    EntityTNTPrimed tntPrimed = etnt.getHandle();
                    tntPrimed.motY += 200;
                    tntPrimed.motZ += 200;
                } else if (entity instanceof FallingBlock) {
                    CraftFallingSand etnt = (CraftFallingSand) entity;
                    EntityFallingBlock tntPrimed = etnt.getHandle();
                    tntPrimed.motY += 200;
                    tntPrimed.motZ += 200;
                }
            }
        }, 5L);


    }

    @Override
    public void execute(CommandSender sender, String[] a) {
        Player player = (Player) sender;
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer ep = craftPlayer.getHandle();
        shoot(new double[]{get(a[0]), get(a[1]), get(a[2])}, new double[]{get(a[3]), get(a[4]), get(a[5])}, ep.getWorld());
    }

    double get(String s) {
        return Double.parseDouble(s);
    }

}

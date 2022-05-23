package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.PatchSession;
import com.i0dev.plugin.patchtest.object.config.MonsterSpawnTime;
import com.i0dev.plugin.patchtest.template.AbstractManager;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class MobManager extends AbstractManager {

    @Getter
    private static final MobManager instance = new MobManager();

    BukkitTask mob;


    @Override
    public void initialize() {
        setListener(true);
        mob = Bukkit.getScheduler().runTaskTimerAsynchronously(PatchTestPlugin.getPlugin(), taskSpawnMobs, 20L, 20L);
    }
    long lastSpawnTime = 0;
    private final Runnable taskSpawnMobs = () -> {
        for (PatchSession session : SessionManager.getInstance().getSessions()) {
            long timeStarted = session.getStartTime();
            for (Object obj : PatchTestPlugin.getPlugin().monster().getList("monsters")) {
                MonsterSpawnTime mst = new MonsterSpawnTime((Map<String, Object>) obj);
                Map<Long, Integer> times = mst.getTimes();
                for (Map.Entry<Long, Integer> entry : times.entrySet()) {
                    long time = entry.getKey();
                    int amountPerPlayer = entry.getValue();
                    if (System.currentTimeMillis() - timeStarted > time
                            && System.currentTimeMillis() - timeStarted < time + 2000
                            && System.currentTimeMillis() - lastSpawnTime > 3000) {
                        Bukkit.getScheduler().runTask(PatchTestPlugin.getPlugin(), () -> {
                            lastSpawnTime = System.currentTimeMillis();
                            for (Player player : session.getPlayers()) {
                                Location spawnLoc = player.getLocation();
                                spawnLoc.add(0, 10, 0);
                                for (int i = 0; i < amountPerPlayer; i++) {
                                    Monster mob = (Monster) session.getPlot().getWorld().spawnEntity(spawnLoc, mst.getEntityType());
                                    mob.setMaxHealth(mst.getHealth());
                                    mob.setHealth(mst.getHealth());
                                    mob.setPassenger(null);
                                    mob.setCanPickupItems(false);
                                    mob.setCustomNameVisible(true);
                                    mob.setCustomName(MsgUtil.color(mst.getCustomName()));
                                    mob.setTarget(player);
                                    if (mob instanceof Zombie) {
                                        ((Zombie) mob).setBaby(false);
                                        ((Zombie) mob).setVillager(false);
                                    }

                                    mst.getPotionEffects().forEach(pot -> mob.addPotionEffect(new PotionEffect(PotionEffectType.getByName(pot.getPotionEffect()), pot.getDuration(), pot.getLevel(), true, true)));

                                    EntityEquipment armor = mob.getEquipment();

                                    armor.setHelmet(mst.getHelmet().toItemStack());
                                    armor.setChestplate(mst.getChestplate().toItemStack());
                                    armor.setLeggings(mst.getLeggings().toItemStack());
                                    armor.setBoots(mst.getBoots().toItemStack());
                                    armor.setItemInHand(mst.getSword().toItemStack());

                                    armor.setHelmetDropChance(0);
                                    armor.setChestplateDropChance(0);
                                    armor.setLeggingsDropChance(0);
                                    armor.setBootsDropChance(0);
                                    armor.setItemInHandDropChance(0);
                                }
                            }
                        });
                    }
                }
            }
        }
    };
}

package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.PatchTestPlugin;
import com.i0dev.plugin.patchtest.object.Session;
import com.i0dev.plugin.patchtest.object.SessionType;
import com.i0dev.plugin.patchtest.object.config.MonsterSpawnTime;
import com.i0dev.plugin.patchtest.template.AbstractManager;
import com.i0dev.plugin.patchtest.utility.MsgUtil;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtinjector.NBTInjector;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

/**
 * Acts as a monster entity manager for the guardians that get spawned in during ranked sessions.
 *
 * @author Andrew Magnuson
 */
public class MobManager extends AbstractManager {

    @Getter
    private static final MobManager instance = new MobManager();

    BukkitTask mob;
    BukkitTask tp;


    @Override
    public void initialize() {
        setListener(true);
        mob = Bukkit.getScheduler().runTaskTimerAsynchronously(PatchTestPlugin.getPlugin(), taskSpawnMobs, 20L, 20L);
        tp = Bukkit.getScheduler().runTaskTimerAsynchronously(PatchTestPlugin.getPlugin(), taskTpMobs, 10L * 20L, 10L * 20L);
    }

    long lastSpawnTime = 0;

    private final Runnable taskTpMobs = () -> {
        World world = Bukkit.getWorld(PatchTestPlugin.getWorldName());
        world.getEntities().stream().filter(entity -> {
            NBTEntity nbtEntity = new NBTEntity(entity);
            return nbtEntity.hasKey("session");
        }).forEach(entity -> {
            NBTEntity nbtEntity = new NBTEntity(entity);
            Player target = Bukkit.getPlayer(UUID.fromString(nbtEntity.getString("target")));
            if (target.getLocation().distance(entity.getLocation()) > 20) {
                Bukkit.getScheduler().runTask(PatchTestPlugin.getPlugin(), () -> {
                    entity.teleport(target.getLocation());
                    ((Monster) entity).setTarget(target);
                });
            }
        });

    };

    private final Runnable taskSpawnMobs = () -> {
        for (Session session : SessionManager.getInstance().getSessions()) {
            if (session.getType() == SessionType.VERSUS) return;
            long timeStarted = session.getStartTimeMillis();
            for (Object obj : PatchTestPlugin.getPlugin().monster().getList("monsters")) {
                MonsterSpawnTime mst = new MonsterSpawnTime((Map<String, Object>) obj);
                Map<Integer, Integer> times = mst.getTimes();
                for (Map.Entry<Integer, Integer> entry : times.entrySet()) {
                    long time = entry.getKey() * 1000L;
                    int amountPerPlayer = entry.getValue();
                    if (System.currentTimeMillis() - timeStarted > time
                            && System.currentTimeMillis() - timeStarted < time + 2000
                            && System.currentTimeMillis() - lastSpawnTime > 3000) {
                        Bukkit.getScheduler().runTask(PatchTestPlugin.getPlugin(), () -> {
                            lastSpawnTime = System.currentTimeMillis();
                            for (Player player : session.getSessionPlayers()) {
                                Location spawnLoc = player.getLocation();
                                spawnLoc.add(0, 1, 0);
                                for (int i = 0; i < amountPerPlayer; i++) {
                                    Monster mob = (Monster) session.getPlot().getWorld().spawnEntity(spawnLoc, mst.getEntityType());
                                    mob.setMaxHealth(mst.getHealth());
                                    mob.setHealth(mst.getHealth());
                                    mob.setPassenger(null);
                                    mob.setCanPickupItems(false);
                                    mob.setCustomNameVisible(true);
                                    mob.setRemoveWhenFarAway(false);
                                    mob.setCustomName(MsgUtil.color(mst.getCustomName()));
                                    mob.setTarget(player);
                                    if (mob instanceof Zombie) {
                                        ((Zombie) mob).setBaby(false);
                                        ((Zombie) mob).setVillager(false);
                                    }

                                    for (com.i0dev.plugin.patchtest.object.config.PotionEffect potionEffect : mst.getPotionEffects()) {
                                        mob.addPotionEffect(new PotionEffect(PotionEffectType.getByName(potionEffect.getPotionEffect()), potionEffect.getDuration(), potionEffect.getLevel(), true, true));
                                    }
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

                                    mob = (Monster) NBTInjector.patchEntity(mob);
                                    NBTCompound comp = NBTInjector.getNbtData(mob);
                                    comp.setString("target", player.getUniqueId().toString());
                                    comp.setString("session", session.getUuid().toString());
                                }
                            }
                        });
                    }
                }
            }
        }
    };


    /**
     * This method will prevent entities from taking damage from exploding blocks. Helpful for having them not die whenever the cannon shoots lol.
     * @param e The event
     */
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) || e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION))
            e.setCancelled(true);
    }

}

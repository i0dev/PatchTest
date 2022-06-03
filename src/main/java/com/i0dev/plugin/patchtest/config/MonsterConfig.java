package com.i0dev.plugin.patchtest.config;

import com.i0dev.plugin.patchtest.object.config.ConfigEnchantedItemStack;
import com.i0dev.plugin.patchtest.object.config.MonsterSpawnTime;
import com.i0dev.plugin.patchtest.object.config.PotionEffect;
import com.i0dev.plugin.patchtest.template.AbstractConfiguration;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;

public class MonsterConfig extends AbstractConfiguration {
    public MonsterConfig(String path) {
        super(path);
    }


    @Override
    protected void setValues() {
        Map<Integer, Integer> spawnTimes = new LinkedHashMap<>();
        spawnTimes.put(10, 2);
        spawnTimes.put(30, 2);
        spawnTimes.put(45, 2);
        spawnTimes.put(60, 2);
        spawnTimes.put(120, 4);
        spawnTimes.put(300, 5);
        spawnTimes.put(400, 5);
        spawnTimes.put(500, 5);
        spawnTimes.put(600, 5);
        spawnTimes.put(700, 5);
        spawnTimes.put(800, 5);
        spawnTimes.put(900, 5);
        spawnTimes.put(1000, 10);
        spawnTimes.put(1200, 10);
        spawnTimes.put(1400, 10);
        spawnTimes.put(1600, 10);
        spawnTimes.put(1800, 10);
        spawnTimes.put(2000, 20);


        config.set("monsters", Arrays.asList(new MonsterSpawnTime(
                spawnTimes,
                "&cGuardian",
                EntityType.ZOMBIE,
                100,
                Arrays.asList(
                        new PotionEffect("WATER_BREATHING", 3, 10000),
                        new PotionEffect("SPEED", 3, 10000)
                ),
                new ConfigEnchantedItemStack(
                        Material.DIAMOND_HELMET,
                        "Helmet",
                        Arrays.asList(
                                "&cAquifier",
                                "&cFatty",
                                "&cLumination"
                        ),
                        0,
                        true,
                        Collections.singletonMap("PROTECTION_ENVIRONMENTAL", 5)
                ),
                new ConfigEnchantedItemStack(
                        Material.DIAMOND_CHESTPLATE,
                        "Chestplate",
                        Arrays.asList(
                                "&cVigorous",
                                "&cHealth Boost",
                                "&cHaste"
                        ),
                        0,
                        true,
                        Collections.singletonMap("PROTECTION_ENVIRONMENTAL", 5)
                ),
                new ConfigEnchantedItemStack(
                        Material.DIAMOND_LEGGINGS,
                        "Leggings",
                        Arrays.asList(
                                "&cReplenish",
                                "&cNether Skin",
                                "&cResistance"
                        ),
                        0,
                        true,
                        Collections.singletonMap("PROTECTION_ENVIRONMENTAL", 5)
                ),
                new ConfigEnchantedItemStack(
                        Material.DIAMOND_BOOTS,
                        "Boots",
                        Arrays.asList(
                                "&cAgility",
                                "&cJelly"
                        ),
                        0,
                        true,
                        Collections.singletonMap("DEPTH_STRIDER", 3)
                ),
                new ConfigEnchantedItemStack(
                        Material.DIAMOND_SWORD,
                        "Sword",
                        new ArrayList<>(),
                        0,
                        true,
                        Collections.singletonMap("DAMAGE_ALL", 15)
                )
        ).serialize()));
    }
}

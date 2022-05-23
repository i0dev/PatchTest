package com.i0dev.plugin.patchtest.config;

import com.i0dev.plugin.patchtest.object.config.ConfigItemStack;
import com.i0dev.plugin.patchtest.object.config.PotionEffect;
import com.i0dev.plugin.patchtest.object.config.MonsterSpawnTime;
import com.i0dev.plugin.patchtest.template.AbstractConfiguration;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MonsterConfig extends AbstractConfiguration {
    public MonsterConfig(String path) {
        super(path);
    }


    @Override
    protected void setValues() {
        config.set("monsters", Arrays.asList(new MonsterSpawnTime(Collections.singletonMap(15000L, 1), "&cGuardian", EntityType.SKELETON, 100,
                Arrays.asList(new PotionEffect("WATER_BREATHING", 3, 10000), new PotionEffect("SPEED", 3, 10000)),
                new ConfigItemStack(
                        Material.DIAMOND_HELMET,
                        "Helmet",
                        new ArrayList<>(),
                        Collections.singletonMap("PROTECTION_ENVIRONMENTAL", 5)
                ),
                new ConfigItemStack(
                        Material.DIAMOND_CHESTPLATE,
                        "Chestplate",
                        new ArrayList<>(),
                        Collections.singletonMap("PROTECTION_ENVIRONMENTAL", 5)
                ),
                new ConfigItemStack(
                        Material.DIAMOND_LEGGINGS,
                        "Leggings",
                        new ArrayList<>(),
                        Collections.singletonMap("PROTECTION_ENVIRONMENTAL", 5)
                ),
                new ConfigItemStack(
                        Material.DIAMOND_BOOTS,
                        "Boots",
                        new ArrayList<>(),
                        Collections.singletonMap("DEPTH_STRIDER", 3)
                ),
                new ConfigItemStack(
                        Material.DIAMOND_SWORD,
                        "Sword",
                        new ArrayList<>(),
                        Collections.singletonMap("PROTECTION_ENVIRONMENTAL", 5)
                )
        ).serialize(), new MonsterSpawnTime(Collections.singletonMap(20000L, 1), "&cGuardian", EntityType.ZOMBIE, 100,
                Arrays.asList(new PotionEffect("WATER_BREATHING", 3, 10000), new PotionEffect("SPEED", 3, 10000)),
                new ConfigItemStack(
                        Material.DIAMOND_HELMET,
                        "Helmet",
                        new ArrayList<>(),
                        Collections.singletonMap("PROTECTION_ENVIRONMENTAL", 5)
                ),
                new ConfigItemStack(
                        Material.DIAMOND_CHESTPLATE,
                        "Chestplate",
                        new ArrayList<>(),
                        Collections.singletonMap("PROTECTION_ENVIRONMENTAL", 5)
                ),
                new ConfigItemStack(
                        Material.DIAMOND_LEGGINGS,
                        "Leggings",
                        new ArrayList<>(),
                        Collections.singletonMap("PROTECTION_ENVIRONMENTAL", 5)
                ),
                new ConfigItemStack(
                        Material.DIAMOND_BOOTS,
                        "Boots",
                        new ArrayList<>(),
                        Collections.singletonMap("DEPTH_STRIDER", 3)
                ),
                new ConfigItemStack(
                        Material.DIAMOND_SWORD,
                        "Sword",
                        new ArrayList<>(),
                        Collections.singletonMap("PROTECTION_ENVIRONMENTAL", 5)
                )
        ).serialize()));
    }
}

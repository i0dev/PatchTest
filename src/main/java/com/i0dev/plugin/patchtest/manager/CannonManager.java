package com.i0dev.plugin.patchtest.manager;

import com.i0dev.plugin.patchtest.template.AbstractManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class CannonManager extends AbstractManager {


    @Getter
    private static final CannonManager instance = new CannonManager();


    public void shootCannon(Location location) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage("shot cannon"));
    }


}

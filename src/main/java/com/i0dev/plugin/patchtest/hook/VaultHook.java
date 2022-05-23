package com.i0dev.plugin.patchtest.hook;

import com.i0dev.plugin.patchtest.template.AbstractHook;
import com.i0dev.plugin.patchtest.PatchTestPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class VaultHook extends AbstractHook {
    Economy economy;
    @Override
    public void initialize() {
        economy = PatchTestPlugin.getPlugin().getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }

    public double getBalance(Player player) {
        return economy.getBalance(player);
    }

    public EconomyResponse withdrawMoney(Player player, double amount) {
        return economy.withdrawPlayer(player, amount);
    }
}

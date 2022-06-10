package com.i0dev.plugin.patchtest.hook;

import com.i0dev.plugin.patchtest.template.AbstractHook;
import com.i0dev.plugin.patchtest.PatchTestPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

/**
 * This hook will be for the plugin Vault, which controls economy, display names, and much more
 *
 * @author Andrew magnuson
 */
public class VaultHook extends AbstractHook {
    Economy economy;

    @Override
    public void initialize() {
        economy = PatchTestPlugin.getPlugin().getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }

    /**
     * @param player The player to get the balance of.
     * @return The balance of the specified player.
     */
    public double getBalance(Player player) {
        return economy.getBalance(player);
    }

    /**
     * @param player The player to withdraw money from.
     * @param amount The amount of money to take.
     * @return an Economy Response with how the transaction went.
     */
    public EconomyResponse withdrawMoney(Player player, double amount) {
        return economy.withdrawPlayer(player, amount);
    }
}

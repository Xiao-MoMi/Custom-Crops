package net.momirealms.customcrops.integration;

import org.bukkit.entity.Player;

public interface JobInterface {
    void addXp(Player player, double amount);
    int getLevel(Player player);
}

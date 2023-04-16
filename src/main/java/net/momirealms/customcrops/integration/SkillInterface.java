package net.momirealms.customcrops.integration;

import org.bukkit.entity.Player;

public interface SkillInterface {

    void addXp(Player player, double amount);

    int getLevel(Player player);
}

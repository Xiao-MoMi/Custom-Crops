package net.momirealms.customcrops.api.object.condition;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.CrowTask;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CrowAttack implements Condition {

    private final double chance;
    private final String fly_model;
    private final String stand_model;

    public CrowAttack(double chance, String fly_model, String stand_model) {
        this.chance = chance;
        this.fly_model = fly_model;
        this.stand_model = stand_model;
    }

    @Override
    public boolean isMet(SimpleLocation simpleLocation) {
        if (Math.random() > chance) return false;
        Location location = simpleLocation.getBukkitLocation();
        if (location == null) return false;
        Bukkit.getScheduler().runTask(CustomCrops.getInstance(), () -> {
            for (Player player : location.getNearbyPlayers(48)) {
                CrowTask crowTask = new CrowTask(player, location.clone().add(0.4,0,0.4), fly_model, stand_model);
                crowTask.runTaskTimerAsynchronously(CustomCrops.getInstance(), 1, 1);
            }
        });
        return true;
    }
}
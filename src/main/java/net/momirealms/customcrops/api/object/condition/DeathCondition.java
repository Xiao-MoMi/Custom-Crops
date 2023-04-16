package net.momirealms.customcrops.api.object.condition;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.CustomCropsAPI;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeathCondition {

    private final String dead_model;
    private final Condition[] conditions;

    public DeathCondition(@Nullable String dead_model, @NotNull Condition[] conditions) {
        this.dead_model = dead_model;
        this.conditions = conditions;
    }

    public boolean checkIfDead(SimpleLocation simpleLocation) {
        for (Condition condition : conditions) {
            if (condition.isMet(simpleLocation)) {
                return true;
            }
        }
        return false;
    }

    public void applyDeadModel(SimpleLocation simpleLocation, ItemMode itemMode) {
        Location location = simpleLocation.getBukkitLocation();
        if (location == null) return;
        Bukkit.getScheduler().callSyncMethod(CustomCrops.getInstance(), () -> {
            CustomCropsAPI.getInstance().removeCustomItem(location, itemMode);
            if (dead_model != null) {
                CustomCropsAPI.getInstance().placeCustomItem(location, dead_model, itemMode);
            }
            return null;
        });
    }
}

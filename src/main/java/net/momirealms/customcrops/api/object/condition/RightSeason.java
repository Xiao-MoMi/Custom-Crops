package net.momirealms.customcrops.api.object.condition;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.season.CCSeason;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.api.object.world.WorldDataManager;

public class RightSeason implements Condition {

    private final CCSeason[] seasons;

    public RightSeason(CCSeason[] seasons) {
        this.seasons = seasons;
    }

    @Override
    public boolean isMet(SimpleLocation simpleLocation) {
        String world = simpleLocation.getWorldName();
        CCSeason current = CustomCrops.getInstance().getIntegrationManager().getSeasonInterface().getSeason(world);
        for (CCSeason allowed : seasons) {
            if (current == allowed) {
                return true;
            }
        }
        WorldDataManager worldDataManager = CustomCrops.getInstance().getWorldDataManager();
        if (ConfigManager.enableGreenhouse) {
            for (int i = 0; i < ConfigManager.greenhouseRange; i++) {
                if (worldDataManager.isGreenhouse(simpleLocation.add(0, i, 0))) {
                    return true;
                }
            }
        }
        return false;
    }
}

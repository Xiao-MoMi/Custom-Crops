package net.momirealms.customcrops.integration.season;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.season.CCSeason;
import net.momirealms.customcrops.integration.SeasonInterface;

public class CustomCropsSeasonImpl implements SeasonInterface {

    @Override
    public CCSeason getSeason(String world) {
        return CustomCrops.getInstance().getSeasonManager().getSeason(world);
    }

    @Override
    public int getDate(String world) {
        return CustomCrops.getInstance().getSeasonManager().getDate(world);
    }
}
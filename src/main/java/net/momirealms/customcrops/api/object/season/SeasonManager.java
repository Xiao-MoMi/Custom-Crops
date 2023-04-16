package net.momirealms.customcrops.api.object.season;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.integration.SeasonInterface;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class SeasonManager extends Function {

    private final CustomCrops plugin;
    private final ConcurrentHashMap<String, SeasonData> seasonMap;

    public SeasonManager(CustomCrops plugin) {
        this.plugin = plugin;
        this.seasonMap = new ConcurrentHashMap<>(4);
    }

    @Override
    public void disable() {
        this.seasonMap.clear();
    }

    @Nullable
    public SeasonData getSeasonData(String world) {
        return seasonMap.get(world);
    }

    public void loadSeasonData(SeasonData seasonData) {
        seasonMap.put(seasonData.getWorld(), seasonData);
    }

    @Nullable
    public SeasonData unloadSeasonData(String world) {
        return seasonMap.remove(world);
    }

    public CCSeason getSeason(String world) {
        SeasonData seasonData = seasonMap.get(world);
        if (seasonData == null) return CCSeason.UNKNOWN;
        return seasonData.getSeason();
    }

    public void addDate(String world) {
        SeasonData seasonData = seasonMap.get(world);
        if (seasonData != null) seasonData.addDate();
    }

    public int getDate(String world) {
        SeasonData seasonData = seasonMap.get(world);
        if (seasonData == null) return -1;
        return seasonData.getDate();
    }
}

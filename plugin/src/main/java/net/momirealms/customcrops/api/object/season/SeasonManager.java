/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.api.object.season;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
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
        SeasonData seasonData = seasonMap.get(ConfigManager.syncSeason ? ConfigManager.referenceWorld : world);
        if (seasonData == null) {
            return CCSeason.UNKNOWN;
        }
        return seasonData.getSeason();
    }

    public void addDate(String world) {
        SeasonData seasonData = seasonMap.get(world);
        if (seasonData != null) seasonData.addDate();
    }

    public int getDate(String world) {
        SeasonData seasonData = seasonMap.get(ConfigManager.syncSeason ? ConfigManager.referenceWorld : world);
        if (seasonData == null) return -1;
        return seasonData.getDate();
    }
}

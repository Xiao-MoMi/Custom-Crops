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

package net.momirealms.customcrops.api.object.requirement;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.action.Action;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.season.CCSeason;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.api.object.world.WorldDataManager;
import net.momirealms.customcrops.integration.SeasonInterface;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SeasonImpl extends AbstractRequirement implements Requirement {

    private final List<CCSeason> seasons;

    public SeasonImpl(@Nullable String[] msg, @Nullable Action[] actions, List<CCSeason> seasons) {
        super(msg, actions);
        this.seasons = seasons;
    }

    @Override
    public boolean isConditionMet(CurrentState currentState) {
        SeasonInterface seasonInterface = CustomCrops.getInstance().getIntegrationManager().getSeasonInterface();
        CCSeason currentSeason = seasonInterface.getSeason(currentState.getLocation().getWorld().getName());
        if (seasons.contains(currentSeason)) {
            return true;
        }

        SimpleLocation simpleLocation = SimpleLocation.getByBukkitLocation(currentState.getLocation());
        WorldDataManager worldDataManager = CustomCrops.getInstance().getWorldDataManager();
        if (ConfigManager.enableGreenhouse) {
            for (int i = 0; i < ConfigManager.greenhouseRange; i++) {
                if (worldDataManager.isGreenhouse(simpleLocation.add(0, i, 0))) {
                    return true;
                }
            }
        }
        notMetActions(currentState);
        return false;
    }
}
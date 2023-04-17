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

package net.momirealms.customcrops.api.object.condition;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.CustomCropsAPI;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
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

    public int checkIfDead(SimpleLocation simpleLocation) {
        for (Condition condition : conditions) {
            if (condition.isMet(simpleLocation)) {
                return condition.getDelay();
            }
        }
        return -1;
    }

    public void applyDeadModel(SimpleLocation simpleLocation, ItemMode itemMode) {
        Location location = simpleLocation.getBukkitLocation();
        if (location == null) return;
        CustomCrops.getInstance().getScheduler().callSyncMethod(() -> {
            CustomCropsAPI.getInstance().removeCustomItem(location, itemMode);
            if (dead_model != null) {
                CustomCropsAPI.getInstance().placeCustomItem(location, dead_model, itemMode);
            }
            return null;
        });
    }
}

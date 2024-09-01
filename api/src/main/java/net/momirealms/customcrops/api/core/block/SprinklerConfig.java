/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.api.core.block;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.misc.water.WaterBar;
import net.momirealms.customcrops.api.misc.water.WateringMethod;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface SprinklerConfig {

    String id();

    int storage();

    int[][] range();

    boolean infinite();

    int sprinklingAmount();

    @Nullable
    String twoDItem();

    @NotNull
    String threeDItem();

    @NotNull
    String threeDItemWithWater();

    @NotNull
    Set<String> potWhitelist();

    @NotNull
    Set<String> modelIDs();

    @Nullable
    WaterBar waterBar();

    @NotNull
    ExistenceForm existenceForm();

    /**
     * Get the requirements for placement
     *
     * @return requirements for placement
     */
    @Nullable
    Requirement<Player>[] placeRequirements();

    /**
     * Get the requirements for breaking
     *
     * @return requirements for breaking
     */
    @Nullable
    Requirement<Player>[] breakRequirements();

    /**
     * Get the requirements for using
     *
     * @return requirements for using
     */
    @Nullable
    Requirement<Player>[] useRequirements();

    @Nullable
    Action<CustomCropsBlockState>[] workActions();

    @Nullable
    Action<Player>[] interactActions();

    @Nullable
    Action<Player>[] placeActions();

    @Nullable
    Action<Player>[] breakActions();

    @Nullable
    Action<Player>[] addWaterActions();

    @Nullable
    Action<Player>[] reachLimitActions();

    @Nullable
    Action<Player>[] fullWaterActions();

    @NotNull
    WateringMethod[] wateringMethods();

    static Builder builder() {
        return new SprinklerConfigImpl.BuilderImpl();
    }

    interface Builder {

        SprinklerConfig build();

        Builder id(String id);

        Builder existenceForm(ExistenceForm existenceForm);

        Builder storage(int storage);

        Builder range(int[][] range);

        Builder infinite(boolean infinite);

        Builder sprinklingAmount(int sprinklingAmount);

        Builder potWhitelist(Set<String> potWhitelist);

        Builder waterBar(WaterBar waterBar);

        Builder twoDItem(@Nullable String twoDItem);

        Builder threeDItem(String threeDItem);

        Builder threeDItemWithWater(String threeDItemWithWater);

        Builder placeRequirements(Requirement<Player>[] placeRequirements);

        Builder breakRequirements(Requirement<Player>[] breakRequirements);

        Builder useRequirements(Requirement<Player>[] useRequirements);

        Builder workActions(Action<CustomCropsBlockState>[] workActions);

        Builder interactActions(Action<Player>[] interactActions);

        Builder addWaterActions(Action<Player>[] addWaterActions);

        Builder reachLimitActions(Action<Player>[] reachLimitActions);

        Builder placeActions(Action<Player>[] placeActions);

        Builder breakActions(Action<Player>[] breakActions);

        Builder fullWaterActions(Action<Player>[] fullWaterActions);

        Builder wateringMethods(WateringMethod[] wateringMethods);
    }
}

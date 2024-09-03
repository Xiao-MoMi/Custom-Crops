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

package net.momirealms.customcrops.api.core.mechanic.crop;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface CropConfig {

    String id();

    String seed();

    int maxPoints();

    Requirement<Player>[] plantRequirements();

    Requirement<Player>[] breakRequirements();

    Requirement<Player>[] interactRequirements();

    GrowCondition[] growConditions();

    Action<Player>[] wrongPotActions();

    Action<Player>[] interactActions();

    Action<Player>[] breakActions();

    Action<Player>[] plantActions();

    Action<Player>[] reachLimitActions();

    Action<CustomCropsBlockState>[] deathActions();

    DeathCondition[] deathConditions();

    BoneMeal[] boneMeals();

    boolean rotation();

    Set<String> potWhitelist();

    CropStageConfig stageByPoint(int point);

    @Nullable
    CropStageConfig stageByID(String stageModel);

    CropStageConfig stageWithModelByPoint(int point);

    Collection<CropStageConfig> stages();

    Collection<String> stageIDs();

    Map.Entry<Integer, CropStageConfig> getFloorStageEntry(int previousPoint);

    static Builder builder() {
        return new CropConfigImpl.BuilderImpl();
    }

    interface Builder {

        CropConfig build();

        Builder id(String id);

        Builder seed(String seed);

        Builder maxPoints(int maxPoints);

        Builder wrongPotActions(Action<Player>[] wrongPotActions);

        Builder interactActions(Action<Player>[] interactActions);

        Builder breakActions(Action<Player>[] breakActions);

        Builder plantActions(Action<Player>[] plantActions);

        Builder reachLimitActions(Action<Player>[] reachLimitActions);

        Builder plantRequirements(Requirement<Player>[] plantRequirements);

        Builder breakRequirements(Requirement<Player>[] breakRequirements);

        Builder interactRequirements(Requirement<Player>[] interactRequirements);

        Builder growConditions(GrowCondition[] growConditions);

        Builder deathConditions(DeathCondition[] deathConditions);

        Builder boneMeals(BoneMeal[] boneMeals);

        Builder rotation(boolean rotation);

        Builder potWhitelist(Set<String> whitelist);

        Builder stages(Collection<CropStageConfig.Builder> stages);
    }
}

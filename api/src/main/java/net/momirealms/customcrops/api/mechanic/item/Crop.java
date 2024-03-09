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

package net.momirealms.customcrops.api.mechanic.item;

import net.momirealms.customcrops.api.common.item.KeyItem;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.condition.Conditions;
import net.momirealms.customcrops.api.mechanic.condition.DeathConditions;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

public interface Crop extends KeyItem {

    String getSeedItemID();

    int getMaxPoints();

    Requirement[] getPlantRequirements();

    Requirement[] getBreakRequirements();

    Requirement[] getInteractRequirements();

    Conditions getGrowConditions();

    DeathConditions[] getDeathConditions();

    BoneMeal[] getBoneMeals();

    boolean hasRotation();

    void trigger(ActionTrigger trigger, State state);

    Stage getStageByPoint(int point);

    String getStageItemByPoint(int point);

    Stage getStageByItemID(String itemID);

    Collection<? extends Stage> getStages();

    HashSet<String> getPotWhitelist();

    ItemCarrier getItemCarrier();

    interface Stage {

        double getHologramOffset();

        @Nullable String getStageID();

        int getPoint();

        void trigger(ActionTrigger trigger, State state);

        Requirement[] getInteractRequirements();

        Requirement[] getBreakRequirements();
    }
}

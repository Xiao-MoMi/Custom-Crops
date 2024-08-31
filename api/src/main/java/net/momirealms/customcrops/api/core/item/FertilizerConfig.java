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

package net.momirealms.customcrops.api.core.item;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface FertilizerConfig {

    String id();

    FertilizerType type();

    boolean beforePlant();

    String icon();

    Requirement<Player>[] requirements();

    String itemID();

    int times();

    Set<String> whitelistPots();

    Action<Player>[] beforePlantActions();

    Action<Player>[] useActions();

    Action<Player>[] wrongPotActions();

    int processGainPoints(int previousPoints);

    int processWaterToLose(int waterToLose);

    double processVariationChance(double previousChance);

    int processDroppedItemAmount(int amount);

    @Nullable
    double[] overrideQualityRatio();
}

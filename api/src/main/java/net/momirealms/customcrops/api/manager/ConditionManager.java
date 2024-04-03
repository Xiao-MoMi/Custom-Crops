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

package net.momirealms.customcrops.api.manager;

import net.momirealms.customcrops.api.common.Reloadable;
import net.momirealms.customcrops.api.mechanic.condition.Condition;
import net.momirealms.customcrops.api.mechanic.condition.ConditionFactory;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConditionManager extends Reloadable {

    boolean registerCondition(String type, ConditionFactory conditionFactory);

    boolean unregisterCondition(String type);

    boolean hasCondition(String type);

    @NotNull
    Condition[] getConditions(ConfigurationSection section);

    Condition getCondition(ConfigurationSection section);

    Condition getCondition(String key, Object args);

    @Nullable ConditionFactory getConditionFactory(String type);

    static boolean isConditionMet(CustomCropsBlock block, boolean offline, Condition... conditions) {
        if (conditions == null) return true;
        for (Condition condition : conditions) {
            if (!condition.isConditionMet(block, offline)) {
                return false;
            }
        }
        return true;
    }
}

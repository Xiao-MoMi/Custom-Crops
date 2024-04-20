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

    /**
     * Register a custom condition type
     *
     * @param type type
     * @param conditionFactory condition factory
     * @return success or not
     */
    boolean registerCondition(String type, ConditionFactory conditionFactory);

    /**
     * Unregister a condition type by id
     *
     * @param type type
     * @return success or not
     */
    boolean unregisterCondition(String type);

    /**
     * If a condition type exists
     *
     * @param type type
     * @return exist or not
     */
    default boolean hasCondition(String type) {
        return getConditionFactory(type) != null;
    }

    /**
     * Build conditions with Bukkit configs
     *
     * @param section bukkit config
     * @return conditions
     */
    @NotNull
    Condition[] getConditions(ConfigurationSection section);

    /**
     * Build a condition instance with Bukkit configs
     *
     * @param section bukkit config
     * @return condition
     */
    Condition getCondition(ConfigurationSection section);

    /**
     * Build a condition instance with Bukkit configs
     *
     * @return condition
     */
    Condition getCondition(String key, Object args);

    /**
     * Get a condition factory by type
     *
     * @param type type
     * @return condition factory
     */
    @Nullable ConditionFactory getConditionFactory(String type);

    /**
     * Are conditions met for a custom crops block
     *
     * @param block block
     * @param offline is the check for offline ticks
     * @param conditions conditions
     * @return meet or not
     */
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

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
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.mechanic.requirement.RequirementFactory;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RequirementManager extends Reloadable {

    boolean registerRequirement(String type, RequirementFactory requirementFactory);

    boolean unregisterRequirement(String type);

    @Nullable
    Requirement[] getRequirements(ConfigurationSection section, boolean advanced);

    boolean hasRequirement(String type);

    @NotNull
    Requirement getRequirement(ConfigurationSection section, boolean advanced);

    @NotNull
    Requirement getRequirement(String type, Object value);

    @Nullable
    RequirementFactory getRequirementFactory(String type);

    static boolean isRequirementMet(State condition, Requirement... requirements) {
        if (requirements == null) return true;
        for (Requirement requirement : requirements) {
            if (!requirement.isStateMet(condition)) {
                return false;
            }
        }
        return true;
    }
}

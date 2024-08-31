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

import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.requirement.Requirement;
import net.momirealms.customcrops.api.requirement.RequirementManager;
import org.jetbrains.annotations.Nullable;

public class DeathCondition {

    private final Requirement<CustomCropsBlockState>[] requirements;
    private final String deathStage;
    private final ExistenceForm existenceForm;
    private final int deathDelay;

    public DeathCondition(Requirement<CustomCropsBlockState>[] requirements, String deathStage, ExistenceForm existenceForm, int deathDelay) {
        this.requirements = requirements;
        this.deathStage = deathStage;
        this.existenceForm = existenceForm;
        this.deathDelay = deathDelay;
    }

    @Nullable
    public String deathStage() {
        return deathStage;
    }

    public int deathDelay() {
        return deathDelay;
    }

    public boolean isMet(Context<CustomCropsBlockState> context) {
        return RequirementManager.isSatisfied(context, requirements);
    }

    public ExistenceForm existenceForm() {
        return existenceForm;
    }
}

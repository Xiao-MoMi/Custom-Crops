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

import net.momirealms.customcrops.api.object.world.SimpleLocation;

import java.util.List;

public class AndCondition implements Condition {

    private final List<Condition> deathConditions;

    public AndCondition(List<Condition> deathConditions) {
        this.deathConditions = deathConditions;
    }

    @Override
    public boolean isMet(SimpleLocation simpleLocation) {
        for (Condition condition : deathConditions) {
            if (!condition.isMet(simpleLocation)) {
                return false;
            }
        }
        return true;
    }
}
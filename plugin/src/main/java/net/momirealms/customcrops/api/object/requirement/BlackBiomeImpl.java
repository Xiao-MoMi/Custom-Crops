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

package net.momirealms.customcrops.api.object.requirement;

import net.momirealms.biomeapi.BiomeAPI;
import net.momirealms.customcrops.api.object.action.Action;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class BlackBiomeImpl extends AbstractRequirement implements Requirement {

    private final HashSet<String> biomes;

    public BlackBiomeImpl(@Nullable String[] msg, @Nullable Action[] actions, HashSet<String> biomes) {
        super(msg, actions);
        this.biomes = biomes;
    }

    @Override
    public boolean isConditionMet(CurrentState currentState) {
        String currentBiome = BiomeAPI.getBiome(currentState.getLocation());
        if (!biomes.contains(currentBiome)) {
            return true;
        }
        notMetActions(currentState);
        return false;
    }
}

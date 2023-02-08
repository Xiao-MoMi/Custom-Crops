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

package net.momirealms.customcrops.objects.requirements;

import net.momirealms.biomeapi.BiomeAPI;

public class RequirementBiome extends Requirement implements RequirementInterface {

    public RequirementBiome(String[] values, boolean mode, String msg) {
        super(values, mode, msg);
    }

    @Override
    public boolean isConditionMet(PlayerCondition playerCondition) {
        String currentBiome = BiomeAPI.getBiome(playerCondition.getLocation());
        if (mode) {
            for (String value : values) {
                if (!(currentBiome.equalsIgnoreCase(value))) {
                    notMetMessage(playerCondition.getPlayer());
                    return false;
                }
            }
            return true;
        }
        else {
            for (String value : values) {
                if (currentBiome.equalsIgnoreCase(value)) {
                    return true;
                }
            }
            notMetMessage(playerCondition.getPlayer());
            return false;
        }
    }
}
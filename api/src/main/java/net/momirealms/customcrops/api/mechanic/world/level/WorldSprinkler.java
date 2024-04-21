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

package net.momirealms.customcrops.api.mechanic.world.level;

import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;

public interface WorldSprinkler extends CustomCropsBlock {

    /**
     * Get the amount of water
     *
     * @return amount of water
     */
    int getWater();

    /**
     * Set the amount of water
     *
     * @param water amount of water
     */
    void setWater(int water);

    /**
     * Get the key of the sprinkler
     *
     * @return key
     */
    String getKey();

    /**
     * Get the sprinkler config
     *
     * @return sprinkler config
     */
    Sprinkler getConfig();
}

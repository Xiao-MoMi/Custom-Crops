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

package net.momirealms.customcrops.api.object;

/**
 * Pot
 */
public interface CCPot {

    /**
     * Get the pot config key
     * @return key
     */
    String getKey();

    /**
     * Get the fertilizer inside the pot
     * @return fertilizer
     */
    CCFertilizer getFertilizer();

    /**
     * Set the fertilizer to the pot
     * @param fertilizer fertilizer
     */
    void setFertilizer(CCFertilizer fertilizer);

    /**
     * Get the water amount
     * @return water amount
     */
    int getWater();

    /**
     * Whether the pot is wet
     * @return wet or not
     */
    boolean isWet();

    /**
     * Add water to pot
     * @param amount water amount
     * @return whether the pot is previously dry
     */
    boolean addWater(int amount);

    /**
     * Set water amount
     * @param amount amount
     */
    void setWater(int amount);
}

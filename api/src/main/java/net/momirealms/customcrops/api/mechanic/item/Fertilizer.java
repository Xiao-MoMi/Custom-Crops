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

package net.momirealms.customcrops.api.mechanic.item;

import net.momirealms.customcrops.api.common.item.EventItem;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;

import java.util.HashSet;

public interface Fertilizer extends EventItem {

    /**
     * Get the key
     *
     * @return key
     */
    String getKey();

    /**
     * Get the item ID
     *
     * @return item ID
     */
    String getItemID();

    /**
     * Get the max times of usage
     *
     * @return the max times of usage
     */
    int getTimes();

    /**
     * Get the type of the fertilizer
     *
     * @return the type of the fertilizer
     */
    FertilizerType getFertilizerType();

    /**
     * Get the pot whitelist
     *
     * @return pot whitelist
     */
    HashSet<String> getPotWhitelist();

    /**
     * If the fertilizer can only be used before planting
     */
    boolean isBeforePlant();

    /**
     * Get the image of the fertilizer
     *
     * @return icon
     */
    String getIcon();

    /**
     * Get the requirements for this fertilizer
     *
     * @return requirements
     */
    Requirement[] getRequirements();
}

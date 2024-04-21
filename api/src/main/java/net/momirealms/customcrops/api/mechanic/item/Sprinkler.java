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

import net.momirealms.customcrops.api.common.item.KeyItem;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.water.PassiveFillMethod;
import net.momirealms.customcrops.api.mechanic.misc.image.WaterBar;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.mechanic.requirement.State;

import java.util.HashSet;

public interface Sprinkler extends KeyItem {

    /**
     * Get the 2D item ID
     *
     * @return 2D item ID
     */
    String get2DItemID();

    /**
     * Get the 3D item ID
     *
     * @return 3D item ID
     */
    String get3DItemID();

    /**
     * Get the 3D item ID (With water inside)
     *
     * @return 3D item ID (With water inside)
     */
    String get3DItemWithWater();

    /**
     * Get the max storage of water
     *
     * @return max storage of water
     */
    int getStorage();

    /**
     * Get the working range
     *
     * @return working range
     */
    int getRange();

    /**
     * Is water infinite
     */
    boolean isInfinite();

    /**
     * Get the amount of water to add to the pot during sprinkling
     *
     * @return amount of water to add to the pot during sprinkling
     */
    int getWater();

    /**
     * Get the pots that receive the water
     *
     * @return whitelisted pots
     */
    HashSet<String> getPotWhitelist();

    /**
     * Get the carrier of the pot
     *
     * @return carrier of the pot
     */
    ItemCarrier getItemCarrier();

    /**
     * Get methods to fill the sprinkler
     *
     * @return methods to fill the sprinkler
     */
    PassiveFillMethod[] getPassiveFillMethods();

    /**
     * Get the requirements for placement
     *
     * @return requirements for placement
     */
    Requirement[] getPlaceRequirements();

    /**
     * Get the requirements for breaking
     *
     * @return requirements for breaking
     */
    Requirement[] getBreakRequirements();

    /**
     * Get the requirements for using
     *
     * @return requirements for using
     */
    Requirement[] getUseRequirements();

    /**
     * Trigger actions
     *
     * @param trigger action trigger
     * @param state player state
     */
    void trigger(ActionTrigger trigger, State state);

    /**
     * Get the water bar images
     *
     * @return water bar images
     */
    WaterBar getWaterBar();
}

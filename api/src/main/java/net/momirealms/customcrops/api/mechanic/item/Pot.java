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

public interface Pot extends KeyItem {

    /**
     * Get max water storage
     *
     * @return water storage
     */
    int getStorage();

    /**
     * Get the key
     *
     * @return key
     */
    String getKey();

    /**
     * Get the blocks that belong to this pot
     *
     * @return blocks
     */
    HashSet<String> getPotBlocks();

    /**
     * Get the methods to fill this pot
     *
     * @return methods
     */
    PassiveFillMethod[] getPassiveFillMethods();

    /**
     * Get the dry state
     *
     * @return dry state item ID
     */
    String getDryItem();

    /**
     * Get the wet state
     *
     * @return wet state item ID
     */
    String getWetItem();

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

    /**
     * Does the pot absorb raindrop
     */
    boolean isRainDropAccepted();

    /**
     * Does nearby water make the pot wet
     */
    boolean isNearbyWaterAccepted();

    /**
     * Get the block ID by water and fertilizers
     *
     * @param water water
     * @param type the type of the fertilizer
     * @return block item ID
     */
    String getBlockState(boolean water, FertilizerType type);

    /**
     * Is the pot a vanilla block
     */
    boolean isVanillaBlock();

    /**
     * Is the id a wet pot
     */
    boolean isWetPot(String id);
}

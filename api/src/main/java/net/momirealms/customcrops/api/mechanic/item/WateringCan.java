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
import net.momirealms.customcrops.api.mechanic.misc.image.WaterBar;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface WateringCan extends KeyItem {

    /**
     * Get the ID of the item
     *
     * @return item ID
     */
    String getItemID();

    /**
     * Get the width of the effective range
     *
     * @return width
     */
    int getWidth();

    /**
     * Get the length of the effective range
     *
     * @return length
     */
    int getLength();

    /**
     * Get the storage of water
     *
     * @return storage of water
     */
    int getStorage();

    /**
     * Get the amount of water to add in one try
     */
    int getWater();

    /**
     * If the watering can has dynamic lore
     */
    boolean hasDynamicLore();

    /**
     * Update a watering can's data
     *
     * @param player player
     * @param itemStack watering can item
     * @param water the amount of water
     * @param args the placeholders
     */
    void updateItem(Player player, ItemStack itemStack, int water, Map<String, String> args);

    /**
     * Get the current water
     *
     * @param itemStack watering can item
     * @return current water
     */
    int getCurrentWater(ItemStack itemStack);

    /**
     * Get the pots that receive water from this watering can
     *
     * @return whitelisted pots
     */
    HashSet<String> getPotWhitelist();

    /**
     * Get the sprinklers that receive water from this watering can
     *
     * @return whitelisted sprinklers
     */
    HashSet<String> getSprinklerWhitelist();

    /**
     * Get the dynamic lores
     *
     * @return dynamic lores
     */
    List<String> getLore();

    /**
     * Get the water bar images
     *
     * @return water bar images
     */
    @Nullable WaterBar getWaterBar();

    /**
     * Get the requirements for using this watering can
     *
     * @return requirements
     */
    Requirement[] getRequirements();

    /**
     * If the water is infinite
     */
    boolean isInfinite();

    /**
     * Trigger actions
     *
     * @param trigger action trigger
     * @param state player state
     */
    void trigger(ActionTrigger trigger, State state);
}

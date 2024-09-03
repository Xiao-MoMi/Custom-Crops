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

package net.momirealms.customcrops.api.core;

import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.item.CustomCropsItem;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerType;
import net.momirealms.customcrops.common.util.Key;

/**
 * Interface defining methods for registering and accessing different types of custom crop mechanics
 * such as blocks, items, and fertilizer types in the CustomCrops plugin.
 */
public interface RegistryAccess {

    /**
     * Registers a new custom crop block mechanic.
     *
     * @param block The custom crop block to register.
     */
    void registerBlockMechanic(CustomCropsBlock block);

    /**
     * Registers a new custom crop item mechanic.
     *
     * @param item The custom crop item to register.
     */
    void registerItemMechanic(CustomCropsItem item);

    /**
     * Registers a new fertilizer type mechanic.
     *
     * @param type The fertilizer type to register.
     */
    void registerFertilizerType(FertilizerType type);

    /**
     * Retrieves the registry containing all registered custom crop blocks.
     *
     * @return the block registry
     */
    Registry<Key, CustomCropsBlock> getBlockRegistry();

    /**
     * Retrieves the registry containing all registered custom crop items.
     *
     * @return the item registry
     */
    Registry<Key, CustomCropsItem> getItemRegistry();

    /**
     * Retrieves the registry containing all registered fertilizer types.
     *
     * @return the fertilizer type registry
     */
    Registry<String, FertilizerType> getFertilizerTypeRegistry();
}

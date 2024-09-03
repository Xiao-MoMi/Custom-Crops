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

package net.momirealms.customcrops.api.core.world;

import com.flowpowered.nbt.Tag;
import net.momirealms.customcrops.api.core.SynchronizedCompoundMap;

/**
 * Interface representing a data block that can store, retrieve, and manipulate NBT data.
 */
public interface DataBlock {

    /**
     * Sets an NBT tag in the data block with the specified key.
     *
     * @param key The key for the tag to set.
     * @param tag The NBT tag to set.
     * @return The previous tag associated with the key, or null if there was no previous tag.
     */
    Tag<?> set(String key, Tag<?> tag);

    /**
     * Retrieves an NBT tag from the data block with the specified key.
     *
     * @param key The key of the tag to retrieve.
     * @return The NBT tag associated with the key, or null if no tag is found.
     */
    Tag<?> get(String key);

    /**
     * Removes an NBT tag from the data block with the specified key.
     *
     * @param key The key of the tag to remove.
     * @return The removed NBT tag, or null if no tag was found with the specified key.
     */
    Tag<?> remove(String key);

    /**
     * Gets the synchronized compound map containing all the NBT data of the block.
     *
     * @return The {@link SynchronizedCompoundMap} containing the block's NBT data.
     */
    SynchronizedCompoundMap compoundMap();
}

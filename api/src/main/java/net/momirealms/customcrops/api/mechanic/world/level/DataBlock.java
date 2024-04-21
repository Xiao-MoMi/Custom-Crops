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

import com.flowpowered.nbt.Tag;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.SynchronizedCompoundMap;

public interface DataBlock {

    /**
     * Set data by key
     *
     * @param key key
     * @param tag data tag
     */
    void setData(String key, Tag<?> tag);

    /**
     * Get data tag by key
     *
     * @param key key
     * @return data tag
     */
    Tag<?> getData(String key);

    /**
     * Get the data map
     *
     * @return data map
     */
    SynchronizedCompoundMap getCompoundMap();

    /**
     * Get the location of the block
     *
     * @return location
     */
    SimpleLocation getLocation();
}

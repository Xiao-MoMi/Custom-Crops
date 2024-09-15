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

package net.momirealms.customcrops.api.core.block;

import com.flowpowered.nbt.CompoundMap;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedBreakEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedPlaceEvent;
import net.momirealms.customcrops.api.misc.NamedTextColor;
import net.momirealms.customcrops.common.util.Key;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public interface CustomCropsBlock {

    /**
     * Get the key
     *
     * @return key
     */
    Key type();

    /**
     * Create a CustomCropsBlockState based on this type
     *
     * @return CustomCropsBlockState
     */
    CustomCropsBlockState createBlockState();

    /**
     * Create a CustomCropsBlockState based on the item id
     *
     * @return CustomCropsBlockState
     */
    @Nullable
    CustomCropsBlockState createBlockState(String itemID);

    /**
     * Create a CustomCropsBlockState based on this type and provided data
     *
     * @return CustomCropsBlockState
     */
    CustomCropsBlockState createBlockState(CompoundMap data);

    /**
     * Runs scheduled tick tasks
     */
    void scheduledTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location, boolean offlineTick);

    /**
     * Runs random tick tasks
     */
    void randomTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location, boolean offlineTick);

    /**
     * Handles interactions
     */
    void onInteract(WrappedInteractEvent event);

    /**
     * Handles breaks
     */
    void onBreak(WrappedBreakEvent event);

    /**
     * Handles placement
     */
    void onPlace(WrappedPlaceEvent event);

    /**
     * Checks if the id is an instance of this block type
     *
     * @param id id
     * @return is instance or not
     */
    boolean isInstance(String id);

    /**
     * Restores the bukkit block state or furniture based on the given block state
     *
     * @param location the location of the block
     * @param state    the provided state
     */
    void restore(Location location, CustomCropsBlockState state);

    /**
     * Get the color on insight mode
     *
     * @return the color
     */
    NamedTextColor insightColor();
}

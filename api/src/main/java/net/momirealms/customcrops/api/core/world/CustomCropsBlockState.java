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

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.util.TagUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Interface representing the state of a custom crops block in the CustomCrops plugin.
 */
public interface CustomCropsBlockState extends DataBlock {

    /**
     * Retrieves the type of the custom crops block associated with this state.
     *
     * @return The {@link CustomCropsBlock} type of this block state.
     */
    @NotNull
    CustomCropsBlock type();

    /**
     * Creates a new instance of {@link CustomCropsBlockState} with the given block type and NBT data.
     *
     * @param owner       The custom crops block type that owns this state.
     * @param compoundMap The NBT data associated with this block state.
     * @return A new instance of {@link CustomCropsBlockState} representing the specified block type and state.
     */
    static CustomCropsBlockState create(CustomCropsBlock owner, CompoundMap compoundMap) {
        return new CustomCropsBlockStateImpl(owner, compoundMap);
    }

    @ApiStatus.Internal
    static CustomCropsBlockState create(CustomCropsBlock owner, byte[] nbtBytes) {
        return new CustomCropsBlockStateImpl(owner, ((CompoundTag) TagUtils.fromBytes(nbtBytes)).getValue());
    }

    @ApiStatus.Internal
    byte[] getNBTDataAsBytes();

    String asString();
}

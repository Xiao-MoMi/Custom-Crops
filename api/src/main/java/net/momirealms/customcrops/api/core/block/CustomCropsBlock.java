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
import net.momirealms.customcrops.common.util.Key;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedBreakEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedPlaceEvent;

public interface CustomCropsBlock {

    Key type();

    CustomCropsBlockState createBlockState();

    CustomCropsBlockState createBlockState(CompoundMap data);

    void scheduledTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location);

    void randomTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location);

    void onInteract(WrappedInteractEvent event);

    void onBreak(WrappedBreakEvent event);

    void onPlace(WrappedPlaceEvent event);
}

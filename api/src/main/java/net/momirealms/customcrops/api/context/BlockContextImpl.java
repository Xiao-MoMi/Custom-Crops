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

package net.momirealms.customcrops.api.context;

import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class BlockContextImpl extends AbstractContext<CustomCropsBlockState> {

    public BlockContextImpl(@NotNull CustomCropsBlockState block, Location location, boolean sync) {
        super(block, sync);
        updateLocation(location);
    }

    @Override
    public String toString() {
        return "BlockContext{" +
                "args=" + args() +
                ", block=" + holder() +
                '}';
    }
}

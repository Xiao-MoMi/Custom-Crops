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

package net.momirealms.customcrops.mechanic.world.block;

import com.flowpowered.nbt.CompoundMap;
import net.momirealms.customcrops.api.mechanic.item.ItemType;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.AbstractCustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.level.WorldGlass;

import java.util.Objects;

public class MemoryGlass extends AbstractCustomCropsBlock implements WorldGlass {

    public MemoryGlass(SimpleLocation location) {
        super(location, new CompoundMap());
    }

    public MemoryGlass(SimpleLocation location, CompoundMap properties) {
        super(location, properties);
    }

    @Override
    public ItemType getType() {
        return ItemType.GREENHOUSE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCustomCropsBlock that = (AbstractCustomCropsBlock) o;
        return Objects.equals(getCompoundMap(), that.getCompoundMap());
    }

    @Override
    public int hashCode() {
        return 1821739123;
    }

    @Override
    public void tick(int interval) {

    }
}

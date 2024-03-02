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

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.Tag;

import java.util.HashMap;
import java.util.Objects;

public class AbstractPropertyItem implements PropertyItem {

    private final CompoundMap compoundMap;

    public AbstractPropertyItem(CompoundMap compoundMap) {
        this.compoundMap = compoundMap;
    }

    @Override
    public void setProperty(String key, Tag<?> tag) {
        compoundMap.put(key, tag);
    }

    @Override
    public Tag<?> getProperty(String name) {
        return compoundMap.get(name);
    }

    @Override
    public CompoundMap getCompoundMap() {
        return compoundMap;
    }
}

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
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.Tag;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.SynchronizedCompoundMap;

public class AbstractCustomCropsBlock implements DataBlock {

    private final SimpleLocation location;
    private final SynchronizedCompoundMap compoundMap;

    public AbstractCustomCropsBlock(SimpleLocation location, CompoundMap compoundMap) {
        this.compoundMap = new SynchronizedCompoundMap(compoundMap);
        this.location = location;
    }

    @Override
    public void setData(String key, Tag<?> tag) {
        compoundMap.put(key, tag);
    }

    @Override
    public Tag<?> getData(String name) {
        return compoundMap.get(name);
    }

    @Override
    public SynchronizedCompoundMap getCompoundMap() {
        return compoundMap;
    }

    @Override
    public SimpleLocation getLocation() {
        return location;
    }

    public boolean canTick(int interval) {
        if (interval == 1) {
            return true;
        }
        Tag<?> tag = getData("tick");
        int tick = 0;
        if (tag != null) {
            tick = tag.getAsIntTag().map(IntTag::getValue).orElse(0);
        }
        if (++tick >= interval) {
            setData("tick", new IntTag("tick", 0));
            return true;
        } else {
            setData("tick", new IntTag("tick", tick));
        }
        return false;
    }
}

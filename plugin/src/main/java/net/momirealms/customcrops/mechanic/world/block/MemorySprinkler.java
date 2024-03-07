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
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.StringTag;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.mechanic.item.ItemType;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.AbstractCustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsChunk;
import net.momirealms.customcrops.api.mechanic.world.level.WorldSprinkler;

import java.util.Objects;

public class MemorySprinkler extends AbstractCustomCropsBlock implements WorldSprinkler {

    public MemorySprinkler(SimpleLocation location, CompoundMap compoundMap) {
        super(location, compoundMap);
    }

    public MemorySprinkler(SimpleLocation location, String key, int water) {
        super(location, new CompoundMap());
        setData("water", new IntTag("water", water));
        setData("key", new StringTag("key", key));
    }

    @Override
    public int getWater() {
        return getData("water").getAsIntTag().map(IntTag::getValue).orElse(0);
    }

    @Override
    public void setWater(int water) {
        setData("water", new IntTag("water", water));
    }

    @Override
    public String getKey() {
        return getData("key").getAsStringTag()
                .map(StringTag::getValue)
                .orElse("");
    }

    @Override
    public Sprinkler getConfig() {
        return CustomCropsPlugin.get().getItemManager().getSprinklerByID(getKey());
    }

    @Override
    public ItemType getType() {
        return ItemType.SPRINKLER;
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
        return getKey().hashCode() + getWater() * 17;
    }

    @Override
    public void tick(int interval, CustomCropsChunk chunk) {

    }
}

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
import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.api.mechanic.item.ItemType;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.AbstractCustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsChunk;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;

import java.util.Objects;

public class MemoryCrop extends AbstractCustomCropsBlock implements WorldCrop {

    public MemoryCrop(SimpleLocation location, String key, int point) {
        super(location, new CompoundMap());
        setProperty("point", new IntTag("point", point));
        setProperty("key", new StringTag("key", key));
    }

    public MemoryCrop(SimpleLocation location, CompoundMap properties) {
        super(location, properties);
    }

    @Override
    public String getKey() {
        return getProperty("key").getAsStringTag()
                .map(StringTag::getValue)
                .orElse("");
    }

    @Override
    public int getPoint() {
        return getProperty("point").getAsIntTag().map(IntTag::getValue).orElse(0);
    }

    @Override
    public void setPoint(int point) {
        point = Math.min(point, getConfig().getMaxPoints());
        setProperty("point", new IntTag("point", point));
    }

    @Override
    public Crop getConfig() {
        return CustomCropsPlugin.get().getItemManager().getCropByID(getKey());
    }

    @Override
    public ItemType getType() {
        return ItemType.CROP;
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
        return getKey().hashCode() + getPoint() * 17;
    }

    @Override
    public void tick(int interval, CustomCropsChunk chunk) {

    }
}

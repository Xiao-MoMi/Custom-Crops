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
import net.momirealms.customcrops.api.common.Property;
import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.api.mechanic.item.ItemType;
import net.momirealms.customcrops.api.mechanic.world.level.AbstractPropertyItem;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;

import java.util.HashMap;
import java.util.Objects;

public class MemoryCrop extends AbstractPropertyItem implements WorldCrop {

    public MemoryCrop(String key, int point) {
        super(new CompoundMap());
        setProperty("point", new IntTag("point", point));
        setProperty("key", new StringTag("key", key));
    }

    public MemoryCrop(CompoundMap properties) {
        super(properties);
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
        AbstractPropertyItem that = (AbstractPropertyItem) o;
        return Objects.equals(getCompoundMap(), that.getCompoundMap());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode() + getPoint() * 17;
    }
}

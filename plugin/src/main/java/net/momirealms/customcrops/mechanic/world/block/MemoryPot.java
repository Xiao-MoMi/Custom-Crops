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
import com.flowpowered.nbt.Tag;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.mechanic.item.ItemType;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.world.level.AbstractPropertyItem;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;

public class MemoryPot extends AbstractPropertyItem implements WorldPot {

    public MemoryPot(CompoundMap compoundMap) {
        super(compoundMap);
    }

    public MemoryPot(String key) {
        super(new CompoundMap());
        setProperty("key", new StringTag("key", key));
        setProperty("water", new IntTag("water", 0));
        setProperty("fertilizer-times", new IntTag("fertilizer-times", 0));
    }

    @Override
    public String getKey() {
        return getProperty("key").getAsStringTag()
                .map(StringTag::getValue)
                .orElse("");
    }

    @Override
    public int getWater() {
        return getProperty("water").getAsIntTag().map(IntTag::getValue).orElse(0);
    }

    @Override
    public void setWater(int water) {
        setProperty("water", new IntTag("water", water));
    }

    @Override
    public String getFertilizer() {
        Tag<?> tag = getProperty("fertilizer");
        if (tag == null) return null;
        return tag.getAsStringTag()
                .map(StringTag::getValue)
                .orElse(null);
    }

    @Override
    public void setFertilizer(String fertilizer) {
        setProperty("fertilizer", new StringTag("fertilizer", fertilizer));
    }

    @Override
    public int getFertilizerTimes() {
        return getProperty("fertilizer-times").getAsIntTag().map(IntTag::getValue).orElse(0);
    }

    @Override
    public void setFertilizerTimes(int fertilizerTimes) {
        setProperty("fertilizer-times", new IntTag("fertilizer-times", fertilizerTimes));
    }

    @Override
    public Pot getConfig() {
        return CustomCropsPlugin.get().getItemManager().getPotByID(getKey());
    }

    @Override
    public ItemType getType() {
        return ItemType.POT;
    }
}

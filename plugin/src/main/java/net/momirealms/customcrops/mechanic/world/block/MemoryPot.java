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

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Property;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.world.level.AbstractPropertyItem;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;

import java.util.HashMap;

public class MemoryPot extends AbstractPropertyItem implements WorldPot {

    private int water;
    private String fertilizer;
    private int fertilizerTimes;

    public MemoryPot(String key, HashMap<String, Property<?>> properties) {
        super(key, properties);
    }

    @Override
    public int getWater() {
        return water;
    }

    @Override
    public void setWater(int water) {
        this.water = Math.min(water, getConfig().getStorage());
    }

    @Override
    public String getFertilizer() {
        return fertilizer;
    }

    @Override
    public void setFertilizer(String fertilizer) {
        this.fertilizer = fertilizer;
    }

    @Override
    public int getFertilizerTimes() {
        return fertilizerTimes;
    }

    @Override
    public void setFertilizerTimes(int fertilizerTimes) {
        this.fertilizerTimes = fertilizerTimes;
    }

    @Override
    public Pot getConfig() {
        return CustomCropsPlugin.get().getItemManager().getPotByID(getKey());
    }
}

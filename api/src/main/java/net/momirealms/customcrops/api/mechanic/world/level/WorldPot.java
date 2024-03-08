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

import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;

public interface WorldPot extends CustomCropsBlock {

    String getKey();

    int getWater();

    void setWater(int water);

    Fertilizer getFertilizer();

    void setFertilizer(Fertilizer fertilizer);

    void removeFertilizer();

    int getFertilizerTimes();

    void setFertilizerTimes(int times);

    Pot getConfig();

    void tickWater(CustomCropsChunk chunk);
}

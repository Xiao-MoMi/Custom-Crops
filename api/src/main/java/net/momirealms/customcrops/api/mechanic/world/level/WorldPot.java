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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WorldPot extends CustomCropsBlock {

    /**
     * Get the key of the pot
     *
     * @return key
     */
    String getKey();

    /**
     * Get the amount of water
     *
     * @return amount of water
     */
    int getWater();

    /**
     * Set the amount of water
     *
     * @param water water
     */
    void setWater(int water);

    /**
     * Get the fertilizer config
     *
     * @return fertilizer config
     */
    @Nullable
    Fertilizer getFertilizer();

    /**
     * Set the fertilizer
     *
     * @param fertilizer fertilizer
     */
    void setFertilizer(@NotNull Fertilizer fertilizer);

    /**
     * Remove the fertilizer
     */
    void removeFertilizer();

    /**
     * Get the remaining usages of the fertilizer
     *
     * @return remaining usages of the fertilizer
     */
    int getFertilizerTimes();

    /**
     * Set the remaining usages of the fertilizer
     *
     * @param times the remaining usages of the fertilizer
     */
    void setFertilizerTimes(int times);

    /**
     * Get the pot config
     *
     * @return pot config
     */
    Pot getConfig();

    /**
     * Tick the rainwater and nearby water
     */
    void tickWater();
}

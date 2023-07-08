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

package net.momirealms.customcrops.api.object.fertilizer;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.CCFertilizer;

import java.io.Serializable;

public class Fertilizer implements Serializable, CCFertilizer {

    private final String key;
    private int times;

    public Fertilizer(String key, int times) {
        this.key = key;
        this.times = times;
    }

    public Fertilizer(FertilizerConfig fertilizerConfig) {
        this.key = fertilizerConfig.getKey();
        this.times = fertilizerConfig.getTimes();
    }

    /*
    If fertilizer is used up
     */
    public boolean reduceTimes() {
        times--;
        return times <= 0;
    }

    public String getKey() {
        return key;
    }

    public FertilizerConfig getConfig() {
        return CustomCrops.getInstance().getFertilizerManager().getConfigByFertilizer(this);
    }

    public int getLeftTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}

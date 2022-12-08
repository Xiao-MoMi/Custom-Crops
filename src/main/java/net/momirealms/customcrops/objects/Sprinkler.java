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

package net.momirealms.customcrops.objects;

import org.jetbrains.annotations.Nullable;

public class Sprinkler {

    private int range;
    private int water;
    private final String key;
    private String twoD;
    private String threeD;

    public Sprinkler(String key, int range, int water) {
        this.range = range;
        this.water = water;
        this.key = key;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public String getKey() {
        return key;
    }

    @Nullable
    public String getTwoD() {
        return twoD;
    }

    public void setTwoD(String twoD) {
        this.twoD = twoD;
    }

    @Nullable
    public String getThreeD() {
        return threeD;
    }

    public void setThreeD(String threeD) {
        this.threeD = threeD;
    }
}

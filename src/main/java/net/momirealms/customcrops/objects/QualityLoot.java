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

public class QualityLoot {

    private final int min;
    private final int max;

    private final String quality_1;
    private final String quality_2;
    private final String quality_3;

    public QualityLoot(int min, int max, String quality_1, String quality_2, String quality_3) {
        this.quality_1 = quality_1;
        this.quality_2 = quality_2;
        this.quality_3 = quality_3;
        this.max = max;
        this.min = min;
    }

    public String getQuality_1() {
        return quality_1;
    }

    public String getQuality_2() {
        return quality_2;
    }

    public String getQuality_3() {
        return quality_3;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}

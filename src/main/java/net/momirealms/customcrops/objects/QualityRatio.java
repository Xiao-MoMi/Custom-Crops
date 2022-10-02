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

public class QualityRatio {

    private final double quality_1;
    private final double quality_2;

    public QualityRatio(double quality_1, double quality_2) {
        this.quality_1 = quality_1;
        this.quality_2 = quality_2;
    }

    public double getQuality_1() {
        return quality_1;
    }

    public double getQuality_2() {
        return quality_2;
    }
}

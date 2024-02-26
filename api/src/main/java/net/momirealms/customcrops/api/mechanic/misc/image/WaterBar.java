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

package net.momirealms.customcrops.api.mechanic.misc.image;

public record WaterBar(String left, String empty, String full, String right) {

    public static WaterBar of(String left, String empty, String full, String right) {
        return new WaterBar(left, empty, full, right);
    }

    public String getWaterBar(int current, int max) {
        return left +
                String.valueOf(full).repeat(current) +
                String.valueOf(empty).repeat(Math.max(max - current, 0)) +
                right;
    }
}

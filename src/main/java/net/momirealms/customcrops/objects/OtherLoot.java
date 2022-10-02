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

public class OtherLoot {

    private final int min;
    private final int max;
    private final String itemID;
    private final double chance;

    public OtherLoot(int min, int max, String itemID, double chance) {
        this.min = min;
        this.max = max;
        this.itemID = itemID;
        this.chance = chance;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String getItemID() {
        return itemID;
    }

    public double getChance() {
        return chance;
    }
}
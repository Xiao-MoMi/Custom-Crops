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

package net.momirealms.customcrops.objects.fertilizer;

public class YieldIncreasing extends Fertilizer {

    private final int bonus;

    public YieldIncreasing(String key, int times, double chance ,int bonus, boolean before, String name) {
        super(key, times, chance, before, name);
        this.bonus = bonus;
    }

    public int getBonus() {
        return bonus;
    }

    @Override
    public Fertilizer getWithTimes(int times) {
        return new YieldIncreasing(this.key, times, this.chance, this.bonus, this.before, this.name);
    }
}

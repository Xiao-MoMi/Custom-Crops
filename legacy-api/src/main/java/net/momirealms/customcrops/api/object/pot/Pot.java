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

package net.momirealms.customcrops.api.object.pot;

import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;

import java.io.Serial;
import java.io.Serializable;

@Deprecated
public class Pot implements Serializable {

    @Serial
    private static final long serialVersionUID = -6598493908660891824L;

    private Fertilizer fertilizer;
    private int water;
    private final String key;

    public Pot(String key, Fertilizer fertilizer, int water) {
        this.key = key;
        this.fertilizer = fertilizer;
        this.water = water;
    }

    public Fertilizer getFertilizer() {
        return fertilizer;
    }

    public int getWater() {
        return water;
    }

    public String getKey() {
        return key;
    }
}
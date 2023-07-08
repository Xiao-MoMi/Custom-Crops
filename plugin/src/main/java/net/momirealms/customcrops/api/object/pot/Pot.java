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

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.CCFertilizer;
import net.momirealms.customcrops.api.object.CCPot;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class Pot implements Serializable, CCPot {

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

    @Override
    public void setFertilizer(CCFertilizer fertilizer) {
        setFertilizer((Fertilizer) fertilizer);
    }

    public void setFertilizer(Fertilizer fertilizer) {
        this.fertilizer = fertilizer;
    }

    public int getWater() {
        return water;
    }

    /*
    whether to change block model
     */
    public boolean addWater(int amount) {
        if (water == 0) {
            this.water = Math.min(getConfig().getMaxStorage(), amount);
            return true;
        } else {
            this.water = Math.min(getConfig().getMaxStorage(), water + amount);
            return false;
        }
    }

    public void setWater(int amount) {
        this.water = amount;
    }

    /*
    whether to change block model
     */
    public boolean reduceWater() {
        if (water == 0) return false;
        water--;
        water = Math.max(0, water);
        return water == 0;
    }

    /*
    whether to change block model
     */
    public boolean reduceFertilizer() {
        if (this.fertilizer != null && fertilizer.reduceTimes()) {
            this.fertilizer = null;
            return true;
        }
        return false;
    }

    public boolean isWet() {
        return water != 0;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    public PotConfig getConfig() {
        return CustomCrops.getInstance().getPotManager().getPotConfig(key);
    }
}
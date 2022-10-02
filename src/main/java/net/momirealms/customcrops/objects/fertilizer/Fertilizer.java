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

import org.bukkit.Particle;

public abstract class Fertilizer {

    String key;
    int times;
    boolean before;
    String name;
    Particle particle;
    double chance;

    protected Fertilizer(String key, int times, double chance, boolean before, String name) {
        this.key = key;
        this.times = times;
        this.chance = chance;
        this.before = before;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public boolean isBefore() {
        return before;
    }

    public String getName() {
        return name;
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public Fertilizer getWithTimes(int times) {
        return null;
    }

    public double getChance() {
        return chance;
    }
}

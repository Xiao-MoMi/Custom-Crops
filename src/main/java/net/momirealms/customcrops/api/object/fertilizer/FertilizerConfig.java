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

import net.kyori.adventure.sound.Sound;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

public abstract class FertilizerConfig {

    protected int times;
    protected double chance;
    protected String key;
    protected FertilizerType fertilizerType;
    protected String[] pot_whitelist;
    protected boolean beforePlant;
    protected Particle particle;
    protected Sound sound;

    public FertilizerConfig(String key, FertilizerType fertilizerType, int times, double chance, @Nullable String[] pot_whitelist, boolean beforePlant, @Nullable Particle particle, @Nullable Sound sound) {
        this.times = times;
        this.chance = chance;
        this.key = key;
        this.fertilizerType = fertilizerType;
        this.pot_whitelist = pot_whitelist;
        this.beforePlant = beforePlant;
        this.particle = particle;
        this.sound = sound;
    }

    public int getTimes() {
        return times;
    }

    public double getChance() {
        return chance;
    }

    public boolean canTakeEffect() {
        return Math.random() < chance;
    }

    public String getKey() {
        return key;
    }

    public FertilizerType getFertilizerType() {
        return fertilizerType;
    }

    @Nullable
    public String[] getPotWhitelist() {
        return pot_whitelist;
    }

    public boolean isBeforePlant() {
        return beforePlant;
    }

    @Nullable
    public Particle getParticle() {
        return particle;
    }

    @Nullable
    public Sound getSound() {
        return sound;
    }
}

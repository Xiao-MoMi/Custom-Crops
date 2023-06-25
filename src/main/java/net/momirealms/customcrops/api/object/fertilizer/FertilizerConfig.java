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
import net.momirealms.customcrops.api.object.requirement.Requirement;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

public abstract class FertilizerConfig {

    private final int times;
    private final double chance;
    private final String key;
    private final FertilizerType fertilizerType;
    private final String[] pot_whitelist;
    private final boolean beforePlant;
    private final Particle particle;
    private final Sound sound;
    private final String icon;
    private final Requirement[] requirements;

    public FertilizerConfig(
            String key,
            FertilizerType fertilizerType,
            int times,
            double chance,
            @Nullable String[] pot_whitelist,
            boolean beforePlant,
            @Nullable Particle particle,
            @Nullable Sound sound,
            @Nullable String icon,
            Requirement[] requirements
    ) {
        this.times = times;
        this.chance = chance;
        this.key = key;
        this.fertilizerType = fertilizerType;
        this.pot_whitelist = pot_whitelist;
        this.beforePlant = beforePlant;
        this.particle = particle;
        this.sound = sound;
        this.icon = icon;
        this.requirements = requirements;
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

    public String getIcon() {
        return icon;
    }

    public Requirement[] getRequirements() {
        return requirements;
    }
}

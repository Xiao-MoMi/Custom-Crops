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
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.requirement.Requirement;
import net.momirealms.customcrops.util.AdventureUtils;
import org.bukkit.Particle;
import org.jetbrains.annotations.Nullable;

public class Quality extends FertilizerConfig {

    private final double[] ratio;

    public Quality(
            String key,
            FertilizerType fertilizerType,
            int times,
            double chance,
            double[] ratio,
            @Nullable String[] pot_whitelist,
            boolean beforePlant,
            @Nullable Particle particle,
            @Nullable Sound sound,
            @Nullable String icon,
            Requirement[] requirements
    ) {
        super(key, fertilizerType, times, chance, pot_whitelist, beforePlant, particle, sound, icon, requirements);
        this.ratio = ratio;
        if (this.ratio.length != ConfigManager.defaultRatio.length) {
            AdventureUtils.consoleMessage("<red>[CustomCrops] Wrong format found at fertilizer: " + key + ". You should make sure that all the quality ratio are in the same format. For example when you set default-ratio to x/x/x/x/x in config.yml. You should set ratio to x/x/x/x/x in fertilizers to work");
        }
    }

    public double[] getRatio() {
        return ratio;
    }
}

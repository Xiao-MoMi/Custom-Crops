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

package net.momirealms.customcrops.config;

import net.momirealms.customcrops.objects.QualityRatio;
import net.momirealms.customcrops.objects.fertilizer.*;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Objects;

public class FertilizerConfig {

    public static HashMap<String, Fertilizer> FERTILIZERS;

    public static void load() {
        FERTILIZERS = new HashMap<>(16);
        YamlConfiguration config = ConfigUtil.getConfig("fertilizers_" + MainConfig.customPlugin + ".yml");
        for (String key : config.getKeys(false)) {
            switch (key) {
                case "speed" -> {
                    for (String fertilizer : Objects.requireNonNull(config.getConfigurationSection(key)).getKeys(false)) {
                        SpeedGrow speedGrow = new SpeedGrow(
                                fertilizer,
                                config.getInt(key + "." + fertilizer + ".times", 14),
                                config.getDouble(key + "." +fertilizer + ".chance", 0.01),
                                config.getBoolean(key + "." + fertilizer + ".before-plant", true),
                                config.getString(key + "." + fertilizer + ".name")
                        );
                        if (config.contains(key + "." + fertilizer + ".particle")) {
                            speedGrow.setParticle(Particle.valueOf(config.getString(key + "." + fertilizer + ".particle", "VILLAGER_HAPPY").toUpperCase()));
                        }
                        FERTILIZERS.put(config.getString(key + "." + fertilizer + ".item"), speedGrow);
                        FERTILIZERS.put(fertilizer, speedGrow);
                    }
                }
                case "gigantic" -> {
                    for (String fertilizer : Objects.requireNonNull(config.getConfigurationSection(key)).getKeys(false)) {
                        Gigantic gigantic = new Gigantic(
                                fertilizer,
                                config.getInt(key + "." + fertilizer + ".times", 14),
                                config.getDouble(key + "." +fertilizer + ".chance", 0.01),
                                config.getBoolean(key + "." + fertilizer + ".before-plant", true),
                                config.getString(key + "." + fertilizer + ".name")
                        );
                        if (config.contains(key + "." + fertilizer + ".particle")) {
                            gigantic.setParticle(Particle.valueOf(config.getString(key + "." + fertilizer + ".particle", "VILLAGER_HAPPY").toUpperCase()));
                        }
                        FERTILIZERS.put(config.getString(key + "." + fertilizer + ".item"), gigantic);
                        FERTILIZERS.put(fertilizer, gigantic);
                    }
                }
                case "retaining" -> {
                    for (String fertilizer : Objects.requireNonNull(config.getConfigurationSection(key)).getKeys(false)) {
                        RetainingSoil retainingSoil = new RetainingSoil(
                                fertilizer,
                                config.getInt(key + "." + fertilizer + ".times", 14),
                                config.getDouble(key + "." +fertilizer + ".chance", 0.01),
                                config.getBoolean(key + "." + fertilizer + ".before-plant", true),
                                config.getString(key + "." + fertilizer + ".name")
                        );
                        if (config.contains(key + "." + fertilizer + ".particle")) {
                            retainingSoil.setParticle(Particle.valueOf(config.getString(key + "." + fertilizer + ".particle", "VILLAGER_HAPPY").toUpperCase()));
                        }
                        FERTILIZERS.put(config.getString(key + "." + fertilizer + ".item"), retainingSoil);
                        FERTILIZERS.put(fertilizer, retainingSoil);
                    }
                }
                case "quantity" -> {
                    for (String fertilizer : Objects.requireNonNull(config.getConfigurationSection(key)).getKeys(false)) {
                        YieldIncreasing yieldIncreasing = new YieldIncreasing(
                                fertilizer,
                                config.getInt(key + "." + fertilizer + ".times", 14),
                                config.getDouble(key + "." +fertilizer + ".chance", 0.01),
                                config.getInt(key + "." +fertilizer + ".bonus",1),
                                config.getBoolean(key + "." + fertilizer + ".before-plant", true),
                                config.getString(key + "." + fertilizer + ".name")
                        );
                        if (config.contains(key + "." + fertilizer + ".particle")) {
                            yieldIncreasing.setParticle(Particle.valueOf(config.getString(key + "." + fertilizer + ".particle", "VILLAGER_HAPPY").toUpperCase()));
                        }
                        FERTILIZERS.put(config.getString(key + "." + fertilizer + ".item"), yieldIncreasing);
                        FERTILIZERS.put(fertilizer, yieldIncreasing);
                    }
                }
                case "quality" -> {
                    for (String fertilizer : Objects.requireNonNull(config.getConfigurationSection(key)).getKeys(false)) {
                        String[] split = StringUtils.split(config.getString(key + "." + fertilizer + ".ratio"), "/");
                        double[] weight = new double[3];
                        weight[0] = Double.parseDouble(split[0]);
                        weight[1] = Double.parseDouble(split[1]);
                        weight[2] = Double.parseDouble(split[2]);
                        double weightTotal = weight[0] + weight[1] + weight[2];
                        QualityRatio qualityRatio = new QualityRatio(weight[0]/(weightTotal), 1 - weight[1]/(weightTotal));
                        QualityCrop qualityCrop = new QualityCrop(
                                fertilizer,
                                config.getInt(key + "." + fertilizer + ".times", 14),
                                config.getDouble(key + "." +fertilizer + ".chance", 0.01),
                                qualityRatio,
                                config.getBoolean(key + "." + fertilizer + ".before-plant", true),
                                config.getString(key + "." + fertilizer + ".name")
                        );
                        if (config.contains(key + "." + fertilizer + ".particle")) {
                            qualityCrop.setParticle(Particle.valueOf(config.getString(key + "." + fertilizer + ".particle", "VILLAGER_HAPPY").toUpperCase()));
                        }
                        FERTILIZERS.put(config.getString(key + "." + fertilizer + ".item"), qualityCrop);
                        FERTILIZERS.put(fertilizer, qualityCrop);
                    }
                }
            }
        }
        AdventureUtil.consoleMessage("[CustomCrops] Loaded <green>" + FERTILIZERS.size() / 2 + "<gray> fertilizers");
    }
}

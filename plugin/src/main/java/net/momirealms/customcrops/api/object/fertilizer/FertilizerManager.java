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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.Pair;
import net.momirealms.customcrops.api.object.requirement.Requirement;
import net.momirealms.customcrops.util.AdventureUtils;
import net.momirealms.customcrops.util.ConfigUtils;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class FertilizerManager extends Function {

    private final CustomCrops plugin;
    private final HashMap<String, FertilizerConfig> fertilizerConfigMap;
    private final HashMap<String, String> itemToKey;

    public FertilizerManager(CustomCrops plugin) {
        this.plugin = plugin;
        this.fertilizerConfigMap = new HashMap<>();
        this.itemToKey = new HashMap<>();
    }

    @Override
    public void load() {
        this.loadConfig();
    }

    @Override
    public void unload() {
        this.fertilizerConfigMap.clear();
        this.itemToKey.clear();
    }

    @Nullable
    public FertilizerConfig getConfigByFertilizer(@Nullable Fertilizer fertilizer) {
        if (fertilizer == null) return null;
        return fertilizerConfigMap.get(fertilizer.getKey());
    }

    @Nullable
    public FertilizerConfig getConfigByKey(String key) {
        return fertilizerConfigMap.get(key);
    }

    @Nullable
    public FertilizerConfig getConfigByItemID(String id) {
        String key = itemToKey.get(id);
        if (key == null) return null;
        return getConfigByKey(key);
    }

    private void loadConfig() {
        File can_folder = new File(plugin.getDataFolder(), "contents" + File.separator + "fertilizers");
        if (!can_folder.exists()) {
            if (!can_folder.mkdirs()) return;
            ConfigUtils.getConfig("contents" + File.separator + "fertilizers" + File.separator + "speed-grow.yml");
            ConfigUtils.getConfig("contents" + File.separator + "fertilizers" + File.separator + "quality.yml");
            ConfigUtils.getConfig("contents" + File.separator + "fertilizers" + File.separator + "soil-retain.yml");
            ConfigUtils.getConfig("contents" + File.separator + "fertilizers" + File.separator + "yield-increase.yml");
            ConfigUtils.getConfig("contents" + File.separator + "fertilizers" + File.separator + "variation.yml");
        }
        File[] files = can_folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String key : config.getKeys(false)) {
                ConfigurationSection fertilizerSec = config.getConfigurationSection(key);
                if (fertilizerSec == null) continue;
                FertilizerConfig fertilizerConfig;
                FertilizerType fertilizerType = FertilizerType.valueOf(fertilizerSec.getString("type", "SPEED_GROW").toUpperCase(Locale.ENGLISH));
                String[] pot_whitelist = fertilizerSec.contains("pot-whitelist") ? fertilizerSec.getStringList("pot-whitelist").toArray(new String[0]) : null;
                boolean beforePlant = fertilizerSec.getBoolean("before-plant", false);
                int times = fertilizerSec.getInt("times", 14);
                Particle particle = fertilizerSec.contains("particle") ? Particle.valueOf(fertilizerSec.getString("particle")) : null;
                @Subst("namespace:key") String soundKey = fertilizerSec.getString("sound", "minecraft:item.hoe.till");
                Sound sound = fertilizerSec.contains("sound") ? Sound.sound(Key.key(soundKey), Sound.Source.PLAYER, 1, 1) : null;
                String icon = fertilizerSec.getString("icon");
                Requirement[] requirements = ConfigUtils.getRequirementsWithMsg(fertilizerSec.getConfigurationSection("requirements"));
                switch (fertilizerType) {
                    case SPEED_GROW -> fertilizerConfig = new SpeedGrow(key, fertilizerType, times, getChancePair(fertilizerSec), pot_whitelist, beforePlant, particle, sound, icon, requirements);
                    case YIELD_INCREASE -> fertilizerConfig = new YieldIncrease(key, fertilizerType, times, fertilizerSec.getDouble("chance"), getChancePair(fertilizerSec), pot_whitelist, beforePlant, particle, sound, icon, requirements);
                    case VARIATION -> fertilizerConfig = new Variation(key, fertilizerType, times, fertilizerSec.getDouble("chance"), pot_whitelist, beforePlant, particle, sound, icon, requirements);
                    case QUALITY -> fertilizerConfig = new Quality(key, fertilizerType, times, fertilizerSec.getDouble("chance"), ConfigUtils.getQualityRatio(fertilizerSec.getString("ratio", "2/2/1")), pot_whitelist, beforePlant, particle, sound, icon, requirements);
                    case SOIL_RETAIN -> fertilizerConfig = new SoilRetain(key, fertilizerType, times, fertilizerSec.getDouble("chance"), pot_whitelist, beforePlant, particle, sound, icon, requirements);
                    default -> fertilizerConfig = null;
                }
                String item = fertilizerSec.getString("item");
                if (fertilizerConfig != null && item != null) {
                    fertilizerConfigMap.put(key, fertilizerConfig);
                    itemToKey.put(item, key);
                }
                else
                    AdventureUtils.consoleMessage("<red>[CustomCrops] Invalid fertilizer: " + key);
            }
        }
        AdventureUtils.consoleMessage("[CustomCrops] Loaded <green>" + fertilizerConfigMap.size() + " <gray>fertilizer(s)");
    }

    public ArrayList<Pair<Double, Integer>> getChancePair(ConfigurationSection fertilizerSec) {
        ArrayList<Pair<Double, Integer>> pairs = new ArrayList<>();
        ConfigurationSection effectSec = fertilizerSec.getConfigurationSection("chance");
        if (effectSec == null) return new ArrayList<>();
        for (String point : effectSec.getKeys(false)) {
            Pair<Double, Integer> pair = new Pair<>(effectSec.getDouble(point), Integer.parseInt(point));
            pairs.add(pair);
        }
        return pairs;
    }
}

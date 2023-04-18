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
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.fertilizer.FertilizerType;
import net.momirealms.customcrops.api.object.fill.PassiveFillMethod;
import net.momirealms.customcrops.api.object.hologram.FertilizerHologram;
import net.momirealms.customcrops.api.object.hologram.HologramManager;
import net.momirealms.customcrops.api.object.hologram.TextDisplayMeta;
import net.momirealms.customcrops.api.object.hologram.WaterAmountHologram;
import net.momirealms.customcrops.api.util.AdventureUtils;
import net.momirealms.customcrops.api.util.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;

public class PotManager extends Function {

    private final CustomCrops plugin;
    private final HashMap<String, PotConfig> potConfigMap;
    private final HashMap<String, String> blockToPotKey;

    public PotManager(CustomCrops plugin) {
        this.plugin = plugin;
        this.potConfigMap = new HashMap<>();
        this.blockToPotKey = new HashMap<>();
    }

    @Override
    public void load() {
        loadConfig();
    }

    @Override
    public void unload() {
        this.potConfigMap.clear();
        this.blockToPotKey.clear();
    }

    private void loadConfig() {
        File pot_folder = new File(plugin.getDataFolder(), "contents" + File.separator + "pots");
        if (!pot_folder.exists()) {
            if (!pot_folder.mkdirs()) return;
            plugin.saveResource("contents" + File.separator + "pots" + File.separator + "default.yml", false);
        }
        File[] files = pot_folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String key : config.getKeys(false)) {
                ConfigurationSection section = config.getConfigurationSection(key);
                if (section == null) continue;
                boolean enableFertilized = section.getBoolean("fertilized-pots.enable", false);
                String base_dry = section.getString("base.dry");
                String base_wet = section.getString("base.wet");
                if (base_wet == null || base_dry == null) {
                    AdventureUtils.consoleMessage("<red>[CustomCrops] base.dry/base.wet is not correctly set for pot: " + key);
                    continue;
                }
                PassiveFillMethod[] methods = ConfigUtils.getPassiveFillMethods(section.getConfigurationSection("fill-method"));
                if (methods == null) {
                    AdventureUtils.consoleMessage("<red>[CustomCrops] fill method is not set for pot: " + key);
                    continue;
                }
                blockToPotKey.put(base_wet, key);
                blockToPotKey.put(base_dry, key);
                PotConfig potConfig = new PotConfig(
                        section.getInt("max-water-storage"),
                        base_dry,
                        base_wet,
                        enableFertilized,
                        methods,
                        section.getBoolean("hologram.fertilizer.enable", false) ? new FertilizerHologram(
                                section.getString("hologram.fertilizer.content", ""),
                                section.getDouble("hologram.fertilizer.vertical-offset"),
                                HologramManager.Mode.valueOf(section.getString("hologram.type", "ARMOR_STAND").toUpperCase()),
                                section.getInt("hologram.duration"),
                                new TextDisplayMeta(
                                        section.getBoolean("hologram.text-display-options.has-shadow", false),
                                        section.getBoolean("hologram.text-display-options.is-see-through", false),
                                        section.getBoolean("hologram.text-display-options.use-default-background-color", false),
                                        ConfigUtils.rgbToDecimal(section.getString("hologram.text-display-options.background-color", "0,0,0,128")),
                                        (byte) section.getInt("hologram.text-display-options.text-opacity")
                                )
                        ) : null,
                        section.getBoolean("hologram.water.enable", false) ? new WaterAmountHologram(
                                section.getString("hologram.water.content", ""),
                                section.getDouble("hologram.water.vertical-offset"),
                                HologramManager.Mode.valueOf(section.getString("hologram.type", "ARMOR_STAND").toUpperCase()),
                                section.getInt("hologram.duration"),
                                section.getString("hologram.water.water-bar.left"),
                                section.getString("hologram.water.water-bar.full"),
                                section.getString("hologram.water.water-bar.empty"),
                                section.getString("hologram.water.water-bar.right"),
                                new TextDisplayMeta(
                                        section.getBoolean("hologram.text-display-options.has-shadow", false),
                                        section.getBoolean("hologram.text-display-options.is-see-through", false),
                                        section.getBoolean("hologram.text-display-options.use-default-background-color", false),
                                        ConfigUtils.rgbToDecimal(section.getString("hologram.text-display-options.background-color", "0,0,0,128")),
                                        (byte) section.getInt("hologram.text-display-options.text-opacity")
                                )
                        ) : null,
                        section.getString("hologram.require-item")
                );
                if (enableFertilized) {
                    ConfigurationSection fertilizedSec = section.getConfigurationSection("fertilized-pots");
                    if (fertilizedSec == null) continue;
                    for (String type : fertilizedSec.getKeys(false)) {
                        if (type.equals("enable")) continue;
                        String dry = fertilizedSec.getString(type + ".dry");
                        String wet = fertilizedSec.getString(type + ".wet");
                        blockToPotKey.put(dry, key);
                        blockToPotKey.put(wet, key);
                        switch (type) {
                            case "quality" -> potConfig.registerFertilizedPot(FertilizerType.QUALITY, dry, wet);
                            case "yield-increase" -> potConfig.registerFertilizedPot(FertilizerType.YIELD_INCREASE, dry, wet);
                            case "variation" -> potConfig.registerFertilizedPot(FertilizerType.VARIATION, dry, wet);
                            case "soil-retain" -> potConfig.registerFertilizedPot(FertilizerType.SOIL_RETAIN, dry, wet);
                            case "speed-grow" -> potConfig.registerFertilizedPot(FertilizerType.SPEED_GROW, dry, wet);
                        }
                    }
                }
                potConfigMap.put(key, potConfig);
            }
        }
        AdventureUtils.consoleMessage("[CustomCrops] Loaded <green>" + potConfigMap.size() + " <gray>pot(s)");
    }

    public boolean containsPotBlock(String id) {
        return blockToPotKey.containsKey(id);
    }

    @Nullable
    public PotConfig getPotConfig(String key) {
        return potConfigMap.get(key);
    }

    @Nullable
    public String getPotKeyByBlockID(String id) {
        return blockToPotKey.get(id);
    }

    @Nullable
    public PotConfig getPotConfigByBlockID(String id) {
        String key = blockToPotKey.get(id);
        if (key == null) return null;
        return potConfigMap.get(key);
    }
}

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

package net.momirealms.customcrops.api.object.crop;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.InteractCrop;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.condition.Condition;
import net.momirealms.customcrops.api.object.condition.DeathCondition;
import net.momirealms.customcrops.api.object.requirement.Requirement;
import net.momirealms.customcrops.customplugin.Platform;
import net.momirealms.customcrops.util.AdventureUtils;
import net.momirealms.customcrops.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class CropManager extends Function implements Listener {

    private final CustomCrops plugin;
    private final HashMap<String, String> stageToCrop;
    private final HashMap<String, CropConfig> seedToCropConfig;
    private final HashMap<String, CropConfig> cropConfigMap;
    private final HashMap<String, StageConfig> stageConfigMap;
    private final HashSet<String> deadCrops;
    private boolean hasCheckedTripwire;

    public CropManager(CustomCrops plugin) {
        this.plugin = plugin;
        this.stageToCrop = new HashMap<>();
        this.cropConfigMap = new HashMap<>();
        this.stageConfigMap = new HashMap<>();
        this.seedToCropConfig = new HashMap<>();
        this.deadCrops = new HashSet<>();
    }

    @Override
    public void load() {
        this.loadConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void unload() {
        this.stageToCrop.clear();
        this.cropConfigMap.clear();
        this.stageConfigMap.clear();
        this.deadCrops.clear();
        this.seedToCropConfig.clear();
        HandlerList.unregisterAll(this);
    }

    private void loadConfig() {
        File crop_folder = new File(plugin.getDataFolder(), "contents" + File.separator + "crops");
        if (!crop_folder.exists()) {
            if (!crop_folder.mkdirs()) return;
            ConfigUtils.getConfig("contents" + File.separator + "crops" + File.separator + "tomato.yml");
        }
        File[] files = crop_folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String key : config.getKeys(false)) {
                ConfigurationSection cropSec = config.getConfigurationSection(key);
                if (cropSec == null) continue;
                ItemMode itemMode = ItemMode.valueOf(cropSec.getString("type", "TripWire").toUpperCase(Locale.ENGLISH));
                if (itemMode == ItemMode.TRIPWIRE && !hasCheckedTripwire) {
                    checkTripwire();
                }
                String[] bottomBlocks = cropSec.getStringList("pot-whitelist").toArray(new String[0]);
                if (bottomBlocks.length == 0) {
                    AdventureUtils.consoleMessage("<red>[CustomCrops] pot-whitelist is not set for crop: " + key);
                    continue;
                }

                String seed = cropSec.getString("seed");
                Requirement[] breakReq = ConfigUtils.getRequirementsWithMsg(cropSec.getConfigurationSection("requirements.break"));
                Requirement[] plantReq = ConfigUtils.getRequirementsWithMsg(cropSec.getConfigurationSection("requirements.plant"));

                int max = cropSec.getInt("max-points", 0);
                ConfigurationSection pointSec = cropSec.getConfigurationSection("points");
                if (pointSec == null || max == 0) {
                    AdventureUtils.consoleMessage("<red>[CustomCrops] Points are not set for crop: " + key);
                    continue;
                }
                HashMap<Integer, StageConfig> stageMap = new HashMap<>();
                for (String point : pointSec.getKeys(false)) {
                    try {
                        int parsed = Integer.parseInt(point);
                        String stageModel = pointSec.getString(point + ".model");
                        StageConfig stageConfig = new StageConfig(
                                parsed,
                                stageModel,
                                ConfigUtils.getActions(pointSec.getConfigurationSection(point + ".events.break"), stageModel),
                                ConfigUtils.getActions(pointSec.getConfigurationSection(point + ".events.grow"), stageModel),
                                ConfigUtils.getInteractActions(pointSec.getConfigurationSection(point + ".events.interact-with-item"), stageModel),
                                pointSec.contains(point + ".events.interact-by-hand") ? new InteractCrop(
                                        "AIR",
                                        false,
                                        null,
                                        ConfigUtils.getActions(pointSec.getConfigurationSection(point + ".events.interact-by-hand"), stageModel),
                                        ConfigUtils.getRequirementsWithMsg(pointSec.getConfigurationSection(point + ".events.interact-by-hand.requirements"))
                                ) : null,
                                pointSec.getDouble(point + ".hologram-offset-correction", 0d)
                        );
                        stageMap.put(parsed, stageConfig);
                        if (stageModel != null) {
                            stageToCrop.put(stageModel, key);
                            stageConfigMap.put(stageModel, stageConfig);
                        }
                    }
                    catch (NumberFormatException e) {
                        AdventureUtils.consoleMessage("<red>[CustomCrops] Unexpected point value: " + point);
                    }
                }
                DeathCondition[] deathConditions = ConfigUtils.getDeathConditions(cropSec.getConfigurationSection("death-conditions"));
                Condition[] growConditions = ConfigUtils.getConditions(cropSec.getConfigurationSection("grow-conditions"));
                CropConfig cropConfig = new CropConfig(
                        key,
                        itemMode,
                        max,
                        bottomBlocks,
                        plantReq,
                        breakReq,
                        deathConditions,
                        growConditions,
                        stageMap,
                        ConfigUtils.getBoneMeals(cropSec.getConfigurationSection("custom-bone-meal")),
                        ConfigUtils.getActions(cropSec.getConfigurationSection("plant-actions"), null),
                        cropSec.getBoolean("random-rotation", false)
                );
                cropConfigMap.put(key, cropConfig);
                if (seed != null) seedToCropConfig.put(seed, cropConfig);
            }
        }
        AdventureUtils.consoleMessage("[CustomCrops] Loaded <green>" + cropConfigMap.size() + " <gray>crop(s)");
    }

    @Nullable
    public StageConfig getStageConfig(String stage_id) {
        return stageConfigMap.get(stage_id);
    }

    @Nullable
    public CropConfig getCropConfigByID(String id) {
        return this.cropConfigMap.get(id);
    }

    @Nullable
    public CropConfig getCropConfigByStage(String stage_id) {
        String key = getCropConfigID(stage_id);
        if (key == null) return null;
        return this.cropConfigMap.get(key);
    }

    @Nullable
    public String getCropConfigID(String stage_id) {
        return this.stageToCrop.get(stage_id);
    }

    public boolean isDeadCrop(String id) {
        return deadCrops.contains(id);
    }

    public boolean containsStage(String stage_id) {
        return stageToCrop.containsKey(stage_id);
    }

    // Prevent players from getting stage model
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (event.isCancelled()) return;
        Item item = event.getEntity();
        String id = plugin.getPlatformInterface().getItemStackID(item.getItemStack());
        if (containsStage(id) || isDeadCrop(id)) {
            event.setCancelled(true);
        }
    }

    public void registerDeadCrops(String id) {
        this.deadCrops.add(id);
    }

    @Nullable
    public CropConfig getCropConfigBySeed(String seed) {
        return seedToCropConfig.get(seed);
    }

    private void checkTripwire() {
        hasCheckedTripwire = true;
        if (plugin.getPlatform() == Platform.ItemsAdder) {
            Plugin iaP = Bukkit.getPluginManager().getPlugin("ItemsAdder");
            if (iaP != null) {
                FileConfiguration config = iaP.getConfig();
                boolean disabled = config.getBoolean("blocks.disable-REAL_WIRE");
                if (disabled) {
                    AdventureUtils.consoleMessage("<red>========================[CustomCrops]=========================");
                    AdventureUtils.consoleMessage("<red>   Detected that one of your crops is using TRIPWIRE type");
                    AdventureUtils.consoleMessage("<red>  If you want to use tripwire for custom crops, please set");
                    AdventureUtils.consoleMessage("<red>\"blocks.disable-REAL_WIRE: false\" in /ItemsAdder/config.yml");
                    AdventureUtils.consoleMessage("<red>       Change this setting requires a server restart");
                    AdventureUtils.consoleMessage("<red>  If you have problems with which one to use, read the wiki.");
                    AdventureUtils.consoleMessage("<red>==============================================================");
                }
            }
        } else if (plugin.getPlatform() == Platform.Oraxen) {
            Plugin oxP = Bukkit.getPluginManager().getPlugin("Oraxen");
            if (oxP != null) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(oxP.getDataFolder(), "mechanics.yml"));
                boolean disabled = !config.getBoolean("stringblock.enabled");
                if (disabled) {
                    AdventureUtils.consoleMessage("<red>========================[CustomCrops]=========================");
                    AdventureUtils.consoleMessage("<red>   Detected that one of your crops is using TRIPWIRE type");
                    AdventureUtils.consoleMessage("<red>  If you want to use tripwire for custom crops, please set");
                    AdventureUtils.consoleMessage("<red>  \"stringblock.enabled: true\" in /Oraxen/mechanics.yml");
                    AdventureUtils.consoleMessage("<red>  If you have problems with which one to use, read the wiki.");
                    AdventureUtils.consoleMessage("<red>==============================================================");
                }
            }
        }
    }
}

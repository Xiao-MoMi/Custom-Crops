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

package net.momirealms.customcrops.api.object.wateringcan;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.fill.PositiveFillMethod;
import net.momirealms.customcrops.util.AdventureUtils;
import net.momirealms.customcrops.util.ConfigUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WateringCanManager extends Function {

    private final CustomCrops plugin;
    private final HashMap<String, WateringCanConfig> wateringCanConfigMap;

    public WateringCanManager(CustomCrops plugin) {
        this.plugin = plugin;
        this.wateringCanConfigMap = new HashMap<>();
    }

    @Override
    public void load() {
        loadConfig();
    }

    @Override
    public void unload() {
        this.wateringCanConfigMap.clear();
    }

    @Nullable
    public WateringCanConfig getConfigByItemID(String id) {
        return wateringCanConfigMap.get(id);
    }

    private void loadConfig() {
        File can_folder = new File(plugin.getDataFolder(), "contents" + File.separator + "watering-cans");
        if (!can_folder.exists()) {
            if (!can_folder.mkdirs()) return;
            ConfigUtils.getConfig("contents" + File.separator + "watering-cans" + File.separator + "default.yml");
        }
        File[] files = can_folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String key : config.getKeys(false)) {
                ConfigurationSection canSec = config.getConfigurationSection(key);
                if (canSec == null) continue;
                PositiveFillMethod[] methods = ConfigUtils.getPositiveFillMethods(canSec.getConfigurationSection("fill-method"));
                if (methods == null) {
                    AdventureUtils.consoleMessage("<red>[CustomCrops] You need to at least one fill-method for: " + key);
                    continue;
                }
                ConfigurationSection appearSec = canSec.getConfigurationSection("appearance");
                HashMap<Integer, Integer> appearanceMap = new HashMap<>();
                if (appearSec != null) {
                    for (Map.Entry<String, Object> entry : appearSec.getValues(false).entrySet()) {
                        appearanceMap.put(Integer.parseInt(entry.getKey()), (Integer) entry.getValue());
                    }
                }
                @Subst("namespace:key") String soundKey = canSec.getString("sound", "minecraft:block.water.ambient");
                Sound sound = canSec.contains("sound") ? Sound.sound(Key.key(soundKey), Sound.Source.PLAYER, 1, 1) : null;
                WateringCanConfig wateringCanConfig = new WateringCanConfig(
                        canSec.getInt("effective-range.width"),
                        canSec.getInt("effective-range.length"),
                        canSec.getInt("capacity"),
                        canSec.getBoolean("dynamic-lore.enable", false),
                        canSec.getBoolean("actionbar.enable", false),
                        canSec.getStringList("dynamic-lore.lore"),
                        canSec.getString("actionbar.content"),
                        canSec.getString("water-bar.left"),
                        canSec.getString("water-bar.full"),
                        canSec.getString("water-bar.empty"),
                        canSec.getString("water-bar.right"),
                        canSec.contains("pot-whitelist") ? canSec.getStringList("pot-whitelist").toArray(new String[0]) : null,
                        canSec.contains("sprinkler-whitelist") ? canSec.getStringList("sprinkler-whitelist").toArray(new String[0]) : null,
                        sound,
                        canSec.contains("particle") ? Particle.valueOf(canSec.getString("particle", "WATER_SPLASH").toUpperCase(Locale.ENGLISH)) : null,
                        methods,
                        appearanceMap,
                        ConfigUtils.getRequirementsWithMsg(canSec.getConfigurationSection("requirements"))
                );
                wateringCanConfigMap.put(canSec.getString("item"), wateringCanConfig);
            }
        }
        AdventureUtils.consoleMessage("[CustomCrops] Loaded <green>" + wateringCanConfigMap.size() + " <gray>watering-can(s)");
    }

    public int getCurrentWater(ItemStack itemStack) {
        if (itemStack.getType() == Material.AIR) return 0;
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getInteger("WaterAmount");
    }

    public void setWater(ItemStack itemStack, int water, WateringCanConfig config) {
        if (itemStack.getType() == Material.AIR) return;
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("WaterAmount", water);
        if (config.hasDynamicLore()) {
            NBTCompound display = nbtItem.getCompound("display");
            List<String> lore = display.getStringList("Lore");
            lore.clear();
            lore.addAll(config.getLore(water));
        }
        int cmd = config.getModelDataByWater(water);
        if (cmd != 0) {
            nbtItem.setInteger("CustomModelData", cmd);
        }
        itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
    }
}

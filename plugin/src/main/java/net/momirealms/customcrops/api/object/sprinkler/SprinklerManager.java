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

package net.momirealms.customcrops.api.object.sprinkler;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.fill.PassiveFillMethod;
import net.momirealms.customcrops.api.object.hologram.HologramManager;
import net.momirealms.customcrops.api.object.hologram.TextDisplayMeta;
import net.momirealms.customcrops.api.object.hologram.WaterAmountHologram;
import net.momirealms.customcrops.util.AdventureUtils;
import net.momirealms.customcrops.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class SprinklerManager extends Function implements Listener {

    private final CustomCrops plugin;
    private final HashMap<String, SprinklerConfig> sprinklerConfigMap;
    private final HashMap<String, String> itemToKey;

    public SprinklerManager(CustomCrops plugin) {
        this.plugin = plugin;
        this.sprinklerConfigMap = new HashMap<>();
        this.itemToKey = new HashMap<>();
    }

    @Override
    public void load() {
        this.loadConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void unload() {
        this.sprinklerConfigMap.clear();
        this.itemToKey.clear();
        HandlerList.unregisterAll(this);
    }

    private void loadConfig() {
        File sprinkler_folder = new File(plugin.getDataFolder(), "contents" + File.separator + "sprinklers");
        if (!sprinkler_folder.exists()) {
            if (!sprinkler_folder.mkdirs()) return;
            ConfigUtils.getConfig("contents" + File.separator + "sprinklers" + File.separator + "default.yml");
        }
        File[] files = sprinkler_folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String key : config.getKeys(false)) {
                ConfigurationSection sprinklerSec = config.getConfigurationSection(key);
                if (sprinklerSec == null) continue;
                String twoD = sprinklerSec.getString("2D-item");
                String threeD = sprinklerSec.getString("3D-item");
                if (threeD == null) {
                    AdventureUtils.consoleMessage("<red>[CustomCrops] 3D-item is not set for sprinkler: " + key);
                    continue;
                }
                PassiveFillMethod[] methods = ConfigUtils.getPassiveFillMethods(sprinklerSec.getConfigurationSection("fill-method"));
                if (methods == null) {
                    AdventureUtils.consoleMessage("<red>[CustomCrops] fill method is not set for sprinkler: " + key);
                    continue;
                }
                @Subst("namespace:key") String soundKey = sprinklerSec.getString("place-sound", "minecraft:block.bone_block.place");
                Sound sound = sprinklerSec.contains("place-sound") ? Sound.sound(Key.key(soundKey), Sound.Source.PLAYER, 1, 1) : null;
                ItemMode itemMode = ItemMode.valueOf(sprinklerSec.getString("type","ITEM_FRAME").toUpperCase(Locale.ENGLISH));
                SprinklerConfig sprinklerConfig = new SprinklerConfig(
                        key,
                        sprinklerSec.getInt("storage", 3),
                        sprinklerSec.getInt("range", 1),
                        sprinklerSec.getInt("water",1),
                        sprinklerSec.contains("pot-whitelist") ? sprinklerSec.getStringList("pot-whitelist").toArray(new String[0]) : null,
                        sound,
                        itemMode,
                        threeD,
                        twoD,
                        methods,
                        sprinklerSec.getBoolean("hologram.enable") ? new WaterAmountHologram(
                                sprinklerSec.getString("hologram.content",""),
                                sprinklerSec.getDouble("hologram.vertical-offset"),
                                HologramManager.Mode.valueOf(sprinklerSec.getString("hologram.type", "ARMOR_STAND").toUpperCase(Locale.ENGLISH)),
                                sprinklerSec.getInt("hologram.duration"),
                                sprinklerSec.getString("hologram.water-bar.left"),
                                sprinklerSec.getString("hologram.water-bar.full"),
                                sprinklerSec.getString("hologram.water-bar.empty"),
                                sprinklerSec.getString("hologram.water-bar.right"),
                                new TextDisplayMeta(
                                        sprinklerSec.getBoolean("hologram.text-display-options.has-shadow", false),
                                        sprinklerSec.getBoolean("hologram.text-display-options.is-see-through", false),
                                        sprinklerSec.getBoolean("hologram.text-display-options.use-default-background-color", false),
                                        ConfigUtils.rgbToDecimal(sprinklerSec.getString("hologram.text-display-options.background-color", "0,0,0,128")),
                                        (byte) sprinklerSec.getInt("hologram.text-display-options.text-opacity")
                                )
                        ) : null,
                        sprinklerSec.getBoolean("animation.enable") ? new SprinklerAnimation(
                                sprinklerSec.getInt("animation.duration"),
                                sprinklerSec.getString("animation.item"),
                                sprinklerSec.getDouble("animation.vertical-offset"),
                                ItemMode.valueOf(sprinklerSec.getString("animation.type", "ARMOR_STAND").toUpperCase(Locale.ENGLISH))
                        ) : null,
                        ConfigUtils.getRequirementsWithMsg(sprinklerSec.getConfigurationSection("requirements"))
                        );
                this.itemToKey.put(threeD, key);
                if (twoD != null) this.itemToKey.put(twoD, key);
                this.sprinklerConfigMap.put(key, sprinklerConfig);
            }
        }
        AdventureUtils.consoleMessage("[CustomCrops] Loaded <green>" + sprinklerConfigMap.size() + " <gray>sprinkler(s)");
    }

    @Nullable
    public SprinklerConfig getConfigByItemID(String id) {
        String key = itemToKey.get(id);
        if (key == null) return null;
        return sprinklerConfigMap.get(key);
    }

    @Nullable
    public String getConfigKeyByItemID(String id) {
        return itemToKey.get(id);
    }

    @Nullable
    public SprinklerConfig getConfigByKey(String key) {
        return sprinklerConfigMap.get(key);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (event.isCancelled()) return;
        Item item = event.getEntity();
        ItemStack origin = item.getItemStack();
        String id = plugin.getPlatformInterface().getItemStackID(origin);
        String key = itemToKey.get(id);
        if (key == null) return;
        String twoD = sprinklerConfigMap.get(key).getTwoD();
        if (twoD == null || id.equals(twoD)) return;
        ItemStack itemStack = plugin.getPlatformInterface().getItemStack(twoD);
        if (itemStack == null) {
            AdventureUtils.consoleMessage("<red>[CustomCrops] 2D sprinkler " + twoD + " doesn't exist");
            return;
        }
        itemStack.setAmount(origin.getAmount());
        item.setItemStack(itemStack);
    }
}
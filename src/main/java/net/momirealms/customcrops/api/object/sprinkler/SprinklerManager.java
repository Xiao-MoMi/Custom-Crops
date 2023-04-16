package net.momirealms.customcrops.api.object.sprinkler;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.fill.PassiveFillMethod;
import net.momirealms.customcrops.api.util.AdventureUtils;
import net.momirealms.customcrops.api.util.ConfigUtils;
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
            plugin.saveResource("contents" + File.separator + "sprinklers" + File.separator + "default.yml", false);
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
                if (twoD == null || threeD == null) {
                    AdventureUtils.consoleMessage("<red>[CustomCrops] 2/3D-item is not set for sprinkler: " + key);
                    continue;
                }
                PassiveFillMethod[] methods = ConfigUtils.getPassiveFillMethods(sprinklerSec.getConfigurationSection("fill-method"));
                if (methods == null) {
                    AdventureUtils.consoleMessage("<red>[CustomCrops] fill method is not set for sprinkler: " + key);
                    continue;
                }
                @Subst("namespace:key") String soundKey = sprinklerSec.getString("place-sound", "minecraft:block.bone_block.place");
                Sound sound = sprinklerSec.contains("place-sound") ? Sound.sound(Key.key(soundKey), Sound.Source.PLAYER, 1, 1) : null;
                ItemMode itemMode = ItemMode.valueOf(sprinklerSec.getString("type","ITEM_FRAME").toUpperCase());
                SprinklerConfig sprinklerConfig = new SprinklerConfig(
                        key,
                        sprinklerSec.getInt("storage", 3),
                        sprinklerSec.getInt("range", 1),
                        sound,
                        itemMode,
                        threeD,
                        twoD,
                        methods
                        );
                this.itemToKey.put(threeD, key);
                this.itemToKey.put(twoD, key);
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

    public boolean containsSprinkler(String key) {
        return sprinklerConfigMap.containsKey(key);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (event.isCancelled()) return;
        Item item = event.getEntity();
        String id = plugin.getPlatformInterface().getItemID(item.getItemStack());
        String key = itemToKey.get(id);
        if (key == null) return;
        String twoD = sprinklerConfigMap.get(key).getTwoD();
        ItemStack itemStack = plugin.getPlatformInterface().getItemStack(twoD);
        if (itemStack == null) {
            AdventureUtils.consoleMessage("<red>[CustomCrops] 2D sprinkler " + twoD + " doesn't exist");
            return;
        }
        item.setItemStack(itemStack);
    }
}
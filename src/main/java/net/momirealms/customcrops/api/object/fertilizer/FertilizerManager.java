package net.momirealms.customcrops.api.object.fertilizer;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.Pair;
import net.momirealms.customcrops.api.util.AdventureUtils;
import net.momirealms.customcrops.api.util.ConfigUtils;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
        return fertilizerConfigMap.get(id);
    }

    private void loadConfig() {
        File can_folder = new File(plugin.getDataFolder(), "contents" + File.separator + "fertilizers");
        if (!can_folder.exists()) {
            if (!can_folder.mkdirs()) return;
            plugin.saveResource("contents" + File.separator + "fertilizers" + File.separator + "speed-grow.yml", false);
            plugin.saveResource("contents" + File.separator + "fertilizers" + File.separator + "quality.yml", false);
            plugin.saveResource("contents" + File.separator + "fertilizers" + File.separator + "soil-retain.yml", false);
            plugin.saveResource("contents" + File.separator + "fertilizers" + File.separator + "yield-increase.yml", false);
            plugin.saveResource("contents" + File.separator + "fertilizers" + File.separator + "variation.yml", false);
        }
        File[] files = can_folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String key : config.getKeys(false)) {
                ConfigurationSection fertilizerSec = config.getConfigurationSection(key);
                if (fertilizerSec == null) continue;
                FertilizerConfig fertilizerConfig;
                FertilizerType fertilizerType = FertilizerType.valueOf(fertilizerSec.getString("type", "SPEED_GROW").toUpperCase());
                String[] pot_whitelist = fertilizerSec.contains("pot-whitelist") ? fertilizerSec.getStringList("pot-whitelist").toArray(new String[0]) : null;
                boolean beforePlant = fertilizerSec.getBoolean("before-plant", false);
                int times = fertilizerSec.getInt("times", 14);
                Particle particle = fertilizerSec.contains("particle") ? Particle.valueOf(fertilizerSec.getString("particle")) : null;
                @Subst("namespace:key") String soundKey = fertilizerSec.getString("sound", "minecraft:item.hoe.till");
                Sound sound = fertilizerSec.contains("sound") ? Sound.sound(Key.key(soundKey), Sound.Source.PLAYER, 1, 1) : null;
                switch (fertilizerType) {
                    case SPEED_GROW -> fertilizerConfig = new SpeedGrow(key, fertilizerType, times, getChancePair(fertilizerSec), pot_whitelist, beforePlant, particle, sound);
                    case YIELD_INCREASE -> fertilizerConfig = new YieldIncrease(key, fertilizerType, times, fertilizerSec.getDouble("chance"), getChancePair(fertilizerSec), pot_whitelist, beforePlant, particle, sound);
                    case VARIATION -> fertilizerConfig = new Variation(key, fertilizerType, times, fertilizerSec.getDouble("chance"), pot_whitelist, beforePlant, particle, sound);
                    case QUALITY -> fertilizerConfig = new Quality(key, fertilizerType, times, fertilizerSec.getDouble("chance"), ConfigUtils.getQualityRatio(fertilizerSec.getString("ratio", "2/2/1")), pot_whitelist, beforePlant, particle, sound);
                    case SOIL_RETAIN -> fertilizerConfig = new SoilRetain(key, fertilizerType, times, fertilizerSec.getDouble("chance"), pot_whitelist, beforePlant, particle, sound);
                    default -> fertilizerConfig = null;
                }
                if (fertilizerConfig != null)
                    fertilizerConfigMap.put(key, fertilizerConfig);
                else
                    AdventureUtils.consoleMessage("<red>[CustomCrops] Invalid fertilizer: " + key);
            }
        }
        AdventureUtils.consoleMessage("[CustomCrops] Loaded <green>" + fertilizerConfigMap.size() + " <gray>fertilizer(s)");
    }

    public ArrayList<Pair<Double, Integer>> getChancePair(ConfigurationSection fertilizerSec) {
        ArrayList<Pair<Double, Integer>> pairs = new ArrayList<>();
        ConfigurationSection effectSec = fertilizerSec.getConfigurationSection("effects");
        if (effectSec == null) return new ArrayList<>();
        for (String point : effectSec.getKeys(false)) {
            Pair<Double, Integer> pair = new Pair<>(effectSec.getDouble(point), Integer.parseInt(point));
            pairs.add(pair);
        }
        return pairs;
    }
}

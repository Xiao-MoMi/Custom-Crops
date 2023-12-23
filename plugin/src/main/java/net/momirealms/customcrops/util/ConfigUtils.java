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

package net.momirealms.customcrops.util;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.BoneMeal;
import net.momirealms.customcrops.api.object.InteractCrop;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.Pair;
import net.momirealms.customcrops.api.object.action.*;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.condition.Random;
import net.momirealms.customcrops.api.object.condition.*;
import net.momirealms.customcrops.api.object.crop.VariationCrop;
import net.momirealms.customcrops.api.object.fill.PassiveFillMethod;
import net.momirealms.customcrops.api.object.fill.PositiveFillMethod;
import net.momirealms.customcrops.api.object.loot.Loot;
import net.momirealms.customcrops.api.object.loot.OtherLoot;
import net.momirealms.customcrops.api.object.loot.QualityLoot;
import net.momirealms.customcrops.api.object.requirement.*;
import net.momirealms.customcrops.api.object.season.CCSeason;
import net.momirealms.customcrops.customplugin.Platform;
import net.momirealms.customcrops.helper.Log;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigUtils {

    /**
     * Get a config by name
     * @param config_path config's path
     * @return yaml
     */
    public static YamlConfiguration getConfig(String config_path) {
        File file = new File(CustomCrops.getInstance().getDataFolder(), config_path);
        if (!file.exists()) {
            CustomCrops.getInstance().saveResource(config_path, false);
            if (CustomCrops.getInstance().getPlatform() == Platform.Oraxen) {
                File generated = new File(CustomCrops.getInstance().getDataFolder(), config_path);
                if (generated.exists() && generated.getName().endsWith(".yml")) {
                    removeNamespace(generated);
                }
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void removeNamespace(File file) {
        String line;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(sb.toString().replace(" customcrops:", " ").replace("CHORUS", "TRIPWIRE").replace("<font:customcrops:default>", "<font:minecraft:customcrops>"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update config
     * @param fileName config
     */
    public static void update(String fileName){
        try {
            YamlDocument.create(new File(CustomCrops.getInstance().getDataFolder(), fileName), Objects.requireNonNull(CustomCrops.getInstance().getResource(fileName)), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
        } catch (IOException e){
            Log.warn(e.getMessage());
        }
    }

    /**
     * Create a data file if not exists
     * @param file file path
     * @return yaml data
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static YamlConfiguration readData(File file) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                AdventureUtils.consoleMessage("<red>[CustomFishing] Failed to generate data files!</red>");
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    @Nullable
    public static DeathCondition[] getDeathConditions(ConfigurationSection section) {
        if (section != null) {
            List<DeathCondition> deathConditions = new ArrayList<>();
            for (String key : section.getKeys(false)) {
                String model = section.getString(key + ".model");
                ConfigurationSection conditionSec = section.getConfigurationSection(key + ".conditions");
                if (conditionSec == null) {
                    AdventureUtils.consoleMessage("<red>[CustomCrops] No condition is set for: " + section.getCurrentPath());
                    continue;
                }
                List<Condition> conditions = getConditions(conditionSec.getValues(false));
                deathConditions.add(new DeathCondition(model, conditions.toArray(new Condition[0])));
                if (model != null) CustomCrops.getInstance().getCropManager().registerDeadCrops(model);
            }
            return deathConditions.toArray(new DeathCondition[0]);
        }
        return null;
    }

    @Nullable
    public static Condition[] getConditions(ConfigurationSection section) {
        if (section != null) {
            return getConditions(section.getValues(false)).toArray(new Condition[0]);
        }
        return null;
    }

    @NotNull
    public static List<Condition> getConditions(Map<String, Object> map) {
        List<Condition> conditions = new ArrayList<>();
        map.forEach((key, value) -> {
            if (key.startsWith("&&")) {
                if (map.get(key) instanceof MemorySection map2) {
                    conditions.add(new AndCondition(getConditions(map2.getValues(false))));
                }
            } else if (key.startsWith("||")) {
                if (map.get(key) instanceof MemorySection map2) {
                    conditions.add(new OrCondition(getConditions(map2.getValues(false))));
                }
            } else {
                if (map.get(key) instanceof MemorySection map2) {
                    String type = map2.getString("type");
                    if (type == null) return;
                    switch (type) {
                        case "water_less_than" -> conditions.add(new WaterLessThan(map2.getInt("value")));
                        case "water_more_than" -> conditions.add(new WaterMoreThan(map2.getInt("value")));
                        case "unsuitable_season" -> {
                            if (!ConfigManager.enableSeason) return;
                            conditions.add(new WrongSeason(map2.getStringList("value").stream().map(s -> CCSeason.valueOf(s.toUpperCase(Locale.ENGLISH))).toList().toArray(new CCSeason[0])));
                        }
                        case "suitable_season" -> {
                            if (!ConfigManager.enableSeason) return;
                            conditions.add(new RightSeason(map2.getStringList("value").stream().map(s -> CCSeason.valueOf(s.toUpperCase(Locale.ENGLISH))).toList().toArray(new CCSeason[0])));
                        }
                        case "crow_attack" -> conditions.add(new CrowAttack(map2.getDouble("value.chance"), map2.getString("value.fly-model"), map2.getString("value.stand-model")));
                        case "random" -> conditions.add(new Random(map2.getDouble("value")));
                        case "weather" -> conditions.add(new Weather(map2.getStringList("value").toArray(new String[0])));
                    }
                }
            }
        });
        return conditions;
    }

    @Nullable
    public static Requirement[] getRequirementsWithMsg(ConfigurationSection section) {
        if (section != null) {
            List<Requirement> requirements = new ArrayList<>();
            for (String id : section.getKeys(false)) {
                ConfigurationSection innerSec = section.getConfigurationSection(id);
                if (innerSec == null) continue;
                String type = innerSec.getString("type");
                if (type == null) continue;
                String[] msg = innerSec.getStringList("message").size() == 0 ? (innerSec.getString("message") == null ? null : new String[]{innerSec.getString("message")}) : innerSec.getStringList("message").toArray(new String[0]);
                ConfigurationSection actionSec = innerSec.getConfigurationSection("actions");
                switch (type) {
                    case "biome" -> requirements.add(new BiomeImpl(msg, getActions(actionSec), new HashSet<>(innerSec.getStringList("value"))));
                    case "!biome" -> requirements.add(new BlackBiomeImpl(msg, getActions(actionSec), new HashSet<>(innerSec.getStringList("value"))));
                    case "weather" -> requirements.add(new WeatherImpl(msg, getActions(actionSec), innerSec.getStringList("value").toArray(new String[0])));
                    case "ypos" -> requirements.add(new YPosImpl(msg, getActions(actionSec), innerSec.getStringList("value")));
                    case "season" -> {
                        if (!ConfigManager.enableSeason) continue;
                        requirements.add(new SeasonImpl(msg, getActions(actionSec), innerSec.getStringList("value").stream().map(str -> CCSeason.valueOf(str.toUpperCase(Locale.ENGLISH))).collect(Collectors.toList())));
                    }
                    case "world" -> requirements.add(new WorldImpl(msg, getActions(actionSec), innerSec.getStringList("value")));
                    case "permission" -> requirements.add(new PermissionImpl(msg, getActions(actionSec), innerSec.getString("value")));
                    case "time" -> requirements.add(new TimeImpl(msg, getActions(actionSec), innerSec.getStringList("value")));
                    case "skill-level" -> requirements.add(new SkillLevelImpl(msg, getActions(actionSec), innerSec.getInt("value")));
                    case "job-level" -> requirements.add(new JobLevelImpl(msg, getActions(actionSec), innerSec.getInt("value.level"), innerSec.getString("value.job")));
                    case "light" -> requirements.add(new LightLevelImpl(msg, getActions(actionSec), innerSec.getInt("value")));
                    case "natural-light" -> requirements.add(new NaturalLightLevelImpl(msg, getActions(actionSec), innerSec.getInt("value")));
                    case "date" -> requirements.add(new DateImpl(msg, getActions(actionSec), new HashSet<>(innerSec.getStringList("value"))));
                    case "max-entity-amount-in-chunk" -> requirements.add(new EntityAmountInChunkImpl(msg, getActions(actionSec), innerSec.getInt("value")));
                    case "papi-condition" -> requirements.add(new CustomPapi(msg, getActions(actionSec), Objects.requireNonNull(innerSec.getConfigurationSection("value")).getValues(false)));
                }
            }
            return requirements.toArray(new Requirement[0]);
        }
        return null;
    }

    @Nullable
    public static Action[] getActions(ConfigurationSection section) {
        return getActions(section, null);
    }

    @Nullable
    public static Action[] getActions(ConfigurationSection section, String model_id) {
        if (section != null) {
            List<Action> actions = new ArrayList<>();
            for (String action_key : section.getKeys(false)) {
                if (action_key.equals("requirements")) continue;
                ConfigurationSection actionSec = section.getConfigurationSection(action_key);
                if (actionSec == null) continue;
                String type = actionSec.getString("type");
                if (type == null) continue;
                switch (type) {
                    case "message" -> actions.add(new MessageActionImpl(
                            actionSec.getStringList("value").toArray(new String[0]),
                            actionSec.getDouble("chance", 1))
                    );
                    case "actionbar" -> actions.add(new ActionBarImpl(
                            actionSec.getString("value"),
                            actionSec.getDouble("chance", 1))
                    );
                    case "command" -> actions.add(new CommandActionImpl(
                            actionSec.getStringList("value").toArray(new String[0]),
                            actionSec.getDouble("chance", 1))
                    );
                    case "exp" -> actions.add(new VanillaXPImpl(
                            actionSec.getInt("value"),
                            false,
                            actionSec.getDouble("chance", 1))
                    );
                    case "mending" -> actions.add(new VanillaXPImpl(
                            actionSec.getInt("value"),
                            true,
                            actionSec.getDouble("chance", 1))
                    );
                    case "skill-xp" -> actions.add(new SkillXPImpl(
                            actionSec.getDouble("value"),
                            actionSec.getDouble("chance", 1))
                    );
                    case "job-xp" -> actions.add(new JobXPImpl(
                            actionSec.getDouble("value.xp"),
                            actionSec.getDouble("chance", 1),
                            actionSec.getString("value.job"))
                    );
                    case "sound" -> actions.add(new SoundActionImpl(
                            actionSec.getString("value.source"),
                            actionSec.getString("value.key"),
                            (float) actionSec.getDouble("value.volume"),
                            (float) actionSec.getDouble("value.pitch"))
                    );
                    case "particle" -> actions.add(new ParticleImpl(
                            Particle.valueOf(actionSec.getString("value.particle", "FLAME").toUpperCase(Locale.ENGLISH)),
                            actionSec.getInt("value.amount"),
                            actionSec.getDouble("value.offset"))
                    );
                    case "potion-effect" -> {
                        PotionEffectType potionEffectType = PotionEffectType.getByName(actionSec.getString("value.type", "BLINDNESS").toUpperCase(Locale.ENGLISH));
                        PotionEffect potionEffect = new PotionEffect(
                                potionEffectType == null ? PotionEffectType.LUCK : potionEffectType,
                                actionSec.getInt("value.duration"),
                                actionSec.getInt("value.amplifier")
                        );
                        actions.add(new PotionEffectImpl(potionEffect, actionSec.getDouble("chance", 1)));
                    }
                    case "drop-items" -> {
                        ConfigurationSection lootSec = actionSec.getConfigurationSection("value");
                        if (lootSec == null) continue;
                        ArrayList<Loot> loots = new ArrayList<>();
                        if (lootSec.contains("quality-crops")) {
                            String[] qualityLoots = new String[ConfigManager.defaultRatio.length];
                            for (int i = 0; i < ConfigManager.defaultRatio.length; i++) {
                                qualityLoots[i] = lootSec.getString("quality-crops.items." + (i+1));
                                if (qualityLoots[i] == null) {
                                    AdventureUtils.consoleMessage("<red>[CustomCrops] Error found at: " + model_id + " quality-crops.items." + (i+1) + ", which can't be null");
                                }
                            }
                            loots.add(new QualityLoot(
                                    lootSec.getInt("quality-crops.min"),
                                    lootSec.getInt("quality-crops.max"),
                                    qualityLoots
                            ));
                        }
                        if (lootSec.contains("other-items")) {
                            ConfigurationSection otherLootSec = lootSec.getConfigurationSection("other-items");
                            if (otherLootSec == null) continue;
                            for (String inner_key : otherLootSec.getKeys(false)) {
                                OtherLoot otherLoot = new OtherLoot(
                                        otherLootSec.getInt(inner_key + ".min"),
                                        otherLootSec.getInt(inner_key + ".max"),
                                        otherLootSec.getString(inner_key + ".item"),
                                        otherLootSec.getDouble(inner_key + ".chance")
                                );
                                loots.add(otherLoot);
                            }
                        }
                        actions.add(new DropItemImpl(loots.toArray(new Loot[0])));
                    }
                    case "break" -> actions.add(new BreakImpl(
                            actionSec.getBoolean("value", true),
                            model_id)
                    );
                    case "replant" -> actions.add(new ReplantImpl(
                            actionSec.getInt("value.point"),
                            actionSec.getString("value.model"),
                            actionSec.getString("value.crop")
                    ));
                    case "variation" -> {
                        ConfigurationSection variationSec = actionSec.getConfigurationSection("value");
                        if (variationSec == null) continue;
                        ArrayList<VariationCrop> variationCrops = new ArrayList<>();
                        for (String inner_key : variationSec.getKeys(false)) {
                            VariationCrop variationCrop = new VariationCrop(
                                    variationSec.getString(inner_key + ".item"),
                                    ItemMode.valueOf(variationSec.getString(inner_key + ".type", "TripWire").toUpperCase(Locale.ENGLISH)),
                                    variationSec.getDouble(inner_key + ".chance")
                            );
                            variationCrops.add(variationCrop);
                        }
                        actions.add(new VariationImpl(variationCrops.toArray(new VariationCrop[0])));
                    }
                    case "chain" -> actions.add(new ChainImpl(
                            getActions(actionSec.getConfigurationSection("value"), model_id),
                            getRequirementsWithMsg(actionSec.getConfigurationSection("requirements")),
                            actionSec.getDouble("chance")
                    ));
                    case "swing-hand" -> actions.add(new SwingHandImpl());
                    case "give-money" -> actions.add(new GiveMoneyImpl(
                            actionSec.getDouble("value"),
                            actionSec.getDouble("chance", 1)
                    ));
                }
            }
            return actions.toArray(new Action[0]);
        }
        return null;
    }

    @Nullable
    public static PassiveFillMethod[] getPassiveFillMethods(ConfigurationSection section) {
        if (section == null) return null;
        ArrayList<PassiveFillMethod> passiveFillMethods = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection methodSec = section.getConfigurationSection(key);
            if (methodSec == null) continue;
            @Subst("namespace:key") String soundKey = methodSec.getString("sound", "minecraft:block.water.ambient");
            Sound sound = methodSec.contains("sound") ? Sound.sound(Key.key(soundKey), Sound.Source.PLAYER, 1, 1) : null;
            PassiveFillMethod passiveFillMethod = new PassiveFillMethod(
                    methodSec.getString("item"),
                    methodSec.getString("return"),
                    methodSec.getInt("amount", 1),
                    methodSec.contains("particle") ? Particle.valueOf(methodSec.getString("particle", "WATER_SPLASH").toUpperCase(Locale.ENGLISH)) : null,
                    sound
            );
            passiveFillMethods.add(passiveFillMethod);
        }
        return passiveFillMethods.toArray(new PassiveFillMethod[0]);
    }

    public static double[] getQualityRatio(String str) {
        String[] split = str.split("/");
        double[] ratio = new double[split.length];
        double weightTotal = Arrays.stream(split).mapToInt(Integer::parseInt).sum();
        double temp = 0;
        for (int i = 0; i < ratio.length; i++) {
            temp += Integer.parseInt(split[i]);
            ratio[i] = temp / weightTotal;
        }
        return ratio;
    }

    public static boolean isVanillaItem(String item) {
        char[] chars = item.toCharArray();
        for (char character : chars) {
            if ((character < 65 || character > 90) && character != 95) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public static BoneMeal[] getBoneMeals(ConfigurationSection section) {
        if (section == null) return null;
        ArrayList<BoneMeal> boneMeals = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection boneMealSec = section.getConfigurationSection(key);
            if (boneMealSec == null) continue;
            ConfigurationSection chanceSec = boneMealSec.getConfigurationSection("chance");
            if (chanceSec == null) {
                AdventureUtils.consoleMessage("chance is not properly set for custom bone meal at: " + boneMealSec.getCurrentPath());
                continue;
            }
            ArrayList<Pair<Double, Integer>> pairs = new ArrayList<>();
            for (String point : chanceSec.getKeys(false)) {
                Pair<Double, Integer> pair = Pair.of(chanceSec.getDouble(point), Integer.parseInt(point));
                pairs.add(pair);
            }
            @Subst("namespace:key") String soundKey = boneMealSec.getString("sound", "minecraft:item.bone_meal.use");
            Sound sound = boneMealSec.contains("sound") ? Sound.sound(Key.key(soundKey), Sound.Source.PLAYER, 1, 1) : null;
            BoneMeal boneMeal = new BoneMeal(
                    boneMealSec.getString("item"),
                    boneMealSec.getString("return"),
                    pairs,
                    sound,
                    boneMealSec.contains("particle") ? Particle.valueOf(boneMealSec.getString("particle", "WATER_SPLASH").toUpperCase(Locale.ENGLISH)) : null
            );
            boneMeals.add(boneMeal);
        }
        return boneMeals.toArray(new BoneMeal[0]);
    }

    @Nullable
    public static PositiveFillMethod[] getPositiveFillMethods(ConfigurationSection section) {
        if (section == null) return null;
        ArrayList<PositiveFillMethod> methods = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection methodSec = section.getConfigurationSection(key);
            if (methodSec == null) continue;
            String id = methodSec.getString("target", "WATER");
            @Subst("namespace:key") String soundKey = methodSec.getString("sound", "minecraft:item.bucket.fill");
            Sound sound = Sound.sound(Key.key(soundKey), Sound.Source.PLAYER, 1, 1);
            PositiveFillMethod method = new PositiveFillMethod(
                    id,
                    methodSec.getInt("amount"),
                    methodSec.contains("particle") ? Particle.valueOf(methodSec.getString("particle", "WATER_SPLASH").toUpperCase(Locale.ENGLISH)) : null,
                    sound
            );
            methods.add(method);
        }
        return methods.toArray(new PositiveFillMethod[0]);
    }

    public static InteractCrop[] getInteractActions(ConfigurationSection section, String stageModel) {
        if (section == null) return null;
        ArrayList<InteractCrop> interactCrops = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection innerSec = section.getConfigurationSection(key);
            if (innerSec == null) continue;
            InteractCrop interactCrop = new InteractCrop(
                    innerSec.getString("item"),
                    innerSec.getBoolean("reduce-amount", false),
                    innerSec.getString("return"),
                    getActions(innerSec.getConfigurationSection("actions"), stageModel),
                    getRequirementsWithMsg(innerSec.getConfigurationSection("requirements"))
            );
            interactCrops.add(interactCrop);
        }
        return interactCrops.toArray(new InteractCrop[0]);
    }

    public static int rgbToDecimal(String rgba) {
        String[] split = rgba.split(",");
        int r = Integer.parseInt(split[0]);
        int g = Integer.parseInt(split[1]);
        int b = Integer.parseInt(split[2]);
        int a = Integer.parseInt(split[3]);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static File getFile(String world, String fileName) {
        File file;
        if (ConfigManager.worldFolderPath.equals("")) {
            file = new File(CustomCrops.getInstance().getDataFolder().getParentFile().getParentFile(), world + File.separator + "customcrops" + File.separator + fileName);
        } else {
            file = new File(ConfigManager.worldFolderPath, world + File.separator + "customcrops" + File.separator + fileName);
        }
        return file;
    }

    public static File getFile(World world, String fileName) {
        File file;
        if (ConfigManager.worldFolderPath.equals("")) {
            file = new File(world.getWorldFolder(), "customcrops" + File.separator + fileName);
        } else {
            file = new File(ConfigManager.worldFolderPath, world.getName() + File.separator + "customcrops" + File.separator + fileName);
        }
        return file;
    }
}
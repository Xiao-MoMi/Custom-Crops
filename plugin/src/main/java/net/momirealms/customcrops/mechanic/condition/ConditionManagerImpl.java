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

package net.momirealms.customcrops.mechanic.condition;

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.ConditionManager;
import net.momirealms.customcrops.api.mechanic.condition.*;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.utils.ClassUtils;
import net.momirealms.customcrops.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ConditionManagerImpl implements ConditionManager {

    private final String EXPANSION_FOLDER = "expansions/condition";
    private final HashMap<String, ConditionFactory> conditionBuilderMap;
    private final CustomCropsPlugin plugin;

    public ConditionManagerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
        this.conditionBuilderMap = new HashMap<>();
        this.registerInbuiltConditions();
    }

    @Override
    public void load() {
        this.loadExpansions();
    }

    @Override
    public void unload() {

    }

    @Override
    public void disable() {
        this.conditionBuilderMap.clear();
    }

    private void registerInbuiltConditions() {
        this.registerSeasonCondition();
        this.registerWaterCondition();
    }

    @Override
    public boolean registerCondition(String type, ConditionFactory conditionFactory) {
        if (this.conditionBuilderMap.containsKey(type)) return false;
        this.conditionBuilderMap.put(type, conditionFactory);
        return true;
    }

    @Override
    public boolean unregisterCondition(String type) {
        return this.conditionBuilderMap.remove(type) != null;
    }

    @Override
    public boolean hasCondition(String type) {
        return conditionBuilderMap.containsKey(type);
    }

    @Override
    public @NotNull Condition[] getConditions(ConfigurationSection section) {
        ArrayList<Condition> conditions = new ArrayList<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection innerSection) {
                    String key = entry.getKey();
                    if (hasCondition(key)) {
                        conditions.add(getCondition(key, innerSection));
                    } else {
                        conditions.add(getCondition(section.getConfigurationSection(key)));
                    }
                }
            }
        }
        return conditions.toArray(new Condition[0]);
    }

    @Override
    public Condition getCondition(ConfigurationSection section) {
        if (section == null) {
            return EmptyCondition.instance;
        }
        return getCondition(section.getString("type"), section.get("value"));
    }

    @Override
    public Condition getCondition(String key, Object args) {
        if (key == null) {
            return EmptyCondition.instance;
        }
        ConditionFactory factory = getConditionFactory(key);
        if (factory == null) {
            LogUtils.warn("Condition type: " + key + " doesn't exist.");
            return EmptyCondition.instance;
        }
        return factory.build(args);
    }

    @Nullable
    @Override
    public ConditionFactory getConditionFactory(String type) {
        return conditionBuilderMap.get(type);
    }

    private void registerSeasonCondition() {
        for (String name : List.of("season", "suitable_season")) {
            registerCondition(name, (args) -> {
                HashSet<String> seasons = new HashSet<>(ConfigUtils.stringListArgs(args).stream().map(it -> it.toUpperCase(Locale.ENGLISH)).toList());
                return cropState -> {
                    Season season = plugin.getIntegrationManager().getSeason(cropState.getLocation().getBukkitWorld());
                    if (season == null)
                        return true;
                    return seasons.contains(season.name());
                };
            });
        }
        for (String name : List.of("!season", "!suitable_season", "unsuitable_season")) {
            registerCondition(name, (args) -> {
                HashSet<String> seasons = new HashSet<>(ConfigUtils.stringListArgs(args).stream().map(it -> it.toUpperCase(Locale.ENGLISH)).toList());
                return cropState -> {
                    Season season = plugin.getIntegrationManager().getSeason(cropState.getLocation().getBukkitWorld());
                    if (season == null)
                        return true;
                    return !seasons.contains(season.name());
                };
            });
        }
    }

    private void registerWaterCondition() {
        for (String name : List.of("water_more_than")) {
            registerCondition(name, (args) -> {
                int value = (int) args;
                return cropState -> {
                    Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(cropState.getLocation());
                    return worldPot.filter(pot -> pot.getWater() > value).isPresent();
                };
            });
        }
        for (String name : List.of("water_less_than")) {
            registerCondition(name, (args) -> {
                int value = (int) args;
                return cropState -> {
                    Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(cropState.getLocation());
                    return worldPot.filter(pot -> pot.getWater() < value).isPresent();
                };
            });
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadExpansions() {
        File expansionFolder = new File(plugin.getDataFolder(), EXPANSION_FOLDER);
        if (!expansionFolder.exists())
            expansionFolder.mkdirs();

        List<Class<? extends ConditionExpansion>> classes = new ArrayList<>();
        File[] expansionJars = expansionFolder.listFiles();
        if (expansionJars == null) return;
        for (File expansionJar : expansionJars) {
            if (expansionJar.getName().endsWith(".jar")) {
                try {
                    Class<? extends ConditionExpansion> expansionClass = ClassUtils.findClass(expansionJar, ConditionExpansion.class);
                    classes.add(expansionClass);
                } catch (IOException | ClassNotFoundException e) {
                    LogUtils.warn("Failed to load expansion: " + expansionJar.getName(), e);
                }
            }
        }
        try {
            for (Class<? extends ConditionExpansion> expansionClass : classes) {
                ConditionExpansion expansion = expansionClass.getDeclaredConstructor().newInstance();
                unregisterCondition(expansion.getConditionType());
                registerCondition(expansion.getConditionType(), expansion.getConditionFactory());
                LogUtils.info("Loaded condition expansion: " + expansion.getConditionType() + "[" + expansion.getVersion() + "]" + " by " + expansion.getAuthor());
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            LogUtils.warn("Error occurred when creating expansion instance.", e);
        }
    }
}

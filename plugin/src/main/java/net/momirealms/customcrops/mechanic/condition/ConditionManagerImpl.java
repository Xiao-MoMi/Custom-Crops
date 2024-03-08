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

import net.momirealms.biomeapi.BiomeAPI;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Pair;
import net.momirealms.customcrops.api.manager.ConditionManager;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.mechanic.condition.Condition;
import net.momirealms.customcrops.api.mechanic.condition.ConditionExpansion;
import net.momirealms.customcrops.api.mechanic.condition.ConditionFactory;
import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.compatibility.papi.ParseUtils;
import net.momirealms.customcrops.mechanic.misc.CrowAttackAnimation;
import net.momirealms.customcrops.utils.ClassUtils;
import net.momirealms.customcrops.utils.ConfigUtils;
import org.bukkit.World;
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
        this.registerTemperatureCondition();
        this.registerAndCondition();
        this.registerOrCondition();
        this.registerRandomCondition();
        this.registerEqualsCondition();
        this.registerNumberEqualCondition();
        this.registerRegexCondition();
        this.registerGreaterThanCondition();
        this.registerLessThanCondition();
        this.registerContainCondition();
        this.registerStartWithCondition();
        this.registerEndWithCondition();
        this.registerInListCondition();
        this.registerBiomeRequirement();
        this.registerFertilizerCondition();
        this.registerCrowAttackCondition();
        this.registerPotCondition();
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
            LogUtils.warn("Condition section should not be null");
            return EmptyCondition.instance;
        }
        return getCondition(section.getString("type"), section.get("value"));
    }

    @Override
    public Condition getCondition(String key, Object args) {
        if (key == null) {
            LogUtils.warn("Condition type should not be null");
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

    private void registerCrowAttackCondition() {
        registerCondition("crow_attack", (args -> {
            if (args instanceof ConfigurationSection section) {
                String flyModel = section.getString("fly-model");
                String standModel = section.getString("stand-model");
                double chance = section.getDouble("chance");
                return block -> {
                    if (Math.random() > chance) return false;
                    SimpleLocation location = block.getLocation();
                    if (ConfigManager.enableScarecrow()) {
                        int range = ConfigManager.scarecrowRange();
                        Optional<CustomCropsWorld> world = plugin.getWorldManager().getCustomCropsWorld(location.getWorldName());
                        if (world.isEmpty()) return false;
                        CustomCropsWorld customCropsWorld = world.get();
                        for (int i = -range; i <= range; i++) {
                            for (int j = -range; j <= range; j++) {
                                for (int k : new int[]{0,-1,1}) {
                                    if (customCropsWorld.getScarecrowAt(location.copy().add(i, k, j)).isPresent()) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                    new CrowAttackAnimation(location, flyModel, standModel).start();
                    return true;
                };
            } else {
                LogUtils.warn("Wrong value format found at crow-attack condition.");
                return EmptyCondition.instance;
            }
        }));
    }

    private void registerBiomeRequirement() {
        registerCondition("biome", (args) -> {
            HashSet<String> biomes = new HashSet<>(ConfigUtils.stringListArgs(args));
            return block -> {
                String currentBiome = BiomeAPI.getBiomeAt(block.getLocation().getBukkitLocation());
                return biomes.contains(currentBiome);
            };
        });
        registerCondition("!biome", (args) -> {
            HashSet<String> biomes = new HashSet<>(ConfigUtils.stringListArgs(args));
            return block -> {
                String currentBiome = BiomeAPI.getBiomeAt(block.getLocation().getBukkitLocation());
                return !biomes.contains(currentBiome);
            };
        });
    }

    private void registerRandomCondition() {
        registerCondition("random", (args -> {
            double value = ConfigUtils.getDoubleValue(args);
            return block -> Math.random() < value;
        }));
    }

    private void registerPotCondition() {
        registerCondition("pot", (args -> {
            HashSet<String> pots = new HashSet<>(ConfigUtils.stringListArgs(args));
            return block -> {
                Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(block.getLocation().copy().add(0,-1,0));
                return worldPot.filter(pot -> pots.contains(pot.getKey())).isPresent();
            };
        }));
        registerCondition("!pot", (args -> {
            HashSet<String> pots = new HashSet<>(ConfigUtils.stringListArgs(args));
            return block -> {
                Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(block.getLocation().copy().add(0,-1,0));
                return worldPot.filter(pot -> !pots.contains(pot.getKey())).isPresent();
            };
        }));
    }

    private void registerFertilizerCondition() {
        registerCondition("fertilizer", (args -> {
            HashSet<String> fertilizer = new HashSet<>(ConfigUtils.stringListArgs(args));
            return block -> {
                Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(block.getLocation().copy().add(0,-1,0));
                return worldPot.filter(pot -> {
                    Fertilizer fertilizerInstance = pot.getFertilizer();
                    if (fertilizerInstance == null) return false;
                    return fertilizer.contains(fertilizerInstance.getKey());
                }).isPresent();
            };
        }));
        registerCondition("fertilizer_type", (args -> {
            HashSet<String> fertilizer = new HashSet<>(ConfigUtils.stringListArgs(args).stream().map(str -> str.toUpperCase(Locale.ENGLISH)).toList());
            return block -> {
                Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(block.getLocation().copy().add(0,-1,0));
                return worldPot.filter(pot -> {
                    Fertilizer fertilizerInstance = pot.getFertilizer();
                    if (fertilizerInstance == null) return false;
                    return fertilizer.contains(fertilizerInstance.getFertilizerType().name());
                }).isPresent();
            };
        }));
    }

    private void registerAndCondition() {
        registerCondition("&&", (args -> {
            if (args instanceof ConfigurationSection section) {
                Condition[] conditions = getConditions(section);
                return block -> ConditionManager.isConditionMet(block, conditions);
            } else {
                LogUtils.warn("Wrong value format found at && condition.");
                return EmptyCondition.instance;
            }
        }));
    }

    private void registerOrCondition() {
        registerCondition("||", (args -> {
            if (args instanceof ConfigurationSection section) {
                Condition[] conditions = getConditions(section);
                return block -> {
                    for (Condition condition : conditions) {
                        if (condition.isConditionMet(block)) {
                            return true;
                        }
                    }
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at || condition.");
                return EmptyCondition.instance;
            }
        }));
    }

    private void registerTemperatureCondition() {
        registerCondition("temperature", (args) -> {
            List<Pair<Integer, Integer>> tempPairs = ConfigUtils.stringListArgs(args).stream().map(it -> ConfigUtils.splitStringIntegerArgs(it, "~")).toList();
            return block -> {
                SimpleLocation location = block.getLocation();
                World world = location.getBukkitWorld();
                if (world == null) return false;
                double temp = world.getTemperature(location.getX(), location.getY(), location.getZ());
                for (Pair<Integer, Integer> pair : tempPairs) {
                    if (temp >= pair.left() && temp <= pair.right()) {
                        return true;
                    }
                }
                return false;
            };
        });
    }

    @SuppressWarnings("DuplicatedCode")
    private void registerGreaterThanCondition() {
        registerCondition(">=", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return Double.parseDouble(p1) >= Double.parseDouble(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at >= requirement.");
                return EmptyCondition.instance;
            }
        });
        registerCondition(">", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return Double.parseDouble(p1) > Double.parseDouble(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at > requirement.");
                return EmptyCondition.instance;
            }
        });
    }

    private void registerRegexCondition() {
        registerCondition("regex", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("papi", "");
                String v2 = section.getString("regex", "");
                return block -> ParseUtils.setPlaceholders(null, v1).matches(v2);
            } else {
                LogUtils.warn("Wrong value format found at regex requirement.");
                return EmptyCondition.instance;
            }
        });
    }

    private void registerNumberEqualCondition() {
        registerCondition("==", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return Double.parseDouble(p1) == Double.parseDouble(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at !startsWith requirement.");
                return EmptyCondition.instance;
            }
        });
        registerCondition("!=", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return Double.parseDouble(p1) != Double.parseDouble(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at !startsWith requirement.");
                return EmptyCondition.instance;
            }
        });
    }

    @SuppressWarnings("DuplicatedCode")
    private void registerLessThanCondition() {
        registerCondition("<", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return Double.parseDouble(p1) < Double.parseDouble(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at < requirement.");
                return EmptyCondition.instance;
            }
        });
        registerCondition("<=", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return Double.parseDouble(p1) <= Double.parseDouble(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at <= requirement.");
                return EmptyCondition.instance;
            }
        });
    }

    private void registerStartWithCondition() {
        registerCondition("startsWith", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return p1.startsWith(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at startsWith requirement.");
                return EmptyCondition.instance;
            }
        });
        registerCondition("!startsWith", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return !p1.startsWith(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at !startsWith requirement.");
                return EmptyCondition.instance;
            }
        });
    }

    private void registerEndWithCondition() {
        registerCondition("endsWith", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return p1.endsWith(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at endsWith requirement.");
                return EmptyCondition.instance;
            }
        });
        registerCondition("!endsWith", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return !p1.endsWith(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at !endsWith requirement.");
                return EmptyCondition.instance;
            }
        });
    }

    private void registerContainCondition() {
        registerCondition("contains", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return p1.contains(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at contains requirement.");
                return EmptyCondition.instance;
            }
        });
        registerCondition("!contains", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return !p1.contains(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at !contains requirement.");
                return EmptyCondition.instance;
            }
        });
    }

    private void registerInListCondition() {
        registerCondition("in-list", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String papi = section.getString("papi", "");
                HashSet<String> values = new HashSet<>(ConfigUtils.stringListArgs(section.get("values")));
                return block -> {
                    String p1 = papi.startsWith("%") ? ParseUtils.setPlaceholders(null, papi) : papi;
                    return values.contains(p1);
                };
            } else {
                LogUtils.warn("Wrong value format found at in-list requirement.");
                return EmptyCondition.instance;
            }
        });
        registerCondition("!in-list", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String papi = section.getString("papi", "");
                HashSet<String> values = new HashSet<>(ConfigUtils.stringListArgs(section.get("values")));
                return block -> {
                    String p1 = papi.startsWith("%") ? ParseUtils.setPlaceholders(null, papi) : papi;
                    return !values.contains(p1);
                };
            } else {
                LogUtils.warn("Wrong value format found at in-list requirement.");
                return EmptyCondition.instance;
            }
        });
    }

    private void registerEqualsCondition() {
        registerCondition("equals", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return p1.equals(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at equals requirement.");
                return EmptyCondition.instance;
            }
        });
        registerCondition("!equals", (args) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return block -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(null, v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(null, v2) : v2;
                    return !p1.equals(p2);
                };
            } else {
                LogUtils.warn("Wrong value format found at !equals requirement.");
                return EmptyCondition.instance;
            }
        });
    }

    private void registerSeasonCondition() {
        registerCondition("suitable_season", (args) -> {
            HashSet<String> seasons = new HashSet<>(ConfigUtils.stringListArgs(args).stream().map(it -> it.toUpperCase(Locale.ENGLISH)).toList());
            return block -> {
                Season season = plugin.getIntegrationManager().getSeason(block.getLocation().getBukkitWorld());
                if (season == null) {
                    return true;
                }
                if (seasons.contains(season.name())) {
                    return true;
                }
                if (ConfigManager.enableGreenhouse()) {
                    SimpleLocation location = block.getLocation();
                    Optional<CustomCropsWorld> world = plugin.getWorldManager().getCustomCropsWorld(location.getWorldName());
                    if (world.isEmpty()) return false;
                    CustomCropsWorld customCropsWorld = world.get();
                    for (int i = 1, range = ConfigManager.greenhouseRange(); i <= range; i++) {
                        if (customCropsWorld.getGlassAt(location.copy().add(0,i,0)).isPresent()) {
                            return true;
                        }
                    }
                }
                return false;
            };
        });
        registerCondition("unsuitable_season", (args) -> {
            HashSet<String> seasons = new HashSet<>(ConfigUtils.stringListArgs(args).stream().map(it -> it.toUpperCase(Locale.ENGLISH)).toList());
            return block -> {
                Season season = plugin.getIntegrationManager().getSeason(block.getLocation().getBukkitWorld());
                if (season == null) {
                    return false;
                }
                if (seasons.contains(season.name())) {
                    if (ConfigManager.enableGreenhouse()) {
                        SimpleLocation location = block.getLocation();
                        Optional<CustomCropsWorld> world = plugin.getWorldManager().getCustomCropsWorld(location.getWorldName());
                        if (world.isEmpty()) return false;
                        CustomCropsWorld customCropsWorld = world.get();
                        for (int i = 1, range = ConfigManager.greenhouseRange(); i <= range; i++) {
                            if (customCropsWorld.getGlassAt(location.copy().add(0,i,0)).isPresent()) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
                return false;
            };
        });
    }

    private void registerWaterCondition() {
        registerCondition("water_more_than", (args) -> {
            int value = (int) args;
            return block -> {
                Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(block.getLocation().copy().add(0,-1,0));
                return worldPot.filter(pot -> pot.getWater() > value).isPresent();
            };
        });
        registerCondition("water_less_than", (args) -> {
            int value = (int) args;
            return block -> {
                Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(block.getLocation().copy().add(0,-1,0));
                return worldPot.filter(pot -> pot.getWater() < value).isPresent();
            };
        });
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

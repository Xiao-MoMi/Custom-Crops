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

package net.momirealms.customcrops.mechanic.requirement;

import net.momirealms.biomeapi.BiomeAPI;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Pair;
import net.momirealms.customcrops.api.integration.LevelInterface;
import net.momirealms.customcrops.api.integration.SeasonInterface;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.manager.RequirementManager;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.mechanic.requirement.RequirementExpansion;
import net.momirealms.customcrops.api.mechanic.requirement.RequirementFactory;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.compatibility.VaultHook;
import net.momirealms.customcrops.compatibility.papi.ParseUtils;
import net.momirealms.customcrops.util.ClassUtils;
import net.momirealms.customcrops.util.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class RequirementManagerImpl implements RequirementManager {

    private final CustomCropsPlugin plugin;
    private final HashMap<String, RequirementFactory> requirementBuilderMap;
    private final String EXPANSION_FOLDER = "expansions/requirement";

    public RequirementManagerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
        this.requirementBuilderMap = new HashMap<>();
        this.registerInbuiltRequirements();
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
        this.requirementBuilderMap.clear();
    }

    @Override
    public boolean registerRequirement(String type, RequirementFactory requirementFactory) {
        if (this.requirementBuilderMap.containsKey(type)) return false;
        this.requirementBuilderMap.put(type, requirementFactory);
        return true;
    }

    @Override
    public boolean unregisterRequirement(String type) {
        return this.requirementBuilderMap.remove(type) != null;
    }

    private void registerInbuiltRequirements() {
        this.registerTimeRequirement();
        this.registerYRequirement();
        this.registerContainRequirement();
        this.registerStartWithRequirement();
        this.registerEndWithRequirement();
        this.registerEqualsRequirement();
        this.registerBiomeRequirement();
        this.registerDateRequirement();
        this.registerPluginLevelRequirement();
        this.registerPermissionRequirement();
        this.registerWorldRequirement();
        this.registerWeatherRequirement();
        this.registerSeasonRequirement();
        this.registerGreaterThanRequirement();
        this.registerAndRequirement();
        this.registerOrRequirement();
        this.registerLevelRequirement();
        this.registerRandomRequirement();
        this.registerCoolDownRequirement();
        this.registerLessThanRequirement();
        this.registerNumberEqualRequirement();
        this.registerRegexRequirement();
        this.registerMoneyRequirement();
        this.registerEnvironmentRequirement();
        this.registerPotionEffectRequirement();
        this.registerInListRequirement();
        this.registerItemInHandRequirement();
        this.registerSneakRequirement();
        this.registerTemperatureRequirement();
        this.registerFertilizerRequirement();
        this.registerLightRequirement();
        this.registerGameModeRequirement();
    }

    @NotNull
    @Override
    public Requirement[] getRequirements(ConfigurationSection section, boolean advanced) {
        List<Requirement> requirements = new ArrayList<>();
        if (section == null) {
            return requirements.toArray(new Requirement[0]);
        }
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            String typeOrName = entry.getKey();
            if (hasRequirement(typeOrName)) {
                requirements.add(getRequirement(typeOrName, entry.getValue()));
            } else {
                requirements.add(getRequirement(section.getConfigurationSection(typeOrName), advanced));
            }
        }
        return requirements.toArray(new Requirement[0]);
    }

    @NotNull
    @Override
    public Requirement getRequirement(ConfigurationSection section, boolean advanced) {
        if (section == null) return EmptyRequirement.instance;
        List<Action> actionList = null;
        if (advanced) {
            actionList = new ArrayList<>();
            if (section.contains("not-met-actions")) {
                for (Map.Entry<String, Object> entry : Objects.requireNonNull(section.getConfigurationSection("not-met-actions")).getValues(false).entrySet()) {
                    if (entry.getValue() instanceof MemorySection inner) {
                        actionList.add(plugin.getActionManager().getAction(inner));
                    }
                }
            }
            if (section.contains("message")) {
                List<String> messages = ConfigUtils.stringListArgs(section.get("message"));
                actionList.add(plugin.getActionManager().getActionFactory("message").build(messages, 1));
            }
            if (actionList.size() == 0)
                actionList = null;
        }
        String type = section.getString("type");
        if (type == null) {
            LogUtils.warn("No requirement type found at " + section.getCurrentPath());
            return EmptyRequirement.instance;
        }
        var builder = getRequirementFactory(type);
        if (builder == null) {
            return EmptyRequirement.instance;
        }
        return builder.build(section.get("value"), actionList, advanced);
    }

    @Override
    @NotNull
    public Requirement getRequirement(String type, Object value) {
        RequirementFactory factory = getRequirementFactory(type);
        if (factory == null) {
            LogUtils.warn("Requirement type: " + type + " doesn't exist.");
            return EmptyRequirement.instance;
        }
        return factory.build(value);
    }

    @Override
    @Nullable
    public RequirementFactory getRequirementFactory(String type) {
        return requirementBuilderMap.get(type);
    }

    private void registerTimeRequirement() {
        registerRequirement("time", (args, actions, advanced) -> {
            List<Pair<Integer, Integer>> timePairs = ConfigUtils.stringListArgs(args).stream().map(it -> ConfigUtils.splitStringIntegerArgs(it, "~")).toList();
            return state -> {
                long time = state.getLocation().getWorld().getTime();
                for (Pair<Integer, Integer> pair : timePairs)
                    if (time >= pair.left() && time <= pair.right())
                        return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void registerYRequirement() {
        registerRequirement("ypos", (args, actions, advanced) -> {
            List<Pair<Integer, Integer>> timePairs = ConfigUtils.stringListArgs(args).stream().map(it -> ConfigUtils.splitStringIntegerArgs(it, "~")).toList();
            return state -> {
                int y = state.getLocation().getBlockY();
                for (Pair<Integer, Integer> pair : timePairs)
                    if (y >= pair.left() && y <= pair.right())
                        return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void registerGameModeRequirement() {
        registerRequirement("gamemode", (args, actions, advanced) -> {
            List<String> modes = ConfigUtils.stringListArgs(args);
            return condition -> {
                if (condition.getPlayer() == null) return true;
                var name = condition.getPlayer().getGameMode().name().toLowerCase(Locale.ENGLISH);
                if (modes.contains(name)) {
                    return true;
                }
                if (advanced) triggerActions(actions, condition);
                return false;
            };
        });
    }

    private void registerTemperatureRequirement() {
        registerRequirement("temperature", (args, actions, advanced) -> {
            List<Pair<Integer, Integer>> tempPairs = ConfigUtils.stringListArgs(args).stream().map(it -> ConfigUtils.splitStringIntegerArgs(it, "~")).toList();
            return state -> {
                Location location = state.getLocation();
                double temp = location.getWorld().getTemperature(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                for (Pair<Integer, Integer> pair : tempPairs)
                    if (temp >= pair.left() && temp <= pair.right())
                        return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void registerLightRequirement() {
        registerRequirement("light", (args, actions, advanced) -> {
            List<Pair<Integer, Integer>> tempPairs = ConfigUtils.stringListArgs(args).stream().map(it -> ConfigUtils.splitStringIntegerArgs(it, "~")).toList();
            return state -> {
                Location location = state.getLocation();
                int temp = location.getBlock().getLightLevel();
                for (Pair<Integer, Integer> pair : tempPairs)
                    if (temp >= pair.left() && temp <= pair.right())
                        return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
        registerRequirement("natural-light", (args, actions, advanced) -> {
            List<Pair<Integer, Integer>> tempPairs = ConfigUtils.stringListArgs(args).stream().map(it -> ConfigUtils.splitStringIntegerArgs(it, "~")).toList();
            return state -> {
                Location location = state.getLocation();
                int temp = location.getBlock().getLightFromSky();
                for (Pair<Integer, Integer> pair : tempPairs)
                    if (temp >= pair.left() && temp <= pair.right())
                        return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void registerOrRequirement() {
        registerRequirement("||", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                Requirement[] requirements = getRequirements(section, advanced);
                return state -> {
                    for (Requirement requirement : requirements) {
                        if (requirement.isStateMet(state)) {
                            return true;
                        }
                    }
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at || requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerAndRequirement() {
        registerRequirement("&&", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                Requirement[] requirements = getRequirements(section, advanced);
                return state -> {
                    outer: {
                        for (Requirement requirement : requirements) {
                            if (!requirement.isStateMet(state)) {
                                break outer;
                            }
                        }
                        return true;
                    }
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at && requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerLevelRequirement() {
        registerRequirement("level", (args, actions, advanced) -> {
            int level = (int) args;
            return state -> {
                if (state.getPlayer() == null) return true;
                int current = state.getPlayer().getLevel();
                if (current >= level)
                    return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void registerMoneyRequirement() {
        registerRequirement("money", (args, actions, advanced) -> {
            double money = ConfigUtils.getDoubleValue(args);
            return state -> {
                if (state.getPlayer() == null) return true;
                double current = VaultHook.getEconomy().getBalance(state.getPlayer());
                if (current >= money)
                    return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void registerRandomRequirement() {
        registerRequirement("random", (args, actions, advanced) -> {
            double random = ConfigUtils.getDoubleValue(args);
            return state -> {
                if (Math.random() < random)
                    return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void registerBiomeRequirement() {
        registerRequirement("biome", (args, actions, advanced) -> {
            HashSet<String> biomes = new HashSet<>(ConfigUtils.stringListArgs(args));
            return state -> {
                String currentBiome = BiomeAPI.getBiomeAt(state.getLocation());
                    if (biomes.contains(currentBiome))
                        return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
        registerRequirement("!biome", (args, actions, advanced) -> {
            HashSet<String> biomes = new HashSet<>(ConfigUtils.stringListArgs(args));
            return state -> {
                String currentBiome = BiomeAPI.getBiomeAt(state.getLocation());
                if (!biomes.contains(currentBiome))
                    return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void registerWorldRequirement() {
        registerRequirement("world", (args, actions, advanced) -> {
            HashSet<String> worlds = new HashSet<>(ConfigUtils.stringListArgs(args));
            return state -> {
                if (worlds.contains(state.getLocation().getWorld().getName()))
                    return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
        registerRequirement("!world", (args, actions, advanced) -> {
            HashSet<String> worlds = new HashSet<>(ConfigUtils.stringListArgs(args));
            return state -> {
                if (!worlds.contains(state.getLocation().getWorld().getName()))
                    return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void registerWeatherRequirement() {
        registerRequirement("weather", (args, actions, advanced) -> {
            List<String> weathers = ConfigUtils.stringListArgs(args);
            return state -> {
                String currentWeather;
                World world = state.getLocation().getWorld();
                if (world.isClearWeather()) currentWeather = "clear";
                else if (world.isThundering()) currentWeather = "thunder";
                else currentWeather = "rain";
                for (String weather : weathers)
                    if (weather.equalsIgnoreCase(currentWeather))
                        return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void registerCoolDownRequirement() {
        registerRequirement("cooldown", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String key = section.getString("key");
                int time = section.getInt("time");
                return state -> {
                    if (state.getPlayer() == null) return true;
                    if (!plugin.getCoolDownManager().isCoolDown(state.getPlayer().getUniqueId(), key, time)) {
                        return true;
                    }
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at cooldown requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerDateRequirement() {
        registerRequirement("date", (args, actions, advanced) -> {
            HashSet<String> dates = new HashSet<>(ConfigUtils.stringListArgs(args));
            return state -> {
                Calendar calendar = Calendar.getInstance();
                String current = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE);
                if (dates.contains(current))
                    return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void registerSneakRequirement() {
        registerRequirement("sneak", (args, actions, advanced) -> {
            boolean sneak = (boolean) args;
            return state -> {
                if (state.getPlayer() == null) return true;
                if (sneak) {
                    if (state.getPlayer().isSneaking())
                        return true;
                } else {
                    if (!state.getPlayer().isSneaking())
                        return true;
                }
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void registerPermissionRequirement() {
        registerRequirement("permission", (args, actions, advanced) -> {
            List<String> perms = ConfigUtils.stringListArgs(args);
            return state -> {
                if (state.getPlayer() == null) return true;
                for (String perm : perms)
                    if (state.getPlayer().hasPermission(perm))
                        return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
        registerRequirement("!permission", (args, actions, advanced) -> {
            List<String> perms = ConfigUtils.stringListArgs(args);
            return state -> {
                if (state.getPlayer() == null) return true;
                for (String perm : perms)
                    if (state.getPlayer().hasPermission(perm)) {
                        if (advanced) triggerActions(actions, state);
                        return false;
                    }
                return true;
            };
        });
    }

    private void registerSeasonRequirement() {
        registerRequirement("season", (args, actions, advanced) -> {
            HashSet<String> seasons = new HashSet<>(ConfigUtils.stringListArgs(args).stream().map(str -> str.toUpperCase(Locale.ENGLISH)).toList());
            return state -> {
                Location location = state.getLocation();
                SeasonInterface seasonInterface = plugin.getIntegrationManager().getSeasonInterface();
                if (seasonInterface == null) return true;
                Season season = seasonInterface.getSeason(location.getWorld());
                if (season == null) return true;
                if (seasons.contains(season.name())) return true;
                if (ConfigManager.enableGreenhouse()) {
                    for (int i = 1; i <= ConfigManager.greenhouseRange(); i++) {
                        if (plugin.getWorldManager().getGlassAt(SimpleLocation.of(location.clone().add(0,i,0))).isPresent()) {
                            return true;
                        }
                    }
                }
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    @SuppressWarnings("DuplicatedCode")
    private void registerGreaterThanRequirement() {
        registerRequirement(">=", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (Double.parseDouble(p1) >= Double.parseDouble(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at >= requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement(">", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (Double.parseDouble(p1) > Double.parseDouble(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at > requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerRegexRequirement() {
        registerRequirement("regex", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("papi", "");
                String v2 = section.getString("regex", "");
                return state -> {
                    if (ParseUtils.setPlaceholders(state.getPlayer(), v1).matches(v2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at regex requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerNumberEqualRequirement() {
        registerRequirement("=", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (Double.parseDouble(p1) == Double.parseDouble(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at = requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("==", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (Double.parseDouble(p1) == Double.parseDouble(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at == requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("!=", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (Double.parseDouble(p1) != Double.parseDouble(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at != requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerFertilizerRequirement() {
        registerRequirement("fertilizer", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                boolean has = section.getBoolean("has");
                HashSet<String> keys = new HashSet<>(ConfigUtils.stringListArgs(section.get("key")));
                int y = section.getInt("y", 0);
                return condition -> {
                    Location location = condition.getLocation().clone().add(0,y,0);
                    SimpleLocation simpleLocation = SimpleLocation.of(location);
                    Optional<CustomCropsBlock> optionalCustomCropsBlock = plugin.getWorldManager().getBlockAt(simpleLocation);
                    if (optionalCustomCropsBlock.isPresent()) {
                        if (optionalCustomCropsBlock.get() instanceof WorldPot pot) {
                            Fertilizer fertilizer = pot.getFertilizer();
                            if (fertilizer == null) {
                                if (!has && keys.size() == 0) {
                                    return true;
                                }
                            } else {
                                String key = fertilizer.getKey();
                                if (has) {
                                    if (keys.size() == 0) {
                                        return true;
                                    }
                                    if (keys.contains(key)) {
                                        return true;
                                    }
                                } else {
                                    if (!keys.contains(key)) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    if (advanced) triggerActions(actions, condition);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at fertilizer requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerItemInHandRequirement() {
        registerRequirement("item-in-hand", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                int amount = section.getInt("amount", 0);
                List<String> items = ConfigUtils.stringListArgs(section.get("item"));
                return condition -> {
                    ItemStack itemStack = condition.getItemInHand();
                    if (itemStack == null) itemStack = new ItemStack(Material.AIR);
                    String id;
                    if (itemStack.getType() == Material.AIR || itemStack.getAmount() == 0) {
                        id = "AIR";
                    } else {
                        id = plugin.getItemManager().getItemID(itemStack);
                    }
                    if ((items.contains(id) || items.contains("*")) && itemStack.getAmount() >= amount) return true;
                    if (advanced) triggerActions(actions, condition);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at item-in-hand requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    @SuppressWarnings("DuplicatedCode")
    private void registerLessThanRequirement() {
        registerRequirement("<", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (Double.parseDouble(p1) < Double.parseDouble(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at < requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("<=", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (Double.parseDouble(p1) <= Double.parseDouble(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at <= requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerStartWithRequirement() {
        registerRequirement("startsWith", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (p1.startsWith(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at startsWith requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("!startsWith", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (!p1.startsWith(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at !startsWith requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerEndWithRequirement() {
        registerRequirement("endsWith", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (p1.endsWith(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at endsWith requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("!endsWith", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (!p1.endsWith(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at !endsWith requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerContainRequirement() {
        registerRequirement("contains", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (p1.contains(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at contains requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("!contains", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (!p1.contains(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at !contains requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerInListRequirement() {
        registerRequirement("in-list", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String papi = section.getString("papi", "");
                HashSet<String> values = new HashSet<>(ConfigUtils.stringListArgs(section.get("values")));
                return state -> {
                    String p1 = papi.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), papi) : papi;
                    if (values.contains(p1)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at in-list requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("!in-list", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String papi = section.getString("papi", "");
                HashSet<String> values = new HashSet<>(ConfigUtils.stringListArgs(section.get("values")));
                return state -> {
                    String p1 = papi.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), papi) : papi;
                    if (!values.contains(p1)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at !in-list requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerEqualsRequirement() {
        registerRequirement("equals", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (p1.equals(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at equals requirement.");
                return EmptyRequirement.instance;
            }
        });
        registerRequirement("!equals", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String v1 = section.getString("value1", "");
                String v2 = section.getString("value2", "");
                return state -> {
                    String p1 = v1.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v1) : v1;
                    String p2 = v2.startsWith("%") ? ParseUtils.setPlaceholders(state.getPlayer(), v2) : v2;
                    if (!p1.equals(p2)) return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at !equals requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerEnvironmentRequirement() {
        registerRequirement("environment", (args, actions, advanced) -> {
            List<String> environments = ConfigUtils.stringListArgs(args);
            return state -> {
                var name = state.getLocation().getWorld().getEnvironment().name().toLowerCase(Locale.ENGLISH);
                if (environments.contains(name)) return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
        registerRequirement("!environment", (args, actions, advanced) -> {
            List<String> environments = ConfigUtils.stringListArgs(args);
            return state -> {
                var name = state.getLocation().getWorld().getEnvironment().name().toLowerCase(Locale.ENGLISH);
                if (!environments.contains(name)) return true;
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void registerPluginLevelRequirement() {
        registerRequirement("plugin-level", (args, actions, advanced) -> {
            if (args instanceof ConfigurationSection section) {
                String pluginName = section.getString("plugin");
                int level = section.getInt("level");
                String target = section.getString("target");
                return state -> {
                    LevelInterface levelInterface = plugin.getIntegrationManager().getLevelPlugin(pluginName);
                    if (levelInterface == null) {
                        LogUtils.warn("Plugin (" + pluginName + "'s) level is not compatible. Please double check if it's a problem caused by pronunciation.");
                        return true;
                    }
                    if (levelInterface.getLevel(state.getPlayer(), target) >= level)
                        return true;
                    if (advanced) triggerActions(actions, state);
                    return false;
                };
            } else {
                LogUtils.warn("Wrong value format found at plugin-level requirement.");
                return EmptyRequirement.instance;
            }
        });
    }

    private void registerPotionEffectRequirement() {
        registerRequirement("potion-effect", (args, actions, advanced) -> {
            String potions = (String) args;
            String[] split = potions.split("(<=|>=|<|>|==|=)", 2);
            PotionEffectType type = PotionEffectType.getByName(split[0]);
            if (type == null) {
                LogUtils.warn("Potion effect doesn't exist: " + split[0]);
                return EmptyRequirement.instance;
            }
            int required = Integer.parseInt(split[1]);
            String operator = potions.substring(split[0].length(), potions.length() - split[1].length());
            return state -> {
                if (state.getPlayer() == null) return true;
                int level = -1;
                PotionEffect potionEffect = state.getPlayer().getPotionEffect(type);
                if (potionEffect != null) {
                    level = potionEffect.getAmplifier();
                }
                boolean result = false;
                switch (operator) {
                    case ">=" -> {
                        if (level >= required) result = true;
                    }
                    case ">" -> {
                        if (level > required) result = true;
                    }
                    case "==", "=" -> {
                        if (level == required) result = true;
                    }
                    case "!=" -> {
                        if (level != required) result = true;
                    }
                    case "<=" -> {
                        if (level <= required) result = true;
                    }
                    case "<" -> {
                        if (level < required) result = true;
                    }
                }
                if (result) {
                    return true;
                }
                if (advanced) triggerActions(actions, state);
                return false;
            };
        });
    }

    private void triggerActions(List<Action> actions, State state) {
        if (actions != null)
            for (Action action : actions)
                action.trigger(state);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadExpansions() {
        File expansionFolder = new File(plugin.getDataFolder(), EXPANSION_FOLDER);
        if (!expansionFolder.exists())
            expansionFolder.mkdirs();

        List<Class<? extends RequirementExpansion>> classes = new ArrayList<>();
        File[] expansionJars = expansionFolder.listFiles();
        if (expansionJars == null) return;
        for (File expansionJar : expansionJars) {
            if (expansionJar.getName().endsWith(".jar")) {
                try {
                    Class<? extends RequirementExpansion> expansionClass = ClassUtils.findClass(expansionJar, RequirementExpansion.class);
                    classes.add(expansionClass);
                } catch (IOException | ClassNotFoundException e) {
                    LogUtils.warn("Failed to load expansion: " + expansionJar.getName(), e);
                }
            }
        }
        try {
            for (Class<? extends RequirementExpansion> expansionClass : classes) {
                RequirementExpansion expansion = expansionClass.getDeclaredConstructor().newInstance();
                unregisterRequirement(expansion.getRequirementType());
                registerRequirement(expansion.getRequirementType(), expansion.getRequirementFactory());
                LogUtils.info("Loaded requirement expansion: " + expansion.getRequirementType() + "[" + expansion.getVersion() + "]" + " by " + expansion.getAuthor());
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            LogUtils.warn("Error occurred when creating expansion instance.", e);
        }
    }
}

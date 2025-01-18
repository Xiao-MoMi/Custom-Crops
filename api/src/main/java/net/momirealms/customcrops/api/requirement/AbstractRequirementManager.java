/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.api.requirement;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.ConfigManager;
import net.momirealms.customcrops.api.core.block.GreenhouseBlock;
import net.momirealms.customcrops.api.core.block.PotBlock;
import net.momirealms.customcrops.api.core.block.ScarecrowBlock;
import net.momirealms.customcrops.api.core.mechanic.crop.CrowAttack;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.core.world.*;
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.api.misc.value.TextValue;
import net.momirealms.customcrops.api.util.MoonPhase;
import net.momirealms.customcrops.common.util.ClassUtils;
import net.momirealms.customcrops.common.util.ListUtils;
import net.momirealms.customcrops.common.util.Pair;
import net.momirealms.sparrow.heart.SparrowHeart;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Farmland;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("DuplicatedCode")
public abstract class AbstractRequirementManager<T> implements RequirementManager<T> {

    private final HashMap<String, RequirementFactory<T>> requirementFactoryMap = new HashMap<>();
    private static final String EXPANSION_FOLDER = "expansions/requirement";
    protected final BukkitCustomCropsPlugin plugin;
    protected Class<T> tClass;

    public AbstractRequirementManager(BukkitCustomCropsPlugin plugin, Class<T> tClass) {
        this.plugin = plugin;
        this.tClass = tClass;
        this.registerBuiltInRequirements();
    }

    protected void registerBuiltInRequirements() {
        this.registerEnvironmentRequirement();
        this.registerTimeRequirement();
        this.registerYRequirement();
        this.registerAndRequirement();
        this.registerOrRequirement();
        this.registerMoonPhaseRequirement();
        this.registerRandomRequirement();
        this.registerBiomeRequirement();
        this.registerSeasonRequirement();
        this.registerWorldRequirement();
        this.registerDateRequirement();
        this.registerWeatherRequirement();
        this.registerPAPIRequirement();
        this.registerLightRequirement();
        this.registerTemperatureRequirement();
        this.registerFertilizerRequirement();
        this.registerPotRequirement();
        this.registerCrowAttackRequirement();
        this.registerWaterRequirement();
        this.registerImpossibleRequirement();
    }

    @Override
    public boolean registerRequirement(@NotNull RequirementFactory<T> requirementFactory, @NotNull String... types) {
        for (String type : types) {
            if (this.requirementFactoryMap.containsKey(type)) return false;
        }
        for (String type : types) {
            this.requirementFactoryMap.put(type, requirementFactory);
        }
        return true;
    }

    @Override
    public boolean unregisterRequirement(@NotNull String type) {
        return this.requirementFactoryMap.remove(type) != null;
    }

    @Override
    public boolean hasRequirement(@NotNull String type) {
        return requirementFactoryMap.containsKey(type);
    }

    @Nullable
    @Override
    public RequirementFactory<T> getRequirementFactory(@NotNull String type) {
        return requirementFactoryMap.get(type);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    @Override
    public Requirement<T>[] parseRequirements(Section section, boolean runActions) {
        List<Requirement<T>> requirements = new ArrayList<>();
        if (section != null)
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                String typeOrName = entry.getKey();
                if (hasRequirement(typeOrName)) {
                    requirements.add(parseRequirement(typeOrName, entry.getValue()));
                } else {
                    Section inner = section.getSection(typeOrName);
                    if (inner != null) {
                        requirements.add(parseRequirement(inner, runActions));
                    } else {
                        plugin.getPluginLogger().warn("Section " + section.getRouteAsString() + "." + typeOrName + " is misconfigured");
                    }
                }
            }
        return requirements.toArray(new Requirement[0]);
    }

    @NotNull
    @Override
    public Requirement<T> parseRequirement(@NotNull Section section, boolean runActions) {
        List<Action<T>> actionList = new ArrayList<>();
        if (runActions && section.contains("not-met-actions")) {
            Action<T>[] actions = plugin.getActionManager(tClass).parseActions(requireNonNull(section.getSection("not-met-actions")));
            actionList.addAll(List.of(actions));
        }
        String type = section.getString("type");
        if (type == null) {
            plugin.getPluginLogger().warn("No requirement type found at " + section.getRouteAsString());
            return Requirement.empty();
        }
        var factory = getRequirementFactory(type);
        if (factory == null) {
            plugin.getPluginLogger().warn("Requirement type: " + type + " not exists");
            return Requirement.empty();
        }
        return factory.process(section.get("value"), actionList, runActions);
    }

    @NotNull
    @Override
    public Requirement<T> parseRequirement(@NotNull String type, @NotNull Object value) {
        RequirementFactory<T> factory = getRequirementFactory(type);
        if (factory == null) {
            plugin.getPluginLogger().warn("Requirement type: " + type + " doesn't exist.");
            return Requirement.empty();
        }
        return factory.process(value);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
    protected void loadExpansions(Class<T> tClass) {
        File expansionFolder = new File(plugin.getDataFolder(), EXPANSION_FOLDER);
        if (!expansionFolder.exists())
            expansionFolder.mkdirs();
        List<Class<? extends RequirementExpansion<T>>> classes = new ArrayList<>();
        File[] expansionJars = expansionFolder.listFiles();
        if (expansionJars == null) return;
        for (File expansionJar : expansionJars) {
            if (expansionJar.getName().endsWith(".jar")) {
                try {
                    Class<? extends RequirementExpansion<T>> expansionClass = (Class<? extends RequirementExpansion<T>>) ClassUtils.findClass(expansionJar, RequirementExpansion.class, tClass);
                    classes.add(expansionClass);
                } catch (IOException | ClassNotFoundException e) {
                    plugin.getPluginLogger().warn("Failed to load expansion: " + expansionJar.getName(), e);
                }
            }
        }
        try {
            for (Class<? extends RequirementExpansion<T>> expansionClass : classes) {
                RequirementExpansion<T> expansion = expansionClass.getDeclaredConstructor().newInstance();
                unregisterRequirement(expansion.getRequirementType());
                registerRequirement(expansion.getRequirementFactory(), expansion.getRequirementType());
                plugin.getPluginLogger().info("Loaded requirement expansion: " + expansion.getRequirementType() + "[" + expansion.getVersion() + "]" + " by " + expansion.getAuthor());
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            plugin.getPluginLogger().warn("Error occurred when creating expansion instance.", e);
        }
    }

    protected void registerImpossibleRequirement() {
        registerRequirement((args, actions, advanced) -> context -> false, "impossible");
    }

    protected void registerEnvironmentRequirement() {
        registerRequirement((args, actions, advanced) -> {
            List<String> environments = ListUtils.toList(args);
            return context -> {
                Location location = context.arg(ContextKeys.LOCATION);
                if (location == null) return false;
                var name = location.getWorld().getEnvironment().name().toLowerCase(Locale.ENGLISH);
                if (environments.contains(name)) return true;
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "environment");
        registerRequirement((args, actions, advanced) -> {
            List<String> environments = ListUtils.toList(args);
            return context -> {
                Location location = context.arg(ContextKeys.LOCATION);
                if (location == null) return false;
                var name = location.getWorld().getEnvironment().name().toLowerCase(Locale.ENGLISH);
                if (!environments.contains(name)) return true;
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "!environment");
    }

    protected void registerTimeRequirement() {
        registerRequirement((args, actions, runActions) -> {
            List<String> list = ListUtils.toList(args);
            List<Pair<Integer, Integer>> timePairs = list.stream().map(line -> {
                String[] split = line.split("~");
                return new Pair<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }).toList();
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                long time = location.getWorld().getTime();
                for (Pair<Integer, Integer> pair : timePairs)
                    if (time >= pair.left() && time <= pair.right())
                        return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "time");
    }

    protected void registerYRequirement() {
        registerRequirement((args, actions, runActions) -> {
            List<String> list = ListUtils.toList(args);
            List<Pair<Double, Double>> posPairs = list.stream().map(line -> {
                String[] split = line.split("~");
                return new Pair<>(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
            }).toList();
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                double y = location.getY();
                for (Pair<Double, Double> pair : posPairs)
                    if (y >= pair.left() && y <= pair.right())
                        return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "ypos");
    }

    protected void registerOrRequirement() {
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                Requirement<T>[] requirements = parseRequirements(section, runActions);
                return context -> {
                    for (Requirement<T> requirement : requirements)
                        if (requirement.isSatisfied(context))
                            return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at || requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "||");
    }

    protected void registerAndRequirement() {
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                Requirement<T>[] requirements = parseRequirements(section, runActions);
                return context -> {
                    outer: {
                        for (Requirement<T> requirement : requirements)
                            if (!requirement.isSatisfied(context))
                                break outer;
                        return true;
                    }
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at && requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "&&");
    }

    protected void registerRandomRequirement() {
        registerRequirement((args, actions, runActions) -> {
            MathValue<T> value = MathValue.auto(args);
            return context -> {
                if (Math.random() < value.evaluate(context, true))
                    return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "random");
    }

    protected void registerBiomeRequirement() {
        registerRequirement((args, actions, runActions) -> {
            HashSet<String> biomes = new HashSet<>(ListUtils.toList(args));
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                String currentBiome = SparrowHeart.getInstance().getBiomeResourceLocation(location);
                if (biomes.contains(currentBiome))
                    return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "biome");
        registerRequirement((args, actions, runActions) -> {
            HashSet<String> biomes = new HashSet<>(ListUtils.toList(args));
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                String currentBiome = SparrowHeart.getInstance().getBiomeResourceLocation(location);
                if (!biomes.contains(currentBiome))
                    return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "!biome");
    }

    protected void registerMoonPhaseRequirement() {
        registerRequirement((args, actions, runActions) -> {
            HashSet<String> moonPhases = new HashSet<>(ListUtils.toList(args));
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                long days = location.getWorld().getFullTime() / 24_000;
                if (moonPhases.contains(MoonPhase.getPhase(days).name().toLowerCase(Locale.ENGLISH)))
                    return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "moon-phase");
        registerRequirement((args, actions, runActions) -> {
            HashSet<String> moonPhases = new HashSet<>(ListUtils.toList(args));
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                long days = location.getWorld().getFullTime() / 24_000;
                if (!moonPhases.contains(MoonPhase.getPhase(days).name().toLowerCase(Locale.ENGLISH)))
                    return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "!moon-phase");
    }

    protected void registerWorldRequirement() {
        registerRequirement((args, actions, runActions) -> {
            HashSet<String> worlds = new HashSet<>(ListUtils.toList(args));
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                if (worlds.contains(location.getWorld().getName()))
                    return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "world");
        registerRequirement((args, actions, runActions) -> {
            HashSet<String> worlds = new HashSet<>(ListUtils.toList(args));
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                if (!worlds.contains(location.getWorld().getName()))
                    return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "!world");
    }

    protected void registerWeatherRequirement() {
        registerRequirement((args, actions, runActions) -> {
            HashSet<String> weathers = new HashSet<>(ListUtils.toList(args));
            return context -> {
                String currentWeather;
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                World world = location.getWorld();
                if (world.isClearWeather()) currentWeather = "clear";
                else if (world.isThundering()) currentWeather = "thunder";
                else currentWeather = "rain";
                if (weathers.contains(currentWeather)) return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "weather");
    }

    protected void registerDateRequirement() {
        registerRequirement((args, actions, runActions) -> {
            HashSet<String> dates = new HashSet<>(ListUtils.toList(args));
            return context -> {
                Calendar calendar = Calendar.getInstance();
                String current = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE);
                if (dates.contains(current))
                    return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "date");
    }

    protected void registerSeasonRequirement() {
        registerRequirement((args, actions, runActions) -> {
            Set<String> seasons = new HashSet<>(ListUtils.toList(args).stream().map(it -> it.toUpperCase(Locale.ENGLISH)).toList());
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                Season season = plugin.getWorldManager().getSeason(location.getWorld());
                if (season == Season.DISABLE) return true;
                if (!seasons.contains(season.name())) return true;
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "!season");
        registerRequirement((args, actions, runActions) -> {
            Set<String> seasons = new HashSet<>(ListUtils.toList(args).stream().map(it -> it.toUpperCase(Locale.ENGLISH)).toList());
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                Season season = plugin.getWorldManager().getSeason(location.getWorld());
                if (season == Season.DISABLE || seasons.contains(season.name())) {
                    return true;
                }
                if (ConfigManager.enableGreenhouse()) {
                    Pos3 pos3 = Pos3.from(location);
                    Optional<CustomCropsWorld<?>> world = plugin.getWorldManager().getWorld(location.getWorld());
                    if (world.isPresent()) {
                        CustomCropsWorld<?> cropsWorld = world.get();
                        for (int i = 1, range = ConfigManager.greenhouseRange(); i <= range; i++) {
                            Optional<CustomCropsBlockState> optionalState = cropsWorld.getBlockState(pos3.add(0,i,0));
                            if (optionalState.isPresent()) {
                                if (optionalState.get().type() instanceof GreenhouseBlock) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "suitable-season", "suitable_season", "season");
        registerRequirement((args, actions, runActions) -> {
            Set<String> seasons = new HashSet<>(ListUtils.toList(args).stream().map(it -> it.toUpperCase(Locale.ENGLISH)).toList());
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                Season season = plugin.getWorldManager().getSeason(location.getWorld());
                if (seasons.contains(season.name())) {
                    if (ConfigManager.enableGreenhouse()) {
                        Pos3 pos3 = Pos3.from(location);
                        Optional<CustomCropsWorld<?>> world = plugin.getWorldManager().getWorld(location.getWorld());
                        if (world.isPresent()) {
                            CustomCropsWorld<?> cropsWorld = world.get();
                            for (int i = 1, range = ConfigManager.greenhouseRange(); i <= range; i++) {
                                Optional<CustomCropsBlockState> optionalState = cropsWorld.getBlockState(pos3.add(0,i,0));
                                if (optionalState.isPresent()) {
                                    if (optionalState.get().type() instanceof GreenhouseBlock) {
                                        if (runActions) ActionManager.trigger(context, actions);
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                    return true;
                }
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "unsuitable-season", "unsuitable_season");
    }

    protected void registerPAPIRequirement() {
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                MathValue<T> v1 = MathValue.auto(section.get("value1"));
                MathValue<T> v2 = MathValue.auto(section.get("value2"));
                return context -> {
                    if (v1.evaluate(context, true) < v2.evaluate(context, true)) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at < requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "<");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                MathValue<T> v1 = MathValue.auto(section.get("value1"));
                MathValue<T> v2 = MathValue.auto(section.get("value2"));
                return context -> {
                    if (v1.evaluate(context, true) <= v2.evaluate(context, true)) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at <= requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "<=");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                MathValue<T> v1 = MathValue.auto(section.get("value1"));
                MathValue<T> v2 = MathValue.auto(section.get("value2"));
                return context -> {
                    if (v1.evaluate(context, true) != v2.evaluate(context, true)) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at != requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "!=");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                MathValue<T> v1 = MathValue.auto(section.get("value1"));
                MathValue<T> v2 = MathValue.auto(section.get("value2"));
                return context -> {
                    if (v1.evaluate(context, true) == v2.evaluate(context, true)) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at == requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "==", "=");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                MathValue<T> v1 = MathValue.auto(section.get("value1"));
                MathValue<T> v2 = MathValue.auto(section.get("value2"));
                return context -> {
                    if (v1.evaluate(context, true) >= v2.evaluate(context, true)) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at >= requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, ">=");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                MathValue<T> v1 = MathValue.auto(section.get("value1"));
                MathValue<T> v2 = MathValue.auto(section.get("value2"));
                return context -> {
                    if (v1.evaluate(context, true) > v2.evaluate(context, true)) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at > requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, ">");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                TextValue<T> v1 = TextValue.auto(section.getString("papi", ""));
                String v2 = section.getString("regex", "");
                return context -> {
                    if (v1.render(context, true).matches(v2)) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at regex requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "regex");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                TextValue<T> v1 = TextValue.auto(section.getString("value1", ""));
                TextValue<T> v2 = TextValue.auto(section.getString("value2", ""));
                return context -> {
                    if (v1.render(context, true).startsWith(v2.render(context, true))) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at startsWith requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "startsWith");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                TextValue<T> v1 = TextValue.auto(section.getString("value1", ""));
                TextValue<T> v2 = TextValue.auto(section.getString("value2", ""));
                return context -> {
                    if (!v1.render(context, true).startsWith(v2.render(context, true))) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at !startsWith requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "!startsWith");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                TextValue<T> v1 = TextValue.auto(section.getString("value1", ""));
                TextValue<T> v2 = TextValue.auto(section.getString("value2", ""));
                return context -> {
                    if (v1.render(context, true).endsWith(v2.render(context, true))) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at endsWith requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "endsWith");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                TextValue<T> v1 = TextValue.auto(section.getString("value1", ""));
                TextValue<T> v2 = TextValue.auto(section.getString("value2", ""));
                return context -> {
                    if (!v1.render(context, true).endsWith(v2.render(context, true))) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at !endsWith requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "!endsWith");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                TextValue<T> v1 = TextValue.auto(section.getString("value1", ""));
                TextValue<T> v2 = TextValue.auto(section.getString("value2", ""));
                return context -> {
                    if (v1.render(context, true).contains(v2.render(context, true))) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at contains requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "contains");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                TextValue<T> v1 = TextValue.auto(section.getString("value1", ""));
                TextValue<T> v2 = TextValue.auto(section.getString("value2", ""));
                return context -> {
                    if (!v1.render(context, true).contains(v2.render(context, true))) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at !contains requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "!contains");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                TextValue<T> papi = TextValue.auto(section.getString("papi", ""));
                List<String> values = ListUtils.toList(section.get("values"));
                return context -> {
                    if (values.contains(papi.render(context, true))) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at in-list requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "in-list");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                TextValue<T> papi = TextValue.auto(section.getString("papi", ""));
                List<String> values = ListUtils.toList(section.get("values"));
                return context -> {
                    if (!values.contains(papi.render(context, true))) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at !in-list requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "!in-list");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                TextValue<T> v1 = TextValue.auto(section.getString("value1", ""));
                TextValue<T> v2 = TextValue.auto(section.getString("value2", ""));

                return context -> {
                    if (v1.render(context, true).equals(v2.render(context, true))) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at equals requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "equals");
        registerRequirement((args, actions, runActions) -> {
            if (args instanceof Section section) {
                TextValue<T> v1 = TextValue.auto(section.getString("value1", ""));
                TextValue<T> v2 = TextValue.auto(section.getString("value2", ""));
                return context -> {
                    if (!v1.render(context, true).equals(v2.render(context, true))) return true;
                    if (runActions) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at !equals requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "!equals");
    }

    protected void registerFertilizerRequirement() {
        registerRequirement((args, actions, advanced) -> {
            if (args instanceof Section section) {
                boolean has = section.getBoolean("has");
                int y = section.getInt("y", 0);
                HashSet<String> types = new HashSet<>(ListUtils.toList(section.get("type")).stream().map(str -> str.toUpperCase(Locale.ENGLISH)).toList());
                return context -> {
                    Location location = requireNonNull(context.arg(ContextKeys.LOCATION)).clone().add(0,y,0);
                    Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(location.getWorld());
                    if (optionalWorld.isEmpty()) {
                        return false;
                    }
                    Pos3 pos3 = Pos3.from(location);
                    CustomCropsWorld<?> world = optionalWorld.get();
                    Optional<CustomCropsBlockState> optionalState = world.getBlockState(pos3);
                    if (optionalState.isPresent()) {
                        if (optionalState.get().type() instanceof PotBlock potBlock) {
                            Fertilizer[] fertilizers = potBlock.fertilizers(optionalState.get());
                            if (fertilizers.length == 0) {
                                if (!has && types.isEmpty()) {
                                    return true;
                                }
                            } else {
                                if (has) {
                                    if (types.isEmpty()) {
                                        return true;
                                    }
                                    for (Fertilizer fertilizer : fertilizers) {
                                        FertilizerConfig config = fertilizer.config();
                                        if (config != null) {
                                            if (types.contains(config.type().id())) {
                                                return true;
                                            }
                                        }
                                    }
                                } else {
                                    outer: {
                                        for (Fertilizer fertilizer : fertilizers) {
                                            FertilizerConfig config = fertilizer.config();
                                            if (config != null) {
                                                if (types.contains(config.type().id())) {
                                                    break outer;
                                                }
                                            }
                                        }
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    if (advanced) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at fertilizer-type requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "fertilizer_type", "fertilizer-type");
        registerRequirement((args, actions, advanced) -> {
            if (args instanceof Section section) {
                boolean has = section.getBoolean("has");
                int y = section.getInt("y", 0);
                HashSet<String> keys = new HashSet<>(ListUtils.toList(section.get("key")));
                return context -> {
                    Location location = requireNonNull(context.arg(ContextKeys.LOCATION)).clone().add(0,y,0);
                    Pos3 pos3 = Pos3.from(location);
                    Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(location.getWorld());
                    if (optionalWorld.isEmpty()) {
                        return false;
                    }
                    CustomCropsWorld<?> world = optionalWorld.get();
                    Optional<CustomCropsBlockState> optionalState = world.getBlockState(pos3);
                    if (optionalState.isPresent()) {
                        if (optionalState.get().type() instanceof PotBlock potBlock) {
                            Fertilizer[] fertilizers = potBlock.fertilizers(optionalState.get());
                            if (fertilizers.length == 0) {
                                if (!has && keys.isEmpty()) {
                                    return true;
                                }
                            } else {
                                if (has) {
                                    if (keys.isEmpty()) {
                                        return true;
                                    }
                                    for (Fertilizer fertilizer : fertilizers) {
                                        if (keys.contains(fertilizer.id())) {
                                            return true;
                                        }
                                    }
                                } else {
                                    outer: {
                                        for (Fertilizer fertilizer : fertilizers) {
                                            if (keys.contains(fertilizer.id())) {
                                                break outer;
                                            }
                                        }
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    if (advanced) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at fertilizer requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "fertilizer");
    }

    protected void registerLightRequirement() {
        registerRequirement((args, actions, advanced) -> {
            List<String> list = ListUtils.toList(args);
            List<Pair<Integer, Integer>> lightPairs = list.stream().map(line -> {
                String[] split = line.split("~");
                return new Pair<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }).toList();
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                int temp = location.getBlock().getLightLevel();
                for (Pair<Integer, Integer> pair : lightPairs)
                    if (temp >= pair.left() && temp <= pair.right())
                        return true;
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "light");
        registerRequirement((args, actions, advanced) -> {
            List<String> list = ListUtils.toList(args);
            List<Pair<Integer, Integer>> lightPairs = list.stream().map(line -> {
                String[] split = line.split("~");
                return new Pair<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }).toList();
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                int temp = location.getBlock().getLightFromSky();
                for (Pair<Integer, Integer> pair : lightPairs)
                    if (temp >= pair.left() && temp <= pair.right())
                        return true;
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "natural-light", "skylight");
        registerRequirement((args, actions, advanced) -> {
            int value = (int) args;
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                int light = location.getBlock().getLightFromSky();
                if (light > value) return true;
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "skylight_more_than", "skylight-more-than", "natural_light_more_than", "natural-light-more-than");
        registerRequirement((args, actions, advanced) -> {
            int value = (int) args;
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                int light = location.getBlock().getLightFromSky();
                if (light < value) return true;
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "skylight_less_than", "skylight-less-than", "natural_light_less_than", "natural-light-less-than");
        registerRequirement((args, actions, advanced) -> {
            int value = (int) args;
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                int light = location.getBlock().getLightLevel();
                if (light > value) return true;
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "light_more_than", "light-more-than");
        registerRequirement((args, actions, advanced) -> {
            int value = (int) args;
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                int light = location.getBlock().getLightLevel();
                if (light < value) return true;
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "light_less_than", "light-less-than");
    }

    protected void registerTemperatureRequirement() {
        registerRequirement((args, actions, advanced) -> {
            List<String> list = ListUtils.toList(args);
            List<Pair<Integer, Integer>> temperaturePairs = list.stream().map(line -> {
                String[] split = line.split("~");
                return new Pair<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }).toList();
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                double temp = location.getWorld().getTemperature(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                for (Pair<Integer, Integer> pair : temperaturePairs)
                    if (temp >= pair.left() && temp <= pair.right())
                        return true;
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "temperature");
    }

    private void registerPotRequirement() {
        registerRequirement((args, actions, advanced) -> {
            if (args instanceof Section section) {
                int y = section.getInt("y", 0);
                HashSet<String> ids = new HashSet<>(ListUtils.toList(section.get("id")));
                return context -> {
                    Location location = requireNonNull(context.arg(ContextKeys.LOCATION)).clone().add(0,y,0);
                    Pos3 pos3 = Pos3.from(location);
                    Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(location.getWorld());
                    if (optionalWorld.isEmpty()) {
                        return false;
                    }
                    CustomCropsWorld<?> world = optionalWorld.get();
                    Optional<CustomCropsBlockState> optionalState = world.getBlockState(pos3);
                    if (optionalState.isPresent()) {
                        if (optionalState.get().type() instanceof PotBlock potBlock) {
                            if (ids.contains(potBlock.id(optionalState.get()))) {
                                return true;
                            }
                        }
                    }
                    if (advanced) ActionManager.trigger(context, actions);
                    return false;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at pot requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "pot");
    }

    private void registerCrowAttackRequirement() {
        registerRequirement((args, actions, advanced) -> {
            if (args instanceof Section section) {
                String flyModel = section.getString("fly-model");
                String standModel = section.getString("stand-model");
                double chance = section.getDouble("chance");
                return (context) -> {
                    if (Math.random() > chance) return false;
                    Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
                    Pos3 pos3 = Pos3.from(location);
                    if (ConfigManager.enableScarecrow()) {
                        Optional<CustomCropsWorld<?>> world = plugin.getWorldManager().getWorld(location.getWorld());
                        if (world.isEmpty()) return false;
                        CustomCropsWorld<?> customCropsWorld = world.get();
                        if (!ConfigManager.scarecrowProtectChunk()) {
                            int range = ConfigManager.scarecrowRange();
                            for (int i = -range; i <= range; i++) {
                                for (int j = -range; j <= range; j++) {
                                    for (int k : new int[]{0,-1,1}) {
                                        Pos3 tempPos3 = pos3.add(i, k, j);
                                        Optional<CustomCropsChunk> optionalChunk = customCropsWorld.getLoadedChunk(tempPos3.toChunkPos());
                                        if (optionalChunk.isPresent()) {
                                            CustomCropsChunk chunk = optionalChunk.get();
                                            Optional<CustomCropsBlockState> optionalState = chunk.getBlockState(tempPos3);
                                            if (optionalState.isPresent() && optionalState.get().type() instanceof ScarecrowBlock) {
                                                if (advanced) ActionManager.trigger(context, actions);
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (customCropsWorld.doesChunkHaveBlock(pos3, ScarecrowBlock.class)) {
                                if (advanced) ActionManager.trigger(context, actions);
                                return false;
                            }
                        }
                    }
                    if (!Optional.ofNullable(context.arg(ContextKeys.OFFLINE)).orElse(false))
                        new CrowAttack(
                                location,
                                plugin.getItemManager().build(null, flyModel),
                                plugin.getItemManager().build(null, standModel)
                        ).start();
                    return true;
                };
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at crow-attack requirement which is expected be `Section`");
                return Requirement.empty();
            }
        }, "crow_attack", "crow-attack");
    }

    protected void registerWaterRequirement() {
        registerRequirement((args, actions, advanced) -> {
            int value;
            int y;
            if (args instanceof Integer integer) {
                y = -1;
                value = integer;
            } else if (args instanceof Section section) {
                y = section.getInt("y");
                value = section.getInt("value");
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at water-more-than requirement which is expected be `Section`");
                return Requirement.empty();
            }
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION)).clone().add(0, y, 0);
                Pos3 pos3 = Pos3.from(location);
                Optional<CustomCropsWorld<?>> world = plugin.getWorldManager().getWorld(location.getWorld());
                if (world.isEmpty()) return false;
                CustomCropsWorld<?> customCropsWorld = world.get();
                Optional<CustomCropsBlockState> state = customCropsWorld.getBlockState(pos3);
                if (state.isPresent() && state.get().type() instanceof PotBlock potBlock) {
                    int water = potBlock.water(state.get());
                    if (water > value) return true;
                }
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "water-more-than", "water_more_than");
        registerRequirement((args, actions, advanced) -> {
            int value;
            int y;
            if (args instanceof Integer integer) {
                y = -1;
                value = integer;
            } else if (args instanceof Section section) {
                y = section.getInt("y");
                value = section.getInt("value");
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at water-less-than requirement which is expected be `Section`");
                return Requirement.empty();
            }
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION)).clone().add(0, y, 0);
                Pos3 pos3 = Pos3.from(location);
                Optional<CustomCropsWorld<?>> world = plugin.getWorldManager().getWorld(location.getWorld());
                if (world.isEmpty()) return false;
                CustomCropsWorld<?> customCropsWorld = world.get();
                Optional<CustomCropsBlockState> state = customCropsWorld.getBlockState(pos3);
                if (state.isPresent() && state.get().type() instanceof PotBlock potBlock) {
                    int water = potBlock.water(state.get());
                    if (water < value) return true;
                } else {
                    return true;
                }
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "water-less-than", "water_less_than");
        registerRequirement((args, actions, advanced) -> {
            int value;
            int y;
            if (args instanceof Integer integer) {
                y = -1;
                value = integer;
            } else if (args instanceof Section section) {
                y = section.getInt("y");
                value = section.getInt("value");
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at water-less-than requirement which is expected be `Section`");
                return Requirement.empty();
            }
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION)).clone().add(0, y, 0);
                Block block = location.getBlock();
                if (block.getBlockData() instanceof Farmland farmland) {
                    if (farmland.getMoisture() > value) {
                        return true;
                    }
                }
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "moisture-more-than", "moisture_more_than");
        registerRequirement((args, actions, advanced) -> {
            int value;
            int y;
            if (args instanceof Integer integer) {
                y = -1;
                value = integer;
            } else if (args instanceof Section section) {
                y = section.getInt("y");
                value = section.getInt("value");
            } else {
                plugin.getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at water-less-than requirement which is expected be `Section`");
                return Requirement.empty();
            }
            return context -> {
                Location location = requireNonNull(context.arg(ContextKeys.LOCATION)).clone().add(0, y, 0);
                Block block = location.getBlock();
                if (block.getBlockData() instanceof Farmland farmland) {
                    if (farmland.getMoisture() < value) {
                        return true;
                    }
                } else {
                    return true;
                }
                if (advanced) ActionManager.trigger(context, actions);
                return false;
            };
        }, "moisture-less-than", "moisture_less_than");
    }
}

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

package net.momirealms.customcrops.utils;

import com.google.common.base.Preconditions;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Pair;
import net.momirealms.customcrops.api.manager.PlaceholderManager;
import net.momirealms.customcrops.api.mechanic.action.Action;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.condition.*;
import net.momirealms.customcrops.api.mechanic.item.BoneMeal;
import net.momirealms.customcrops.api.mechanic.item.FertilizerType;
import net.momirealms.customcrops.api.mechanic.item.ItemCarrier;
import net.momirealms.customcrops.api.mechanic.item.water.PassiveFillMethod;
import net.momirealms.customcrops.api.mechanic.item.water.PositiveFillMethod;
import net.momirealms.customcrops.api.mechanic.misc.Value;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.mechanic.world.level.WorldSetting;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.mechanic.item.impl.CropConfig;
import net.momirealms.customcrops.mechanic.misc.value.ExpressionValue;
import net.momirealms.customcrops.mechanic.misc.value.PlainValue;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class ConfigUtils {

    private ConfigUtils() {}

    public static YamlConfiguration getConfig(String file) {
        File config = new File(CustomCropsPlugin.get().getDataFolder(), file);
        if (!config.exists()) CustomCropsPlugin.get().saveResource(file, false);
        return YamlConfiguration.loadConfiguration(config);
    }

    public static WorldSetting getWorldSettingFromSection(ConfigurationSection section) {
        return WorldSetting.of(
                section.getBoolean("enable", true),
                section.getInt("point-interval", 300),
                section.getInt("tick-pot-interval", 2),
                section.getInt("tick-sprinkler-interval", 2),
                section.getBoolean("offline-grow", true),
                section.getBoolean("season", false),
                section.getBoolean("auto-season-change", false),
                section.getInt("season-duration", 28),
                section.getInt("crop-per-chunk", 128),
                section.getInt("pot-per-chunk", -1),
                section.getInt("sprinkler-per-chunk", 32)
        );
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

    /**
     * Converts an object into an ArrayList of strings.
     *
     * @param object The input object
     * @return An ArrayList of strings
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<String> stringListArgs(Object object) {
        ArrayList<String> list = new ArrayList<>();
        if (object instanceof String member) {
            list.add(member);
        } else if (object instanceof List<?> members) {
            list.addAll((Collection<? extends String>) members);
        } else if (object instanceof String[] strings) {
            list.addAll(List.of(strings));
        }
        return list;
    }

    /**
     * Converts an object into a double value.
     *
     * @param arg The input object
     * @return A double value
     */
    public static double getDoubleValue(Object arg) {
        if (arg instanceof Double d) {
            return d;
        } else if (arg instanceof Integer i) {
            return Double.valueOf(i);
        }
        return 0;
    }

    /**
     * Converts an object into a "value".
     *
     * @param arg int / double / expression
     * @return Value
     */
    public static Value getValue(Object arg) {
        if (arg instanceof Integer i) {
            return new PlainValue(i);
        } else if (arg instanceof Double d) {
            return new PlainValue(d);
        } else if (arg instanceof String s) {
            return new ExpressionValue(s);
        }
        throw new IllegalArgumentException("Illegal value type");
    }

    public static double getExpressionValue(Player player, String formula, Map<String, String> vars) {
        formula = PlaceholderManager.getInstance().parse(player, formula, vars);
        return new ExpressionBuilder(formula).build().evaluate();
    }

    /**
     * Splits a string into a pair of integers using the "~" delimiter.
     *
     * @param value The input string
     * @return A Pair of integers
     */
    public static Pair<Integer, Integer> splitStringIntegerArgs(String value, String regex) {
        String[] split = value.split(regex);
        return Pair.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    public static List<File> getFilesRecursively(File folder) {
        List<File> ymlFiles = new ArrayList<>();
        if (folder != null && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        ymlFiles.addAll(getFilesRecursively(file));
                    } else if (file.getName().endsWith(".yml")) {
                        ymlFiles.add(file);
                    }
                }
            }
        }
        return ymlFiles;
    }

    public static Action[] getActions(ConfigurationSection section) {
        return CustomCropsPlugin.get().getActionManager().getActions(section);
    }


    public static HashMap<ActionTrigger, Action[]> getActionMap(ConfigurationSection section) {
        HashMap<ActionTrigger, Action[]> map = new HashMap<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection innerSection) {
                    try {
                        ActionTrigger trigger = ActionTrigger.valueOf(entry.getKey());
                        map.put(trigger, getActions(innerSection));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return map;
    }

    public static PositiveFillMethod[] getPositiveFillMethods(ConfigurationSection section) {
        ArrayList<PositiveFillMethod> methods = new ArrayList<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection innerSection) {
                    PositiveFillMethod fillMethod = new PositiveFillMethod(
                            Preconditions.checkNotNull(innerSection.getString("target"), "fill-method target should not be null"),
                            innerSection.getInt("amount", 1),
                            getActions(innerSection.getConfigurationSection("actions")),
                            getRequirements(innerSection.getConfigurationSection("requirements"))
                    );
                    methods.add(fillMethod);
                }
            }
        }
        return methods.toArray(new PositiveFillMethod[0]);
    }

    public static PassiveFillMethod[] getPassiveFillMethods(ConfigurationSection section) {
        ArrayList<PassiveFillMethod> methods = new ArrayList<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection innerSection) {
                    PassiveFillMethod fillMethod = new PassiveFillMethod(
                            Preconditions.checkNotNull(innerSection.getString("item"), "fill-method item should not be null"),
                            innerSection.getInt("item-amount", 1),
                            innerSection.getString("return"),
                            innerSection.getInt("return-amount", 1),
                            innerSection.getInt("amount", 1),
                            getActions(innerSection.getConfigurationSection("actions")),
                            getRequirements(innerSection.getConfigurationSection("requirements"))
                    );
                    methods.add(fillMethod);
                }
            }
        }
        return methods.toArray(new PassiveFillMethod[0]);
    }

    public static HashMap<Integer, Integer> getInt2IntMap(ConfigurationSection section) {
        HashMap<Integer, Integer> map = new HashMap<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                try {
                    int i1 = Integer.parseInt(entry.getKey());
                    if (entry.getValue() instanceof Integer i2) {
                        map.put(i1, i2);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    public static Requirement[] getRequirements(ConfigurationSection section) {
        return CustomCropsPlugin.get().getRequirementManager().getRequirements(section, true);
    }

    public static HashMap<FertilizerType, Pair<String, String>> getFertilizedPotMap(ConfigurationSection section) {
        HashMap<FertilizerType, Pair<String, String>> map = new HashMap<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection innerSection) {
                    FertilizerType type = switch (entry.getKey()) {
                        case "quality" -> FertilizerType.QUALITY;
                        case "yield-increase" -> FertilizerType.YIELD_INCREASE;
                        case "variation-increase" -> FertilizerType.VARIATION;
                        case "soil-retain" -> FertilizerType.SOIL_RETAIN;
                        case "speed-grow" -> FertilizerType.SPEED_GROW;
                        default -> null;
                    };
                    if (type != null) {
                        map.put(type, Pair.of(
                                Preconditions.checkNotNull(innerSection.getString("dry"), entry.getKey() + ".dry should not be null"),
                                Preconditions.checkNotNull(innerSection.getString("wet"), entry.getKey() + ".wet should not be null")
                        ));
                    }
                }
            }
        }
        return map;
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

    public static List<Pair<Double, Integer>> getIntChancePair(ConfigurationSection section) {
        ArrayList<Pair<Double, Integer>> pairs = new ArrayList<>();
        if (section != null) {
            for (String point : section.getKeys(false)) {
                Pair<Double, Integer> pair = new Pair<>(section.getDouble(point), Integer.parseInt(point));
                pairs.add(pair);
            }
        }
        return pairs;
    }

    public static BoneMeal[] getBoneMeals(ConfigurationSection section) {
        ArrayList<BoneMeal> boneMeals = new ArrayList<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection innerSection) {
                    BoneMeal boneMeal = new BoneMeal(
                            Preconditions.checkNotNull(innerSection.getString("item"), "Bone meal item can't be null"),
                            innerSection.getInt("item-amount",1),
                            innerSection.getString("return"),
                            innerSection.getInt("return-amount",1),
                            getIntChancePair(section.getConfigurationSection("chance")),
                            getActions(section.getConfigurationSection("actions"))
                    );
                    boneMeals.add(boneMeal);
                }
            }
        }
        return boneMeals.toArray(new BoneMeal[0]);
    }

    public static DeathConditions[] getDeathConditions(ConfigurationSection section, ItemCarrier original) {
        ArrayList<DeathConditions> conditions = new ArrayList<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection inner) {
                    DeathConditions deathConditions = new DeathConditions(
                            getConditions(inner.getConfigurationSection("conditions")),
                            inner.getString("model"),
                            Optional.ofNullable(inner.getString("type")).map(ItemCarrier::valueOf).orElse(original)
                    );
                    conditions.add(deathConditions);
                }
            }
        }
        return conditions.toArray(new DeathConditions[0]);
    }

    public static Condition[] getConditions(ConfigurationSection section) {
        return CustomCropsPlugin.get().getConditionManager().getConditions(section);
    }

    public static HashMap<Integer, CropConfig.CropStageConfig> getStageConfigs(ConfigurationSection section) {
        HashMap<Integer, CropConfig.CropStageConfig> map = new HashMap<>();
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection inner) {
                    try {
                        int point = Integer.parseInt(entry.getKey());
                        if (point < 0) {
                            LogUtils.warn(entry.getKey() + " is not a valid number");
                        } else {
                            map.put(point, new CropConfig.CropStageConfig(
                                    inner.getString("model"),
                                    point,
                                    inner.getDouble("hologram-offset-correction", 0d),
                                    getActionMap(inner.getConfigurationSection("events"))
                            ));
                        }
                    } catch (NumberFormatException e) {
                        LogUtils.warn(entry.getKey() + " is not a valid number");
                    }
                }
            }
        }
        return map;
    }
}

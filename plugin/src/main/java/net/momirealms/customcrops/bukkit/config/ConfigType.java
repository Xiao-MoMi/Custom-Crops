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

package net.momirealms.customcrops.bukkit.config;

import com.google.common.base.Preconditions;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.core.ConfigManager;
import net.momirealms.customcrops.api.core.CustomForm;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.InternalRegistries;
import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
import net.momirealms.customcrops.api.core.mechanic.crop.CropStageConfig;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerType;
import net.momirealms.customcrops.api.core.mechanic.pot.PotConfig;
import net.momirealms.customcrops.api.core.mechanic.sprinkler.SprinklerConfig;
import net.momirealms.customcrops.api.core.mechanic.wateringcan.WateringCanConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.misc.value.TextValue;
import net.momirealms.customcrops.api.misc.water.WaterBar;
import net.momirealms.customcrops.api.requirement.RequirementManager;
import net.momirealms.customcrops.common.util.ListUtils;
import net.momirealms.customcrops.common.util.Pair;
import net.momirealms.customcrops.common.util.TriFunction;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

/**
 * Configuration types for various mechanics.
 */
public class ConfigType {

    public static final ConfigType WATERING_CAN = of(
            "watering-cans",
            (manager, id, section) -> {
                ActionManager<Player> pam = BukkitCustomCropsPlugin.getInstance().getActionManager(Player.class);
                WateringCanConfig config = WateringCanConfig.builder()
                        .id(id)
                        .itemID(section.getString("item"))
                        .storage(section.getInt("capacity", 3))
                        .wateringAmount(section.getInt("water", 1))
                        .infinite(section.getBoolean("infinite", false))
                        .width(section.getInt("effective-range.width", 1))
                        .length(section.getInt("effective-range.length", 1))
                        .potWhitelist(new HashSet<>(section.getStringList("pot-whitelist")))
                        .sprinklerWhitelist(new HashSet<>(section.getStringList("sprinkler-whitelist")))
                        .dynamicLore(section.getBoolean("dynamic-lore.enable"))
                        .lore(section.getStringList("dynamic-lore.lore").stream().map(TextValue::<Player>auto).toList())
                        .fillMethods(manager.getFillMethods(section.getSection("fill-method")))
                        .requirements(BukkitCustomCropsPlugin.getInstance().getRequirementManager(Player.class).parseRequirements(section.getSection("requirements"), true))
                        .fullActions(pam.parseActions(section.getSection("events.full")))
                        .addWaterActions(pam.parseActions(section.getSection("events.add_water")))
                        .consumeWaterActions(pam.parseActions(section.getSection("events.consume_water")))
                        .runOutOfWaterActions(pam.parseActions(section.getSection("events.no_water")))
                        .wrongPotActions(pam.parseActions(section.getSection("events.wrong_pot")))
                        .wrongSprinklerActions(pam.parseActions(section.getSection("events.wrong_sprinkler")))
                        .appearances(manager.getInt2IntMap(section.getSection("appearance")))
                        .waterBar(section.contains("water-bar") ? WaterBar.of(
                                section.getString("water-bar.left", ""),
                                section.getString("water-bar.empty", ""),
                                section.getString("water-bar.full", ""),
                                section.getString("water-bar.right", "")
                        ) : null)
                        .build();
                manager.registerWateringCanConfig(config);
                return false;
            }
    );

    public static final ConfigType FERTILIZER = of(
            "fertilizers",
            (manager, id, section) -> {
                String typeName = Preconditions.checkNotNull(section.getString("type"), "Fertilizer type can't be null").toLowerCase(Locale.ENGLISH);
                FertilizerType type = InternalRegistries.FERTILIZER_TYPE.get(typeName);
                if (type == null) {
                    BukkitCustomCropsPlugin.getInstance().getPluginLogger().warn("Fertilizer type " + typeName + " not found");
                    return false;
                }
                FertilizerConfig config = type.parse(manager, id, section);
                manager.registerFertilizerConfig(config);
                return false;
            }
    );

    public static final ConfigType POT = of(
            "pots",
            (manager, id, section) -> {

                ActionManager<Player> pam = BukkitCustomCropsPlugin.getInstance().getActionManager(Player.class);
                ActionManager<CustomCropsBlockState> bam = BukkitCustomCropsPlugin.getInstance().getActionManager(CustomCropsBlockState.class);
                RequirementManager<Player> prm = BukkitCustomCropsPlugin.getInstance().getRequirementManager(Player.class);

                PotConfig config = PotConfig.builder()
                        .id(id)
                        .vanillaFarmland(section.getBoolean("vanilla-farmland", false))
                        .vanillaPots(ListUtils.toList(section.get("vanilla-blocks")))
                        .ignoreRandomTick(section.getBoolean("ignore-random-tick", false))
                        .ignoreScheduledTick(section.getBoolean("ignore-scheduled-tick", false))
                        .storage(section.getInt("storage", section.getInt("max-water-storage", 5)))
                        .isRainDropAccepted(section.getBoolean("absorb-rainwater", false))
                        .isNearbyWaterAccepted(section.getBoolean("absorb-nearby-water", false))
                        .maxFertilizers(section.getInt("max-fertilizers", 1))
                        .basicAppearance(Pair.of(section.getString("base.dry"), section.getString("base.wet")))
                        .potAppearanceMap(manager.getFertilizedPotMap(section.getSection("fertilized-pots")))
                        .wateringMethods(manager.getWateringMethods(section.getSection("fill-method")))
                        .addWaterActions(pam.parseActions(section.getSection("events.add_water")))
                        .placeActions(pam.parseActions(section.getSection("events.place")))
                        .breakActions(pam.parseActions(section.getSection("events.break")))
                        .maxFertilizerActions(pam.parseActions(section.getSection("events.max_fertilizers")))
                        .interactActions(pam.parseActions(section.getSection("events.interact")))
                        .reachLimitActions(pam.parseActions(section.getSection("events.reach_limit")))
                        .fullWaterActions(pam.parseActions(section.getSection("events.full")))
                        .tickActions(bam.parseActions(section.getSection("events.tick")))
                        .useRequirements(prm.parseRequirements(section.getSection("requirements.use"), true))
                        .placeRequirements(prm.parseRequirements(section.getSection("requirements.place"), true))
                        .breakRequirements(prm.parseRequirements(section.getSection("requirements.break"), true))
                        .waterBar(section.contains("water-bar") ? WaterBar.of(
                                section.getString("water-bar.left", ""),
                                section.getString("water-bar.empty", ""),
                                section.getString("water-bar.full", ""),
                                section.getString("water-bar.right", "")
                        ) : null)
                        .build();

                manager.registerPotConfig(config);
                return false;
            }
    );

    public static final ConfigType CROP = of(
            "crops",
            (manager, id, section) -> {

                ActionManager<Player> pam = BukkitCustomCropsPlugin.getInstance().getActionManager(Player.class);
                ActionManager<CustomCropsBlockState> bam = BukkitCustomCropsPlugin.getInstance().getActionManager(CustomCropsBlockState.class);
                RequirementManager<Player> prm = BukkitCustomCropsPlugin.getInstance().getRequirementManager(Player.class);

                boolean needUpdate = false;

                ExistenceForm form = CustomForm.valueOf(section.getString("type").toUpperCase(Locale.ENGLISH)).existenceForm();

                Section growConditionSection = section.getSection("grow-conditions");
                if (growConditionSection != null) {
                    for (Map.Entry<String, Object> entry : growConditionSection.getStringRouteMappedValues(false).entrySet()) {
                        if (entry.getValue() instanceof Section inner) {
                            if (inner.contains("type")) {
                                needUpdate = true;
                                break;
                            }
                        }
                    }
                }

                if (needUpdate) {
                    section.remove("grow-conditions");
                    section.set("grow-conditions.default.point", 1);
                    Section newSection = section.createSection("grow-conditions.default.conditions");
                    newSection.setValue(growConditionSection.getStoredValue());
                }

                Section pointSection = section.getSection("points");
                if (pointSection == null) {
                    BukkitCustomCropsPlugin.getInstance().getPluginLogger().warn("points section not found in crop[" + id + "]");
                    return false;
                }

                ArrayList<CropStageConfig.Builder> builders = new ArrayList<>();
                for (Map.Entry<String, Object> entry : pointSection.getStringRouteMappedValues(false).entrySet()) {
                    if (entry.getValue() instanceof Section inner) {
                        int point = Integer.parseInt(entry.getKey());
                        CropStageConfig.Builder builder = CropStageConfig.builder()
                                .point(point)
                                .existenceForm(inner.contains("type") ? CustomForm.valueOf(inner.getString("type").toUpperCase(Locale.ENGLISH)).existenceForm() : form)
                                .displayInfoOffset(inner.getDouble("hologram-offset-correction"))
                                .stageID(inner.getString("model"))
                                .breakRequirements(prm.parseRequirements(inner.getSection("requirements.break"), true))
                                .interactRequirements(prm.parseRequirements(inner.getSection("requirements.interact"), true))
                                .breakActions(pam.parseActions(inner.getSection("events.break")))
                                .interactActions(pam.parseActions(inner.getSection("events.interact")))
                                .growActions(bam.parseActions(inner.getSection("events.grow")));
                        builders.add(builder);
                    }
                }

                CropConfig config = CropConfig.builder()
                        .id(id)
                        .seed(section.getString("seed"))
                        .rotation(section.getBoolean("random-rotation", false))
                        .maxPoints(section.getInt("max-points", 1))
                        .potWhitelist(new HashSet<>(section.getStringList("pot-whitelist")))
                        .wrongPotActions(pam.parseActions(section.getSection("events.wrong_pot")))
                        .plantActions(pam.parseActions(section.getSection("events.plant")))
                        .breakActions(pam.parseActions(section.getSection("events.break")))
                        .interactActions(pam.parseActions(section.getSection("events.interact")))
                        .reachLimitActions(pam.parseActions(section.getSection("events.reach_limit")))
                        .interactRequirements(prm.parseRequirements(section.getSection("requirements.interact"), true))
                        .plantRequirements(prm.parseRequirements(section.getSection("requirements.plant"), true))
                        .breakRequirements(prm.parseRequirements(section.getSection("requirements.break"), true))
                        .boneMeals(manager.getBoneMeals(section.getSection("custom-bone-meal")))
                        .deathConditions(manager.getDeathConditions(section.getSection("death-conditions"), form))
                        .growConditions(manager.getGrowConditions(section.getSection("grow-conditions")))
                        .stages(builders)
                        .build();

                manager.registerCropConfig(config);
                return needUpdate;
            }
    );

    public static final ConfigType SPRINKLER = of(
            "sprinklers",
            (manager, id, section) -> {
                int rangeValue = section.getInt("range",1);
                int workingMode = section.getInt("working-mode", 1);
                int[][] range;
                if (workingMode == 1) {
                    int blocks = 4 * rangeValue * rangeValue + 4 * rangeValue + 1;
                    range = new int[blocks][2];
                    int index = 0;
                    for (int i = -rangeValue; i <= rangeValue; i++) {
                        for (int j = -rangeValue; j <= rangeValue; j++) {
                            range[index++] = new int[]{i, j};
                        }
                    }
                } else if (workingMode == 2) {
                    int blocks = (2 * rangeValue * rangeValue) + 2 * rangeValue + 1;
                    range = new int[blocks][2];
                    int index = 0;
                    for (int i = -rangeValue; i <= rangeValue; i++) {
                        for (int j = -rangeValue; j <= rangeValue; j++) {
                            if (Math.abs(i) + Math.abs(j) <= rangeValue) {
                                range[index++] = new int[]{i, j};
                            }
                        }
                    }
                } else if (workingMode == 3) {
                    ArrayList<int[]> offsets = new ArrayList<>();
                    for (int i = -rangeValue; i <= rangeValue; i++) {
                        for (int j = -rangeValue; j <= rangeValue; j++) {
                            if (Math.sqrt(i * i + j * j) <= rangeValue + 0.3) {
                                offsets.add(new int[]{i, j});
                            }
                        }
                    }
                    range = offsets.toArray(new int[offsets.size()][]);
                } else {
                    throw new IllegalArgumentException("Unrecognized working mode: " + workingMode);
                }

                ActionManager<Player> pam = BukkitCustomCropsPlugin.getInstance().getActionManager(Player.class);
                ActionManager<CustomCropsBlockState> bam = BukkitCustomCropsPlugin.getInstance().getActionManager(CustomCropsBlockState.class);
                RequirementManager<Player> prm = BukkitCustomCropsPlugin.getInstance().getRequirementManager(Player.class);

                SprinklerConfig config = SprinklerConfig.builder()
                        .id(id)
                        .range(range)
                        .storage(section.getInt("storage", 4))
                        .infinite(section.getBoolean("infinite", false))
                        .twoDItem(section.getString("2D-item"))
                        .wateringAmount(section.getInt("water", 1))
                        .sprinklingAmount(section.getInt("sprinkling", 1))
                        .threeDItem(section.getString("3D-item"))
                        .threeDItemWithWater(section.getString("3D-item-with-water"))
                        .wateringMethods(manager.getWateringMethods(section.getSection("fill-method")))
                        .potWhitelist(new HashSet<>(section.getStringList("pot-whitelist")))
                        .existenceForm(CustomForm.valueOf(section.getString("type", "FURNITURE").toUpperCase(Locale.ENGLISH)).existenceForm())
                        .addWaterActions(pam.parseActions(section.getSection("events.add_water")))
                        .breakActions(pam.parseActions(section.getSection("events.break")))
                        .placeActions(pam.parseActions(section.getSection("events.place")))
                        .fullWaterActions(pam.parseActions(section.getSection("events.full")))
                        .reachLimitActions(pam.parseActions(section.getSection("events.reach_limit")))
                        .interactActions(pam.parseActions(section.getSection("events.interact")))
                        .workActions(bam.parseActions(section.getSection("events.work")))
                        .useRequirements(prm.parseRequirements(section.getSection("requirements.use"), true))
                        .placeRequirements(prm.parseRequirements(section.getSection("requirements.place"), true))
                        .breakRequirements(prm.parseRequirements(section.getSection("requirements.break"), true))
                        .waterBar(section.contains("water-bar") ? WaterBar.of(
                                section.getString("water-bar.left", ""),
                                section.getString("water-bar.empty", ""),
                                section.getString("water-bar.full", ""),
                                section.getString("water-bar.right", "")
                        ) : null)
                        .build();

                manager.registerSprinklerConfig(config);
                return false;
            }
    );

    private static final ConfigType[] values = new ConfigType[] {CROP, SPRINKLER, WATERING_CAN, POT, FERTILIZER};

    public static ConfigType[] values() {
        return values;
    }

    private final String path;
    private final TriFunction<ConfigManager, String, Section, Boolean> argumentConsumer;

    public ConfigType(String path, TriFunction<ConfigManager, String, Section, Boolean> argumentConsumer) {
        this.path = path;
        this.argumentConsumer = argumentConsumer;
    }

    public static ConfigType of(String path, TriFunction<ConfigManager, String, Section, Boolean> argumentConsumer) {
        return new ConfigType(path, argumentConsumer);
    }

    public String path() {
        return path;
    }

    public boolean parse(ConfigManager manager, String id, Section section) {
        return argumentConsumer.apply(manager, id, section);
    }
}

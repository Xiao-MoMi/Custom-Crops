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

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.ConstructorException;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.boostedyaml.utils.format.NodeRole;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.*;
import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
import net.momirealms.customcrops.api.core.mechanic.crop.CropStageConfig;
import net.momirealms.customcrops.api.core.mechanic.crop.DeathCondition;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.core.mechanic.pot.PotConfig;
import net.momirealms.customcrops.api.core.mechanic.sprinkler.SprinklerConfig;
import net.momirealms.customcrops.api.core.mechanic.wateringcan.WateringCanConfig;
import net.momirealms.customcrops.common.helper.AdventureHelper;
import net.momirealms.customcrops.common.locale.TranslationManager;
import net.momirealms.customcrops.common.plugin.CustomCropsProperties;
import net.momirealms.customcrops.common.util.ListUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class BukkitConfigManager extends ConfigManager {

    private static YamlDocument MAIN_CONFIG;

    public static YamlDocument getMainConfig() {
        return MAIN_CONFIG;
    }

    public BukkitConfigManager(BukkitCustomCropsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        String configVersion = CustomCropsProperties.getValue("config");
        try (InputStream inputStream = new FileInputStream(resolveConfig("config.yml").toFile())) {
            MAIN_CONFIG = YamlDocument.create(
                    inputStream,
                    plugin.getResourceStream("config.yml"),
                    GeneralSettings.builder()
                            .setRouteSeparator('.')
                            .setUseDefaults(false)
                            .build(),
                    LoaderSettings
                            .builder()
                            .setAutoUpdate(true)
                            .build(),
                    DumperSettings.builder()
                            .setScalarFormatter((tag, value, role, def) -> {
                                if (role == NodeRole.KEY) {
                                    return ScalarStyle.PLAIN;
                                } else {
                                    return tag == Tag.STR ? ScalarStyle.DOUBLE_QUOTED : ScalarStyle.PLAIN;
                                }
                            })
                            .build(),
                    UpdaterSettings
                            .builder()
                            .setVersioning(new BasicVersioning("config-version"))
                            .addIgnoredRoute(configVersion, "other-settings.placeholder-register", '.')
                            .build()
            );
            MAIN_CONFIG.save(resolveConfig("config.yml").toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.loadSettings();
        this.loadConfigs();
        plugin.debug("Loaded " + Registries.CROP.size() + " crops");
        plugin.debug("Loaded " + Registries.SPRINKLER.size() + " sprinklers");
        plugin.debug("Loaded " + Registries.WATERING_CAN.size() + " watering-cans");
        plugin.debug("Loaded " + Registries.POT.size() + " pots");
        plugin.debug("Loaded " + Registries.FERTILIZER.size() + " fertilizers");
    }

    private void loadSettings() {
        YamlDocument config = getMainConfig();

        TranslationManager.forceLocale(TranslationManager.parseLocale(config.getString("force-locale", "")));
        AdventureHelper.legacySupport = config.getBoolean("other-settings.legacy-color-code-support", true);

        metrics = config.getBoolean("metrics", true);
        checkUpdate = config.getBoolean("update-checker", true);
        debug = config.getBoolean("debug", false);

        protectOriginalLore = config.getBoolean("other-settings.protect-original-lore", false);
        doubleCheck = config.getBoolean("other-settings.double-check", false);

        enableScarecrow = config.getBoolean("mechanics.scarecrow.enable", true);
        scarecrow = new HashSet<>(ListUtils.toList(config.get("mechanics.scarecrow.id")));
        scarecrowExistenceForm = CustomForm.valueOf(config.getString("mechanics.scarecrow.type", "ITEM_FRAME")).existenceForm();
        scarecrowRange = config.getInt("mechanics.scarecrow.range", 7);
        scarecrowProtectChunk = config.getBoolean("mechanics.scarecrow.protect-chunk", false);

        enableGreenhouse = config.getBoolean("mechanics.greenhouse.enable", true);
        greenhouse = new HashSet<>(ListUtils.toList(config.get("mechanics.greenhouse.id")));
        greenhouseExistenceForm = CustomForm.valueOf(config.getString("mechanics.greenhouse.type", "CHORUS")).existenceForm();
        greenhouseRange = config.getInt("mechanics.greenhouse.range", 5);

        syncSeasons = config.getBoolean("mechanics.sync-season.enable", false);
        referenceWorld = config.getString("mechanics.sync-season.reference", "world");

        itemDetectOrder = config.getStringList("other-settings.item-detection-order").toArray(new String[0]);

        absoluteWorldPath = config.getString("worlds.absolute-world-folder-path");

        defaultQualityRatio = getQualityRatio(config.getString("mechanics.default-quality-ratio", "17/2/1"));

        preventTrampling = config.getBoolean("mechanics.vanilla-farmland.prevent-trampling", false);
        disableMoistureMechanic = config.getBoolean("mechanics.vanilla-farmland.disable-moisture-mechanic", false);

        offsets.clear();
        Section section = config.getSection("mechanics.hologram-offset-correction");
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                if (entry.getValue() instanceof Number n) {
                    offsets.put(entry.getKey(), n.doubleValue());
                }
            }
        }

        for (String id : scarecrow) {
            Registries.BLOCKS.register(id, BuiltInBlockMechanics.SCARECROW.mechanic());
        }
        for (String id : greenhouse) {
            Registries.BLOCKS.register(id, BuiltInBlockMechanics.GREENHOUSE.mechanic());
        }
    }

    @Override
    public void saveResource(String filePath) {
        File file = new File(plugin.getDataFolder(), filePath);
        if (!file.exists()) {
            plugin.getBoostrap().saveResource(filePath, false);
            addDefaultNamespace(file);
        }
    }

    @Override
    public void unload() {
        this.clearConfigs();
    }

    private void loadConfigs() {
        Deque<File> fileDeque = new ArrayDeque<>();
        for (ConfigType type : ConfigType.values()) {
            File typeFolder = new File(plugin.getDataFolder(), "contents" + File.separator + type.path());
            if (!typeFolder.exists()) {
                if (!typeFolder.mkdirs()) return;
                saveResource("contents" + File.separator + type.path() + File.separator + "default.yml");
            }
            fileDeque.push(typeFolder);
            while (!fileDeque.isEmpty()) {
                File file = fileDeque.pop();
                File[] files = file.listFiles();
                if (files == null) continue;
                for (File subFile : files) {
                    if (subFile.isDirectory()) {
                        fileDeque.push(subFile);
                    } else if (subFile.isFile() && subFile.getName().endsWith(".yml")) {
                        try {
                            YamlDocument document = plugin.getConfigManager().loadData(subFile);
                            boolean save = false;
                            for (Map.Entry<String, Object> entry : document.getStringRouteMappedValues(false).entrySet()) {
                                if (entry.getValue() instanceof Section section) {
                                    if (type.parse(this, entry.getKey(), section)) {
                                        save = true;
                                    }
                                }
                            }
                            if (save) {
                                document.save(subFile);
                            }
                        } catch (ConstructorException e) {
                            plugin.getPluginLogger().warn("Could not load config file: " + subFile.getAbsolutePath() + ". Is it a corrupted file?");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    private void clearConfigs() {
        Registries.CROP.clear();
        Registries.SEED_TO_CROP.clear();
        Registries.STAGE_TO_CROP_UNSAFE.clear();

        Registries.SPRINKLER.clear();
        Registries.ITEM_TO_SPRINKLER.clear();

        Registries.POT.clear();
        Registries.ITEM_TO_POT.clear();

        Registries.FERTILIZER.clear();
        Registries.ITEM_TO_SPRINKLER.clear();

        Registries.WATERING_CAN.clear();

        Registries.ITEMS.clear();
        Registries.BLOCKS.clear();
    }

    @Override
    public void registerWateringCanConfig(WateringCanConfig config) {
        Registries.WATERING_CAN.register(config.id(), config);
        Registries.ITEM_TO_WATERING_CAN.register(config.itemID(), config);
        Registries.ITEMS.register(config.itemID(), BuiltInItemMechanics.WATERING_CAN.mechanic());
    }

    @Override
    public void registerFertilizerConfig(FertilizerConfig config) {
        Registries.FERTILIZER.register(config.id(), config);
        Registries.ITEM_TO_FERTILIZER.register(config.itemID(), config);
        Registries.ITEMS.register(config.itemID(), BuiltInItemMechanics.FERTILIZER.mechanic());
    }

    @Override
    public void registerCropConfig(CropConfig config) {
        Registries.CROP.register(config.id(), config);
        Registries.SEED_TO_CROP.register(config.seed(), config);
        Registries.ITEMS.register(config.seed(), BuiltInItemMechanics.SEED.mechanic());
        for (DeathCondition condition : config.deathConditions()) {
            String deadStage = condition.deathStage();
            if (deadStage != null) {
                if (!Registries.BLOCKS.containsKey(deadStage)) {
                    Registries.BLOCKS.register(deadStage, BuiltInBlockMechanics.DEAD_CROP.mechanic());
                }
                if (!Registries.ITEM_TO_DEAD_CROP.containsKey(deadStage)) {
                    Registries.ITEM_TO_DEAD_CROP.register(deadStage, 0);
                }
            }
        }
        for (CropStageConfig stageConfig : config.stages()) {
            String stageID = stageConfig.stageID();
            if (stageID != null) {
                offsets.put(stageID, stageConfig.displayInfoOffset());
                List<CropConfig> list = Registries.STAGE_TO_CROP_UNSAFE.get(stageID);
                if (list != null) {
                    list.add(config);
                } else {
                    Registries.STAGE_TO_CROP_UNSAFE.register(stageID, new ArrayList<>(List.of(config)));
                    Registries.BLOCKS.register(stageID, BuiltInBlockMechanics.CROP.mechanic());
                }
            }
        }
    }

    @Override
    public void registerPotConfig(PotConfig config) {
        Registries.POT.register(config.id(), config);
        for (String pot : config.blocks()) {
            Registries.ITEM_TO_POT.register(pot, config);
            Registries.BLOCKS.register(pot, BuiltInBlockMechanics.POT.mechanic());
        }
    }

    @Override
    public void registerSprinklerConfig(SprinklerConfig config) {
        Registries.SPRINKLER.register(config.id(), config);
        for (String id : new HashSet<>(List.of(config.threeDItem(), config.threeDItemWithWater()))) {
            Registries.ITEM_TO_SPRINKLER.register(id, config);
            Registries.BLOCKS.register(id, BuiltInBlockMechanics.SPRINKLER.mechanic());
        }
        if (config.twoDItem() != null) {
            Registries.ITEM_TO_SPRINKLER.register(config.twoDItem(), config);
            Registries.ITEMS.register(config.twoDItem(), BuiltInItemMechanics.SPRINKLER_ITEM.mechanic());
        }
    }
}
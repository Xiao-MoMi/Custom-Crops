package net.momirealms.customcrops.mechanic.misc.migrator;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.mechanic.world.level.WorldSetting;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.mechanic.world.CWorld;
import net.momirealms.customcrops.mechanic.world.adaptor.BukkitWorldAdaptor;
import net.momirealms.customcrops.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class Migration {

    public static void tryUpdating() {
        File configFile = new File(CustomCropsPlugin.getInstance().getDataFolder(), "config.yml");
        // If not config file found, do nothing
        if (!configFile.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String version = config.getString("config-version");
        if (version == null) return;

        int versionNumber = Integer.parseInt(version);
        if (versionNumber >= 25 && versionNumber <= 34) {
            doV33Migration(config);
            return;
        }
        if (versionNumber == 35) {
            doV343Migration();
        }
    }

    private static void doV343Migration() {
        if (CustomCropsPlugin.get().getWorldManager().getWorldAdaptor() instanceof BukkitWorldAdaptor adaptor) {
            for (World world : Bukkit.getWorlds()) {
                CWorld temp = new CWorld(CustomCropsPlugin.getInstance().getWorldManager(), world);
                temp.setWorldSetting(WorldSetting.of(false,300,true, 1,true,2,true,2,false,false,false,28,-1,-1,-1, 0));
                adaptor.convertWorldFromV342toV343(temp, world);
            }
        }
    }

    private static void doV33Migration(YamlConfiguration config) {
        // do migration
        if (config.contains("mechanics.season.sync-season")) {
            config.set("mechanics.sync-season.enable", config.getBoolean("mechanics.season.sync-season.enable"));
            config.set("mechanics.sync-season.reference", config.getString("mechanics.season.sync-season.reference"));
        }
        if (config.contains("mechanics.season.greenhouse")) {
            config.set("mechanics.greenhouse.enable", config.getBoolean("mechanics.season.greenhouse.enable"));
            config.set("mechanics.greenhouse.id", config.getString("mechanics.season.greenhouse.block"));
            config.set("mechanics.greenhouse.range", config.getInt("mechanics.season.greenhouse.range"));
        }
        if (config.contains("mechanics.scarecrow")) {
            config.set("mechanics.scarecrow.id", config.getString("mechanics.scarecrow"));
        }

        try {
            config.save(new File(CustomCropsPlugin.getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            YamlDocument.create(
                    new File(CustomCropsPlugin.getInstance().getDataFolder(), "config.yml"),
                    Objects.requireNonNull(CustomCropsPlugin.getInstance().getResource("config.yml")),
                    GeneralSettings.DEFAULT,
                    LoaderSettings
                            .builder()
                            .setAutoUpdate(true)
                            .build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings
                            .builder()
                            .setVersioning(new BasicVersioning("config-version"))
                            .build()
            );
        } catch (IOException e) {
            LogUtils.warn(e.getMessage());
        }

        updateWateringCans();
        updatePots();
        updateFertilizers();
        updateSprinklers();
        updateCrops();

        if (CustomCropsPlugin.get().getWorldManager().getWorldAdaptor() instanceof BukkitWorldAdaptor adaptor) {
            for (World world : Bukkit.getWorlds()) {
                CWorld temp = new CWorld(CustomCropsPlugin.getInstance().getWorldManager(), world);
                temp.setWorldSetting(WorldSetting.of(false,300,true, 1,true,2,true,2,false,false,false,28,-1,-1,-1, 0));
                adaptor.convertWorldFromV33toV34(temp, world);
            }
        }
    }

    private static void updateWateringCans() {
        var files = ConfigUtils.getFilesRecursively(new File(CustomCropsPlugin.getInstance().getDataFolder(), "contents" + File.separator + "watering-cans"));
        for (File file : files) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            for (Map.Entry<String, Object> sections : yaml.getValues(false).entrySet()) {
                if (sections.getValue() instanceof ConfigurationSection section) {
                    ConfigurationSection fillSection = section.getConfigurationSection("fill-method");
                    if (fillSection != null) {
                        for (String key : fillSection.getKeys(false)) {
                            fillSection.set(key + ".particle", null);
                            fillSection.set(key + ".sound", null);
                        }
                    }
                    if (section.contains("sound")) {
                        section.set("events.consume_water.sound_action.type", "sound");
                        section.set("events.consume_water.sound_action.value.key", section.getString("sound"));
                        section.set("events.consume_water.sound_action.value.source", "player");
                        section.set("events.consume_water.sound_action.value.volume", 1);
                        section.set("events.consume_water.sound_action.value.pitch", 1);
                        section.set("sound", null);
                    }
                    if (section.contains("particle")) {
                        section.set("events.add_water.particle_action.type", "particle");
                        section.set("events.add_water.particle_action.value.particle", section.getString("particle"));
                        section.set("events.add_water.particle_action.value.x", 0.5);
                        section.set("events.add_water.particle_action.value.z", 0.5);
                        section.set("events.add_water.particle_action.value.y", 1.3);
                        section.set("events.add_water.particle_action.value.count", 5);
                        section.set("particle", null);
                    }
                    if (section.contains("actionbar")) {
                        if (section.getBoolean("actionbar.enable")) {
                            section.set("events.consume_water.actionbar_action.type", "actionbar");
                            section.set("events.consume_water.actionbar_action.value", section.getString("actionbar.content"));
                            section.set("events.add_water.actionbar_action.type", "actionbar");
                            section.set("events.add_water.actionbar_action.value", section.getString("actionbar.content"));
                        }
                        section.set("actionbar", null);
                    }
                    section.set("events.add_water.sound_action.type", "sound");
                    section.set("events.add_water.sound_action.value.key", "minecraft:item.bucket.empty");
                    section.set("events.add_water.sound_action.value.source", "player");
                    section.set("events.add_water.sound_action.value.volume", 1);
                    section.set("events.add_water.sound_action.value.pitch", 1);
                }
            }
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void updatePots() {
        var files = ConfigUtils.getFilesRecursively(new File(CustomCropsPlugin.getInstance().getDataFolder(), "contents" + File.separator + "pots"));
        for (File file : files) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            for (Map.Entry<String, Object> sections : yaml.getValues(false).entrySet()) {
                if (sections.getValue() instanceof ConfigurationSection section) {
                    section.set("absorb-rainwater", true);
                    section.set("absorb-nearby-water", false);
                    ConfigurationSection fillSection = section.getConfigurationSection("fill-method");
                    if (fillSection != null) {
                        for (String key : fillSection.getKeys(false)) {
                            fillSection.set(key + ".particle", null);
                            fillSection.set(key + ".sound", null);
                        }
                    }
                    if (section.contains("hologram.water.water-bar")) {
                        section.set("water-bar.left", section.getString("hologram.water.water-bar.left"));
                        section.set("water-bar.right", section.getString("hologram.water.water-bar.right"));
                        section.set("water-bar.empty", section.getString("hologram.water.water-bar.empty"));
                        section.set("water-bar.full", section.getString("hologram.water.water-bar.full"));
                    }
                    section.set("events.add_water.particle_action.type", "particle");
                    section.set("events.add_water.particle_action.value.particle", "WATER_SPLASH");
                    section.set("events.add_water.particle_action.value.x", 0.5);
                    section.set("events.add_water.particle_action.value.z", 0.5);
                    section.set("events.add_water.particle_action.value.y", 1.3);
                    section.set("events.add_water.particle_action.value.count", 5);
                    section.set("events.add_water.particle_action.value.offset-x", 0.3);
                    section.set("events.add_water.particle_action.value.offset-z", 0.3);

                    ConfigurationSection holoSection = section.getConfigurationSection("hologram");
                    if (holoSection != null) {
                        int duration = holoSection.getInt("duration") * 20;
                        String requireItem = holoSection.getString("require-item", "*");
                        section.set("events.interact.conditional_action.type", "conditional");
                        section.set("events.interact.conditional_action.value.conditions.requirement_1.type", "item-in-hand");
                        section.set("events.interact.conditional_action.value.conditions.requirement_1.value.amount", 1);
                        section.set("events.interact.conditional_action.value.conditions.requirement_1.value.item", requireItem);
                        if (holoSection.getBoolean("water.enable")) {
                            String waterText = holoSection.getString("water.content");
                            section.set("events.interact.conditional_action.value.actions.water_hologram.type", "hologram");
                            section.set("events.interact.conditional_action.value.actions.water_hologram.value.duration", duration);
                            section.set("events.interact.conditional_action.value.actions.water_hologram.value.text", waterText);
                            section.set("events.interact.conditional_action.value.actions.water_hologram.value.apply-correction", true);
                            section.set("events.interact.conditional_action.value.actions.water_hologram.value.x", 0.5);
                            section.set("events.interact.conditional_action.value.actions.water_hologram.value.y", 0.6);
                            section.set("events.interact.conditional_action.value.actions.water_hologram.value.z", 0.5);
                        }
                        if (holoSection.getBoolean("fertilizer.enable")) {
                            String fertilizerText = holoSection.getString("fertilizer.content");
                            section.set("events.interact.conditional_action.value.actions.conditional_fertilizer_action.type", "conditional");
                            section.set("events.interact.conditional_action.value.actions.conditional_fertilizer_action.value.conditions.requirement_1.type", "fertilizer");
                            section.set("events.interact.conditional_action.value.actions.conditional_fertilizer_action.value.conditions.requirement_1.value.has", true);
                            section.set("events.interact.conditional_action.value.actions.conditional_fertilizer_action.value.actions.fertilizer_hologram.type", "hologram");
                            section.set("events.interact.conditional_action.value.actions.conditional_fertilizer_action.value.actions.fertilizer_hologram.value.text", fertilizerText);
                            section.set("events.interact.conditional_action.value.actions.conditional_fertilizer_action.value.actions.fertilizer_hologram.value.duration", duration);
                            section.set("events.interact.conditional_action.value.actions.conditional_fertilizer_action.value.actions.fertilizer_hologram.value.apply-correction", true);
                            section.set("events.interact.conditional_action.value.actions.conditional_fertilizer_action.value.actions.fertilizer_hologram.value.x", 0.5);
                            section.set("events.interact.conditional_action.value.actions.conditional_fertilizer_action.value.actions.fertilizer_hologram.value.y", 0.83);
                            section.set("events.interact.conditional_action.value.actions.conditional_fertilizer_action.value.actions.fertilizer_hologram.value.z", 0.5);
                        }
                        section.set("hologram", null);
                    }
                }
            }
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void updateFertilizers() {
        var files = ConfigUtils.getFilesRecursively(new File(CustomCropsPlugin.getInstance().getDataFolder(), "contents" + File.separator + "fertilizers"));
        for (File file : files) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            for (Map.Entry<String, Object> sections : yaml.getValues(false).entrySet()) {
                if (sections.getValue() instanceof ConfigurationSection section) {
                    if (section.contains("particle")) {
                        section.set("events.use.particle_action.type", "particle");
                        section.set("events.use.particle_action.value.particle", section.getString("particle"));
                        section.set("events.use.particle_action.value.x", 0.5);
                        section.set("events.use.particle_action.value.y", 1.3);
                        section.set("events.use.particle_action.value.z", 0.5);
                        section.set("events.use.particle_action.value.count", 5);
                        section.set("events.use.particle_action.value.offset-x", 0.3);
                        section.set("events.use.particle_action.value.offset-z", 0.3);
                        section.set("particle", null);
                    }
                    if (section.contains("sound")) {
                        section.set("events.use.sound_action.type", "sound");
                        section.set("events.use.sound_action.value.source", "player");
                        section.set("events.use.sound_action.value.key", section.getString("sound"));
                        section.set("events.use.sound_action.value.volume", 1);
                        section.set("events.use.sound_action.value.pitch", 1);
                        section.set("sound", null);
                    }
                    if (section.contains("pot-whitelist")) {
                        section.set("events.wrong_pot.sound_action.type", "sound");
                        section.set("events.wrong_pot.sound_action.value.source", "player");
                        section.set("events.wrong_pot.sound_action.value.key", "minecraft:item.bundle.insert");
                        section.set("events.wrong_pot.sound_action.value.volume", 1);
                        section.set("events.wrong_pot.sound_action.value.pitch", 1);
                        section.set("events.wrong_pot.actionbar_action.type", "actionbar");
                        section.set("events.wrong_pot.actionbar_action.value", "<red><bold>[X] This fertilizer can only be used in pots.");
                    }
                    if (section.getBoolean("before-plant")) {
                        section.set("events.before_plant.sound_action.type", "sound");
                        section.set("events.before_plant.sound_action.value.source", "player");
                        section.set("events.before_plant.sound_action.value.key", "minecraft:item.bundle.insert");
                        section.set("events.before_plant.sound_action.value.volume", 1);
                        section.set("events.before_plant.sound_action.value.pitch", 1);
                        section.set("events.before_plant.actionbar_action.type", "actionbar");
                        section.set("events.before_plant.actionbar_action.value", "<red><bold>[X] You can only use this fertilizer before planting the crop.");
                    }
                }
            }
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void updateSprinklers() {
        var files = ConfigUtils.getFilesRecursively(new File(CustomCropsPlugin.getInstance().getDataFolder(), "contents" + File.separator + "sprinklers"));
        for (File file : files) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            for (Map.Entry<String, Object> sections : yaml.getValues(false).entrySet()) {
                if (sections.getValue() instanceof ConfigurationSection section) {
                    section.set("infinite", false);
                    ConfigurationSection fillSection = section.getConfigurationSection("fill-method");
                    if (fillSection != null) {
                        for (String key : fillSection.getKeys(false)) {
                            fillSection.set(key + ".particle", null);
                            fillSection.set(key + ".sound", null);
                        }
                    }
                    if (section.contains("place-sound")) {
                        section.set("events.place.sound_action.type", "sound");
                        section.set("events.place.sound_action.value.key", section.getString("place-sound"));
                        section.set("events.place.sound_action.value.source", "player");
                        section.set("events.place.sound_action.value.volume", 1);
                        section.set("events.place.sound_action.value.pitch", 1);
                        section.set("place-sound", null);
                    }
                    section.set("events.interact.force_work_action.type", "conditional");
                    section.set("events.interact.force_work_action.value.conditions.requirement_1.type", "sneak");
                    section.set("events.interact.force_work_action.value.conditions.requirement_1.value", true);
                    section.set("events.interact.force_work_action.value.actions.action_1.type", "force-tick");
                    if (section.contains("hologram")) {
                        if (section.getBoolean("hologram.enable")) {
                            int duration = section.getInt("hologram.duration") * 20;
                            String text = section.getString("hologram.content");
                            if (section.contains("hologram.water-bar")) {
                                section.set("water-bar.left", section.getString("hologram.water-bar.left"));
                                section.set("water-bar.right", section.getString("hologram.water-bar.right"));
                                section.set("water-bar.empty", section.getString("hologram.water-bar.empty"));
                                section.set("water-bar.full", section.getString("hologram.water-bar.full"));
                            }
                            section.set("events.interact.hologram_action.type", "hologram");
                            section.set("events.interact.hologram_action.value.duration", duration);
                            section.set("events.interact.hologram_action.value.text", text);
                            section.set("events.interact.hologram_action.value.x", 0.5);
                            section.set("events.interact.hologram_action.value.y", -0.3);
                            section.set("events.interact.hologram_action.value.z", 0.5);
                            section.set("events.interact.hologram_action.value.visible-to-all", false);
                        }
                        section.set("hologram", null);
                    }
                    if (section.contains("animation")) {
                        if (section.getBoolean("animation.enable")) {
                            String item = section.getString("animation.item");
                            int duration = section.getInt("animation.duration") * 20;
                            section.set("events.work.fake_item_action.type", "fake-item");
                            section.set("events.work.fake_item_action.value.item", item);
                            section.set("events.work.fake_item_action.value.duration", duration);
                            section.set("events.work.fake_item_action.value.x", 0.5);
                            section.set("events.work.fake_item_action.value.y", 0.4);
                            section.set("events.work.fake_item_action.value.z", 0.5);
                            section.set("events.work.fake_item_action.value.visible-to-all", true);
                        }
                        section.set("animation", null);
                    }
                    section.set("events.add_water.particle_action.type", "particle");
                    section.set("events.add_water.particle_action.value.particle", "WATER_SPLASH");
                    section.set("events.add_water.particle_action.value.x", 0.5);
                    section.set("events.add_water.particle_action.value.z", 0.5);
                    section.set("events.add_water.particle_action.value.y", 0.7);
                    section.set("events.add_water.particle_action.value.count", 5);
                    section.set("events.add_water.sound_action.type", "sound");
                    section.set("events.add_water.sound_action.value.key", "minecraft:item.bucket.empty");
                    section.set("events.add_water.sound_action.value.source", "player");
                    section.set("events.add_water.sound_action.value.pitch", 1);
                    section.set("events.add_water.sound_action.value.volume", 1);
                }
            }
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void updateCrops() {
        var files = ConfigUtils.getFilesRecursively(new File(CustomCropsPlugin.getInstance().getDataFolder(), "contents" + File.separator + "crops"));
        for (File file : files) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            for (Map.Entry<String, Object> sections : yaml.getValues(false).entrySet()) {
                if (sections.getValue() instanceof ConfigurationSection section) {
                    if (section.contains("plant-actions")) {
                        section.set("events.plant", section.getConfigurationSection("plant-actions"));
                        section.set("plant-actions", null);
                    }
                    ConfigurationSection boneMeal = section.getConfigurationSection("custom-bone-meal");
                    if (boneMeal != null) {
                        for (Map.Entry<String, Object> entry : boneMeal.getValues(false).entrySet()) {
                            if (entry.getValue() instanceof ConfigurationSection inner) {
                                inner.set("actions.swing_action.type", "swing-hand");
                                inner.set("actions.swing_action.value", true);
                                if (inner.contains("particle")) {
                                    inner.set("actions.particle_action.type", "particle");
                                    inner.set("actions.particle_action.value.particle", inner.getString("particle"));
                                    inner.set("actions.particle_action.value.count",5);
                                    inner.set("actions.particle_action.value.x", 0.5);
                                    inner.set("actions.particle_action.value.y", 0.5);
                                    inner.set("actions.particle_action.value.z", 0.5);
                                    inner.set("actions.particle_action.value.offset-x", 0.3);
                                    inner.set("actions.particle_action.value.offset-y", 0.3);
                                    inner.set("actions.particle_action.value.offset-z", 0.3);
                                    inner.set("particle", null);
                                }
                                if (inner.contains("sound")) {
                                    inner.set("actions.sound_action.type", "sound");
                                    inner.set("actions.sound_action.value.key", inner.getString("sound"));
                                    inner.set("actions.sound_action.value.source", "player");
                                    inner.set("actions.sound_action.value.volume", 1);
                                    inner.set("actions.sound_action.value.pitch", 1);
                                    inner.set("sound", null);
                                }
                            }
                        }
                    }

                    ConfigurationSection pointSection = section.getConfigurationSection("points");
                    if (pointSection == null) continue;
                    for (Map.Entry<String, Object> entry1 : pointSection.getValues(false).entrySet()) {
                        if (entry1.getValue() instanceof ConfigurationSection pointS1) {
                            ConfigurationSection eventSection = pointS1.getConfigurationSection("events");
                            if (eventSection != null) {
                                if (eventSection.contains("interact-by-hand")) {
                                    eventSection.set("interact.empty_hand_action.type", "conditional");
                                    eventSection.set("interact.empty_hand_action.value.conditions", eventSection.getConfigurationSection("interact-by-hand.requirements"));
                                    eventSection.set("interact.empty_hand_action.value.conditions.requirement_empty_hand.type", "item-in-hand");
                                    eventSection.set("interact.empty_hand_action.value.conditions.requirement_empty_hand.value.item", "AIR");
                                    eventSection.set("interact.empty_hand_action.value.actions", eventSection.getConfigurationSection("interact-by-hand"));
                                    eventSection.set("interact.empty_hand_action.value.actions.requirements", null);
                                    eventSection.set("interact-by-hand", null);
                                }
                                if (eventSection.contains("interact-with-item")) {
                                    ConfigurationSection interactWithItem = eventSection.getConfigurationSection("interact-with-item");
                                    if (interactWithItem != null) {
                                        int amount = 0;
                                        for (Map.Entry<String, Object> entry : interactWithItem.getValues(false).entrySet()) {
                                            if (entry.getValue() instanceof ConfigurationSection inner) {
                                                amount++;
                                                String requiredItem = inner.getString("item");
                                                boolean consume = inner.getBoolean("reduce-amount");
                                                String returned = inner.getString("return");
                                                ConfigurationSection actions = inner.getConfigurationSection("actions");
                                                eventSection.set("interact.action_" + amount + ".type", "conditional");
                                                eventSection.set("interact.action_" + amount + ".type", "conditional");
                                                eventSection.set("interact.action_" + amount + ".value.conditions", inner.getConfigurationSection("requirements"));
                                                eventSection.set("interact.action_" + amount + ".value.conditions.requirement_item.type", "item-in-hand");
                                                eventSection.set("interact.action_" + amount + ".value.conditions.requirement_item.value.item", requiredItem);
                                                eventSection.set("interact.action_" + amount + ".value.actions", actions);
                                                if (consume) {
                                                    eventSection.set("interact.action_" + amount + ".value.actions.consume_item.type", "item-amount");
                                                    eventSection.set("interact.action_" + amount + ".value.actions.consume_item.value", -1);
                                                }
                                                if (returned != null) {
                                                    eventSection.set("interact.action_" + amount + ".value.actions.return_item.type", "give-item");
                                                    eventSection.set("interact.action_" + amount + ".value.actions.return_item.value.id", returned);
                                                    eventSection.set("interact.action_" + amount + ".value.actions.return_item.value.amount", 1);
                                                }
                                            }
                                        }
                                    }
                                    eventSection.set("interact-with-item", null);
                                }
                            }

                        }
                    }

                }
            }
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

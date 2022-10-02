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

package net.momirealms.customcrops.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.helper.Log;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigUtil {

    public static void update(String fileName){
        try {
            YamlDocument.create(new File(CustomCrops.plugin.getDataFolder(), fileName), CustomCrops.plugin.getResource(fileName), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
        } catch (IOException e){
            Log.warn(e.getMessage());
        }
    }

    public static YamlConfiguration readData(File file) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                AdventureUtil.consoleMessage("<red>[CustomCrops] Failed to generate data files!</red>");
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static YamlConfiguration getConfig(String configName) {
        File file = new File(CustomCrops.plugin.getDataFolder(), configName);
        if (!file.exists()) CustomCrops.plugin.saveResource(configName, false);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void reloadConfigs() {
        MainConfig.load();
        BasicItemConfig.load();
        CropConfig.load();
        FertilizerConfig.load();
        MessageConfig.load();
        SeasonConfig.load();
        SprinklerConfig.load();
        WaterCanConfig.load();
        SoundConfig.load();
        if (CustomCrops.plugin.getPlaceholderManager() != null) {
            CustomCrops.plugin.getPlaceholderManager().unload();
            CustomCrops.plugin.getPlaceholderManager().load();
        }
    }
}

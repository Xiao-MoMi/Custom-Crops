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

package net.momirealms.customcrops.common.config;

import dev.dejvokep.boostedyaml.YamlDocument;

import java.io.File;

/**
 * Interface for loading and managing configuration files.
 */
public interface ConfigLoader {

    /**
     * Loads a YAML configuration file from the specified file path.
     *
     * @param filePath the path to the configuration file
     * @return the loaded {@link YamlDocument}
     */
    YamlDocument loadConfig(String filePath);

    /**
     * Loads a YAML configuration file from the specified file path with a custom route separator.
     *
     * @param filePath      the path to the configuration file
     * @param routeSeparator the custom route separator character
     * @return the loaded {@link YamlDocument}
     */
    YamlDocument loadConfig(String filePath, char routeSeparator);

    /**
     * Loads a YAML data file.
     *
     * @param file the {@link File} object representing the data file
     * @return the loaded {@link YamlDocument}
     */
    YamlDocument loadData(File file);

    /**
     * Loads a YAML data file with a custom route separator.
     *
     * @param file           the {@link File} object representing the data file
     * @param routeSeparator the custom route separator character
     * @return the loaded {@link YamlDocument}
     */
    YamlDocument loadData(File file, char routeSeparator);

    /**
     * Saves a resource file from the plugin's jar to the specified file path.
     *
     * @param filePath the path where the resource file will be saved
     */
    void saveResource(String filePath);
}

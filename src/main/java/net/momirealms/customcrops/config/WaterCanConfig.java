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

import net.momirealms.customcrops.objects.WaterCan;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class WaterCanConfig {

    public static HashMap<String, WaterCan> CANS = new HashMap<>();

    public static void load() {
        CANS = new HashMap<>(8);
        YamlConfiguration config = ConfigUtil.getConfig("watercans_" + MainConfig.customPlugin + ".yml");
        for (String key : config.getKeys(false)) {
            WaterCan waterCan = new WaterCan(
                    config.getInt(key + ".max-water-storage"),
                    config.getInt(key + ".width"),
                    config.getInt(key + ".length")
            );
            CANS.put(config.getString(key + ".item"), waterCan);
        }
        AdventureUtil.consoleMessage("[CustomCrops] Loaded <green>" + CANS.size() + "<gray> watering cans");
    }
}
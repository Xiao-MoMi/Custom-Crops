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

import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class SprinklerConfig {

    public static HashMap<String, Sprinkler> SPRINKLERS_CONFIG;
    public static HashMap<String, Sprinkler> SPRINKLERS_2D;
    public static HashMap<String, Sprinkler> SPRINKLERS_3D;

    public static void load() {
        SPRINKLERS_3D = new HashMap<>(8);
        SPRINKLERS_2D = new HashMap<>(8);
        SPRINKLERS_CONFIG = new HashMap<>(8);
        YamlConfiguration config = ConfigUtil.getConfig("sprinklers_" + MainConfig.customPlugin + ".yml");

        int amount = 0;
        for (String key : config.getKeys(false)) {

            Sprinkler sprinkler = new Sprinkler(
                    key,
                    config.getInt(key + ".range", 1),
                    config.getInt(key + ".max-water-storage", 5)
            );
            String twoD = config.getString(key + ".2Ditem");
            String threeD = config.getString(key + ".3Ditem");
            sprinkler.setTwoD(twoD);
            sprinkler.setThreeD(threeD);
            SPRINKLERS_CONFIG.put(key + "CONFIG", sprinkler);
            SPRINKLERS_2D.put(twoD, sprinkler);
            SPRINKLERS_3D.put(threeD, sprinkler);
            amount++;
        }

        AdventureUtil.consoleMessage("[CustomCrops] Loaded <green>" + amount + "<gray> sprinklers");
    }
}

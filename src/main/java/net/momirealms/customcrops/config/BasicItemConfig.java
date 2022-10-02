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

import org.bukkit.configuration.file.YamlConfiguration;

public class BasicItemConfig {

    public static String dryPot;
    public static String wetPot;
    public static String deadCrop;
    public static String soilSurveyor;
    public static String greenHouseGlass;
    public static String crow;
    public static String scarecrow;
    public static String waterEffect;

    public static void load() {
        YamlConfiguration config = ConfigUtil.getConfig("basic_" + MainConfig.customPlugin + ".yml");
        dryPot = config.getString("dry-pot");
        wetPot = config.getString("wet-pot");
        greenHouseGlass = config.getString("greenhouse-glass");
        soilSurveyor = config.getString("soil-surveyor");
        deadCrop = config.getString("dead-crop");
        crow = config.getString("crow");
        scarecrow = config.getString("scarecrow");
        waterEffect = config.getString("water-effect");
    }
}

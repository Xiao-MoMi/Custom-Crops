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

public class SeasonConfig {

    public static boolean enable;
    public static boolean auto;
    public static int duration;
    public static boolean greenhouse;
    public static int effectiveRange;

    public static void load() {

        YamlConfiguration config = ConfigUtil.getConfig("config.yml");
        enable = config.getBoolean("mechanics.season.enable", true);
        auto = config.getBoolean("mechanics.season.auto-season-change.enable", true);
        duration = config.getInt("mechanics.season.auto-season-change.duration", 28);
        greenhouse = config.getBoolean("mechanics.season.greenhouse.enable", true);
        effectiveRange = config.getInt("mechanics.season.greenhouse.range", 5);
    }
}

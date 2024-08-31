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

package net.momirealms.customcrops.api.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PluginUtils {

    public static boolean isEnabled(String name) {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }

    @SuppressWarnings("deprecation")
    public static String getPluginVersion(String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        if (plugin != null) {
            return plugin.getDescription().getVersion();
        }
        return "";
    }
}

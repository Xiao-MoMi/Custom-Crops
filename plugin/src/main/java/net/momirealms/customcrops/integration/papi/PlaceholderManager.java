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

package net.momirealms.customcrops.integration.papi;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderManager extends Function {

    private SeasonPapi seasonPapi;
    private boolean hasPapi;

    public PlaceholderManager(CustomCrops plugin) {
        this.hasPapi = false;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.seasonPapi = new SeasonPapi(plugin);
            this.hasPapi = true;
        }
    }

    @Override
    public void load() {
        if (seasonPapi != null) seasonPapi.register();
    }

    @Override
    public void unload() {
        if (seasonPapi != null) seasonPapi.unregister();
    }

    public String parse(Player player, String text) {
        return hasPapi ? ParseUtil.setPlaceholders(player, text) : text;
    }
}

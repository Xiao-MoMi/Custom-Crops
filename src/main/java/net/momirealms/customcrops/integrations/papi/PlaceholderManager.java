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

package net.momirealms.customcrops.integrations.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customcrops.Function;
import net.momirealms.customcrops.config.SeasonConfig;
import org.bukkit.entity.Player;

public class PlaceholderManager extends Function {

    private SeasonPapi seasonPapi;

    public PlaceholderManager() {
        load();
    }

    @Override
    public void load() {
        super.load();
        if (SeasonConfig.enable) {
            this.seasonPapi = new SeasonPapi();
            this.seasonPapi.register();
        }
    }

    @Override
    public void unload() {
        super.unload();
        if (this.seasonPapi != null) {
            this.seasonPapi.unregister();
        }
    }

    public String parse(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}

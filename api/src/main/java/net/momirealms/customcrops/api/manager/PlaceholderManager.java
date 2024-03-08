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

package net.momirealms.customcrops.api.manager;

import net.momirealms.customcrops.api.common.Reloadable;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class PlaceholderManager implements Reloadable {

    public static final Pattern pattern = Pattern.compile("\\{[^{}]+}");
    private static PlaceholderManager instance;

    public PlaceholderManager() {
        instance = this;
    }

    public static PlaceholderManager getInstance() {
        return instance;
    }

    public abstract String parse(Player player, String text, Map<String, String> vars);

    public abstract List<String> parse(Player player, List<String> text, Map<String, String> vars);

    public abstract List<String> detectPlaceholders(String text);

    public abstract String setPlaceholders(Player player, String text);
}

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

package net.momirealms.customcrops.api.misc.value;

import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.misc.placeholder.BukkitPlaceholderManager;
import org.bukkit.OfflinePlayer;

import java.util.Map;

public class PlaceholderTextValueImpl<T> implements TextValue<T> {

    private final String raw;

    public PlaceholderTextValueImpl(String raw) {
        this.raw = raw;
    }

    @Override
    public String render(Context<T> context) {
        Map<String, String> replacements = context.placeholderMap();
        String text;
        if (context.holder() instanceof OfflinePlayer player) text = BukkitPlaceholderManager.getInstance().parse(player, raw, replacements);
        else text = BukkitPlaceholderManager.getInstance().parse(null, raw, replacements);
        return text;
    }
}

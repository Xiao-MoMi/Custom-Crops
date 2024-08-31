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

import net.momirealms.customcrops.api.misc.placeholder.BukkitPlaceholderManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DynamicText {

    private final Player owner;
    private String originalValue;
    private String latestValue;
    private String[] placeholders;

    public DynamicText(Player owner, String rawValue) {
        this.owner = owner;
        analyze(rawValue);
    }

    private void analyze(String value) {
        // Analyze the provided text to find and replace placeholders with '%s'.
        // Store the original value, placeholders, and the initial latest value.
        List<String> placeholdersOwner = new ArrayList<>(BukkitPlaceholderManager.getInstance().resolvePlaceholders(value));
        String origin = value;
        for (String placeholder : placeholdersOwner) {
            origin = origin.replace(placeholder, "%s");
        }
        originalValue = origin;
        placeholders = placeholdersOwner.toArray(new String[0]);
        latestValue = originalValue;
    }

    public String getLatestValue() {
        return latestValue;
    }

    public boolean update(Map<String, String> placeholders) {
        String string = originalValue;
        if (this.placeholders.length != 0) {
            BukkitPlaceholderManager bukkitPlaceholderManager = BukkitPlaceholderManager.getInstance();
            if ("%s".equals(originalValue)) {
                string = bukkitPlaceholderManager.parseSingle(owner, this.placeholders[0], placeholders);
            } else {
                Object[] values = new String[this.placeholders.length];
                for (int i = 0; i < this.placeholders.length; i++) {
                    values[i] = bukkitPlaceholderManager.parseSingle(owner, this.placeholders[i], placeholders);
                }
                string = String.format(originalValue, values);
            }
        }
        if (!latestValue.equals(string)) {
            latestValue = string;
            return true;
        }
        return false;
    }
}

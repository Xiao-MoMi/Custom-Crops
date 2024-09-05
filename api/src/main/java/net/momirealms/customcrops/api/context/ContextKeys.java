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

package net.momirealms.customcrops.api.context;

import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;

/**
 * Represents keys for accessing context values with specific types.
 *
 * @param <T> the type of the value associated with the context key.
 */
public class ContextKeys<T> {

    public static final ContextKeys<Location> LOCATION = of("location", Location.class);
    public static final ContextKeys<String> WATER_BAR = of("water_bar", String.class);
    public static final ContextKeys<Integer> CURRENT_WATER = of("current", Integer.class);
    public static final ContextKeys<Integer> STORAGE = of("storage", Integer.class);
    public static final ContextKeys<Integer> X = of("x", Integer.class);
    public static final ContextKeys<Integer> Y = of("y", Integer.class);
    public static final ContextKeys<Integer> Z = of("z", Integer.class);
    public static final ContextKeys<String> WORLD = of("world", String.class);
    public static final ContextKeys<String> PLAYER = of("player", String.class);
    public static final ContextKeys<EquipmentSlot> SLOT = of("slot", EquipmentSlot.class);
    public static final ContextKeys<String> TEMP_NEAR_PLAYER = of("near", String.class);
    public static final ContextKeys<Boolean> OFFLINE = of("offline", Boolean.class);
    public static final ContextKeys<String> ICON = of("icon", String.class);
    public static final ContextKeys<Integer> MAX_TIMES = of("max_times", Integer.class);
    public static final ContextKeys<Integer> LEFT_TIMES = of("left_times", Integer.class);

    private final String key;
    private final Class<T> type;

    protected ContextKeys(String key, Class<T> type) {
        this.key = key;
        this.type = type;
    }

    /**
     * Gets the key.
     *
     * @return the key.
     */
    public String key() {
        return key;
    }

    /**
     * Gets the type associated with the key.
     *
     * @return the type.
     */
    public Class<T> type() {
        return type;
    }

    /**
     * Creates a new context key.
     *
     * @param key the key.
     * @param type the type.
     * @param <T> the type of the value.
     * @return a new ContextKeys instance.
     */
    public static <T> ContextKeys<T> of(String key, Class<T> type) {
        return new ContextKeys<T>(key, type);
    }

    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (other != null && this.getClass() == other.getClass()) {
            ContextKeys<?> that = (ContextKeys) other;
            return Objects.equals(this.key, that.key);
        } else {
            return false;
        }
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(this.key);
    }

    @Override
    public String toString() {
        return "ContextKeys{" +
                "key='" + key + '\'' +
                '}';
    }
}

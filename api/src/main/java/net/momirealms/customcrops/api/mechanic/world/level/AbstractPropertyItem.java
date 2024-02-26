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

package net.momirealms.customcrops.api.mechanic.world.level;

import net.momirealms.customcrops.api.common.Property;

import java.util.HashMap;

public class AbstractPropertyItem implements PropertyItem {

    private String key;
    private final HashMap<String, Property<?>> properties;

    public AbstractPropertyItem(String key, HashMap<String, Property<?>> properties) {
        this.key = key;
        this.properties = properties;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public <T> void setProperty(String name, Property<T> property) {
        properties.put(name, property);
    }

    @Override
    public <T> T getProperty(String name, Class<T> type) {
        Property<?> prop = properties.get(name);
        if (prop != null) {
            return type.cast(prop.get());
        }
        return null;
    }
}

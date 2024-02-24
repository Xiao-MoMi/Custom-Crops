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

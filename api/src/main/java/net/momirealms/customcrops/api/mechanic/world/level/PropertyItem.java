package net.momirealms.customcrops.api.mechanic.world.level;

import net.momirealms.customcrops.api.common.Property;
import net.momirealms.customcrops.api.common.item.KeyItem;

public interface PropertyItem extends KeyItem {

    <T> void setProperty(String name, Property<T> property);

    <T> T getProperty(String name, Class<T> type);
}

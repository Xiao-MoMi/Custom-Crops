package net.momirealms.customcrops.api.common.property;

import net.momirealms.customcrops.api.common.Property;

public class SimpleProperty<T> implements Property<T> {

    private T value;

    public SimpleProperty(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }
}

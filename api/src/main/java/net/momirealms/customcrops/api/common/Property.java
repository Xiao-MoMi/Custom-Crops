package net.momirealms.customcrops.api.common;

public interface Property<T> {
    T get();
    void set(T value);
}
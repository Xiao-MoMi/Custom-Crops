package net.momirealms.customcrops.api.core;

public interface ClearableRegistry<K, T> extends WriteableRegistry<K, T> {

    void clear();
}

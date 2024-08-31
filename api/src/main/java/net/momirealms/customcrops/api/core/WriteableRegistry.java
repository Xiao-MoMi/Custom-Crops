package net.momirealms.customcrops.api.core;

public interface WriteableRegistry<K, T> extends Registry<K, T> {

    void register(K key, T value);
}

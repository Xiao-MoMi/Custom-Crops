package net.momirealms.customcrops.api.core;

import net.momirealms.customcrops.common.util.Key;

import javax.annotation.Nullable;

public interface Registry<K, T> extends IdMap<T> {

    Key key();

    @Override
    int getId(@Nullable T value);

    @Nullable
    T get(@Nullable K key);
}

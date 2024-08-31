package net.momirealms.customcrops.api.core;

import javax.annotation.Nullable;

public interface IdMap<T> extends Iterable<T> {
    int DEFAULT = -1;

    int getId(T value);

    @Nullable
    T byId(int index);

    default T byIdOrThrow(int index) {
        T object = this.byId(index);
        if (object == null) {
            throw new IllegalArgumentException("No value with id " + index);
        } else {
            return object;
        }
    }

    default int getIdOrThrow(T value) {
        int i = this.getId(value);
        if (i == -1) {
            throw new IllegalArgumentException("Can't find id for '" + value + "' in map " + this);
        } else {
            return i;
        }
    }

    int size();
}

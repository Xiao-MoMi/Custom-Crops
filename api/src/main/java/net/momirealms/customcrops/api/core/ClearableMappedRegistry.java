package net.momirealms.customcrops.api.core;

import net.momirealms.customcrops.common.util.Key;

public class ClearableMappedRegistry<K, T> extends MappedRegistry<K, T> implements ClearableRegistry<K, T> {

    public ClearableMappedRegistry(Key key) {
        super(key);
    }

    @Override
    public void clear() {
        super.byID.clear();
        super.byKey.clear();
        super.byValue.clear();
    }
}

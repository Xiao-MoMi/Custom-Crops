package net.momirealms.customcrops.api.core;

import net.momirealms.customcrops.common.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MappedRegistry<K, T> implements WriteableRegistry<K, T> {

    protected final Map<K, T> byKey = new HashMap<>(1024);
    protected final Map<T, K> byValue = new IdentityHashMap<>(1024);
    protected final ArrayList<T> byID = new ArrayList<>(1024);
    private final Key key;

    public MappedRegistry(Key key) {
        this.key = key;
    }

    @Override
    public void register(K key, T value) {
        byKey.put(key, value);
        byValue.put(value, key);
    }

    @Override
    public Key key() {
        return key;
    }

    @Override
    public int getId(@Nullable T value) {
        return byID.indexOf(value);
    }

    @Nullable
    @Override
    public T byId(int index) {
        return byID.get(index);
    }

    @Override
    public int size() {
        return byKey.size();
    }

    @Nullable
    @Override
    public T get(@Nullable K key) {
        return byKey.get(key);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.byKey.values().iterator();
    }
}

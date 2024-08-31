/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.api.context;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractContext<T> implements Context<T> {

    private final T holder;
    private final Map<ContextKeys<?>, Object> args;
    private final Map<String, String> placeholderMap;

    public AbstractContext(@Nullable T holder, boolean sync) {
        this.holder = holder;
        this.args = sync ? new ConcurrentHashMap<>() : new HashMap<>();
        this.placeholderMap = sync ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    @Override
    public Map<ContextKeys<?>, Object> args() {
        return args;
    }

    @Override
    public Map<String, String> placeholderMap() {
        return placeholderMap;
    }

    @Override
    public <C> AbstractContext<T> arg(ContextKeys<C> key, C value) {
        if (key == null || value == null) return this;
        this.args.put(key, value);
        this.placeholderMap.put("{" + key.key() + "}", value.toString());
        return this;
    }

    @Override
    public AbstractContext<T> combine(Context<T> other) {
        this.args.putAll(other.args());
        this.placeholderMap.putAll(other.placeholderMap());
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C arg(ContextKeys<C> key) {
        return (C) args.get(key);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    @Override
    public <C> C remove(ContextKeys<C> key) {
        placeholderMap.remove("{" + key.key() + "}");
        return (C) args.remove(key);
    }

    @Override
    public T holder() {
        return holder;
    }
}

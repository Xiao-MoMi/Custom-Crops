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

package net.momirealms.customcrops.common.item;

import net.momirealms.customcrops.common.plugin.CustomCropsPlugin;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class ItemFactory<P extends CustomCropsPlugin, R, I> {

    protected final P plugin;

    protected ItemFactory(P plugin) {
        this.plugin = plugin;
    }

    public Item<I> wrap(R item) {
        Objects.requireNonNull(item, "item");
        return new AbstractItem<>(this.plugin, this, item);
    }

    protected abstract Optional<Object> getTag(R item, Object... path);

    protected abstract void setTag(R item, Object value, Object... path);

    protected abstract boolean hasTag(R item, Object... path);

    protected abstract boolean removeTag(R item, Object... path);

    protected abstract void update(R item);

    protected abstract I load(R item);

    protected abstract I getItem(R item);

    protected abstract I loadCopy(R item);

    protected abstract void customModelData(R item, Integer data);

    protected abstract Optional<Integer> customModelData(R item);

    protected abstract Optional<List<String>> lore(R item);

    protected abstract void lore(R item, List<String> lore);

    protected abstract Optional<Integer> damage(R item);

    protected abstract void damage(R item, Integer damage);

    protected abstract Optional<Integer> maxDamage(R item);

    protected abstract void maxDamage(R item, Integer damage);

    protected abstract boolean unbreakable(R item);
}

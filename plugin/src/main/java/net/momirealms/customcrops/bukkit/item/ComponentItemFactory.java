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

package net.momirealms.customcrops.bukkit.item;

import com.saicone.rtag.RtagItem;
import com.saicone.rtag.data.ComponentType;
import net.momirealms.customcrops.common.item.ComponentKeys;
import net.momirealms.customcrops.common.plugin.CustomCropsPlugin;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class ComponentItemFactory extends BukkitItemFactory {

    public ComponentItemFactory(CustomCropsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void customModelData(RtagItem item, Integer data) {
        if (data == null) {
            item.removeComponent(ComponentKeys.CUSTOM_MODEL_DATA);
        } else {
            item.setComponent(ComponentKeys.CUSTOM_MODEL_DATA, data);
        }
    }

    @Override
    protected Optional<Integer> customModelData(RtagItem item) {
        if (!item.hasComponent(ComponentKeys.CUSTOM_MODEL_DATA)) return Optional.empty();
        return Optional.ofNullable(
                (Integer) ComponentType.encodeJava(
                        ComponentKeys.CUSTOM_MODEL_DATA,
                    item.getComponent(ComponentKeys.CUSTOM_MODEL_DATA)
                ).orElse(null)
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Optional<List<String>> lore(RtagItem item) {
        if (item.getComponent(ComponentKeys.LORE) == null) return Optional.empty();
        return Optional.ofNullable(
                (List<String>) ComponentType.encodeJava(
                        ComponentKeys.LORE,
                        item.getComponent(ComponentKeys.LORE)
                ).orElse(null)
        );
    }

    @Override
    protected void lore(RtagItem item, List<String> lore) {
        if (lore == null || lore.isEmpty()) {
            item.removeComponent(ComponentKeys.LORE);
        } else {
            item.setComponent(ComponentKeys.LORE, lore);
        }
    }

    @Override
    protected Optional<Integer> maxDamage(RtagItem item) {
        if (!item.hasComponent(ComponentKeys.MAX_DAMAGE)) return Optional.of((int) item.getItem().getType().getMaxDurability());
        return Optional.ofNullable(
                (Integer) ComponentType.encodeJava(
                        ComponentKeys.MAX_DAMAGE,
                        item.getComponent(ComponentKeys.MAX_DAMAGE)
                ).orElse(null)
        );
    }

    @Override
    protected void maxDamage(RtagItem item, Integer data) {
        if (data == null) {
            item.removeComponent(ComponentKeys.MAX_DAMAGE);
        } else {
            item.setComponent(ComponentKeys.MAX_DAMAGE, data);
        }
    }

    @Override
    protected Optional<Integer> damage(RtagItem item) {
        if (!item.hasComponent(ComponentKeys.DAMAGE)) return Optional.of(0);
        return Optional.ofNullable(
                (Integer) ComponentType.encodeJava(
                        ComponentKeys.DAMAGE,
                        item.getComponent(ComponentKeys.DAMAGE)
                ).orElse(null)
        );
    }

    @Override
    protected void damage(RtagItem item, Integer data) {
        if (data == null) {
            item.removeComponent(ComponentKeys.DAMAGE);
        } else {
            item.setComponent(ComponentKeys.DAMAGE, data);
        }
    }
}
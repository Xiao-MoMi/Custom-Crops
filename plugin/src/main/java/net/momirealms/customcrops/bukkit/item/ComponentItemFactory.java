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
import net.momirealms.customcrops.common.helper.VersionHelper;
import net.momirealms.customcrops.common.item.ComponentKeys;
import net.momirealms.customcrops.common.plugin.CustomCropsPlugin;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class ComponentItemFactory extends BukkitItemFactory {

    private final BiConsumer<RtagItem, Integer> customModelDataSetter;
    private final Function<RtagItem, Optional<Integer>> customModelDataGetter;

    public ComponentItemFactory(CustomCropsPlugin plugin) {
        super(plugin);
        this.customModelDataSetter = VersionHelper.isVersionNewerThan1_21_4() ?
                ((item, data) -> item.setComponent(ComponentKeys.CUSTOM_MODEL_DATA,
                        Map.of("floats", List.of(data.floatValue())))) : ((item, data) -> item.setComponent(ComponentKeys.CUSTOM_MODEL_DATA, data));
        this.customModelDataGetter = VersionHelper.isVersionNewerThan1_21_4() ?
                (item) -> {
                    Optional<Object> optional = ComponentType.encodeJava(ComponentKeys.CUSTOM_MODEL_DATA, item.getComponent(ComponentKeys.CUSTOM_MODEL_DATA));
                    if (optional.isEmpty()) return Optional.empty();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) optional.get();
                    @SuppressWarnings("unchecked")
                    List<Float> floats = (List<Float>) data.get("floats");
                    if (floats == null || floats.isEmpty()) return Optional.empty();
                    return Optional.of((int) Math.floor(floats.get(0)));
                } : (item) -> Optional.ofNullable(
                (Integer) ComponentType.encodeJava(
                        ComponentKeys.CUSTOM_MODEL_DATA,
                        item.getComponent(ComponentKeys.CUSTOM_MODEL_DATA)
                ).orElse(null)
        );
    }

    @Override
    protected void customModelData(RtagItem item, Integer data) {
        if (data == null) {
            item.removeComponent(ComponentKeys.CUSTOM_MODEL_DATA);
        } else {
            this.customModelDataSetter.accept(item, data);
        }
    }

    @Override
    protected Optional<Integer> customModelData(RtagItem item) {
        if (!item.hasComponent(ComponentKeys.CUSTOM_MODEL_DATA)) return Optional.empty();
        return this.customModelDataGetter.apply(item);
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
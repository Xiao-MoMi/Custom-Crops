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
import net.momirealms.customcrops.common.plugin.CustomCropsPlugin;

import java.util.List;
import java.util.Optional;

public class UniversalItemFactory extends BukkitItemFactory {

    public UniversalItemFactory(CustomCropsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void customModelData(RtagItem item, Integer data) {
        if (data == null) {
            item.remove("CustomModelData");
        } else {
            item.set(data, "CustomModelData");
        }
    }

    @Override
    protected Optional<Integer> customModelData(RtagItem item) {
        if (!item.hasTag("CustomModelData")) return Optional.empty();
        return Optional.of(item.get("CustomModelData"));
    }

    @Override
    protected Optional<List<String>> lore(RtagItem item) {
        if (!item.hasTag("display", "Lore")) return Optional.empty();
        return Optional.of(item.get("display", "Lore"));
    }

    @Override
    protected void lore(RtagItem item, List<String> lore) {
        if (lore == null || lore.isEmpty()) {
            item.remove("display", "Lore");
        } else {
            item.set(lore, "display", "Lore");
        }
    }

    @Override
    protected Optional<Integer> maxDamage(RtagItem item) {
        return Optional.of((int) item.getItem().getType().getMaxDurability());
    }

    @Override
    protected void maxDamage(RtagItem item, Integer data) {
        throw new RuntimeException("Unsupported operation");
    }

    @Override
    protected Optional<Integer> damage(RtagItem item) {
        return Optional.of(item.getOptional("Damage").or(0));
    }

    @Override
    protected void damage(RtagItem item, Integer data) {
        item.set(data, "Damage");
    }
}

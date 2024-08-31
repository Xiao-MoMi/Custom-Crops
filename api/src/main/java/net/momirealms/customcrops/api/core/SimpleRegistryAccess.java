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

package net.momirealms.customcrops.api.core;

import net.momirealms.customcrops.common.util.Key;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.item.CustomCropsItem;
import net.momirealms.customcrops.api.core.item.FertilizerType;

public class SimpleRegistryAccess implements RegistryAccess {

    private BukkitCustomCropsPlugin plugin;
    private boolean frozen;

    public SimpleRegistryAccess(BukkitCustomCropsPlugin plugin) {
        this.plugin = plugin;
    }

    public void freeze() {
        this.frozen = true;
    }

    @Override
    public void registerBlockMechanic(CustomCropsBlock block) {
        if (frozen) throw new RuntimeException("Registries are frozen");
        Registries.BLOCK.register(block.type(), block);
    }

    @Override
    public void registerItemMechanic(CustomCropsItem item) {
        if (frozen) throw new RuntimeException("Registries are frozen");
        Registries.ITEM.register(item.type(), item);
    }

    @Override
    public void registerFertilizerType(FertilizerType type) {
        if (frozen) throw new RuntimeException("Registries are frozen");
        Registries.FERTILIZER_TYPE.register(type.id(), type);
    }

    @Override
    public Registry<Key, CustomCropsBlock> getBlockRegistry() {
        return Registries.BLOCK;
    }

    @Override
    public Registry<Key, CustomCropsItem> getItemRegistry() {
        return Registries.ITEM;
    }

    @Override
    public Registry<String, FertilizerType> getFertilizerTypeRegistry() {
        return Registries.FERTILIZER_TYPE;
    }
}

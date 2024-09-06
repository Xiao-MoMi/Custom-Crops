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

import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.item.CustomCropsItem;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerType;
import net.momirealms.customcrops.common.util.Key;

@SuppressWarnings("deprecation")
public class SimpleRegistryAccess implements RegistryAccess {

    private boolean frozen;
    private static SimpleRegistryAccess instance;

    private SimpleRegistryAccess() {
        instance = this;
    }

    public static SimpleRegistryAccess getInstance() {
        if (instance == null) {
            instance = new SimpleRegistryAccess();
        }
        return instance;
    }

    public void freeze() {
        this.frozen = true;
    }

    @Override
    public void registerBlockMechanic(CustomCropsBlock block) {
        if (frozen) throw new RuntimeException("Registries are frozen");
        InternalRegistries.BLOCK.register(block.type(), block);
    }

    @Override
    public void registerItemMechanic(CustomCropsItem item) {
        if (frozen) throw new RuntimeException("Registries are frozen");
        InternalRegistries.ITEM.register(item.type(), item);
    }

    @Override
    public void registerFertilizerType(FertilizerType type) {
        if (frozen) throw new RuntimeException("Registries are frozen");
        InternalRegistries.FERTILIZER_TYPE.register(type.id(), type);
    }

    @Override
    public Registry<Key, CustomCropsBlock> getBlockRegistry() {
        return InternalRegistries.BLOCK;
    }

    @Override
    public Registry<Key, CustomCropsItem> getItemRegistry() {
        return InternalRegistries.ITEM;
    }

    @Override
    public Registry<String, FertilizerType> getFertilizerTypeRegistry() {
        return InternalRegistries.FERTILIZER_TYPE;
    }
}

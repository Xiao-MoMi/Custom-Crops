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

package net.momirealms.customcrops.bukkit.integration.custom.craftengine_r1;

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineFurniture;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.entity.furniture.LoadedFurniture;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.entity.furniture.AnchorType;
import net.momirealms.craftengine.core.item.ItemBuildContext;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import net.momirealms.customcrops.api.core.CustomItemProvider;
import net.momirealms.customcrops.api.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

public class CraftEngineProvider implements CustomItemProvider {

    @Override
    public boolean removeCustomBlock(Location location) {
        return CraftEngineBlocks.remove(location.getBlock());
    }

    @Override
    public boolean placeCustomBlock(Location location, String id) {
        return CraftEngineBlocks.place(location, Key.of(id), new CompoundTag(), false);
    }

    @Override
    public @Nullable Entity placeFurniture(Location location, String id) {
        LoadedFurniture furniture = CraftEngineFurniture.place(LocationUtils.toSurfaceCenterLocation(location), Key.of(id), AnchorType.GROUND);
        if (furniture == null) return null;
        return furniture.baseEntity();
    }

    @Override
    public boolean removeFurniture(Entity entity) {
        return CraftEngineFurniture.remove(entity);
    }

    @Override
    public @Nullable String blockID(Block block) {
        ImmutableBlockState state = CraftEngineBlocks.getCustomBlockState(block);
        if (state == null) return null;
        return state.owner().value().id().toString();
    }

    @Override
    public @Nullable String itemID(ItemStack itemStack) {
        return Optional.ofNullable(CraftEngineItems.getCustomItemId(itemStack)).map(Key::toString).orElse(null);
    }

    @Override
    public @Nullable ItemStack itemStack(Player player, String id) {
        return Optional.ofNullable(CraftEngineItems.byId(Key.of(id)))
                .map(it -> it.buildItemStack(ItemBuildContext.EMPTY))
                .orElse(null);
    }

    @Override
    public @Nullable String furnitureID(Entity entity) {
        return Optional.ofNullable(CraftEngineFurniture.getLoadedFurnitureByBaseEntity(entity))
                .map(it -> it.id().toString())
                .orElse(null);
    }

    @Override
    public boolean isFurniture(Entity entity) {
        return CraftEngineFurniture.isFurniture(entity);
    }
}

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

import net.momirealms.customcrops.common.item.Item;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemManager {

    void place(@NotNull Location location, @NotNull ExistenceForm form, @NotNull String id, FurnitureRotation rotation);

    FurnitureRotation remove(@NotNull Location location, @NotNull ExistenceForm form);

    void placeBlock(@NotNull Location location, @NotNull String id);

    void placeFurniture(@NotNull Location location, @NotNull String id, FurnitureRotation rotation);

    void removeBlock(@NotNull Location location);

    @Nullable
    FurnitureRotation removeFurniture(@NotNull Location location);

    @NotNull
    default String blockID(@NotNull Location location) {
        return blockID(location.getBlock());
    }

    @NotNull
    String blockID(@NotNull Block block);

    @Nullable
    String furnitureID(@NotNull Entity entity);

    @NotNull String entityID(@NotNull Entity entity);

    @Nullable
    String furnitureID(Location location);

    @NotNull
    String anyID(Location location);

    @Nullable
    String id(Location location, ExistenceForm form);

    void setCustomEventListener(@NotNull AbstractCustomEventListener listener);

    void setCustomItemProvider(@NotNull CustomItemProvider provider);

    String id(ItemStack itemStack);

    @Nullable
    ItemStack build(Player player, String id);

    Item<ItemStack> wrap(ItemStack itemStack);

    void decreaseDamage(Player player, ItemStack itemStack, int amount);

    void increaseDamage(Player holder, ItemStack itemStack, int amount);
}

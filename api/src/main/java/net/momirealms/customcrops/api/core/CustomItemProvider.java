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

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface CustomItemProvider {

    boolean removeCustomBlock(Location location);

    boolean placeCustomBlock(Location location, String id);

    @Nullable
    Entity placeFurniture(Location location, String id);

    boolean removeFurniture(Entity entity);

    @Nullable
    String blockID(Block block);

    @Nullable
    String itemID(ItemStack itemStack);

    @Nullable
    ItemStack itemStack(Player player, String id);

    @Nullable
    String furnitureID(Entity entity);

    boolean isFurniture(Entity entity);
}

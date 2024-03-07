/*
 *  Copyright (C) <2022> <XiaoMoMi>
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

package net.momirealms.customcrops.api.manager;

import net.momirealms.customcrops.api.common.Reloadable;
import net.momirealms.customcrops.api.integration.ItemLibrary;
import net.momirealms.customcrops.api.mechanic.item.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ItemManager extends Reloadable {

    boolean registerItemLibrary(@NotNull ItemLibrary itemLibrary);

    boolean unregisterItemLibrary(String identification);

    String getItemID(ItemStack itemStack);

    ItemStack getItemStack(Player player, String id);

    void placeItem(Location location, ItemCarrier carrier, String id);

    void removeAnythingAt(Location location);

    @Nullable
    WateringCan getWateringCanByID(@NotNull String id);

    @Nullable
    WateringCan getWateringCanByItemID(@NotNull String id);

    @Nullable
    WateringCan getWateringCanByItemStack(@NotNull ItemStack itemStack);

    @Nullable
    Sprinkler getSprinklerByID(@NotNull String id);

    @Nullable
    Sprinkler getSprinklerBy3DItemID(@NotNull String id);

    @Nullable
    Sprinkler getSprinklerBy2DItemID(@NotNull String id);

    @Nullable
    Sprinkler getSprinklerByEntity(@NotNull Entity entity);

    @Nullable
    Sprinkler getSprinklerBy2DItemStack(@NotNull ItemStack itemStack);

    @Nullable
    Sprinkler getSprinklerBy3DItemStack(@NotNull ItemStack itemStack);

    @Nullable
    Sprinkler getSprinklerByItemStack(@NotNull ItemStack itemStack);

    @Nullable
    Pot getPotByID(@NotNull String id);

    @Nullable
    Pot getPotByBlockID(@NotNull String id);

    @Nullable
    Pot getPotByBlock(@NotNull Block block);

    @Nullable
    Pot getPotByItemStack(@NotNull ItemStack itemStack);

    Fertilizer getFertilizerByID(String id);

    Fertilizer getFertilizerByItemID(String id);

    Fertilizer getFertilizerByItemStack(@NotNull ItemStack itemStack);

    Crop getCropByID(String id);

    Crop getCropBySeedID(String id);

    Crop getCropBySeedItemStack(ItemStack itemStack);

    Crop getCropByStageID(String id);

    Crop getCropByEntity(Entity entity);

    Crop getCropByBlock(Block block);

    Crop.Stage getCropStageByStageID(String id);

    void updatePotState(Location location, Pot pot, boolean hasWater, Fertilizer fertilizer);

    @NotNull
    Collection<Location> getPotInRange(Location baseLocation, int width, int length, float yaw, String potID);
}

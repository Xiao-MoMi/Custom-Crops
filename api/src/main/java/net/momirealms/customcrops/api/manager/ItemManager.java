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
import net.momirealms.customcrops.api.mechanic.misc.CRotation;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface ItemManager extends Reloadable {

    /**
     * Register an item library
     *
     * @param itemLibrary item library
     * @return success or not
     */
    boolean registerItemLibrary(@NotNull ItemLibrary itemLibrary);

    /**
     * Unregister an item library by identification
     *
     * @param identification identification
     * @return success or not
     */
    boolean unregisterItemLibrary(String identification);

    /**
     * Get item's id by ItemStack
     * ItemsAdder: namespace:id
     * Oraxen: id
     * Vanilla: CAPITAL_ID
     * Other item libraries: LibraryID:ItemID
     *
     * @param itemStack item
     * @return ID
     */
    String getItemID(ItemStack itemStack);

    /**
     * Get item by ID
     * ItemsAdder: namespace:id
     * Oraxen: id
     * Vanilla: CAPITAL_ID
     * Other item libraries: LibraryID:ItemID
     *
     * @param player player
     * @param id id
     * @return item
     */
    ItemStack getItemStack(Player player, String id);

    /**
     * Place an item at a certain location
     *
     * @param location location
     * @param carrier carrier
     * @param id id
     */
    void placeItem(Location location, ItemCarrier carrier, String id);

    /**
     * Place an item at a certain location
     *
     * @param location location
     * @param carrier carrier
     * @param id id
     * @param rotation rotation
     */
    void placeItem(Location location, ItemCarrier carrier, String id, CRotation rotation);

    /**
     * Remove any block/entity from a certain location
     *
     * @param location location
     * @return the rotation of the removed entity
     */
    CRotation removeAnythingAt(Location location);

    /**
     * Get the rotation of the removed entity
     *
     * @param location location
     * @return rotation
     */
    CRotation getRotation(Location location);

    /**
     * Get watering can config by ID
     *
     * @param id id
     * @return watering can config
     */
    @Nullable
    WateringCan getWateringCanByID(@NotNull String id);

    /**
     * Get watering can config by item ID
     *
     * @param id item ID
     * @return watering can config
     */
    @Nullable
    WateringCan getWateringCanByItemID(@NotNull String id);

    /**
     * Get watering can config by itemStack
     *
     * @param itemStack itemStack
     * @return watering can config
     */
    @Nullable
    WateringCan getWateringCanByItemStack(@NotNull ItemStack itemStack);

    /**
     * Get sprinkler config by ID
     *
     * @param id id
     * @return sprinkler config
     */
    @Nullable
    Sprinkler getSprinklerByID(@NotNull String id);

    /**
     * Get sprinkler config by 3D item ID
     *
     * @param id 3D item ID
     * @return sprinkler config
     */
    @Nullable
    Sprinkler getSprinklerBy3DItemID(@NotNull String id);

    /**
     * Get sprinkler config by 2D item ID
     *
     * @param id 2D item ID
     * @return sprinkler config
     */
    @Nullable
    Sprinkler getSprinklerBy2DItemID(@NotNull String id);

    /**
     * Get sprinkler config by entity
     *
     * @param entity entity
     * @return sprinkler config
     */
    @Nullable
    Sprinkler getSprinklerByEntity(@NotNull Entity entity);

    /**
     * Get sprinkler config by block
     *
     * @param block block
     * @return sprinkler config
     */
    @Nullable
    Sprinkler getSprinklerByBlock(@NotNull Block block);

    /**
     * Get sprinkler config by 2D itemStack
     *
     * @param itemStack 2D itemStack
     * @return sprinkler config
     */
    @Nullable
    Sprinkler getSprinklerBy2DItemStack(@NotNull ItemStack itemStack);

    /**
     * Get sprinkler config by 3D itemStack
     *
     * @param itemStack 3D itemStack
     * @return sprinkler config
     */
    @Nullable
    Sprinkler getSprinklerBy3DItemStack(@NotNull ItemStack itemStack);

    /**
     * Get pot config by ID
     *
     * @param id id
     * @return pot config
     */
    @Nullable
    Pot getPotByID(@NotNull String id);

    /**
     * Get pot config by block ID
     *
     * @param id block ID
     * @return pot config
     */
    @Nullable
    Pot getPotByBlockID(@NotNull String id);

    /**
     * Get pot config by block
     *
     * @param block block
     * @return pot config
     */
    @Nullable
    Pot getPotByBlock(@NotNull Block block);

    /**
     * Get pot config by block itemStack
     *
     * @param itemStack itemStack
     * @return pot config
     */
    @Nullable
    Pot getPotByItemStack(@NotNull ItemStack itemStack);

    /**
     * Get fertilizer config by ID
     *
     * @param id id
     * @return fertilizer config
     */
    @Nullable
    Fertilizer getFertilizerByID(String id);

    /**
     * Get fertilizer config by item ID
     *
     * @param id item id
     * @return fertilizer config
     */
    @Nullable
    Fertilizer getFertilizerByItemID(String id);

    /**
     * Get fertilizer config by itemStack
     *
     * @param itemStack itemStack
     * @return fertilizer config
     */
    @Nullable
    Fertilizer getFertilizerByItemStack(@NotNull ItemStack itemStack);

    /**
     * Get crop config by ID
     *
     * @param id id
     * @return crop config
     */
    @Nullable
    Crop getCropByID(String id);

    /**
     * Get crop config by seed ID
     *
     * @param id seed ID
     * @return crop config
     */
    @Nullable
    Crop getCropBySeedID(String id);

    /**
     * Get crop config by seed itemStack
     *
     * @param itemStack seed itemStack
     * @return crop config
     */
    @Nullable
    Crop getCropBySeedItemStack(ItemStack itemStack);

    /**
     * Get crop config by stage item ID
     *
     * @param id stage item ID
     * @return crop config
     */
    @Nullable
    Crop getCropByStageID(String id);

    /**
     * Get crop config by entity
     *
     * @param entity entity
     * @return crop config
     */
    @Nullable
    Crop getCropByEntity(Entity entity);

    /**
     * Get crop config by block
     *
     * @param block block
     * @return crop config
     */
    @Nullable
    Crop getCropByBlock(Block block);

    /**
     * Get crop stage config by stage ID
     *
     * @param id stage ID
     * @return crop stage config
     */
    @Nullable
    Crop.Stage getCropStageByStageID(String id);

    /**
     * Update a pot's block state
     *
     * @param location location
     * @param pot pot config
     * @param hasWater has water or not
     * @param fertilizer fertilizer
     */
    void updatePotState(Location location, Pot pot, boolean hasWater, Fertilizer fertilizer);

    /**
     * Get the pots that can be watered with a watering can
     *
     * @param baseLocation the clicked pot's location
     * @param width width of the working range
     * @param length length of the working range
     * @param yaw player's yaw
     * @param potID pot's ID
     * @return the pots that can be watered
     */
    @NotNull
    Collection<Location> getPotInRange(Location baseLocation, int width, int length, float yaw, String potID);

    void handlePlayerInteractBlock(
            Player player,
            Block clickedBlock,
            BlockFace clickedFace,
            Cancellable event
    );

    void handlePlayerInteractAir(
            Player player,
            Cancellable event
    );

    void handlePlayerBreakBlock(
            Player player,
            Block brokenBlock,
            String blockID,
            Cancellable event
    );

    void handlePlayerInteractFurniture(
            Player player,
            Location location,
            String id,
            Entity baseEntity,
            Cancellable event
    );

    void handlePlayerPlaceFurniture(
            Player player,
            Location location,
            String id,
            Cancellable event
    );

    void handlePlayerBreakFurniture(
            Player player,
            Location location,
            String id,
            Cancellable event
    );

    void handlePlayerPlaceBlock(
            Player player,
            Block block,
            String blockID,
            Cancellable event
    );

    void handleEntityTramplingBlock(
            Entity entity,
            Block block,
            Cancellable event
    );

    void handleExplosion(
            Entity entity,
            List<Block> blocks,
            Cancellable event
    );
}

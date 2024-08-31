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

package net.momirealms.customcrops.api.event;

import net.momirealms.customcrops.api.core.block.CropConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An event that triggered when a player interacts a crop
 */
public class CropInteractEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location location;
    private final CustomCropsBlockState blockState;
    private final ItemStack itemInHand;
    private final EquipmentSlot hand;
    private final CropConfig config;
    private final String stageItemID;

    public CropInteractEvent(
            @NotNull Player who,
            @NotNull ItemStack itemInHand,
            @NotNull Location location,
            @NotNull CustomCropsBlockState blockState,
            @NotNull EquipmentSlot hand,
            @NotNull CropConfig config,
            @NotNull String stageItemID
    ) {
        super(who);
        this.location = location;
        this.blockState = blockState;
        this.itemInHand = itemInHand;
        this.hand = hand;
        this.config = config;
        this.stageItemID = stageItemID;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    /**
     * Get the crop location
     *
     * @return location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }

    /**
     * Get the item in player's hand
     * If there's nothing in hand, it would return AIR
     *
     * @return item in hand
     */
    @NotNull
    public ItemStack getItemInHand() {
        return itemInHand;
    }

    @NotNull
    public CropConfig getCropConfig() {
        return config;
    }

    @NotNull
    public CustomCropsBlockState getBlockState() {
        return blockState;
    }

    @NotNull
    public EquipmentSlot getHand() {
        return hand;
    }

    @NotNull
    public String getStageItemID() {
        return stageItemID;
    }
}

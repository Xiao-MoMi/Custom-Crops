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

import net.momirealms.customcrops.api.object.CCFertilizer;
import net.momirealms.customcrops.api.object.CCGrowingCrop;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is called after a player interacted a pot
 * So the fertilizer/water would be updated
 */
public class PotInfoEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final CCFertilizer fertilizer;
    private final int water;
    private final CCGrowingCrop growingCrop;
    private final ItemStack itemInHand;
    private final Location location;


    public PotInfoEvent(
            @NotNull Player who,
            @NotNull Location location,
            @NotNull ItemStack itemInHand,
            @Nullable CCFertilizer fertilizer,
            int water,
            @Nullable CCGrowingCrop growingCrop
    ) {
        super(who);
        this.fertilizer = fertilizer;
        this.water = water;
        this.growingCrop = growingCrop;
        this.itemInHand = itemInHand;
        this.location = location;
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
     * Get the fertilizer
     * @return fertilizer
     */
    @Nullable
    public CCFertilizer getFertilizer() {
        return fertilizer;
    }

    /**
     * Get the water amount
     * @return water amount
     */
    public int getWater() {
        return water;
    }

    /**
     * Get the on growing crop above the pot
     * It would be null if there's no crop or the crop is already ripe
     * @return crop
     */
    @Nullable
    public CCGrowingCrop getGrowingCrop() {
        return growingCrop;
    }

    /**
     * Get the item in player's hand
     * If there's nothing in hand, it would return AIR
     * @return item in hand
     */
    @NotNull
    public ItemStack getItemInHand() {
        return itemInHand;
    }

    /**
     * Get the pot location
     * @return location
     */
    @NotNull
    public Location getLocation() {
        return location;
    }
}

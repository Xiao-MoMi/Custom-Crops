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

package net.momirealms.customcrops.api.core.wrapper;

import net.momirealms.customcrops.api.core.block.BreakReason;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WrappedBreakEvent  {

    private final Entity entity;
    private final Block block;
    private final Location location;
    private final String brokenID;
    private final ItemStack itemInHand;
    private final String itemID;
    private final Cancellable event;
    private final CustomCropsWorld<?> world;
    private final BreakReason reason;
    private final EquipmentSlot hand;

    public WrappedBreakEvent(
            @Nullable Entity entityBreaker,
            @Nullable Block blockBreaker,
            @Nullable EquipmentSlot hand,
            Location location,
            String brokenID,
            ItemStack itemInHand,
            String itemID,
            BreakReason reason,
            CustomCropsWorld<?> world,
            Cancellable event
    ) {
        this.entity = entityBreaker;
        this.block = blockBreaker;
        this.location = location;
        this.brokenID = brokenID;
        this.hand = hand;
        this.itemInHand = itemInHand;
        this.itemID = itemID;
        this.event = event;
        this.world = world;
        this.reason = reason;
    }

    @NotNull
    public BreakReason reason() {
        return reason;
    }

    @NotNull
    public CustomCropsWorld<?> world() {
        return world;
    }

    @NotNull
    public Location location() {
        return location;
    }

    @NotNull
    public String brokenID() {
        return brokenID;
    }

    @Nullable
    public ItemStack itemInHand() {
        return itemInHand;
    }

    @Nullable
    public String itemID() {
        return itemID;
    }

    @Nullable
    public EquipmentSlot hand() {
        return hand;
    }

    public boolean isCancelled() {
        return event.isCancelled();
    }

    public void setCancelled(boolean cancel) {
        event.setCancelled(cancel);
    }

    @Nullable
    public Player playerBreaker() {
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }

    @Nullable
    public Entity entityBreaker() {
        return entity;
    }

    @Nullable
    public Block blockBreaker() {
        return block;
    }
}

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

import net.momirealms.customcrops.api.object.crop.GrowingCrop;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.object.pot.PotConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotInfoEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final PotConfig potConfig;
    private final Fertilizer fertilizer;
    private final int water;
    private final GrowingCrop growingCrop;
    private final ItemStack hand;
    private final Location location;

    public PotInfoEvent(@NotNull Player who, Location location, ItemStack hand, PotConfig potConfig, @Nullable Fertilizer fertilizer, int water, GrowingCrop growingCrop) {
        super(who);
        this.potConfig = potConfig;
        this.fertilizer = fertilizer;
        this.water = water;
        this.growingCrop = growingCrop;
        this.hand = hand;
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

    public PotConfig getPotConfig() {
        return potConfig;
    }

    @Nullable
    public Fertilizer getFertilizer() {
        return fertilizer;
    }

    public int getWater() {
        return water;
    }

    @Nullable
    public GrowingCrop getGrowingCrop() {
        return growingCrop;
    }

    public ItemStack getHand() {
        return hand;
    }

    public Location getLocation() {
        return location;
    }
}

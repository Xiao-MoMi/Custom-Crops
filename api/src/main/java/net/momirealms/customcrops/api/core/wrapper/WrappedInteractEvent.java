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

import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WrappedInteractEvent {

    private final CustomCropsWorld<?> world;
    private final String relatedID;
    private final ItemStack itemInHand;
    private final String itemID;
    private final EquipmentSlot hand;
    private final BlockFace blockFace;
    private final Cancellable event;
    private final Location clickedLocation;
    private final ExistenceForm existenceForm;
    private final Player player;

    public WrappedInteractEvent(
            ExistenceForm existenceForm,
            Player player,
            CustomCropsWorld<?> world,
            Location clickedLocation,
            String relatedID,
            ItemStack itemInHand,
            String itemID,
            EquipmentSlot hand,
            BlockFace blockFace,
            Cancellable event
    ) {
        this.player = player;
        this.world = world;
        this.clickedLocation = clickedLocation;
        this.relatedID = relatedID;
        this.itemID = itemID;
        this.itemInHand = itemInHand;
        this.hand = hand;
        this.blockFace = blockFace;
        this.event = event;
        this.existenceForm = existenceForm;
    }

    public CustomCropsWorld<?> world() {
        return world;
    }

    public boolean isCancelled() {
        return event.isCancelled();
    }

    public void setCancelled(boolean cancel) {
        event.setCancelled(cancel);
    }

    public ExistenceForm existenceForm() {
        return existenceForm;
    }

    public Location location() {
        return clickedLocation;
    }

    public String relatedID() {
        return relatedID;
    }

    public ItemStack itemInHand() {
        return itemInHand;
    }

    public String itemID() {
        return itemID;
    }

    public EquipmentSlot hand() {
        return hand;
    }

    @Nullable
    public BlockFace clickedBlockFace() {
        return blockFace;
    }

    public Player player() {
        return player;
    }
}

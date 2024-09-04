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
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractItemManager implements ItemManager {

    public abstract void handlePlayerInteractAir(
            Player player,
            EquipmentSlot hand,
            ItemStack itemInHand
    );

    public abstract void handlePlayerInteractBlock(
            Player player,
            Block block,
            String blockID,
            BlockFace blockFace,
            EquipmentSlot hand,
            ItemStack itemInHand,
            Cancellable event
    );

    // it's not a good choice to use Entity as parameter because the entity might be fake
    public abstract void handlePlayerInteractFurniture(
            Player player,
            Location location,
            String furnitureID,
            EquipmentSlot hand,
            ItemStack itemInHand,
            Cancellable event
    );

    public abstract void handlePlayerBreak(
            Player player,
            Location location,
            ItemStack itemInHand,
            String brokenID,
            Cancellable event
    );

    public abstract void handlePhysicsBreak(
            Location location,
            String brokenID,
            Cancellable event
    );

    public abstract void handleEntityTrample(
            Entity entity,
            Location location,
            String brokenID,
            Cancellable event
    );

    public abstract void handleEntityExplode(
            Entity entity,
            Location location,
            String brokenID,
            Cancellable event
    );

    public abstract void handleBlockExplode(
            Block block,
            Location location,
            String brokenID,
            Cancellable event
    );

    public abstract void handlePlayerPlace(
            Player player,
            Location location,
            String placedID,
            EquipmentSlot hand,
            ItemStack itemInHand,
            Cancellable event
    );
}

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

package net.momirealms.customcrops.bukkit.integration.custom.crucible_r1;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.adapters.BukkitEntity;
import io.lumine.mythiccrucible.events.MythicFurniturePlaceEvent;
import io.lumine.mythiccrucible.events.MythicFurnitureRemoveEvent;
import io.lumine.mythiccrucible.items.blocks.CustomBlockItemContext;
import net.momirealms.customcrops.api.core.AbstractCustomEventListener;
import net.momirealms.customcrops.api.core.AbstractItemManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CrucibleListener extends AbstractCustomEventListener {

    private final CrucibleProvider provider;

    private static final Set<Material> IGNORED = new HashSet<>(
            List.of(
                    Material.NOTE_BLOCK,
                    Material.RED_MUSHROOM_BLOCK,
                    Material.BROWN_MUSHROOM_BLOCK,
                    Material.MUSHROOM_STEM,
                    Material.TRIPWIRE,
                    Material.CHORUS_PLANT
            )
    );

    @Override
    protected Set<Material> ignoredMaterials() {
        return IGNORED;
    }

    public CrucibleListener(AbstractItemManager itemManager, CrucibleProvider provider) {
        super(itemManager);
        this.provider = provider;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInteractFurniture(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Interaction interaction) {
            provider.getFurnitureManager().getFurniture(interaction).ifPresent(furniture -> {
                AbstractLocation location = furniture.getLocation();
                itemManager.handlePlayerInteractFurniture(
                        event.getPlayer(),
                        new Location(event.getPlayer().getWorld(), location.getX(), location.getY(), location.getZ()),
                        furniture.getFurnitureData().getItem().getInternalName(),
                        event.getHand(),
                        event.getPlayer().getInventory().getItem(event.getHand()),
                        event
                );
            });
        } else {
            provider.getFurnitureManager().getFurniture(new BukkitEntity(event.getRightClicked())).ifPresent(furniture -> {
                AbstractLocation location = furniture.getLocation();
                itemManager.handlePlayerInteractFurniture(
                        event.getPlayer(),
                        new Location(event.getPlayer().getWorld(), location.getX(), location.getY(), location.getZ()),
                        furniture.getFurnitureData().getItem().getInternalName(),
                        event.getHand(),
                        event.getPlayer().getInventory().getItem(event.getHand()),
                        event
                );
            });
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInteractCustomBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Block clicked = event.getClickedBlock();
        provider.getBlockManager().getBlockFromBlock(clicked).ifPresent(block -> {
            itemManager.handlePlayerInteractBlock(
                    event.getPlayer(),
                    clicked,
                    block.getCrucibleItem().getInternalName(),
                    event.getBlockFace(),
                    event.getHand(),
                    event.getItem(),
                    event
            );
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakFurniture(MythicFurnitureRemoveEvent event) {
        if (event.getBreaker() instanceof Player player) {
            AbstractLocation location = event.getFurniture().getLocation();
            itemManager.handlePlayerBreak(
                    player,
                    new Location(player.getWorld(), location.getX(), location.getY(), location.getZ()),
                    player.getInventory().getItemInMainHand(),
                    event.getFurniture().getFurnitureData().getItem().getInternalName(),
                    event
            );
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBreakCustomBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Optional<CustomBlockItemContext> maybeBlock = provider.getBlockManager().getBlockFromBlock(event.getBlock());
        maybeBlock.ifPresent(block -> {
            itemManager.handlePlayerBreak(
                    player,
                    event.getBlock().getLocation(),
                    event.getPlayer().getInventory().getItemInMainHand(),
                    block.getCrucibleItem().getInternalName(),
                    event
            );
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceFurniture(MythicFurniturePlaceEvent event) {
        itemManager.handlePlayerPlace(
                event.getPlayer(),
                event.getBlock().getLocation(),
                event.getFurnitureItemContext().getItem().getInternalName(),
                EquipmentSlot.HAND,
                event.getPlayer().getInventory().getItemInMainHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlaceCustomBlock(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        Optional<CustomBlockItemContext> maybeBlock = provider.getBlockManager().getBlockFromItem(itemStack);
        maybeBlock.ifPresent(customBlockItemContext -> itemManager.handlePlayerPlace(
                event.getPlayer(),
                event.getBlock().getLocation(),
                customBlockItemContext.getCrucibleItem().getInternalName(),
                event.getHand(),
                event.getItemInHand(),
                event
        ));
    }
}

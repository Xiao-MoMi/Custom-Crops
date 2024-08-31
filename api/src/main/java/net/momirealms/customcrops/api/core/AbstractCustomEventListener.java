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

import net.momirealms.customcrops.common.helper.VersionHelper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.List;

public abstract class AbstractCustomEventListener implements Listener {

    private final HashSet<EntityType> entities = new HashSet<>();
    private final HashSet<Material> blocks = new HashSet<>();

    protected final AbstractItemManager itemManager;

    public AbstractCustomEventListener(AbstractItemManager itemManager) {
        this.itemManager = itemManager;
        this.entities.addAll(List.of(EntityType.ITEM_FRAME, EntityType.ARMOR_STAND));
        if (VersionHelper.isVersionNewerThan1_19_4()) {
            this.entities.addAll(List.of(EntityType.ITEM_DISPLAY, EntityType.INTERACTION));
        }
        this.blocks.addAll(List.of(
                Material.NOTE_BLOCK,
                Material.MUSHROOM_STEM, Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK,
                Material.TRIPWIRE,
                Material.CHORUS_PLANT, Material.CHORUS_FLOWER,
                Material.ACACIA_LEAVES, Material.BIRCH_LEAVES, Material.JUNGLE_LEAVES, Material.DARK_OAK_LEAVES, Material.AZALEA_LEAVES, Material.FLOWERING_AZALEA_LEAVES, Material.OAK_LEAVES, Material.SPRUCE_LEAVES,
                Material.CAVE_VINES, Material.TWISTING_VINES, Material.WEEPING_VINES,
                Material.KELP,
                Material.CACTUS
        ));
        if (VersionHelper.isVersionNewerThan1_19()) {
            this.blocks.add(Material.MANGROVE_LEAVES);
        }
        if (VersionHelper.isVersionNewerThan1_20()) {
            this.blocks.add(Material.CHERRY_LEAVES);
        }
    }

    @EventHandler
    public void onInteractAir(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR)
            return;
        this.itemManager.handlePlayerInteractAir(
                event.getPlayer(),
                event.getHand(),
                event.getItem()
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Block block = event.getClickedBlock();
        assert block != null;
        if (blocks.contains(block.getType())) {
            return;
        }
        this.itemManager.handlePlayerInteractBlock(
                event.getPlayer(),
                block,
                block.getType().name(), event.getBlockFace(),
                event.getHand(),
                event.getItem(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        EntityType type = event.getRightClicked().getType();
        if (entities.contains(type)) {
            return;
        }
        this.itemManager.handlePlayerInteractFurniture(
                event.getPlayer(),
                event.getRightClicked().getLocation(), type.name(),
                event.getHand(), event.getPlayer().getInventory().getItem(event.getHand()),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceBlock(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (blocks.contains(block.getType())) {
            return;
        }
        this.itemManager.handlePlayerPlace(
                event.getPlayer(),
                block.getLocation(),
                block.getType().name(),
                event.getHand(),
                event.getItemInHand(),
                event
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (blocks.contains(block.getType())) {
            return;
        }
        this.itemManager.handlePlayerBreak(
                event.getPlayer(),
                block.getLocation(), event.getPlayer().getInventory().getItemInMainHand(), block.getType().name(),
                event
        );
    }
}

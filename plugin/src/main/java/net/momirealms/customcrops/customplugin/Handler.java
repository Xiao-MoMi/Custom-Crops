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

package net.momirealms.customcrops.customplugin;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.pot.PotManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public abstract class Handler extends Function implements Listener {

    protected PlatformManager platformManager;

    public Handler(PlatformManager platformManager) {
        this.platformManager = platformManager;
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(this, CustomCrops.getInstance());
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        platformManager.onInteractBlock(event);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        platformManager.onBreakVanillaBlock(event);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        platformManager.onPlaceVanillaBlock(event);
    }

    @EventHandler
    public void onMoistureChange(MoistureChangeEvent event) {
        if (event.isCancelled()) return;
        if (ConfigManager.disableMoistureMechanic) event.setCancelled(true);
    }

    @EventHandler
    public void onTrampling(EntityChangeBlockEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND && event.getTo() == Material.DIRT) {
            if (ConfigManager.preventTrampling) {
                event.setCancelled(true);
            } else if (PotManager.enableFarmLand) {
                platformManager.onBreakPot(event.getEntity(), "FARMLAND", block.getLocation(), event);
            }
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (!PotManager.enableFarmLand || event.isCancelled()) return;
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND) {
            if (CustomCrops.getInstance().getPlatformInterface().detectAnyThing(event.getBlock().getLocation().clone().add(0,1,0))) {
                event.setCancelled(true);
            } else {
                platformManager.onBreakPot(null, "FARMLAND", block.getLocation(), event);
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!PotManager.enableVanillaBlock || event.isCancelled()) return;
        handlePiston(event.getBlocks(), event);
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!PotManager.enableVanillaBlock || event.isCancelled()) return;
        handlePiston(event.getBlocks(), event);
    }

    public void handlePiston(List<Block> blocks, Cancellable event) {
        PotManager potManager = CustomCrops.getInstance().getPotManager();
        for (Block block : blocks) {
            String id = block.getType().name();
            if (potManager.containsPotBlock(id)) {
                platformManager.onBreakPot(null, id, block.getLocation(), event);
            }
        }
    }
}
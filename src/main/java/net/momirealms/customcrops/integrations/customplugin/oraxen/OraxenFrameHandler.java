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

package net.momirealms.customcrops.integrations.customplugin.oraxen;

import io.th0rgal.oraxen.events.OraxenFurnitureBreakEvent;
import io.th0rgal.oraxen.events.OraxenFurnitureInteractEvent;
import io.th0rgal.oraxen.events.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.events.OraxenNoteBlockInteractEvent;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.config.BasicItemConfig;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.SprinklerConfig;
import net.momirealms.customcrops.integrations.AntiGrief;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.managers.CustomWorld;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.utils.FurnitureUtil;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class OraxenFrameHandler extends OraxenHandler {

    public OraxenFrameHandler(CropManager cropManager) {
        super(cropManager);
    }

    @Override
    public void onBreakNoteBlock(OraxenNoteBlockBreakEvent event) {
        if (event.isCancelled()) return;

        String id = event.getNoteBlockMechanic().getItemID();
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        if (id.equals(BasicItemConfig.dryPot)
                || id.equals(BasicItemConfig.wetPot)) {

            if (!AntiGrief.testBreak(player, location)) {
                event.setCancelled(true);
                return;
            }

            super.onBreakPot(location);

            Location seedLocation = location.clone().add(0.5,1,0.5);
            ItemFrame itemFrame = FurnitureUtil.getItemFrame(seedLocation);
            if (itemFrame == null) return;
            String furnitureID = itemFrame.getPersistentDataContainer().get(OraxenHook.FURNITURE, PersistentDataType.STRING);
            if (furnitureID == null) return;
            if (furnitureID.contains("_stage_")) {
                itemFrame.remove();
                if (furnitureID.equals(BasicItemConfig.deadCrop)) return;
                super.onBreakRipeCrop(seedLocation, furnitureID, player, false, false);
            }
        }
    }

    @Override
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        if (event.isCancelled()) return;
        String id = event.getFurnitureMechanic().getItemID();
        if (id == null) return;
        //TODO check if needs anti grief
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            super.onBreakSprinkler(event.getBlock().getLocation());
            return;
        }

        if (id.contains("_stage_")) {
            if (id.equals(BasicItemConfig.deadCrop)) return;
            if (hasNextStage(id)) return;
            super.onBreakRipeCrop(event.getBlock().getLocation(), id, event.getPlayer(), false, false);
        }
    }

    @Override
    public void onInteractNoteBlock(OraxenNoteBlockInteractEvent event) {
        if (event.isCancelled()) return;
        String blockID = event.getNoteBlockMechanic().getItemID();
        if (blockID.equals(BasicItemConfig.dryPot) || blockID.equals(BasicItemConfig.wetPot)) {
            Location potLoc = event.getBlock().getLocation();
            super.tryMisc(event.getPlayer(), event.getItemInHand(), potLoc);
        }
    }

    @Override
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        if (event.isCancelled()) return;
        String id = event.getFurnitureMechanic().getItemID();
        if (id == null) return;
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            super.onInteractSprinkler(event.getBlock().getLocation(), event.getPlayer(), event.getPlayer().getActiveItem(), sprinkler);
            return;
        }

        if (id.contains("_stage_")) {
            if (!id.equals(BasicItemConfig.deadCrop)) {

                if (!hasNextStage(id) && MainConfig.canRightClickHarvest) {
                    event.getItemFrame().remove();
                    this.onInteractRipeCrop(event.getBlock().getLocation(), id, event.getPlayer());
                }

                else {
                    Location potLoc = event.getBlock().getLocation().clone().subtract(0,1,0);
                    super.tryMisc(event.getPlayer(), event.getPlayer().getItemInUse(), potLoc);
                }
            }
        }
    }

    private void onInteractRipeCrop(Location location, String id, Player player) {

        Crop crop = getCropFromID(id);
        if (crop == null) return;
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;

        Fertilizer fertilizer = customWorld.getFertilizer(location.clone().subtract(0,1,0));
        cropManager.proceedHarvest(crop, player, location, fertilizer);

        if (crop.getReturnStage() == null) {
            customWorld.removeCrop(location);
            return;
        }
        customWorld.addCrop(location, crop.getKey());
        cropManager.getCustomInterface().placeFurniture(location, crop.getReturnStage());
    }
}

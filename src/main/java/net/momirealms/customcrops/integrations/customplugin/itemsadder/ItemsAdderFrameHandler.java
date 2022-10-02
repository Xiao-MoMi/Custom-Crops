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

package net.momirealms.customcrops.integrations.customplugin.itemsadder;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
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

public class ItemsAdderFrameHandler extends ItemsAdderHandler {

    public ItemsAdderFrameHandler(CropManager cropManager) {
        super(cropManager);
    }

    //maybe crop or sprinkler
    public void onInteractFurniture(FurnitureInteractEvent event) {

        if (event.isCancelled()) return;
        String namespacedID = event.getNamespacedID();
        if (namespacedID == null) return;
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(namespacedID);
        if (sprinkler != null) {
            super.onInteractSprinkler(event.getBukkitEntity().getLocation(), event.getPlayer(), event.getPlayer().getActiveItem(), sprinkler);
            return;
        }

        if (namespacedID.contains("_stage_")) {
            if (!namespacedID.equals(BasicItemConfig.deadCrop)) {

                if (!hasNextStage(namespacedID) && MainConfig.canRightClickHarvest) {
                    CustomFurniture.remove(event.getBukkitEntity(), false);
                    this.onInteractRipeCrop(event.getBukkitEntity().getLocation(), namespacedID, event.getPlayer());
                }

                else {
                    Location potLoc = event.getBukkitEntity().getLocation().clone().subtract(0,1,0);
                    super.tryMisc(event.getPlayer(), event.getPlayer().getItemInUse(), potLoc);
                }
            }
        }
    }

    public void onBreakFurniture(FurnitureBreakEvent event) {
        if (event.isCancelled()) return;
        String namespacedId = event.getNamespacedID();
        if (namespacedId == null) return;

        Location location = event.getBukkitEntity().getLocation();
        Player player = event.getPlayer();
        //No need for antiGrief checks
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(namespacedId);
        if (sprinkler != null) {
            super.onBreakSprinkler(location);
            return;
        }

        if (namespacedId.contains("_stage_")) {
            if (namespacedId.equals(BasicItemConfig.deadCrop)) return;
            if (hasNextStage(namespacedId)) {
                super.onBreakUnripeCrop(location);
                return;
            }
            super.onBreakRipeCrop(location, namespacedId, player, false, true);
        }
    }

    //This can only be pot
    public void onInteractBlock(CustomBlockInteractEvent event) {
        if (event.isCancelled()) return;
        String blockID = event.getNamespacedID();
        if (blockID.equals(BasicItemConfig.dryPot) || blockID.equals(BasicItemConfig.wetPot)) {
            Location potLoc = event.getBlockClicked().getLocation();
            super.tryMisc(event.getPlayer(), event.getItem(), potLoc);
        }
    }

    @Override
    public void onBreakBlock(CustomBlockBreakEvent event) {

        if (event.isCancelled()) return;
        String namespacedId = event.getNamespacedID();
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        //fix buggy chorus duplication
        chorusFix(event.getBlock());

        if (namespacedId.equals(BasicItemConfig.dryPot)
                || namespacedId.equals(BasicItemConfig.wetPot)) {

            if (!AntiGrief.testBreak(player, location)) {
                event.setCancelled(true);
                return;
            }

            super.onBreakPot(location);

            //Check if there's crop above
            Location seedLocation = location.clone().add(0.5,1,0.5);

            ItemFrame itemFrame = FurnitureUtil.getItemFrame(seedLocation);
            if (itemFrame == null) return;
            CustomFurniture customFurniture = CustomFurniture.byAlreadySpawned(itemFrame);
            if (customFurniture == null) return;
            String seedID = customFurniture.getNamespacedID();
            if (seedID.contains("_stage_")) {
                CustomFurniture.remove(itemFrame, false);
                if (seedID.equals(BasicItemConfig.deadCrop)) return;
                super.onBreakRipeCrop(seedLocation, seedID, player, false, true);
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
        CustomFurniture.spawn(crop.getReturnStage(), location.getBlock());
    }
}

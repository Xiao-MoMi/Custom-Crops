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

package net.momirealms.customcrops.api.object.action;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.basic.MessageManager;
import net.momirealms.customcrops.api.object.crop.CropConfig;
import net.momirealms.customcrops.api.object.crop.GrowingCrop;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.api.util.AdventureUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ReplantImpl implements Action {

    private final int point;
    private final String crop;
    private final String model;

    public ReplantImpl(int point, String model, String crop) {
        this.point = point;
        this.crop = crop;
        this.model = model;
    }

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation crop_loc, ItemMode itemMode) {
        if (player == null || crop_loc == null) return;
        CropConfig cropConfig = CustomCrops.getInstance().getCropManager().getCropConfigByID(crop);
        if (cropConfig != null) {
            ItemMode newCMode = cropConfig.getCropMode();
            CustomCrops.getInstance().getScheduler().runTask(() -> {
                Location location = crop_loc.getBukkitLocation();
                if (location == null) return;
                if (ConfigManager.enableLimitation && CustomCrops.getInstance().getWorldDataManager().getChunkCropAmount(crop_loc) >= ConfigManager.maxCropPerChunk) {
                    AdventureUtils.playerMessage(player, MessageManager.prefix + MessageManager.reachChunkLimit);
                    return;
                }

                CropPlantEvent cropPlantEvent = new CropPlantEvent(player, player.getInventory().getItemInMainHand(), location, crop, point, model);
                Bukkit.getPluginManager().callEvent(cropPlantEvent);
                if (cropPlantEvent.isCancelled()) {
                    return;
                }

                if (!CustomCrops.getInstance().getPlatformInterface().detectAnyThing(location)) {
                    CustomCrops.getInstance().getPlatformInterface().placeCustomItem(location, model, newCMode);
                    CustomCrops.getInstance().getWorldDataManager().addCropData(crop_loc, new GrowingCrop(crop, point), true);
                }
            });
        }
    }
}
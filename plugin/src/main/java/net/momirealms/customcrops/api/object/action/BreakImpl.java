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
import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.crop.CropConfig;
import net.momirealms.customcrops.api.object.crop.StageConfig;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BreakImpl implements Action {

    private final boolean triggerAction;
    private final String stageID;

    public BreakImpl(boolean triggerAction, @Nullable String stageID) {
        this.triggerAction = triggerAction;
        this.stageID = stageID;
    }

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation cropLoc, ItemMode itemMode) {
        if (cropLoc == null || stageID == null) return;
        CropConfig cropConfig = CustomCrops.getInstance().getCropManager().getCropConfigByStage(stageID);
        Location bLoc = cropLoc.getBukkitLocation();
        if (bLoc == null || cropConfig == null) return;
        if (player != null) {
            CropBreakEvent cropBreakEvent = new CropBreakEvent(player, cropConfig.getKey(), stageID, bLoc);
            Bukkit.getPluginManager().callEvent(cropBreakEvent);
            if (cropBreakEvent.isCancelled()) {
                return;
            }
            CustomCrops.getInstance().getPlatformInterface().removeAnyThingAt(bLoc);
            CustomCrops.getInstance().getWorldDataManager().removeCropData(cropLoc);
            doTriggerActions(player, cropLoc, itemMode);
        } else {
            CompletableFuture<Chunk> asyncGetChunk = bLoc.getWorld().getChunkAtAsync(bLoc.getBlockX() >> 4, bLoc.getBlockZ() >> 4);
            if (itemMode == ItemMode.ITEM_FRAME || itemMode == ItemMode.ITEM_DISPLAY) {
                CompletableFuture<Boolean> loadEntities = asyncGetChunk.thenApply((chunk) -> {
                    chunk.getEntities();
                    return chunk.isEntitiesLoaded();
                });
                loadEntities.whenComplete((result, throwable) ->
                        CustomCrops.getInstance().getScheduler().runTask(() -> {
                            CustomCrops.getInstance().getWorldDataManager().removeCropData(cropLoc);
                            if (CustomCrops.getInstance().getPlatformInterface().removeCustomItem(bLoc, itemMode)) {
                                doTriggerActions(null, cropLoc, itemMode);
                            }
                        }));
            } else {
                asyncGetChunk.whenComplete((result, throwable) ->
                        CustomCrops.getInstance().getScheduler().runTask(() -> {
                            CustomCrops.getInstance().getWorldDataManager().removeCropData(cropLoc);
                            if (CustomCrops.getInstance().getPlatformInterface().removeCustomItem(bLoc, itemMode)) {
                                doTriggerActions(null, cropLoc, itemMode);
                            }
                        }));
            }
        }
    }

    private void doTriggerActions(@Nullable Player player, @NotNull SimpleLocation crop_loc, ItemMode itemMode) {
        if (triggerAction) {
            StageConfig stageConfig = CustomCrops.getInstance().getCropManager().getStageConfig(stageID);
            if (stageConfig != null) {
                Action[] actions = stageConfig.getBreakActions();
                if (actions != null) {
                    for (Action action : actions) {
                        action.doOn(player, crop_loc, itemMode);
                    }
                }
            }
        }
    }
}

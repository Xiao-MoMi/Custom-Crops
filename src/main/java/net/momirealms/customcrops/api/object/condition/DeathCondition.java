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

package net.momirealms.customcrops.api.object.condition;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.ItemType;
import net.momirealms.customcrops.api.object.OfflineReplaceTask;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DeathCondition {

    private final String dead_model;
    private final Condition[] conditions;

    public DeathCondition(@Nullable String dead_model, @NotNull Condition[] conditions) {
        this.dead_model = dead_model;
        this.conditions = conditions;
    }

    public boolean checkIfDead(SimpleLocation simpleLocation) {
        for (Condition condition : conditions) {
            if (condition.isMet(simpleLocation)) {
                return true;
            }
        }
        return false;
    }

    public void applyDeadModel(SimpleLocation simpleLocation, ItemMode itemMode) {
        Location location = simpleLocation.getBukkitLocation();
        if (location == null) return;

        if (location.getWorld().isChunkLoaded(simpleLocation.getX() >> 4, simpleLocation.getZ() >> 4)) {
            replaceDeadModels(location, itemMode);
            return;
        }

        if (ConfigManager.updateDuringLoading) {
            CustomCrops.getInstance().getWorldDataManager().addOfflineTask(simpleLocation, new OfflineReplaceTask(dead_model, ItemType.CROP, itemMode));
            return;
        }

        CompletableFuture<Chunk> asyncGetChunk = location.getWorld().getChunkAtAsync(location.getBlockX() >> 4, location.getBlockZ() >> 4);
        if (itemMode == ItemMode.ITEM_FRAME || itemMode == ItemMode.ITEM_DISPLAY) {
            CompletableFuture<Boolean> loadEntities = asyncGetChunk.thenApply((chunk) -> {
                chunk.getEntities();
                return chunk.isEntitiesLoaded();
            });
            loadEntities.whenComplete((result, throwable) -> replaceDeadModels(location, itemMode));
        } else {
            asyncGetChunk.whenComplete((result, throwable) -> replaceDeadModels(location, itemMode));
        }
    }

    private void replaceDeadModels(Location location, ItemMode itemMode) {
        CustomCrops.getInstance().getScheduler().runTask(() -> {
            if (CustomCrops.getInstance().getPlatformInterface().removeCustomItem(location, itemMode)) {
                if (dead_model != null) {
                    CustomCrops.getInstance().getPlatformInterface().placeCustomItem(location, dead_model, itemMode);
                }
            }
        });
    }
}

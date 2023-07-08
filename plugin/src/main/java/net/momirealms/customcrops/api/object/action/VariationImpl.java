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
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.crop.VariationCrop;
import net.momirealms.customcrops.api.object.fertilizer.Variation;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public record VariationImpl(VariationCrop[] variationCrops) implements Action {

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation cropLoc, ItemMode itemMode) {
        if (cropLoc == null) return;
        double bonus = 0;
        Pot pot = CustomCrops.getInstance().getWorldDataManager().getPotData(cropLoc.add(0,-1,0));
        if (pot != null && CustomCrops.getInstance().getFertilizerManager().getConfigByFertilizer(pot.getFertilizer()) instanceof Variation variation) {
            bonus = variation.getChance();
        }
        for (VariationCrop variationCrop : variationCrops) {
            if (Math.random() < variationCrop.getChance() + bonus) {
                doVariation(cropLoc, itemMode, variationCrop);
                break;
            }
        }
    }

    public boolean doOn(@Nullable SimpleLocation crop_loc, ItemMode itemMode) {
        if (crop_loc == null) return false;
        double bonus = 0;
        Pot pot = CustomCrops.getInstance().getWorldDataManager().getPotData(crop_loc.add(0,-1,0));
        if (pot != null && CustomCrops.getInstance().getFertilizerManager().getConfigByFertilizer(pot.getFertilizer()) instanceof Variation variation) {
            bonus = variation.getChance();
        }
        for (VariationCrop variationCrop : variationCrops) {
            if (Math.random() < variationCrop.getChance() + bonus) {
                doVariation(crop_loc, itemMode, variationCrop);
                return true;
            }
        }
        return false;
    }

    private void doVariation(@NotNull SimpleLocation crop_loc, ItemMode itemMode, VariationCrop variationCrop) {
        Location location = crop_loc.getBukkitLocation();
        if (location == null) return;
        CompletableFuture<Chunk> asyncGetChunk = location.getWorld().getChunkAtAsync(location.getBlockX() >> 4, location.getBlockZ() >> 4);
        if (itemMode == ItemMode.ITEM_FRAME || itemMode == ItemMode.ITEM_DISPLAY) {
            CompletableFuture<Boolean> loadEntities = asyncGetChunk.thenApply((chunk) -> {
                chunk.getEntities();
                return chunk.isEntitiesLoaded();
            });
            loadEntities.whenComplete((result, throwable) ->
                    CustomCrops.getInstance().getScheduler().callSyncMethod(() -> {
                        if (CustomCrops.getInstance().getPlatformInterface().removeCustomItem(location, itemMode)) {
                            CustomCrops.getInstance().getPlatformInterface().placeCustomItem(location, variationCrop.getId(), variationCrop.getCropMode());
                        }
                        return null;
                    }));
        } else {
            asyncGetChunk.whenComplete((result, throwable) ->
                    CustomCrops.getInstance().getScheduler().callSyncMethod(() -> {
                        if (CustomCrops.getInstance().getPlatformInterface().removeCustomItem(location, itemMode)) {
                            CustomCrops.getInstance().getPlatformInterface().placeCustomItem(location, variationCrop.getId(), variationCrop.getCropMode());
                        }
                        return null;
                    }));
        }
    }
}
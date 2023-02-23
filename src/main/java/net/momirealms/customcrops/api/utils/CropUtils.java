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

package net.momirealms.customcrops.api.utils;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.config.BasicItemConfig;
import net.momirealms.customcrops.config.CropConfig;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.managers.CustomWorld;
import net.momirealms.customcrops.objects.GrowingCrop;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.utils.FurnitureUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class CropUtils {

    /**
     * get a crop config
     * @param crop crop
     * @return crop config
     */
    @Nullable
    public static Crop getCropConfig(String crop) {
        return CropConfig.CROPS.get(crop);
    }

    /**
     * whether planting succeeds
     * @param location location
     * @param crop crop
     * @return success or not
     */
    public static boolean plantCrop(Location location, String crop) {
        return CustomCrops.plugin.getCropManager().getHandler().plantSeed(location, crop, null, null);
    }

    /**
     * Oraxen & ItemsAdder handle item frame hitbox in different ways
     * If you want to remove a crop at a certain location, use location.getBlock() method as the param
     * @param block block
     * @return success or not
     */
    public static boolean removeCrop(Block block) {
        Location location = block.getLocation();
        CustomCrops.plugin.getCropManager().getHandler().onBreakUnripeCrop(location);
        if (MainConfig.cropMode) {
            CustomCrops.plugin.getCropManager().getCustomInterface().removeBlock(location);
        }
        else {
            if (MainConfig.OraxenHook) {CustomCrops.plugin.getCropManager().getCustomInterface().removeFurniture(FurnitureUtil.getItemFrame(location.clone().add(0.5,0.03125,0.5)));}
            else {CustomCrops.plugin.getCropManager().getCustomInterface().removeFurniture(FurnitureUtil.getItemFrame(location.clone().add(0.5,0.5,0.5)));}
        }
        return true;
    }

    /**
     * get the growing crop
     * @param location crop location
     * @return growing crop
     */
    @Nullable
    public static GrowingCrop getGrowingCrop(Location location) {
        CustomWorld customWorld = CustomCrops.plugin.getCropManager().getCustomWorld(location.getWorld());
        if (customWorld != null) {
            return customWorld.getCropCache(location);
        }
        return null;
    }

    /**
     * [Only works in tripwire mode]
     * @param block block
     * @return the block is crop or not
     */
    public static boolean isCropBlock(Block block) {
        String block_id = CustomCrops.plugin.getCropManager().getCustomInterface().getBlockID(block.getLocation());
        if (block_id == null) return false;
        return block_id.contains("_stage_") || block_id.equals(BasicItemConfig.deadCrop);
    }

    /**
     * [Only works in item_frame mode]
     * @param entity entity
     * @return the entity is crop or not
     */
    public static boolean isCropEntity(Entity entity) {
        String entity_id = CustomCrops.plugin.getCropManager().getCustomInterface().getEntityID(entity);
        if (entity_id == null) return false;
        return entity_id.contains("_stage_") || entity_id.equals(BasicItemConfig.deadCrop);
    }

    /**
     * get the cropManager
     * @return cropManager
     */
    public static CropManager getCropManager() {
        return CustomCrops.plugin.getCropManager();
    }

    /**
     * get the fertilizer
     * @param location pot location
     * @return fertilizer
     */
    @Nullable
    public static Fertilizer getFertilizer(Location location) {
        CustomWorld customWorld = CustomCrops.plugin.getCropManager().getCustomWorld(location.getWorld());
        if (customWorld != null) {
            return customWorld.getFertilizerCache(location);
        }
        return null;
    }

    /**
     * get the wet state
     * @param location pot location
     * @return wet or not
     */
    public static boolean isPotWet(Location location) {
        String block_id = CustomCrops.plugin.getCropManager().getCustomInterface().getBlockID(location);
        if (block_id == null) return false;
        return block_id.equals(BasicItemConfig.wetPot);
    }

    /**
     * Make a pot dry
     * @param location pot location
     */
    public static void makePotDry(Location location) {
        CustomCrops.plugin.getCropManager().makePotDry(location);
    }

    /**
     * If the block is a pot
     * @param location pot location
     * @return the block is a pot or not
     */
    public static boolean isPotBlock(Location location) {
        String block_id = CustomCrops.plugin.getCropManager().getCustomInterface().getBlockID(location);
        if (block_id == null) return false;
        return block_id.equals(BasicItemConfig.wetPot) || block_id.equals(BasicItemConfig.dryPot);
    }

    /**
     * get the sprinkler data
     * @param location sprinkler location
     * @return sprinkler data
     */
    @Nullable
    public static Sprinkler getSprinkler(Location location) {
        CustomWorld customWorld = CustomCrops.plugin.getCropManager().getCustomWorld(location.getWorld());
        if (customWorld != null) {
            return customWorld.getSprinklerCache(location);
        }
        return null;
    }
}

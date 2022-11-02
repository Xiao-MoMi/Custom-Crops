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
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.config.BasicItemConfig;
import net.momirealms.customcrops.config.CropConfig;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.integrations.customplugin.CustomInterface;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.managers.CropModeInterface;
import net.momirealms.customcrops.objects.GiganticCrop;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.objects.fertilizer.Gigantic;
import net.momirealms.customcrops.objects.fertilizer.SpeedGrow;
import net.momirealms.customcrops.utils.FurnitureUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;

public class ItemsAdderFrameCropImpl implements CropModeInterface {

    private final CropManager cropManager;
    private final CustomInterface customInterface;

    public ItemsAdderFrameCropImpl(CropManager cropManager) {
        this.cropManager = cropManager;
        this.customInterface = cropManager.getCustomInterface();
    }

    @Override
    public boolean growJudge(Location location) {

        Chunk chunk = location.getChunk();

        if (chunk.isEntitiesLoaded()) {
            Location cropLoc = location.clone().add(0.5,0.5,0.5);
            ItemFrame itemFrame = FurnitureUtil.getItemFrame(cropLoc);
            if (itemFrame == null) return true;
            String id = customInterface.getItemID(itemFrame.getItem());
            if (id == null) return true;
            if (id.equals(BasicItemConfig.deadCrop)) return true;

            String cropId = StringUtils.split(id, ":")[1];
            String[] cropNameList = StringUtils.split(cropId, "_");
            String cropKey = cropNameList[0];

            Crop crop = CropConfig.CROPS.get(cropKey);
            if (crop == null) return true;
            if (MainConfig.needSkyLight && location.getBlock().getLightFromSky() < MainConfig.skyLightLevel) {
                itemFrame.setItem(customInterface.getItemStack(BasicItemConfig.deadCrop), false);
                return true;
            }
            if (cropManager.isWrongSeason(location, crop.getSeasons())) {
                itemFrame.setItem(customInterface.getItemStack(BasicItemConfig.deadCrop), false);
                return true;
            }
            Location potLoc = location.clone().subtract(0,1,0);
            String potID = customInterface.getBlockID(potLoc);
            if (potID == null) return true;
            Fertilizer fertilizer = cropManager.getFertilizer(potLoc);
            boolean certainGrow = potID.equals(BasicItemConfig.wetPot);
            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
            String temp = StringUtils.chop(id);
            if (customInterface.doesExist(temp + nextStage)) {
                if (MainConfig.enableCrow && cropManager.crowJudge(location, itemFrame)) return true;
                if (fertilizer instanceof SpeedGrow speedGrow && Math.random() < speedGrow.getChance()) {
                    if (customInterface.doesExist(temp + (nextStage+1))) {
                        addStage(itemFrame, temp + (nextStage + 1), crop.canRotate());
                    }
                }
                else if (certainGrow || Math.random() < MainConfig.dryGrowChance) {
                    addStage(itemFrame, temp + nextStage, crop.canRotate());
                }
            }
            else {
                if (MainConfig.enableCrow && cropManager.crowJudge(location, itemFrame)) return true;
                GiganticCrop giganticCrop = crop.getGiganticCrop();
                if (giganticCrop != null) {
                    double chance = giganticCrop.getChance();
                    if (fertilizer instanceof Gigantic gigantic) {
                        chance += gigantic.getChance();
                    }
                    if (Math.random() < chance) {
                        Bukkit.getScheduler().runTask(CustomCrops.plugin, () -> {
                            customInterface.removeFurniture(itemFrame);
                            if (giganticCrop.isBlock()) {
                                customInterface.placeWire(location, giganticCrop.getBlockID());
                            }
                            else {
                                customInterface.placeFurniture(location, giganticCrop.getBlockID());
                            }
                        });
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void addStage(ItemFrame itemFrame, String stage, boolean rotate) {
        CustomFurniture.remove(itemFrame, false);
        CustomFurniture customFurniture = CustomFurniture.spawn(stage, itemFrame.getLocation().getBlock());
        if (rotate && customFurniture.getArmorstand() instanceof ItemFrame frame) {
            frame.setRotation(FurnitureUtil.getRandomRotation());
        }
    }
}

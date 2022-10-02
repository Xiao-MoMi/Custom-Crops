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

package net.momirealms.customcrops.managers;

import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.config.BasicItemConfig;
import net.momirealms.customcrops.config.CropConfig;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.integrations.customplugin.CustomInterface;
import net.momirealms.customcrops.integrations.customplugin.oraxen.OraxenHook;
import net.momirealms.customcrops.objects.GiganticCrop;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.objects.fertilizer.RetainingSoil;
import net.momirealms.customcrops.objects.fertilizer.SpeedGrow;
import net.momirealms.customcrops.utils.FurnitureUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.persistence.PersistentDataType;

public class FrameCropImpl implements CropModeInterface {

    private final CropManager cropManager;
    private final CustomInterface customInterface;

    public FrameCropImpl(CropManager cropManager) {
        this.cropManager = cropManager;
        this.customInterface = cropManager.getCustomInterface();
    }

    @Override
    public void loadChunk(Location location) {
        Chunk chunk = location.getChunk();
        chunk.load();
    }

    @Override
    public boolean growJudge(Location location) {

        Chunk chunk = location.getChunk();

        if (chunk.isEntitiesLoaded()) {

            ItemFrame itemFrame = FurnitureUtil.getItemFrame(location);
            if (itemFrame == null) return true;
            String id = customInterface.getItemID(itemFrame.getItem());
            if (id == null) return true;
            if (id.equals(BasicItemConfig.deadCrop)) return true;

            String[] cropNameList = StringUtils.split(id,"_");
            String cropKey = cropNameList[0];
            if (cropKey.contains(":")) cropKey = StringUtils.split(cropKey, ":")[1];
            Crop crop = CropConfig.CROPS.get(cropKey);
            if (crop == null) return true;
            if (cropManager.isWrongSeason(location, crop.getSeasons())) {
                itemFrame.setItem(customInterface.getItemStack(BasicItemConfig.deadCrop));
                if (MainConfig.OraxenHook) itemFrame.getPersistentDataContainer().set(OraxenHook.FURNITURE, PersistentDataType.STRING, BasicItemConfig.deadCrop);
                return true;
            }

            Location potLoc = location.clone().subtract(0,1,0);
            String potID = customInterface.getBlockID(potLoc);
            if (potID == null) return true;

            Fertilizer fertilizer = cropManager.getFertilizer(potLoc);

            boolean certainGrow = false;
            if (potID.equals(BasicItemConfig.wetPot)) {
                if (!(fertilizer instanceof RetainingSoil retainingSoil && Math.random() < retainingSoil.getChance())) {
                    cropManager.potDryJudge(potLoc);
                }
                certainGrow = true;
            }

            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
            String temp = StringUtils.chop(id);
            if (customInterface.doesExist(temp + nextStage)) {
                if (fertilizer instanceof SpeedGrow speedGrow && Math.random() < speedGrow.getChance()) {
                    if (customInterface.doesExist(temp + (nextStage+1))) {
                        addStage(itemFrame, temp + (nextStage+1));
                    }
                }
                else if (certainGrow || Math.random() < MainConfig.dryGrowChance) {
                    addStage(itemFrame, temp + nextStage);
                }
            }
            else {
                GiganticCrop giganticCrop = crop.getGiganticCrop();
                if (giganticCrop != null && Math.random() < giganticCrop.getChance()) {
                    customInterface.removeFurniture(itemFrame);
                    if (giganticCrop.isBlock()) {
                        customInterface.placeWire(location, giganticCrop.getBlockID());
                    }
                    else {
                        customInterface.placeFurniture(location, giganticCrop.getBlockID());
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void addStage(ItemFrame itemFrame, String stage) {
        itemFrame.setItem(customInterface.getItemStack(stage));
        if (!MainConfig.OraxenHook) itemFrame.getPersistentDataContainer().set(OraxenHook.FURNITURE, PersistentDataType.STRING, stage);
    }
}

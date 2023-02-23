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

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.config.CropConfig;
import net.momirealms.customcrops.integrations.customplugin.CustomInterface;
import net.momirealms.customcrops.utils.FurnitureUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook implements CustomInterface {

    @Override
    public void removeBlock(Location location) {
        CustomBlock.remove(location);
    }

    @Override
    public void placeWire(Location location, String crop) {
        CustomBlock.place(crop, location);
    }

    @Override
    public void placeNoteBlock(Location location, String blockID) {
        CustomBlock.place(blockID, location);
    }

    @Override
    @Nullable
    public String getBlockID(Location location) {
        CustomBlock cb = CustomBlock.byAlreadyPlaced(location.getBlock());
        if (cb == null) return null;
        return cb.getNamespacedID();
    }

    @Override
    @Nullable
    public ItemStack getItemStack(String id) {
        if (id == null) return null;
        CustomStack cs = CustomStack.getInstance(id);
        if (cs == null) return null;
        return cs.getItemStack();
    }

    @Override
    @Nullable
    public String getItemID(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return null;
        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound nbtCompound = nbtItem.getCompound("itemsadder");
        if (nbtCompound == null) return null;
        return nbtCompound.getString("namespace") + ":" + nbtCompound.getString("id");
    }

    @Override
    @Nullable
    public ItemFrame placeFurniture(Location location, String id) {
        CustomFurniture customFurniture = CustomFurniture.spawn(id, location.getBlock());
        Entity entity = customFurniture.getArmorstand();
        if (entity instanceof ItemFrame itemFrame) {
            return itemFrame;
        }
        else {
            customFurniture.remove(false);
            return null;
        }
    }

    @Override
    public void removeFurniture(Entity entity) {
        CustomFurniture.remove(entity,false);
    }

    @Override
    public boolean doesExist(String itemID) {
        return CustomStack.getInstance(itemID) != null;
    }

    @Override
    public boolean hasNextStage(String id) {
        return doesExist(getNextStage(id));
    }

    @Override
    public String getNextStage(String id) {
        String stageStr = id.substring(id.indexOf("_stage_") + 7);
        int nextStage = Integer.parseInt(stageStr) + 1;
        return id.substring(0, id.length() - stageStr.length()) + nextStage;
    }

    @Override
    public @Nullable Crop getCropFromID(String id) {
        String cropNameWithoutNS = StringUtils.split(id, ":")[1];
        return CropConfig.CROPS.get(cropNameWithoutNS.substring(0, cropNameWithoutNS.indexOf("_stage_")));
    }

    @Override
    public Location getFrameCropLocation(Location seedLoc) {
        return seedLoc.clone().add(0.5,0.5,0.5);
    }

    @Override
    public void addFrameStage(ItemFrame itemFrame, String stage, boolean rotate) {
        CustomFurniture.remove(itemFrame, false);
        CustomFurniture customFurniture = CustomFurniture.spawn(stage, itemFrame.getLocation().getBlock());
        if (rotate && customFurniture.getArmorstand() instanceof ItemFrame frame) {
            frame.setRotation(FurnitureUtil.getRandomRotation());
        }
    }

    @Override
    public void addWireStage(Location seedLoc, String stage) {
        Bukkit.getScheduler().runTask(CustomCrops.plugin, () -> {
            removeBlock(seedLoc);
            placeWire(seedLoc, stage);
        });
    }

    @Override
    @Nullable
    public String getEntityID(Entity entity) {
        CustomFurniture customFurniture = CustomFurniture.byAlreadySpawned(entity);
        if (customFurniture == null) return null;
        return customFurniture.getNamespacedID();
    }
}

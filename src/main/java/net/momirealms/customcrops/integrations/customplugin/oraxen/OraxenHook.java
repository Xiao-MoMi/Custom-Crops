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

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanicFactory;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.config.CropConfig;
import net.momirealms.customcrops.integrations.customplugin.CustomInterface;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class OraxenHook implements CustomInterface {

    public static NamespacedKey FURNITURE = new NamespacedKey(OraxenPlugin.get(), "furniture");

    @Override
    public void removeBlock(Location location) {
        OraxenBlocks.remove(location, null);
    }

    @Override
    public void placeWire(Location location, String crop) {
        StringBlockMechanicFactory.setBlockModel(location.getBlock(), crop);
    }

    @Override
    public void placeNoteBlock(Location location, String id) {
        NoteBlockMechanicFactory.setBlockModel(location.getBlock(), id);
    }

    @Override
    @Nullable
    public String getBlockID(Location location) {
        Block block = location.getBlock();
        if (block.getType() == Material.TRIPWIRE) {
            StringBlockMechanic mechanic = OraxenBlocks.getStringMechanic(block);
            if (mechanic == null) return null;
            else return mechanic.getItemID();
        }
        else if (block.getType() == Material.NOTE_BLOCK) {
            NoteBlockMechanic mechanic = OraxenBlocks.getNoteBlockMechanic(block);
            if (mechanic == null) return null;
            else return mechanic.getItemID();
        }
        return null;
    }

    @Override
    @Nullable
    public ItemStack getItemStack(String id) {
        ItemBuilder ib = OraxenItems.getItemById(id);
        if (ib == null) return null;
        return ib.build();
    }

    @Override
    @Nullable
    public String getItemID(ItemStack itemStack) {
        return OraxenItems.getIdByItem(itemStack);
    }

    @Override
    @Nullable
    public ItemFrame placeFurniture(Location location, String id) {
        FurnitureMechanic mechanic = (FurnitureMechanic) FurnitureFactory.getInstance().getMechanic(id);
        return mechanic.place(Rotation.NONE, 0, BlockFace.UP, location);
    }

    @Override
    public void removeFurniture(Entity entity) {
        entity.remove();
    }

    @Override
    public boolean doesExist(String itemID) {
        return OraxenItems.getItemById(itemID) != null;
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
        return CropConfig.CROPS.get(id.substring(0, id.indexOf("_stage_")));
    }

    @Override
    public Location getFrameCropLocation(Location seedLoc) {
        return seedLoc.clone().add(0.5,0.03125,0.5);
    }

    @Override
    public void addFrameStage(ItemFrame itemFrame, String stage, boolean rotate) {
        itemFrame.setItem(getItemStack(stage), false);
        itemFrame.getPersistentDataContainer().set(OraxenHook.FURNITURE, PersistentDataType.STRING, stage);
    }

    @Override
    public void addWireStage(Location seedLoc, String stage) {
        Bukkit.getScheduler().runTask(CustomCrops.plugin, () -> placeWire(seedLoc, stage));
    }

    @Override
    @Nullable
    public String getEntityID(Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return pdc.get(FURNITURE, PersistentDataType.STRING);
    }
}

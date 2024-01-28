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

package net.momirealms.customcrops.customplugin.oraxen;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurnitureInteractEvent;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.block.BlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.block.BlockMechanicFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanicFactory;
import io.th0rgal.oraxen.utils.drops.Drop;
import net.momirealms.customcrops.customplugin.PlatformInterface;
import net.momirealms.customcrops.util.AdventureUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OraxenPluginImpl implements PlatformInterface {

    @Override
    public boolean removeCustomBlock(Location location) {
        return OraxenBlocks.remove(location, null);
    }

    @Nullable
    @Override
    public String getCustomBlockID(Location location) {
        Mechanic mechanic = OraxenBlocks.getOraxenBlock(location);
        return mechanic == null ? null : mechanic.getItemID();
    }

    @Nullable
    @Override
    public ItemStack getItemStack(String id) {
        ItemBuilder itemBuilder = OraxenItems.getItemById(id);
        return itemBuilder == null ? null : itemBuilder.build();
    }

    @Nullable
    @Override
    public ItemFrame placeItemFrame(Location location, String id) {
        FurnitureMechanic mechanic = (FurnitureMechanic) FurnitureFactory.getInstance().getMechanic(id);
        if (mechanic == null) {
            AdventureUtils.consoleMessage("<red>[CustomCrops] Furniture not exists: " + id);
            return null;
        }
        Entity entity = mechanic.place(location, 0f, BlockFace.UP);
        if (entity instanceof ItemFrame itemFrame)
            return itemFrame;
        else {
            AdventureUtils.consoleMessage("<red>[CustomCrops] ItemFrame not exists: " + id);
            // use oraxen method to remove sub entities
            OraxenFurniture.remove(entity, null);
            return null;
        }
    }

    @Nullable
    @Override
    public ItemDisplay placeItemDisplay(Location location, String id) {
        FurnitureMechanic mechanic = (FurnitureMechanic) FurnitureFactory.getInstance().getMechanic(id);
        if (mechanic == null) {
            AdventureUtils.consoleMessage("<red>[CustomCrops] Furniture not exists: " + id);
            return null;
        }
        Entity entity = mechanic.place(location);
        if (entity instanceof ItemDisplay itemDisplay)
            return itemDisplay;
        else {
            AdventureUtils.consoleMessage("<red>[CustomCrops] ItemDisplay not exists: " + id);
            // use oraxen method to remove sub entities
            OraxenFurniture.remove(entity, null);
            return null;
        }
    }

    @Override
    public void placeNoteBlock(Location location, String id) {
        try {
            NoteBlockMechanicFactory.setBlockModel(location.getBlock(), id);
        } catch (NullPointerException e) {
            AdventureUtils.consoleMessage("<red>[CustomCrop] NoteBlock not exists: " + id);
        }
    }

    @Override
    public void placeTripWire(Location location, String id) {
        try {
            StringBlockMechanicFactory.setBlockModel(location.getBlock(), id);
        } catch (NullPointerException e) {
            AdventureUtils.consoleMessage("<red>[CustomCrop] Tripwire not exists: " + id);
        }
    }

    @NotNull
    @Override
    public String getBlockID(Block block) {
        Mechanic mechanic = OraxenBlocks.getOraxenBlock(block.getBlockData());
        return mechanic == null ? block.getType().name() : mechanic.getItemID();
    }

    @Override
    public boolean doesItemExist(String id) {
        return OraxenItems.getItemById(id) != null;
    }

    @Override
    public void dropBlockLoot(Block block) {
        BlockMechanic mechanic = BlockMechanicFactory.getBlockMechanic(block);
        if (mechanic == null) return;
        Drop drop = mechanic.getDrop();
        if (drop != null)
            drop.spawns(block.getLocation(), new ItemStack(Material.AIR));
    }

    @Override
    public void placeChorus(Location location, String id) {
        StringBlockMechanicFactory.setBlockModel(location.getBlock(), id);
    }

    @Nullable
    @Override
    public String getItemDisplayID(ItemDisplay itemDisplay) {
        FurnitureMechanic furnitureMechanic = OraxenFurniture.getFurnitureMechanic(itemDisplay);
        if (furnitureMechanic != null) {
            return furnitureMechanic.getItemID();
        }
        return null;
    }

    @Nullable
    @Override
    public String getItemFrameID(ItemFrame itemFrame) {
        FurnitureMechanic furnitureMechanic = OraxenFurniture.getFurnitureMechanic(itemFrame);
        if (furnitureMechanic != null) {
            return furnitureMechanic.getItemID();
        }
        return null;
    }

    @NotNull
    @Override
    public String getItemStackID(@NotNull ItemStack itemStack) {
        if (itemStack.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(itemStack);
            NBTCompound bukkitPublic = nbtItem.getCompound("PublicBukkitValues");
            if (bukkitPublic != null) {
                String id = bukkitPublic.getString("oraxen:id");
                if (!id.equals("")) return id;
            }
        }
        return itemStack.getType().name();
    }
}
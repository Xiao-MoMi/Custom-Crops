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
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.items.OraxenItems;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicListener;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanicFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanicListener;
import net.momirealms.customcrops.integrations.customplugin.CustomInterface;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic.FURNITURE_KEY;

public class OraxenHook implements CustomInterface {

    public static NamespacedKey FURNITURE = new NamespacedKey(OraxenPlugin.get(), "furniture");

    @Override
    public void removeBlock(Location location) {
        location.getBlock().setType(Material.AIR);
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
            StringBlockMechanic mechanic = StringBlockMechanicListener.getStringMechanic(block);
            if (mechanic == null) return null;
            else return mechanic.getItemID();
        }
        else if (block.getType() == Material.NOTE_BLOCK) {
            NoteBlockMechanic mechanic = NoteBlockMechanicListener.getNoteBlockMechanic(block);
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
        ItemBuilder itemBuilder = OraxenItems.getItemById(id);
        if (itemBuilder == null) return null;
        return location.getWorld().spawn(location, ItemFrame.class, (ItemFrame frame) -> {
            frame.setVisible(false);
            frame.setFixed(false);
            frame.setPersistent(true);
            frame.setItemDropChance(0);
            frame.setItem(itemBuilder.build(), false);
            frame.setFacingDirection(BlockFace.UP, true);
            PersistentDataContainer pdc = frame.getPersistentDataContainer();
            pdc.set(FURNITURE_KEY, PersistentDataType.STRING, id);
        });
    }

    @Override
    public void removeFurniture(Entity entity) {
        entity.remove();
    }

    @Override
    public boolean doesExist(String itemID) {
        return OraxenItems.getItemById(itemID) != null;
    }
}

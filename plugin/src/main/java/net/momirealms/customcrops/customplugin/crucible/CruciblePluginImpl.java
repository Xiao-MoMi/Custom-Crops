///*
// *  Copyright (C) <2022> <XiaoMoMi>
// *
// *  This program is free software: you can redistribute it and/or modify
// *  it under the terms of the GNU General Public License as published by
// *  the Free Software Foundation, either version 3 of the License, or
// *  any later version.
// *
// *  This program is distributed in the hope that it will be useful,
// *  but WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *  GNU General Public License for more details.
// *
// *  You should have received a copy of the GNU General Public License
// *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
// */
//
//package net.momirealms.customcrops.api.customplugin.crucible;
//
//import io.lumine.mythic.bukkit.BukkitAdapter;
//import io.lumine.mythiccrucible.MythicCrucible;
//import io.lumine.mythiccrucible.items.CrucibleItem;
//import io.lumine.mythiccrucible.items.blocks.CustomBlockItemContext;
//import net.momirealms.customcrops.api.customplugin.PlatformInterface;
//import net.momirealms.customcrops.api.util.AdventureUtils;
//import org.bukkit.Location;
//import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
//import org.bukkit.entity.ItemDisplay;
//import org.bukkit.entity.ItemFrame;
//import org.bukkit.inventory.ItemStack;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.Optional;
//
//@Deprecated
//public class CruciblePluginImpl implements PlatformInterface {
//
//    @Override
//    public boolean removeCustomBlock(Location location) {
//        Optional<CustomBlockItemContext> optionalCB = MythicCrucible.inst().getItemManager().getCustomBlockManager().getBlockFromBlock(location.getBlock());
//        if (optionalCB.isEmpty()) return false;
//        optionalCB.get().remove(location.getBlock(), null, false);
//        return true;
//    }
//
//    @Nullable
//    @Override
//    public String getCustomBlockID(Location location) {
//        Optional<CustomBlockItemContext> optionalCB = MythicCrucible.inst().getItemManager().getCustomBlockManager().getBlockFromBlock(location.getBlock());
//        if (optionalCB.isEmpty()) return null;
//        else return optionalCB.get().getCrucibleItem().getInternalName();
//    }
//
//    @Nullable
//    @Override
//    public ItemStack getItemStack(String id) {
//        Optional<CrucibleItem> optionalCI = MythicCrucible.inst().getItemManager().getItem(id);
//        if (optionalCI.isEmpty()) return null;
//        else return BukkitAdapter.adapt(optionalCI.get().getMythicItem().generateItemStack(1));
//    }
//
//    @Nullable
//    @Override
//    @Deprecated
//    public ItemFrame placeItemFrame(Location location, String id) {
//        Optional<CrucibleItem> optionalCI = MythicCrucible.inst().getItemManager().getItem(id);
//        if (optionalCI.isPresent()) {
//            optionalCI.get().getFurnitureData().place(location.getBlock(), BlockFace.UP, 0);
//        } else {
//            AdventureUtils.consoleMessage("<red>[CustomCrop] ItemFrame not exists: " + id);
//        }
//        //TODO API limits (It's using private methods to place)
//        return null;
//    }
//
//    @Nullable
//    @Override
//    @Deprecated
//    public ItemDisplay placeItemDisplay(Location location, String id) {
//        //TODO Not implemented feature
//        return null;
//    }
//
//    @Override
//    public void placeNoteBlock(Location location, String id) {
//        Optional<CrucibleItem> optionalCI = MythicCrucible.inst().getItemManager().getItem(id);
//        if (optionalCI.isPresent()) {
//            location.getBlock().setBlockData(optionalCI.get().getBlockData().getBlockData());
//        } else {
//            AdventureUtils.consoleMessage("<red>[CustomCrop] NoteBlock not exists: " + id);
//        }
//    }
//
//    @Override
//    public void placeTripWire(Location location, String id) {
//        Optional<CrucibleItem> optionalCI = MythicCrucible.inst().getItemManager().getItem(id);
//        if (optionalCI.isPresent()) {
//            location.getBlock().setBlockData(optionalCI.get().getBlockData().getBlockData());
//        } else {
//            AdventureUtils.consoleMessage("<red>[CustomCrop] Tirpwire not exists: " + id);
//        }
//    }
//
//    @NotNull
//    @Override
//    public String getBlockID(Block block) {
//        Optional<CustomBlockItemContext> optionalCB = MythicCrucible.inst().getItemManager().getCustomBlockManager().getBlockFromBlock(block);
//        if (optionalCB.isEmpty()) return block.getType().name();
//        else return optionalCB.get().getCrucibleItem().getInternalName();
//    }
//
//    @Override
//    public boolean doesItemExist(String id) {
//        Optional<CrucibleItem> optionalCI = MythicCrucible.inst().getItemManager().getItem(id);
//        return optionalCI.isPresent();
//    }
//
//    @Override
//    @Deprecated
//    public void dropBlockLoot(Block block) {
//        //TODO
//    }
//
//    @Override
//    @Deprecated
//    public void placeChorus(Location location, String id) {
//        //TODO
//    }
//
//    @NotNull
//    @Override
//    public String getItemStackID(@NotNull ItemStack itemStack) {
//        Optional<CrucibleItem> optionalCI = MythicCrucible.inst().getItemManager().getItem(itemStack);
//        if (optionalCI.isEmpty()) return itemStack.getType().name();
//        else return optionalCI.get().getInternalName();
//    }
//
//    @Nullable
//    @Override
//    @Deprecated
//    public String getItemDisplayID(ItemDisplay itemDisplay) {
//        return null;
//    }
//
//    @Nullable
//    @Override
//    public String getItemFrameID(ItemFrame itemFrame) {
//        Optional<CrucibleItem> optionalCI = MythicCrucible.inst().getItemManager().getFurnitureManager().getItemFromFrame(itemFrame);
//        if (optionalCI.isEmpty()) return null;
//        else return optionalCI.get().getInternalName();
//    }
//}

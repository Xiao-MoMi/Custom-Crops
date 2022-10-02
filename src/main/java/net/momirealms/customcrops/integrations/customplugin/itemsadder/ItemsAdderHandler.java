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
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.api.event.WaterEvent;
import net.momirealms.customcrops.config.CropConfig;
import net.momirealms.customcrops.config.WaterCanConfig;
import net.momirealms.customcrops.integrations.customplugin.HandlerP;
import net.momirealms.customcrops.integrations.customplugin.itemsadder.listeners.ItemsAdderBlockListener;
import net.momirealms.customcrops.integrations.customplugin.itemsadder.listeners.ItemsAdderFurnitureListener;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.objects.WaterCan;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ItemsAdderHandler extends HandlerP {

    private final ItemsAdderBlockListener itemsAdderBlockListener;
    private final ItemsAdderFurnitureListener itemsAdderFurnitureListener;

    public ItemsAdderHandler(CropManager cropManager) {
        super(cropManager);
        this.itemsAdderBlockListener = new ItemsAdderBlockListener(this);
        this.itemsAdderFurnitureListener = new ItemsAdderFurnitureListener(this);
    }

    @Override
    public void load() {
        super.load();
        Bukkit.getPluginManager().registerEvents(this.itemsAdderBlockListener, CustomCrops.plugin);
        Bukkit.getPluginManager().registerEvents(this.itemsAdderFurnitureListener, CustomCrops.plugin);
    }

    @Override
    public void unload() {
        super.unload();
        HandlerList.unregisterAll(this.itemsAdderBlockListener);
        HandlerList.unregisterAll(this.itemsAdderFurnitureListener);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

        final Player player = event.getPlayer();
        final Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {

            Block block = event.getClickedBlock();
            if (block != null && (block.getType().isInteractable() || block.getType() == Material.TRIPWIRE)) return;

            ItemStack item = event.getItem();
            if (item == null || item.getType() == Material.AIR) return;
            NBTItem nbtItem = new NBTItem(item);
            NBTCompound iaCompound = nbtItem.getCompound("itemsadder");
            if (iaCompound == null) return;
            String namespace = iaCompound.getString("namespace");
            String id = iaCompound.getString("id");
            String namespacedID = namespace + ":" + id;

            if (fillWaterCan(namespacedID, nbtItem, item, player)) {
                return;
            }

            if (block == null) return;

            if (event.getBlockFace() == BlockFace.UP && placeSprinkler(namespacedID, event.getClickedBlock().getLocation(), player, item)) {
                return;
            }
        }
    }

    public void tryMisc(Player player, ItemStack itemInHand, Location potLoc) {
        if (itemInHand == null || itemInHand.getType() == Material.AIR) return;
        CustomStack customStack = CustomStack.byItemStack(itemInHand);

        if (customStack == null) return;
        String itemID = customStack.getNamespacedID();

        if (useSurveyor(potLoc, itemID, player)) {
            return;
        }
        if (useFertilizer(potLoc, itemID, player, itemInHand)){
            return;
        }
        if (useWateringCan(potLoc, itemID, player, customStack)) {
            return;
        }
        //for future misc
    }

    private boolean useWateringCan(Location potLoc, String namespacedID, Player player, @NotNull CustomStack can) {
        WaterCan waterCan = WaterCanConfig.CANS.get(namespacedID);
        if (waterCan == null) return false;

        if (can.hasCustomDurability()) {
            if (can.getDurability() > 0) {
                can.setDurability(can.getDurability() - 1);
            }
            else return true;
        }

        NBTItem nbtItem = new NBTItem(can.getItemStack());
        int water = nbtItem.getInteger("WaterAmount");
        if (water > 0) {

            WaterEvent waterEvent = new WaterEvent(player, can.getItemStack());
            Bukkit.getPluginManager().callEvent(waterEvent);
            if (waterEvent.isCancelled()) {
                return true;
            }

            nbtItem.setInteger("WaterAmount", water - 1);
            //TODO check
            can.getItemStack().setItemMeta(nbtItem.getItem().getItemMeta());
            super.waterPot(waterCan.width(), waterCan.getLength(), potLoc, player.getLocation().getYaw());
        }

        return true;
    }

    public Crop getCropFromID(String namespacedID) {
        String[] cropNameList = StringUtils.split(StringUtils.split(namespacedID, ":")[1], "_");
        return CropConfig.CROPS.get(cropNameList[0]);
    }

    public void onBreakBlock(CustomBlockBreakEvent event) {
        //null
    }

    public void onInteractBlock(CustomBlockInteractEvent event) {
        //null
    }

    public void onInteractFurniture(FurnitureInteractEvent event) {
        //null
    }

    public void onBreakFurniture(FurnitureBreakEvent event) {
        //null
    }

    public void chorusFix(Block block) {
        if (block.getType() != Material.CHORUS_PLANT) return;
        CustomBlock.remove(block.getLocation());
    }
}

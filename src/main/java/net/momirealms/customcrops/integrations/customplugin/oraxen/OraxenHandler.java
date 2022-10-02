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

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.th0rgal.oraxen.events.*;
import io.th0rgal.oraxen.items.OraxenItems;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.api.event.WaterEvent;
import net.momirealms.customcrops.config.CropConfig;
import net.momirealms.customcrops.config.WaterCanConfig;
import net.momirealms.customcrops.integrations.customplugin.HandlerP;
import net.momirealms.customcrops.integrations.customplugin.oraxen.listeners.OraxenBlockListener;
import net.momirealms.customcrops.integrations.customplugin.oraxen.listeners.OraxenFurnitureListener;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.objects.WaterCan;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class OraxenHandler extends HandlerP {

    private final OraxenBlockListener oraxenBlockListener;
    private final OraxenFurnitureListener oraxenFurnitureListener;

    public OraxenHandler(CropManager cropManager) {
        super(cropManager);
        this.oraxenBlockListener = new OraxenBlockListener(this);
        this.oraxenFurnitureListener = new OraxenFurnitureListener(this);
    }

    @Override
    public void load() {
        super.load();
        Bukkit.getPluginManager().registerEvents(this.oraxenBlockListener, CustomCrops.plugin);
        Bukkit.getPluginManager().registerEvents(this.oraxenFurnitureListener, CustomCrops.plugin);
    }

    @Override
    public void unload() {
        super.unload();
        HandlerList.unregisterAll(this.oraxenBlockListener);
        HandlerList.unregisterAll(this.oraxenFurnitureListener);
    }

    public void tryMisc(Player player, ItemStack itemInHand, Location potLoc) {
        if (itemInHand == null || itemInHand.getType() == Material.AIR) return;
        String id = OraxenItems.getIdByItem(itemInHand);
        if (id == null) return;

        if (useSurveyor(potLoc, id, player)) {
            return;
        }
        if (useFertilizer(potLoc, id, player, itemInHand)){
            return;
        }
        if (useWateringCan(potLoc, id, player, itemInHand)) {
            return;
        }
        //for future misc
    }

    private boolean useWateringCan(Location potLoc, String id, Player player, @NotNull ItemStack can) {
        WaterCan waterCan = WaterCanConfig.CANS.get(id);
        if (waterCan == null) return false;

        NBTItem nbtItem = new NBTItem(can);
        int water = nbtItem.getInteger("WaterAmount");
        if (water > 0) {

            WaterEvent waterEvent = new WaterEvent(player, can);
            Bukkit.getPluginManager().callEvent(waterEvent);
            if (waterEvent.isCancelled()) {
                return true;
            }
            nbtItem.setInteger("WaterAmount", water - 1);

            can.setItemMeta(nbtItem.getItem().getItemMeta());
            super.waterPot(waterCan.width(), waterCan.getLength(), potLoc, player.getLocation().getYaw());
        }
        return true;
    }

    @Nullable
    public Crop getCropFromID(String id) {
        return CropConfig.CROPS.get(StringUtils.split(id, "_")[0]);
    }

    public void onBreakNoteBlock(OraxenNoteBlockBreakEvent event) {
    }

    public void onBreakStringBlock(OraxenStringBlockBreakEvent event) {
    }

    public void onInteractStringBlock(OraxenStringBlockInteractEvent event) {
    }

    public void onInteractNoteBlock(OraxenNoteBlockInteractEvent event) {
    }

    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
    }

    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
    }
}

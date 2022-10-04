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

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.th0rgal.oraxen.events.*;
import io.th0rgal.oraxen.items.OraxenItems;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.api.event.WaterEvent;
import net.momirealms.customcrops.config.CropConfig;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.SoundConfig;
import net.momirealms.customcrops.config.WaterCanConfig;
import net.momirealms.customcrops.integrations.customplugin.HandlerP;
import net.momirealms.customcrops.integrations.customplugin.oraxen.listeners.OraxenBlockListener;
import net.momirealms.customcrops.integrations.customplugin.oraxen.listeners.OraxenFurnitureListener;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.objects.WaterCan;
import net.momirealms.customcrops.utils.AdventureUtil;
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

    public boolean tryMisc(Player player, ItemStack itemInHand, Location potLoc) {
        if (itemInHand == null || itemInHand.getType() == Material.AIR) return true;
        String id = OraxenItems.getIdByItem(itemInHand);
        if (id == null) return false;

        if (useSurveyor(potLoc, id, player)) {
            return true;
        }
        if (useFertilizer(potLoc, id, player, itemInHand)){
            return true;
        }
        if (useWateringCan(potLoc, id, player, itemInHand)) {
            return true;
        }
        return false;
        //for future misc
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {

            Block block = event.getClickedBlock();

            if (block != null && ((block.getType().isInteractable() && block.getType() != Material.NOTE_BLOCK) || block.getType() == Material.TRIPWIRE)) return;

            ItemStack item = event.getItem();
            if (item == null || item.getType() == Material.AIR) return;
            NBTItem nbtItem = new NBTItem(item);

            NBTCompound bukkitCompound = nbtItem.getCompound("PublicBukkitValues");
            if (bukkitCompound != null) {
                String id = bukkitCompound.getString("oraxen:id");
                if (id == null || id.equals("")) return;

                if (fillWaterCan(id, nbtItem, item, player)) {
                    return;
                }

                if (block == null) return;

                if (event.getBlockFace() == BlockFace.UP && placeSprinkler(id, block.getLocation(), player, item)) {
                    return;
                }
            }
        }
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

            if (SoundConfig.waterPot.isEnable()) {
                AdventureUtil.playerSound(
                        player,
                        SoundConfig.waterPot.getSource(),
                        SoundConfig.waterPot.getKey(),
                        1,1
                );
            }

            if (MainConfig.enableActionBar) {
                String canID = customInterface.getItemID(can);
                WaterCan canConfig = WaterCanConfig.CANS.get(canID);
                if (canConfig == null) return true;

                AdventureUtil.playerActionbar(
                        player,
                        (MainConfig.actionBarLeft +
                                MainConfig.actionBarFull.repeat(water) +
                                MainConfig.actionBarEmpty.repeat(canConfig.getMax() - water) +
                                MainConfig.actionBarRight)
                                .replace("{max_water}", String.valueOf(canConfig.getMax()))
                                .replace("{water}", String.valueOf(water))
                );
            }

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

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
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import net.kyori.adventure.key.Key;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.event.WaterPotEvent;
import net.momirealms.customcrops.config.BasicItemConfig;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.SoundConfig;
import net.momirealms.customcrops.config.WaterCanConfig;
import net.momirealms.customcrops.integrations.CCAntiGrief;
import net.momirealms.customcrops.integrations.customplugin.HandlerP;
import net.momirealms.customcrops.integrations.customplugin.itemsadder.listeners.ItemsAdderBlockListener;
import net.momirealms.customcrops.integrations.customplugin.itemsadder.listeners.ItemsAdderFurnitureListener;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.managers.CustomWorld;
import net.momirealms.customcrops.objects.WaterCan;
import net.momirealms.customcrops.utils.AdventureUtil;
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

    public void placeScarecrow(FurniturePlaceSuccessEvent event) {
        if (!MainConfig.enableCrow) return;
        String id = event.getNamespacedID();
        if (id == null || !id.equals(BasicItemConfig.scarecrow)) return;
        Location location = event.getBukkitEntity().getLocation();
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;
        customWorld.addScarecrowCache(location);
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
            NBTCompound iaCompound = nbtItem.getCompound("itemsadder");
            if (iaCompound == null) return;
            String namespace = iaCompound.getString("namespace");
            String id = iaCompound.getString("id");
            String namespacedID = namespace + ":" + id;

            if (fillWaterCan(namespacedID, nbtItem, item, player)) {
                return;
            }

            if (block == null) return;

            Location location = block.getLocation();
            if (!CCAntiGrief.testPlace(player, location)) return;
            if (event.getBlockFace() == BlockFace.UP) {
                placeSprinkler(namespacedID, event.getClickedBlock().getLocation(), player, item);
            }
        }
    }

    public boolean tryMisc(Player player, ItemStack itemInHand, Location potLoc) {
        if (!CCAntiGrief.testPlace(player, potLoc)) return true;
        if (itemInHand == null || itemInHand.getType() == Material.AIR) return true;

        if (useBucket(potLoc, player, itemInHand)) {
            return true;
        }

        CustomStack customStack = CustomStack.byItemStack(itemInHand);
        if (customStack == null) return false;
        String itemID = customStack.getNamespacedID();

        if (useSurveyor(potLoc, itemID, player)) {
            return true;
        }
        if (useFertilizer(potLoc, itemID, player, itemInHand)){
            return true;
        }
        if (useWateringCan(potLoc, itemID, player, customStack)) {
            return true;
        }

        return false;
    }

    private boolean useWateringCan(Location potLoc, String namespacedID, Player player, @NotNull CustomStack can) {

        WaterCan canConfig = WaterCanConfig.CANS.get(namespacedID);
        if (canConfig == null) return false;

        ItemStack itemStack = can.getItemStack();
        NBTItem nbtItem = new NBTItem(itemStack);
        int water = nbtItem.getInteger("WaterAmount");
        if (water > 0) {

            WaterPotEvent waterPotEvent = new WaterPotEvent(player, potLoc, itemStack, --water);
            Bukkit.getPluginManager().callEvent(waterPotEvent);
            if (waterPotEvent.isCancelled()) {
                return true;
            }
            nbtItem = new NBTItem(waterPotEvent.getItemStack());
            NBTCompound nbtCompound = nbtItem.getCompound("itemsadder");
            if (nbtCompound.hasTag("custom_durability")){
                int dur = nbtCompound.getInteger("custom_durability");
                int max_dur = nbtCompound.getInteger("max_custom_durability");
                if (dur > 0) {
                    nbtCompound.setInteger("custom_durability", dur - 1);
                    nbtCompound.setDouble("fake_durability", (int) itemStack.getType().getMaxDurability() * (double) (dur/max_dur));
                    nbtItem.setInteger("Damage", (int) (itemStack.getType().getMaxDurability() * (1 - (double) dur/max_dur)));
                }
                else {
                    AdventureUtil.playerSound(player, net.kyori.adventure.sound.Sound.Source.PLAYER, Key.key("minecraft:item.shield.break"), 1, 1);
                    itemStack.setAmount(itemStack.getAmount() - 1);
                }
            }

            nbtItem.setInteger("WaterAmount", waterPotEvent.getCurrentWater());

            if (SoundConfig.waterPot.isEnable()) {
                AdventureUtil.playerSound(
                        player,
                        SoundConfig.waterPot.getSource(),
                        SoundConfig.waterPot.getKey(),
                        1,1
                );
            }

            if (MainConfig.enableActionBar) {
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

            if (MainConfig.enableWaterCanLore && !MainConfig.enablePacketLore) {
                addWaterLore(nbtItem, canConfig, water);
            }

            itemStack.setItemMeta(nbtItem.getItem().getItemMeta());

            if (MainConfig.enableWaterCanLore && MainConfig.enablePacketLore) {
                player.updateInventory();
            }

            super.waterPot(canConfig.getWidth(), canConfig.getLength(), potLoc, player.getLocation().getYaw());
        }

        return true;
    }

    public void onBreakBlock(CustomBlockBreakEvent event) {
        //null
    }

    public void onInteractFurniture(FurnitureInteractEvent event) {
        //null
    }

    public void onBreakFurniture(FurnitureBreakEvent event) {
        //null
    }
}

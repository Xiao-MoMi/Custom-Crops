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

package net.momirealms.customcrops.listener.tripwire;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import net.momirealms.customcrops.listener.JoinAndQuit;
import net.momirealms.customcrops.objects.SimpleLocation;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.WateringCan;
import net.momirealms.customcrops.utils.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;


public class InteractFurnitureT implements Listener {

    @EventHandler
    public void onEntityInteract(FurnitureInteractEvent event){
        Sprinkler config = ConfigReader.SPRINKLERS.get(event.getNamespacedID());
        if(config != null){
            long time = System.currentTimeMillis();
            Player player = event.getPlayer();
            if (time - (JoinAndQuit.coolDown.getOrDefault(player, time - 200)) < 200) return;
            JoinAndQuit.coolDown.put(player, time);
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            Location location = event.getBukkitEntity().getLocation();
            String world = location.getWorld().getName();
            int x = location.getBlockX();
            int z = location.getBlockZ();
            int maxWater = config.getWater();
            int currentWater = 0;
            Location loc = location.clone().subtract(0,1,0).getBlock().getLocation().add(0,1,0);
            Sprinkler sprinkler = SprinklerManager.Cache.get(LocUtil.fromLocation(loc));
            if (itemStack.getType() == Material.WATER_BUCKET){
                itemStack.setType(Material.BUCKET);
                if (sprinkler != null){
                    currentWater = sprinkler.getWater();
                    currentWater += ConfigReader.Config.sprinklerRefill;
                    if (currentWater > maxWater) currentWater = maxWater;
                    sprinkler.setWater(currentWater);
                }else {
                    String path = world + "." + x / 16 + "," + z / 16 + "." + x + "," + location.getBlockY() + "," + z ;
                    currentWater = SprinklerManager.data.getInt(path+ ".water");
                    currentWater += ConfigReader.Config.sprinklerRefill;
                    if (currentWater > maxWater) currentWater = maxWater;
                    SprinklerManager.data.set(path + ".water", currentWater);
                    SprinklerManager.data.set(path + ".range", config.getRange());
                }
                AdventureManager.playerSound(player, ConfigReader.Sounds.addWaterToSprinklerSource, ConfigReader.Sounds.addWaterToSprinklerKey);
            }
            else {
                if (ConfigReader.Config.canAddWater && itemStack.getType() != Material.AIR){
                    NBTItem nbtItem = new NBTItem(itemStack);
                    NBTCompound nbtCompound = nbtItem.getCompound("itemsadder");
                    if (nbtCompound != null) {
                        String id = nbtCompound.getString("id");
                        String namespace = nbtCompound.getString("namespace");
                        WateringCan wateringCan = ConfigReader.CANS.get(namespace + ":" + id);
                        if (wateringCan != null) {
                            int water = nbtItem.getInteger("WaterAmount");
                            if (water > 0){
                                nbtItem.setInteger("WaterAmount", --water);
                                AdventureManager.playerSound(player, ConfigReader.Sounds.addWaterToSprinklerSource, ConfigReader.Sounds.addWaterToSprinklerKey);
                                if (sprinkler != null){
                                    currentWater = sprinkler.getWater();
                                    currentWater++;
                                    if (currentWater > maxWater) currentWater = maxWater;
                                    sprinkler.setWater(currentWater);
                                }else {
                                    String path = world + "." + x / 16 + "," + z / 16 + "." + x + "," + location.getBlockY() + "," + z ;
                                    currentWater = SprinklerManager.data.getInt(path + ".water");
                                    currentWater++;
                                    if (currentWater > maxWater) currentWater = maxWater;
                                    SprinklerManager.data.set(path + ".water", currentWater);
                                    SprinklerManager.data.set(path + ".range", config.getRange());
                                }
                            }
                            else {
                                currentWater = SprinklerManager.getCurrentWater(location, world, x, z, sprinkler);
                            }
                            if (ConfigReader.Message.hasWaterInfo){
                                AdventureManager.playerActionbar(player,
                                        (ConfigReader.Message.waterLeft +
                                         ConfigReader.Message.waterFull.repeat(water) +
                                         ConfigReader.Message.waterEmpty.repeat(wateringCan.getMax() - water) +
                                         ConfigReader.Message.waterRight)
                                        .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                        .replace("{water}", String.valueOf(water)));
                            }
                            if (ConfigReader.Basic.hasWaterLore){
                                String string =
                                        (ConfigReader.Basic.waterLeft +
                                         ConfigReader.Basic.waterFull.repeat(water) +
                                         ConfigReader.Basic.waterEmpty.repeat(wateringCan.getMax() - water) +
                                         ConfigReader.Basic.waterRight)
                                        .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                        .replace("{water}", String.valueOf(water));
                                List<String> lores = nbtItem.getCompound("display").getStringList("Lore");
                                lores.clear();
                                ConfigReader.Basic.waterLore.forEach(lore -> lores.add(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(lore.replace("{water_info}", string)))));
                            }
                            itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
                        }
                    }
                    else currentWater = SprinklerManager.getCurrentWater(location, world, x, z, sprinkler);
                }
                else currentWater = SprinklerManager.getCurrentWater(location, world, x, z, sprinkler);
            }
            if (ConfigReader.Message.hasSprinklerInfo)
                HoloUtil.showHolo(
                        (ConfigReader.Message.sprinklerLeft +
                         ConfigReader.Message.sprinklerFull.repeat(currentWater) +
                         ConfigReader.Message.sprinklerEmpty.repeat(maxWater - currentWater) +
                         ConfigReader.Message.sprinklerRight)
                        .replace("{max_water}", String.valueOf(maxWater))
                        .replace("{water}", String.valueOf(currentWater)),
                        player,
                        location.add(0, ConfigReader.Message.sprinklerOffset,0),
                        ConfigReader.Message.sprinklerTime);
        }
    }
}

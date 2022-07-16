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

package net.momirealms.customcrops.listener;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.datamanager.CropManager;
import net.momirealms.customcrops.datamanager.PotManager;
import net.momirealms.customcrops.datamanager.SeasonManager;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import net.momirealms.customcrops.fertilizer.Fertilizer;
import net.momirealms.customcrops.fertilizer.QualityCrop;
import net.momirealms.customcrops.fertilizer.RetainingSoil;
import net.momirealms.customcrops.fertilizer.SpeedGrow;
import net.momirealms.customcrops.integrations.Integration;
import net.momirealms.customcrops.limits.CropsPerChunk;
import net.momirealms.customcrops.limits.SprinklersPerChunk;
import net.momirealms.customcrops.requirements.PlantingCondition;
import net.momirealms.customcrops.requirements.Requirement;
import net.momirealms.customcrops.utils.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class RightClick implements Listener {

    private final HashMap<Player, Long> coolDown = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        long time = System.currentTimeMillis();
        Player player = event.getPlayer();
        if (time - (coolDown.getOrDefault(player, time - 250)) < 250) {
            return;
        }
        coolDown.put(player, time);
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
            ItemStack itemStack = event.getItem();
            if (itemStack != null){
                if (itemStack.getType() == Material.AIR) return;
                NBTItem nbtItem = new NBTItem(itemStack);
                NBTCompound nbtCompound = nbtItem.getCompound("itemsadder");
                if (nbtCompound != null){
                    String id = nbtCompound.getString("id");
                    if (id.endsWith("_seeds") && action == Action.RIGHT_CLICK_BLOCK && event.getBlockFace() == BlockFace.UP){
                        String cropName = StringUtils.remove(id, "_seeds");
                        Optional<CropInstance> crop = Optional.ofNullable(ConfigReader.CROPS.get(cropName));
                        if (crop.isPresent()){
                            Block block = event.getClickedBlock();
                            CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                            if (customBlock == null) return;
                            String namespacedID = customBlock.getNamespacedID();
                            if (namespacedID.equals(ConfigReader.Basic.pot) || namespacedID.equals(ConfigReader.Basic.watered_pot)){
                                Location location = block.getLocation().add(0,1,0); //已+1
                                for (Integration integration : ConfigReader.Config.integration){
                                    if(!integration.canPlace(location, player)) return;
                                }
                                CropInstance cropInstance = crop.get();
                                PlantingCondition plantingCondition = new PlantingCondition(player, location);
                                if (cropInstance.getRequirements() != null){
                                    for (Requirement requirement : cropInstance.getRequirements()){
                                        if (!requirement.canPlant(plantingCondition)) return;
                                    }
                                }
                                Label_out:
                                if (ConfigReader.Season.enable && cropInstance.getSeasons() != null){
                                    if (!ConfigReader.Config.allWorld){
                                        for (String season : cropInstance.getSeasons()) {
                                            if (season.equals(SeasonManager.SEASON.get(location.getWorld().getName()))){
                                                break Label_out;
                                            }
                                        }
                                    }else {
                                        for(String season : cropInstance.getSeasons()){
                                            if (season.equals(SeasonManager.SEASON.get(ConfigReader.Config.referenceWorld))) {
                                                break Label_out;
                                            }
                                        }
                                    }
                                    if(ConfigReader.Season.greenhouse){
                                        for(int i = 1; i <= ConfigReader.Season.range; i++){
                                            CustomBlock cb = CustomBlock.byAlreadyPlaced(location.clone().add(0,i,0).getBlock());
                                            if (cb != null){
                                                if(cb.getNamespacedID().equalsIgnoreCase(ConfigReader.Basic.glass)){
                                                    break Label_out;
                                                }
                                            }
                                        }
                                    }
                                    AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.badSeason);
                                    return;
                                }
                                if(location.getBlock().getType() != Material.AIR){
                                    return;
                                }
                                if(CropsPerChunk.isLimited(location)){
                                    AdventureManager.playerMessage(player,ConfigReader.Message.prefix + ConfigReader.Message.crop_limit.replace("{max}", String.valueOf(ConfigReader.Config.cropLimit)));
                                    return;
                                }
                                itemStack.setAmount(itemStack.getAmount() - 1);
                                CropManager.Cache.put(location, cropName);
                                CustomBlock.place((nbtCompound.getString("namespace") + ":" + cropName + "_stage_1"), location);
                                AdventureManager.playerSound(player, ConfigReader.Sounds.plantSeedSource, ConfigReader.Sounds.plantSeedKey);
                                return;
                            }
                        }else {
                            AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.not_configed);
                        }
                        return;
                    }
                    Optional<WateringCan> can = Optional.ofNullable(ConfigReader.CANS.get(id));
                    if (can.isPresent()){
                        WateringCan wateringCan = can.get();
                        int water = nbtItem.getInteger("WaterAmount");
                        List<Block> lineOfSight = player.getLineOfSight(null, 5);
                        for (Block block : lineOfSight) {
                            if (block.getType() == Material.WATER) {
                                if (wateringCan.getMax() > water){
                                    water += ConfigReader.Config.waterCanRefill;
                                    if (water > wateringCan.getMax()){
                                        water = wateringCan.getMax();
                                    }
                                    nbtItem.setInteger("WaterAmount", water);
                                    player.getWorld().playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL,1,1);
                                    if (ConfigReader.Message.hasWaterInfo){
                                        String string = ConfigReader.Message.waterLeft + ConfigReader.Message.waterFull.repeat(water) +
                                                ConfigReader.Message.waterEmpty.repeat(wateringCan.getMax() - water) + ConfigReader.Message.waterRight;
                                        AdventureManager.playerActionbar(player, string.replace("{max_water}", String.valueOf(wateringCan.getMax())).replace("{water}", String.valueOf(water)));
                                    }
                                    if (ConfigReader.Basic.hasWaterLore){
                                        String string = (ConfigReader.Basic.waterLeft + ConfigReader.Basic.waterFull.repeat(water) +
                                                ConfigReader.Basic.waterEmpty.repeat(wateringCan.getMax() - water) + ConfigReader.Basic.waterRight).replace("{max_water}", String.valueOf(wateringCan.getMax())).replace("{water}", String.valueOf(water));
                                        List<String> lores = nbtItem.getCompound("display").getStringList("Lore");
                                        lores.clear();
                                        ConfigReader.Basic.waterLore.forEach(lore -> lores.add(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(lore.replace("{water_info}", string)))));
                                    }
                                    itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
                                }
                                return;
                            }
                        }
                        if(water != 0 && action == Action.RIGHT_CLICK_BLOCK && event.getBlockFace() == BlockFace.UP){
                            Block block = event.getClickedBlock();
                            CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                            if (customBlock == null) return;
                            for (Integration integration : ConfigReader.Config.integration){
                                if(!integration.canPlace(block.getLocation(), player)) return;
                            }
                            String namespacedID = customBlock.getNamespacedID();
                            if (namespacedID.equals(ConfigReader.Basic.pot) || namespacedID.equals(ConfigReader.Basic.watered_pot)){
                                nbtItem.setInteger("WaterAmount", water - 1);
                                AdventureManager.playerSound(player, ConfigReader.Sounds.waterPotSource, ConfigReader.Sounds.waterPotKey);
                                waterPot(wateringCan.getWidth(), wateringCan.getLength(), block.getLocation(), player.getLocation().getYaw());
                                if (ConfigReader.Message.hasWaterInfo){
                                    String string = ConfigReader.Message.waterLeft + ConfigReader.Message.waterFull.repeat(water - 1) +
                                            ConfigReader.Message.waterEmpty.repeat(wateringCan.getMax() - water + 1) + ConfigReader.Message.waterRight;
                                    AdventureManager.playerActionbar(player, string.replace("{max_water}", String.valueOf(wateringCan.getMax())).replace("{water}", String.valueOf(water -1)));
                                }
                                if (ConfigReader.Basic.hasWaterLore){
                                    String string = (ConfigReader.Basic.waterLeft + ConfigReader.Basic.waterFull.repeat(water - 1) +
                                            ConfigReader.Basic.waterEmpty.repeat(wateringCan.getMax() - water + 1) + ConfigReader.Basic.waterRight).replace("{max_water}", String.valueOf(wateringCan.getMax())).replace("{water}", String.valueOf(water -1));
                                    List<String> lores = nbtItem.getCompound("display").getStringList("Lore");
                                    lores.clear();
                                    ConfigReader.Basic.waterLore.forEach(lore -> lores.add(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(lore.replace("{water_info}", string)))));
                                }
                                itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
                            }else if (namespacedID.contains("_stage_")){
                                nbtItem.setInteger("WaterAmount", water - 1);
                                AdventureManager.playerSound(player, ConfigReader.Sounds.waterPotSource, ConfigReader.Sounds.waterPotKey);
                                waterPot(wateringCan.getWidth(), wateringCan.getLength(), block.getLocation().subtract(0,1,0), player.getLocation().getYaw());
                                if (ConfigReader.Message.hasWaterInfo){
                                    String string = ConfigReader.Message.waterLeft + ConfigReader.Message.waterFull.repeat(water - 1) +
                                            ConfigReader.Message.waterEmpty.repeat(wateringCan.getMax() - water + 1) + ConfigReader.Message.waterRight;
                                    AdventureManager.playerActionbar(player, string.replace("{max_water}", String.valueOf(wateringCan.getMax())).replace("{water}", String.valueOf(water -1)));
                                }
                                if (ConfigReader.Basic.hasWaterLore){
                                    String string = (ConfigReader.Basic.waterLeft + ConfigReader.Basic.waterFull.repeat(water - 1) +
                                            ConfigReader.Basic.waterEmpty.repeat(wateringCan.getMax() - water + 1) + ConfigReader.Basic.waterRight).replace("{max_water}", String.valueOf(wateringCan.getMax())).replace("{water}", String.valueOf(water -1));
                                    List<String> lores = nbtItem.getCompound("display").getStringList("Lore");
                                    lores.clear();
                                    ConfigReader.Basic.waterLore.forEach(lore -> lores.add(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(lore.replace("{water_info}", string)))));
                                }
                                itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
                            }
                        }
                        return;
                    }
                    Optional<Fertilizer> fertilize = Optional.ofNullable(ConfigReader.FERTILIZERS.get(id));
                    if (fertilize.isPresent() && action == Action.RIGHT_CLICK_BLOCK){
                        Fertilizer fertilizerConfig = fertilize.get();
                        Block block = event.getClickedBlock();
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                        if (customBlock == null) return;
                        for (Integration integration : ConfigReader.Config.integration){
                            if(!integration.canPlace(block.getLocation(), player)) return;
                        }
                        String namespacedID = customBlock.getNamespacedID();
                        if (namespacedID.equals(ConfigReader.Basic.pot) || namespacedID.equals(ConfigReader.Basic.watered_pot)){
                            CustomBlock customBlockUp = CustomBlock.byAlreadyPlaced(block.getLocation().clone().add(0,1,0).getBlock());
                            if (customBlockUp != null){
                                if (fertilizerConfig.isBefore() && customBlockUp.getNamespacedID().contains("_stage_")){
                                    AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.beforePlant);
                                    return;
                                }else {
                                    itemStack.setAmount(itemStack.getAmount() - 1);
                                    AdventureManager.playerSound(player, ConfigReader.Sounds.useFertilizerSource, ConfigReader.Sounds.useFertilizerKey);
                                    addFertilizer(fertilizerConfig, block.getLocation());
                                }
                            }else {
                                itemStack.setAmount(itemStack.getAmount() - 1);
                                AdventureManager.playerSound(player, ConfigReader.Sounds.useFertilizerSource, ConfigReader.Sounds.useFertilizerKey);
                                addFertilizer(fertilizerConfig, block.getLocation());
                            }
                        }else if (namespacedID.contains("_stage_")){
                            if (!fertilizerConfig.isBefore()){
                                itemStack.setAmount(itemStack.getAmount() - 1);
                                addFertilizer(fertilizerConfig, block.getLocation().subtract(0,1,0));
                                AdventureManager.playerSound(player, ConfigReader.Sounds.useFertilizerSource, ConfigReader.Sounds.useFertilizerKey);
                            }else {
                                AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.beforePlant);
                                return;
                            }
                        }
                        return;
                    }
                    Optional<Sprinkler> sprinkler = Optional.ofNullable(ConfigReader.SPRINKLERS.get(id));
                    if (sprinkler.isPresent() && action == Action.RIGHT_CLICK_BLOCK && event.getBlockFace() == BlockFace.UP){
                        Location location = event.getClickedBlock().getLocation();
                        for (Integration integration : ConfigReader.Config.integration){
                            if (!integration.canPlace(location, player)) return;
                        }
                        if(IAFurniture.getFromLocation(location.clone().add(0.5, 1.5, 0.5), location.getWorld())){
                            return;
                        }
                        if(SprinklersPerChunk.isLimited(location)){
                            AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.sprinkler_limit.replace("{max}", String.valueOf(ConfigReader.Config.sprinklerLimit)));
                            return;
                        }
                        Sprinkler sprinklerData = new Sprinkler(sprinkler.get().getRange(), 0);
                        itemStack.setAmount(itemStack.getAmount() - 1);
                        SprinklerManager.Cache.put(location.add(0,1,0), sprinklerData);
                        IAFurniture.placeFurniture(sprinkler.get().getNamespacedID_2(),location);
                        AdventureManager.playerSound(player, ConfigReader.Sounds.placeSprinklerSource, ConfigReader.Sounds.placeSprinklerKey);
                        return;
                    }
                    if (ConfigReader.Message.hasCropInfo && id.equals(ConfigReader.Basic.soilDetector) && action == Action.RIGHT_CLICK_BLOCK){
                        Block block = event.getClickedBlock();
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                        if (customBlock == null) return;
                        for (Integration integration : ConfigReader.Config.integration){
                            if(!integration.canPlace(block.getLocation(), player)) return;
                        }
                        String namespacedID = customBlock.getNamespacedID();
                        if (namespacedID.contains("_stage_")){
                            Location location = block.getLocation().subtract(0,1,0);
                            Fertilizer fertilizer = PotManager.Cache.get(SimpleLocation.fromLocation(location));
                            if (fertilizer != null){
                                Fertilizer config = ConfigReader.FERTILIZERS.get(fertilizer.getKey());
                                String name = config.getName();
                                int max_times = config.getTimes();
                                if(HoloUtil.cache.get(location.add(0.5, ConfigReader.Message.cropOffset, 0.5)) == null) {
                                    HoloUtil.showHolo(ConfigReader.Message.cropText.replace("{fertilizer}", name).replace("{times}", String.valueOf(fertilizer.getTimes())).replace("{max_times}", String.valueOf(max_times)), player, location, ConfigReader.Message.cropTime);
                                }
                            }
                        }else if(namespacedID.equals(ConfigReader.Basic.pot) || namespacedID.equals(ConfigReader.Basic.watered_pot)){
                            Location location = block.getLocation();
                            Fertilizer fertilizer = PotManager.Cache.get(SimpleLocation.fromLocation(block.getLocation()));
                            if (fertilizer != null){
                                Fertilizer config = ConfigReader.FERTILIZERS.get(fertilizer.getKey());
                                String name = config.getName();
                                int max_times = config.getTimes();
                                if(HoloUtil.cache.get(location.add(0.5,ConfigReader.Message.cropOffset,0.5)) == null){
                                    HoloUtil.showHolo(ConfigReader.Message.cropText.replace("{fertilizer}", name).replace("{times}", String.valueOf(fertilizer.getTimes())).replace("{max_times}", String.valueOf(max_times)), player, location, ConfigReader.Message.cropTime);
                                }
                            }
                        }
                    }
                }
            }
            else if (action == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                Location location = block.getLocation();
                CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                if (customBlock == null) return;
                for (Integration integration : ConfigReader.Config.integration){
                    if (!integration.canBreak(location, player)) return;
                }
                String namespacedID = customBlock.getNamespacedID();
                if (namespacedID.contains("_stage_")){
                    if(namespacedID.equals(ConfigReader.Basic.dead)) return;
                    String[] cropNameList = StringUtils.split(customBlock.getId(), "_");
                    int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                    if (CustomBlock.getInstance(StringUtils.chop(namespacedID) + nextStage) == null) {
                        if (ConfigReader.Config.quality){
                            CropInstance cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                            ThreadLocalRandom current = ThreadLocalRandom.current();
                            int random = current.nextInt(cropInstance.getMin(), cropInstance.getMax() + 1);
                            World world = location.getWorld();
                            Location itemLoc = location.clone().add(0.5,0.2,0.5);
                            Fertilizer fertilizer = PotManager.Cache.get(location.clone().subtract(0,1,0));
                            if (fertilizer != null){
                                if (fertilizer instanceof QualityCrop qualityCrop){
                                    int[] weights = qualityCrop.getChance();
                                    double weightTotal = weights[0] + weights[1] + weights[2];
                                    double rank_1 = weights[0]/(weightTotal);
                                    double rank_2 = 1 - weights[1]/(weightTotal);
                                    for (int i = 0; i < random; i++){
                                        double ran = Math.random();
                                        if (ran < rank_1){
                                            world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_1()).getItemStack());
                                        }else if(ran > rank_2){
                                            world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_2()).getItemStack());
                                        }else {
                                            world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_3()).getItemStack());
                                        }
                                    }
                                }else {
                                    BreakBlock.normalDrop(cropInstance, random, itemLoc, world);
                                }
                            }
                            else {
                                BreakBlock.normalDrop(cropInstance, random, itemLoc, world);
                            }
                        }else {
                            customBlock.getLoot().forEach(loot-> location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), loot));
                        }
                        CustomBlock.remove(location);
                        CropInstance crop = ConfigReader.CROPS.get(cropNameList[0]);
                        if(crop.getReturnStage() != null){
                            CustomBlock.place(crop.getReturnStage(), location);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        coolDown.remove(event.getPlayer());
    }

    private void addFertilizer(Fertilizer fertilizerConfig, Location location) {
        if (fertilizerConfig instanceof QualityCrop config){
            QualityCrop qualityCrop = new QualityCrop(config.getKey(), config.getTimes(), config.getChance(), config.isBefore());
            PotManager.Cache.put(SimpleLocation.fromLocation(location), qualityCrop);
        }else if (fertilizerConfig instanceof SpeedGrow config){
            SpeedGrow speedGrow = new SpeedGrow(config.getKey(), config.getTimes(),config.getChance(), config.isBefore());
            PotManager.Cache.put(SimpleLocation.fromLocation(location), speedGrow);
        }else if (fertilizerConfig instanceof RetainingSoil config){
            RetainingSoil retainingSoil = new RetainingSoil(config.getKey(), config.getTimes(),config.getChance(), config.isBefore());
            PotManager.Cache.put(SimpleLocation.fromLocation(location), retainingSoil);
        }
    }

    private void waterPot(int width, int length, Location clickedLocation, float yaw){
        int extend = width / 2;
        // -90~90 z+
        // -180~-90 & 90-180 z-
        // -180~0 x+
        // 0~180 x-
        if (yaw < 45 && yaw > -135) {
            // -45 ~ 45
            if (yaw > -45) {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = clickedLocation.clone().add(i, 0, -1);
                    for (int j = 0; j < length; j++){
                        tempLoc.add(0,0,1);
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(tempLoc.getBlock());
                        if(customBlock != null){
                            if(customBlock.getNamespacedID().equals(ConfigReader.Basic.pot)){
                                CustomBlock.remove(tempLoc);
                                CustomBlock.place(ConfigReader.Basic.watered_pot, tempLoc);
                            }
                        }
                    }
                }
            }
            // -135 ~ -45
            else {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = clickedLocation.clone().add(-1, 0, i);
                    for (int j = 0; j < length; j++){
                        tempLoc.add(1,0,0);
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(tempLoc.getBlock());
                        if(customBlock != null){
                            if(customBlock.getNamespacedID().equals(ConfigReader.Basic.pot)){
                                CustomBlock.remove(tempLoc);
                                CustomBlock.place(ConfigReader.Basic.watered_pot, tempLoc);
                            }
                        }
                    }
                }
            }
        }
        else {
            // 45 ~ 135
            if (yaw > 45 && yaw < 135) {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = clickedLocation.clone().add(1, 0, i);
                    for (int j = 0; j < length; j++){
                        tempLoc.subtract(1,0,0);
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(tempLoc.getBlock());
                        if(customBlock != null){
                            if(customBlock.getNamespacedID().equals(ConfigReader.Basic.pot)){
                                CustomBlock.remove(tempLoc);
                                CustomBlock.place(ConfigReader.Basic.watered_pot, tempLoc);
                            }
                        }
                    }
                }
            }
            // -180 ~ -135 135~180
            else {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = clickedLocation.clone().add(i, 0, 1);
                    for (int j = 0; j < length; j++){
                        tempLoc.subtract(0,0,1);
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(tempLoc.getBlock());
                        if(customBlock != null){
                            if(customBlock.getNamespacedID().equals(ConfigReader.Basic.pot)){
                                CustomBlock.remove(tempLoc);
                                CustomBlock.place(ConfigReader.Basic.watered_pot, tempLoc);
                            }
                        }
                    }
                }
            }
        }
    }
}

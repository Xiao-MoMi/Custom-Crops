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
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.datamanager.CropManager;
import net.momirealms.customcrops.datamanager.PotManager;
import net.momirealms.customcrops.datamanager.SeasonManager;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.objects.fertilizer.QualityCrop;
import net.momirealms.customcrops.integrations.protection.Integration;
import net.momirealms.customcrops.limits.CropsPerChunk;
import net.momirealms.customcrops.limits.SprinklersPerChunk;
import net.momirealms.customcrops.listener.JoinAndQuit;
import net.momirealms.customcrops.objects.Crop;
import net.momirealms.customcrops.objects.SimpleLocation;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.WateringCan;
import net.momirealms.customcrops.objects.fertilizer.YieldIncreasing;
import net.momirealms.customcrops.requirements.PlantingCondition;
import net.momirealms.customcrops.requirements.Requirement;
import net.momirealms.customcrops.utils.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RightClickT implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        long time = System.currentTimeMillis();
        Player player = event.getPlayer();
        if (time - (JoinAndQuit.coolDown.getOrDefault(player, time - 200)) < 200) return;
        JoinAndQuit.coolDown.put(player, time);
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
            ItemStack itemStack = event.getItem();
            if (itemStack != null && itemStack.getType() != Material.AIR){
                NBTItem nbtItem = new NBTItem(itemStack);
                NBTCompound nbtCompound = nbtItem.getCompound("itemsadder");
                if (nbtCompound != null){
                    String id = nbtCompound.getString("id");
                    String namespace = nbtCompound.getString("namespace");
                    String itemNID = namespace + ":" + id;
                    if (id.endsWith("_seeds") && action == Action.RIGHT_CLICK_BLOCK && event.getBlockFace() == BlockFace.UP){
                        String cropName = StringUtils.remove(id, "_seeds");
                        Crop cropInstance = ConfigReader.CROPS.get(cropName);
                        if (cropInstance != null){
                            Block block = event.getClickedBlock();
                            CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                            if (customBlock == null) return;
                            String namespacedID = customBlock.getNamespacedID();
                            if (namespacedID.equals(ConfigReader.Basic.pot) || namespacedID.equals(ConfigReader.Basic.watered_pot)){
                                Location location = block.getLocation().add(0,1,0); //已+1
                                for (Integration integration : ConfigReader.Config.integration)
                                    if(!integration.canPlace(location, player)) return;
                                if(FurnitureUtil.isSprinkler(location.clone().add(0.5, 0.5, 0.5))) return;
                                PlantingCondition plantingCondition = new PlantingCondition(player, location);
                                if (cropInstance.getRequirements() != null)
                                    for (Requirement requirement : cropInstance.getRequirements())
                                        if (!requirement.canPlant(plantingCondition)) return;
                                Label_out:
                                if (ConfigReader.Season.enable && cropInstance.getSeasons() != null){
                                    if (!ConfigReader.Config.allWorld){
                                        for (String season : cropInstance.getSeasons())
                                            if (season.equals(SeasonManager.SEASON.get(location.getWorld().getName())))
                                                break Label_out;
                                    }else {
                                        for(String season : cropInstance.getSeasons())
                                            if (season.equals(SeasonManager.SEASON.get(ConfigReader.Config.referenceWorld)))
                                                break Label_out;
                                    }
                                    if(ConfigReader.Season.greenhouse){
                                        for(int i = 1; i <= ConfigReader.Season.range; i++){
                                            CustomBlock cb = CustomBlock.byAlreadyPlaced(location.clone().add(0,i,0).getBlock());
                                            if (cb != null)
                                                if(cb.getNamespacedID().equalsIgnoreCase(ConfigReader.Basic.glass))
                                                    break Label_out;
                                        }
                                    }
                                    if (ConfigReader.Config.nwSeason) AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.badSeason);
                                    if (ConfigReader.Config.pwSeason) return;
                                }
                                if (location.getBlock().getType() != Material.AIR) return;
                                if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                                if (CropsPerChunk.isLimited(location)){
                                    AdventureManager.playerMessage(player,ConfigReader.Message.prefix + ConfigReader.Message.crop_limit.replace("{max}", String.valueOf(ConfigReader.Config.cropLimit)));
                                    return;
                                }
                                SimpleLocation simpleLocation = LocUtil.fromLocation(location);
                                CropManager.RemoveCache.remove(simpleLocation);
                                CropManager.Cache.put(simpleLocation, player.getName());
                                CustomBlock.place((namespace + ":" + cropName + "_stage_1"), location);
                                AdventureManager.playerSound(player, ConfigReader.Sounds.plantSeedSource, ConfigReader.Sounds.plantSeedKey);
                            }
                        }else AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.not_configed);
                        return;
                    }
                    WateringCan wateringCan = ConfigReader.CANS.get(itemNID);
                    if (wateringCan != null){
                        int water = nbtItem.getInteger("WaterAmount");
                        List<Block> lineOfSight = player.getLineOfSight(null, 5);
                        for (Block block : lineOfSight) {
                            if (block.getType() == Material.WATER) {
                                if (wateringCan.getMax() > water){
                                    water += ConfigReader.Config.waterCanRefill;
                                    if (water > wateringCan.getMax()) water = wateringCan.getMax();
                                    nbtItem.setInteger("WaterAmount", water);
                                    player.getWorld().playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL,1,1);
                                    if (ConfigReader.Message.hasWaterInfo)
                                        AdventureManager.playerActionbar(player,
                                                (ConfigReader.Message.waterLeft +
                                                 ConfigReader.Message.waterFull.repeat(water) +
                                                 ConfigReader.Message.waterEmpty.repeat(wateringCan.getMax() - water) +
                                                 ConfigReader.Message.waterRight)
                                                .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                                .replace("{water}", String.valueOf(water)));
                                    if (ConfigReader.Basic.hasWaterLore){
                                        List<String> lores = nbtItem.getCompound("display").getStringList("Lore");
                                        lores.clear();
                                        String string =
                                                (ConfigReader.Basic.waterLeft +
                                                 ConfigReader.Basic.waterFull.repeat(water) +
                                                 ConfigReader.Basic.waterEmpty.repeat(wateringCan.getMax() - water) +
                                                 ConfigReader.Basic.waterRight)
                                                .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                                .replace("{water}", String.valueOf(water));
                                        ConfigReader.Basic.waterLore.forEach(lore -> lores.add(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(lore.replace("{water_info}", string)))));
                                    }
                                    if (ConfigReader.Config.hasParticle) player.getWorld().spawnParticle(Particle.WATER_SPLASH, block.getLocation().add(0.5,1, 0.5),15,0.1,0.1,0.1);
                                    itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
                                }
                                return;
                            }
                        }
                        if(action == Action.RIGHT_CLICK_BLOCK){
                            Block block = event.getClickedBlock();
                            CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                            if (customBlock == null) return;
                            for (Integration integration : ConfigReader.Config.integration)
                                if(!integration.canPlace(block.getLocation(), player)) return;
                            String namespacedID = customBlock.getNamespacedID();
                            if ((namespacedID.equals(ConfigReader.Basic.pot) || namespacedID.equals(ConfigReader.Basic.watered_pot)) && event.getBlockFace() == BlockFace.UP){
                                if (water > 0){
                                    nbtItem.setInteger("WaterAmount", --water);
                                    AdventureManager.playerSound(player, ConfigReader.Sounds.waterPotSource, ConfigReader.Sounds.waterPotKey);
                                    PotUtil.waterPot(wateringCan.getWidth(), wateringCan.getLength(), block.getLocation(), player.getLocation().getYaw());
                                }
                                if (ConfigReader.Message.hasWaterInfo)
                                    AdventureManager.playerActionbar(player,
                                            (ConfigReader.Message.waterLeft +
                                             ConfigReader.Message.waterFull.repeat(water) +
                                             ConfigReader.Message.waterEmpty.repeat(wateringCan.getMax() - water) +
                                             ConfigReader.Message.waterRight)
                                            .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                            .replace("{water}", String.valueOf(water)));
                                if (ConfigReader.Basic.hasWaterLore){
                                    List<String> lores = nbtItem.getCompound("display").getStringList("Lore");
                                    lores.clear();
                                    String string =
                                            (ConfigReader.Basic.waterLeft +
                                             ConfigReader.Basic.waterFull.repeat(water) +
                                             ConfigReader.Basic.waterEmpty.repeat(wateringCan.getMax() - water) +
                                             ConfigReader.Basic.waterRight)
                                            .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                            .replace("{water}", String.valueOf(water));
                                    ConfigReader.Basic.waterLore.forEach(lore -> lores.add(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(lore.replace("{water_info}", string)))));
                                }
                                itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
                            }
                            else if (namespacedID.contains("_stage_")){
                                if (water > 0) {
                                    nbtItem.setInteger("WaterAmount", --water);
                                    AdventureManager.playerSound(player, ConfigReader.Sounds.waterPotSource, ConfigReader.Sounds.waterPotKey);
                                    PotUtil.waterPot(wateringCan.getWidth(), wateringCan.getLength(), block.getLocation().subtract(0, 1, 0), player.getLocation().getYaw());
                                }
                                if (ConfigReader.Message.hasWaterInfo)
                                    AdventureManager.playerActionbar(player,
                                            (ConfigReader.Message.waterLeft +
                                             ConfigReader.Message.waterFull.repeat(water) +
                                             ConfigReader.Message.waterEmpty.repeat(wateringCan.getMax() - water) +
                                             ConfigReader.Message.waterRight)
                                            .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                            .replace("{water}", String.valueOf(water)));
                                if (ConfigReader.Basic.hasWaterLore){
                                    List<String> lores = nbtItem.getCompound("display").getStringList("Lore");
                                    lores.clear();
                                    String string =
                                            (ConfigReader.Basic.waterLeft +
                                             ConfigReader.Basic.waterFull.repeat(water) +
                                             ConfigReader.Basic.waterEmpty.repeat(wateringCan.getMax() - water) +
                                             ConfigReader.Basic.waterRight)
                                            .replace("{max_water}", String.valueOf(wateringCan.getMax()))
                                            .replace("{water}", String.valueOf(water));
                                    ConfigReader.Basic.waterLore.forEach(lore -> lores.add(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(lore.replace("{water_info}", string)))));
                                }
                                itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
                            }
                        }
                        return;
                    }
                    Fertilizer fertilizerConfig = ConfigReader.FERTILIZERS.get(id);
                    if (fertilizerConfig != null && action == Action.RIGHT_CLICK_BLOCK){
                        Block block = event.getClickedBlock();
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                        if (customBlock == null) return;
                        for (Integration integration : ConfigReader.Config.integration)
                            if(!integration.canPlace(block.getLocation(), player)) return;
                        String namespacedID = customBlock.getNamespacedID();
                        if (namespacedID.equals(ConfigReader.Basic.pot) || namespacedID.equals(ConfigReader.Basic.watered_pot)){
                            CustomBlock customBlockUp = CustomBlock.byAlreadyPlaced(block.getLocation().clone().add(0,1,0).getBlock());
                            if (customBlockUp != null){
                                if (fertilizerConfig.isBefore() && customBlockUp.getNamespacedID().contains("_stage_")){
                                    AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.beforePlant);
                                    return;
                                }else {
                                    if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                                    AdventureManager.playerSound(player, ConfigReader.Sounds.useFertilizerSource, ConfigReader.Sounds.useFertilizerKey);
                                    PotUtil.addFertilizer(fertilizerConfig, block.getLocation());
                                }
                            }else {
                                if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                                AdventureManager.playerSound(player, ConfigReader.Sounds.useFertilizerSource, ConfigReader.Sounds.useFertilizerKey);
                                PotUtil.addFertilizer(fertilizerConfig, block.getLocation());
                            }
                        }else if (namespacedID.contains("_stage_")){
                            if (!fertilizerConfig.isBefore()){
                                if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                                PotUtil.addFertilizer(fertilizerConfig, block.getLocation().subtract(0,1,0));
                                AdventureManager.playerSound(player, ConfigReader.Sounds.useFertilizerSource, ConfigReader.Sounds.useFertilizerKey);
                            }else {
                                AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.beforePlant);
                                return;
                            }
                        }
                        return;
                    }
                    Sprinkler sprinkler = ConfigReader.SPRINKLERS.get(itemNID);
                    if (sprinkler != null && action == Action.RIGHT_CLICK_BLOCK && event.getBlockFace() == BlockFace.UP){
                        Location location = event.getClickedBlock().getLocation();
                        for (Integration integration : ConfigReader.Config.integration)
                            if (!integration.canPlace(location, player)) return;
                        if (FurnitureUtil.isSprinkler(location.clone().add(0.5, 1.5, 0.5))) return;
                        if (SprinklersPerChunk.isLimited(location)){
                            AdventureManager.playerMessage(player, ConfigReader.Message.prefix + ConfigReader.Message.sprinkler_limit.replace("{max}", String.valueOf(ConfigReader.Config.sprinklerLimit)));
                            return;
                        }
                        Sprinkler sprinklerData = new Sprinkler(sprinkler.getRange(), 0);
                        sprinklerData.setPlayer(player.getName());
                        if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                        SimpleLocation simpleLocation = LocUtil.fromLocation(location.add(0,1,0));
                        SprinklerManager.Cache.put(simpleLocation, sprinklerData);
                        SprinklerManager.RemoveCache.remove(simpleLocation);
                        FurnitureUtil.placeFurniture(sprinkler.getNamespacedID_2(),location);
                        AdventureManager.playerSound(player, ConfigReader.Sounds.placeSprinklerSource, ConfigReader.Sounds.placeSprinklerKey);
                        return;
                    }
                    if (ConfigReader.Message.hasCropInfo && itemNID.equals(ConfigReader.Basic.soilDetector) && action == Action.RIGHT_CLICK_BLOCK){
                        Block block = event.getClickedBlock();
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                        if (customBlock == null) return;
                        for (Integration integration : ConfigReader.Config.integration) if(!integration.canPlace(block.getLocation(), player)) return;
                        String namespacedID = customBlock.getNamespacedID();
                        if (namespacedID.contains("_stage_")){
                            Location location = block.getLocation().subtract(0,1,0);
                            Fertilizer fertilizer = PotManager.Cache.get(LocUtil.fromLocation(location));
                            if (fertilizer != null){
                                Fertilizer config = ConfigReader.FERTILIZERS.get(fertilizer.getKey());
                                if (config == null){
                                    PotManager.Cache.remove(LocUtil.fromLocation(location));
                                    return;
                                }
                                HoloUtil.showHolo(
                                        ConfigReader.Message.cropText
                                        .replace("{fertilizer}", config.getName())
                                        .replace("{times}", String.valueOf(fertilizer.getTimes()))
                                        .replace("{max_times}", String.valueOf(config.getTimes())),
                                        player,
                                        location.add(0.5, ConfigReader.Message.cropOffset, 0.5),
                                        ConfigReader.Message.cropTime);
                            }
                        }
                        else if(namespacedID.equals(ConfigReader.Basic.pot) || namespacedID.equals(ConfigReader.Basic.watered_pot)){
                            Location location = block.getLocation();
                            Fertilizer fertilizer = PotManager.Cache.get(LocUtil.fromLocation(block.getLocation()));
                            if (fertilizer != null){
                                Fertilizer config = ConfigReader.FERTILIZERS.get(fertilizer.getKey());
                                if (config == null){
                                    PotManager.Cache.remove(LocUtil.fromLocation(location));
                                    return;
                                }
                                HoloUtil.showHolo(
                                        ConfigReader.Message.cropText
                                                .replace("{fertilizer}", config.getName())
                                                .replace("{times}", String.valueOf(fertilizer.getTimes()))
                                                .replace("{max_times}", String.valueOf(config.getTimes())),
                                        player,
                                        location.add(0.5,ConfigReader.Message.cropOffset,0.5),
                                        ConfigReader.Message.cropTime);
                            }
                        }
                    }
                }
                else if (ConfigReader.Config.boneMeal && itemStack.getType() == Material.BONE_MEAL && action == Action.RIGHT_CLICK_BLOCK){
                    Block block = event.getClickedBlock();
                    CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                    if (customBlock == null) return;
                    for (Integration integration : ConfigReader.Config.integration)
                        if(!integration.canPlace(block.getLocation(), player)) return;
                    String namespacedID = customBlock.getNamespacedID();
                    if (namespacedID.contains("_stage_") && !namespacedID.equals(ConfigReader.Basic.dead)){
                        int nextStage = Integer.parseInt(namespacedID.substring(namespacedID.length()-1)) + 1;
                        String next = StringUtils.chop(namespacedID) + nextStage;
                        if (CustomBlock.getInstance(next) != null){
                            Location location = block.getLocation();
                            if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
                            AdventureManager.playerSound(player, ConfigReader.Sounds.boneMealSource, ConfigReader.Sounds.boneMealKey);
                            if (Math.random() < ConfigReader.Config.boneMealChance){
                                CustomBlock.remove(location);
                                CustomBlock.place(next, location);
                                block.getWorld().spawnParticle(ConfigReader.Config.boneMealSuccess, location.add(0.5,0.3,0.5),5,0.2,0.2,0.2);
                            }
                        }
                    }
                }
                else if(ConfigReader.Config.rightClickHarvest && !ConfigReader.Config.needEmptyHand && action == Action.RIGHT_CLICK_BLOCK)
                    rightClickHarvest(event.getClickedBlock(), player);
            }
            else if (ConfigReader.Config.rightClickHarvest && action == Action.RIGHT_CLICK_BLOCK)
                rightClickHarvest(event.getClickedBlock(), player);
        }
    }

    /**
     * 右键收获判定
     * @param block 农作物方块
     * @param player 玩家
     */
    private void rightClickHarvest(Block block, Player player) {
        Location location = block.getLocation();
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        if (customBlock == null) return;
        for (Integration integration : ConfigReader.Config.integration)
            if (!integration.canBreak(location, player)) return;
        String namespacedID = customBlock.getNamespacedID();
        if (namespacedID.contains("_stage_")){
            if(namespacedID.equals(ConfigReader.Basic.dead)) return;
            String[] cropNameList = StringUtils.split(customBlock.getId(), "_");
            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
            if (CustomBlock.getInstance(StringUtils.chop(namespacedID) + nextStage) == null) {
                Crop cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                if (ConfigReader.Config.quality){
                    ThreadLocalRandom current = ThreadLocalRandom.current();
                    int random = current.nextInt(cropInstance.getMin(), cropInstance.getMax() + 1);
                    World world = location.getWorld();
                    Location itemLoc = location.clone().add(0.5,0.2,0.5);
                    Fertilizer fertilizer = PotManager.Cache.get(LocUtil.fromLocation(location.clone().subtract(0,1,0)));
                    List<String> commands = cropInstance.getCommands();
                    if (commands != null)
                        for (String command : commands)
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
                    if (ConfigReader.Config.skillXP != null && cropInstance.getSkillXP() != 0) ConfigReader.Config.skillXP.addXp(player, cropInstance.getSkillXP());
                    if (cropInstance.doesDropIALoot()) customBlock.getLoot().forEach(itemStack -> location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), itemStack));
                    if (cropInstance.getOtherLoots() != null) cropInstance.getOtherLoots().forEach(s -> location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), CustomStack.getInstance(s).getItemStack()));
                    if (fertilizer != null){
                        Fertilizer fConfig = ConfigReader.FERTILIZERS.get(fertilizer.getKey());
                        if (fConfig == null) return;
                        if (fConfig instanceof QualityCrop qualityCrop){
                            int[] weights = qualityCrop.getChance();
                            double weightTotal = weights[0] + weights[1] + weights[2];
                            for (int i = 0; i < random; i++){
                                double ran = Math.random();
                                if (ran < weights[0]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_1()).getItemStack());
                                else if(ran > 1 - weights[1]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_2()).getItemStack());
                                else world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_3()).getItemStack());
                            }
                        }else if (fConfig instanceof YieldIncreasing yieldIncreasing){
                            if (Math.random() < yieldIncreasing.getChance()){
                                random += yieldIncreasing.getBonus();
                            }
                            DropUtil.normalDrop(cropInstance, random, itemLoc, world);
                        }
                        else DropUtil.normalDrop(cropInstance, random, itemLoc, world);
                    }
                    else DropUtil.normalDrop(cropInstance, random, itemLoc, world);
                }
                else customBlock.getLoot().forEach(loot-> location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), loot));
                CustomBlock.remove(location);
                AdventureManager.playerSound(player, ConfigReader.Sounds.harvestSource, ConfigReader.Sounds.harvestKey);
                if(cropInstance.getReturnStage() != null){
                    CustomBlock.place(cropInstance.getReturnStage(), location);
                    SimpleLocation simpleLocation = LocUtil.fromLocation(location);
                    CropManager.RemoveCache.remove(simpleLocation);
                    CropManager.Cache.put(simpleLocation, player.getName());
                }
            }
        }
    }
}

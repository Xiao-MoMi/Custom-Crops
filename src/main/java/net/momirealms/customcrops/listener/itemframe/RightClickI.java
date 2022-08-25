package net.momirealms.customcrops.listener.itemframe;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomBlock;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.datamanager.CropManager;
import net.momirealms.customcrops.datamanager.PotManager;
import net.momirealms.customcrops.datamanager.SeasonManager;
import net.momirealms.customcrops.datamanager.SprinklerManager;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.integrations.protection.Integration;
import net.momirealms.customcrops.limits.CropsPerChunkEntity;
import net.momirealms.customcrops.limits.SprinklersPerChunk;
import net.momirealms.customcrops.listener.JoinAndQuit;
import net.momirealms.customcrops.objects.Crop;
import net.momirealms.customcrops.objects.SimpleLocation;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.WateringCan;
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

public class RightClickI implements Listener {

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
                                if (FurnitureUtil.getNamespacedID(location.clone().add(0.5,0.1,0.5)) != null) return;
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
                                if (CropsPerChunkEntity.isLimited(location)){
                                    AdventureManager.playerMessage(player,ConfigReader.Message.prefix + ConfigReader.Message.crop_limit.replace("{max}", String.valueOf(ConfigReader.Config.cropLimit)));
                                    return;
                                }
                                SimpleLocation simpleLocation = LocUtil.fromLocation(location);
                                CropManager.RemoveCache.remove(simpleLocation);
                                CropManager.Cache.put(simpleLocation, player.getName());
                                FurnitureUtil.placeCrop(namespace + ":" + cropName + "_stage_1", location);
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
                            String furniture = FurnitureUtil.getNamespacedID(block.getLocation().clone().add(0.5,1.1,0.5));
                            if (furniture != null){
                                if (fertilizerConfig.isBefore() && furniture.contains("_stage_")){
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
                        if(namespacedID.equals(ConfigReader.Basic.pot) || namespacedID.equals(ConfigReader.Basic.watered_pot)){
                            Location location = block.getLocation();
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
                                        location.add(0.5,ConfigReader.Message.cropOffset,0.5),
                                        ConfigReader.Message.cropTime);
                            }
                        }
                    }
                }
            }
        }
    }
}

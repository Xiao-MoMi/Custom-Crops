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

package net.momirealms.customcrops.integrations.customplugin;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.api.event.*;
import net.momirealms.customcrops.api.utils.CCSeason;
import net.momirealms.customcrops.api.utils.SeasonUtils;
import net.momirealms.customcrops.config.*;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.managers.CustomWorld;
import net.momirealms.customcrops.managers.listener.InteractListener;
import net.momirealms.customcrops.objects.Function;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.WaterCan;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.objects.requirements.PlayerCondition;
import net.momirealms.customcrops.objects.requirements.RequirementInterface;
import net.momirealms.customcrops.utils.AdventureUtil;
import net.momirealms.customcrops.utils.FurnitureUtil;
import net.momirealms.customcrops.utils.HologramUtil;
import net.momirealms.customcrops.utils.LimitationUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public abstract class HandlerP extends Function {

    protected CropManager cropManager;
    protected CustomInterface customInterface;
    private final InteractListener interactListener;
    protected HashMap<Player, Long> coolDown = new HashMap<>();

    public HandlerP(CropManager cropManager) {
        this.cropManager = cropManager;
        this.customInterface = cropManager.getCustomInterface();
        this.interactListener = new InteractListener(this);
    }

    @Override
    public void load() {
        super.load();
        Bukkit.getPluginManager().registerEvents(interactListener, CustomCrops.plugin);
    }

    @Override
    public void unload() {
        super.unload();
        HandlerList.unregisterAll(interactListener);
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        //null
    }

    public void onInteractSprinkler(Location location, Player player, @Nullable ItemStack itemStack, Sprinkler config) {

        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;

        Sprinkler sprinkler = customWorld.getSprinklerCache(location);
        if (sprinkler == null) {
            sprinkler = new Sprinkler(config.getKey(), config.getRange(), 0);
            customWorld.addSprinklerCache(location, sprinkler);
        }

        if (itemStack != null) {

            if (itemStack.getType() == Material.WATER_BUCKET) {

                WaterBucketFillSprinklerEvent waterBucketFillSprinklerEvent = new WaterBucketFillSprinklerEvent(player, itemStack);
                Bukkit.getPluginManager().callEvent(waterBucketFillSprinklerEvent);
                if (waterBucketFillSprinklerEvent.isCancelled()) {
                    return;
                }

                itemStack.setType(Material.BUCKET);
                int water = sprinkler.getWater() + MainConfig.waterBucketToSprinkler;
                if (water > config.getWater()) water =  config.getWater();
                sprinkler.setWater(water);

                if (SoundConfig.addWaterToSprinkler.isEnable()) {
                    AdventureUtil.playerSound(
                    player,
                    SoundConfig.addWaterToSprinkler.getSource(),
                    SoundConfig.addWaterToSprinkler.getKey(),
                    1,1
                    );
                }
            }
            else if (itemStack.getType() != Material.AIR) {

                String canID = customInterface.getItemID(itemStack);
                WaterCan canConfig = WaterCanConfig.CANS.get(canID);
                if (canConfig != null) {

                    NBTItem nbtItem = new NBTItem(itemStack);
                    int canWater = nbtItem.getInteger("WaterAmount");
                    if (canWater > 0) {

                        WaterCanFillSprinklerEvent waterCanFillSprinklerEvent = new WaterCanFillSprinklerEvent(player, nbtItem);
                        Bukkit.getPluginManager().callEvent(waterCanFillSprinklerEvent);
                        if (waterCanFillSprinklerEvent.isCancelled()) {
                            return;
                        }

                        nbtItem.setInteger("WaterAmount", --canWater);

                        int water = sprinkler.getWater() + MainConfig.wateringCanToSprinkler;
                        if (water > config.getWater()) water = config.getWater();
                        sprinkler.setWater(water);

                        if (SoundConfig.addWaterToSprinkler.isEnable()) {
                            AdventureUtil.playerSound(
                                    player,
                                    SoundConfig.addWaterToSprinkler.getSource(),
                                    SoundConfig.addWaterToSprinkler.getKey(),
                                    1,1
                            );
                        }

                        if (MainConfig.enableActionBar) {
                            AdventureUtil.playerActionbar(
                                    player,
                                    (MainConfig.actionBarLeft +
                                            MainConfig.actionBarFull.repeat(canWater) +
                                            MainConfig.actionBarEmpty.repeat(canConfig.getMax() - canWater) +
                                            MainConfig.actionBarRight)
                                            .replace("{max_water}", String.valueOf(canConfig.getMax()))
                                            .replace("{water}", String.valueOf(canWater))
                            );
                        }

                        if (MainConfig.enableWaterCanLore && !MainConfig.enablePacketLore) {
                            addWaterLore(nbtItem, canConfig, canWater);
                        }

                        itemStack.setItemMeta(nbtItem.getItem().getItemMeta());

                        if (MainConfig.enableWaterCanLore && MainConfig.enablePacketLore) {
                            player.updateInventory();
                        }
                    }
                }
            }
        }

        if (MainConfig.enableSprinklerInfo)
            HologramUtil.showHolo(
            (MainConfig.sprinklerLeft +
            MainConfig.sprinklerFull.repeat(sprinkler.getWater()) +
            MainConfig.sprinklerEmpty.repeat(config.getWater() - sprinkler.getWater()) +
            MainConfig.sprinklerRight)
            .replace("{max_water}", String.valueOf(config.getWater()))
            .replace("{water}", String.valueOf(sprinkler.getWater())),
            player,
            location.add(0, MainConfig.sprinklerInfoY - 1,0),
            MainConfig.sprinklerInfoDuration);
    }

    public boolean useSurveyor(Location potLoc, String id, Player player) {

        if (!id.equals(BasicItemConfig.soilSurveyor)) return false;

        CustomWorld customWorld = cropManager.getCustomWorld(potLoc.getWorld());
        if (customWorld == null) return false;
        Fertilizer fertilizer = customWorld.getFertilizerCache(potLoc);

        SurveyorUseEvent surveyorUseEvent = new SurveyorUseEvent(player, fertilizer, potLoc);
        Bukkit.getPluginManager().callEvent(surveyorUseEvent);
        if (surveyorUseEvent.isCancelled()) {
            return true;
        }

        if (fertilizer != null) {

            Fertilizer config = FertilizerConfig.FERTILIZERS.get(fertilizer.getKey());
            if (config == null) return true;

            if (SoundConfig.surveyor.isEnable()) {
                AdventureUtil.playerSound(
                player,
                SoundConfig.surveyor.getSource(),
                SoundConfig.surveyor.getKey(),
                0.5f,1
                );
            }

            HologramUtil.showHolo(
            MainConfig.fertilizerInfo
            .replace("{fertilizer}", fertilizer.getName())
            .replace("{times}", String.valueOf(fertilizer.getTimes()))
            .replace("{max_times}", String.valueOf(config.getTimes())),
            player,
            potLoc.add(0.5, MainConfig.fertilizerInfoY, 0.5),
            MainConfig.fertilizerInfoDuration);
        }
        return true;
    }

    public void onBreakUnripeCrop(Location location) {
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;
        customWorld.removeCropCache(location);
    }

    public void onBreakRipeCrop(Location location, Crop crop, Player player, boolean instant) {
        if (isInCoolDown(player, 50)) return;
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld != null) {
            customWorld.removeCropCache(location);
            Fertilizer fertilizer = customWorld.getFertilizerCache(location.clone().subtract(0,1,0));
            if (instant) {
                //To prevent some unhooked region plugin duplication
                Bukkit.getScheduler().runTaskLater(CustomCrops.plugin, ()-> {
                    if (location.getBlock().getType() != Material.AIR) return;
                    cropManager.proceedHarvest(crop, player, location, fertilizer, false);
                },1);
            }
            else {
                cropManager.proceedHarvest(crop, player, location, fertilizer, false);
            }
        }
        else if (MainConfig.dropLootsInAllWorlds) {
            if (instant) {
                Bukkit.getScheduler().runTaskLater(CustomCrops.plugin, ()-> {
                    if (location.getBlock().getType() != Material.AIR) return;
                    cropManager.proceedHarvest(crop, player, location, null, false);
                },1);
            }
            else {
                cropManager.proceedHarvest(crop, player, location, null, false);
            }
        }
    }

    public void removeScarecrowCache(Location location) {
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;
        customWorld.removeScarecrowCache(location);
    }

    public boolean placeSprinkler(String id, Location location, Player player, ItemStack item) {

        Sprinkler config = SprinklerConfig.SPRINKLERS_2D.get(id);
        if (config != null) {
            Location sprinklerLoc;
            if (MainConfig.OraxenHook) sprinklerLoc = location.clone().add(0.5, 1.03125, 0.5);
            else sprinklerLoc = location.clone().add(0.5, 1.5, 0.5);

            if (FurnitureUtil.hasFurniture(sprinklerLoc)) return false;
            Sprinkler sprinkler = new Sprinkler(config.getKey(), config.getRange(), 0);
            CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
            if (customWorld == null) return false;

            if (MainConfig.limitation && LimitationUtil.reachFrameLimit(location)) {
                AdventureUtil.playerMessage(player, MessageConfig.prefix + MessageConfig.limitFrame.replace("{max}", String.valueOf(MainConfig.frameAmount)));
                return true;
            }

            SprinklerPlaceEvent sprinklerPlaceEvent = new SprinklerPlaceEvent(player, sprinklerLoc);
            Bukkit.getPluginManager().callEvent(sprinklerPlaceEvent);
            if (sprinklerPlaceEvent.isCancelled()) {
                return true;
            }

            if (SoundConfig.placeSprinkler.isEnable()) {
                AdventureUtil.playerSound(
                player,
                SoundConfig.placeSprinkler.getSource(),
                SoundConfig.placeSprinkler.getKey(),
                1,1
                );
            }

            if (player.getGameMode() != GameMode.CREATIVE) item.setAmount(item.getAmount() - 1);
            customWorld.addSprinklerCache(sprinklerLoc, sprinkler);
            customInterface.placeFurniture(sprinklerLoc, config.getThreeD());
            return true;
        }
        return false;
    }

    public boolean fillWaterCan(String id, NBTItem nbtItem, ItemStack itemStack, Player player) {
        WaterCan config = WaterCanConfig.CANS.get(id);
        if (config != null) {
            int water = nbtItem.getInteger("WaterAmount");
            List<Block> lineOfSight = player.getLineOfSight(null, 5);

            for (Block block : lineOfSight) {
                if (block.getType() == Material.WATER) {
                    if (config.getMax() > water) {

                        water += MainConfig.waterToWaterCan;
                        if (water > config.getMax()) water = config.getMax();

                        WateringCanFillEvent wateringCanFillEvent = new WateringCanFillEvent(player, nbtItem, water);
                        Bukkit.getPluginManager().callEvent(wateringCanFillEvent);
                        if (wateringCanFillEvent.isCancelled()) {
                            return true;
                        }

                        water = wateringCanFillEvent.getCurrentWater();

                        if (water > config.getMax()) water = config.getMax();
                        nbtItem.setInteger("WaterAmount", water);

                        if (SoundConfig.addWaterToCan.isEnable()) {
                            AdventureUtil.playerSound(
                            player,
                            SoundConfig.addWaterToCan.getSource(),
                            SoundConfig.addWaterToCan.getKey(),
                            1,1
                            );
                        }

                        if (MainConfig.enableParticles) {
                            player.getWorld().spawnParticle(Particle.WATER_SPLASH, block.getLocation().add(0.5,1, 0.5),10,0.1,0.1,0.1);
                        }

                        if (MainConfig.enableWaterCanLore && !MainConfig.enablePacketLore) {
                            addWaterLore(nbtItem, config, water);
                        }

                        itemStack.setItemMeta(nbtItem.getItem().getItemMeta());

                        if (MainConfig.enableWaterCanLore && MainConfig.enablePacketLore) {
                            player.updateInventory();
                        }
                    }

                    break;
                }
            }

            if (MainConfig.enableActionBar) {
                AdventureUtil.playerActionbar(
                player,
                (MainConfig.actionBarLeft +
                MainConfig.actionBarFull.repeat(water) +
                MainConfig.actionBarEmpty.repeat(config.getMax() - water) +
                MainConfig.actionBarRight)
                .replace("{max_water}", String.valueOf(config.getMax()))
                .replace("{water}", String.valueOf(water))
                );
            }
            return true;
        }
        return false;
    }

    protected void addWaterLore(NBTItem nbtItem, WaterCan config, int water) {
        NBTCompound display = nbtItem.getCompound("display");
        List<String> lore = display.getStringList("Lore");
        lore.clear();
        for (String text : MainConfig.waterCanLore) {
            lore.add(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(
                    text.replace("{water_bar}",
                                    MainConfig.waterBarLeft +
                                            MainConfig.waterBarFull.repeat(water) +
                                            MainConfig.waterBarEmpty.repeat(config.getMax() - water) +
                                            MainConfig.waterBarRight
                            )
                            .replace("{water}", String.valueOf(water))
                            .replace("{max_water}", String.valueOf(config.getMax()))
            )));
        }
    }

    public boolean useFertilizer(Location potLoc, String id, Player player, ItemStack itemStack) {
        Fertilizer fertilizer = FertilizerConfig.FERTILIZERS.get(id);
        if (fertilizer == null) return false;
        CustomWorld customWorld = cropManager.getCustomWorld(potLoc.getWorld());
        if (customWorld == null) return false;

        if (fertilizer.isBefore()) {
            Location above;
            if (MainConfig.OraxenHook) above = potLoc.clone().add(0.5,1.03125,0.5);
            else above = potLoc.clone().add(0.5,1.5,0.5);

            if (FurnitureUtil.hasFurniture(above)) {
                AdventureUtil.playerMessage(player, MessageConfig.prefix + MessageConfig.beforePlant);
                return true;
            }
            if (above.getBlock().getType() == Material.TRIPWIRE) {
                AdventureUtil.playerMessage(player, MessageConfig.prefix + MessageConfig.beforePlant);
                return true;
            }
        }

        FertilizerUseEvent fertilizerUseEvent = new FertilizerUseEvent(player, fertilizer, potLoc);
        Bukkit.getPluginManager().callEvent(fertilizerUseEvent);
        if (fertilizerUseEvent.isCancelled()) {
            return true;
        }

        if (fertilizer.getParticle() != null) {
            potLoc.getWorld().spawnParticle(fertilizer.getParticle(), potLoc.clone().add(0.5,1.1,0.5), 5,0.25,0.1,0.25, 0);
        }

        if (SoundConfig.useFertilizer.isEnable()) {
            AdventureUtil.playerSound(
            player,
            SoundConfig.useFertilizer.getSource(),
            SoundConfig.useFertilizer.getKey(),
            1, 1
            );
        }

        if (player.getGameMode() != GameMode.CREATIVE) itemStack.setAmount(itemStack.getAmount() - 1);
        customWorld.addFertilizerCache(potLoc, fertilizer.getWithTimes(fertilizer.getTimes()));
        return true;
    }

    public void onBreakSprinkler(Location location) {
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;
        customWorld.removeSprinklerCache(location);
    }

    public void onBreakPot(Location location) {
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;
        //remove fertilizer
        customWorld.removeFertilizerCache(location);
        customWorld.removePotFromWatered(location);
    }

    public void onQuit(Player player) {
        coolDown.remove(player);
    }

    public void waterPot(int width, int length, Location location, float yaw){

        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;

        int extend = width / 2;
        if (yaw < 45 && yaw > -135) {
            if (yaw > -45) {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = location.clone().add(i, 0, -1);
                    for (int j = 0; j < length; j++){
                        tempLoc.add(0,0,1);
                        customWorld.setPlayerWatered(tempLoc);
                        waterPot(tempLoc);
                    }
                }
            }
            else {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = location.clone().add(-1, 0, i);
                    for (int j = 0; j < length; j++){
                        tempLoc.add(1,0,0);
                        customWorld.setPlayerWatered(tempLoc);
                        waterPot(tempLoc);
                    }
                }
            }
        }
        else {
            if (yaw > 45 && yaw < 135) {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = location.clone().add(1, 0, i);
                    for (int j = 0; j < length; j++){
                        tempLoc.subtract(1,0,0);
                        customWorld.setPlayerWatered(tempLoc);
                        waterPot(tempLoc);
                    }
                }
            }
            else {
                for (int i = -extend; i <= extend; i++) {
                    Location tempLoc = location.clone().add(i, 0, 1);
                    for (int j = 0; j < length; j++){
                        tempLoc.subtract(0,0,1);
                        customWorld.setPlayerWatered(tempLoc);
                        waterPot(tempLoc);
                    }
                }
            }
        }
    }

    private void waterPot(Location tempLoc) {
        String blockID = customInterface.getBlockID(tempLoc);
        if(blockID != null){
            if(blockID.equals(BasicItemConfig.dryPot)){
                customInterface.removeBlock(tempLoc);
                customInterface.placeNoteBlock(tempLoc, BasicItemConfig.wetPot);
                if (MainConfig.enableParticles) {
                    tempLoc.getWorld().spawnParticle(Particle.WATER_SPLASH, tempLoc.clone().add(0.5,1, 0.5),3,0.1,0.1,0.1);
                }
            }
        }
    }

    protected void onInteractRipeCrop(Location location, Crop crop, Player player) {
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld != null) {
            Fertilizer fertilizer = customWorld.getFertilizerCache(location.clone().subtract(0,1,0));
            cropManager.proceedHarvest(crop, player, location, fertilizer, true);
            String returnStage = crop.getReturnStage();
            if (returnStage == null) customWorld.removeCropCache(location);
            else customWorld.addCropCache(location, crop.getKey(), Integer.parseInt(returnStage.substring(returnStage.indexOf("_stage_") + 7)));
        }
        else if (MainConfig.dropLootsInAllWorlds) {
            cropManager.proceedHarvest(crop, player, location, null, true);
        }
    }

    public boolean plantSeed(Location seedLoc, String cropName, @Nullable Player player, @Nullable ItemStack itemInHand) {
        Crop crop = CropConfig.CROPS.get(cropName);
        if (crop == null) return false;

        CustomWorld customWorld = cropManager.getCustomWorld(seedLoc.getWorld());
        if (customWorld == null) return false;

        if (FurnitureUtil.hasFurniture(customInterface.getFrameCropLocation(seedLoc)) || seedLoc.getBlock().getType() != Material.AIR) return false;

        if (player != null) {
            PlayerCondition playerCondition = new PlayerCondition(seedLoc, player);
            if (crop.getPlantRequirements() != null) {
                for (RequirementInterface requirement : crop.getPlantRequirements()) {
                    if (!requirement.isConditionMet(playerCondition)) {
                        return false;
                    }
                }
            }
        }

        CCSeason[] seasons = crop.getSeasons();
        if (SeasonConfig.enable && seasons != null) {
            if (cropManager.isWrongSeason(seedLoc, seasons)) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < seasons.length; i++) {
                    if (i < seasons.length - 1) stringBuilder.append(SeasonUtils.getSeasonText(seasons[i])).append(", ");
                    else stringBuilder.append(SeasonUtils.getSeasonText(seasons[i]));
                }
                if (MainConfig.notifyInWrongSeason) AdventureUtil.playerMessage(player, MessageConfig.prefix + MessageConfig.wrongSeason.replace("{season}", SeasonUtils.getSeasonText(SeasonUtils.getSeason(seedLoc.getWorld())))
                        .replace("{suitable}", stringBuilder.toString()));
                if (MainConfig.preventInWrongSeason) return false;
            }
        }

        if (MainConfig.limitation) {
            if (MainConfig.cropMode) {
                if (LimitationUtil.reachWireLimit(seedLoc)) {
                    if (player != null) AdventureUtil.playerMessage(player, MessageConfig.prefix + MessageConfig.limitWire.replace("{max}", String.valueOf(MainConfig.wireAmount)));
                    return false;
                }
            }
            else {
                if (LimitationUtil.reachFrameLimit(seedLoc)) {
                    if (player != null) AdventureUtil.playerMessage(player, MessageConfig.prefix + MessageConfig.limitFrame.replace("{max}", String.valueOf(MainConfig.frameAmount)));
                    return false;
                }
            }
        }

        SeedPlantEvent seedPlantEvent = new SeedPlantEvent(player, seedLoc, crop);
        Bukkit.getPluginManager().callEvent(seedPlantEvent);
        if (seedPlantEvent.isCancelled()) {
            return false;
        }

        if (SoundConfig.plantSeed.isEnable() && player != null) {
            AdventureUtil.playerSound(
                    player,
                    SoundConfig.plantSeed.getSource(),
                    SoundConfig.plantSeed.getKey(),
                    1,1
            );
        }

        if (itemInHand != null && player != null && player.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
        if (MainConfig.cropMode) customInterface.placeWire(seedLoc,  CropConfig.namespace + cropName + "_stage_1");
        else {
            ItemFrame itemFrame = customInterface.placeFurniture(seedLoc, CropConfig.namespace + cropName + "_stage_1");
            if (itemFrame == null) return false;
            if (crop.canRotate()) itemFrame.setRotation(FurnitureUtil.getRandomRotation());
        }
        customWorld.addCropCache(seedLoc, cropName, 1);
        return true;
    }

    protected boolean useBucket(Location potLoc, Player player, ItemStack itemInHand) {
        if (itemInHand.getType() == Material.WATER_BUCKET) {
            WaterPotEvent waterPotEvent = new WaterPotEvent(player, potLoc, itemInHand, 0);
            Bukkit.getPluginManager().callEvent(waterPotEvent);
            if (waterPotEvent.isCancelled()) {
                return false;
            }
            itemInHand.setType(Material.BUCKET);
            waterPot(1,1, potLoc, 0);
            return true;
        }
        return false;
    }

    protected boolean isInCoolDown(Player player, int delay) {
        long time = System.currentTimeMillis();
        if (time - (coolDown.getOrDefault(player, time - delay)) < delay) return true;
        coolDown.put(player, time);
        return false;
    }

    public boolean isRipe(String id) {
        Crop crop = customInterface.getCropFromID(id);
        if (crop == null) return false;
        int stage = Integer.parseInt(id.substring(id.indexOf("_stage_") + 7));
        return stage == crop.getMax_stage();
    }

    protected boolean checkHarvestRequirements(Player player, Location location, Crop crop) {
        if (player.getGameMode() == GameMode.CREATIVE) return true;
        PlayerCondition playerCondition = new PlayerCondition(location, player);
        if (crop.getHarvestRequirements() != null) {
            for (RequirementInterface requirement : crop.getHarvestRequirements()) {
                if (!requirement.isConditionMet(playerCondition)) {
                    return false;
                }
            }
        }
        return true;
    }
}

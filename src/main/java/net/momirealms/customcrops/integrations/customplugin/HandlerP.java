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

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.Function;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.api.event.*;
import net.momirealms.customcrops.config.*;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.managers.CustomWorld;
import net.momirealms.customcrops.managers.listener.InteractListener;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.objects.WaterCan;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.utils.AdventureUtil;
import net.momirealms.customcrops.utils.FurnitureUtil;
import net.momirealms.customcrops.utils.HologramUtil;
import net.momirealms.customcrops.utils.LimitationUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
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

        Sprinkler sprinkler = customWorld.getSprinkler(location);
        if (sprinkler == null) {
            sprinkler = new Sprinkler(config.getKey(), config.getRange(), 0);
            customWorld.addSprinkler(location, sprinkler);
        }

        if (itemStack != null) {

            if (itemStack.getType() == Material.WATER_BUCKET) {

                SprinklerFillEvent sprinklerFillEvent = new SprinklerFillEvent(player, itemStack);
                Bukkit.getPluginManager().callEvent(sprinklerFillEvent);
                if (sprinklerFillEvent.isCancelled()) {
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
                NBTItem nbtItem = new NBTItem(itemStack);
                int canWater = nbtItem.getInteger("WaterAmount");
                if (canWater > 0) {

                    SprinklerFillEvent sprinklerFillEvent = new SprinklerFillEvent(player, itemStack);
                    Bukkit.getPluginManager().callEvent(sprinklerFillEvent);
                    if (sprinklerFillEvent.isCancelled()) {
                        return;
                    }

                    nbtItem.setInteger("WaterAmount", --canWater);
                    itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
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
                        String canID = customInterface.getItemID(itemStack);
                        WaterCan canConfig = WaterCanConfig.CANS.get(canID);
                        if (canConfig == null) return;

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
        Fertilizer fertilizer = customWorld.getFertilizer(potLoc);

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
        //remove crop cache
        customWorld.removeCrop(location);
    }

    public void onBreakRipeCrop(Location location, String id, Player player, boolean instant, boolean hasNamespace) {
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;
        //remove crop cache
        customWorld.removeCrop(location);

        String[] cropNameList;
        if (hasNamespace) cropNameList = StringUtils.split(StringUtils.split(id, ":")[1], "_");
        else cropNameList = StringUtils.split(id, "_");

        Crop crop = CropConfig.CROPS.get(cropNameList[0]);
        if (crop == null) return;

        Fertilizer fertilizer = customWorld.getFertilizer(location.clone().subtract(0,1,0));
        //double check to prevent dupe when there's no antiGrief integration
        if (instant) {
            Bukkit.getScheduler().runTaskLater(CustomCrops.plugin, ()-> {
                if (location.getBlock().getType() != Material.AIR) return;
                cropManager.proceedHarvest(crop, player, location, fertilizer);
            },1);
        }
        else {
            cropManager.proceedHarvest(crop, player, location, fertilizer);
        }
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
            customWorld.addSprinkler(sprinklerLoc, sprinkler);
            customInterface.placeFurniture(sprinklerLoc, config.getThreeD());

            return true;
        }
        return false;
    }

    public boolean hasNextStage(String id) {
        int nextStage = Integer.parseInt(id.substring(id.length()-1)) + 1;
        return customInterface.doesExist(StringUtils.chop(id) + nextStage);
    }

    public String getNextStage(String id) {
        int nextStage = Integer.parseInt(id.substring(id.length()-1)) + 1;
        return StringUtils.chop(id) + nextStage;
    }

    public void placeScareCrow(Location location) {
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;

    }

    public boolean fillWaterCan(String id, NBTItem nbtItem, ItemStack itemStack, Player player) {
        WaterCan config = WaterCanConfig.CANS.get(id);
        if (config != null) {
            int water = nbtItem.getInteger("WaterAmount");
            List<Block> lineOfSight = player.getLineOfSight(null, 5);

            for (Block block : lineOfSight) {
                if (block.getType() == Material.WATER) {
                    if (config.getMax() > water) {

                        WateringCanFillEvent wateringCanFillEvent = new WateringCanFillEvent(player, itemStack);
                        Bukkit.getPluginManager().callEvent(wateringCanFillEvent);
                        if (wateringCanFillEvent.isCancelled()) {
                            return true;
                        }

                        water += MainConfig.waterToWaterCan;
                        if (water > config.getMax()) water = config.getMax();
                        nbtItem.setInteger("WaterAmount", water);
                        itemStack.setItemMeta(nbtItem.getItem().getItemMeta());

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
        customWorld.addFertilizer(potLoc, fertilizer.getWithTimes(fertilizer.getTimes()));
        return true;
    }

    public void onBreakSprinkler(Location location) {
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;
        customWorld.removeSprinkler(location);
    }

    public void onBreakPot(Location location) {
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;
        //remove fertilizer
        customWorld.removeFertilizer(location);
        customWorld.removeWatered(location);
    }

    public void onQuit(Player player) {
        coolDown.remove(player);
    }

    public void waterPot(int width, int length, Location location, float yaw){
        //TODO
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
}

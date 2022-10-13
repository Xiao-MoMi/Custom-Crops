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

package net.momirealms.customcrops.managers;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.Function;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.api.event.CropHarvestEvent;
import net.momirealms.customcrops.config.BasicItemConfig;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.SeasonConfig;
import net.momirealms.customcrops.config.SoundConfig;
import net.momirealms.customcrops.integrations.customplugin.CustomInterface;
import net.momirealms.customcrops.integrations.customplugin.HandlerP;
import net.momirealms.customcrops.integrations.customplugin.itemsadder.*;
import net.momirealms.customcrops.integrations.customplugin.oraxen.*;
import net.momirealms.customcrops.integrations.season.CCSeason;
import net.momirealms.customcrops.integrations.season.InternalSeason;
import net.momirealms.customcrops.integrations.season.RealisticSeasonsHook;
import net.momirealms.customcrops.integrations.season.SeasonInterface;
import net.momirealms.customcrops.managers.listener.*;
import net.momirealms.customcrops.managers.timer.CrowTask;
import net.momirealms.customcrops.managers.timer.TimerTask;
import net.momirealms.customcrops.objects.OtherLoot;
import net.momirealms.customcrops.objects.QualityLoot;
import net.momirealms.customcrops.objects.QualityRatio;
import net.momirealms.customcrops.objects.actions.ActionInterface;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.objects.fertilizer.QualityCrop;
import net.momirealms.customcrops.objects.fertilizer.RetainingSoil;
import net.momirealms.customcrops.objects.fertilizer.YieldIncreasing;
import net.momirealms.customcrops.utils.AdventureUtil;
import net.momirealms.customcrops.utils.ArmorStandUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class CropManager extends Function {

    private ItemSpawnListener itemSpawnListener;
    private WorldListener worldListener;
    private TimerTask timerTask;
    private ConcurrentHashMap<World, CustomWorld> customWorlds;
    private CropModeInterface cropMode;
    private SeasonInterface seasonInterface;
    private CustomInterface customInterface;
    private ArmorStandUtil armorStandUtil;
    private ContainerListener containerListener;
    private PlayerModeListener playerModeListener;
    private PlayerContainerListener playerContainerListener;
    private HandlerP handler;

    public CropManager() {
        load();
    }

    @Override
    public void load() {
        super.load();
        this.customWorlds = new ConcurrentHashMap<>();
        this.itemSpawnListener = new ItemSpawnListener(this);
        this.worldListener = new WorldListener(this);
        this.armorStandUtil = new ArmorStandUtil(this);

        loadMode();
        loadSeason();
        loadPacket();

        //load Worlds
        for (World world : Bukkit.getWorlds()) {
            onWorldLoad(world);
        }
        //new Time Check task
        this.timerTask = new TimerTask(this);
        this.timerTask.runTaskTimerAsynchronously(CustomCrops.plugin, 1,100);
    }

    public void loadMode() {

        if (this.handler != null) {
            handler.unload();
            handler = null;
        }

        //Custom Plugin
        if (MainConfig.customPlugin.equals("itemsadder")) {
            customInterface = new ItemsAdderHook();
            if (MainConfig.cropMode) {
                this.handler = new ItemsAdderWireHandler(this);
                this.cropMode = new ItemsAdderWireCropImpl(this);
                this.handler.load();
            }
            else {
                this.handler = new ItemsAdderFrameHandler(this);
                this.cropMode = new ItemsAdderFrameCropImpl(this);
                this.handler.load();
            }
        }
        else if (MainConfig.customPlugin.equals("oraxen")){
            customInterface = new OraxenHook();
            if (MainConfig.cropMode) {
                this.handler = new OraxenWireHandler(this);
                this.cropMode = new OraxenWireCropImpl(this);
                this.handler.load();
            }
            else {
                this.handler = new OraxenFrameHandler(this);
                this.cropMode = new OraxenFrameCropImpl(this);
                this.handler.load();
            }
        }
    }

    public void loadSeason() {
        if (SeasonConfig.enable) {
            if (MainConfig.realisticSeasonHook) seasonInterface = new RealisticSeasonsHook();
            else seasonInterface = new InternalSeason();
            seasonInterface.load();
        }
        else if (this.seasonInterface != null) {
            this.seasonInterface.unload();
            this.seasonInterface = null;
        }
    }

    public void loadPacket() {
        if (this.containerListener != null) {
            CustomCrops.protocolManager.removePacketListener(containerListener);
            this.containerListener = null;
        }
        if (this.playerModeListener != null) {
            HandlerList.unregisterAll(playerModeListener);
            this.playerModeListener = null;
        }
        if (this.playerContainerListener != null) {
            CustomCrops.protocolManager.removePacketListener(playerContainerListener);
            this.playerContainerListener = null;
        }
        if (!MainConfig.enableWaterCanLore || !MainConfig.enablePacketLore) return;
        containerListener = new ContainerListener(this);
        CustomCrops.protocolManager.addPacketListener(containerListener);
        playerContainerListener = new PlayerContainerListener(handler);
        CustomCrops.protocolManager.addPacketListener(playerContainerListener);
        playerModeListener = new PlayerModeListener();
        Bukkit.getPluginManager().registerEvents(playerModeListener, CustomCrops.plugin);
    }

    @Override
    public void unload() {
        super.unload();
        HandlerList.unregisterAll(this.itemSpawnListener);
        HandlerList.unregisterAll(this.worldListener);
        if (this.handler != null) handler.unload();
        if (this.timerTask != null) this.timerTask.cancel();
        for (CustomWorld customWorld : customWorlds.values()) {
            customWorld.unload(true);
        }
        customWorlds.clear();
        if (this.seasonInterface != null) seasonInterface.unload();
        if (this.containerListener != null) CustomCrops.protocolManager.removePacketListener(containerListener);
    }

    public void onItemSpawn(Item item) {
        String id = customInterface.getItemID(item.getItemStack());
        if (id == null) return;
        if (id.contains("_stage_")) item.remove();
    }

    public void onWorldLoad(World world) {
        CustomWorld cw = customWorlds.get(world);
        if (cw != null) return;
        if (MainConfig.getWorldsList().contains(world)) {
            CustomWorld customWorld = new CustomWorld(world, this);
            customWorlds.put(world, customWorld);
            if (MainConfig.autoGrow && MainConfig.enableCompensation) {
                if (world.getTime() > 1200) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(CustomCrops.plugin, () -> grow(world, MainConfig.timeToGrow, MainConfig.timeToWork, MainConfig.timeToDry, true), 100);
                }
            }
        }
    }

    public void onWorldUnload(World world, boolean disable) {
        CustomWorld customWorld = customWorlds.get(world);
        if (customWorld == null) return;
        customWorld.unload(disable);
        customWorlds.remove(world);
        seasonInterface.unloadWorld(world);
    }

    public void grow(World world, int cropTime, int sprinklerTime, int dryTime, boolean compensation) {
        CustomWorld customWorld = customWorlds.get(world);
        if (customWorld == null) return;
        if (MainConfig.cropMode) customWorld.growWire(cropTime, sprinklerTime, dryTime, compensation);
        else customWorld.growFrame(cropTime, sprinklerTime, dryTime, compensation);
    }

    public CropModeInterface getCropMode() {
        return cropMode;
    }

    public SeasonInterface getSeasonAPI() {
        return seasonInterface;
    }

    public boolean hasGlass(Location location) {
        for(int i = 1; i <= SeasonConfig.effectiveRange; i++){
            String blockID = customInterface.getBlockID(location.clone().add(0,i,0));
            if (blockID != null && blockID.equals(BasicItemConfig.greenHouseGlass)) return true;
        }
        return false;
    }

    public boolean hasScarecrow(Location location) {
        CustomWorld customWorld = customWorlds.get(location.getWorld());
        if (customWorld == null) return true;
        return customWorld.hasScarecrow(location);
    }

    public CustomInterface getCustomInterface() {
        return customInterface;
    }

    public boolean isWrongSeason(Location location, CCSeason[] seasonList) {
        if (!SeasonConfig.enable) return false;
        if (seasonInterface.isWrongSeason(location.getWorld(), seasonList)) {
            if (SeasonConfig.greenhouse) return !hasGlass(location);
            else return true;
        }
        return false;
    }

    @Nullable
    public Fertilizer getFertilizer(Location potLoc) {
        World world = potLoc.getWorld();
        CustomWorld customWorld = customWorlds.get(world);
        if (customWorld == null) return null;
        return customWorld.getFertilizer(potLoc);
    }

    public void potDryJudge(Location potLoc) {
        World world = potLoc.getWorld();
        CustomWorld customWorld = customWorlds.get(world);
        if (customWorld == null) return;
        if (!customWorld.isPotWet(potLoc)) {
            makePotDry(potLoc);
            return;
        }
        Fertilizer fertilizer = customWorld.getFertilizer(potLoc);
        if (!(fertilizer instanceof RetainingSoil retainingSoil && Math.random() < retainingSoil.getChance())) {
            makePotDry(potLoc);
        }
    }

    public void makePotDry(Location potLoc) {
        String potID = customInterface.getBlockID(potLoc);
        if (potID == null) return;
        if (!potID.equals(BasicItemConfig.wetPot)) return;
        customInterface.removeBlock(potLoc);
        customInterface.placeNoteBlock(potLoc, BasicItemConfig.dryPot);
    }

    public void makePotWet(Location potLoc) {
        String potID = customInterface.getBlockID(potLoc);
        if (potID == null) return;
        if (!potID.equals(BasicItemConfig.dryPot)) return;
        Bukkit.getScheduler().runTask(CustomCrops.plugin, () -> {
            customInterface.removeBlock(potLoc);
            customInterface.placeNoteBlock(potLoc, BasicItemConfig.wetPot);
        });
    }

    @Nullable
    public CustomWorld getCustomWorld(World world) {
        return customWorlds.get(world);
    }

    public void proceedHarvest(Crop crop, Player player, Location location, @Nullable Fertilizer fertilizer) {
        //Call harvest event
        CropHarvestEvent cropHarvestEvent = new CropHarvestEvent(player, crop, location, fertilizer);
        Bukkit.getPluginManager().callEvent(cropHarvestEvent);
        if (cropHarvestEvent.isCancelled()) return;

        ActionInterface[] actions = crop.getActions();
        if (actions != null) performActions(actions, player);

        if (SoundConfig.harvestCrop.isEnable()) {
            AdventureUtil.playerSound(
                    player,
                    SoundConfig.harvestCrop.getSource(),
                    SoundConfig.harvestCrop.getKey(),
                    1,1
            );
        }

        if (player.getGameMode() == GameMode.CREATIVE) return;

        QualityLoot qualityLoot = crop.getQualityLoot();
        if (qualityLoot != null) {
            int amount = ThreadLocalRandom.current().nextInt(qualityLoot.getMin(), qualityLoot.getMax() + 1);
            QualityRatio qualityRatio = null;
            if (fertilizer instanceof YieldIncreasing yieldIncreasing) {
                if (Math.random() < yieldIncreasing.getChance()) {
                    amount += yieldIncreasing.getBonus();
                }
            }
            else if (fertilizer instanceof QualityCrop qualityCrop) {
                if (Math.random() < qualityCrop.getChance()) {
                    qualityRatio = qualityCrop.getQualityRatio();
                }
            }
            dropQualityLoots(qualityLoot, amount, location.getBlock().getLocation(), qualityRatio);
        }
        OtherLoot[] otherLoots = crop.getOtherLoots();
        if (otherLoots != null) dropOtherLoots(otherLoots, location.getBlock().getLocation());
    }

    public void performActions(ActionInterface[] actions, Player player) {
        for (ActionInterface action : actions) {
            action.performOn(player);
        }
    }

    public void dropOtherLoots(OtherLoot[] otherLoots, Location location) {
        for (OtherLoot otherLoot : otherLoots) {
            if (Math.random() < otherLoot.getChance()) {
                int random = ThreadLocalRandom.current().nextInt(otherLoot.getMin(), otherLoot.getMax() + 1);
                ItemStack drop = customInterface.getItemStack(otherLoot.getItemID());
                if (drop == null) continue;
                drop.setAmount(random);
                location.getWorld().dropItemNaturally(location, drop);
            }
        }
    }

    public void dropQualityLoots(QualityLoot qualityLoot, int amount, Location location, @Nullable QualityRatio qualityRatio) {
        if (qualityRatio == null) qualityRatio = MainConfig.qualityRatio;
        for (int i = 0; i < amount; i++) {
            double random = Math.random();
            World world = location.getWorld();
            if (random < qualityRatio.getQuality_1()) {
                ItemStack drop = customInterface.getItemStack(qualityLoot.getQuality_1());
                if (drop == null) continue;
                world.dropItemNaturally(location, drop);
            }
            else if(random > qualityRatio.getQuality_2()){
                ItemStack drop = customInterface.getItemStack(qualityLoot.getQuality_2());
                if (drop == null) continue;
                world.dropItemNaturally(location, drop);
            }
            else {
                ItemStack drop = customInterface.getItemStack(qualityLoot.getQuality_3());
                if (drop == null) continue;
                world.dropItemNaturally(location, drop);
            }
        }
    }

    public boolean crowJudge(Location location, ItemFrame itemFrame) {
        if (Math.random() < MainConfig.crowChance && !hasScarecrow(location)) {
            for (Player player : location.getNearbyPlayers(48)) {
                CrowTask crowTask = new CrowTask(player, location.clone().add(0.4,0,0.4), getArmorStandUtil());
                crowTask.runTaskTimerAsynchronously(CustomCrops.plugin, 1, 1);
            }
            Bukkit.getScheduler().runTaskLater(CustomCrops.plugin, () -> {
                customInterface.removeFurniture(itemFrame);
            }, 125);
            return true;
        }
        return false;
    }

    public boolean crowJudge(Location location) {
        if (Math.random() < MainConfig.crowChance && !hasScarecrow(location)) {
            Bukkit.getScheduler().runTask(CustomCrops.plugin, () -> {
                for (Player player : location.getNearbyPlayers(48)) {
                    CrowTask crowTask = new CrowTask(player, location.clone().add(0.4,0,0.4), getArmorStandUtil());
                    crowTask.runTaskTimerAsynchronously(CustomCrops.plugin, 1, 1);
                }
            });
            Bukkit.getScheduler().runTaskLater(CustomCrops.plugin, () -> {
                customInterface.removeBlock(location);
            }, 125);
            return true;
        }
        return false;
    }

    public ArmorStandUtil getArmorStandUtil() {
        return armorStandUtil;
    }
}
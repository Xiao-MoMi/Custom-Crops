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

import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.Function;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.api.event.CropHarvestEvent;
import net.momirealms.customcrops.config.BasicItemConfig;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.SeasonConfig;
import net.momirealms.customcrops.integrations.customplugin.CustomInterface;
import net.momirealms.customcrops.integrations.customplugin.HandlerP;
import net.momirealms.customcrops.integrations.customplugin.itemsadder.ItemsAdderFrameHandler;
import net.momirealms.customcrops.integrations.customplugin.itemsadder.ItemsAdderHook;
import net.momirealms.customcrops.integrations.customplugin.itemsadder.ItemsAdderWireHandler;
import net.momirealms.customcrops.integrations.customplugin.oraxen.OraxenFrameHandler;
import net.momirealms.customcrops.integrations.customplugin.oraxen.OraxenHook;
import net.momirealms.customcrops.integrations.customplugin.oraxen.OraxenWireHandler;
import net.momirealms.customcrops.integrations.season.CCSeason;
import net.momirealms.customcrops.integrations.season.InternalSeason;
import net.momirealms.customcrops.integrations.season.RealisticSeasonsHook;
import net.momirealms.customcrops.integrations.season.SeasonInterface;
import net.momirealms.customcrops.managers.listener.ItemSpawnListener;
import net.momirealms.customcrops.managers.listener.WorldListener;
import net.momirealms.customcrops.managers.timer.TimerTask;
import net.momirealms.customcrops.objects.OtherLoot;
import net.momirealms.customcrops.objects.QualityLoot;
import net.momirealms.customcrops.objects.QualityRatio;
import net.momirealms.customcrops.objects.actions.ActionInterface;
import net.momirealms.customcrops.objects.fertilizer.Fertilizer;
import net.momirealms.customcrops.objects.fertilizer.QualityCrop;
import net.momirealms.customcrops.objects.fertilizer.YieldIncreasing;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
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

        //new Time Check task
        this.timerTask = new TimerTask(this);
        if (MainConfig.asyncTimeCheck) this.timerTask.runTaskTimerAsynchronously(CustomCrops.plugin, 1, 100);
        else this.timerTask.runTaskTimer(CustomCrops.plugin, 1,100);

        //Crop mode
        if (MainConfig.cropMode.equalsIgnoreCase("tripwire")) this.cropMode = new WireCropImpl(this);
        else this.cropMode = new FrameCropImpl(this);

        if (MainConfig.customPlugin.equals("itemsadder")) {
            customInterface = new ItemsAdderHook();
            if (MainConfig.cropMode.equalsIgnoreCase("tripwire")) this.handler = new ItemsAdderWireHandler(this);
            else this.handler = new ItemsAdderFrameHandler(this);
        }
        else if (MainConfig.customPlugin.equals("oraxen")){
            customInterface = new OraxenHook();
            if (MainConfig.cropMode.equalsIgnoreCase("tripwire")) this.handler = new OraxenWireHandler(this);
            else this.handler = new OraxenFrameHandler(this);
        }

        handler.load();

        if (SeasonConfig.enable) {
            if (MainConfig.realisticSeasonHook) seasonInterface = new RealisticSeasonsHook();
            else seasonInterface = new InternalSeason();
        }

        //load Worlds
        for (World world : Bukkit.getWorlds()) {
            onWorldLoad(world);
        }
    }

    @Override
    public void unload() {
        super.unload();
        HandlerList.unregisterAll(this.itemSpawnListener);
        HandlerList.unregisterAll(this.worldListener);
        if (this.handler != null) handler.unload();
        this.timerTask.cancel();
        for (CustomWorld customWorld : customWorlds.values()) {
            customWorld.unload(true);
        }
        customWorlds.clear();
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
        }
    }

    public void onWorldUnload(World world, boolean disable) {
        CustomWorld customWorld = customWorlds.get(world);
        if (customWorld == null) return;
        customWorld.unload(disable);
        customWorlds.remove(world);
        seasonInterface.unloadWorld(world);
    }

    public void grow(World world, int time) {
        CustomWorld customWorld = customWorlds.get(world);
        if (customWorld == null) return;
        if (MainConfig.cropMode.equals("tripwire")) customWorld.growWire(time);
        else customWorld.growFrame(time);
    }

    public CropModeInterface getCropMode() {
        return cropMode;
    }

    public SeasonInterface getSeasonAPI() {
        return seasonInterface;
    }

    public boolean hasGlass(Location location) {
        for(int i = 1; i <= SeasonConfig.effectiveRange; i++){
            String blockID = customInterface.getBlockID(location);
            if (blockID != null && blockID.equals(BasicItemConfig.greenHouseGlass)) return true;
        }
        return false;
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
        if (customWorld == null) {
            makePotDry(potLoc);
        }
        else if (!customWorld.isPotWet(potLoc)) {
            makePotDry(potLoc);
        }
    }

    private void makePotDry(Location potLoc) {
        customInterface.removeBlock(potLoc);
        customInterface.placeNoteBlock(potLoc, BasicItemConfig.dryPot);
    }

    public void makePotWet(Location potLoc) {
        String potID = customInterface.getBlockID(potLoc);
        if (potID == null) return;
        if (!potID.equals(BasicItemConfig.dryPot)) return;
        customInterface.removeBlock(potLoc);
        customInterface.placeNoteBlock(potLoc, BasicItemConfig.wetPot);
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

        if (player.getGameMode() == GameMode.CREATIVE) return;

        Location itemLoc = location.clone().add(0.5,0.2,0.5);

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
            dropQualityLoots(qualityLoot, amount, itemLoc, qualityRatio);
        }
        OtherLoot[] otherLoots = crop.getOtherLoots();
        if (otherLoots != null) dropOtherLoots(otherLoots, itemLoc);
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
                ItemStack drop = CustomStack.getInstance(otherLoot.getItemID()).getItemStack();
                drop.setAmount(random);
                location.getWorld().dropItem(location, drop);
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
                world.dropItem(location, drop);
            }
            else if(random > qualityRatio.getQuality_2()){
                ItemStack drop = customInterface.getItemStack(qualityLoot.getQuality_2());
                if (drop == null) continue;
                world.dropItem(location, drop);
            }
            else {
                ItemStack drop = customInterface.getItemStack(qualityLoot.getQuality_3());
                if (drop == null) continue;
                world.dropItem(location, drop);
            }
        }
    }
}
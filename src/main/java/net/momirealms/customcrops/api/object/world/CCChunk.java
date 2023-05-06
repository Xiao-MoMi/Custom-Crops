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

package net.momirealms.customcrops.api.object.world;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.crop.GrowingCrop;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.sprinkler.Sprinkler;
import net.momirealms.customcrops.api.util.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Farmland;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class CCChunk implements Serializable {

    @Serial
    private static final long serialVersionUID = 5300805317167684402L;

    private final ConcurrentHashMap<SimpleLocation, GrowingCrop> growingCropMap;
    private final ConcurrentHashMap<SimpleLocation, Pot> potMap;
    private final ConcurrentHashMap<SimpleLocation, Sprinkler> sprinklerMap;
    private final Set<SimpleLocation> greenhouseSet;
    private final Set<SimpleLocation> scarecrowSet;

    public CCChunk() {
        this.growingCropMap = new ConcurrentHashMap<>(64);
        this.potMap = new ConcurrentHashMap<>(64);
        this.sprinklerMap = new ConcurrentHashMap<>(16);
        this.greenhouseSet = Collections.synchronizedSet(new HashSet<>(64));
        this.scarecrowSet = Collections.synchronizedSet(new HashSet<>(4));
    }

    public void removeCropData(SimpleLocation simpleLocation) {
        growingCropMap.remove(simpleLocation);
    }

    public void addCropData(SimpleLocation simpleLocation, GrowingCrop growingCrop) {
        growingCropMap.put(simpleLocation, growingCrop);
    }

    @Nullable
    public GrowingCrop getCropData(SimpleLocation simpleLocation) {
        return growingCropMap.get(simpleLocation);
    }

    public int getCropAmount() {
        return growingCropMap.size();
    }

    @Nullable
    public Pot getPotData(SimpleLocation simpleLocation) {
        return potMap.get(simpleLocation);
    }

    public void addPotData(SimpleLocation simpleLocation, Pot pot) {
        potMap.put(simpleLocation, pot);
    }

    public void removePotData(SimpleLocation simpleLocation) {
        potMap.remove(simpleLocation);
    }

    public void addGreenhouse(SimpleLocation simpleLocation) {
        greenhouseSet.add(simpleLocation);
    }

    public void removeGreenhouse(SimpleLocation simpleLocation) {
        greenhouseSet.remove(simpleLocation);
    }

    public boolean isGreenhouse(SimpleLocation simpleLocation) {
        return greenhouseSet.contains(simpleLocation);
    }

    public void addScarecrow(SimpleLocation simpleLocation) {
        scarecrowSet.add(simpleLocation);
    }

    public void removeScarecrow(SimpleLocation simpleLocation) {
        scarecrowSet.remove(simpleLocation);
    }

    public boolean hasScarecrow() {
        return scarecrowSet.size() != 0;
    }

    public boolean isUseless() {
        return growingCropMap.size() == 0 && potMap.size() == 0 && greenhouseSet.size() == 0 && sprinklerMap.size() == 0 && scarecrowSet.size() == 0;
    }

    @Nullable
    public Sprinkler getSprinklerData(SimpleLocation simpleLocation) {
        return sprinklerMap.get(simpleLocation);
    }

    public void removeSprinklerData(SimpleLocation simpleLocation) {
        sprinklerMap.remove(simpleLocation);
    }

    public void addSprinklerData(SimpleLocation simpleLocation, Sprinkler sprinkler) {
        sprinklerMap.put(simpleLocation, sprinkler);
    }

    public void addWaterToPot(SimpleLocation simpleLocation, int amount, @Nullable String pot_id) {
        Pot pot = potMap.get(simpleLocation);
        if (pot != null) {
            if (pot.addWater(amount)) {
                CustomCrops.getInstance().getScheduler().callSyncMethod(() -> {
                    changePotModel(simpleLocation, pot);
                    return null;
                });
            }
            return;
        }
        if (pot_id == null) {
            Location bukkitLoc = simpleLocation.getBukkitLocation();
            if (bukkitLoc == null) return;
            String id = CustomCrops.getInstance().getPlatformInterface().getCustomBlockID(bukkitLoc);
            if (id != null) {
                pot_id = CustomCrops.getInstance().getPotManager().getPotKeyByBlockID(id);
            } else {
                return;
            }
        }

        Pot newPot = new Pot(pot_id, null, amount);
        potMap.put(simpleLocation, newPot);
        CustomCrops.getInstance().getScheduler().callSyncMethod(() -> {
            changePotModel(simpleLocation, newPot);
            return null;
        });
    }

    public void addFertilizerToPot(SimpleLocation simpleLocation, Fertilizer fertilizer, @NotNull String pot_id) {
        Pot pot = potMap.get(simpleLocation);
        if (pot != null) {
            pot.setFertilizer(fertilizer);
            CustomCrops.getInstance().getScheduler().callSyncMethod(() -> {
                changePotModel(simpleLocation, pot);
                return null;
            });
        } else {
            Pot newPot = new Pot(pot_id, fertilizer, 0);
            potMap.put(simpleLocation, newPot);
            CustomCrops.getInstance().getScheduler().callSyncMethod(() -> {
                changePotModel(simpleLocation, newPot);
                return null;
            });
        }
    }

    public void scheduleGrowTask(CCWorld ccWorld) {
        Random randomGenerator = ThreadLocalRandom.current();
        int delay = ConfigManager.pointGainInterval * 1000;
        for (SimpleLocation simpleLocation : growingCropMap.keySet()) {
            ccWorld.pushCropTask(simpleLocation, randomGenerator.nextInt(delay));
        }
    }

    public void scheduleSprinklerTask(CCWorld ccWorld, int startDelay) {
        Random randomGenerator = ThreadLocalRandom.current();
        int delay = (Math.min(30, ConfigManager.pointGainInterval) + startDelay) * 1000;
        for (SimpleLocation simpleLocation : sprinklerMap.keySet()) {
            ccWorld.pushSprinklerTask(simpleLocation, randomGenerator.nextInt(delay));
        }
    }

    public void scheduleConsumeTask(CCWorld ccWorld, int startDelay) {
        Random randomGenerator = ThreadLocalRandom.current();
        int delay = (Math.min(30, ConfigManager.pointGainInterval) + startDelay) * 1000;
        for (SimpleLocation simpleLocation : potMap.keySet()) {
            ccWorld.pushConsumeTask(simpleLocation, randomGenerator.nextInt(delay));
        }
    }

    public void changePotModel(SimpleLocation simpleLocation, Pot pot) {
        Location location = simpleLocation.getBukkitLocation();
        if (location == null) return;
        if (CustomCrops.getInstance().getPlatformInterface().removeAnyBlock(location)) {
            String replacer = pot.isWet() ? pot.getConfig().getWetPot(pot.getFertilizer()) : pot.getConfig().getDryPot(pot.getFertilizer());
            if (ConfigUtils.isVanillaItem(replacer)) {
                Block block = location.getBlock();
                block.setType(Material.valueOf(replacer));
                if (block.getBlockData() instanceof Farmland farmland && ConfigManager.disableMoistureMechanic) {
                    farmland.setMoisture(pot.isWet() ? farmland.getMaximumMoisture() : 0);
                    block.setBlockData(farmland);
                }
            }
            else CustomCrops.getInstance().getPlatformInterface().placeNoteBlock(location, replacer);
        } else {
            CustomCrops.getInstance().getWorldDataManager().removePotData(simpleLocation);
        }
    }
}
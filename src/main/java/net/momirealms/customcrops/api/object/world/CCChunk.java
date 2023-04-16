package net.momirealms.customcrops.api.object.world;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.CustomCropsAPI;
import net.momirealms.customcrops.api.object.basic.ConfigManager;
import net.momirealms.customcrops.api.object.crop.GrowingCrop;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.object.sprinkler.Sprinkler;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class CCChunk implements Serializable {

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
                Bukkit.getScheduler().callSyncMethod(CustomCrops.getInstance(), () -> {
                    CustomCropsAPI.getInstance().changePotModel(simpleLocation, pot);
                    return null;
                });
            }
        }
        else if (pot_id != null) {
            Pot newPot = new Pot(pot_id, null, amount);
            potMap.put(simpleLocation, newPot);
            Bukkit.getScheduler().callSyncMethod(CustomCrops.getInstance(), () -> {
                CustomCropsAPI.getInstance().changePotModel(simpleLocation, newPot);
                return null;
            });
        }
    }

    public void addFertilizerToPot(SimpleLocation simpleLocation, Fertilizer fertilizer, @NotNull String pot_id) {
        Pot pot = potMap.get(simpleLocation);
        if (pot != null) {
            pot.setFertilizer(fertilizer);
            Bukkit.getScheduler().callSyncMethod(CustomCrops.getInstance(), () -> {
                CustomCropsAPI.getInstance().changePotModel(simpleLocation, pot);
                return null;
            });
        }
        else {
            Pot newPot = new Pot(pot_id, fertilizer, 0);
            potMap.put(simpleLocation, newPot);
            Bukkit.getScheduler().callSyncMethod(CustomCrops.getInstance(), () -> {
                CustomCropsAPI.getInstance().changePotModel(simpleLocation, newPot);
                return null;
            });
        }
    }

    public void scheduleGrowTask(CCWorld ccWorld) {
        Random randomGenerator = ThreadLocalRandom.current();
        for (SimpleLocation simpleLocation : growingCropMap.keySet()) {
            ccWorld.pushCropTask(simpleLocation, randomGenerator.nextInt(ConfigManager.pointGainInterval));
        }
    }

    public void scheduleSprinklerTask(CCWorld ccWorld) {
        Random randomGenerator = ThreadLocalRandom.current();
        for (SimpleLocation simpleLocation : sprinklerMap.keySet()) {
            ccWorld.pushSprinklerTask(simpleLocation, randomGenerator.nextInt(30));
        }
    }

    public void scheduleConsumeTask(CCWorld ccWorld) {
        Random randomGenerator = ThreadLocalRandom.current();
        for (SimpleLocation simpleLocation : potMap.keySet()) {
            ccWorld.pushConsumeTask(simpleLocation, randomGenerator.nextInt(60));
        }
    }
}
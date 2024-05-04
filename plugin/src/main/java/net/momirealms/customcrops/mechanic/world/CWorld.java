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

package net.momirealms.customcrops.mechanic.world;

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Pair;
import net.momirealms.customcrops.api.event.SeasonChangeEvent;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.manager.VersionManager;
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.RegionPos;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.*;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import net.momirealms.customcrops.api.scheduler.CancellableTask;
import net.momirealms.customcrops.api.scheduler.Scheduler;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.api.util.LogUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CWorld implements CustomCropsWorld {

    private final WorldManager worldManager;
    private WeakReference<World> world;
    private final ConcurrentHashMap<ChunkPos, CChunk> loadedChunks;
    private final ConcurrentHashMap<ChunkPos, CChunk> lazyChunks;
    private final ConcurrentHashMap<RegionPos, CRegion> loadedRegions;
    private WorldSetting setting;
    private WorldInfoData infoData;
    private final String worldName;
    private CancellableTask worldTask;
    private int currentMinecraftDay;
    private int regionTimer;

    public CWorld(WorldManager worldManager, World world) {
        this.world = new WeakReference<>(world);
        this.worldManager = worldManager;
        this.loadedChunks = new ConcurrentHashMap<>();
        this.lazyChunks = new ConcurrentHashMap<>();
        this.loadedRegions = new ConcurrentHashMap<>();
        this.worldName = world.getName();
        this.currentMinecraftDay = (int) (world.getFullTime() / 24000);
        this.regionTimer = 0;
    }

    @Override
    public void save() {
        long time1 = System.currentTimeMillis();
        worldManager.saveInfoData(this);
        for (CChunk chunk : loadedChunks.values()) {
            worldManager.saveChunkToCachedRegion(chunk);
        }
        for (CChunk chunk : lazyChunks.values()) {
            worldManager.saveChunkToCachedRegion(chunk);
        }
        for (CRegion region : loadedRegions.values()) {
            worldManager.saveRegionToFile(region);
        }
        long time2 = System.currentTimeMillis();
        CustomCropsPlugin.get().debug("Took " + (time2-time1) + "ms to save world " + worldName + ". Saved " + (lazyChunks.size() + loadedChunks.size()) + " chunks.");
    }

    @Override
    public void startTick() {
        if (this.worldTask == null || this.worldTask.isCancelled())
            this.worldTask = CustomCropsPlugin.get().getScheduler().runTaskAsyncTimer(this::timer, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void cancelTick() {
        if (!this.worldTask.isCancelled())
            this.worldTask.cancel();
    }

    private void timer() {
        ArrayList<Pair<ChunkPos, CChunk>> chunksToSave = new ArrayList<>();
        for (Map.Entry<ChunkPos, CChunk> lazyEntry : lazyChunks.entrySet()) {
            CChunk chunk = lazyEntry.getValue();
            int sec = chunk.getUnloadedSeconds() + 1;
            if (sec >= 30) {
                chunksToSave.add(Pair.of(lazyEntry.getKey(), chunk));
            } else {
                chunk.setUnloadedSeconds(sec);
            }
        }
        for (Pair<ChunkPos, CChunk> pair : chunksToSave) {
            lazyChunks.remove(pair.left());
            worldManager.saveChunkToCachedRegion(pair.right());
        }
        if (setting.isAutoSeasonChange()) {
            this.updateSeasonAndDate();
        }
        if (setting.isSchedulerEnabled()) {
            Scheduler scheduler = CustomCropsPlugin.get().getScheduler();
            if (VersionManager.folia()) {
                for (CChunk chunk : loadedChunks.values()) {
                    if (unloadIfNotLoaded(chunk.getChunkPos())) {
                        continue;
                    }
                    scheduler.runTaskSync(chunk::secondTimer, getWorld(), chunk.getChunkPos().x(), chunk.getChunkPos().z());
                }
            } else {
                for (CChunk chunk : loadedChunks.values()) {
                    if (unloadIfNotLoaded(chunk.getChunkPos())) {
                        continue;
                    }
                    chunk.secondTimer();
                }
            }
        }

        // timer check to unload regions
        this.regionTimer++;
        if (this.regionTimer >= 600) {
            this.regionTimer = 0;
            ArrayList<RegionPos> removed = new ArrayList<>();
            for (Map.Entry<RegionPos, CRegion> entry : loadedRegions.entrySet()) {
                if (isRegionNoLongerLoaded(entry.getKey())) {
                    worldManager.saveRegionToFile(entry.getValue());
                    removed.add(entry.getKey());
                }
            }
            for (RegionPos pos : removed) {
                loadedRegions.remove(pos);
            }
        }
    }

    private void updateSeasonAndDate() {
        World bukkitWorld = getWorld();
        if (bukkitWorld == null) {
            LogUtils.severe(String.format("World %s unloaded unexpectedly. Stop ticking task...", worldName));
            this.cancelTick();
            return;
        }

        long ticks = bukkitWorld.getFullTime();
        int days = (int) (ticks / 24000);
        if (days == this.currentMinecraftDay) {
            return;
        }

        if (days > this.currentMinecraftDay) {
            int date = infoData.getDate();
            date++;
            if (date > setting.getSeasonDuration()) {
                date = 1;
                Season next = infoData.getSeason().getNextSeason();
                infoData.setSeason(next);
                EventUtils.fireAndForget(new SeasonChangeEvent(bukkitWorld, next));
            }
            infoData.setDate(date);
        }
        this.currentMinecraftDay = days;
    }

    @Override
    public CustomCropsChunk removeLazyChunkAt(ChunkPos chunkPos) {
        return lazyChunks.remove(chunkPos);
    }

    @Override
    public WorldSetting getWorldSetting() {
        return setting;
    }

    @Override
    public void setWorldSetting(WorldSetting setting) {
        this.setting = setting;
    }

    @Override
    public Collection<? extends CustomCropsChunk> getChunkStorage() {
        return loadedChunks.values();
    }

    @Nullable
    @Override
    public World getWorld() {
        return Optional.ofNullable(world.get()).orElseGet(() -> {
            World bukkitWorld = Bukkit.getWorld(worldName);
            if (bukkitWorld != null) {
                this.world = new WeakReference<>(bukkitWorld);
            }
            return bukkitWorld;
        });
    }

    @NotNull
    @Override
    public String getWorldName() {
        return worldName;
    }

    @Override
    public boolean isChunkLoaded(ChunkPos chunkPos) {
        return loadedChunks.containsKey(chunkPos);
    }

    @Override
    public boolean isRegionLoaded(RegionPos regionPos) {
        return loadedRegions.containsKey(regionPos);
    }

    @Override
    public Optional<CustomCropsChunk> getOrCreateLoadedChunkAt(ChunkPos chunkPos) {
        return Optional.ofNullable(createOrGetChunk(chunkPos));
    }

    @Override
    public Optional<CustomCropsChunk> getLoadedChunkAt(ChunkPos chunkPos) {
        return Optional.ofNullable(loadedChunks.get(chunkPos));
    }

    @Override
    public Optional<CustomCropsRegion> getLoadedRegionAt(RegionPos regionPos) {
        return Optional.ofNullable(loadedRegions.get(regionPos));
    }

    @Override
    public void loadRegion(CustomCropsRegion region) {
        RegionPos regionPos = region.getRegionPos();
        if (loadedRegions.containsKey(regionPos)) {
            LogUtils.warn("Invalid operation: Loaded region is loaded again." + regionPos);
            return;
        }
        loadedRegions.put(regionPos, (CRegion) region);
    }

    @Override
    public void loadChunk(CustomCropsChunk chunk) {
        ChunkPos chunkPos = chunk.getChunkPos();
        if (loadedChunks.containsKey(chunkPos)) {
            LogUtils.warn("Invalid operation: Loaded chunk is loaded again." + chunkPos);
            return;
        }
        loadedChunks.put(chunkPos, (CChunk) chunk);
    }

    @Override
    public void unloadChunk(ChunkPos chunkPos) {
        CChunk chunk = loadedChunks.remove(chunkPos);
        if (chunk != null) {
            chunk.updateLastLoadedTime();
            lazyChunks.put(chunkPos, chunk);
        }
    }

    @Override
    public void deleteChunk(ChunkPos chunkPos) {
        CChunk chunk = loadedChunks.remove(chunkPos);
    }

    @Override
    public void setInfoData(WorldInfoData infoData) {
        this.infoData = infoData;
    }

    @Override
    public WorldInfoData getInfoData() {
        return infoData;
    }

    @Override
    public int getDate() {
        if (setting.isEnableSeason()) {
            if (ConfigManager.syncSeasons() && ConfigManager.referenceWorld() != world) {
                return worldManager.getCustomCropsWorld(ConfigManager.referenceWorld()).map(customCropsWorld -> customCropsWorld.getInfoData().getDate()).orElse(0);
            }
            return infoData.getDate();
        } else {
            return 0;
        }
    }

    @Override
    @Nullable
    public Season getSeason() {
        if (setting.isEnableSeason()) {
            if (ConfigManager.syncSeasons() && ConfigManager.referenceWorld() != world) {
                return worldManager.getCustomCropsWorld(ConfigManager.referenceWorld()).map(customCropsWorld -> customCropsWorld.getInfoData().getSeason()).orElse(null);
            }
            return infoData.getSeason();
        } else {
            return null;
        }
    }

    @Override
    public Optional<WorldSprinkler> getSprinklerAt(SimpleLocation location) {
        CChunk chunk = loadedChunks.get(location.getChunkPos());
        if (chunk == null) return Optional.empty();
        return chunk.getSprinklerAt(location);
    }

    @Override
    public Optional<WorldPot> getPotAt(SimpleLocation location) {
        CChunk chunk = loadedChunks.get(location.getChunkPos());
        if (chunk == null) return Optional.empty();
        return chunk.getPotAt(location);
    }

    @Override
    public Optional<WorldCrop> getCropAt(SimpleLocation location) {
        CChunk chunk = loadedChunks.get(location.getChunkPos());
        if (chunk == null) return Optional.empty();
        return chunk.getCropAt(location);
    }

    @Override
    public Optional<WorldGlass> getGlassAt(SimpleLocation location) {
        CChunk chunk = loadedChunks.get(location.getChunkPos());
        if (chunk == null) return Optional.empty();
        return chunk.getGlassAt(location);
    }

    @Override
    public Optional<WorldScarecrow> getScarecrowAt(SimpleLocation location) {
        CChunk chunk = loadedChunks.get(location.getChunkPos());
        if (chunk == null) return Optional.empty();
        return chunk.getScarecrowAt(location);
    }

    @Override
    public Optional<CustomCropsBlock> getBlockAt(SimpleLocation location) {
        CChunk chunk = loadedChunks.get(location.getChunkPos());
        if (chunk == null) return Optional.empty();
        return chunk.getBlockAt(location);
    }

    @Override
    public void addWaterToSprinkler(Sprinkler sprinkler, int amount, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getOrCreateLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            chunk.get().addWaterToSprinkler(sprinkler, amount, location);
        } else {
            LogUtils.warn("Invalid operation: Adding water to sprinkler in a not generated chunk");
        }
    }

    @Override
    public void addFertilizerToPot(Pot pot, Fertilizer fertilizer, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getOrCreateLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            chunk.get().addFertilizerToPot(pot, fertilizer, location);
        } else {
            LogUtils.warn("Invalid operation: Adding fertilizer to pot in a not generated chunk");
        }
    }

    @Override
    public void addWaterToPot(Pot pot, int amount, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getOrCreateLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            chunk.get().addWaterToPot(pot, amount, location);
        } else {
            LogUtils.warn("Invalid operation: Adding water to pot in a not generated chunk");
        }
    }

    @Override
    public void addPotAt(WorldPot pot, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getOrCreateLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            chunk.get().addPotAt(pot, location);
        } else {
            LogUtils.warn("Invalid operation: Adding pot in a not generated chunk");
        }
    }

    @Override
    public void addSprinklerAt(WorldSprinkler sprinkler, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getOrCreateLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            chunk.get().addSprinklerAt(sprinkler, location);
        } else {
            LogUtils.warn("Invalid operation: Adding sprinkler in a not generated chunk");
        }
    }

    @Override
    public void addCropAt(WorldCrop crop, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getOrCreateLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            chunk.get().addCropAt(crop, location);
        } else {
            LogUtils.warn("Invalid operation: Adding crop in a not generated chunk");
        }
    }

    @Override
    public void addPointToCrop(Crop crop, int points, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getOrCreateLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            chunk.get().addPointToCrop(crop, points, location);
        } else {
            LogUtils.warn("Invalid operation: Adding point to crop in a not generated chunk");
        }
    }

    @Override
    public void addGlassAt(WorldGlass glass, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getOrCreateLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            chunk.get().addGlassAt(glass, location);
        } else {
            LogUtils.warn("Invalid operation: Adding glass in a not generated chunk");
        }
    }

    @Override
    public void addScarecrowAt(WorldScarecrow scarecrow, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getOrCreateLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            chunk.get().addScarecrowAt(scarecrow, location);
        } else {
            LogUtils.warn("Invalid operation: Adding scarecrow in a not generated chunk");
        }
    }

    @Override
    public WorldSprinkler removeSprinklerAt(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            return chunk.get().removeSprinklerAt(location);
        } else {
            LogUtils.warn("Invalid operation: Removing sprinkler from an unloaded/empty chunk");
            return null;
        }
    }

    @Override
    public WorldPot removePotAt(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            return chunk.get().removePotAt(location);
        } else {
            LogUtils.warn("Invalid operation: Removing pot from an unloaded/empty chunk");
            return null;
        }
    }

    @Override
    public WorldCrop removeCropAt(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            return chunk.get().removeCropAt(location);
        } else {
            LogUtils.warn("Invalid operation: Removing crop from an unloaded/empty chunk");
            return null;
        }
    }

    @Override
    public WorldGlass removeGlassAt(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            return chunk.get().removeGlassAt(location);
        } else {
            LogUtils.warn("Invalid operation: Removing glass from an unloaded/empty chunk");
            return null;
        }
    }

    @Override
    public WorldScarecrow removeScarecrowAt(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            return chunk.get().removeScarecrowAt(location);
        } else {
            LogUtils.warn("Invalid operation: Removing scarecrow from an unloaded/empty chunk");
            return null;
        }
    }

    @Override
    public CustomCropsBlock removeAnythingAt(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getLoadedChunkAt(location.getChunkPos());
        if (chunk.isPresent()) {
            return chunk.get().removeBlockAt(location);
        } else {
            LogUtils.warn("Invalid operation: Removing anything from an unloaded/empty chunk");
            return null;
        }
    }

    @Nullable
    private CustomCropsChunk createOrGetChunk(ChunkPos chunkPos) {
        World bukkitWorld = world.get();
        if (bukkitWorld == null)
            return null;
        CChunk chunk = loadedChunks.get(chunkPos);
        if (chunk != null) {
            return chunk;
        }
        // is a loaded chunk, but it doesn't have CustomCrops data
        if (bukkitWorld.isChunkLoaded(chunkPos.x(), chunkPos.z())) {
            chunk = new CChunk(this, chunkPos);
            loadChunk(chunk);
            return chunk;
        } else {
            return null;
        }
    }

    @Override
    public boolean doesChunkHaveScarecrow(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getLoadedChunkAt(location.getChunkPos());
        return chunk.map(CustomCropsChunk::hasScarecrow).orElse(false);
    }

    @Override
    public boolean isPotReachLimit(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getLoadedChunkAt(location.getChunkPos());
        if (chunk.isEmpty()) return false;
        if (setting.getPotPerChunk() < 0) return false;
        return chunk.get().getPotAmount() >= setting.getPotPerChunk();
    }

    @Override
    public boolean isCropReachLimit(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getLoadedChunkAt(location.getChunkPos());
        if (chunk.isEmpty()) return false;
        if (setting.getCropPerChunk() < 0) return false;
        return chunk.get().getCropAmount() >= setting.getCropPerChunk();
    }

    @Override
    public boolean isSprinklerReachLimit(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getLoadedChunkAt(location.getChunkPos());
        if (chunk.isEmpty()) return false;
        if (setting.getSprinklerPerChunk() < 0) return false;
        return chunk.get().getSprinklerAmount() >= setting.getSprinklerPerChunk();
    }

    private boolean isRegionNoLongerLoaded(RegionPos region) {
        World w = world.get();
        if (w != null) {
            for (int chunkX = region.x() * 32; chunkX < region.x() * 32 + 32; chunkX++) {
                for (int chunkZ = region.z() * 32; chunkZ < region.z() * 32 + 32; chunkZ++) {
                    // if a chunk is unloaded, then it should not be in the loaded chunks map
                    if (w.isChunkLoaded(chunkX, chunkZ) || lazyChunks.containsKey(ChunkPos.of(chunkX, chunkZ))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean unloadIfNotLoaded(ChunkPos pos) {
        if (!world.get().isChunkLoaded(pos.x(), pos.z())) {
            unloadChunk(pos);
            return true;
        }
        return false;
    }
}

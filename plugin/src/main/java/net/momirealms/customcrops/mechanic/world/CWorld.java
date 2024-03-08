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
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.*;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import net.momirealms.customcrops.api.scheduler.CancellableTask;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.utils.EventUtils;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CWorld implements CustomCropsWorld {

    private final WorldManager worldManager;
    private final WeakReference<World> world;
    private final ConcurrentHashMap<ChunkCoordinate, CChunk> loadedChunks;
    private final ConcurrentHashMap<ChunkCoordinate, CChunk> lazyChunks;
    private WorldSetting setting;
    private WorldInfoData infoData;
    private final String worldName;
    private CancellableTask worldTask;
    private int currentMinecraftDay;

    public CWorld(WorldManager worldManager, World world) {
        this.world = new WeakReference<>(world);
        this.worldManager = worldManager;
        this.loadedChunks = new ConcurrentHashMap<>();
        this.lazyChunks = new ConcurrentHashMap<>();
        this.worldName = world.getName();
        this.currentMinecraftDay = (int) (world.getFullTime() / 24000);
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
        ArrayList<Pair<ChunkCoordinate, CChunk>> chunksToSave = new ArrayList<>();
        for (Map.Entry<ChunkCoordinate, CChunk> lazyEntry : lazyChunks.entrySet()) {
            CChunk chunk = lazyEntry.getValue();
            int sec = chunk.getUnloadedSeconds() + 1;
            if (sec >= 10) {
                chunksToSave.add(Pair.of(lazyEntry.getKey(), chunk));
            } else {
                chunk.setUnloadedSeconds(sec);
            }
        }
        for (Pair<ChunkCoordinate, CChunk> pair : chunksToSave) {
            lazyChunks.remove(pair.left());
            worldManager.saveChunkToFile(pair.right());
        }
        if (setting.isAutoSeasonChange()) {
            this.updateSeasonAndDate();
        }
        for (CChunk chunk : loadedChunks.values()) {
            chunk.secondTimer();
        }
    }

    private void updateSeasonAndDate() {
        World bukkitWorld = world.get();
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
    public CustomCropsChunk removeLazyChunkAt(ChunkCoordinate chunkCoordinate) {
        return lazyChunks.remove(chunkCoordinate);
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
        return world.get();
    }

    @Override
    public String getWorldName() {
        return worldName;
    }

    @Override
    public boolean isChunkLoaded(ChunkCoordinate chunkCoordinate) {
        return loadedChunks.containsKey(chunkCoordinate);
    }

    @Override
    public Optional<CustomCropsChunk> getChunkAt(ChunkCoordinate chunkCoordinate) {
        return Optional.ofNullable(createOrGetChunk(chunkCoordinate));
    }

    @Override
    public void loadChunk(CustomCropsChunk chunk) {
        ChunkCoordinate chunkCoordinate = chunk.getChunkCoordinate();
        if (loadedChunks.containsKey(chunkCoordinate)) {
            LogUtils.warn("Invalid operation: Loaded chunk is loaded again." + chunkCoordinate);
            return;
        }
        loadedChunks.put(chunkCoordinate, (CChunk) chunk);
    }

    @Override
    public void unloadChunk(ChunkCoordinate chunkCoordinate) {
        CChunk chunk = loadedChunks.remove(chunkCoordinate);
        if (chunk != null) {
            chunk.updateLastLoadedTime();
            lazyChunks.put(chunkCoordinate, chunk);
        }
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
        CChunk chunk = loadedChunks.get(location.getChunkCoordinate());
        if (chunk == null) return Optional.empty();
        return chunk.getSprinklerAt(location);
    }

    @Override
    public Optional<WorldPot> getPotAt(SimpleLocation location) {
        CChunk chunk = loadedChunks.get(location.getChunkCoordinate());
        if (chunk == null) return Optional.empty();
        return chunk.getPotAt(location);
    }

    @Override
    public Optional<WorldCrop> getCropAt(SimpleLocation location) {
        CChunk chunk = loadedChunks.get(location.getChunkCoordinate());
        if (chunk == null) return Optional.empty();
        return chunk.getCropAt(location);
    }

    @Override
    public Optional<WorldGlass> getGlassAt(SimpleLocation location) {
        CChunk chunk = loadedChunks.get(location.getChunkCoordinate());
        if (chunk == null) return Optional.empty();
        return chunk.getGlassAt(location);
    }

    @Override
    public Optional<WorldScarecrow> getScarecrowAt(SimpleLocation location) {
        CChunk chunk = loadedChunks.get(location.getChunkCoordinate());
        if (chunk == null) return Optional.empty();
        return chunk.getScarecrowAt(location);
    }

    @Override
    public Optional<CustomCropsBlock> getBlockAt(SimpleLocation location) {
        CChunk chunk = loadedChunks.get(location.getChunkCoordinate());
        if (chunk == null) return Optional.empty();
        return chunk.getBlockAt(location);
    }

    @Override
    public void addWaterToSprinkler(Sprinkler sprinkler, SimpleLocation location, int amount) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().addWaterToSprinkler(sprinkler, location, amount);
        } else {
            LogUtils.warn("Invalid operation: Adding water to sprinkler in a not generated chunk");
        }
    }

    @Override
    public void addFertilizerToPot(Pot pot, Fertilizer fertilizer, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().addFertilizerToPot(pot, fertilizer, location);
        } else {
            LogUtils.warn("Invalid operation: Adding fertilizer to pot in a not generated chunk");
        }
    }

    @Override
    public void addWaterToPot(Pot pot, SimpleLocation location, int amount) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().addWaterToPot(pot, location, amount);
        } else {
            LogUtils.warn("Invalid operation: Adding water to pot in a not generated chunk");
        }
    }

    @Override
    public void addPotAt(WorldPot pot, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().addPotAt(pot, location);
        } else {
            LogUtils.warn("Invalid operation: Adding pot in a not generated chunk");
        }
    }

    @Override
    public void addSprinklerAt(WorldSprinkler sprinkler, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().addSprinklerAt(sprinkler, location);
        } else {
            LogUtils.warn("Invalid operation: Adding sprinkler in a not generated chunk");
        }
    }

    @Override
    public void addCropAt(WorldCrop crop, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().addCropAt(crop, location);
        } else {
            LogUtils.warn("Invalid operation: Adding crop in a not generated chunk");
        }
    }

    @Override
    public void addPointToCrop(Crop crop, SimpleLocation location, int points) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().addPointToCrop(crop, location, points);
        } else {
            LogUtils.warn("Invalid operation: Adding points to crop in a not generated chunk");
        }
    }

    @Override
    public void addGlassAt(WorldGlass glass, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().addGlassAt(glass, location);
        } else {
            LogUtils.warn("Invalid operation: Adding glass in a not generated chunk");
        }
    }

    @Override
    public void addScarecrowAt(WorldScarecrow scarecrow, SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().addScarecrowAt(scarecrow, location);
        } else {
            LogUtils.warn("Invalid operation: Adding scarecrow in a not generated chunk");
        }
    }

    @Override
    public void removeSprinklerAt(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().removeSprinklerAt(location);
        } else {
            LogUtils.warn("Invalid operation: Removing sprinkler from a not generated chunk");
        }
    }

    @Override
    public void removePotAt(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().removePotAt(location);
        } else {
            LogUtils.warn("Invalid operation: Removing pot from a not generated chunk");
        }
    }

    @Override
    public void removeCropAt(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().removeCropAt(location);
        } else {
            LogUtils.warn("Invalid operation: Removing crop from a not generated chunk");
        }
    }

    @Override
    public void removeGlassAt(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().removeGlassAt(location);
        } else {
            LogUtils.warn("Invalid operation: Removing glass from a not generated chunk");
        }
    }

    @Override
    public void removeScarecrowAt(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            chunk.get().removeScarecrowAt(location);
        } else {
            LogUtils.warn("Invalid operation: Removing scarecrow from a not generated chunk");
        }
    }

    @Override
    public CustomCropsBlock removeAnythingAt(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isPresent()) {
            return chunk.get().removeBlockAt(location);
        } else {
            LogUtils.warn("Invalid operation: Removing anything from a not generated chunk");
            return null;
        }
    }

    @Nullable
    private CustomCropsChunk createOrGetChunk(ChunkCoordinate chunkCoordinate) {
        World bukkitWorld = world.get();
        if (bukkitWorld == null)
            return null;
        CChunk chunk = loadedChunks.get(chunkCoordinate);
        if (chunk != null) {
            return chunk;
        }
        if (bukkitWorld.isChunkLoaded(chunkCoordinate.x(), chunkCoordinate.z())) {
            chunk = new CChunk(this, chunkCoordinate);
            loadChunk(chunk);
            return chunk;
        } else {
            if (bukkitWorld.isChunkGenerated(chunkCoordinate.x(), chunkCoordinate.z())) {
                Chunk bukkitChunk = bukkitWorld.getChunkAt(chunkCoordinate.x(), chunkCoordinate.z());
                worldManager.handleChunkLoad(bukkitChunk);
                chunk = loadedChunks.get(chunkCoordinate);
                return Objects.requireNonNullElseGet(chunk, () -> new CChunk(this, chunkCoordinate));
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean isPotReachLimit(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isEmpty()) {
            LogUtils.warn("Invalid operation: Querying pot amount from a not generated chunk");
            return true;
        }
        if (setting.getPotPerChunk() < 0) return false;
        return chunk.get().getPotAmount() >= setting.getPotPerChunk();
    }

    @Override
    public boolean isCropReachLimit(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isEmpty()) {
            LogUtils.warn("Invalid operation: Querying crop amount from a not generated chunk");
            return true;
        }
        if (setting.getCropPerChunk() < 0) return false;
        return chunk.get().getCropAmount() >= setting.getCropPerChunk();
    }

    @Override
    public boolean isSprinklerReachLimit(SimpleLocation location) {
        Optional<CustomCropsChunk> chunk = getChunkAt(location.getChunkCoordinate());
        if (chunk.isEmpty()) {
            LogUtils.warn("Invalid operation: Querying sprinkler amount from a not generated chunk");
            return true;
        }
        if (setting.getSprinklerPerChunk() < 0) return false;
        return chunk.get().getSprinklerAmount() >= setting.getSprinklerPerChunk();
    }

    public Collection<CChunk> getAllChunksToSave() {
        ArrayList<CChunk> chunks = new ArrayList<>();
        chunks.addAll(lazyChunks.values());
        chunks.addAll(loadedChunks.values());
        return chunks;
    }
}

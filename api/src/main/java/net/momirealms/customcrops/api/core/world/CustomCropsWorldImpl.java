/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.api.core.world;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.world.adaptor.WorldAdaptor;
import net.momirealms.customcrops.common.helper.VersionHelper;
import net.momirealms.customcrops.common.plugin.scheduler.SchedulerAdapter;
import net.momirealms.customcrops.common.plugin.scheduler.SchedulerTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CustomCropsWorldImpl<W> implements CustomCropsWorld<W> {

    private final ConcurrentHashMap<ChunkPos, CustomCropsChunk> loadedChunks = new ConcurrentHashMap<>(512);
    private final ConcurrentHashMap<ChunkPos, CustomCropsChunk> lazyChunks = new ConcurrentHashMap<>(128);
    private final ConcurrentHashMap<RegionPos, CustomCropsRegion> loadedRegions = new ConcurrentHashMap<>(128);
    private final WeakReference<W> world;
    private final WeakReference<World> bukkitWorld;
    private final String worldName;
    private long currentMinecraftDay;
    private int regionTimer;
    private SchedulerTask tickTask;
    private WorldSetting setting;
    private final WorldAdaptor<W> adaptor;
    private final WorldExtraData extraData;
    private final WorldScheduler scheduler;

    public CustomCropsWorldImpl(W world, WorldAdaptor<W> adaptor) {
        this.world = new WeakReference<>(world);
        this.worldName = adaptor.getName(world);
        this.bukkitWorld = new WeakReference<>(Bukkit.getWorld(worldName));
        this.regionTimer = 0;
        this.adaptor = adaptor;
        this.extraData = adaptor.loadExtraData(world);
        this.currentMinecraftDay = (int) (bukkitWorld().getFullTime() / 24_000);
        this.scheduler = new WorldScheduler(BukkitCustomCropsPlugin.getInstance());
    }

    @NotNull
    @Override
    public WorldAdaptor<W> adaptor() {
        return adaptor;
    }

    @NotNull
    @Override
    public WorldExtraData extraData() {
        return extraData;
    }

    @Override
    public boolean testChunkLimitation(Pos3 pos3, Class<? extends CustomCropsBlock> clazz, int amount) {
        Optional<CustomCropsChunk> optional = getChunk(pos3.toChunkPos());
        if (optional.isPresent()) {
            int i = 0;
            CustomCropsChunk chunk = optional.get();
            for (CustomCropsSection section : chunk.sections()) {
                for (CustomCropsBlockState state : section.blockMap().values()) {
                    if (clazz.isAssignableFrom(state.type().getClass())) {
                        i++;
                        if (i >= amount) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean doesChunkHaveBlock(Pos3 pos3, Class<? extends CustomCropsBlock> clazz) {
        Optional<CustomCropsChunk> optional = getChunk(pos3.toChunkPos());
        if (optional.isPresent()) {
            CustomCropsChunk chunk = optional.get();
            for (CustomCropsSection section : chunk.sections()) {
                for (CustomCropsBlockState state : section.blockMap().values()) {
                    if (clazz.isAssignableFrom(state.type().getClass())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int getChunkBlockAmount(Pos3 pos3, Class<? extends CustomCropsBlock> clazz) {
        Optional<CustomCropsChunk> optional = getChunk(pos3.toChunkPos());
        if (optional.isPresent()) {
            int i = 0;
            CustomCropsChunk chunk = optional.get();
            for (CustomCropsSection section : chunk.sections()) {
                for (CustomCropsBlockState state : section.blockMap().values()) {
                    if (clazz.isAssignableFrom(state.type().getClass())) {
                        i++;
                    }
                }
            }
            return i;
        } else {
            return 0;
        }
    }

    @Override
    public CustomCropsChunk[] loadedChunks() {
        return loadedChunks.values().toArray(new CustomCropsChunk[0]);
    }

    @Override
    public CustomCropsChunk[] lazyChunks() {
        return lazyChunks.values().toArray(new CustomCropsChunk[0]);
    }

    @Override
    public CustomCropsRegion[] loadedRegions() {
        return loadedRegions.values().toArray(new CustomCropsRegion[0]);
    }

    @Override
    public @NotNull Optional<CustomCropsBlockState> getLoadedBlockState(Pos3 location) {
        ChunkPos pos = location.toChunkPos();
        Optional<CustomCropsChunk> chunk = getLoadedChunk(pos);
        if (chunk.isEmpty()) {
            return Optional.empty();
        } else {
            CustomCropsChunk customChunk = chunk.get();
            return customChunk.getBlockState(location);
        }
    }

    @NotNull
    @Override
    public Optional<CustomCropsBlockState> getBlockState(Pos3 location) {
        ChunkPos pos = location.toChunkPos();
        Optional<CustomCropsChunk> chunk = getChunk(pos);
        if (chunk.isEmpty()) {
            return Optional.empty();
        } else {
            CustomCropsChunk customChunk = chunk.get();
            // to let the bukkit system trigger the ChunkUnloadEvent later
            customChunk.load(true);
            return customChunk.getBlockState(location);
        }
    }

    @NotNull
    @Override
    public Optional<CustomCropsBlockState> removeBlockState(Pos3 location) {
        ChunkPos pos = location.toChunkPos();
        Optional<CustomCropsChunk> chunk = getChunk(pos);
        if (chunk.isEmpty()) {
            return Optional.empty();
        } else {
            CustomCropsChunk customChunk = chunk.get();
            // to let the bukkit system trigger the ChunkUnloadEvent later
            customChunk.load(true);
            return customChunk.removeBlockState(location);
        }
    }

    @NotNull
    @Override
    public Optional<CustomCropsBlockState> addBlockState(Pos3 location, CustomCropsBlockState block) {
        ChunkPos pos = location.toChunkPos();
        CustomCropsChunk chunk = getOrCreateChunk(pos);
        return chunk.addBlockState(location, block);
    }

    @Override
    public void save(boolean async, boolean disabling) {
        if (async && !disabling) {
            this.scheduler.async().execute(this::save);
        } else {
            if (disabling) {
                save();
            } else {
                BukkitCustomCropsPlugin.getInstance().getScheduler().sync().run(this::save, null);
            }
        }
    }

    private void save() {
        long time1 = System.currentTimeMillis();
        this.adaptor.saveExtraData(this);
        for (CustomCropsChunk chunk : loadedChunks.values()) {
            this.adaptor.saveChunk(this, chunk);
        }
        for (CustomCropsChunk chunk : lazyChunks.values()) {
            this.adaptor.saveChunk(this, chunk);
        }
        for (CustomCropsRegion region : loadedRegions.values()) {
            this.adaptor.saveRegion(this, region);
        }
        long time2 = System.currentTimeMillis();
        BukkitCustomCropsPlugin.getInstance().debug(() -> "Took " + (time2-time1) + "ms to save world " + worldName + ". Saved " + (lazyChunks.size() + loadedChunks.size()) + " chunks.");
    }

    @Override
    public void setTicking(boolean tick) {
        if (tick) {
            if (this.tickTask == null || this.tickTask.isCancelled())
                this.tickTask = this.scheduler.asyncRepeating(this::timer, 1, 1, TimeUnit.SECONDS);
        } else {
            if (this.tickTask != null && !this.tickTask.isCancelled())
                this.tickTask.cancel();
        }
    }

    private void timer() {
        saveLazyChunks();
        saveLazyRegions();
        if (isANewDay()) {
            if (setting().autoSeasonChange()) {
                updateSeasonAndDate();
            }
        }
        if (setting().enableScheduler()) {
            tickChunks();
        }
    }

    private void tickChunks() {
        if (VersionHelper.isFolia()) {
            SchedulerAdapter<Location, World> scheduler = BukkitCustomCropsPlugin.getInstance().getScheduler();
            for (CustomCropsChunk chunk : loadedChunks.values()) {
                scheduler.sync().run(chunk::timer, bukkitWorld(), chunk.chunkPos().x(), chunk.chunkPos().z());
            }
        } else {
            for (CustomCropsChunk chunk : loadedChunks.values()) {
                chunk.timer();
            }
        }
    }

    private void updateSeasonAndDate() {
        int date = extraData().getDate();
        date++;
        if (date > setting().seasonDuration()) {
            Season season = extraData().getSeason().getNextSeason();
            extraData().setSeason(season);
            extraData().setDate(1);
        } else {
            extraData().setDate(date);
        }
    }

    private boolean isANewDay() {
        long currentDay = bukkitWorld().getFullTime() / 24_000;
        if (currentDay != currentMinecraftDay) {
            currentMinecraftDay = currentDay;
            return true;
        }
        return false;
    }

    private void saveLazyRegions() {
        this.regionTimer++;
        // To avoid the same timing as saving
        if (this.regionTimer >= 666) {
            this.regionTimer = 0;
            ArrayList<CustomCropsRegion> removed = new ArrayList<>();
            for (Map.Entry<RegionPos, CustomCropsRegion> entry : loadedRegions.entrySet()) {
                if (shouldUnloadRegion(entry.getKey())) {
                    removed.add(entry.getValue());
                }
            }
            for (CustomCropsRegion region : removed) {
                region.unload();
            }
        }
    }

    private void saveLazyChunks() {
        ArrayList<CustomCropsChunk> chunksToSave = new ArrayList<>();
        for (Map.Entry<ChunkPos, CustomCropsChunk> lazyEntry : this.lazyChunks.entrySet()) {
            CustomCropsChunk chunk = lazyEntry.getValue();
            int sec = chunk.lazySeconds() + 1;
            if (sec >= 30) {
                chunksToSave.add(chunk);
            } else {
                chunk.lazySeconds(sec);
            }
        }
        for (CustomCropsChunk chunk : chunksToSave) {
            unloadLazyChunk(chunk.chunkPos());
        }
    }

    @Override
    public W world() {
        return world.get();
    }

    @Override
    public World bukkitWorld() {
        return bukkitWorld.get();
    }

    @Override
    public String worldName() {
        return worldName;
    }

    @Override
    public @NotNull WorldSetting setting() {
        return setting;
    }

    @Override
    public void setting(WorldSetting setting) {
        this.setting = setting;
    }

    @Nullable
    public CustomCropsChunk removeLazyChunk(ChunkPos chunkPos) {
        return this.lazyChunks.remove(chunkPos);
    }

    public void deleteChunk(ChunkPos chunkPos) {
        this.lazyChunks.remove(chunkPos);
        this.loadedChunks.remove(chunkPos);
        getRegion(RegionPos.getByChunkPos(chunkPos)).ifPresent(region -> region.removeCachedChunk(chunkPos));
    }

    @Nullable
    public CustomCropsChunk getLazyChunk(ChunkPos chunkPos) {
        return this.lazyChunks.get(chunkPos);
    }

    @Override
    public boolean isChunkLoaded(ChunkPos pos) {
        return this.loadedChunks.containsKey(pos);
    }

    public boolean loadChunk(CustomCropsChunk chunk) {
        Optional<CustomCropsChunk> previousChunk = getLoadedChunk(chunk.chunkPos());
        if (previousChunk.isPresent()) {
            BukkitCustomCropsPlugin.getInstance().debug(() -> "Chunk " + chunk.chunkPos() + " already loaded.");
            if (previousChunk.get() != chunk) {
                BukkitCustomCropsPlugin.getInstance().getPluginLogger().severe("Failed to load the chunk. There is already a different chunk instance with the same coordinates in the cache. " + chunk.chunkPos());
                return false;
            }
            return true;
        }
        this.loadedChunks.put(chunk.chunkPos(), chunk);
        this.lazyChunks.remove(chunk.chunkPos());
        return true;
    }

    @ApiStatus.Internal
    public boolean unloadChunk(CustomCropsChunk chunk, boolean lazy) {
        ChunkPos pos = chunk.chunkPos();
        Optional<CustomCropsChunk> previousChunk = getLoadedChunk(chunk.chunkPos());
        if (previousChunk.isPresent()) {
            if (previousChunk.get() != chunk) {
                BukkitCustomCropsPlugin.getInstance().getPluginLogger().severe("Failed to remove the chunk. The provided chunk instance is inconsistent with the one in the cache. " + chunk.chunkPos());
                return false;
            }
        } else {
            return false;
        }
        this.loadedChunks.remove(chunk.chunkPos());
        chunk.updateLastUnloadTime();
        if (lazy) {
            this.lazyChunks.put(pos, chunk);
        } else {
            this.adaptor.saveChunk(this, chunk);
        }
        return true;
    }

    @ApiStatus.Internal
    public boolean unloadChunk(ChunkPos pos, boolean lazy) {
        CustomCropsChunk removed = this.loadedChunks.remove(pos);
        if (removed != null) {
            removed.updateLastUnloadTime();
            if (lazy) {
                this.lazyChunks.put(pos, removed);
            } else {
                this.adaptor.saveChunk(this, removed);
            }
            return true;
        }
        return false;
    }

    @ApiStatus.Internal
    public boolean unloadLazyChunk(ChunkPos pos) {
        CustomCropsChunk removed = this.lazyChunks.remove(pos);
        if (removed != null) {
            this.adaptor.saveChunk(this, removed);
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Optional<CustomCropsChunk> getLoadedChunk(ChunkPos chunkPos) {
        return Optional.ofNullable(this.loadedChunks.get(chunkPos));
    }

    @NotNull
    @Override
    public Optional<CustomCropsChunk> getChunk(ChunkPos chunkPos) {
        return Optional.ofNullable(getLoadedChunk(chunkPos).orElseGet(() -> {
            CustomCropsChunk chunk = getLazyChunk(chunkPos);
            if (chunk != null) {
                return chunk;
            }
            return this.adaptor.loadChunk(this, chunkPos, false);
        }));
    }

    @NotNull
    @Override
    public CustomCropsChunk getOrCreateChunk(ChunkPos chunkPos) {
        return Objects.requireNonNull(getLoadedChunk(chunkPos).orElseGet(() -> {
            CustomCropsChunk chunk = getLazyChunk(chunkPos);
            if (chunk != null) {
                return chunk;
            }
            chunk = this.adaptor.loadChunk(this, chunkPos, true);
            // to let the bukkit system trigger the ChunkUnloadEvent later
            chunk.load(true);
            return chunk;
        }));
    }

    /*
     * Regions
     */
    @Override
    public boolean isRegionLoaded(RegionPos pos) {
        return this.loadedRegions.containsKey(pos);
    }

    @ApiStatus.Internal
    public boolean loadRegion(CustomCropsRegion region) {
        Optional<CustomCropsRegion> previousRegion = getLoadedRegion(region.regionPos());
        if (previousRegion.isPresent()) {
            BukkitCustomCropsPlugin.getInstance().debug(() -> "Region " + region.regionPos() + " already loaded.");
            if (previousRegion.get() != region) {
                BukkitCustomCropsPlugin.getInstance().getPluginLogger().severe("Failed to load the region. There is already a different region instance with the same coordinates in the cache. " + region.regionPos());
                return false;
            }
            return true;
        }
        this.loadedRegions.put(region.regionPos(), region);
        return true;
    }

    @NotNull
    @Override
    public Optional<CustomCropsRegion> getLoadedRegion(RegionPos regionPos) {
        return Optional.ofNullable(loadedRegions.get(regionPos));
    }

    @NotNull
    @Override
    public Optional<CustomCropsRegion> getRegion(RegionPos regionPos) {
        return Optional.ofNullable(getLoadedRegion(regionPos).orElse(adaptor.loadRegion(this, regionPos, false)));
    }

    @NotNull
    @Override
    public CustomCropsRegion getOrCreateRegion(RegionPos regionPos) {
        return Objects.requireNonNull(getLoadedRegion(regionPos).orElse(adaptor.loadRegion(this, regionPos, true)));
    }

    private boolean shouldUnloadRegion(RegionPos regionPos) {
        World bukkitWorld = bukkitWorld();
        for (int chunkX = regionPos.x() * 32; chunkX < regionPos.x() * 32 + 32; chunkX++) {
            for (int chunkZ = regionPos.z() * 32; chunkZ < regionPos.z() * 32 + 32; chunkZ++) {
                // if a chunk is unloaded, then it should not be in the loaded chunks map
                ChunkPos pos = ChunkPos.of(chunkX, chunkZ);
                if (isChunkLoaded(pos) || this.lazyChunks.containsKey(pos) || bukkitWorld.isChunkLoaded(chunkX, chunkZ)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean unloadRegion(CustomCropsRegion region) {
        Optional<CustomCropsRegion> previousRegion = getLoadedRegion(region.regionPos());
        if (previousRegion.isPresent()) {
            if (previousRegion.get() != region) {
                BukkitCustomCropsPlugin.getInstance().getPluginLogger().severe("Failed to remove the region. The provided region instance is inconsistent with the one in the cache. " + region.regionPos());
                return false;
            }
        } else {
            return false;
        }
        RegionPos regionPos = region.regionPos();
        for (int chunkX = regionPos.x() * 32; chunkX < regionPos.x() * 32 + 32; chunkX++) {
            for (int chunkZ = regionPos.z() * 32; chunkZ < regionPos.z() * 32 + 32; chunkZ++) {
                ChunkPos pos = ChunkPos.of(chunkX, chunkZ);
                if (!unloadLazyChunk(pos)) {
                    unloadChunk(pos, false);
                }
            }
        }
        this.adaptor.saveRegion(this, region);
        this.loadedRegions.remove(region.regionPos());
        BukkitCustomCropsPlugin.getInstance().debug(() -> "[" + worldName + "] " + "Region " + region.regionPos() + " unloaded.");
        return true;
    }

    @Override
    public WorldScheduler scheduler() {
        return scheduler;
    }
}

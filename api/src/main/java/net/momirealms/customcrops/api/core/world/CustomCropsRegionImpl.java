package net.momirealms.customcrops.api.core.world;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomCropsRegionImpl implements CustomCropsRegion {

    private final CustomCropsWorld<?> world;
    private final RegionPos regionPos;
    private final ConcurrentHashMap<ChunkPos, byte[]> cachedChunks;
    private boolean isLoaded = false;

    protected CustomCropsRegionImpl(CustomCropsWorld<?> world, RegionPos regionPos) {
        this.world = world;
        this.cachedChunks = new ConcurrentHashMap<>();
        this.regionPos = regionPos;
    }

    protected CustomCropsRegionImpl(CustomCropsWorld<?> world, RegionPos regionPos, ConcurrentHashMap<ChunkPos, byte[]> cachedChunks) {
        this.world = world;
        this.regionPos = regionPos;
        this.cachedChunks = cachedChunks;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void unload() {
        if (this.isLoaded) {
            if (((CustomCropsWorldImpl<?>) world).unloadRegion(this)) {
                this.isLoaded = false;
            }
        }
    }

    @Override
    public void load() {
        if (!this.isLoaded) {
            if (((CustomCropsWorldImpl<?>) world).loadRegion(this)) {
                this.isLoaded = true;
            }
        }
    }

    @NotNull
    @Override
    public CustomCropsWorld<?> getWorld() {
        return this.world;
    }

    @Override
    public byte[] getCachedChunkBytes(ChunkPos pos) {
        return this.cachedChunks.get(pos);
    }

    @Override
    public RegionPos regionPos() {
        return this.regionPos;
    }

    @Override
    public boolean removeCachedChunk(ChunkPos pos) {
        return cachedChunks.remove(pos) != null;
    }

    @Override
    public void setCachedChunk(ChunkPos pos, byte[] data) {
        this.cachedChunks.put(pos, data);
    }

    @Override
    public Map<ChunkPos, byte[]> dataToSave() {
        return new HashMap<>(cachedChunks);
    }

    @Override
    public boolean canPrune() {
        return cachedChunks.isEmpty();
    }
}

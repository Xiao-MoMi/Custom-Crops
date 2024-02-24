package net.momirealms.customcrops.api.mechanic.world.level;

import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface CustomCropsWorld {

    void startTick();

    void cancelTick();

    WorldSetting getWorldSetting();

    void setWorldSetting(WorldSetting setting);

    Collection<? extends CustomCropsChunk> getChunkStorage();

    @Nullable
    World getWorld();

    String getWorldName();

    Optional<CustomCropsChunk> getChunkAt(ChunkCoordinate chunkCoordinate);

    void setInfoData(WorldInfoData infoData);

    WorldInfoData getInfoData();

    @Nullable
    Season getSeason();

    Optional<WorldSprinkler> getSprinklerAt(SimpleLocation location);

    Optional<WorldPot> getPotAt(SimpleLocation location);

    Optional<WorldCrop> getCropAt(SimpleLocation location);

    void addWaterToSprinkler(Sprinkler sprinkler, SimpleLocation location, int amount);

    void addFertilizerToPot(Pot pot, Fertilizer fertilizer, SimpleLocation location);

    void addWaterToPot(Pot pot, SimpleLocation location, int amount);

    void removeSprinklerAt(SimpleLocation location);

    void removePotAt(SimpleLocation location);

    void removeCropAt(SimpleLocation location);

    @Nullable
    CustomCropsChunk createOrGetChunk(ChunkCoordinate chunkCoordinate);

    boolean isPotReachLimit(SimpleLocation location);

    boolean isCropReachLimit(SimpleLocation location);

    boolean isSprinklerReachLimit(SimpleLocation location);
}

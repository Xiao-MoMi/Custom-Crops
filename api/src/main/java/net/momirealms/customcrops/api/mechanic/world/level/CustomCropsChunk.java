package net.momirealms.customcrops.api.mechanic.world.level;

import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;

import java.util.Optional;

public interface CustomCropsChunk {

    CustomCropsWorld getCustomCropsWorld();

    ChunkCoordinate getChunkCoordinate();

    void secondTimer();

    int getLoadedSeconds();

    void notifyUpdates();

    Optional<WorldCrop> getCropAt(SimpleLocation simpleLocation);

    Optional<WorldSprinkler> getSprinklerAt(SimpleLocation simpleLocation);

    Optional<WorldPot> getPotAt(SimpleLocation simpleLocation);

    void addWaterToSprinkler(Sprinkler sprinkler, SimpleLocation location, int amount);

    void addFertilizerToPot(Pot pot, Fertilizer fertilizer, SimpleLocation location);

    void addWaterToPot(Pot pot, SimpleLocation location, int amount);

    void removeSprinklerAt(SimpleLocation location);

    void removePotAt(SimpleLocation location);

    void removeCropAt(SimpleLocation location);

    int getCropAmount();

    int getPotAmount();

    int getSprinklerAmount();

    void addPotAt(WorldPot pot, SimpleLocation location);

    void addSprinklerAt(WorldSprinkler sprinkler, SimpleLocation location);
}

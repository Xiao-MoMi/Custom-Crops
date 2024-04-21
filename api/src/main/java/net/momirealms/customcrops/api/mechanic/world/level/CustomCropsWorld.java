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

package net.momirealms.customcrops.api.mechanic.world.level;

import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.api.mechanic.item.Fertilizer;
import net.momirealms.customcrops.api.mechanic.item.Pot;
import net.momirealms.customcrops.api.mechanic.item.Sprinkler;
import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.RegionPos;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface CustomCropsWorld {

    /**
     * Save all the data
     */
    void save();

    /**
     * Start the tick system
     */
    void startTick();

    /**
     * Stop the tick system
     */
    void cancelTick();

    /**
     * Check if a region is loaded
     *
     * @param regionPos region position
     * @return loaded or not
     */
    boolean isRegionLoaded(RegionPos regionPos);

    /**
     * Removed lazy chunks (Delayed unloading chunks)
     *
     * @param chunkPos chunk position
     * @return the removed lazy chunk
     */
    CustomCropsChunk removeLazyChunkAt(ChunkPos chunkPos);

    /**
     * Get the world's settings
     *
     * @return world settings
     */
    WorldSetting getWorldSetting();

    /**
     * Set the world's settings
     *
     * @param setting settings
     */
    void setWorldSetting(WorldSetting setting);

    /**
     * Get all the chunks
     *
     * @return chunks
     */
    Collection<? extends CustomCropsChunk> getChunkStorage();

    /**
     * Get bukkit world
     * This would be null if the world is unloaded
     *
     * @return bukkit world
     */
    @Nullable
    World getWorld();

    /**
     * Get the world's name
     *
     * @return world's name
     */
    @NotNull
    String getWorldName();

    /**
     * Check if the chunk is loaded
     * The chunk would only be loaded when it has CustomCrops data and being loaded by minecraft chunk system
     *
     * @param chunkPos chunk position
     * @return loaded or not
     */
    boolean isChunkLoaded(ChunkPos chunkPos);

    /**
     * Create CustomCrops chunk at a certain chunk position
     * This method can only be called if that chunk is loaded
     * Otherwise it would fail
     *
     * @param chunkPos chunk position
     * @return the generated CustomCrops chunk
     */
    Optional<CustomCropsChunk> getOrCreateLoadedChunkAt(ChunkPos chunkPos);

    /**
     * Get the loaded CustomCrops chunk at a certain chunk position
     *
     * @param chunkPos chunk position
     * @return CustomCrops chunk
     */
    Optional<CustomCropsChunk> getLoadedChunkAt(ChunkPos chunkPos);

    /**
     * Get the loaded CustomCrops region at a certain region position
     *
     * @param regionPos region position
     * @return CustomCrops region
     */
    Optional<CustomCropsRegion> getLoadedRegionAt(RegionPos regionPos);

    /**
     * Load a CustomCrops region
     * You don't need to worry about the unloading since CustomCrops would unload the region
     * if it's unused
     *
     * @param region region
     */
    void loadRegion(CustomCropsRegion region);

    /**
     * Load a CustomCrops chunk
     * It's unsafe to call this method. Please use world.getChunkAt instead
     *
     * @param chunk chunk
     */
    void loadChunk(CustomCropsChunk chunk);

    /**
     * Unload a CustomCrops chunk
     * It's unsafe to call this method
     *
     * @param chunkPos chunk position
     */
    void unloadChunk(ChunkPos chunkPos);

    /**
     * Delete a chunk's data
     *
     * @param chunkPos chunk position
     */
    void deleteChunk(ChunkPos chunkPos);

    /**
     * Set the season and date of the world
     *
     * @param infoData info data
     */
    void setInfoData(WorldInfoData infoData);

    /**
     * Get the season and date
     * This might return the wrong value if sync-seasons is enabled
     *
     * @return info data
     */
    WorldInfoData getInfoData();

    /**
     * Get the date of the world
     * This would return the reference world's date if sync-seasons is enabled
     *
     * @return date
     */
    int getDate();

    /**
     * Get the season of the world
     * This would return the reference world's season if sync-seasons is enabled
     *
     * @return date
     */
    @Nullable
    Season getSeason();

    /**
     * Get sprinkler at a certain location
     *
     * @param location location
     * @return sprinkler data
     */
    Optional<WorldSprinkler> getSprinklerAt(SimpleLocation location);

    /**
     * Get pot at a certain location
     *
     * @param location location
     * @return pot data
     */
    Optional<WorldPot> getPotAt(SimpleLocation location);

    /**
     * Get crop at a certain location
     *
     * @param location location
     * @return crop data
     */
    Optional<WorldCrop> getCropAt(SimpleLocation location);

    /**
     * Get greenhouse glass at a certain location
     *
     * @param location location
     * @return greenhouse glass data
     */
    Optional<WorldGlass> getGlassAt(SimpleLocation location);

    /**
     * Get scarecrow at a certain location
     *
     * @param location location
     * @return scarecrow data
     */
    Optional<WorldScarecrow> getScarecrowAt(SimpleLocation location);

    /**
     * Get block data at a certain location
     *
     * @param location location
     * @return block data
     */
    Optional<CustomCropsBlock> getBlockAt(SimpleLocation location);

    /**
     * Add water to the sprinkler
     * This method would create new sprinkler data if the sprinkler data not exists in that place
     * This method would also update the sprinkler's model if it has models according to the water amount
     *
     * @param sprinkler sprinkler config
     * @param amount    amount of water
     * @param location  location
     */
    void addWaterToSprinkler(Sprinkler sprinkler, int amount, SimpleLocation location);

    /**
     * Add fertilizer to the pot
     * This method would create new pot data if the pot data not exists in that place
     * This method would update the pot's block state if it has appearance variations for different fertilizers
     *
     * @param pot pot config
     * @param fertilizer fertilizer config
     * @param location location
     */
    void addFertilizerToPot(Pot pot, Fertilizer fertilizer, SimpleLocation location);

    /**
     * Add water to the pot
     * This method would create new pot data if the pot data not exists in that place
     * This method would update the pot's block state if it's dry
     *
     * @param pot      pot config
     * @param amount   amount of water
     * @param location location
     */
    void addWaterToPot(Pot pot, int amount, SimpleLocation location);

    /**
     * Add points to a crop
     * This method would do nothing if the crop data not exists in that place
     * This method would change the stage of the crop and trigger the actions
     *
     * @param crop     crop config
     * @param points   points to add
     * @param location location
     */
    void addPointToCrop(Crop crop, int points, SimpleLocation location);

    /**
     * Remove sprinkler data from a certain location
     *
     * @param location location
     */
    @Nullable
    WorldSprinkler removeSprinklerAt(SimpleLocation location);

    /**
     * Remove pot data from a certain location
     *
     * @param location location
     */
    @Nullable
    WorldPot removePotAt(SimpleLocation location);

    /**
     * Remove crop data from a certain location
     *
     * @param location location
     */
    @Nullable
    WorldCrop removeCropAt(SimpleLocation location);

    /**
     * Remove greenhouse glass data from a certain location
     *
     * @param location location
     */
    @Nullable
    WorldGlass removeGlassAt(SimpleLocation location);

    /**
     * Remove scarecrow data from a certain location
     *
     * @param location location
     */
    @Nullable
    WorldScarecrow removeScarecrowAt(SimpleLocation location);

    /**
     * Remove any block data from a certain location
     *
     * @param location location
     * @return block data
     */
    @Nullable
    CustomCropsBlock removeAnythingAt(SimpleLocation location);

    /**
     * If the amount of pot reaches the limitation
     *
     * @param location location
     * @return reach or not
     */
    boolean isPotReachLimit(SimpleLocation location);

    /**
     * If the amount of crop reaches the limitation
     *
     * @param location location
     * @return reach or not
     */
    boolean isCropReachLimit(SimpleLocation location);

    /**
     * If the amount of sprinkler reaches the limitation
     *
     * @param location location
     * @return reach or not
     */
    boolean isSprinklerReachLimit(SimpleLocation location);

    /**
     * Add pot data
     *
     * @param pot pot data
     * @param location location
     */
    void addPotAt(WorldPot pot, SimpleLocation location);

    /**
     * Add sprinkler data
     *
     * @param sprinkler sprinkler data
     * @param location location
     */
    void addSprinklerAt(WorldSprinkler sprinkler, SimpleLocation location);

    /**
     * Add crop data
     *
     * @param crop crop data
     * @param location location
     */
    void addCropAt(WorldCrop crop, SimpleLocation location);

    /**
     * Add greenhouse glass data
     *
     * @param glass glass data
     * @param location location
     */
    void addGlassAt(WorldGlass glass, SimpleLocation location);

    /**
     * Add scarecrow data
     *
     * @param scarecrow scarecrow data
     * @param location location
     */
    void addScarecrowAt(WorldScarecrow scarecrow, SimpleLocation location);
}

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
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface CustomCropsChunk {

    /**
     * Calculate the unload time and perform compensation
     * This method can only be called in a loaded chunk
     */
    void notifyOfflineUpdates();

    /**
     * Get the world associated with the chunk
     *
     * @return CustomCrops world
     */
    CustomCropsWorld getCustomCropsWorld();

    /**
     * Get the region associated with the chunk
     *
     * @return CustomCrops region
     */
    CustomCropsRegion getCustomCropsRegion();

    /**
     * Get the position of the chunk
     *
     * @return chunk position
     */
    ChunkPos getChunkPos();

    /**
     * Do second timer
     */
    void secondTimer();

    /**
     * Get the unloaded time in seconds
     * This value would increase if the chunk is lazy
     *
     * @return the unloaded time
     */
    int getUnloadedSeconds();

    /**
     * Set the unloaded seconds
     *
     * @param unloadedSeconds unloadedSeconds
     */
    void setUnloadedSeconds(int unloadedSeconds);

    /**
     * Get the last loaded time
     *
     * @return last loaded time
     */
    long getLastLoadedTime();

    /**
     * Set the last loaded time to latest
     */
    void updateLastLoadedTime();

    /**
     * Get the loaded time in seconds
     *
     * @return loaded time
     */
    int getLoadedSeconds();

    /**
     * Get crop at a certain location
     *
     * @param location location
     * @return crop data
     */
    Optional<WorldCrop> getCropAt(SimpleLocation location);

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
    CustomCropsBlock removeBlockAt(SimpleLocation location);

    /**
     * Add a custom block data at a certain location
     *
     * @param block block data
     * @param location location
     * @return the previous block data
     */
    @Nullable
    CustomCropsBlock addBlockAt(CustomCropsBlock block, SimpleLocation location);

    /**
     * Get the amount of crops in this chunk
     *
     * @return the amount of crops
     */
    int getCropAmount();

    /**
     * Get the amount of pots in this chunk
     *
     * @return the amount of pots
     */
    int getPotAmount();

    /**
     * Get the amount of sprinklers in this chunk
     *
     * @return the amount of sprinklers
     */
    int getSprinklerAmount();

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

    /**
     * Get CustomCrops sections
     *
     * @return sections
     */
    CustomCropsSection[] getSections();

    /**
     * Get section by ID
     *
     * @param sectionID id
     * @return section
     */
    @Nullable
    CustomCropsSection getSection(int sectionID);

    /**
     * If the chunk can be pruned
     *
     * @return can be pruned or not
     */
    boolean canPrune();
}

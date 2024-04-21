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

package net.momirealms.customcrops.api.manager;

import net.momirealms.customcrops.api.common.Reloadable;
import net.momirealms.customcrops.api.mechanic.item.*;
import net.momirealms.customcrops.api.mechanic.world.AbstractWorldAdaptor;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.*;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface WorldManager extends Reloadable {

    /**
     * Load a specified world and convert it into a CustomCrops world
     * This method ignores the whitelist and blacklist
     * If there already exists one, it would not create a new instance but return the created one
     *
     * @param world world
     */
    @NotNull
    CustomCropsWorld loadWorld(@NotNull World world);

    /**
     * Unload a specified world and save it to file
     * This method ignores the whitelist and blacklist
     *
     * @param world world
     */
    boolean unloadWorld(@NotNull World world);

    /**
     * Check if the world has CustomCrops mechanisms
     *
     * @param world world
     * @return has or not
     */
    boolean isMechanicEnabled(@NotNull World world);

    /**
     * Get all the worlds loaded in CustomCrops
     *
     * @return worlds
     */
    @NotNull
    Collection<String> getWorldNames();

    /**
     * Get all the worlds loaded in CustomCrops
     *
     * @return worlds
     */
    @NotNull
    Collection<World> getBukkitWorlds();

    /**
     * Get all the worlds loaded in CustomCrops
     *
     * @return worlds
     */
    @NotNull
    Collection<? extends CustomCropsWorld> getCustomCropsWorlds();

    /**
     * Get CustomCrops world by name
     *
     * @param name name
     * @return CustomCrops world
     */
    @NotNull
    Optional<CustomCropsWorld> getCustomCropsWorld(@NotNull String name);

    /**
     * Get CustomCrops world by Bukkit world
     *
     * @param world world
     * @return CustomCrops world
     */
    @NotNull
    Optional<CustomCropsWorld> getCustomCropsWorld(@NotNull World world);

    /**
     * Get sprinkler at a certain location
     *
     * @param location location
     * @return sprinkler
     */
    @NotNull
    Optional<WorldSprinkler> getSprinklerAt(@NotNull SimpleLocation location);

    /**
     * Get pot at a certain location
     *
     * @param location location
     * @return pot
     */
    @NotNull
    Optional<WorldPot> getPotAt(@NotNull SimpleLocation location);

    /**
     * Get crop at a certain location
     *
     * @param location location
     * @return crop
     */
    @NotNull
    Optional<WorldCrop> getCropAt(@NotNull SimpleLocation location);

    /**
     * Get greenhouse glass at a certain location
     *
     * @param location location
     * @return greenhouse glass
     */
    @NotNull Optional<WorldGlass> getGlassAt(@NotNull SimpleLocation location);

    /**
     * Get scarecrow at a certain location
     *
     * @param location location
     * @return scarecrow
     */
    @NotNull Optional<WorldScarecrow> getScarecrowAt(@NotNull SimpleLocation location);

    /**
     * Get any CustomCrops block at a certain location
     * The block can be crop, sprinkler and etc.
     *
     * @param location location
     * @return CustomCrops block
     */
    Optional<CustomCropsBlock> getBlockAt(SimpleLocation location);

    /**
     * Create crop data
     *
     * @param location location
     * @param crop crop config
     * @param point initial point
     * @return the crop data
     */
    WorldCrop createCropData(SimpleLocation location, Crop crop, int point);

    /**
     * Create crop data
     *
     * @param location location
     * @param crop crop config
     * @return the crop data
     */
    default WorldCrop createCropData(SimpleLocation location, Crop crop) {
        return createCropData(location, crop, 0);
    }

    /**
     * Create sprinkler data
     *
     * @param location location
     * @param sprinkler sprinkler config
     * @param water initial water
     * @return the sprinkler data
     */
    WorldSprinkler createSprinklerData(SimpleLocation location, Sprinkler sprinkler, int water);

    /**
     * Create sprinkler data
     *
     * @param location location
     * @param sprinkler sprinkler config
     * @return the sprinkler data
     */
    default WorldSprinkler createSprinklerData(SimpleLocation location, Sprinkler sprinkler) {
        return createSprinklerData(location, sprinkler, 0);
    }

    /**
     * Create pot data
     *
     * @param location location
     * @param pot pot config
     * @param water initial water
     * @param fertilizer fertilizer config
     * @param fertilizerTimes the remaining usages of the fertilizer
     * @return the pot data
     */
    WorldPot createPotData(SimpleLocation location, Pot pot, int water, @Nullable Fertilizer fertilizer, int fertilizerTimes);

    /**
     * Create pot data
     *
     * @param location location
     * @param pot pot config
     * @return the pot data
     */
    default WorldPot createPotData(SimpleLocation location, Pot pot) {
        return createPotData(location, pot, 0, null, 0);
    }

    /**
     * Create Greenhouse glass data
     *
     * @param location location
     * @return the greenhouse glass data
     */
    WorldGlass createGreenhouseGlassData(SimpleLocation location);

    /**
     * Create scarecrow data
     *
     * @param location location
     * @return the scarecrow data
     */
    WorldScarecrow createScarecrowData(SimpleLocation location);

    /**
     * Add water to the sprinkler
     * This method would create new sprinkler data if the sprinkler data not exists in that place
     * This method would also update the sprinkler's model if it has models according to the water amount
     *
     * @param sprinkler sprinkler config
     * @param location location
     * @param amount amount of water
     */
    void addWaterToSprinkler(@NotNull Sprinkler sprinkler, @NotNull SimpleLocation location, int amount);

    /**
     * Add fertilizer to the pot
     * This method would create new pot data if the pot data not exists in that place
     * This method would update the pot's block state if it has appearance variations for different fertilizers
     *
     * @param pot pot config
     * @param fertilizer fertilizer config
     * @param location location
     */
    void addFertilizerToPot(@NotNull Pot pot, @NotNull Fertilizer fertilizer, @NotNull SimpleLocation location);

    /**
     * Add water to the pot
     * This method would create new pot data if the pot data not exists in that place
     * This method would update the pot's block state if it's dry
     *
     * @param pot      pot config
     * @param amount   amount of water
     * @param location location
     */
    void addWaterToPot(@NotNull Pot pot, int amount, @NotNull SimpleLocation location);

    /**
     * Add points to a crop
     * This method would do nothing if the crop data not exists in that place
     * This method would change the stage of the crop and trigger the actions
     *
     * @param crop     crop config
     * @param points   points to add
     * @param location location
     */
    void addPointToCrop(@NotNull Crop crop, int points, @NotNull SimpleLocation location);

    /**
     * Add greenhouse glass data
     *
     * @param glass glass data
     * @param location location
     */
    void addGlassAt(@NotNull WorldGlass glass, @NotNull SimpleLocation location);

    /**
     * Add pot data
     *
     * @param pot pot data
     * @param location location
     */
    void addPotAt(@NotNull WorldPot pot, @NotNull SimpleLocation location);

    /**
     * Add sprinkler data
     *
     * @param sprinkler sprinkler data
     * @param location location
     */
    void addSprinklerAt(@NotNull WorldSprinkler sprinkler, @NotNull SimpleLocation location);

    /**
     * Add crop data
     *
     * @param crop crop data
     * @param location location
     */
    void addCropAt(@NotNull WorldCrop crop, @NotNull SimpleLocation location);

    /**
     * Add scarecrow data
     *
     * @param scarecrow scarecrow data
     * @param location location
     */
    void addScarecrowAt(@NotNull WorldScarecrow scarecrow, @NotNull SimpleLocation location);

    /**
     * Remove sprinkler data from a certain location
     *
     * @param location location
     */
    @Nullable
    WorldSprinkler removeSprinklerAt(@NotNull SimpleLocation location);

    /**
     * Remove pot data from a certain location
     *
     * @param location location
     */
    @Nullable
    WorldPot removePotAt(@NotNull SimpleLocation location);

    /**
     * Remove crop data from a certain location
     *
     * @param location location
     */
    @Nullable
    WorldCrop removeCropAt(@NotNull SimpleLocation location);

    /**
     * Remove greenhouse glass data from a certain location
     *
     * @param location location
     */
    @Nullable
    WorldGlass removeGlassAt(@NotNull SimpleLocation location);

    /**
     * Remove scarecrow data from a certain location
     *
     * @param location location
     */
    @Nullable
    WorldScarecrow removeScarecrowAt(@NotNull SimpleLocation location);

    /**
     * If a certain type of item reached the limitation
     *
     * @param location location
     * @param itemType the type of the item
     * @return reached or not
     */
    boolean isReachLimit(SimpleLocation location, ItemType itemType);

    /**
     * Handle the load of a chunk
     * It's recommended to call world.getChunkAt(x,z), otherwise you have to manually control the load/unload process
     *
     * @param bukkitChunk chunk
     */
    void handleChunkLoad(Chunk bukkitChunk);

    /**
     * Handle the unload of a chunk
     *
     * @param bukkitChunk chunk
     */
    void handleChunkUnload(Chunk bukkitChunk);

    /**
     * Save a chunk to region (from memory to memory)
     *
     * @param chunk the chunk to save
     */
    void saveChunkToCachedRegion(CustomCropsChunk chunk);

    /**
     * Save a region to file (from memory to disk)
     *
     * @param region the region to save
     */
    void saveRegionToFile(CustomCropsRegion region);

    /**
     * Remove any block data from a certain location
     *
     * @param location location
     * @return block data
     */
    CustomCropsBlock removeAnythingAt(SimpleLocation location);

    /**
     * Get the world adaptor
     *
     * @return the world adaptor
     */
    AbstractWorldAdaptor getWorldAdaptor();

    /**
     * Save a world's season and date
     *
     * @param customCropsWorld the world to save
     */
    void saveInfoData(CustomCropsWorld customCropsWorld);
}

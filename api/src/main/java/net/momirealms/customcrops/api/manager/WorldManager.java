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
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.*;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    Optional<CustomCropsWorld> getCustomCropsWorld(@NotNull String name);

    @NotNull
    Optional<CustomCropsWorld> getCustomCropsWorld(@NotNull World world);

    @NotNull
    Optional<WorldSprinkler> getSprinklerAt(@NotNull SimpleLocation location);

    @NotNull
    Optional<WorldPot> getPotAt(@NotNull SimpleLocation location);

    @NotNull
    Optional<WorldCrop> getCropAt(@NotNull SimpleLocation location);

    @NotNull Optional<WorldGlass> getGlassAt(@NotNull SimpleLocation location);

    @NotNull Optional<WorldScarecrow> getScarecrowAt(@NotNull SimpleLocation location);

    Optional<CustomCropsBlock> getBlockAt(SimpleLocation location);

    void addWaterToSprinkler(@NotNull Sprinkler sprinkler, @NotNull SimpleLocation location, int amount);

    void addFertilizerToPot(@NotNull Pot pot, @NotNull Fertilizer fertilizer, @NotNull SimpleLocation location);

    void addWaterToPot(@NotNull Pot pot, @NotNull SimpleLocation location, int amount);

    void addGlassAt(@NotNull WorldGlass glass, @NotNull SimpleLocation location);

    void addScarecrowAt(@NotNull WorldScarecrow scarecrow, @NotNull SimpleLocation location);

    void removeSprinklerAt(@NotNull SimpleLocation location);

    void removePotAt(@NotNull SimpleLocation location);

    void removeCropAt(@NotNull SimpleLocation location);

    boolean isReachLimit(SimpleLocation location, ItemType itemType);

    void addPotAt(@NotNull WorldPot pot, @NotNull SimpleLocation location);

    void addSprinklerAt(@NotNull WorldSprinkler sprinkler, @NotNull SimpleLocation location);

    void addCropAt(@NotNull WorldCrop crop, @NotNull SimpleLocation location);

    void addPointToCrop(@NotNull Crop crop, @NotNull SimpleLocation location, int points);

    void handleChunkLoad(Chunk bukkitChunk);

    void handleChunkUnload(Chunk bukkitChunk);

    void saveChunkToFile(CustomCropsChunk chunk);

    void removeGlassAt(@NotNull SimpleLocation location);

    void removeScarecrowAt(@NotNull SimpleLocation location);

    CustomCropsBlock removeAnythingAt(SimpleLocation location);
}

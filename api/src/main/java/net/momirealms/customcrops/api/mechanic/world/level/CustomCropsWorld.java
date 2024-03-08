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
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public interface CustomCropsWorld {

    void startTick();

    void cancelTick();

    CustomCropsChunk removeLazyChunkAt(ChunkCoordinate chunkCoordinate);

    WorldSetting getWorldSetting();

    void setWorldSetting(WorldSetting setting);

    Collection<? extends CustomCropsChunk> getChunkStorage();

    World getWorld();

    String getWorldName();

    boolean isChunkLoaded(ChunkCoordinate chunkCoordinate);

    Optional<CustomCropsChunk> getChunkAt(ChunkCoordinate chunkCoordinate);

    void loadChunk(CustomCropsChunk chunk);

    void unloadChunk(ChunkCoordinate chunkCoordinate);

    void setInfoData(WorldInfoData infoData);

    WorldInfoData getInfoData();

    @Nullable
    Season getSeason();

    Optional<WorldSprinkler> getSprinklerAt(SimpleLocation location);

    Optional<WorldPot> getPotAt(SimpleLocation location);

    Optional<WorldCrop> getCropAt(SimpleLocation location);

    Optional<WorldGlass> getGlassAt(SimpleLocation location);

    Optional<WorldScarecrow> getScarecrowAt(SimpleLocation location);

    Optional<CustomCropsBlock> getBlockAt(SimpleLocation location);

    void addWaterToSprinkler(Sprinkler sprinkler, SimpleLocation location, int amount);

    void addFertilizerToPot(Pot pot, Fertilizer fertilizer, SimpleLocation location);

    void addWaterToPot(Pot pot, SimpleLocation location, int amount);

    void removeSprinklerAt(SimpleLocation location);

    void removePotAt(SimpleLocation location);

    void removeCropAt(SimpleLocation location);

    void removeGlassAt(SimpleLocation location);

    void removeScarecrowAt(SimpleLocation location);

    CustomCropsBlock removeAnythingAt(SimpleLocation location);

    boolean isPotReachLimit(SimpleLocation location);

    boolean isCropReachLimit(SimpleLocation location);

    boolean isSprinklerReachLimit(SimpleLocation location);

    void addPotAt(WorldPot pot, SimpleLocation location);

    void addSprinklerAt(WorldSprinkler sprinkler, SimpleLocation location);

    void addCropAt(WorldCrop crop, SimpleLocation location);

    void addPointToCrop(Crop crop, SimpleLocation location, int points);

    void addGlassAt(WorldGlass glass, SimpleLocation location);

    void addScarecrowAt(WorldScarecrow scarecrow, SimpleLocation location);
}

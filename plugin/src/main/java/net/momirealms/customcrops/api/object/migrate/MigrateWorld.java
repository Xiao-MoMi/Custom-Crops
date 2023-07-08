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

package net.momirealms.customcrops.api.object.migrate;

import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.object.crop.GrowingCrop;
import net.momirealms.customcrops.api.object.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.sprinkler.Sprinkler;
import net.momirealms.customcrops.api.object.world.CCChunk;
import net.momirealms.customcrops.api.object.world.ChunkCoordinate;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.util.ConfigUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MigrateWorld extends Function {

    private final String worldName;
    private final ConcurrentHashMap<ChunkCoordinate, CCChunk> chunkMap;

    public MigrateWorld(String world) {
        this.worldName = world;
        this.chunkMap = new ConcurrentHashMap<>(64);
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void init() {
        File chunks_folder = ConfigUtils.getFile(worldName, "chunks");
        if (!chunks_folder.exists()) chunks_folder.mkdirs();
        File[] data_files = chunks_folder.listFiles();
        if (data_files == null) return;
        for (File file : data_files) {
            ChunkCoordinate chunkCoordinate = ChunkCoordinate.getByString(file.getName().substring(0, file.getName().length() - 7));
            try (FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis)) {
                CCChunk chunk = (CCChunk) ois.readObject();
                if (chunk.isUseless()) {
                    file.delete();
                    continue;
                }
                if (chunkCoordinate != null) chunkMap.put(chunkCoordinate, chunk);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void disable() {
        File chunks_folder = ConfigUtils.getFile(worldName, "chunks");
        if (!chunks_folder.exists()) chunks_folder.mkdirs();
        for (Map.Entry<ChunkCoordinate, CCChunk> entry : chunkMap.entrySet()) {
            ChunkCoordinate chunkCoordinate = entry.getKey();
            CCChunk chunk = entry.getValue();
            String fileName = chunkCoordinate.getFileName() + ".ccdata";
            File file = new File(chunks_folder, fileName);
            if (chunk.isUseless() && file.exists()) {
                file.delete();
                continue;
            }
            try (FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(chunk);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getWorldName() {
        return worldName;
    }

    public void removePotData(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return;
        chunk.removePotData(simpleLocation);
    }

    public void removeCropData(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return;
        chunk.removeCropData(simpleLocation);
    }

    public void addCropData(SimpleLocation simpleLocation, GrowingCrop growingCrop) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addCropData(simpleLocation, growingCrop);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addCropData(simpleLocation, growingCrop);
    }

    public GrowingCrop getCropData(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            return chunk.getCropData(simpleLocation);
        }
        return null;
    }

    public int getChunkCropAmount(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return 0;
        return chunk.getCropAmount();
    }

    public void removeGreenhouse(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return;
        chunk.removeGreenhouse(simpleLocation);
    }

    public void addGreenhouse(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addGreenhouse(simpleLocation);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addGreenhouse(simpleLocation);
    }

    public boolean isGreenhouse(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return false;
        return chunk.isGreenhouse(simpleLocation);
    }

    public void removeScarecrow(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return;
        chunk.removeScarecrow(simpleLocation);
    }

    public void addScarecrow(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addScarecrow(simpleLocation);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addScarecrow(simpleLocation);
    }

    public boolean hasScarecrow(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return false;
        return chunk.hasScarecrow();
    }

    public void removeSprinklerData(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return;
        chunk.removeSprinklerData(simpleLocation);
    }

    public void addSprinklerData(SimpleLocation simpleLocation, Sprinkler sprinkler) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addSprinklerData(simpleLocation, sprinkler);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addSprinklerData(simpleLocation, sprinkler);
    }

    @Nullable
    public Sprinkler getSprinklerData(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return null;
        return chunk.getSprinklerData(simpleLocation);
    }

    public void addWaterToPot(SimpleLocation simpleLocation, int amount, @NotNull String pot_id) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addWaterToPot(simpleLocation, amount, pot_id);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addWaterToPot(simpleLocation, amount, pot_id);
    }

    public void addFertilizerToPot(SimpleLocation simpleLocation, Fertilizer fertilizer, @NotNull String pot_id) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addFertilizerToPot(simpleLocation, fertilizer, pot_id);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addFertilizerToPot(simpleLocation, fertilizer, pot_id);
    }

    public Pot getPotData(SimpleLocation simpleLocation) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk == null) return null;
        return chunk.getPotData(simpleLocation);
    }

    public void addPotData(SimpleLocation simpleLocation, Pot pot) {
        CCChunk chunk = chunkMap.get(simpleLocation.getChunkCoordinate());
        if (chunk != null) {
            chunk.addPotData(simpleLocation, pot);
            return;
        }
        chunk = createNewChunk(simpleLocation);
        chunk.addPotData(simpleLocation, pot);
    }

    public CCChunk createNewChunk(SimpleLocation simpleLocation) {
        CCChunk newChunk = new CCChunk();
        chunkMap.put(simpleLocation.getChunkCoordinate(), newChunk);
        return newChunk;
    }
}
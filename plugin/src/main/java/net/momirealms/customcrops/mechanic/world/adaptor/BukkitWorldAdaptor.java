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

package net.momirealms.customcrops.mechanic.world.adaptor;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.github.luben.zstd.Zstd;
import com.google.gson.Gson;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsChunk;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import net.momirealms.customcrops.api.mechanic.world.level.WorldInfoData;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.mechanic.world.*;
import net.momirealms.customcrops.mechanic.world.block.MemoryCrop;
import net.momirealms.customcrops.mechanic.world.block.MemoryPot;
import net.momirealms.customcrops.mechanic.world.block.MemorySprinkler;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitWorldAdaptor extends AbstractWorldAdaptor {

    private static final NamespacedKey key = new NamespacedKey(CustomCropsPlugin.get(), "data");
    private final Gson gson;
    private final String worldFolder;

    public BukkitWorldAdaptor(WorldManager worldManager, String worldFolder) {
        super(worldManager);
        this.gson = new Gson();
        this.worldFolder = worldFolder;
    }

    @Override
    public void unload(CustomCropsWorld customCropsWorld) {
        CWorld cWorld = (CWorld) customCropsWorld;
        World world = cWorld.getWorld();
        if (world == null) {
            LogUtils.severe("Unexpected issue: World " + cWorld.getWorldName() + " unloaded before data saved");
            return;
        }

        // save world data into psd
        world.getPersistentDataContainer().set(key, PersistentDataType.STRING,
                gson.toJson(cWorld.getInfoData()));

        new File(world.getWorldFolder(), "customcrops").mkdir();

        for (CChunk chunk : cWorld.getAllChunksToSave()) {
            saveDynamicData(cWorld, chunk);
        }
    }

    @Override
    public void init(CustomCropsWorld customCropsWorld) {
        CWorld cWorld = (CWorld) customCropsWorld;
        World world = cWorld.getWorld();
        if (world == null) {
            LogUtils.severe("Unexpected issue: World " + cWorld.getWorldName() + " unloaded before data loaded");
            return;
        }

        // init world basic info
        String json = world.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        WorldInfoData data = json == null ? WorldInfoData.empty() : gson.fromJson(json, WorldInfoData.class);
        cWorld.setInfoData(data);

        new File(world.getWorldFolder(), "customcrops").mkdir();
    }

    @Override
    public void loadDynamicData(CustomCropsWorld customCropsWorld, ChunkCoordinate chunkCoordinate) {
        CWorld cWorld = (CWorld) customCropsWorld;
        World world = cWorld.getWorld();
        if (world == null) {
            LogUtils.severe("Unexpected issue: World " + cWorld.getWorldName() + " unloaded before data loaded");
            return;
        }

        // load lazy chunks firstly
        CustomCropsChunk lazyChunk = customCropsWorld.removeLazyChunkAt(chunkCoordinate);
        if (lazyChunk != null) {
            CChunk cChunk = (CChunk) lazyChunk;
            cChunk.setUnloadedSeconds(0);
            cWorld.loadChunk(cChunk);
            return;
        }
        // create or get chunk files
        File data = getChunkDataFilePath(world, chunkCoordinate);
        if (!data.exists())
            return;
        // load chunk from local files
        long time1 = System.currentTimeMillis();
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(getChunkDataFilePath(world, chunkCoordinate)))) {
            DataInputStream dataStream = new DataInputStream(bis);
            CChunk chunk = deserialize(cWorld, dataStream);
            dataStream.close();
            cWorld.loadChunk(chunk);
            long time2 = System.currentTimeMillis();
            CustomCropsPlugin.get().debug("Took " + (time2-time1) + "ms to load chunk " + chunkCoordinate);
        } catch (IOException e) {
            LogUtils.severe("Failed to load CustomCrops data at " + chunkCoordinate);
            e.printStackTrace();
        }
    }

    @Override
    public void unloadDynamicData(CustomCropsWorld ccWorld, ChunkCoordinate chunkCoordinate) {
        CWorld cWorld = (CWorld) ccWorld;
        World world = cWorld.getWorld();
        if (world == null) {
            LogUtils.severe("Unexpected issue: World " + cWorld.getWorldName() + " unloaded before data loaded");
            return;
        }

        cWorld.unloadChunk(chunkCoordinate);
    }

    @Override
    public void saveDynamicData(CustomCropsWorld ccWorld, CustomCropsChunk chunk) {
        long time1 = System.currentTimeMillis();
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(getChunkDataFilePath(ccWorld.getWorld(), chunk.getChunkCoordinate())))) {
            bos.write(serialize((CChunk) chunk));
            long time2 = System.currentTimeMillis();
            CustomCropsPlugin.get().debug("Took " + (time2-time1) + "ms to save chunk " + chunk.getChunkCoordinate());
        } catch (IOException e) {
            LogUtils.severe("Failed to save CustomCrops data.");
            e.printStackTrace();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent event) {
        if (worldManager.isMechanicEnabled(event.getWorld()))
            worldManager.loadWorld(event.getWorld());
    }

    @EventHandler (ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        if (worldManager.isMechanicEnabled(event.getWorld()))
            worldManager.unloadWorld(event.getWorld());
    }

    private String getChunkDataFile(ChunkCoordinate chunkCoordinate) {
        return chunkCoordinate.x() + "," + chunkCoordinate.z() + ".ccd";
    }

    private File getChunkDataFilePath(World world, ChunkCoordinate chunkCoordinate) {
        if (worldFolder.isEmpty()) {
            return new File(world.getWorldFolder(), "customcrops" + File.separator + getChunkDataFile(chunkCoordinate));
        } else {
            return new File(worldFolder, world.getName() + File.separator + "customcrops" + File.separator + getChunkDataFile(chunkCoordinate));
        }
    }

    public byte[] serialize(CChunk chunk) {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(outByteStream);
        SerializableChunk serializableChunk = SerializationUtils.toSerializableChunk(chunk);

        try {
            outStream.writeByte(version);
            outStream.writeInt(serializableChunk.getX());
            outStream.writeInt(serializableChunk.getZ());
            outStream.writeInt(serializableChunk.getLoadedSeconds());
            outStream.writeLong(serializableChunk.getLastLoadedTime());

            List<CompoundTag> blocksToSave = serializableChunk.getBlocks();
            byte[] serializedBlocks = serializeBlocks(blocksToSave);
            byte[] compressed = Zstd.compress(serializedBlocks);

            outStream.writeInt(compressed.length);
            outStream.writeInt(serializedBlocks.length);
            outStream.write(compressed);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return outByteStream.toByteArray();
    }

    public CChunk deserialize(CWorld world, DataInputStream dataStream) throws IOException {
        int worldVersion = dataStream.readByte();
        int x = dataStream.readInt();
        int z = dataStream.readInt();
        ChunkCoordinate chunkCoordinate = new ChunkCoordinate(x, z);
        int loadedSeconds = dataStream.readInt();
        long lastLoadedTime = dataStream.readLong();
        byte[] blockData = readCompressedBytes(dataStream);
        var blockMap = deserializeBlocks(blockData);
        return new CChunk(world, chunkCoordinate, loadedSeconds, lastLoadedTime, blockMap);
    }

    private ConcurrentHashMap<ChunkPos, CustomCropsBlock> deserializeBlocks(byte[] bytes) throws IOException {
        DataInputStream chunkData = new DataInputStream(new ByteArrayInputStream(bytes));
        int blocks = chunkData.readInt();
        ConcurrentHashMap<ChunkPos, CustomCropsBlock> blockMap = new ConcurrentHashMap<>(blocks);
        for (int i = 0; i < blocks; i++) {
            byte[] blockData = new byte[chunkData.readInt()];
            chunkData.read(blockData);
            CompoundMap block = readCompound(blockData).getValue();
            String type = (String) block.get("type").getValue();
            CompoundMap data = (CompoundMap) block.get("data").getValue();
            switch (type) {
                case "CROP" -> {
                    for (int pos : (int[]) block.get("pos").getValue()) {
                        blockMap.put(new ChunkPos(pos), new MemoryCrop(data));
                    }
                }
                case "POT" -> {
                    for (int pos : (int[]) block.get("pos").getValue()) {
                        blockMap.put(new ChunkPos(pos), new MemoryPot(data));
                    }
                }
                case "SPRINKLER" -> {
                    for (int pos : (int[]) block.get("pos").getValue()) {
                        blockMap.put(new ChunkPos(pos), new MemorySprinkler(data));
                    }
                }
            }
        }
        return blockMap;
    }

    private byte[] serializeBlocks(Collection<CompoundTag> blocks) throws IOException {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream(16384);
        DataOutputStream outStream = new DataOutputStream(outByteStream);
        outStream.writeInt(blocks.size());
        for (CompoundTag block : blocks) {
            byte[] blockData = serializeCompoundTag(block);
            outStream.writeInt(blockData.length);
            outStream.write(blockData);
        }
        return outByteStream.toByteArray();
    }

    private byte[] readCompressedBytes(DataInputStream dataStream) throws IOException {
        int compressedLength = dataStream.readInt();
        int decompressedLength = dataStream.readInt();
        byte[] compressedData = new byte[compressedLength];
        byte[] decompressedData = new byte[decompressedLength];

        dataStream.read(compressedData);
        Zstd.decompress(decompressedData, compressedData);
        return decompressedData;
    }

    private CompoundTag readCompound(byte[] bytes) throws IOException {
        if (bytes.length == 0)
            return null;
        NBTInputStream nbtInputStream = new NBTInputStream(
                new ByteArrayInputStream(bytes),
                NBTInputStream.NO_COMPRESSION,
                ByteOrder.BIG_ENDIAN
        );
        return (CompoundTag) nbtInputStream.readTag();
    }

    private byte[] serializeCompoundTag(CompoundTag tag) throws IOException {
        if (tag == null || tag.getValue().isEmpty())
            return new byte[0];
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        NBTOutputStream outStream = new NBTOutputStream(
                outByteStream,
                NBTInputStream.NO_COMPRESSION,
                ByteOrder.BIG_ENDIAN
        );
        outStream.writeTag(tag);
        return outByteStream.toByteArray();
    }
}

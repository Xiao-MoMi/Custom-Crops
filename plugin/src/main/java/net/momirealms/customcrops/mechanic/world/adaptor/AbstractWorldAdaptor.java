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
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import net.momirealms.customcrops.mechanic.world.CChunk;
import net.momirealms.customcrops.mechanic.world.CWorld;
import net.momirealms.customcrops.mechanic.world.NBTUtils;
import net.momirealms.customcrops.mechanic.world.SerializableChunk;
import net.momirealms.customcrops.mechanic.world.block.MemoryCrop;
import net.momirealms.customcrops.mechanic.world.block.MemoryPot;
import net.momirealms.customcrops.mechanic.world.block.MemorySprinkler;
import org.bukkit.event.Listener;

import java.io.*;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractWorldAdaptor implements Listener {

    public static final int version = 1;
    protected WorldManager worldManager;
    protected LZ4Compressor compressor;
    protected LZ4FastDecompressor decompressor;

    public AbstractWorldAdaptor(WorldManager worldManager) {
        this.worldManager = worldManager;
        LZ4Factory factory = LZ4Factory.fastestInstance();
        compressor = factory.fastCompressor();
        decompressor = factory.fastDecompressor();
    }

    public abstract void unload(CustomCropsWorld customCropsWorld);

    public abstract void init(CustomCropsWorld customCropsWorld);

    public abstract void loadAllData(CustomCropsWorld customCropsWorld);

    public abstract void loadDynamicData(CustomCropsWorld customCropsWorld, ChunkCoordinate chunkCoordinate);

    public abstract void unloadDynamicData(CustomCropsWorld customCropsWorld, ChunkCoordinate chunkCoordinate);

    public byte[] serialize(CChunk chunk) {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(outByteStream);

        SerializableChunk serializableChunk = NBTUtils.toSerializableChunk(chunk);

        try {
            outStream.writeByte(version);
            outStream.writeInt(serializableChunk.getX());
            outStream.writeInt(serializableChunk.getX());
            outStream.writeInt(serializableChunk.getLoadedSeconds());
            outStream.writeLong(serializableChunk.getLastLoadedTime());

            List<CompoundTag> blocksToSave = serializableChunk.getBlocks();
            byte[] serializedBlocks = serializeBlocks(blocksToSave);

            long time1 = System.currentTimeMillis();
            int maxCompressedLength = compressor.maxCompressedLength(serializedBlocks.length);
            byte[] compressed = new byte[maxCompressedLength];
            int compressedLength = compressor.compress(serializedBlocks, 0, serializedBlocks.length, compressed, 0, maxCompressedLength);
            long time2 = System.currentTimeMillis();
            System.out.println("压缩花了" + (time2 - time1) + "ms");

            outStream.writeInt(compressedLength);
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

        long time1 = System.currentTimeMillis();
        byte[] blockData = readCompressedBytes(dataStream);
        long time2 = System.currentTimeMillis();
        System.out.println("解压缩花了" + (time2 - time1) + "ms");
        var blockMap = deserializeBlocks(world.getWorldName(), blockData);

        return new CChunk(world, chunkCoordinate, loadedSeconds, lastLoadedTime, blockMap);
    }

    private ConcurrentHashMap<SimpleLocation, CustomCropsBlock> deserializeBlocks(String world, byte[] bytes) throws IOException {
        DataInputStream chunkData = new DataInputStream(new ByteArrayInputStream(bytes));
        int blocks = chunkData.readInt();
        ConcurrentHashMap<SimpleLocation, CustomCropsBlock> blockMap = new ConcurrentHashMap<>(blocks);
        for (int i = 0; i < blocks; i++) {
            byte[] blockData = new byte[chunkData.readInt()];
            chunkData.read(blockData);
            Tag<CompoundMap> block = readCompound(blockData);
            if (block != null) {
                CompoundMap values = block.getValue();
                String type = values.get("type").getAsStringTag().get().getValue();
                int[] pos = values.get("pos").getAsIntArrayTag().get().getValue();
                SimpleLocation location = new SimpleLocation(world, pos[0], pos[1], pos[2]);
                switch (type) {
                    case "CROP" -> blockMap.put(location, new MemoryCrop(values.get("data").getAsCompoundTag().get().getValue()));
                    case "POT" -> blockMap.put(location, new MemoryPot(values.get("data").getAsCompoundTag().get().getValue()));
                    case "SPRINKLER" -> blockMap.put(location, new MemorySprinkler(values.get("data").getAsCompoundTag().get().getValue()));
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
        decompressor.decompress(compressedData, 0, decompressedData, 0, decompressedLength);
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

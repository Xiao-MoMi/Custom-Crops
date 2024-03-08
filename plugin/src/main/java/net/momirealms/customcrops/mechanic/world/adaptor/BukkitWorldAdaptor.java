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
import com.flowpowered.nbt.IntArrayTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.github.luben.zstd.Zstd;
import com.google.gson.Gson;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsChunk;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import net.momirealms.customcrops.api.mechanic.world.level.WorldInfoData;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.mechanic.world.*;
import net.momirealms.customcrops.mechanic.world.block.*;
import net.momirealms.customcrops.scheduler.task.TickTask;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitWorldAdaptor extends AbstractWorldAdaptor {

    private static final NamespacedKey key = new NamespacedKey(CustomCropsPlugin.get(), "data");
    private final Gson gson;
    private String worldFolder;

    public BukkitWorldAdaptor(WorldManager worldManager) {
        super(worldManager);
        this.gson = new Gson();
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
        WorldInfoData data = (json == null || json.equals("null")) ? WorldInfoData.empty() : gson.fromJson(json, WorldInfoData.class);
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
        if (worldManager.isMechanicEnabled(event.getWorld())) {
            worldManager.loadWorld(event.getWorld());
        }
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

    public void setWorldFolder(String folder) {
        this.worldFolder = folder;
    }

    public byte[] serialize(CChunk chunk) {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(outByteStream);
        SerializableChunk serializableChunk = toSerializableChunk(chunk);
        try {
            outStream.writeByte(version);
            byte[] serializedSections = serializeChunk(serializableChunk);
            byte[] compressed = Zstd.compress(serializedSections);
            outStream.writeInt(compressed.length);
            outStream.writeInt(serializedSections.length);
            outStream.write(compressed);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outByteStream.toByteArray();
    }

    public CChunk deserialize(CWorld world, DataInputStream dataStream) throws IOException {
        int worldVersion = dataStream.readByte();
        byte[] blockData = readCompressedBytes(dataStream);
        return deserializeChunk(world, blockData);
    }

    private CChunk deserializeChunk(CWorld cWorld, byte[] bytes) throws IOException {
        String world = cWorld.getWorldName();
        DataInputStream chunkData = new DataInputStream(new ByteArrayInputStream(bytes));
        // read coordinate
        int x = chunkData.readInt();
        int z = chunkData.readInt();
        ChunkCoordinate coordinate = new ChunkCoordinate(x, z);
        // read loading info
        int loadedSeconds = chunkData.readInt();
        long lastLoadedTime = chunkData.readLong();
        // read task queue
        int tasksSize = chunkData.readInt();
        PriorityQueue<TickTask> queue = new PriorityQueue<>(Math.max(11, tasksSize));
        for (int i = 0; i < tasksSize; i++) {
            int time = chunkData.readInt();
            ChunkPos pos = new ChunkPos(chunkData.readInt());
            queue.add(new TickTask(time, pos));
        }
        // read ticked blocks
        int tickedSize = chunkData.readInt();
        HashSet<ChunkPos> tickedSet = new HashSet<>(Math.max(11, tickedSize));
        for (int i = 0; i < tickedSize; i++) {
            tickedSet.add(new ChunkPos(chunkData.readInt()));
        }
        // read block data
        ConcurrentHashMap<Integer, CSection> sectionMap = new ConcurrentHashMap<>();
        int sections = chunkData.readInt();
        // read sections
        for (int i = 0; i < sections; i++) {
            ConcurrentHashMap<ChunkPos, CustomCropsBlock> blockMap = new ConcurrentHashMap<>();
            int sectionID = chunkData.readInt();
            byte[] sectionBytes = new byte[chunkData.readInt()];
            chunkData.read(sectionBytes);
            DataInputStream sectionData = new DataInputStream(new ByteArrayInputStream(sectionBytes));
            int blockAmount = sectionData.readInt();
            // read blocks
            for (int j = 0; j < blockAmount; j++){
                byte[] blockData = new byte[sectionData.readInt()];
                sectionData.read(blockData);
                CompoundMap block = readCompound(blockData).getValue();
                String type = (String) block.get("type").getValue();
                CompoundMap data = (CompoundMap) block.get("data").getValue();
                switch (type) {
                    case "CROP" -> {
                        for (int pos : (int[]) block.get("pos").getValue()) {
                            ChunkPos chunkPos = new ChunkPos(pos);
                            blockMap.put(chunkPos, new MemoryCrop(chunkPos.getLocation(world, coordinate), new CompoundMap(data)));
                        }
                    }
                    case "POT" -> {
                        for (int pos : (int[]) block.get("pos").getValue()) {
                            ChunkPos chunkPos = new ChunkPos(pos);
                            blockMap.put(chunkPos, new MemoryPot(chunkPos.getLocation(world, coordinate), new CompoundMap(data)));
                        }
                    }
                    case "SPRINKLER" -> {
                        for (int pos : (int[]) block.get("pos").getValue()) {
                            ChunkPos chunkPos = new ChunkPos(pos);
                            blockMap.put(chunkPos, new MemorySprinkler(chunkPos.getLocation(world, coordinate), new CompoundMap(data)));
                        }
                    }
                    case "SCARECROW" -> {
                        for (int pos : (int[]) block.get("pos").getValue()) {
                            ChunkPos chunkPos = new ChunkPos(pos);
                            blockMap.put(chunkPos, new MemoryScarecrow(chunkPos.getLocation(world, coordinate), new CompoundMap(data)));
                        }
                    }
                    case "GLASS" -> {
                        for (int pos : (int[]) block.get("pos").getValue()) {
                            ChunkPos chunkPos = new ChunkPos(pos);
                            blockMap.put(chunkPos, new MemoryGlass(chunkPos.getLocation(world, coordinate), new CompoundMap(data)));
                        }
                    }
                }
            }
            CSection cSection = new CSection(sectionID, blockMap);
            sectionMap.put(sectionID, cSection);
        }

        return new CChunk(cWorld, coordinate, loadedSeconds, lastLoadedTime, sectionMap, queue, tickedSet);
    }

    private byte[] serializeChunk(SerializableChunk chunk) throws IOException {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream(16384);
        DataOutputStream outStream = new DataOutputStream(outByteStream);
        outStream.writeInt(chunk.getX());
        outStream.writeInt(chunk.getZ());
        outStream.writeInt(chunk.getLoadedSeconds());
        outStream.writeLong(chunk.getLastLoadedTime());
        // write queue
        int[] queue = chunk.getQueuedTasks();
        outStream.writeInt(queue.length / 2);
        for (int i : queue) {
            outStream.writeInt(i);
        }
        // write ticked blocks
        int[] tickedSet = chunk.getTicked();
        outStream.writeInt(tickedSet.length);
        for (int i : tickedSet) {
            outStream.writeInt(i);
        }
        // write block data
        List<SerializableSection> sectionsToSave = chunk.getSections();
        outStream.writeInt(sectionsToSave.size());
        for (SerializableSection section : sectionsToSave) {
            outStream.writeInt(section.getSectionID());
            byte[] blockData = serializeBlocks(section.getBlocks());
            outStream.writeInt(blockData.length);
            outStream.write(blockData);
        }
        return outByteStream.toByteArray();
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

    public SerializableChunk toSerializableChunk(CChunk chunk) {
        ChunkCoordinate chunkCoordinate = chunk.getChunkCoordinate();
        return new SerializableChunk(
                chunkCoordinate.x(),
                chunkCoordinate.z(),
                chunk.getLoadedSeconds(),
                chunk.getLastLoadedTime(),
                Arrays.stream(chunk.getSectionsForSerialization()).map(this::toSerializableSection).toList(),
                queueToIntArray(chunk.getQueue()),
                tickedBlocksToArray(chunk.getTickedBlocks())
        );
    }

    private int[] tickedBlocksToArray(Set<ChunkPos> set) {
        int[] ticked = new int[set.size()];
        int i = 0;
        for (ChunkPos pos : set) {
            ticked[i] = pos.getPosition();
            i++;
        }
        return ticked;
    }

    private int[] queueToIntArray(PriorityQueue<TickTask> queue) {
        int size = queue.size() * 2;
        int[] tasks = new int[size];
        int i = 0;
        for (TickTask task : queue) {
            tasks[i * 2] = task.getTime();
            tasks[i * 2 + 1] = task.getChunkPos().getPosition();
            i++;
        }
        return tasks;
    }

    private SerializableSection toSerializableSection(CSection section) {
        return new SerializableSection(section.getSectionID(), toCompoundTags(section.getBlockMap()));
    }

    private List<CompoundTag> toCompoundTags(Map<ChunkPos, CustomCropsBlock> blocks) {
        ArrayList<CompoundTag> tags = new ArrayList<>(blocks.size());
        Map<CustomCropsBlock, List<Integer>> blockToPosMap = new HashMap<>();
        for (Map.Entry<ChunkPos, CustomCropsBlock> entry : blocks.entrySet()) {
            ChunkPos coordinate = entry.getKey();
            CustomCropsBlock block = entry.getValue();
            List<Integer> coordinates = blockToPosMap.computeIfAbsent(block, k -> new ArrayList<>());
            coordinates.add(coordinate.getPosition());
        }
        for (Map.Entry<CustomCropsBlock, List<Integer>> entry : blockToPosMap.entrySet()) {
            tags.add(new CompoundTag("", toCompoundMap(entry.getKey(), entry.getValue())));
        }
        return tags;
    }

    private CompoundMap toCompoundMap(CustomCropsBlock block, List<Integer> pos) {
        CompoundMap map = new CompoundMap();
        int[] result = new int[pos.size()];
        for (int i = 0; i < pos.size(); i++) {
            result[i] = pos.get(i);
        }
        map.put(new StringTag("type", block.getType().name()));
        map.put(new IntArrayTag("pos", result));
        map.put(new CompoundTag("data", block.getCompoundMap().getOriginalMap()));
        return map;
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

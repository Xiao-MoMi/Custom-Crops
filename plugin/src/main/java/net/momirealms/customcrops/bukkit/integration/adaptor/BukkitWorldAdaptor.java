/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.bukkit.integration.adaptor;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.ConfigManager;
import net.momirealms.customcrops.api.core.InternalRegistries;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.world.*;
import net.momirealms.customcrops.api.core.world.adaptor.AbstractWorldAdaptor;
import net.momirealms.customcrops.api.util.StringUtils;
import net.momirealms.customcrops.api.util.TagUtils;
import net.momirealms.customcrops.common.helper.GsonHelper;
import net.momirealms.customcrops.common.helper.VersionHelper;
import net.momirealms.customcrops.common.util.Key;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BukkitWorldAdaptor extends AbstractWorldAdaptor<World> {

    private static BiFunction<World, RegionPos, File> regionFileProvider;
    private static Function<World, File> worldFolderProvider;
    private static final NamespacedKey WORLD_DATA = new NamespacedKey(BukkitCustomCropsPlugin.getInstance().getBootstrap(), "data");
    private static final String DATA_FILE = "customcrops.dat";

    public BukkitWorldAdaptor() {
        worldFolderProvider = (world -> {
            if (ConfigManager.absoluteWorldPath().isEmpty()) {
                return world.getWorldFolder();
            } else {
                return new File(ConfigManager.absoluteWorldPath(), world.getName());
            }
        });
        regionFileProvider = (world, pos) -> new File(worldFolderProvider.apply(world), "customcrops" + File.separator + getRegionDataFile(pos));
    }

    public static void regionFileProvider(BiFunction<World, RegionPos, File> regionFileProvider) {
        BukkitWorldAdaptor.regionFileProvider = regionFileProvider;
    }

    public static void worldFolderProvider(Function<World, File> worldFolderProvider) {
        BukkitWorldAdaptor.worldFolderProvider = worldFolderProvider;
    }

    @Override
    public World getWorld(String worldName) {
        return Bukkit.getWorld(worldName);
    }

    @Override
    public CustomCropsWorld<World> adapt(Object world) {
        return CustomCropsWorld.create((World) world, this);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public WorldExtraData loadExtraData(World world) {
        if (VersionHelper.isVersionNewerThan1_18()) {
            // init world basic info
            String json = world.getPersistentDataContainer().get(WORLD_DATA, PersistentDataType.STRING);
            WorldExtraData data = (json == null || json.equals("null")) ? WorldExtraData.empty() : GsonHelper.get().fromJson(json, WorldExtraData.class);
            if (data == null) data = WorldExtraData.empty();
            return data;
        } else {
            File data = new File(getWorldFolder(world), DATA_FILE);
            if (data.exists()) {
                byte[] fileBytes = new byte[(int) data.length()];
                try (FileInputStream fis = new FileInputStream(data)) {
                    fis.read(fileBytes);
                } catch (IOException e) {
                    BukkitCustomCropsPlugin.getInstance().getPluginLogger().severe("[" + world.getName() + "] Failed to load extra data from " + data.getAbsolutePath(), e);
                }
                String jsonContent = new String(fileBytes, StandardCharsets.UTF_8);
                return GsonHelper.get().fromJson(jsonContent, WorldExtraData.class);
            } else {
                return WorldExtraData.empty();
            }
        }
    }

    @Override
    public void saveExtraData(CustomCropsWorld<World> world) {
        if (VersionHelper.isVersionNewerThan1_18()) {
            world.world().getPersistentDataContainer().set(WORLD_DATA, PersistentDataType.STRING,
                    GsonHelper.get().toJson(world.extraData()));
        } else {
            File data = new File(getWorldFolder(world.world()), DATA_FILE);
            File parentDir = data.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (FileWriter file = new FileWriter(data)) {
                GsonHelper.get().toJson(world.extraData(), file);
            } catch (IOException e) {
                BukkitCustomCropsPlugin.getInstance().getPluginLogger().severe("[" + world.worldName() + "] Failed to save extra data to " + data.getAbsolutePath(), e);
            }
        }
    }

    @Nullable
    @Override
    public CustomCropsRegion loadRegion(CustomCropsWorld<World> world, RegionPos pos, boolean createIfNotExist) {
        File data = getRegionDataFile(world.world(), pos);
        // if the data file not exists
        if (!data.exists()) {
            return createIfNotExist ? world.createRegion(pos) : null;
        } else {
            // load region from local files
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(data))) {
                DataInputStream dataStream = new DataInputStream(bis);
                CustomCropsRegion region = deserializeRegion(world, dataStream, pos);
                dataStream.close();
                return region;
            } catch (Exception e) {
                BukkitCustomCropsPlugin.getInstance().getPluginLogger().severe("[" + world.worldName() + "] Failed to load CustomCrops region data at " + pos + ". Deleting the corrupted region.", e);
                boolean success = data.delete();
                if (success) {
                    return createIfNotExist ? world.createRegion(pos) : null;
                } else {
                    throw new RuntimeException("[" + world.worldName() + "] Failed to delete corrupted CustomCrops region data at " + pos);
                }
            }
        }
    }

    @Nullable
    @Override
    public CustomCropsChunk loadChunk(CustomCropsWorld<World> world, ChunkPos pos, boolean createIfNotExist) {
        CustomCropsRegion region = world.getOrCreateRegion(pos.toRegionPos());
        // In order to reduce frequent disk reads to determine whether a region exists, we read the region into the cache
        if (!region.isLoaded()) {
            region.load();
        }
        byte[] bytes = region.getCachedChunkBytes(pos);
        if (bytes == null) {
            return createIfNotExist ? world.createChunk(pos) : null;
        } else {
            try {
                long time1 = System.currentTimeMillis();
                DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(bytes));
                CustomCropsChunk chunk = deserializeChunk(world, dataStream);
                dataStream.close();
                long time2 = System.currentTimeMillis();
                BukkitCustomCropsPlugin.getInstance().debug(() -> "[" + world.worldName() + "] Took " + (time2-time1) + "ms to load chunk " + pos + " from cached region");
                return chunk;
            } catch (IOException e) {
                BukkitCustomCropsPlugin.getInstance().getPluginLogger().severe("[" + world.worldName() + "] Failed to load CustomCrops data at " + pos, e);
                region.removeCachedChunk(pos);
                return createIfNotExist ? world.createChunk(pos) : null;
            }
        }
    }

    @Override
    public void saveRegion(CustomCropsWorld<World> world, CustomCropsRegion region) {
        File file = getRegionDataFile(world.world(), region.regionPos());
        if (region.canPrune()) {
            if (file.exists()) {
                file.delete();
            }
            return;
        }
        long time1 = System.currentTimeMillis();
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(file); BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            bos.write(serializeRegion(region));
            long time2 = System.currentTimeMillis();
            BukkitCustomCropsPlugin.getInstance().debug(() -> "[" + world.worldName() + "] Took " + (time2-time1) + "ms to save region " + region.regionPos());
        } catch (IOException e) {
            BukkitCustomCropsPlugin.getInstance().getPluginLogger().severe("[" + world.worldName() + "] Failed to save CustomCrops region data." + region.regionPos(), e);
        }
    }

    @Override
    public void saveChunk(CustomCropsWorld<World> world, CustomCropsChunk chunk) {
        RegionPos pos = chunk.chunkPos().toRegionPos();
        Optional<CustomCropsRegion> region = world.getLoadedRegion(pos);
        if (region.isEmpty()) {
            BukkitCustomCropsPlugin.getInstance().getPluginLogger().severe("[" + world.worldName() + "] Region " + pos + " unloaded before chunk " + chunk.chunkPos() + " saving.");
        } else {
            CustomCropsRegion cropsRegion = region.get();
            SerializableChunk serializableChunk = toSerializableChunk(chunk);
            if (serializableChunk.canPrune()) {
                cropsRegion.removeCachedChunk(chunk.chunkPos());
            } else {
                cropsRegion.setCachedChunk(chunk.chunkPos(), serializeChunk(serializableChunk));
            }
        }
    }

    @Override
    public String getName(World world) {
        return world.getName();
    }

    @Override
    public int priority() {
        return BUKKIT_WORLD_PRIORITY;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private CustomCropsRegion deserializeRegion(CustomCropsWorld<World> world, DataInputStream dataStream, RegionPos pos) throws IOException {
        int regionVersion = dataStream.readByte();
        int regionX = dataStream.readInt();
        int regionZ = dataStream.readInt();
        RegionPos regionPos = RegionPos.of(regionX, regionZ);
        ConcurrentHashMap<ChunkPos, byte[]> map = new ConcurrentHashMap<>();
        int chunkAmount = dataStream.readInt();
        for (int i = 0; i < chunkAmount; i++) {
            int chunkX = dataStream.readInt();
            int chunkZ = dataStream.readInt();
            ChunkPos chunkPos = ChunkPos.of(chunkX, chunkZ);
            byte[] chunkData = new byte[dataStream.readInt()];
            dataStream.read(chunkData);
            map.put(chunkPos, chunkData);
        }
        return world.restoreRegion(pos, map);
    }

    private byte[] serializeRegion(CustomCropsRegion region) {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(outByteStream);
        try {
            outStream.writeByte(REGION_VERSION);
            outStream.writeInt(region.regionPos().x());
            outStream.writeInt(region.regionPos().z());
            Map<ChunkPos, byte[]> map = region.dataToSave();
            outStream.writeInt(map.size());
            for (Map.Entry<ChunkPos, byte[]> entry : map.entrySet()) {
                outStream.writeInt(entry.getKey().x());
                outStream.writeInt(entry.getKey().z());
                byte[] dataArray = entry.getValue();
                outStream.writeInt(dataArray.length);
                outStream.write(dataArray);
            }
        } catch (IOException e) {
            BukkitCustomCropsPlugin.getInstance().getPluginLogger().severe("Failed to serialize CustomCrops region data." + region.regionPos(), e);
        }
        return outByteStream.toByteArray();
    }

    private CustomCropsChunk deserializeChunk(CustomCropsWorld<World> world, DataInputStream dataStream) throws IOException {
        int chunkVersion = dataStream.readByte();
        byte[] blockData = readCompressedBytes(dataStream);
        return deserializeChunk(world, blockData, chunkVersion);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private byte[] readCompressedBytes(DataInputStream dataStream) throws IOException {
        int compressedLength = dataStream.readInt();
        int decompressedLength = dataStream.readInt();
        byte[] compressedData = new byte[compressedLength];
        byte[] decompressedData = new byte[decompressedLength];

        dataStream.read(compressedData);
        zstdDecompress(decompressedData, compressedData);
        return decompressedData;
    }

    private File getWorldFolder(World world) {
        return worldFolderProvider.apply(world);
    }

    private File getRegionDataFile(World world, RegionPos regionPos) {
        return regionFileProvider.apply(world, regionPos);
    }

    private String getRegionDataFile(RegionPos regionPos) {
        return "r." + regionPos.x() + "." + regionPos.z() + ".mcc";
    }

    private byte[] serializeChunk(SerializableChunk serializableChunk) {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(outByteStream);
        try {
            outStream.writeByte(CHUNK_VERSION);
            byte[] serializedSections = toBytes(serializableChunk);
            byte[] compressed = zstdCompress(serializedSections);
            outStream.writeInt(compressed.length);
            outStream.writeInt(serializedSections.length);
            outStream.write(compressed);
        } catch (IOException e) {
            BukkitCustomCropsPlugin.getInstance().getPluginLogger().severe("Failed to serialize chunk " + ChunkPos.of(serializableChunk.x(), serializableChunk.z()));
        }
        return outByteStream.toByteArray();
    }

    private byte[] toBytes(SerializableChunk chunk) throws IOException {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream(16384);
        DataOutputStream outStream = new DataOutputStream(outByteStream);
        outStream.writeInt(chunk.x());
        outStream.writeInt(chunk.z());
        outStream.writeInt(chunk.loadedSeconds());
        outStream.writeLong(chunk.lastLoadedTime());
        // write queue
        int[] queue = chunk.queuedTasks();
        outStream.writeInt(queue.length / 2);
        for (int i : queue) {
            outStream.writeInt(i);
        }
        // write ticked blocks
        int[] tickedSet = chunk.ticked();
        outStream.writeInt(tickedSet.length);
        for (int i : tickedSet) {
            outStream.writeInt(i);
        }
        // write block data
        List<SerializableSection> sectionsToSave = chunk.sections();
        outStream.writeInt(sectionsToSave.size());
        for (SerializableSection section : sectionsToSave) {
            outStream.writeInt(section.sectionID());
            byte[] blockData = toBytes(section.blocks());
            outStream.writeInt(blockData.length);
            outStream.write(blockData);
        }
        return outByteStream.toByteArray();
    }

    private byte[] toBytes(Collection<CompoundTag> blocks) throws IOException {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream(16384);
        DataOutputStream outStream = new DataOutputStream(outByteStream);
        outStream.writeInt(blocks.size());
        for (CompoundTag block : blocks) {
            byte[] blockData = toBytes(block);
            outStream.writeInt(blockData.length);
            outStream.write(blockData);
        }
        return outByteStream.toByteArray();
    }

    private byte[] toBytes(CompoundTag tag) throws IOException {
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

    @SuppressWarnings("all")
    private CustomCropsChunk deserializeChunk(CustomCropsWorld world, byte[] bytes, int chunkVersion) throws IOException {
        Function<String, Key> keyFunction = chunkVersion < 2 ?
        (s) -> {
            return Key.key("customcrops", StringUtils.toLowerCase(s));
        } : s -> {
            return Key.key(s);
        };
        DataInputStream chunkData = new DataInputStream(new ByteArrayInputStream(bytes));
        // read coordinate
        int x = chunkData.readInt();
        int z = chunkData.readInt();
        ChunkPos coordinate = new ChunkPos(x, z);
        // read loading info
        int loadedSeconds = chunkData.readInt();
        long lastLoadedTime = chunkData.readLong();
        // read task queue
        int tasksSize = chunkData.readInt();
        PriorityBlockingQueue<DelayedTickTask> queue = new PriorityBlockingQueue<>(Math.max(11, tasksSize));
        for (int i = 0; i < tasksSize; i++) {
            int time = chunkData.readInt();
            BlockPos pos = new BlockPos(chunkData.readInt());
            queue.add(new DelayedTickTask(time, pos));
        }
        // read ticked blocks
        int tickedSize = chunkData.readInt();
        HashSet<BlockPos> tickedSet = new HashSet<>(Math.max(11, tickedSize));
        for (int i = 0; i < tickedSize; i++) {
            tickedSet.add(new BlockPos(chunkData.readInt()));
        }
        // read block data
        ConcurrentHashMap<Integer, CustomCropsSection> sectionMap = new ConcurrentHashMap<>();
        int sections = chunkData.readInt();
        // read sections
        for (int i = 0; i < sections; i++) {
            ConcurrentHashMap<BlockPos, CustomCropsBlockState> blockMap = new ConcurrentHashMap<>();
            int sectionID = chunkData.readInt();
            byte[] sectionBytes = new byte[chunkData.readInt()];
            chunkData.read(sectionBytes);
            DataInputStream sectionData = new DataInputStream(new ByteArrayInputStream(sectionBytes));
            int blockAmount = sectionData.readInt();
            // read blocks
            for (int j = 0; j < blockAmount; j++) {
                byte[] blockData = new byte[sectionData.readInt()];
                sectionData.read(blockData);
                CompoundTag tag = readCompound(blockData);
                CompoundMap block = tag.getValue();
                Key key = keyFunction.apply((String) block.get("type").getValue());
                CompoundMap data = (CompoundMap) block.get("data").getValue();
                CustomCropsBlock customBlock = InternalRegistries.BLOCK.get(key);
                if (customBlock == null) {
                    BukkitCustomCropsPlugin.getInstance().getInstance().getPluginLogger().warn("[" + world.worldName() + "] Unrecognized custom block " + key + " has been removed from chunk " + ChunkPos.of(x, z));
                    continue;
                }
                for (int pos : (int[]) block.get("pos").getValue()) {
                    BlockPos blockPos = new BlockPos(pos);
                    blockMap.put(blockPos, CustomCropsBlockState.create(customBlock, TagUtils.deepClone(data)));
                }
            }
            sectionMap.put(sectionID, CustomCropsSection.restore(sectionID, blockMap));
        }
        return world.restoreChunk(coordinate, loadedSeconds, lastLoadedTime, sectionMap, queue, tickedSet);
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
}

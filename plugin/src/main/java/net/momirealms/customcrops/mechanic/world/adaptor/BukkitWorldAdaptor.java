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

import com.google.gson.Gson;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.world.ChunkCoordinate;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import net.momirealms.customcrops.api.mechanic.world.level.WorldInfoData;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.mechanic.world.CChunk;
import net.momirealms.customcrops.mechanic.world.CWorld;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;

public class BukkitWorldAdaptor extends AbstractWorldAdaptor {

    private static final NamespacedKey key = new NamespacedKey(CustomCropsPlugin.get(), "data");
    private final Gson gson;
    private String worldFolder;

    public BukkitWorldAdaptor(WorldManager worldManager) {
        super(worldManager);
        gson = new Gson();
//        kryo = new Kryo();
//        kryo.setReferences(false);
//        kryo.register(PriorityQueue.class, 17);
//        kryo.register(ArrayList.class, 18);
//        kryo.register(ConcurrentHashMap.class, 19);
//        kryo.register(HashMap.class, 20);
//        kryo.register(ChunkCoordinate.class, 21);
//        kryo.register(SimpleLocation.class, 22);
//        kryo.register(CChunk.class, 23);
//        kryo.register(MemoryCrop.class, 24);
//        kryo.register(MemorySprinkler.class, 25);
//        kryo.register(MemoryPot.class, 26);
//        kryo.register(CheckTask.class, 27);
//        kryo.register(CheckTask.TaskType.class, 28);
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

//        try {
//            // save chunks
//            for (CustomCropsChunk chunk : cWorld.getChunkStorage()) {
////                Output output = new Output(new FileOutputStream(getChunkDataFilePath(world, chunk.getChunkCoordinate())));
//////                kryo.writeObject(output, chunk);
////                output.close();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
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
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void loadAllData(CustomCropsWorld customCropsWorld) {
        CWorld cWorld = (CWorld) customCropsWorld;
        World world = cWorld.getWorld();
        if (world == null) {
            LogUtils.severe("Unexpected issue: World " + cWorld.getWorldName() + " unloaded before data loaded");
            return;
        }

        // create or get chunk files
        File folder = new File(world.getWorldFolder(), "customcrops");
        if (!folder.exists())
            folder.mkdir();
        File[] chunks = folder.listFiles();
        if (chunks == null)
            return;
//        try {
//            // load chunk into world
//            for (File chunkFile : chunks) {
//                Input input = new Input(new FileInputStream(chunkFile));
//                CChunk chunk = kryo.readObject(input, CChunk.class);
//                input.close();
//                chunk.setWorld(cWorld);
//                cWorld.loadChunk(chunk);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void loadDynamicData(CustomCropsWorld customCropsWorld, ChunkCoordinate chunkCoordinate) {
        CWorld cWorld = (CWorld) customCropsWorld;
        World world = cWorld.getWorld();
        if (world == null) {
            LogUtils.severe("Unexpected issue: World " + cWorld.getWorldName() + " unloaded before data loaded");
            return;
        }
        // create or get chunk files
        File data = getChunkDataFilePath(world, chunkCoordinate);
        if (!data.exists())
            return;

        long time1 = System.currentTimeMillis();
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(getChunkDataFilePath(world, chunkCoordinate)))) {
            byte[] bytes = bis.readAllBytes();
            DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(bytes));
            CChunk chunk = deserialize(cWorld, dataStream);
            dataStream.close();
            cWorld.loadChunk(chunk);
            long time2 = System.currentTimeMillis();
            System.out.println(time2 - time1 + "ms load " + chunkCoordinate);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            long time1 = System.currentTimeMillis();
//            // load chunk into world
//            Input input = new Input(new FileInputStream(data));
//            CChunk chunk = kryo.readObject(input, CChunk.class);
//            input.close();
//            chunk.setWorld(cWorld);
//            cWorld.loadChunk(chunk);
//            long time2 = System.currentTimeMillis();
//            System.out.println(time2 - time1 + "ms load " + chunkCoordinate);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void unloadDynamicData(CustomCropsWorld customCropsWorld, ChunkCoordinate chunkCoordinate) {
        CWorld cWorld = (CWorld) customCropsWorld;
        World world = cWorld.getWorld();
        if (world == null) {
            LogUtils.severe("Unexpected issue: World " + cWorld.getWorldName() + " unloaded before data loaded");
            return;
        }

        CChunk chunk = cWorld.unloadChunk(chunkCoordinate);
        if (chunk == null)
            return;

        long time1 = System.currentTimeMillis();
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(getChunkDataFilePath(world, chunkCoordinate)))) {
            bos.write(serialize(chunk));
            long time2 = System.currentTimeMillis();
            System.out.println(time2 - time1 + "ms unload " + chunkCoordinate);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            long time1 = System.currentTimeMillis();
//            // save chunks
//            Output output = new Output(new FileOutputStream(getChunkDataFilePath(world, chunkCoordinate)));
//            kryo.writeObject(output, chunk);
//            output.close();
//            long time2 = System.currentTimeMillis();
//            System.out.println(time2 - time1 + "ms unload " + chunkCoordinate);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
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
        return new File(world.getWorldFolder(), "customcrops" + File.separator + getChunkDataFile(chunkCoordinate));
    }

    public void setWorldPath(String folder) {
        this.worldFolder = folder;
    }
}

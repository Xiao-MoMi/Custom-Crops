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

import com.flowpowered.nbt.*;
import com.infernalsuite.aswm.api.SlimePlugin;
import com.infernalsuite.aswm.api.events.LoadSlimeWorldEvent;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.world.BlockPos;
import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsChunk;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsRegion;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import net.momirealms.customcrops.api.mechanic.world.level.WorldInfoData;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.mechanic.world.*;
import net.momirealms.customcrops.mechanic.world.block.*;
import net.momirealms.customcrops.scheduler.task.TickTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class SlimeWorldAdaptor extends BukkitWorldAdaptor {

    private final SlimePlugin slimePlugin;

    public SlimeWorldAdaptor(WorldManager worldManager) {
        super(worldManager);
        this.slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
    }

    @EventHandler (ignoreCancelled = true)
    public void onSlimeWorldLoad(LoadSlimeWorldEvent event) {
        World world = Bukkit.getWorld(event.getSlimeWorld().getName());
        if (world == null) {
            LogUtils.warn("Failed to load slime world because the bukkit world not loaded");
            return;
        }
        if (worldManager.isMechanicEnabled(world)) {
            worldManager.loadWorld(world);
        }
    }

    @Override
    public void saveInfoData(CustomCropsWorld customCropsWorld) {
        SlimeWorld slimeWorld = slimePlugin.getWorld(customCropsWorld.getWorldName());
        if (slimeWorld == null) {
            super.saveInfoData(customCropsWorld);
            return;
        }

        Optional<CompoundTag> optionalCompoundTag = slimeWorld.getExtraData().getAsCompoundTag("customcrops");
        if (optionalCompoundTag.isEmpty()) {
            LogUtils.warn("Failed to unload data for world " + customCropsWorld.getWorldName() + " because slime world format is incorrect.");
            return;
        }

        CompoundMap ccDataMap = optionalCompoundTag.get().getValue();
        ccDataMap.put(new StringTag("world-info", gson.toJson(customCropsWorld.getInfoData())));
    }

    @Override
    public void unload(CustomCropsWorld customCropsWorld) {
        SlimeWorld slimeWorld = slimePlugin.getWorld(customCropsWorld.getWorldName());
        if (slimeWorld == null) {
            super.unload(customCropsWorld);
            return;
        }
        customCropsWorld.save();
    }

    @Override
    public void init(CustomCropsWorld customCropsWorld) {
        SlimeWorld slimeWorld = slimePlugin.getWorld(customCropsWorld.getWorldName());
        if (slimeWorld == null) {
            super.init(customCropsWorld);
            return;
        }

        Optional<CompoundTag> optionalCompoundTag = slimeWorld.getExtraData().getAsCompoundTag("customcrops");
        CompoundMap ccDataMap;
        if (optionalCompoundTag.isEmpty()) {
            ccDataMap = new CompoundMap();
            slimeWorld.getExtraData().getValue().put(new CompoundTag("customcrops", ccDataMap));
        } else {
            ccDataMap = optionalCompoundTag.get().getValue();
        }

        String json = Optional.ofNullable(ccDataMap.get("world-info")).map(tag -> tag.getAsStringTag().get().getValue()).orElse(null);
        WorldInfoData data = (json == null || json.equals("null")) ? WorldInfoData.empty() : gson.fromJson(json, WorldInfoData.class);
        customCropsWorld.setInfoData(data);
    }

    @Override
    public void loadChunkData(CustomCropsWorld customCropsWorld, ChunkPos chunkPos) {
        SlimeWorld slimeWorld = slimePlugin.getWorld(customCropsWorld.getWorldName());
        if (slimeWorld == null) {
            super.loadChunkData(customCropsWorld, chunkPos);
            return;
        }

        long time1 = System.currentTimeMillis();
        CWorld cWorld = (CWorld) customCropsWorld;
        // load lazy chunks firstly
        CustomCropsChunk lazyChunk = customCropsWorld.removeLazyChunkAt(chunkPos);
        if (lazyChunk != null) {
            CChunk cChunk = (CChunk) lazyChunk;
            cChunk.resetUnloadedSeconds();
            cWorld.loadChunk(cChunk);
            long time2 = System.currentTimeMillis();
            CustomCropsPlugin.get().debug("Took " + (time2-time1) + "ms to load chunk " + chunkPos + " from lazy chunks");
            return;
        }

        Optional<CompoundTag> optionalCompoundTag = slimeWorld.getExtraData().getAsCompoundTag("customcrops");
        if (optionalCompoundTag.isEmpty()) {
            LogUtils.warn("Failed to load data for " + chunkPos + " in world " + customCropsWorld.getWorldName() + " because slime world format is incorrect.");
            return;
        }

        Tag<?> chunkTag = optionalCompoundTag.get().getValue().get(chunkPos.getAsString());
        if (chunkTag == null) {
            return;
        }
        Optional<CompoundTag> chunkCompoundTag = chunkTag.getAsCompoundTag();
        if (chunkCompoundTag.isEmpty()) {
            return;
        }

        // load chunk from slime world
        cWorld.loadChunk(tagToChunk(cWorld, chunkCompoundTag.get()));
        long time2 = System.currentTimeMillis();
        CustomCropsPlugin.get().debug("Took " + (time2-time1) + "ms to load chunk " + chunkPos);
    }

    @Override
    public void saveChunkToCachedRegion(CustomCropsChunk customCropsChunk) {
        CustomCropsWorld customCropsWorld = customCropsChunk.getCustomCropsWorld();
        SlimeWorld slimeWorld = getSlimeWorld(customCropsChunk.getCustomCropsWorld().getWorldName());
        if (slimeWorld == null) {
            super.saveChunkToCachedRegion(customCropsChunk);
            return;
        }

        Optional<CompoundTag> optionalCompoundTag = slimeWorld.getExtraData().getAsCompoundTag("customcrops");
        if (optionalCompoundTag.isEmpty()) {
            LogUtils.warn("Failed to save data for " + customCropsChunk.getChunkPos() + " in world " + customCropsWorld.getWorldName() + " because slime world format is incorrect.");
            return;
        }

        SerializableChunk serializableChunk = toSerializableChunk((CChunk) customCropsChunk);
        if (Bukkit.isPrimaryThread()) {
            if (serializableChunk.canPrune()) {
                optionalCompoundTag.get().getValue().remove(customCropsChunk.getChunkPos().getAsString());
            } else {
                optionalCompoundTag.get().getValue().put(chunkToTag(serializableChunk));
            }
        } else {
            CustomCropsPlugin.get().getScheduler().runTaskSync(() -> {
                if (serializableChunk.canPrune()) {
                    optionalCompoundTag.get().getValue().remove(customCropsChunk.getChunkPos().getAsString());
                } else {
                    optionalCompoundTag.get().getValue().put(chunkToTag(serializableChunk));
                }
            }, null);
        }
    }

    @Override
    public void saveRegion(CustomCropsRegion customCropsRegion) {
        CustomCropsWorld customCropsWorld = customCropsRegion.getCustomCropsWorld();
        SlimeWorld slimeWorld = getSlimeWorld(customCropsWorld.getWorldName());
        if (slimeWorld == null) {
            super.saveRegion(customCropsRegion);
            return;
        }

        // don't need to save region to slime world
    }

    private SlimeWorld getSlimeWorld(String name) {
        return slimePlugin.getWorld(name);
    }

    private CompoundTag chunkToTag(SerializableChunk serializableChunk) {
        CompoundMap map = new CompoundMap();
        map.put(new IntTag("x", serializableChunk.getX()));
        map.put(new IntTag("z", serializableChunk.getZ()));
        map.put(new IntTag("loadedSeconds", serializableChunk.getLoadedSeconds()));
        map.put(new LongTag("lastLoadedTime", serializableChunk.getLastLoadedTime()));
        map.put(new IntArrayTag("queued", serializableChunk.getTicked()));
        map.put(new IntArrayTag("ticked", serializableChunk.getTicked()));
        CompoundMap sectionMap = new CompoundMap();
        for (SerializableSection section : serializableChunk.getSections()) {
            sectionMap.put(new ListTag<>(String.valueOf(section.getSectionID()), TagType.TAG_COMPOUND, section.getBlocks()));
        }
        map.put(new CompoundTag("sections", sectionMap));
        return new CompoundTag(serializableChunk.getX() + "," + serializableChunk.getZ(), map);
    }

    private CChunk tagToChunk(CWorld cWorld, CompoundTag tag) {
        String world = cWorld.getWorldName();
        CompoundMap map = tag.getValue();
        int x = map.get("x").getAsIntTag().get().getValue();
        int z = map.get("z").getAsIntTag().get().getValue();
        ChunkPos coordinate = new ChunkPos(x, z);
        int loadedSeconds = map.get("loadedSeconds").getAsIntTag().get().getValue();
        long lastLoadedTime = map.get("lastLoadedTime").getAsLongTag().get().getValue();
        int[] queued = map.get("queued").getAsIntArrayTag().get().getValue();
        int[] ticked = map.get("ticked").getAsIntArrayTag().get().getValue();

        PriorityQueue<TickTask> queue = new PriorityQueue<>(Math.max(11, queued.length / 2));
        for (int i = 0, size = queued.length / 2; i < size; i++) {
            BlockPos pos = new BlockPos(queued[2*i+1]);
            queue.add(new TickTask(queued[2*i], pos));
        }

        HashSet<BlockPos> tickedSet = new HashSet<>(Math.max(11, ticked.length));
        for (int tick : ticked) {
            tickedSet.add(new BlockPos(tick));
        }

        ConcurrentHashMap<Integer, CSection> sectionMap = new ConcurrentHashMap<>();
        CompoundMap sectionCompoundMap = map.get("sections").getAsCompoundTag().get().getValue();
        for (Map.Entry<String, Tag<?>> entry : sectionCompoundMap.entrySet()) {
            if (entry.getValue() instanceof ListTag<?> listTag) {
                int id = Integer.parseInt(entry.getKey());
                ConcurrentHashMap<BlockPos, CustomCropsBlock> blockMap = new ConcurrentHashMap<>();
                ListTag<CompoundTag> blocks = (ListTag<CompoundTag>) listTag;
                for (CompoundTag blockTag : blocks.getValue()) {
                    CompoundMap block = blockTag.getValue();
                    String type = (String) block.get("type").getValue();
                    CompoundMap data = (CompoundMap) block.get("data").getValue();
                    switch (type) {
                        case "CROP" -> {
                            for (int pos : (int[]) block.get("pos").getValue()) {
                                BlockPos blockPos = new BlockPos(pos);
                                blockMap.put(blockPos, new MemoryCrop(blockPos.getLocation(world, coordinate), new CompoundMap(data)));
                            }
                        }
                        case "POT" -> {
                            for (int pos : (int[]) block.get("pos").getValue()) {
                                BlockPos blockPos = new BlockPos(pos);
                                blockMap.put(blockPos, new MemoryPot(blockPos.getLocation(world, coordinate), new CompoundMap(data)));
                            }
                        }
                        case "SPRINKLER" -> {
                            for (int pos : (int[]) block.get("pos").getValue()) {
                                BlockPos blockPos = new BlockPos(pos);
                                blockMap.put(blockPos, new MemorySprinkler(blockPos.getLocation(world, coordinate), new CompoundMap(data)));
                            }
                        }
                        case "SCARECROW" -> {
                            for (int pos : (int[]) block.get("pos").getValue()) {
                                BlockPos blockPos = new BlockPos(pos);
                                blockMap.put(blockPos, new MemoryScarecrow(blockPos.getLocation(world, coordinate), new CompoundMap(data)));
                            }
                        }
                        case "GLASS" -> {
                            for (int pos : (int[]) block.get("pos").getValue()) {
                                BlockPos blockPos = new BlockPos(pos);
                                blockMap.put(blockPos, new MemoryGlass(blockPos.getLocation(world, coordinate), new CompoundMap(data)));
                            }
                        }
                    }
                }
                sectionMap.put(id, new CSection(id, blockMap));
            }
        }

        return new CChunk(
                cWorld,
                coordinate,
                loadedSeconds,
                lastLoadedTime,
                sectionMap,
                queue,
                tickedSet
        );
    }
}

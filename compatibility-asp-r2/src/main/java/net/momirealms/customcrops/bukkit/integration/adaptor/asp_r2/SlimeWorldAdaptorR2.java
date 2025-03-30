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

package net.momirealms.customcrops.bukkit.integration.adaptor.asp_r2;

import com.infernalsuite.aswm.api.events.LoadSlimeWorldEvent;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.InternalRegistries;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.world.*;
import net.momirealms.customcrops.api.core.world.adaptor.AbstractWorldAdaptor;
import net.momirealms.customcrops.api.util.StringUtils;
import net.momirealms.customcrops.api.util.TagUtils;
import net.momirealms.customcrops.common.helper.GsonHelper;
import net.momirealms.customcrops.common.util.Key;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Function;

public class SlimeWorldAdaptorR2 extends AbstractWorldAdaptor<SlimeWorld> implements Listener {

    private final Function<String, SlimeWorld> getSlimeWorldFunction;

    public SlimeWorldAdaptorR2(int version) {
        try {
            if (version == 1) {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
                Class<?> slimeClass = Class.forName("com.infernalsuite.aswm.api.SlimePlugin");
                Method method = slimeClass.getMethod("getWorld", String.class);
                this.getSlimeWorldFunction = (name) -> {
                    try {
                        return (SlimeWorld) method.invoke(plugin, name);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                };
            } else if (version == 2) {
                Class<?> apiClass = Class.forName("com.infernalsuite.aswm.api.AdvancedSlimePaperAPI");
                Object apiInstance = apiClass.getMethod("instance").invoke(null);
                Method method = apiClass.getMethod("getLoadedWorld", String.class);
                this.getSlimeWorldFunction = (name) -> {
                    try {
                        return (SlimeWorld) method.invoke(apiInstance, name);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                };
            } else {
                throw new IllegalArgumentException("Unsupported version: " + version);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onWorldLoad(LoadSlimeWorldEvent event) {
        World world = Bukkit.getWorld(event.getSlimeWorld().getName());
        if (!BukkitCustomCropsPlugin.getInstance().getWorldManager().isMechanicEnabled(world)) return;
        BukkitCustomCropsPlugin.getInstance().getWorldManager().loadWorld(adapt(event.getSlimeWorld()));
    }

    @Override
    public CustomCropsWorld<SlimeWorld> adapt(Object world) {
        return CustomCropsWorld.create((SlimeWorld) world, this);
    }

    @Override
    public SlimeWorld getWorld(String worldName) {
        return getSlimeWorldFunction.apply(worldName);
    }

    private CompoundMap createOrGetDataMap(SlimeWorld world) {
        Optional<CompoundTag> optionalCompoundTag = world.getExtraData().getAsCompoundTag("customcrops");
        CompoundMap ccDataMap;
        if (optionalCompoundTag.isEmpty()) {
            ccDataMap = new CompoundMap();
            world.getExtraData().getValue().put(new CompoundTag("customcrops", ccDataMap));
        } else {
            ccDataMap = optionalCompoundTag.get().getValue();
        }
        return ccDataMap;
    }

    @Override
    public WorldExtraData loadExtraData(SlimeWorld world) {
        CompoundMap ccDataMap = createOrGetDataMap(world);
        String json = Optional.ofNullable(ccDataMap.get("world-info")).map(tag -> tag.getAsStringTag().get().getValue()).orElse(null);
        return (json == null || json.equals("null")) ? WorldExtraData.empty() : GsonHelper.get().fromJson(json, WorldExtraData.class);
    }

    @Override
    public void saveExtraData(CustomCropsWorld<SlimeWorld> world) {
        CompoundMap ccDataMap = createOrGetDataMap(world.world());
        ccDataMap.put(new StringTag("world-info", GsonHelper.get().toJson(world.extraData())));
    }

    @Nullable
    @Override
    public CustomCropsRegion loadRegion(CustomCropsWorld<SlimeWorld> world, RegionPos pos, boolean createIfNotExists) {
        return null;
    }

    @Nullable
    @Override
    public CustomCropsChunk loadChunk(CustomCropsWorld<SlimeWorld> world, ChunkPos pos, boolean createIfNotExists) {
        long time1 = System.currentTimeMillis();
        CompoundMap ccDataMap = createOrGetDataMap(world.world());
        Tag<?> chunkTag = ccDataMap.get(pos.asString());
        if (chunkTag == null) {
            return createIfNotExists ? world.createChunk(pos) : null;
        }
        Optional<CompoundTag> chunkCompoundTag = chunkTag.getAsCompoundTag();
        if (chunkCompoundTag.isEmpty()) {
            return createIfNotExists ? world.createChunk(pos) : null;
        }
        CustomCropsChunk chunk = tagToChunk(world, chunkCompoundTag.get());
        long time2 = System.currentTimeMillis();
        BukkitCustomCropsPlugin.getInstance().debug(() -> "Took " + (time2-time1) + "ms to load chunk " + pos);
        return chunk;
    }

    @Override
    public void saveRegion(CustomCropsWorld<SlimeWorld> world, CustomCropsRegion region) {}

    @Override
    public void saveChunk(CustomCropsWorld<SlimeWorld> world, CustomCropsChunk chunk) {
        CompoundMap ccDataMap = createOrGetDataMap(world.world());
        SerializableChunk serializableChunk = toSerializableChunk(chunk);
        Runnable runnable = () -> {
            if (serializableChunk.canPrune()) {
                ccDataMap.remove(chunk.chunkPos().asString());
            } else {
                ccDataMap.put(chunkToTag(serializableChunk));
            }
        };
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            BukkitCustomCropsPlugin.getInstance().getScheduler().sync().run(runnable, null);
        }
    }

    @Override
    public String getName(SlimeWorld world) {
        return world.getName();
    }

    @Override
    public int priority() {
        return SLIME_WORLD_PRIORITY;
    }

    private CompoundTag chunkToTag(SerializableChunk serializableChunk) {
        CompoundMap map = new CompoundMap();
        map.put(new IntTag("x", serializableChunk.x()));
        map.put(new IntTag("z", serializableChunk.z()));
        map.put(new IntTag("version", CHUNK_VERSION));
        map.put(new IntTag("loadedSeconds", serializableChunk.loadedSeconds()));
        map.put(new LongTag("lastLoadedTime", serializableChunk.lastLoadedTime()));
        map.put(new IntArrayTag("queued", serializableChunk.queuedTasks()));
        map.put(new IntArrayTag("ticked", serializableChunk.ticked()));
        CompoundMap sectionMap = new CompoundMap();
        for (SerializableSection section : serializableChunk.sections()) {
            sectionMap.put(new ListTag<>(String.valueOf(section.sectionID()), TagType.TAG_COMPOUND, section.blocks()));
        }
        map.put(new CompoundTag("sections", sectionMap));
        return new CompoundTag(serializableChunk.x() + "," + serializableChunk.z(), map);
    }

    @SuppressWarnings("all")
    private CustomCropsChunk tagToChunk(CustomCropsWorld<SlimeWorld> world, CompoundTag tag) {
        CompoundMap map = tag.getValue();
        IntTag version = (IntTag) map.getOrDefault("version", new IntTag("version", 1));
        int versionNumber = version.getValue();
        Function<String, Key> keyFunction = versionNumber < 2 ?
                (s) -> {
                    return Key.key("customcrops", StringUtils.toLowerCase(s));
                } : s -> {
            return Key.key(s);
        };
        int x = (int) map.get("x").getValue();
        int z = (int) map.get("z").getValue();

        ChunkPos coordinate = new ChunkPos(x, z);
        int loadedSeconds = (int) map.get("loadedSeconds").getValue();
        long lastLoadedTime = (long) map.get("lastLoadedTime").getValue();
        int[] queued = (int[]) map.get("queued").getValue();
        int[] ticked = (int[]) map.get("ticked").getValue();

        PriorityBlockingQueue<DelayedTickTask> queue = new PriorityBlockingQueue<>(Math.max(11, queued.length / 2));
        for (int i = 0, size = queued.length / 2; i < size; i++) {
            BlockPos pos = new BlockPos(queued[2*i+1]);
            queue.add(new DelayedTickTask(queued[2*i], pos));
        }

        HashSet<BlockPos> tickedSet = new HashSet<>(Math.max(11, ticked.length));
        for (int tick : ticked) {
            tickedSet.add(new BlockPos(tick));
        }

        ConcurrentHashMap<Integer, CustomCropsSection> sectionMap = new ConcurrentHashMap<>();
        CompoundMap sectionCompoundMap = (CompoundMap) map.get("sections").getValue();
        for (Map.Entry<String, Tag<?>> entry : sectionCompoundMap.entrySet()) {
            if (entry.getValue() instanceof ListTag<?> listTag) {
                int id = Integer.parseInt(entry.getKey());
                ConcurrentHashMap<BlockPos, CustomCropsBlockState> blockMap = new ConcurrentHashMap<>();
                ListTag<CompoundTag> blocks = (ListTag<CompoundTag>) listTag;
                for (CompoundTag blockTag : blocks.getValue()) {
                    CompoundMap block = blockTag.getValue();
                    CompoundMap data = (CompoundMap) block.get("data").getValue();
                    Key key = keyFunction.apply((String) block.get("type").getValue());
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
                sectionMap.put(id, CustomCropsSection.restore(id, blockMap));
            }
        }
        return world.restoreChunk(coordinate, loadedSeconds, lastLoadedTime, sectionMap, queue, tickedSet);
    }
}

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

package net.momirealms.customcrops.bukkit.integration.worldedit;

import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.ConfigManager;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.common.plugin.feature.Reloadable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WorldEditListener implements Reloadable, Listener {

    private Constructor<?> baseBlockConstructor;

    public WorldEditListener() {
        try {
            baseBlockConstructor = BaseBlock.class.getDeclaredConstructor(BlockState.class, CompoundTag.class);
            baseBlockConstructor.setAccessible(true);
//            if (PluginUtils.isEnabled("FastAsyncWorldEdit")) {
//                BukkitCustomCropsPlugin.getInstance().getPluginLogger().info("FastAsyncWorldEdit detected. Don't forget to add `" + CustomCropsDelegateExtent.class.getCanonicalName() + "` to allowed-plugins.");
//            }
        } catch (ReflectiveOperationException e) {
            BukkitCustomCropsPlugin.getInstance().getPluginLogger().warn("Not a supported WorldEdit version", e);
        }
    }

    @Override
    public void unload() {
        WorldEdit.getInstance().getEventBus().unregister(this);
        HandlerList.unregisterAll(this);
    }

    @Override
    public void load() {
        WorldEdit.getInstance().getEventBus().register(this);
        Bukkit.getPluginManager().registerEvents(this, BukkitCustomCropsPlugin.getInstance().getBootstrap());
    }

    @Subscribe(priority = EventHandler.Priority.LATE)
    public void onEditSession(EditSessionEvent event) {
        if (!ConfigManager.worldeditSupport()) return;
        if (event.getStage() != EditSession.Stage.BEFORE_CHANGE) {
            return;
        }
        try {
            event.setExtent(new CustomCropsDelegateExtent(event));
        } catch (Exception e) {
            BukkitCustomCropsPlugin.getInstance().debug(e::getMessage);
        }
    }

    @org.bukkit.event.EventHandler(ignoreCancelled = true)
    private void onCommand(PlayerCommandPreprocessEvent event) {
        if (!ConfigManager.worldeditSupport()) return;
        String string = event.getMessage();
        if (string.startsWith("//copy")) {
            Player player = event.getPlayer();
            BukkitCustomCropsPlugin.getInstance().getScheduler().sync().runLater(() ->
                    updateCopyClipBoard(player), 1, player.getLocation());
        }

        if (string.startsWith("//cut")) {
            updateCutClipBoard(event.getPlayer());
        }
    }

    public void updateCutClipBoard(Player player) {
        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player);
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);
        World world = actor.getWorld();
        Region region;
        try {
            region = localSession.getRegionSelector(world).getRegion();
        } catch (IncompleteRegionException e) {
            return;
        }
        Optional<CustomCropsWorld<?>> optionalWorld = BukkitCustomCropsPlugin.getInstance().getWorldManager().getWorld(world.getName());
        if (optionalWorld.isEmpty()) {
            return;
        }
        CustomCropsWorld<?> customWorld = optionalWorld.get();

        HashMap<BlockVector3, CustomCropsBlockState> tempMap = new HashMap<>();

        BlockVector3 maxPoint = region.getMaximumPoint();
        BlockVector3 minPoint = region.getMinimumPoint();
        int minX = Math.min(minPoint.getBlockX(), maxPoint.getBlockX());
        int minY = Math.min(minPoint.getBlockY(), maxPoint.getBlockY());
        int minZ = Math.min(minPoint.getBlockZ(), maxPoint.getBlockZ());
        int maxX = Math.max(maxPoint.getBlockX(), minPoint.getBlockX());
        int maxY = Math.max(maxPoint.getBlockY(), minPoint.getBlockY());
        int maxZ = Math.max(maxPoint.getBlockZ(), minPoint.getBlockZ());
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Pos3 pos3 = new Pos3(x, y, z);
                    BlockVector3 vector3 = BlockVector3.at(x, y, z);
                    Optional<CustomCropsBlockState> optionalState = customWorld.getBlockState(pos3);
                    optionalState.ifPresent(customCropsBlockState -> tempMap.put(vector3, customCropsBlockState));
                }
            }
        }

        BukkitCustomCropsPlugin.getInstance().getScheduler().sync().runLater(() -> {
            ClipboardHolder holder;
            try {
                holder = localSession.getClipboard();
            } catch (EmptyClipboardException e) {
                return;
            }
            Clipboard clipboard = holder.getClipboard();
            for (Map.Entry<BlockVector3, CustomCropsBlockState> entry : tempMap.entrySet()) {
                BaseBlock baseBlock = clipboard.getFullBlock(entry.getKey());
                CustomCropsBlockState state = entry.getValue();
                CompoundTag tag = baseBlock.getNbtData();
                if (tag != null) {
                    Map<String, Tag> map = tag.getValue();
                    map.put("cc_type", new StringTag(state.type().type().asString()));
                    map.put("cc_data", new ByteArrayTag(state.getNBTDataAsBytes()));
                } else {
                    try {
                        clipboard.setBlock(entry.getKey(), (BaseBlock) baseBlockConstructor.newInstance(
                                baseBlock.toImmutableState(),
                                new CompoundTag(
                                        new HashMap<>(
                                                Map.of(
                                                        "cc_type", new StringTag(state.type().type().asString()),
                                                        "cc_data", new ByteArrayTag(state.getNBTDataAsBytes())
                                                )
                                        )
                                )
                        ));
                    } catch (WorldEditException | InvocationTargetException |
                             InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, 1, player.getLocation());
    }

    public void updateCopyClipBoard(Player player) {
        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player);
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);
        ClipboardHolder holder;
        try {
            holder = localSession.getClipboard();
        } catch (EmptyClipboardException e) {
            return;
        }
        Clipboard clipboard = holder.getClipboard();

        Region region = clipboard.getRegion();
        World world = region.getWorld();
        if (world == null) return;

        BukkitCustomCropsPlugin.getInstance().getWorldManager().getWorld(world.getName())
                .ifPresent(customWorld -> {
                    BukkitCustomCropsPlugin.getInstance().getScheduler().async().execute(() -> {
                        BlockVector3 maxPoint = region.getMaximumPoint();
                        BlockVector3 minPoint = region.getMinimumPoint();
                        int minX = Math.min(minPoint.getBlockX(), maxPoint.getBlockX());
                        int minY = Math.min(minPoint.getBlockY(), maxPoint.getBlockY());
                        int minZ = Math.min(minPoint.getBlockZ(), maxPoint.getBlockZ());
                        int maxX = Math.max(maxPoint.getBlockX(), minPoint.getBlockX());
                        int maxY = Math.max(maxPoint.getBlockY(), minPoint.getBlockY());
                        int maxZ = Math.max(maxPoint.getBlockZ(), minPoint.getBlockZ());
                        for (int x = minX; x <= maxX; x++) {
                            for (int y = minY; y <= maxY; y++) {
                                for (int z = minZ; z <= maxZ; z++) {
                                    Pos3 pos3 = new Pos3(x, y, z);
                                    BlockVector3 vector3 = BlockVector3.at(x, y, z);
                                    Optional<CustomCropsBlockState> optionalState = customWorld.getBlockState(pos3);
                                    if (optionalState.isPresent()) {
                                        BaseBlock baseBlock = clipboard.getFullBlock(vector3);
                                        CustomCropsBlockState state = optionalState.get();
                                        CompoundTag tag = baseBlock.getNbtData();
                                        if (tag != null) {
                                            Map<String, Tag> map = tag.getValue();
                                            map.put("cc_type", new StringTag(state.type().type().asString()));
                                            map.put("cc_data", new ByteArrayTag(state.getNBTDataAsBytes()));
                                        } else {
                                            try {
                                                clipboard.setBlock(vector3, (BaseBlock) baseBlockConstructor.newInstance(
                                                        baseBlock.toImmutableState(),
                                                        new CompoundTag(
                                                                new HashMap<>(
                                                                        Map.of(
                                                                                "cc_type", new StringTag(state.type().type().asString()),
                                                                                "cc_data", new ByteArrayTag(state.getNBTDataAsBytes())
                                                                        )
                                                                )
                                                        )
                                                ));
                                            } catch (WorldEditException | InvocationTargetException |
                                                     InstantiationException | IllegalAccessException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                });
    }
}

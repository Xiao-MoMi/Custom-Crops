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

package net.momirealms.customcrops.bukkit.command.feature;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.world.*;
import net.momirealms.customcrops.api.util.LocationUtils;
import net.momirealms.customcrops.bukkit.command.BukkitCommandFeature;
import net.momirealms.customcrops.common.command.CustomCropsCommandManager;
import net.momirealms.customcrops.common.locale.MessageConstants;
import net.momirealms.customcrops.common.plugin.scheduler.SchedulerTask;
import net.momirealms.sparrow.heart.SparrowHeart;
import net.momirealms.sparrow.heart.feature.color.NamedTextColor;
import net.momirealms.sparrow.heart.feature.highlight.HighlightBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DebugInsightCommand extends BukkitCommandFeature<CommandSender> implements Listener {

    public DebugInsightCommand(CustomCropsCommandManager<CommandSender> commandManager) {
        super(commandManager);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(context -> {
                    BukkitCustomCropsPlugin plugin = BukkitCustomCropsPlugin.getInstance();
                    Player player = context.sender();
                    if (player.hasMetadata("customcrops:insight")) {
                        player.removeMetadata("customcrops:insight", plugin.getBootstrap());
                        handleFeedback(context, MessageConstants.COMMAND_DEBUG_INSIGHT_OFF);
                        return;
                    }

                    player.setMetadata("customcrops:insight", new FixedMetadataValue(plugin.getBootstrap(), 1));
                    new InsightPlayer(player.getUniqueId());
                    handleFeedback(context, MessageConstants.COMMAND_DEBUG_INSIGHT_ON);
                });
    }

    @Override
    public String getFeatureID() {
        return "debug_insight";
    }

    @Override
    public void unregisterRelatedFunctions() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void registerRelatedFunctions() {
        Bukkit.getPluginManager().registerEvents(this, BukkitCustomCropsPlugin.getInstance().getBootstrap());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.removeMetadata("customcrops:insight", BukkitCustomCropsPlugin.getInstance().getBootstrap());
    }

    public static class InsightPlayer implements Runnable {

        private final SchedulerTask task;
        private final UUID uuid;
        private final HashMap<ChunkPos, HighlightBlocks[]> highlightCache = new HashMap<>();
        private ChunkPos currentPos = null;
        private String currentWorld = null;

        public InsightPlayer(UUID uuid) {
            this.uuid = uuid;
            this.task = BukkitCustomCropsPlugin.getInstance().getScheduler().asyncRepeating(this, 50, 500, TimeUnit.MILLISECONDS);
        }

        @Override
        public void run() {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                task.cancel();
                highlightCache.clear();
                return;
            }
            if (!player.hasMetadata("customcrops:insight")) {
                for (HighlightBlocks[] blocks : highlightCache.values()) {
                    for (HighlightBlocks block : blocks) {
                        block.destroy(player);
                    }
                }
                highlightCache.clear();
                task.cancel();
                return;
            }
            World world = player.getWorld();
            String worldName = player.getWorld().getName();
            if (!worldName.equals(currentWorld)) {
                currentWorld = worldName;
                for (HighlightBlocks[] blocks : highlightCache.values()) {
                    for (HighlightBlocks block : blocks) {
                        block.destroy(player);
                    }
                }
                highlightCache.clear();
                currentPos = null;
            }

            Optional<CustomCropsWorld<?>> optionWorld = BukkitCustomCropsPlugin.getInstance().getWorldManager().getWorld(world);
            if (optionWorld.isEmpty()) {
                return;
            }
            CustomCropsWorld<?> customCropsWorld = optionWorld.get();

            Chunk chunk = player.getLocation().getChunk();
            ChunkPos chunkPos = ChunkPos.fromBukkitChunk(chunk);
            if (!chunkPos.equals(currentPos)) {
                currentPos = chunkPos;
                HashSet<ChunkPos> nearbyChunks = new HashSet<>();
                for (int i = -2; i < 3; i++) {
                    for (int j = -2; j < 3; j++) {
                        nearbyChunks.add(ChunkPos.of(currentPos.x() + i, currentPos.z() + j));
                    }
                }
                ArrayList<ChunkPos> chunksToRemove = new ArrayList<>();
                for (Map.Entry<ChunkPos, HighlightBlocks[]> entry : highlightCache.entrySet()) {
                    if (!nearbyChunks.contains(entry.getKey())) {
                        chunksToRemove.add(entry.getKey());
                    }
                }
                for (ChunkPos pos : chunksToRemove) {
                    HighlightBlocks[] blocks = highlightCache.remove(pos);
                    if (blocks != null) {
                        for (HighlightBlocks block : blocks) {
                            block.destroy(player);
                        }
                    }
                }
                for (ChunkPos pos : nearbyChunks) {
                    if (!highlightCache.containsKey(pos)) {
                        customCropsWorld.getChunk(pos).ifPresentOrElse(cropsChunk -> {
                            ArrayList<HighlightBlocks> highlightBlockList = new ArrayList<>();
                            HashMap<net.momirealms.customcrops.api.misc.NamedTextColor, List<Location>> blockMap = new HashMap<>();
                            for (CustomCropsSection section : cropsChunk.sections()) {
                                for (Map.Entry<BlockPos, CustomCropsBlockState> entry : section.blockMap().entrySet()) {
                                    net.momirealms.customcrops.api.misc.NamedTextColor namedTextColor = entry.getValue().type().insightColor();
                                    Location location = LocationUtils.toSurfaceCenterLocation(entry.getKey().toPos3(pos).toLocation(world));
                                    List<Location> locations = blockMap.computeIfAbsent(namedTextColor, k -> new ArrayList<>());
                                    locations.add(location);
                                }
                            }
                            for (Map.Entry<net.momirealms.customcrops.api.misc.NamedTextColor, List<Location>> entry : blockMap.entrySet()) {
                                highlightBlockList.add(SparrowHeart.getInstance().highlightBlocks(
                                        player, NamedTextColor.namedColor(entry.getKey().getValue()), entry.getValue().toArray(new Location[0])
                                ));
                            }
                            highlightCache.put(pos, highlightBlockList.toArray(new HighlightBlocks[0]));
                        }, () -> {
                            highlightCache.put(pos, new HighlightBlocks[0]);
                        });
                    }
                }
            }
        }
    }
}

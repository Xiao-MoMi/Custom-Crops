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

import net.kyori.adventure.text.Component;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.AbstractItemManager;
import net.momirealms.customcrops.api.core.world.*;
import net.momirealms.customcrops.bukkit.command.BukkitCommandFeature;
import net.momirealms.customcrops.common.command.CustomCropsCommandManager;
import net.momirealms.customcrops.common.locale.MessageConstants;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.Map;
import java.util.Optional;

public class UnsafeRestoreCommand extends BukkitCommandFeature<CommandSender> {

    public UnsafeRestoreCommand(CustomCropsCommandManager<CommandSender> commandManager) {
        super(commandManager);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .flag(manager.flagBuilder("silent").build())
                .handler(context -> {
                    Player player = context.sender();
                    World bukkitWorld = player.getWorld();
                    Optional<CustomCropsWorld<?>> optional = BukkitCustomCropsPlugin.getInstance().getWorldManager().getWorld(bukkitWorld);
                    if (optional.isEmpty()) {
                        handleFeedback(context, MessageConstants.COMMAND_UNSAFE_RESTORE_FAILURE_WORLD, Component.text(bukkitWorld.getName()));
                        return;
                    }
                    ChunkPos chunkPos = ChunkPos.fromBukkitChunk(player.getLocation().getChunk());
                    CustomCropsWorld<?> world = optional.get();
                    Optional<CustomCropsChunk> chunk = world.getLoadedChunk(chunkPos);
                    if (chunk.isEmpty()) {
                        handleFeedback(context, MessageConstants.COMMAND_UNSAFE_RESTORE_FAILURE_CHUNK);
                        return;
                    }
                    CustomCropsChunk customCropsChunk = chunk.get();
                    AbstractItemManager itemManager = BukkitCustomCropsPlugin.getInstance().getItemManager();
                    int totalBlocks = 0;
                    int restoredBlocks = 0;
                    for (CustomCropsSection section : customCropsChunk.sections()) {
                        for (Map.Entry<BlockPos, CustomCropsBlockState> entry : section.blockMap().entrySet()) {
                            totalBlocks++;
                            Pos3 pos3 = entry.getKey().toPos3(chunkPos);
                            Location location = pos3.toLocation(bukkitWorld);
                            String realID = itemManager.anyID(location);
                            if (!entry.getValue().type().isInstance(realID)) {
                                restoredBlocks++;
                                entry.getValue().type().restore(location, entry.getValue());
                            }
                        }
                    }
                    handleFeedback(context, MessageConstants.COMMAND_UNSAFE_RESTORE_SUCCESS, Component.text(restoredBlocks), Component.text(totalBlocks));
                });
    }

    @Override
    public String getFeatureID() {
        return "unsafe_restore";
    }
}

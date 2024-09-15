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
import net.momirealms.customcrops.api.core.world.ChunkPos;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.CustomCropsWorldImpl;
import net.momirealms.customcrops.bukkit.command.BukkitCommandFeature;
import net.momirealms.customcrops.common.command.CustomCropsCommandManager;
import net.momirealms.customcrops.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.Optional;

public class UnsafeDeleteCommand extends BukkitCommandFeature<CommandSender> {

    public UnsafeDeleteCommand(CustomCropsCommandManager<CommandSender> commandManager) {
        super(commandManager);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .flag(manager.flagBuilder("silent").build())
                .handler(context -> {
                    Player player = context.sender();
                    Optional<CustomCropsWorld<?>> optional = BukkitCustomCropsPlugin.getInstance().getWorldManager().getWorld(player.getWorld());
                    if (optional.isEmpty()) {
                        handleFeedback(context, MessageConstants.COMMAND_UNSAFE_DELETE_FAILURE_WORLD, Component.text(player.getWorld().getName()));
                        return;
                    }
                    CustomCropsWorld<?> world = optional.get();
                    CustomCropsWorldImpl<?> customCropsWorld = (CustomCropsWorldImpl<?>) world;
                    ChunkPos chunkPos = ChunkPos.fromBukkitChunk(player.getLocation().getChunk());
                    customCropsWorld.deleteChunk(chunkPos);
                    handleFeedback(context, MessageConstants.COMMAND_UNSAFE_DELETE_SUCCESS);
                });
    }

    @Override
    public String getFeatureID() {
        return "unsafe_delete";
    }
}

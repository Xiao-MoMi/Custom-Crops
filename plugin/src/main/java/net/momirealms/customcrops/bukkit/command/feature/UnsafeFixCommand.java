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
import net.momirealms.customcrops.api.core.Registries;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
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

import java.util.Optional;

public class UnsafeFixCommand extends BukkitCommandFeature<CommandSender> {

    public UnsafeFixCommand(CustomCropsCommandManager<CommandSender> commandManager) {
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
                        handleFeedback(context, MessageConstants.COMMAND_UNSAFE_FIX_FAILURE_WORLD, Component.text(bukkitWorld.getName()));
                        return;
                    }
                    ChunkPos chunkPos = ChunkPos.fromBukkitChunk(player.getLocation().getChunk());
                    CustomCropsWorld<?> world = optional.get();
                    CustomCropsChunk chunk = world.getOrCreateChunk(chunkPos);

                    CustomCropsSection section = chunk.getSection(BlockPos.fromPos3( Pos3.from(player.getLocation())).sectionID());
                    AbstractItemManager itemManager = BukkitCustomCropsPlugin.getInstance().getItemManager();
                    Location baseLocation = new Location(bukkitWorld, chunkPos.x() * 16, section.getSectionID() * 16, chunkPos.z() * 16);

                    int fixedBlocks = 0;
                    int corruptedBlocks = 0;

                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < 16; y++) {
                                Location temp = baseLocation.clone().add(x, y, z);
                                String id = itemManager.anyID(temp);
                                Pos3 pos3 = Pos3.from(temp);
                                Optional<CustomCropsBlockState> previousState = chunk.getBlockState(pos3);
                                if (previousState.isPresent()) {
                                    CustomCropsBlockState state = previousState.get();
                                    if (state.type().isInstance(id)) {
                                        continue;
                                    } else {
                                        corruptedBlocks++;
                                        chunk.removeBlockState(pos3);
                                    }
                                }
                                CustomCropsBlock customCropsBlock = Registries.BLOCKS.get(id);
                                if (customCropsBlock == null) {
                                    continue;
                                }
                                CustomCropsBlockState state = customCropsBlock.createBlockState(id);
                                if (state != null) {
                                    chunk.addBlockState(pos3, state);
                                    fixedBlocks++;
                                }
                            }
                        }
                    }

                    handleFeedback(context, MessageConstants.COMMAND_UNSAFE_FIX_SUCCESS, Component.text(fixedBlocks), Component.text(corruptedBlocks));
                });
    }

    @Override
    public String getFeatureID() {
        return "unsafe_fix";
    }
}

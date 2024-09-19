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
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.bukkit.command.BukkitCommandFeature;
import net.momirealms.customcrops.common.command.CustomCropsCommandManager;
import net.momirealms.customcrops.common.locale.MessageConstants;
import net.momirealms.customcrops.common.locale.TranslationManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.Optional;

public class DebugDataCommand extends BukkitCommandFeature<CommandSender> {

    public DebugDataCommand(CustomCropsCommandManager<CommandSender> commandManager) {
        super(commandManager);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .flag(manager.flagBuilder("this").build())
                .handler(context -> {
                    Player player = context.sender();
                    Location location;
                    Block block;
                    if (context.flags().hasFlag("this")) {
                        location = player.getLocation();
                        block = location.getBlock();
                    } else {
                        block = player.getTargetBlockExact(10);
                        if (block == null) {
                            handleFeedback(context, MessageConstants.COMMAND_DEBUG_DATA_FAILURE);
                            return;
                        }
                        location = block.getLocation();
                    }
                    BukkitCustomCropsPlugin.getInstance().getWorldManager().getWorld(location.getWorld()).ifPresent(world -> {
                        Optional<CustomCropsBlockState> state = world.getBlockState(Pos3.from(location));
                        state.ifPresent(customCropsBlockState -> {
                            String cData = customCropsBlockState.asString();
                            TranslatableComponent component = MessageConstants.COMMAND_DEBUG_DATA_SUCCESS_CUSTOM
                                    .arguments(Component.text(cData))
                                    .build();
                            commandManager.feedbackConsumer().accept(context.sender(), component.key(), TranslationManager.render(component).clickEvent(ClickEvent.copyToClipboard(cData)));
                        });
                    });
                    String bData = block.getBlockData().getAsString();
                    TranslatableComponent component = MessageConstants.COMMAND_DEBUG_DATA_SUCCESS_VANILLA
                            .arguments(Component.text(bData))
                            .build();
                    commandManager.feedbackConsumer().accept(context.sender(), component.key(), TranslationManager.render(component).clickEvent(ClickEvent.copyToClipboard(bData)));
                });
    }

    @Override
    public String getFeatureID() {
        return "debug_data";
    }


}

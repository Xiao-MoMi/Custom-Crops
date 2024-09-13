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
import net.momirealms.customcrops.bukkit.command.BukkitCommandFeature;
import net.momirealms.customcrops.common.command.CustomCropsCommandManager;
import net.momirealms.customcrops.common.helper.AdventureHelper;
import net.momirealms.customcrops.common.sender.Sender;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class DebugWorldsCommand extends BukkitCommandFeature<CommandSender> {

    public DebugWorldsCommand(CustomCropsCommandManager<CommandSender> commandManager) {
        super(commandManager);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .handler(context -> {
                    Sender sender = BukkitCustomCropsPlugin.getInstance().getSenderFactory().wrap(context.sender());
                    for (World world : Bukkit.getWorlds()) {
                        BukkitCustomCropsPlugin.getInstance().getWorldManager().getWorld(world).ifPresent(w -> {
                            sender.sendMessage(AdventureHelper.miniMessage("<gold>World: " + world.getName() + "</gold>"));
                            sender.sendMessage(AdventureHelper.miniMessage(" - Loaded regions: " + w.loadedRegions().length));
                            sender.sendMessage(AdventureHelper.miniMessage(" - Loaded chunks: " + w.loadedChunks().length));
                            sender.sendMessage(AdventureHelper.miniMessage(" - Lazy chunks: " + w.lazyChunks().length));
                        });
                    }
                });
    }

    @Override
    public String getFeatureID() {
        return "debug_worlds";
    }
}

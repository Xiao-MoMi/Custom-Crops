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

package net.momirealms.customcrops.common.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public interface CommandFeature<C> {

    Command<C> registerCommand(CommandManager<C> cloudCommandManager, Command.Builder<C> builder);

    String getFeatureID();

    void registerRelatedFunctions();

    void unregisterRelatedFunctions();

    void handleFeedback(CommandContext<?> context, TranslatableComponent.Builder key, Component... args);

    void handleFeedback(C sender, TranslatableComponent.Builder key, Component... args);

    CustomCropsCommandManager<C> getCustomCropsCommandManager();

    CommandConfig<C> getCommandConfig();
}

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
import net.momirealms.customcrops.api.core.ConfigManager;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Season;
import net.momirealms.customcrops.api.integration.SeasonProvider;
import net.momirealms.customcrops.bukkit.command.BukkitCommandFeature;
import net.momirealms.customcrops.common.command.CustomCropsCommandManager;
import net.momirealms.customcrops.common.locale.MessageConstants;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.WorldParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SetSeasonCommand extends BukkitCommandFeature<CommandSender> {

    public SetSeasonCommand(CustomCropsCommandManager<CommandSender> commandManager) {
        super(commandManager);
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("world", StringParser.stringComponent(StringParser.StringMode.GREEDY).suggestionProvider(new SuggestionProvider<>() {
                    @Override
                    public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<Object> context, @NonNull CommandInput input) {
                        return CompletableFuture.completedFuture(Bukkit.getWorlds().stream().map(World::getName).map(Suggestion::suggestion).toList());
                    }
                }))
                .required("season", StringParser.stringComponent().suggestionProvider(SuggestionProvider.suggesting(
                        Suggestion.suggestion("spring"), Suggestion.suggestion("summer"),Suggestion.suggestion("autumn"),Suggestion.suggestion("winter")
                )))
                .handler(context -> {
                    String worldName = context.get("world");
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) return;
                    SeasonProvider provider = BukkitCustomCropsPlugin.getInstance().getWorldManager().seasonProvider();

                    if (provider.identifier().equals("CustomCrops")) {
                        Optional<CustomCropsWorld<?>> optionalWorld = BukkitCustomCropsPlugin.getInstance().getWorldManager().getWorld(world);
                        if (optionalWorld.isPresent()) {
                            CustomCropsWorld<?> customCropsWorld = optionalWorld.get();
                            String season = context.get("season");
                            Season seasonEnum;
                            try {
                                seasonEnum = Season.valueOf(season.toUpperCase(Locale.ENGLISH));
                                if (seasonEnum == Season.DISABLE) {
                                    throw new IllegalArgumentException("Invalid season: " + season);
                                }
                            } catch (IllegalArgumentException e) {
                                handleFeedback(context, MessageConstants.COMMAND_SET_SEASON_FAILURE_INVALID, Component.text(world.getName()), Component.text(season));
                                return;
                            }
                            if (customCropsWorld.setting().enableSeason()) {
                                if (ConfigManager.syncSeasons()) {
                                    if (ConfigManager.referenceWorld().equals(world.getName())) {
                                        customCropsWorld.extraData().setSeason(seasonEnum);
                                        handleFeedback(context, MessageConstants.COMMAND_SET_SEASON_SUCCESS, Component.text(world.getName()), Component.text(seasonEnum.translation()));
                                    } else {
                                        handleFeedback(context, MessageConstants.COMMAND_SET_SEASON_FAILURE_REFERENCE, Component.text(world.getName()));
                                    }
                                } else {
                                    customCropsWorld.extraData().setSeason(seasonEnum);
                                    handleFeedback(context, MessageConstants.COMMAND_SET_SEASON_SUCCESS, Component.text(world.getName()), Component.text(seasonEnum.translation()));
                                }
                            } else {
                                handleFeedback(context, MessageConstants.COMMAND_SET_SEASON_FAILURE_DISABLE, Component.text(world.getName()));
                            }
                        } else {
                            handleFeedback(context, MessageConstants.COMMAND_SET_SEASON_FAILURE_DISABLE, Component.text(world.getName()));
                        }
                    } else {
                        handleFeedback(context, MessageConstants.COMMAND_SET_SEASON_FAILURE_OTHER, Component.text(world.getName()), Component.text(provider.identifier()));
                    }
                });
    }

    @Override
    public String getFeatureID() {
        return "set_season";
    }
}

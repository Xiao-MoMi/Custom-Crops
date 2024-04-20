/*
 *  Copyright (C) <2022> <XiaoMoMi>
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

package net.momirealms.customcrops.manager;

import dev.jorel.commandapi.*;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Initable;
import net.momirealms.customcrops.api.integration.SeasonInterface;
import net.momirealms.customcrops.api.manager.AdventureManager;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.manager.MessageManager;
import net.momirealms.customcrops.api.mechanic.item.ItemType;
import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsChunk;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsSection;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import net.momirealms.customcrops.compatibility.season.InBuiltSeason;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.WorldInfo;

import java.util.Locale;
import java.util.Optional;

public class CommandManager implements Initable {

    private final CustomCropsPlugin plugin;

    public CommandManager(CustomCropsPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    @Override
    public void init() {
        if (!CommandAPI.isLoaded())
            CommandAPI.onLoad(new CommandAPIBukkitConfig(plugin).silentLogs(true));
        new CommandAPICommand("customcrops")
                .withPermission(CommandPermission.OP)
                .withAliases("ccrops")
                .withSubcommands(
                        getReloadCommand(),
                        getAboutCommand(),
                        getSeasonCommand(),
                        getDateCommand(),
                        getForceTickCommand(),
                        getUnsafeCommand()
                )
                .register();
    }

    @Override
    public void disable() {
        CommandAPI.unregister("customcrops");
    }

    private CommandAPICommand getReloadCommand() {
        return new CommandAPICommand("reload")
                .executes((sender, args) -> {
                    long time1 = System.currentTimeMillis();
                    plugin.reload();
                    long time2 = System.currentTimeMillis();
                    plugin.getAdventure().sendMessageWithPrefix(sender, MessageManager.reloadMessage().replace("{time}", String.valueOf(time2 - time1)));
                });
    }

    private CommandAPICommand getUnsafeCommand() {
        return new CommandAPICommand("unsafe")
                .withSubcommands(
                        new CommandAPICommand("delete-chunk-data").executesPlayer((player, args) -> {
                            CustomCropsPlugin.get().getWorldManager().getCustomCropsWorld(player.getWorld()).ifPresent(customCropsWorld -> {
                                var optionalChunk = customCropsWorld.getLoadedChunkAt(ChunkPos.getByBukkitChunk(player.getChunk()));
                                if (optionalChunk.isEmpty()) {
                                    AdventureManager.getInstance().sendMessageWithPrefix(player, "<white>This chunk doesn't have any data.");
                                    return;
                                }
                                customCropsWorld.deleteChunk(ChunkPos.getByBukkitChunk(player.getChunk()));
                                AdventureManager.getInstance().sendMessageWithPrefix(player, "<white>Done.");
                            });
                        })
                );
    }

    private CommandAPICommand getAboutCommand() {
        return new CommandAPICommand("about").executes((sender, args) -> {
            plugin.getAdventure().sendMessage(sender, "<#FFA500>⛈ CustomCrops <gray>- <#87CEEB>" + CustomCropsPlugin.getInstance().getVersionManager().getPluginVersion());
            plugin.getAdventure().sendMessage(sender, "<#FFFFE0>Ultra-customizable planting experience for Minecraft servers");
            plugin.getAdventure().sendMessage(sender, "<#DA70D6>\uD83E\uDDEA Author: <#FFC0CB>XiaoMoMi");
            plugin.getAdventure().sendMessage(sender, "<#FF7F50>\uD83D\uDD25 Contributors: <#FFA07A>Cha_Shao<white>, <#FFA07A>TopOrigin<white>, <#FFA07A>AmazingCat");
            plugin.getAdventure().sendMessage(sender, "<#FFD700>⭐ <click:open_url:https://mo-mi.gitbook.io/xiaomomi-plugins/plugin-wiki/customcrops>Document</click> <#A9A9A9>| <#FAFAD2>⛏ <click:open_url:https://github.com/Xiao-MoMi/Custom-Crops>Github</click> <#A9A9A9>| <#48D1CC>\uD83D\uDD14 <click:open_url:https://polymart.org/resource/customcrops.2625>Polymart</click>");
        });
    }

    private CommandAPICommand getForceTickCommand() {
        return new CommandAPICommand("force-tick")
                .withArguments(new StringArgument("world").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> Bukkit.getWorlds().stream().map(WorldInfo::getName).toList().toArray(new String[0]))))
                .withArguments(new StringArgument("type").replaceSuggestions(ArgumentSuggestions.strings("sprinkler", "crop", "pot", "scarecrow", "greenhouse")))
                .executes((sender, args) -> {
                    String worldName = (String) args.get("world");
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        plugin.getAdventure().sendMessageWithPrefix(sender, "CustomCrops is not enabled in that world");
                        return;
                    }
                    ItemType itemType = ItemType.valueOf(((String) args.get("type")).toUpperCase(Locale.ENGLISH));
                    Optional<CustomCropsWorld> customCropsWorld = plugin.getWorldManager().getCustomCropsWorld(world);
                    if (customCropsWorld.isEmpty()) {
                        plugin.getAdventure().sendMessageWithPrefix(sender, "CustomCrops is not enabled in that world");
                        return;
                    }
                    plugin.getScheduler().runTaskAsync(() -> {
                        for (CustomCropsChunk chunk : customCropsWorld.get().getChunkStorage()) {
                            for (CustomCropsSection section : chunk.getSections()) {
                                for (CustomCropsBlock block : section.getBlocks()) {
                                    if (block.getType() == itemType) {
                                        block.tick(1, false);
                                    }
                                }
                            }
                        }
                    });
                });
    }

    private CommandAPICommand getDateCommand() {
        return new CommandAPICommand("date")
                .withSubcommands(
                        new CommandAPICommand("get")
                                .withArguments(new StringArgument("world").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> plugin.getWorldManager().getCustomCropsWorlds().stream()
                                        .filter(customCropsWorld -> customCropsWorld.getWorldSetting().isEnableSeason())
                                        .map(CustomCropsWorld::getWorldName)
                                        .toList()
                                        .toArray(new String[0]))))
                                .executes((sender, args) -> {
                                    String worldName = (String) args.get("world");
                                    World world = Bukkit.getWorld(worldName);
                                    if (world == null) {
                                        plugin.getAdventure().sendMessageWithPrefix(sender, "CustomCrops is not enabled in that world");
                                        return;
                                    }
                                    plugin.getAdventure().sendMessageWithPrefix(sender, String.valueOf(plugin.getIntegrationManager().getSeasonInterface().getDate(world)));
                                }),
                        new CommandAPICommand("set")
                                .withArguments(new StringArgument("world").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> plugin.getWorldManager().getCustomCropsWorlds().stream()
                                        .filter(customCropsWorld -> customCropsWorld.getWorldSetting().isEnableSeason())
                                        .map(CustomCropsWorld::getWorldName)
                                        .toList()
                                        .toArray(new String[0]))))
                                .withArguments(new IntegerArgument("date",1))
                                .executes((sender, args) -> {
                                    String worldName = (String) args.get("world");
                                    World world = Bukkit.getWorld(worldName);
                                    if (world == null) {
                                        plugin.getAdventure().sendMessageWithPrefix(sender, "CustomCrops is not enabled in that world");
                                        return;
                                    }
                                    int date = (int) args.getOrDefault("date", 1);
                                    SeasonInterface seasonInterface = plugin.getIntegrationManager().getSeasonInterface();
                                    if (!(seasonInterface instanceof InBuiltSeason inBuiltSeason)) {
                                        plugin.getAdventure().sendMessageWithPrefix(sender, "Detected that you are using a season plugin. Please set date in that plugin.");
                                        return;
                                    }
                                    Optional<CustomCropsWorld> customCropsWorld = plugin.getWorldManager().getCustomCropsWorld(world);
                                    if (customCropsWorld.isEmpty()) {
                                        plugin.getAdventure().sendMessageWithPrefix(sender, "CustomCrops is not enabled in that world");
                                        return;
                                    }
                                    if (!customCropsWorld.get().getWorldSetting().isEnableSeason()) {
                                        plugin.getAdventure().sendMessageWithPrefix(sender, "Season is not enabled in that world");
                                        return;
                                    }
                                    if (date > customCropsWorld.get().getWorldSetting().getSeasonDuration()) {
                                        plugin.getAdventure().sendMessageWithPrefix(sender, "Date should be a value no higher than season duration");
                                        return;
                                    }
                                    String pre = String.valueOf(inBuiltSeason.getDate(world));
                                    customCropsWorld.get().getInfoData().setDate(date);
                                    plugin.getAdventure().sendMessageWithPrefix(sender, "Date in world("+world.getName()+"): " + pre + " -> " + date);
                                })
                );
    }

    private CommandAPICommand getSeasonCommand() {
        return new CommandAPICommand("season")
                .withSubcommands(
                        new CommandAPICommand("get")
                                .withArguments(new StringArgument("world").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> plugin.getWorldManager().getCustomCropsWorlds().stream()
                                        .filter(customCropsWorld -> customCropsWorld.getWorldSetting().isEnableSeason())
                                        .map(CustomCropsWorld::getWorldName)
                                        .toList()
                                        .toArray(new String[0]))))
                                .executes((sender, args) -> {
                                    String worldName = (String) args.get("world");
                                    World world = Bukkit.getWorld(worldName);
                                    if (world == null) {
                                        plugin.getAdventure().sendMessageWithPrefix(sender, "CustomCrops is not enabled in that world");
                                        return;
                                    }
                                    plugin.getAdventure().sendMessageWithPrefix(sender, MessageManager.seasonTranslation(plugin.getIntegrationManager().getSeasonInterface().getSeason(world)));
                                }),
                        new CommandAPICommand("set")
                                .withArguments(new StringArgument("world").replaceSuggestions(ArgumentSuggestions.strings(commandSenderSuggestionInfo -> {
                                            if (ConfigManager.syncSeasons()) {
                                                return new String[]{ConfigManager.referenceWorld().getName()};
                                            }
                                            return plugin.getWorldManager().getCustomCropsWorlds().stream()
                                                    .filter(customCropsWorld -> customCropsWorld.getWorldSetting().isEnableSeason())
                                                    .map(CustomCropsWorld::getWorldName)
                                                    .toList()
                                                    .toArray(new String[0]);
                                        })))
                                .withArguments(new StringArgument("season")
                                        .replaceSuggestions(ArgumentSuggestions.stringsWithTooltips(info ->
                                                new IStringTooltip[] {
                                                        StringTooltip.ofString("Spring", MessageManager.seasonTranslation(Season.SPRING)),
                                                        StringTooltip.ofString("Summer", MessageManager.seasonTranslation(Season.SUMMER)),
                                                        StringTooltip.ofString("Autumn", MessageManager.seasonTranslation(Season.AUTUMN)),
                                                        StringTooltip.ofString("Winter", MessageManager.seasonTranslation(Season.WINTER))
                                                }
                                        ))
                                )
                                .executes((sender, args) -> {
                                    String worldName = (String) args.get("world");
                                    World world = Bukkit.getWorld(worldName);
                                    if (world == null) {
                                        plugin.getAdventure().sendMessageWithPrefix(sender, "CustomCrops is not enabled in that world");
                                        return;
                                    }
                                    String seasonName = (String) args.get("season");

                                    SeasonInterface seasonInterface = plugin.getIntegrationManager().getSeasonInterface();
                                    if (!(seasonInterface instanceof InBuiltSeason inBuiltSeason)) {
                                        plugin.getAdventure().sendMessageWithPrefix(sender, "Detected that you are using a season plugin. Please set season in that plugin.");
                                        return;
                                    }
                                    String pre = MessageManager.seasonTranslation(inBuiltSeason.getSeason(world));
                                    Optional<CustomCropsWorld> customCropsWorld = plugin.getWorldManager().getCustomCropsWorld(world);
                                    if (customCropsWorld.isEmpty()) {
                                        plugin.getAdventure().sendMessageWithPrefix(sender, "CustomCrops is not enabled in that world");
                                        return;
                                    }
                                    if (!customCropsWorld.get().getWorldSetting().isEnableSeason()) {
                                        plugin.getAdventure().sendMessageWithPrefix(sender, "Season is not enabled in that world");
                                        return;
                                    }
                                    try {
                                        Season season = Season.valueOf(seasonName.toUpperCase(Locale.ENGLISH));
                                        customCropsWorld.get().getInfoData().setSeason(season);
                                        String next = MessageManager.seasonTranslation(season);
                                        plugin.getAdventure().sendMessageWithPrefix(sender, "Season in world("+world.getName()+"): " + pre + " -> " + next);
                                    } catch (IllegalArgumentException e) {
                                        plugin.getAdventure().sendMessageWithPrefix(sender, "That season doesn't exist");
                                    }
                                })
                );
    }
}

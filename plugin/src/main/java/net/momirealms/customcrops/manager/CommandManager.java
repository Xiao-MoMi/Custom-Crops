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
import dev.jorel.commandapi.arguments.WorldArgument;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Initable;
import net.momirealms.customcrops.api.integration.SeasonInterface;
import net.momirealms.customcrops.api.manager.MessageManager;
import net.momirealms.customcrops.api.mechanic.world.level.CustomCropsWorld;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import net.momirealms.customcrops.compatibility.season.InBuiltSeason;
import org.bukkit.World;

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
                        getDateCommand()
                        //getStressTest()
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

    private CommandAPICommand getAboutCommand() {
        return new CommandAPICommand("about").executes((sender, args) -> {
            plugin.getAdventure().sendMessage(sender, "<#FFA500>⛈ CustomCrops <gray>- <#87CEEB>" + CustomCropsPlugin.getInstance().getVersionManager().getPluginVersion());
            plugin.getAdventure().sendMessage(sender, "<#FFFFE0>Ultra-customizable planting experience for Minecraft servers");
            plugin.getAdventure().sendMessage(sender, "<#DA70D6>\uD83E\uDDEA Author: <#FFC0CB>XiaoMoMi");
            plugin.getAdventure().sendMessage(sender, "<#FF7F50>\uD83D\uDD25 Contributors: <#FFA07A>Cha_Shao<white>, <#FFA07A>TopOrigin<white>, <#FFA07A>AmazingCat");
            plugin.getAdventure().sendMessage(sender, "<#FFD700>⭐ <click:open_url:https://mo-mi.gitbook.io/xiaomomi-plugins/plugin-wiki/customcrops>Document</click> <#A9A9A9>| <#FAFAD2>⛏ <click:open_url:https://github.com/Xiao-MoMi/Custom-Crops>Github</click> <#A9A9A9>| <#48D1CC>\uD83D\uDD14 <click:open_url:https://polymart.org/resource/customcrops.2625>Polymart</click>");
        });
    }

    private CommandAPICommand getDateCommand() {
        return new CommandAPICommand("date")
                .withSubcommands(
                        new CommandAPICommand("get")
                                .withArguments(new WorldArgument("world"))
                                .executes((sender, args) -> {
                                    World world = (World) args.get("world");
                                    plugin.getAdventure().sendMessageWithPrefix(sender, String.valueOf(plugin.getIntegrationManager().getDate(world)));
                                }),
                        new CommandAPICommand("set")
                                .withArguments(new WorldArgument("world"))
                                .withArguments(new IntegerArgument("date",1))
                                .executes((sender, args) -> {
                                    World world = (World) args.get("world");
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
                                .withArguments(new WorldArgument("world"))
                                .executes((sender, args) -> {
                                    World world = (World) args.get("world");
                                    plugin.getAdventure().sendMessageWithPrefix(sender, MessageManager.seasonTranslation(plugin.getIntegrationManager().getSeason(world)));
                                }),
                        new CommandAPICommand("set")
                                .withArguments(new WorldArgument("world"))
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
                                    World world = (World) args.get("world");
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

//    private CommandAPICommand getStressTest() {
//        return new CommandAPICommand("test").executes((sender, args) -> {
//            for (int i = 0; i < 16; i++) {
//                for (int j = 0; j < 16; j++) {
//                    for (int k = -64; k < 0; k++) {
//                        SimpleLocation location = new SimpleLocation("world", 1024 + i, k, 1024 + j);
//                        plugin.getWorldManager().addCropAt(new MemoryCrop(location, "tomato", 0), location);
//                    }
//                    for (int k = 1; k < 64; k++) {
//                        SimpleLocation location = new SimpleLocation("world", 1024 + i, k, 1024 + j);
//                        plugin.getWorldManager().addCropAt(new MemoryCrop(location, "tomato", 1), location);
//                    }
//                    for (int k = 65; k < 128; k++) {
//                        SimpleLocation location = new SimpleLocation("world", 1024 + i, k, 1024 + j);
//                        plugin.getWorldManager().addCropAt(new MemoryCrop(location, "tomato", 2), location);
//                    }
//                    for (int k = 129; k < 165; k++) {
//                        SimpleLocation location = new SimpleLocation("world", 1024 + i, k, 1024 + j);
//                        plugin.getWorldManager().addPotAt(new MemoryPot(location, "default"), location);
//                    }
//                    for (int k = 166; k < 190; k++) {
//                        SimpleLocation location = new SimpleLocation("world", 1024 + i, k, 1024 + j);
//                        plugin.getWorldManager().addPotAt(new MemoryPot(location, "sprinkler"), location);
//                    }
//                    for (int k = 191; k < 250; k++) {
//                        SimpleLocation location = new SimpleLocation("world", 1024 + i, k, 1024 + j);
//                        plugin.getWorldManager().addCropAt(new MemoryCrop(location, "tomato", 3), location);
//                    }
//                    for (int k = 251; k < 300; k++) {
//                        SimpleLocation location = new SimpleLocation("world", 1024 + i, k, 1024 + j);
//                        plugin.getWorldManager().addCropAt(new MemoryCrop(location, "sbsbssbsb", 3), location);
//                    }
//                    for (int k = 301; k < 320; k++) {
//                        SimpleLocation location = new SimpleLocation("world", 1024 + i, k, 1024 + j);
//                        plugin.getWorldManager().addCropAt(new MemoryCrop(location, "sbsbssbsb", 2), location);
//                    }
//                }
//            }
//        });
//    }
}

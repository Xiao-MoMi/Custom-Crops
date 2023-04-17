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

package net.momirealms.customcrops.api.object.basic;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.Function;
import net.momirealms.customcrops.api.util.ConfigUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManager extends Function {


    private CustomCrops plugin;
    
    public static String prefix;
    public static String reload;
    public static String unavailableArgs;
    public static String noConsole;
    public static String notOnline;
    public static String lackArgs;
    public static String nonArgs;
    public static String beforePlant;
    public static String unsuitablePot;
    public static String reachChunkLimit;
    public static String spring;
    public static String summer;
    public static String autumn;
    public static String winter;
    public static String noPerm;
    public static String noSeason;
    public static String setSeason;
    public static String setDate;
    public static String worldNotExist;
    public static String seasonNotExist;

    public MessageManager(CustomCrops plugin) {
        this.plugin =plugin;
    }

    @Override
    public void load() {
        this.loadMessage();
    }

    private void loadMessage() {
        YamlConfiguration config = ConfigUtils.getConfig("messages" + File.separator + "messages_" + ConfigManager.lang + ".yml");
        prefix = config.getString("prefix","<gradient:#ff206c:#fdee55>[CustomCrops] </gradient>");
        reload = config.getString("reload", "<white>Reloaded! Took <green>{time}ms.");
        unavailableArgs = config.getString("invalid-args", "<white>Invalid arguments.");
        noConsole = config.getString("no-console", "This command can only be executed by a player.");
        notOnline = config.getString("not-online", "<white>Player {player} is not online.");
        lackArgs = config.getString("lack-args", "<white>Arguments are insufficient.");
        nonArgs = config.getString("not-none-args", "<white>Not a none argument command.");
        beforePlant = config.getString("before-plant", "<white>This fertilizer can only be used before planting.");
        unsuitablePot = config.getString("unsuitable-pot", "<white>You can't plant the seed in this pot.");
        reachChunkLimit = config.getString("reach-crop-limit", "<white>The number of crops has reached the limitation.");
        noPerm = config.getString("no-perm", "<red>You don't have permission to do that.");
        spring = config.getString("spring", "Spring");
        summer = config.getString("summer", "Summer");
        autumn = config.getString("autumn", "Autumn");
        winter = config.getString("winter", "Winter");
        noSeason = config.getString("no-season", "SEASON DISABLED IN THIS WORLD");
        setSeason = config.getString("set-season", "<white>Successfully set {world}'s season to {season}.");
        setDate = config.getString("set-date", "<white>Successfully set {world}'s date to {date}.");
        worldNotExist = config.getString("world-not-exist", "<white>World {world} does not exist.");
        seasonNotExist = config.getString("season-not-exist", "<white>Season {season} does not exist.");
    }
}

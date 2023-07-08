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
import net.momirealms.customcrops.util.ConfigUtils;
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
    public static String forceWork;
    public static String forceConsume;
    public static String forceGrow;

    public MessageManager(CustomCrops plugin) {
        this.plugin =plugin;
    }

    @Override
    public void load() {
        this.loadMessage();
    }

    private void loadMessage() {
        YamlConfiguration config = ConfigUtils.getConfig("messages" + File.separator + "messages_" + ConfigManager.lang + ".yml");
        prefix = config.getString("messages.prefix","<gradient:#ff206c:#fdee55>[CustomCrops] </gradient>");
        reload = config.getString("messages.reload", "<white>Reloaded! Took <green>{time}ms.");
        unavailableArgs = config.getString("messages.invalid-args", "<white>Invalid arguments.");
        noConsole = config.getString("messages.no-console", "This command can only be executed by a player.");
        notOnline = config.getString("messages.not-online", "<white>Player {player} is not online.");
        lackArgs = config.getString("messages.lack-args", "<white>Arguments are insufficient.");
        nonArgs = config.getString("messages.not-none-args", "<white>Not a none argument command.");
        beforePlant = config.getString("messages.before-plant", "<white>This fertilizer can only be used before planting.");
        unsuitablePot = config.getString("messages.unsuitable-pot", "<white>You can't plant the seed in this pot.");
        reachChunkLimit = config.getString("messages.reach-crop-limit", "<white>The number of crops has reached the limitation.");
        noPerm = config.getString("messages.no-perm", "<red>You don't have permission to do that.");
        spring = config.getString("messages.spring", "Spring");
        summer = config.getString("messages.summer", "Summer");
        autumn = config.getString("messages.autumn", "Autumn");
        winter = config.getString("messages.winter", "Winter");
        noSeason = config.getString("messages.no-season", "SEASON DISABLED IN THIS WORLD");
        setSeason = config.getString("messages.set-season", "<white>Successfully set {world}'s season to {season}.");
        setDate = config.getString("messages.set-date", "<white>Successfully set {world}'s date to {date}.");
        worldNotExist = config.getString("messages.world-not-exist", "<white>World {world} does not exist.");
        seasonNotExist = config.getString("messages.season-not-exist", "<white>Season {season} does not exist.");
        forceWork = config.getString("messages.force-sprinkler-work", "<white>Forced {world}'s sprinklers to work.");
        forceConsume = config.getString("messages.force-consume", "<white>Forced {world}'s pots to reduce water amount and the remaining use of fertilizers.");
        forceGrow = config.getString("messages.force-grow", "<white>Forced {world}'s crops to grow one point.");
    }
}

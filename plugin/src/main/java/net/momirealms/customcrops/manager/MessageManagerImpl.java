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

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Reloadable;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.manager.MessageManager;
import net.momirealms.customcrops.api.mechanic.world.season.Season;
import net.momirealms.customcrops.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageManagerImpl extends MessageManager implements Reloadable {

    private CustomCropsPlugin plugin;
    private String reload;
    private String prefix;
    private String spring;
    private String summer;
    private String autumn;
    private String winter;
    private String noSeason;

    public MessageManagerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        YamlConfiguration config = ConfigUtils.getConfig("messages" + File.separator + ConfigManager.lang() + ".yml");
        ConfigurationSection section = config.getConfigurationSection("messages");
        if (section != null) {
            prefix = section.getString("prefix", "<gradient:#ff206c:#fdee55>[CustomCrops]</gradient> ");
            reload = section.getString("reload", "<white>Reloaded! Took <green>{time}ms.</green></white>");

            spring = section.getString("spring", "Spring");
            summer = section.getString("summer", "Summer");
            autumn = section.getString("autumn", "Autumn");
            winter = section.getString("winter", "Winter");
            noSeason = section.getString("no-season", "Season Disabled");
        }
    }

    @Override
    public void unload() {

    }

    @Override
    public String getSeasonTranslation(Season season) {
        if (season == null) return noSeason;
        return switch (season) {
            case SPRING -> spring;
            case SUMMER -> summer;
            case AUTUMN -> autumn;
            case WINTER -> winter;
        };
    }

    @Override
    public void reload() {
        load();
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    protected String getReload() {
        return reload;
    }
}

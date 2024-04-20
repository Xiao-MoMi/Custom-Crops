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

package net.momirealms.customcrops.compatibility;

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.integration.LevelInterface;
import net.momirealms.customcrops.api.integration.SeasonInterface;
import net.momirealms.customcrops.api.manager.IntegrationManager;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.compatibility.item.MMOItemsItemImpl;
import net.momirealms.customcrops.compatibility.item.MythicMobsItemImpl;
import net.momirealms.customcrops.compatibility.item.NeigeItemsItemImpl;
import net.momirealms.customcrops.compatibility.item.ZaphkielItemImpl;
import net.momirealms.customcrops.compatibility.level.*;
import net.momirealms.customcrops.compatibility.season.AdvancedSeasonsImpl;
import net.momirealms.customcrops.compatibility.season.InBuiltSeason;
import net.momirealms.customcrops.compatibility.season.RealisticSeasonsImpl;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class IntegrationManagerImpl implements IntegrationManager {

    private final CustomCropsPlugin plugin;
    private final HashMap<String, LevelInterface> levelPluginMap;
    private SeasonInterface seasonInterface;

    public IntegrationManagerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
        this.levelPluginMap = new HashMap<>();
    }

    @Override
    public void init() {
        if (plugin.doesHookedPluginExist("MMOItems")) {
            plugin.getItemManager().registerItemLibrary(new MMOItemsItemImpl());
            hookMessage("MMOItems");
        }
        if (plugin.doesHookedPluginExist("Zaphkiel")) {
            plugin.getItemManager().registerItemLibrary(new ZaphkielItemImpl());
            hookMessage("Zaphkiel");
        }
        if (plugin.doesHookedPluginExist("NeigeItems")) {
            plugin.getItemManager().registerItemLibrary(new NeigeItemsItemImpl());
            hookMessage("NeigeItems");
        }
        if (plugin.doesHookedPluginExist("MythicMobs")) {
            plugin.getItemManager().registerItemLibrary(new MythicMobsItemImpl());
            hookMessage("MythicMobs");
        }
        if (plugin.doesHookedPluginExist("EcoJobs")) {
            registerLevelPlugin("EcoJobs", new EcoJobsImpl());
            hookMessage("EcoJobs");
        }
        if (plugin.doesHookedPluginExist("Jobs")) {
            registerLevelPlugin("JobsReborn", new JobsRebornImpl());
            hookMessage("JobsReborn");
        }
        if (plugin.doesHookedPluginExist("AureliumSkills")) {
            registerLevelPlugin("AureliumSkills", new AureliumSkillsImpl());
            hookMessage("AureliumSkills");
        }
        if (plugin.doesHookedPluginExist("EcoSkills")) {
            registerLevelPlugin("EcoSkills", new EcoSkillsImpl());
            hookMessage("EcoSkills");
        }
        if (plugin.doesHookedPluginExist("mcMMO")) {
            registerLevelPlugin("mcMMO", new McMMOImpl());
            hookMessage("mcMMO");
        }
        if (plugin.doesHookedPluginExist("MMOCore")) {
            registerLevelPlugin("MMOCore", new MMOCoreImpl());
            hookMessage("MMOCore");
        }
        if (plugin.doesHookedPluginExist("AuraSkills")) {
            registerLevelPlugin("AuraSkills", new AuraSkillsImpl());
            hookMessage("AuraSkills");
        }
        if (plugin.isHookedPluginEnabled("RealisticSeasons")) {
            this.seasonInterface = new RealisticSeasonsImpl();
            hookMessage("RealisticSeasons");
        } else if (plugin.isHookedPluginEnabled("AdvancedSeasons")) {
            this.seasonInterface = new AdvancedSeasonsImpl();
            hookMessage("AdvancedSeasons");
        } else {
            this.seasonInterface = new InBuiltSeason(plugin.getWorldManager());
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public boolean registerLevelPlugin(String plugin, LevelInterface level) {
        if (levelPluginMap.containsKey(plugin)) return false;
        levelPluginMap.put(plugin, level);
        return true;
    }

    @Override
    public boolean unregisterLevelPlugin(String plugin) {
        return levelPluginMap.remove(plugin) != null;
    }

    private void hookMessage(String plugin) {
        LogUtils.info( plugin + " hooked!");
    }

    @Override
    @Nullable
    public LevelInterface getLevelPlugin(String plugin) {
        return levelPluginMap.get(plugin);
    }

    @Override
    public SeasonInterface getSeasonInterface() {
        return seasonInterface;
    }
}

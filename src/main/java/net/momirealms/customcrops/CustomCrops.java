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

package net.momirealms.customcrops;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.tr7zw.changeme.nbtapi.utils.VersionChecker;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.momirealms.customcrops.commands.PluginCommand;
import net.momirealms.customcrops.config.ConfigUtil;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.helper.LibraryLoader;
import net.momirealms.customcrops.helper.VersionHelper;
import net.momirealms.customcrops.integrations.papi.PlaceholderManager;
import net.momirealms.customcrops.integrations.protection.WorldGuardHook;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.integrations.quest.BattlePassCCQuest;
import net.momirealms.customcrops.integrations.quest.ClueScrollCCQuest;
import net.momirealms.customcrops.integrations.quest.NewBetonQuestCCQuest;
import net.momirealms.customcrops.integrations.quest.OldBetonQuestCCQuest;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.TimeZone;

public final class CustomCrops extends JavaPlugin {

    public static BukkitAudiences adventure;
    public static CustomCrops plugin;
    public static ProtocolManager protocolManager;

    private PlaceholderManager placeholderManager;
    private CropManager cropManager;
    private VersionHelper versionHelper;

//                              _ooOoo_
//                             o8888888o
//                             88" . "88
//                             (| -_- |)
//                             O\  =  /O
//                          ____/`---'\____
//                        .'  \\|     |//  `.
//                       /  \\|||  :  |||//  \
//                      /  _||||| -:- |||||_  \
//                      |   | \\\  -  /'| |   |
//                      | \_|  `\`---'//  |_/ |
//                      \  .-\__ `-. -'__/-.  /
//                    ___`. .'  /--.--\  `. .'___
//                 ."" '<  `.___\_<|>_/___.' _> \"".
//                | | :  `- \`. ;`. _/; .'/ /  .' ; |
//                \  \ `-.   \_\_`. _.'_/_/  -' _.' /
//  ================-.`___`-.__\ \___  /__.-'_.'_.-'================
//                              `=--=-'
//                    佛祖保佑    永无BUG    永不卡服

    @Override
    public void onLoad(){
        plugin = this;
        TimeZone timeZone = TimeZone.getDefault();
        String libRepo = timeZone.getID().startsWith("Asia") ? "https://maven.aliyun.com/repository/public/" : "https://repo.maven.apache.org/maven2/";
        LibraryLoader.load("dev.dejvokep","boosted-yaml","1.3",libRepo);
        LibraryLoader.load("commons-io","commons-io","2.11.0",libRepo);
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            WorldGuardHook.initialize();
        }
    }

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(plugin);
        protocolManager = ProtocolLibrary.getProtocolManager();
        this.versionHelper = new VersionHelper(this);
        AdventureUtil.consoleMessage("[CustomCrops] Running on <white>" + Bukkit.getVersion());
        VersionChecker.hideOk = true;
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            MainConfig.customPlugin = "itemsadder";
            AdventureUtil.consoleMessage("[CustomCrops] Custom Item Plugin Platform: <#BA55D3><u>ItemsAdder");
        }
        else if (Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
            MainConfig.customPlugin = "oraxen";
            AdventureUtil.consoleMessage("[CustomCrops] Custom Item Plugin Platform: <#6495ED><u>Oraxen");
        }
        else {
            AdventureUtil.consoleMessage("<red>[CustomCrops] You need either ItemsAdder or Oraxen as CustomCrops' dependency");
            Bukkit.getPluginManager().disablePlugin(CustomCrops.plugin);
            return;
        }

        ConfigUtil.reloadConfigs();
        this.registerQuests();

        PluginCommand pluginCommand = new PluginCommand();
        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setExecutor(pluginCommand);
        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setTabCompleter(pluginCommand);

        this.cropManager = new CropManager();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderManager = new PlaceholderManager();
        }

        AdventureUtil.consoleMessage("[CustomCrops] Plugin Enabled!");

        if (MainConfig.metrics) {
            new Metrics(this, 16593);
        }
    }

    @Override
    public void onDisable() {
        if (adventure != null) {
            adventure.close();
        }
        if (this.placeholderManager != null) {
            this.placeholderManager.unload();
        }
        if (this.cropManager != null) {
            this.cropManager.unload();
        }
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public boolean hasPapi() {
        return placeholderManager != null;
    }

    public CropManager getCropManager() {
        return cropManager;
    }

    public VersionHelper getVersionHelper() {
        return versionHelper;
    }

    private void registerQuests() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled("ClueScrolls")) {
            ClueScrollCCQuest quest = new ClueScrollCCQuest();
            Bukkit.getPluginManager().registerEvents(quest, plugin);
        }
        if (pluginManager.isPluginEnabled("BetonQuest")) {
            if (Bukkit.getPluginManager().getPlugin("BetonQuest").getDescription().getVersion().startsWith("2")) NewBetonQuestCCQuest.register();
            else OldBetonQuestCCQuest.register();
        }
        if (pluginManager.isPluginEnabled("BattlePass")) {
            BattlePassCCQuest.register();
        }
    }
}

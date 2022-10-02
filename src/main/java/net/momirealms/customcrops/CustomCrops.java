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

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.momirealms.customcrops.commands.PluginCommand;
import net.momirealms.customcrops.config.ConfigUtil;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.helper.LibraryLoader;
import net.momirealms.customcrops.integrations.papi.PlaceholderManager;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CustomCrops extends JavaPlugin {

    public static BukkitAudiences adventure;
    public static CustomCrops plugin;

    private PlaceholderManager placeholderManager;
    private CropManager cropManager;
    private PluginCommand pluginCommand;

    @Override
    public void onLoad(){
        plugin = this;
        LibraryLoader.load("dev.dejvokep","boosted-yaml","1.3","https://repo.maven.apache.org/maven2/");
        LibraryLoader.load("commons-io","commons-io","2.11.0","https://repo.maven.apache.org/maven2/");
    }

    @Override
    public void onEnable() {

        adventure = BukkitAudiences.create(plugin);
        AdventureUtil.consoleMessage("[CustomCrops] Running on <white>" + Bukkit.getVersion());

        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            MainConfig.customPlugin = "itemsadder";
            MainConfig.OraxenHook = false;
            AdventureUtil.consoleMessage("[CustomCrops] Custom Item Plugin Platform: <#BA55D3><u>ItemsAdder");
        }
        else if (Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
            MainConfig.customPlugin = "oraxen";
            MainConfig.OraxenHook = true;
            AdventureUtil.consoleMessage("[CustomCrops] Custom Item Plugin Platform: <#6495ED><u>Oraxen");
        }
        else {
            AdventureUtil.consoleMessage("<red>[CustomCrops] You need either ItemsAdder or Oraxen as CustomCrops' dependency");
            Bukkit.getPluginManager().disablePlugin(CustomCrops.plugin);
            return;
        }

        ConfigUtil.reloadConfigs();

        this.pluginCommand = new PluginCommand();
        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setExecutor(pluginCommand);
        Objects.requireNonNull(Bukkit.getPluginCommand("customcrops")).setTabCompleter(pluginCommand);

        this.cropManager = new CropManager();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderManager = new PlaceholderManager();
        }

        AdventureUtil.consoleMessage("[CustomCrops] Plugin Enabled!");
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
}

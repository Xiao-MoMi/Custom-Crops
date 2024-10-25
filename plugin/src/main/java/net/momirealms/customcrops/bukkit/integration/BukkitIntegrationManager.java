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

package net.momirealms.customcrops.bukkit.integration;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.integration.*;
import net.momirealms.customcrops.bukkit.integration.entity.ItemsAdderEntityProvider;
import net.momirealms.customcrops.bukkit.integration.entity.MythicEntityProvider;
import net.momirealms.customcrops.bukkit.integration.entity.VanillaEntityProvider;
import net.momirealms.customcrops.bukkit.integration.item.*;
import net.momirealms.customcrops.bukkit.integration.level.*;
import net.momirealms.customcrops.bukkit.integration.papi.CustomCropsPapi;
import net.momirealms.customcrops.bukkit.integration.quest.BattlePassQuest;
import net.momirealms.customcrops.bukkit.integration.quest.BetonQuestQuest;
import net.momirealms.customcrops.bukkit.integration.quest.ClueScrollsQuest;
import net.momirealms.customcrops.bukkit.integration.region.WorldGuardRegion;
import net.momirealms.customcrops.bukkit.integration.season.AdvancedSeasonsProvider;
import net.momirealms.customcrops.bukkit.integration.season.RealisticSeasonsProvider;
import net.momirealms.customcrops.bukkit.item.BukkitItemManager;
import net.momirealms.customcrops.bukkit.world.BukkitWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class BukkitIntegrationManager implements IntegrationManager {

    private final BukkitCustomCropsPlugin plugin;
    private final HashMap<String, LevelerProvider> levelerProviders = new HashMap<>();
    private final HashMap<String, EntityProvider> entityProviders = new HashMap<>();

    public BukkitIntegrationManager(BukkitCustomCropsPlugin plugin) {
        this.plugin = plugin;
        try {
            this.load();
        } catch (Exception e) {
            plugin.getPluginLogger().warn("Failed to load integrations", e);
        }
    }

    @Override
    public void disable() {
        this.levelerProviders.clear();
        this.entityProviders.clear();
    }

    @Override
    public void load() {
        registerEntityProvider(new VanillaEntityProvider());
        if (isHooked("MMOItems")) {
            registerItemProvider(new MMOItemsItemProvider());
        }
        if (isHooked("Zaphkiel")) {
            registerItemProvider(new ZaphkielItemProvider());
        }
        if (isHooked("ExecutableItems")) {
            registerItemProvider(new ExecutableItemProvider());
        }
        if (isHooked("NeigeItems")) {
            registerItemProvider(new NeigeItemsItemProvider());
        }
        if (isHooked("ItemsAdder")) {
            registerEntityProvider(new ItemsAdderEntityProvider());
        }
        if (isHooked("CustomFishing", "2.2", "2.3", "2.4")) {
            registerItemProvider(new CustomFishingItemProvider());
        }
        if (isHooked("MythicMobs", "5")) {
            registerItemProvider(new MythicMobsItemProvider());
            registerEntityProvider(new MythicEntityProvider());
        }
        if (isHooked("EcoJobs")) {
            registerLevelerProvider(new EcoJobsLevelerProvider());
        }
        if (isHooked("EcoSkills")) {
            registerLevelerProvider(new EcoSkillsLevelerProvider());
        }
        if (isHooked("Jobs")) {
            registerLevelerProvider(new JobsRebornLevelerProvider());
        }
        if (isHooked("MMOCore")) {
            registerLevelerProvider(new MMOCoreLevelerProvider());
        }
        if (isHooked("mcMMO")) {
            registerLevelerProvider(new McMMOLevelerProvider());
        }
        if (isHooked("AureliumSkills")) {
            registerLevelerProvider(new AureliumSkillsProvider());
        }
        if (isHooked("AuraSkills")) {
            registerLevelerProvider(new AuraSkillsLevelerProvider());
        }
        if (isHooked("RealisticSeasons")) {
            registerSeasonProvider(new RealisticSeasonsProvider());
        } else if (isHooked("AdvancedSeasons", "1.4", "1.5", "1.6")) {
            registerSeasonProvider(new AdvancedSeasonsProvider());
        }
        if (isHooked("Vault")) {
            VaultHook.init();
        }
        if (isHooked("PlaceholderAPI")) {
            new CustomCropsPapi(plugin).load();
        }
        if (isHooked("BattlePass")){
            BattlePassQuest battlePassQuest = new BattlePassQuest();
            battlePassQuest.register();
        }
        if (isHooked("ClueScrolls")) {
            ClueScrollsQuest clueScrollsQuest = new ClueScrollsQuest();
            clueScrollsQuest.register();
        }
        if (isHooked("BetonQuest", "2")) {
            BetonQuestQuest.register();
        }
        if (isHooked("WorldGuard", "7")) {
            WorldGuardRegion.register();
        }
    }

    private boolean doesPluginExists(String hooked) {
        return Bukkit.getPluginManager().getPlugin(hooked) != null;
    }

    @SuppressWarnings("deprecation")
    private boolean doesPluginExists(String hooked, String... versionPrefix) {
        Plugin p = Bukkit.getPluginManager().getPlugin(hooked);
        if (p != null) {
            String ver = p.getDescription().getVersion();
            for (String prefix : versionPrefix) {
                if (ver.startsWith(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isHooked(String hooked) {
        if (Bukkit.getPluginManager().getPlugin(hooked) != null) {
            plugin.getPluginLogger().info(hooked + " hooked!");
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private boolean isHooked(String hooked, String... versionPrefix) {
        Plugin p = Bukkit.getPluginManager().getPlugin(hooked);
        if (p != null) {
            String ver = p.getDescription().getVersion();
            for (String prefix : versionPrefix) {
                if (ver.startsWith(prefix)) {
                    plugin.getPluginLogger().info(hooked + " hooked!");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean registerLevelerProvider(@NotNull LevelerProvider leveler) {
        if (levelerProviders.containsKey(leveler.identifier())) return false;
        levelerProviders.put(leveler.identifier(), leveler);
        return true;
    }

    @Override
    public boolean unregisterLevelerProvider(@NotNull String id) {
        return levelerProviders.remove(id) != null;
    }

    @Override
    @Nullable
    public LevelerProvider getLevelerProvider(String id) {
        return levelerProviders.get(id);
    }

    @Override
    public void registerSeasonProvider(@NotNull SeasonProvider season) {
        ((BukkitWorldManager) plugin.getWorldManager()).seasonProvider(season);
    }

    @Override
    public boolean registerItemProvider(@NotNull ItemProvider item) {
        return ((BukkitItemManager) plugin.getItemManager()).registerItemProvider(item);
    }

    @Override
    public boolean unregisterItemProvider(@NotNull String id) {
        return ((BukkitItemManager) plugin.getItemManager()).unregisterItemProvider(id);
    }

    @Override
    public boolean registerEntityProvider(@NotNull EntityProvider entity) {
        if (entityProviders.containsKey(entity.identifier())) return false;
        entityProviders.put(entity.identifier(), entity);
        return true;
    }

    @Override
    public boolean unregisterEntityProvider(@NotNull String id) {
        return entityProviders.remove(id) != null;
    }

    @Nullable
    @Override
    public EntityProvider getEntityProvider(String id) {
        return entityProviders.get(id);
    }
}

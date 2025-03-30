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

package net.momirealms.customcrops.bukkit.world;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.ConfigManager;
import net.momirealms.customcrops.api.core.TickMode;
import net.momirealms.customcrops.api.core.world.*;
import net.momirealms.customcrops.api.core.world.adaptor.WorldAdaptor;
import net.momirealms.customcrops.api.integration.SeasonProvider;
import net.momirealms.customcrops.bukkit.config.BukkitConfigManager;
import net.momirealms.customcrops.bukkit.integration.adaptor.BukkitWorldAdaptor;
import net.momirealms.customcrops.bukkit.integration.adaptor.asp_r1.SlimeWorldAdaptorR1;
import net.momirealms.customcrops.common.helper.VersionHelper;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitWorldManager implements WorldManager, Listener {
    private final BukkitCustomCropsPlugin plugin;
    private final TreeSet<WorldAdaptor<?>> adaptors = new TreeSet<>();
    private final ConcurrentHashMap<String, CustomCropsWorld<?>> worlds = new ConcurrentHashMap<>();
    private final HashMap<String, WorldSetting> worldSettings = new HashMap<>();
    private WorldSetting defaultWorldSetting;
    private MatchRule matchRule;
    private HashSet<String> worldList;
    private SeasonProvider seasonProvider;

    public BukkitWorldManager(BukkitCustomCropsPlugin plugin) {
        this.plugin = plugin;
        // asp uses adventure nbt since 1.21.4
        if (!VersionHelper.isVersionNewerThan1_21_4()) {
            try {
                Class.forName("com.infernalsuite.aswm.api.SlimePlugin");
                SlimeWorldAdaptorR1 adaptor = new SlimeWorldAdaptorR1(1);
                adaptors.add(adaptor);
                Bukkit.getPluginManager().registerEvents(adaptor, plugin.getBootstrap());
                plugin.getPluginLogger().info("SlimeWorldManager hooked!");
            } catch (ClassNotFoundException ignored) {
            }
            if (Bukkit.getPluginManager().isPluginEnabled("SlimeWorldPlugin")) {
                SlimeWorldAdaptorR1 adaptor = new SlimeWorldAdaptorR1(2);
                adaptors.add(adaptor);
                Bukkit.getPluginManager().registerEvents(adaptor, plugin.getBootstrap());
                plugin.getPluginLogger().info("AdvancedSlimePaper hooked!");
            }
        }
        this.adaptors.add(new BukkitWorldAdaptor());
        this.seasonProvider = new SeasonProvider() {
            @NotNull
            @Override
            public Season getSeason(@NotNull World world) {
                return BukkitWorldManager.this.getWorld(world).map(w -> {
                    if (!w.setting().enableSeason()) {
                        return Season.DISABLE;
                    }
                    return w.extraData().getSeason();
                }).orElse(Season.DISABLE);
            }
            @Override
            public String identifier() {
                return "CustomCrops";
            }
        };
    }

    public void seasonProvider(SeasonProvider seasonProvider) {
        this.seasonProvider = seasonProvider;
    }

    @Override
    public SeasonProvider seasonProvider() {
        return seasonProvider;
    }

    @Override
    public Season getSeason(World world) {
        if (ConfigManager.syncSeasons()) {
            World reference = Bukkit.getWorld(ConfigManager.referenceWorld());
            if (reference != null) {
                return seasonProvider.getSeason(reference);
            } else {
                return Season.DISABLE;
            }
        } else {
            return seasonProvider.getSeason(world);
        }
    }

    @Override
    public int getDate(World world) {
        if (ConfigManager.syncSeasons()) {
            World reference = Bukkit.getWorld(ConfigManager.referenceWorld());
            if (reference != null) {
                return getWorld(reference).map(w -> w.extraData().getDate()).orElse(-1);
            } else {
                return -1;
            }
        } else {
            return getWorld(world).map(w -> w.extraData().getDate()).orElse(-1);
        }
    }

    @Override
    public void load() {
        this.loadConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin.getBootstrap());
        // load and unload worlds
        for (World world : Bukkit.getWorlds()) {
            if (isMechanicEnabled(world)) {
                loadWorld(world);
            } else {
                unloadWorld(world, false);
            }
        }
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
        this.worldSettings.clear();
    }

    @Override
    public void disable() {
        plugin.debug(() -> "Saving Worlds");
        this.unload();
        for (World world : Bukkit.getWorlds()) {
            plugin.debug(() -> "Unloading " + world.getName());
            unloadWorld(world, true);
            plugin.debug(() -> "Unloaded " + world.getName());
        }
        plugin.debug(() -> "Unload adaptors");
        for (WorldAdaptor<?> adaptor : this.adaptors) {
            if (adaptor instanceof Listener listener) {
                HandlerList.unregisterAll(listener);
            }
        }
        plugin.debug(() -> "Unloaded Worlds");
    }

    private void loadConfig() {
        YamlDocument config = BukkitConfigManager.getMainConfig();

        Section section = config.getSection("worlds");
        if (section == null) {
            plugin.getPluginLogger().warn("worlds section should not be null");
            return;
        }

        this.matchRule = MatchRule.valueOf(section.getString("mode", "blacklist").toUpperCase(Locale.ENGLISH));
        this.worldList = new HashSet<>(section.getStringList("list"));

        Section settingSection = section.getSection("settings");
        if (settingSection == null) {
            plugin.getPluginLogger().warn("worlds.settings section should not be null");
            return;
        }

        Section defaultSection = settingSection.getSection("_DEFAULT_");
        if (defaultSection == null) {
            plugin.getPluginLogger().warn("worlds.settings._DEFAULT_ section should not be null");
            return;
        }

        this.defaultWorldSetting = sectionToWorldSetting(defaultSection);

        Section worldSection = settingSection.getSection("_WORLDS_");
        if (worldSection != null) {
            for (Map.Entry<String, Object> entry : worldSection.getStringRouteMappedValues(false).entrySet()) {
                if (entry.getValue() instanceof Section inner) {
                    this.worldSettings.put(entry.getKey(), sectionToWorldSetting(inner));
                }
            }
        }
    }

    @Override
    public CustomCropsWorld<?> loadWorld(CustomCropsWorld<?> world) {
        Optional<CustomCropsWorld<?>> optionalWorld = getWorld(world.worldName());
        if (optionalWorld.isPresent()) {
            CustomCropsWorld<?> customCropsWorld = optionalWorld.get();
            customCropsWorld.setting(Optional.ofNullable(worldSettings.get(world.worldName())).orElse(defaultWorldSetting));
            return customCropsWorld;
        }
        world.setting(Optional.ofNullable(worldSettings.get(world.worldName())).orElse(defaultWorldSetting));
        world.setTicking(true);
        this.worlds.put(world.worldName(), world);
        for (Chunk chunk : world.bukkitWorld().getLoadedChunks()) {
            ChunkPos pos = ChunkPos.fromBukkitChunk(chunk);
            loadLoadedChunk(world, pos);
            notifyOfflineUpdates(world, pos);
        }
        return world;
    }

    @Override
    public CustomCropsWorld<?> loadWorld(World world) {
        Optional<CustomCropsWorld<?>> optionalWorld = getWorld(world);
        if (optionalWorld.isPresent()) {
            CustomCropsWorld<?> customCropsWorld = optionalWorld.get();
            customCropsWorld.setting(Optional.ofNullable(worldSettings.get(world.getName())).orElse(defaultWorldSetting));
            return customCropsWorld;
        }
        CustomCropsWorld<?> adaptedWorld = adapt(world);
        adaptedWorld.setting(Optional.ofNullable(worldSettings.get(world.getName())).orElse(defaultWorldSetting));
        adaptedWorld.setTicking(true);
        this.worlds.put(world.getName(), adaptedWorld);
        for (Chunk chunk : world.getLoadedChunks()) {
            ChunkPos pos = ChunkPos.fromBukkitChunk(chunk);
            loadLoadedChunk(adaptedWorld, pos);
            notifyOfflineUpdates(adaptedWorld, pos);
        }
        return adaptedWorld;
    }

    // Before using the method, make sure that the bukkit chunk is loaded
    public void loadLoadedChunk(CustomCropsWorld<?> world, ChunkPos pos) {
        if (world.isChunkLoaded(pos)) return;
        Optional<CustomCropsChunk> customChunk = world.getChunk(pos);
        // don't load bukkit chunk again since it has been loaded
        customChunk.ifPresent(customCropsChunk -> customCropsChunk.load(false));
    }

    public void notifyOfflineUpdates(CustomCropsWorld<?> world, ChunkPos pos) {
        world.getLoadedChunk(pos).ifPresent(CustomCropsChunk::notifyOfflineTask);
    }

    @Override
    public boolean unloadWorld(World world, boolean disabling) {
        CustomCropsWorld<?> removedWorld = worlds.remove(world.getName());
        if (removedWorld == null) {
            return false;
        }
        removedWorld.setTicking(false);
        plugin.debug(() -> "Unloading -> Saving");
        removedWorld.save(false, disabling);
        plugin.debug(() -> "Saving -> Shutdown");
        removedWorld.scheduler().shutdownScheduler();
        removedWorld.scheduler().shutdownExecutor();
        plugin.debug(() -> "Finished Shutdown");
        return true;
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        final World world = event.getWorld();
        getWorld(world).ifPresent(world1 -> world1.save(ConfigManager.asyncWorldSaving(), false));
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        if (!isMechanicEnabled(world)) return;
        loadWorld(world);
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onWorldUnload(WorldUnloadEvent event) {
        World world = event.getWorld();
        if (!isMechanicEnabled(world)) return;
        unloadWorld(world, false);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        final Chunk chunk = event.getChunk();
        final World world = event.getWorld();
        this.getWorld(world)
                .flatMap(customWorld -> customWorld.getLoadedChunk(ChunkPos.fromBukkitChunk(chunk)))
                .ifPresent(customChunk -> customChunk.unload(true));
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        final Chunk chunk = event.getChunk();
        final World world = event.getWorld();
        this.getWorld(world).ifPresent(customWorld -> {
            ChunkPos pos = ChunkPos.fromBukkitChunk(chunk);
            loadLoadedChunk(customWorld, pos);
            if (chunk.isEntitiesLoaded() && customWorld.setting().offlineTick()) {
                notifyOfflineUpdates(customWorld, pos);
            }
        });
    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        final Chunk chunk = event.getChunk();
        final World world = event.getWorld();
        this.getWorld(world).ifPresent(customWorld -> {
            if (customWorld.setting().offlineTick()) {
                notifyOfflineUpdates(customWorld, ChunkPos.fromBukkitChunk(chunk));
            }
        });
    }

    @Override
    public boolean isMechanicEnabled(World world) {
        if (world == null) return false;
        if (matchRule == MatchRule.WHITELIST) {
            return worldList.contains(world.getName());
        } else if (matchRule == MatchRule.BLACKLIST) {
            return !worldList.contains(world.getName());
        } else {
            for (String regex : worldList) {
                if (world.getName().matches(regex)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public Optional<CustomCropsWorld<?>> getWorld(World world) {
        return getWorld(world.getName());
    }

    @Override
    public Optional<CustomCropsWorld<?>> getWorld(String world) {
        return Optional.ofNullable(worlds.get(world));
    }

    @Override
    public boolean isWorldLoaded(World world) {
        return worlds.containsKey(world.getName());
    }

    @Override
    public TreeSet<WorldAdaptor<?>> adaptors() {
        return adaptors;
    }

    @Override
    public CustomCropsWorld<?> adapt(World world) {
        return adapt(world.getName());
    }

    @Override
    public CustomCropsWorld<?> adapt(String name) {
        for (WorldAdaptor<?> adaptor : adaptors) {
            Object world = adaptor.getWorld(name);
            if (world != null) {
                return adaptor.adapt(world);
            }
        }
        throw new RuntimeException("Unable to adapt world " + name);
    }

    public enum MatchRule {

        BLACKLIST,
        WHITELIST,
        REGEX
    }

    private static WorldSetting sectionToWorldSetting(Section section) {
        return WorldSetting.of(
                section.getBoolean("enable", true),
                section.getInt("min-tick-unit", 300),
                getRandomTickModeByString(section.getString("crop.mode")),
                section.getInt("crop.tick-interval", 1),
                getRandomTickModeByString(section.getString("pot.mode")),
                section.getInt("pot.tick-interval", 2),
                getRandomTickModeByString(section.getString("sprinkler.mode")),
                section.getInt("sprinkler.tick-interval", 2),
                section.getBoolean("offline-tick.enable", false),
                section.getInt("offline-tick.max-offline-seconds", 1200),
                section.getInt("offline-tick.max-loading-time", 100),
                section.getBoolean("season.enable", false),
                section.getBoolean("season.auto-alternation", false),
                section.getInt("season.duration", 28),
                section.getInt("crop.max-per-chunk", 128),
                section.getInt("pot.max-per-chunk", -1),
                section.getInt("sprinkler.max-per-chunk", 32),
                section.getInt("random-tick-speed", 0)
        );
    }

    private static int getRandomTickModeByString(String str) {
        if (str == null) {
            return 1;
        }
        return TickMode.valueOf(str.toUpperCase(Locale.ENGLISH)).mode();
    }
}

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

package net.momirealms.customcrops.mechanic.world;

import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.manager.WorldManager;
import net.momirealms.customcrops.api.mechanic.item.*;
import net.momirealms.customcrops.api.mechanic.misc.MatchRule;
import net.momirealms.customcrops.api.mechanic.world.AbstractWorldAdaptor;
import net.momirealms.customcrops.api.mechanic.world.ChunkPos;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.*;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.mechanic.world.adaptor.BukkitWorldAdaptor;
import net.momirealms.customcrops.mechanic.world.adaptor.SlimeWorldAdaptor;
import net.momirealms.customcrops.mechanic.world.block.*;
import net.momirealms.customcrops.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorldManagerImpl implements WorldManager, Listener {

    private final CustomCropsPlugin plugin;
    private final ConcurrentHashMap<String, CWorld> loadedWorlds;
    private final HashMap<String, WorldSetting> worldSettingMap;
    private WorldSetting defaultWorldSetting;
    private MatchRule matchRule;
    private HashSet<String> worldList;
    private AbstractWorldAdaptor worldAdaptor;
    private String absoluteWorldFolder;

    public WorldManagerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
        this.loadedWorlds = new ConcurrentHashMap<>();
        this.worldSettingMap = new HashMap<>();
        try {
            Class.forName("com.infernalsuite.aswm.api.world.SlimeWorld");
            this.worldAdaptor = new SlimeWorldAdaptor(this);
        } catch (ClassNotFoundException ignore) {
            this.worldAdaptor = new BukkitWorldAdaptor(this);
        }
    }

    @Override
    public void load() {
        this.registerListener();
        this.loadConfig();
        if (this.worldAdaptor instanceof BukkitWorldAdaptor adaptor) {
            adaptor.setWorldFolder(absoluteWorldFolder);
        }
        for (World world : Bukkit.getWorlds()) {
            if (isMechanicEnabled(world)) {
                loadWorld(world);
            } else {
                unloadWorld(world);
            }
        }
    }

    @Override
    public void unload() {
        this.unregisterListener();
        this.worldSettingMap.clear();
    }

    @Override
    public void disable() {
        this.unload();
        for (World world : Bukkit.getWorlds()) {
            unloadWorld(world);
        }
        if (this.loadedWorlds.size() != 0) {
            LogUtils.severe("Detected that some worlds are not properly unloaded. " +
                    "You can safely ignore this if you are editing \"worlds.list\" and restarting to apply it");
            for (String world : this.loadedWorlds.keySet()) {
                LogUtils.severe(" - " + world);
            }
            for (CustomCropsWorld world : this.loadedWorlds.values()) {
                worldAdaptor.unload(world);
            }
            this.loadedWorlds.clear();
        }
    }

    private void loadConfig() {
        YamlConfiguration config = ConfigUtils.getConfig("config.yml");

        ConfigurationSection section = config.getConfigurationSection("worlds");
        if (section == null) {
            LogUtils.severe("worlds section should not be null");
            return;
        }

        this.absoluteWorldFolder = section.getString("absolute-world-folder-path","");
        this.matchRule = MatchRule.valueOf(section.getString("mode", "blacklist").toUpperCase(Locale.ENGLISH));
        this.worldList = new HashSet<>(section.getStringList("list"));

        // limitation
        ConfigurationSection settingSection = section.getConfigurationSection("settings");
        if (settingSection == null) {
            LogUtils.severe("worlds.settings section should not be null");
            return;
        }

        ConfigurationSection defaultSection = settingSection.getConfigurationSection("_DEFAULT_");
        if (defaultSection == null) {
            LogUtils.severe("worlds.settings._DEFAULT_ section should not be null");
            return;
        }

        this.defaultWorldSetting = ConfigUtils.getWorldSettingFromSection(defaultSection);
        ConfigurationSection worldSection = settingSection.getConfigurationSection("_WORLDS_");
        if (worldSection != null) {
            for (Map.Entry<String, Object> entry : worldSection.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection inner) {
                    this.worldSettingMap.put(entry.getKey(), ConfigUtils.getWorldSettingFromSection(inner));
                }
            }
        }
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        if (worldAdaptor != null)
            Bukkit.getPluginManager().registerEvents(worldAdaptor, plugin);
    }

    private void unregisterListener() {
        HandlerList.unregisterAll(this);
        if (worldAdaptor != null)
            HandlerList.unregisterAll(worldAdaptor);
    }

    @NotNull
    @Override
    public CustomCropsWorld loadWorld(@NotNull World world) {
        String worldName = world.getName();
        if (loadedWorlds.containsKey(worldName)) {
            CWorld cWorld = loadedWorlds.get(worldName);
            cWorld.setWorldSetting(getInitWorldSetting(world));
            return cWorld;
        }
        CWorld cWorld = new CWorld(this, world);
        worldAdaptor.init(cWorld);
        loadedWorlds.put(worldName, cWorld);
        cWorld.setWorldSetting(getInitWorldSetting(world));
        cWorld.startTick();
        for (Chunk chunk : world.getLoadedChunks()) {
            handleChunkLoad(chunk);
        }
        return cWorld;
    }

    @Override
    public boolean unloadWorld(@NotNull World world) {
        CustomCropsWorld customCropsWorld = loadedWorlds.remove(world.getName());
        if (customCropsWorld != null) {
            customCropsWorld.cancelTick();
            worldAdaptor.unload(customCropsWorld);
            return true;
        }
        return false;
    }

    private WorldSetting getInitWorldSetting(World world) {
        if (worldSettingMap.containsKey(world.getName()))
            return worldSettingMap.get(world.getName()).clone();
        return defaultWorldSetting.clone();
    }

    @Override
    public boolean isMechanicEnabled(@NotNull World world) {
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

    @NotNull
    @Override
    public Collection<String> getWorldNames() {
        return loadedWorlds.keySet();
    }

    @NotNull
    @Override
    public Collection<World> getBukkitWorlds() {
        return loadedWorlds.keySet().stream().map(Bukkit::getWorld).toList();
    }

    @NotNull
    @Override
    public Collection<? extends CustomCropsWorld> getCustomCropsWorlds() {
        return loadedWorlds.values();
    }

    @NotNull
    @Override
    public Optional<CustomCropsWorld> getCustomCropsWorld(@NotNull String name) {
        return Optional.ofNullable(loadedWorlds.get(name));
    }

    @NotNull
    @Override
    public Optional<CustomCropsWorld> getCustomCropsWorld(@NotNull World world) {
        return Optional.ofNullable(loadedWorlds.get(world.getName()));
    }

    @NotNull
    @Override
    public Optional<WorldSprinkler> getSprinklerAt(@NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) return Optional.empty();
        return cWorld.getSprinklerAt(location);
    }

    @NotNull
    @Override
    public Optional<WorldPot> getPotAt(@NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) return Optional.empty();
        return cWorld.getPotAt(location);
    }

    @NotNull
    @Override
    public Optional<WorldCrop> getCropAt(@NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) return Optional.empty();
        return cWorld.getCropAt(location);
    }

    @NotNull
    @Override
    public Optional<WorldGlass> getGlassAt(@NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) return Optional.empty();
        return cWorld.getGlassAt(location);
    }

    @NotNull
    @Override
    public Optional<WorldScarecrow> getScarecrowAt(@NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) return Optional.empty();
        return cWorld.getScarecrowAt(location);
    }

    @Override
    public Optional<CustomCropsBlock> getBlockAt(SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) return Optional.empty();
        return cWorld.getBlockAt(location);
    }

    @Override
    public WorldCrop createCropData(SimpleLocation location, Crop crop, int point) {
        return new MemoryCrop(location, crop.getKey(), point);
    }

    @Override
    public WorldSprinkler createSprinklerData(SimpleLocation location, Sprinkler sprinkler, int water) {
        return new MemorySprinkler(location, sprinkler.getKey(), water);
    }

    @Override
    public WorldPot createPotData(SimpleLocation location, Pot pot, int water, Fertilizer fertilizer, int fertilizerTimes) {
        return new MemoryPot(location, pot.getKey(), water, fertilizer == null ? "" : fertilizer.getKey(), fertilizerTimes);
    }

    @Override
    public WorldGlass createGreenhouseGlassData(SimpleLocation location) {
        return new MemoryGlass(location);
    }

    @Override
    public WorldScarecrow createScarecrowData(SimpleLocation location) {
        return new MemoryScarecrow(location);
    }

    @Override
    public void addWaterToSprinkler(@NotNull Sprinkler sprinkler, @NotNull SimpleLocation location, int amount) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Adding water to sprinkler in unloaded world " + location);
            return;
        }
        cWorld.addWaterToSprinkler(sprinkler, amount, location);
    }

    @Override
    public void addFertilizerToPot(@NotNull Pot pot, @NotNull Fertilizer fertilizer, @NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Adding fertilizer to pot in unloaded world " + location);
            return;
        }
        cWorld.addFertilizerToPot(pot, fertilizer, location);
    }

    @Override
    public void addWaterToPot(@NotNull Pot pot, int amount, @NotNull SimpleLocation location) {
        if (amount <= 0) return;
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Adding water to pot in unloaded world " + location);
            return;
        }
        cWorld.addWaterToPot(pot, amount, location);
    }

    @Override
    public void addPotAt(@NotNull WorldPot pot, @NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Adding pot in unloaded world " + location);
            return;
        }
        cWorld.addPotAt(pot, location);
    }

    @Override
    public void addSprinklerAt(@NotNull WorldSprinkler sprinkler, @NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Adding sprinkler in unloaded world " + location);
            return;
        }
        cWorld.addSprinklerAt(sprinkler, location);
    }

    @Override
    public void addCropAt(@NotNull WorldCrop crop, @NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Adding crop in unloaded world " + location);
            return;
        }
        cWorld.addCropAt(crop, location);
    }

    @Override
    public void addPointToCrop(@NotNull Crop crop, int points, @NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Adding point to crop in unloaded world " + location);
            return;
        }
        cWorld.addPointToCrop(crop, points, location);
    }

    @Override
    public void addGlassAt(@NotNull WorldGlass glass, @NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Adding glass in unloaded world " + location);
            return;
        }
        cWorld.addGlassAt(glass, location);
    }

    @Override
    public void addScarecrowAt(@NotNull WorldScarecrow scarecrow, @NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Adding scarecrow in unloaded world " + location);
            return;
        }
        cWorld.addScarecrowAt(scarecrow, location);
    }

    @Override
    public WorldSprinkler removeSprinklerAt(@NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Removing sprinkler from unloaded world " + location);
            return null;
        }
        return cWorld.removeSprinklerAt(location);
    }

    @Override
    public WorldPot removePotAt(@NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Removing pot from unloaded world " + location);
            return null;
        }
        return cWorld.removePotAt(location);
    }

    @Override
    public WorldCrop removeCropAt(@NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Removing crop from unloaded world " + location);
            return null;
        }
        return cWorld.removeCropAt(location);
    }

    @Override
    public WorldGlass removeGlassAt(@NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Removing glass from unloaded world " + location);
            return null;
        }
        return cWorld.removeGlassAt(location);
    }

    @Override
    public WorldScarecrow removeScarecrowAt(@NotNull SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Removing scarecrow from unloaded world " + location);
            return null;
        }
        return cWorld.removeScarecrowAt(location);
    }

    @Override
    public CustomCropsBlock removeAnythingAt(SimpleLocation location) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Removing anything from unloaded world " + location);
            return null;
        }
        return cWorld.removeAnythingAt(location);
    }

    @Override
    public boolean isReachLimit(SimpleLocation location, ItemType itemType) {
        CWorld cWorld = loadedWorlds.get(location.getWorldName());
        if (cWorld == null) {
            LogUtils.warn("Unsupported operation: Querying amount in an unloaded world " + location);
            return true;
        }
        switch (itemType) {
            case CROP -> {
                return cWorld.isCropReachLimit(location);
            }
            case SPRINKLER -> {
                return cWorld.isSprinklerReachLimit(location);
            }
            case POT -> {
                return cWorld.isPotReachLimit(location);
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Still need further investigations into why chunk load event is called twice
     */
    @Override
    public void handleChunkLoad(Chunk bukkitChunk) {
        Optional<CustomCropsWorld> optional = getCustomCropsWorld(bukkitChunk.getWorld());
        if (optional.isEmpty())
            return;

        CustomCropsWorld customCropsWorld = optional.get();
        ChunkPos chunkPos = ChunkPos.getByBukkitChunk(bukkitChunk);

        if (customCropsWorld.isChunkLoaded(chunkPos)) {
            return;
        }

        // load chunks
        this.worldAdaptor.loadChunkData(customCropsWorld, chunkPos);

        // offline grow part
        if (!customCropsWorld.getWorldSetting().isOfflineTick()) return;

        // If chunk data not exists, return
        Optional<CustomCropsChunk> optionalChunk = customCropsWorld.getLoadedChunkAt(chunkPos);
        if (optionalChunk.isEmpty()) {
            return;
        }

        CustomCropsChunk chunk = optionalChunk.get();

        // load the entities if not loaded
        bukkitChunk.getEntities();
        if (bukkitChunk.isEntitiesLoaded()) {
            chunk.notifyOfflineUpdates();
        }
    }

    @Override
    public void handleChunkUnload(Chunk bukkitChunk) {
        Optional<CustomCropsWorld> optional = getCustomCropsWorld(bukkitChunk.getWorld());
        if (optional.isEmpty())
            return;

        CustomCropsWorld customCropsWorld = optional.get();
        ChunkPos chunkPos = ChunkPos.getByBukkitChunk(bukkitChunk);

        this.worldAdaptor.unloadChunkData(customCropsWorld, chunkPos);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        handleChunkLoad(event.getChunk());
    }

    @EventHandler
    public void onChunkUnLoad(ChunkUnloadEvent event) {
        handleChunkUnload(event.getChunk());
    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event) {

        Chunk bukkitChunk = event.getChunk();
        Optional<CustomCropsWorld> optional = getCustomCropsWorld(bukkitChunk.getWorld());
        if (optional.isEmpty())
            return;

        CustomCropsWorld customCropsWorld = optional.get();
        // offline grow part
        if (!customCropsWorld.getWorldSetting().isOfflineTick()) return;

        ChunkPos chunkPos = ChunkPos.getByBukkitChunk(bukkitChunk);

        Optional<CustomCropsChunk> optionalChunk = customCropsWorld.getLoadedChunkAt(chunkPos);
        if (optionalChunk.isEmpty()) {
            return;
        }

        if (!optionalChunk.get().isOfflineTaskNotified()) {
            optionalChunk.get().notifyOfflineUpdates();
        }
    }

    @Override
    public void saveChunkToCachedRegion(CustomCropsChunk chunk) {
        this.worldAdaptor.saveChunkToCachedRegion(chunk);
    }

    @Override
    public void saveRegionToFile(CustomCropsRegion region) {
        this.worldAdaptor.saveRegion(region);
    }

    @Override
    public AbstractWorldAdaptor getWorldAdaptor() {
        return worldAdaptor;
    }

    @Override
    public void saveInfoData(CustomCropsWorld customCropsWorld) {
        this.worldAdaptor.saveInfoData(customCropsWorld);
    }
}

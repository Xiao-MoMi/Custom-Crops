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

package net.momirealms.customcrops.mechanic.item;

import com.google.common.base.Preconditions;
import net.momirealms.antigrieflib.AntiGriefLib;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.event.*;
import net.momirealms.customcrops.api.integration.ItemLibrary;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.manager.ItemManager;
import net.momirealms.customcrops.api.manager.RequirementManager;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.condition.Conditions;
import net.momirealms.customcrops.api.mechanic.item.*;
import net.momirealms.customcrops.api.mechanic.item.water.PassiveFillMethod;
import net.momirealms.customcrops.api.mechanic.item.water.PositiveFillMethod;
import net.momirealms.customcrops.api.mechanic.misc.image.WaterBar;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;
import net.momirealms.customcrops.api.mechanic.world.level.WorldPot;
import net.momirealms.customcrops.api.mechanic.world.level.WorldSprinkler;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.mechanic.item.custom.AbstractCustomListener;
import net.momirealms.customcrops.mechanic.item.custom.itemsadder.ItemsAdderListener;
import net.momirealms.customcrops.mechanic.item.custom.itemsadder.ItemsAdderProvider;
import net.momirealms.customcrops.mechanic.item.custom.oraxen.OraxenListener;
import net.momirealms.customcrops.mechanic.item.custom.oraxen.OraxenProvider;
import net.momirealms.customcrops.mechanic.item.function.CFunction;
import net.momirealms.customcrops.mechanic.item.function.FunctionResult;
import net.momirealms.customcrops.mechanic.item.function.FunctionTrigger;
import net.momirealms.customcrops.mechanic.item.function.wrapper.*;
import net.momirealms.customcrops.mechanic.item.impl.CropConfig;
import net.momirealms.customcrops.mechanic.item.impl.PotConfig;
import net.momirealms.customcrops.mechanic.item.impl.SprinklerConfig;
import net.momirealms.customcrops.mechanic.item.impl.WateringCanConfig;
import net.momirealms.customcrops.mechanic.item.impl.fertilizer.*;
import net.momirealms.customcrops.mechanic.world.block.MemoryCrop;
import net.momirealms.customcrops.mechanic.world.block.MemoryPot;
import net.momirealms.customcrops.mechanic.world.block.MemorySprinkler;
import net.momirealms.customcrops.utils.ConfigUtils;
import net.momirealms.customcrops.utils.EventUtils;
import net.momirealms.customcrops.utils.ItemUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class ItemManagerImpl implements ItemManager {

    private final CustomCropsPlugin plugin;
    private AbstractCustomListener listener;
    private CustomProvider customProvider;
    private final HashMap<String, ItemLibrary> itemLibraryMap;
    private ItemLibrary[] itemDetectionArray;
    private final HashMap<String, HashMap<FunctionTrigger, TreeSet<CFunction>>> itemID2FunctionMap;
    private final HashMap<String, WateringCan> id2WateringCanMap;
    private final HashMap<String, WateringCan> item2WateringCanMap;
    private final HashMap<String, Sprinkler> id2SprinklerMap;
    private final HashMap<String, Sprinkler> twoDItem2SprinklerMap;
    private final HashMap<String, Sprinkler> threeDItem2SprinklerMap;
    private final HashMap<String, Pot> id2PotMap;
    private final HashMap<String, Pot> item2PotMap;
    private final HashMap<String, Crop> stage2CropMap;
    private final HashMap<String, Crop> seed2CropMap;
    private final HashMap<String, Crop> id2CropMap;
    private final HashMap<String, Crop.Stage> stage2CropStageMap;
    private final HashMap<String, Fertilizer> id2FertilizerMap;
    private final HashMap<String, Fertilizer> item2FertilizerMap;
    private final AntiGriefLib antiGrief;

    public ItemManagerImpl(CustomCropsPlugin plugin, AntiGriefLib antiGriefLib) {
        this.plugin = plugin;
        this.antiGrief = antiGriefLib;
        this.itemLibraryMap = new HashMap<>();
        this.itemID2FunctionMap = new HashMap<>();
        this.id2WateringCanMap = new HashMap<>();
        this.item2WateringCanMap = new HashMap<>();
        this.id2SprinklerMap = new HashMap<>();
        this.twoDItem2SprinklerMap = new HashMap<>();
        this.threeDItem2SprinklerMap = new HashMap<>();
        this.id2PotMap = new HashMap<>();
        this.item2PotMap = new HashMap<>();
        this.stage2CropMap = new HashMap<>();
        this.seed2CropMap = new HashMap<>();
        this.id2CropMap = new HashMap<>();
        this.id2FertilizerMap = new HashMap<>();
        this.item2FertilizerMap = new HashMap<>();
        this.stage2CropStageMap = new HashMap<>();
        if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
            listener = new OraxenListener(this);
            customProvider = new OraxenProvider();
        } else if (Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
            listener = new ItemsAdderListener(this);
            customProvider = new ItemsAdderProvider();
        } else {
            LogUtils.severe("======================================================");
            LogUtils.severe(" Please install ItemsAdder or Oraxen as dependency.");
            LogUtils.severe(" ItemsAdder: https://www.spigotmc.org/resources/73355/");
            LogUtils.severe(" Oraxen: https://www.spigotmc.org/resources/72448/");
            LogUtils.severe("======================================================");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    @Override
    public void load() {
        this.loadItems();
        if (this.listener != null) {
            Bukkit.getPluginManager().registerEvents(this.listener, this.plugin);
        }
        this.resetItemDetectionOrder();
    }

    @Override
    public void unload() {
        if (this.listener != null) {
            HandlerList.unregisterAll(this.listener);
        }
        this.itemID2FunctionMap.clear();
        this.id2WateringCanMap.clear();
        this.item2WateringCanMap.clear();
        this.id2SprinklerMap.clear();
        this.twoDItem2SprinklerMap.clear();
        this.threeDItem2SprinklerMap.clear();
        this.id2PotMap.clear();
        this.item2PotMap.clear();
        this.stage2CropMap.clear();
        this.seed2CropMap.clear();
        this.id2CropMap.clear();
        this.id2FertilizerMap.clear();
        this.item2FertilizerMap.clear();
    }

    @Override
    public void disable() {
        unload();
        this.itemLibraryMap.clear();
    }

    @Override
    public boolean registerItemLibrary(@NotNull ItemLibrary itemLibrary) {
        if (this.itemLibraryMap.containsKey(itemLibrary.identification()))
            return false;
        this.itemLibraryMap.put(itemLibrary.identification(), itemLibrary);
        resetItemDetectionOrder();
        return true;
    }

    @Override
    public boolean unregisterItemLibrary(String identification) {
        boolean success = this.itemLibraryMap.remove(identification) != null;
        if (success) {
            resetItemDetectionOrder();
        }
        return success;
    }

    private void resetItemDetectionOrder() {
        ArrayList<ItemLibrary> itemLibraries = new ArrayList<>();
        for (String identification : ConfigManager.itemDetectionOrder()) {
            ItemLibrary library = itemLibraryMap.get(identification);
            if (library != null) {
                itemLibraries.add(library);
            }
        }
        this.itemDetectionArray = itemLibraries.toArray(new ItemLibrary[0]);
    }

    @Override
    public String getItemID(ItemStack itemStack) {
        if (itemStack == null)
            return "AIR";
        String id;
        id = customProvider.getItemID(itemStack);
        if (id != null) return id;
        else {
            for (ItemLibrary library : itemDetectionArray) {
                id = library.getItemID(itemStack);
                if (id != null)
                    return id;
            }
        }
        return itemStack.getType().name();
    }

    @Override
    public ItemStack getItemStack(Player player, String id) {
        ItemStack itemStack;
        itemStack = customProvider.getItemStack(id);
        if (itemStack != null)
            return itemStack;
        if (id.contains(":")) {
            String[] split = id.split(":", 2);
            return itemLibraryMap.get(split[0]).buildItem(player, split[1]);
        } else {
            try {
                return new ItemStack(Material.valueOf(id.toUpperCase(Locale.ENGLISH)));
            } catch (IllegalArgumentException e) {
                return new ItemStack(Material.AIR);
            }
        }
    }

    public boolean registerFertilizer(@NotNull Fertilizer fertilizer) {
        if (this.id2FertilizerMap.containsKey(fertilizer.getKey())) {
            return false;
        }
        this.id2FertilizerMap.put(fertilizer.getKey(), fertilizer);
        if (this.item2FertilizerMap.put(fertilizer.getItemID(), fertilizer) != null) {
            LogUtils.warn("Item " + fertilizer.getItemID() + " has more than one fertilizer config.");
            return false;
        }
        return true;
    }

    public boolean registerWateringCan(@NotNull WateringCan wateringCan) {
        if (this.id2WateringCanMap.containsKey(wateringCan.getKey())) {
            return false;
        }
        this.id2WateringCanMap.put(wateringCan.getKey(), wateringCan);
        if (this.item2WateringCanMap.put(wateringCan.getItemID(), wateringCan) != null) {
            LogUtils.warn("Item " + wateringCan.getItemID() + " has more than one watering-can config.");
            return false;
        }
        return true;
    }

    public boolean registerPot(@NotNull Pot pot) {
        if (this.id2PotMap.containsKey(pot.getKey())) {
            return false;
        }
        this.id2PotMap.put(pot.getKey(), pot);
        for (String block : pot.getPotBlocks()) {
            if (this.item2PotMap.put(block, pot) != null) {
                LogUtils.warn("Block " + block + " has more than one pot config.");
                return false;
            }
        }
        return true;
    }

    public boolean registerCrop(@NotNull Crop crop) {
        if (this.id2CropMap.containsKey(crop.getKey())) {
            return false;
        }
        this.id2CropMap.put(crop.getKey(), crop);
        if (this.seed2CropMap.put(crop.getSeedItemID(), crop) != null) {
            LogUtils.warn("Item " + crop.getSeedItemID() + " has more than one crop config.");
            return false;
        }
        for (Crop.Stage stage : crop.getStages()) {
            if (stage.getStageID() != null) {
                if (this.stage2CropMap.put(stage.getStageID(), crop) != null) {
                    LogUtils.warn("Item " + stage.getStageID() + " has more than one crop config.");
                    return false;
                }
                if (this.stage2CropStageMap.put(stage.getStageID(), stage) != null) {
                    LogUtils.warn("Item " + stage.getStageID() + " has more than one crop config.");
                    return false;
                }
            }
        }
        return true;
    }

    public boolean registerSprinkler(@NotNull Sprinkler sprinkler) {
        if (this.id2SprinklerMap.containsKey(sprinkler.getKey())) {
            return false;
        }
        this.id2SprinklerMap.put(sprinkler.getKey(), sprinkler);
        if (sprinkler.get2DItemID() != null) {
            if (this.twoDItem2SprinklerMap.put(sprinkler.get2DItemID(), sprinkler) != null) {
                LogUtils.warn("Item " + sprinkler.get2DItemID() + " has more than one sprinkler config.");
                return false;
            }
        }
        if (this.threeDItem2SprinklerMap.put(sprinkler.get3DItemID(), sprinkler) != null) {
            LogUtils.warn("Item " + sprinkler.get3DItemID() + " has more than one sprinkler config.");
            return false;
        }
        return true;
    }

    @Override
    public WateringCan getWateringCanByID(@NotNull String id) {
        return id2WateringCanMap.get(id);
    }

    @Override
    public WateringCan getWateringCanByItemID(@NotNull String id) {
        return item2WateringCanMap.get(id);
    }

    @Override
    public WateringCan getWateringCanByItemStack(@NotNull ItemStack itemStack) {
        if (itemStack.getType() == Material.AIR)
            return null;
        return getWateringCanByItemID(getItemID(itemStack));
    }

    @Override
    public Sprinkler getSprinklerByID(@NotNull String id) {
        return id2SprinklerMap.get(id);
    }

    @Override
    public Sprinkler getSprinklerBy3DItemID(@NotNull String id) {
        return threeDItem2SprinklerMap.get(id);
    }

    @Override
    public Sprinkler getSprinklerBy2DItemID(@NotNull String id) {
        return twoDItem2SprinklerMap.get(id);
    }

    @Override
    public Sprinkler getSprinklerByEntity(@NotNull Entity entity) {
        return Optional.ofNullable(customProvider.getEntityID(entity)).map(threeDItem2SprinklerMap::get).orElse(null);
    }

    @Override
    public Sprinkler getSprinklerBy2DItemStack(@NotNull ItemStack itemStack) {
        return getSprinklerBy2DItemID(getItemID(itemStack));
    }

    @Override
    public Sprinkler getSprinklerBy3DItemStack(@NotNull ItemStack itemStack) {
        return getSprinklerBy3DItemID(getItemID(itemStack));
    }

    @Override
    public Pot getPotByID(@NotNull String id) {
        return id2PotMap.get(id);
    }

    @Override
    public Pot getPotByBlockID(@NotNull String id) {
        return item2PotMap.get(id);
    }

    @Override
    public Pot getPotByBlock(@NotNull Block block) {
        return getPotByBlockID(customProvider.getBlockID(block));
    }

    @Override
    public Pot getPotByItemStack(@NotNull ItemStack itemStack) {
        return getPotByBlockID(getItemID(itemStack));
    }

    @Override
    public Sprinkler getSprinklerByItemStack(@NotNull ItemStack itemStack) {
        if (itemStack.getType() == Material.AIR)
            return null;
        return getSprinklerBy2DItemID(getItemID(itemStack));
    }

    @Override
    public Fertilizer getFertilizerByID(@NotNull String id) {
        return id2FertilizerMap.get(id);
    }

    @Override
    public Fertilizer getFertilizerByItemID(@NotNull String id) {
        return item2FertilizerMap.get(id);
    }

    @Override
    public Fertilizer getFertilizerByItemStack(@NotNull ItemStack itemStack) {
        if (itemStack.getType() == Material.AIR)
            return null;
        return item2FertilizerMap.get(getItemID(itemStack));
    }

    @Override
    public Crop getCropByID(String id) {
        return id2CropMap.get(id);
    }

    @Override
    public Crop getCropBySeedID(String id) {
        return seed2CropMap.get(id);
    }

    @Override
    public Crop getCropBySeedItemStack(ItemStack itemStack) {
        if (itemStack.getType() == Material.AIR)
            return null;
        return getCropByID(getItemID(itemStack));
    }

    @Override
    public Crop getCropByStageID(String id) {
        return stage2CropMap.get(id);
    }

    @Override
    public Crop getCropByEntity(Entity entity) {
        return Optional.ofNullable(customProvider.getEntityID(entity)).map(stage2CropMap::get).orElse(null);
    }

    @Override
    public Crop getCropByBlock(Block block) {
        return stage2CropMap.get(customProvider.getBlockID(block));
    }

    @SuppressWarnings("DuplicatedCode")
    private void loadItems() {
        for (String item : List.of("watering-cans", "pots", "crops", "sprinklers", "fertilizers")) {
            File folder = new File(plugin.getDataFolder(), "contents" + File.separator + item);
            if (!folder.exists()) {
                plugin.saveResource("contents" + File.separator + item + File.separator + "default.yml", true);
                ConfigUtils.addDefaultNamespace(customProvider instanceof ItemsAdderProvider, new File(plugin.getDataFolder(), "contents" + File.separator + item + File.separator + "default.yml"));
            }
            List<File> files = ConfigUtils.getFilesRecursively(new File(plugin.getDataFolder(), "contents" + File.separator + item));
            for (File file : files) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
                    if (entry.getValue() instanceof ConfigurationSection section) {
                        switch (item) {
                            case "watering-cans" -> loadWateringCan(entry.getKey(), section);
                            case "pots" -> loadPot(entry.getKey(), section);
                            case "crops" -> loadCrop(entry.getKey(), section);
                            case "sprinklers" -> loadSprinkler(entry.getKey(), section);
                            case "fertilizers" -> loadFertilizer(entry.getKey(), section);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void loadWateringCan(String key, ConfigurationSection section) {
        String itemID = section.getString("item");
        int capacity = section.getInt("capacity");
        int width = section.getInt("effective-range.width");
        int length = section.getInt("effective-range.length");
        HashSet<String> potWhiteList = new HashSet<>(section.getStringList("pot-whitelist"));
        HashSet<String> sprinklerWhiteList = new HashSet<>(section.getStringList("sprinkler-whitelist"));
        boolean hasDynamicLore = section.getBoolean("dynamic-lore.enable", false);
        List<String> lore = section.getStringList("dynamic-lore.lore");

        WateringCanConfig wateringCan = new WateringCanConfig(
                key,
                itemID, section.getBoolean("infinite", false), width,
                length, capacity,
                hasDynamicLore, lore,
                potWhiteList, sprinklerWhiteList,
                ConfigUtils.getPositiveFillMethods(section.getConfigurationSection("fill-method")),
                ConfigUtils.getInt2IntMap(section.getConfigurationSection("appearance")),
                ConfigUtils.getRequirements(section.getConfigurationSection("requirements")),
                ConfigUtils.getActionMap(section.getConfigurationSection("events")),
                section.contains("water-bar") ? WaterBar.of(
                        section.getString("water-bar.left", ""),
                        section.getString("water-bar.empty", ""),
                        section.getString("water-bar.full", ""),
                        section.getString("water-bar.right", "")
                ) : null
        );

        if (!this.registerWateringCan(wateringCan)) {
            LogUtils.warn("Failed to register new watering can: " + key + " due to duplicated entries.");
            return;
        }

        this.registerItemFunction(itemID, FunctionTrigger.INTERACT_AT,
                /*
                 * Handle clicking pot with a watering can
                 */
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractBlockWrapper blockWrapper)) {
                        return FunctionResult.PASS;
                    }
                    // click the upper face
                    if (blockWrapper.getClickedFace() != BlockFace.UP) {
                        return FunctionResult.PASS;
                    }
                    // is a pot
                    Pot pot = getPotByBlock(blockWrapper.getClickedBlock());
                    if (pot == null) {
                        return FunctionResult.PASS;
                    }
                    final Player player = blockWrapper.getPlayer();;
                    final ItemStack itemStack = blockWrapper.getItemInHand();
                    final Location clicked = blockWrapper.getClickedBlock().getLocation();
                    State state = new State(player, itemStack, clicked);
                    // check watering-can requirements
                    if (!RequirementManager.isRequirementMet(state, wateringCan.getRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // check whitelist
                    if (!wateringCan.getPotWhitelist().contains(pot.getKey())) {
                        wateringCan.trigger(ActionTrigger.WRONG_POT, state);
                        return FunctionResult.RETURN;
                    }
                    // check amount of water
                    int waterInCan = wateringCan.getCurrentWater(itemStack);
                    if (waterInCan > 0 || wateringCan.isInfinite()) {
                        wateringCan.updateItem(itemStack, waterInCan - 1);
                        wateringCan.trigger(ActionTrigger.CONSUME_WATER, state);
                        Collection<Location> pots = getPotInRange(clicked, wateringCan.getWidth(), wateringCan.getLength(), player.getLocation().getYaw(), pot.getKey());
                        for (Location location : pots) {
                            plugin.getWorldManager().addWaterToPot(pot, SimpleLocation.getByBukkitLocation(location), 1);
                            pot.trigger(ActionTrigger.ADD_WATER, new State(player, itemStack, location));
                        }
                    } else {
                        wateringCan.trigger(ActionTrigger.NO_WATER, state);
                    }
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.HIGH),
                /*
                 * Handle clicking crop with a watering can
                 */
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractBlockWrapper blockWrapper)) {
                        return FunctionResult.PASS;
                    }
                    // is a crop
                    Crop crop = getCropByBlock(blockWrapper.getClickedBlock());
                    if (crop == null) {
                        return FunctionResult.PASS;
                    }
                    // get pot block
                    Block potBlock = blockWrapper.getClickedBlock().getRelative(BlockFace.DOWN);
                    Pot pot = getPotByBlock(potBlock);
                    if (pot == null) {
                        LogUtils.warn("Unexpected issue: Detetced that crops are not planted on a pot: " + blockWrapper.getClickedBlock().getLocation());
                        return FunctionResult.RETURN;
                    }

                    final Player player = blockWrapper.getPlayer();;
                    final ItemStack itemStack = blockWrapper.getItemInHand();
                    final Location clicked = blockWrapper.getClickedBlock().getLocation();
                    State state = new State(player, itemStack, clicked);
                    // check watering-can use requirements
                    if (!RequirementManager.isRequirementMet(state, wateringCan.getRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // check crop interact requirements
                    if (RequirementManager.isRequirementMet(state, crop.getInteractRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // check pot use requirements
                    if (!RequirementManager.isRequirementMet(state, pot.getUseRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // check watering-can whitelist
                    if (!wateringCan.getPotWhitelist().contains(pot.getKey())) {
                        wateringCan.trigger(ActionTrigger.WRONG_POT, state);
                        return FunctionResult.RETURN;
                    }
                    // check amount of water
                    int waterInCan = wateringCan.getCurrentWater(itemStack);
                    if (waterInCan > 0 || wateringCan.isInfinite()) {
                        wateringCan.updateItem(itemStack, waterInCan - 1);
                        wateringCan.trigger(ActionTrigger.CONSUME_WATER, state);
                        Collection<Location> pots = getPotInRange(potBlock.getLocation(), wateringCan.getWidth(), wateringCan.getLength(), player.getLocation().getYaw(), pot.getKey());
                        for (Location location : pots) {
                            plugin.getWorldManager().addWaterToPot(pot, SimpleLocation.getByBukkitLocation(location), 1);
                            pot.trigger(ActionTrigger.ADD_WATER, new State(player, itemStack, location));
                        }
                    } else {
                        wateringCan.trigger(ActionTrigger.NO_WATER, state);
                    }
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.HIGH),
                /*
                 * Handle clicking furniture with a watering can
                 * This furniture may be a sprinkler, or it may be a custom piece of furniture such as a well
                 */
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractFurnitureWrapper furnitureWrapper)) {
                        return FunctionResult.PASS;
                    }
                    // check watering-can requirements
                    State state = new State(furnitureWrapper.getPlayer(), furnitureWrapper.getItemInHand(), furnitureWrapper.getLocation());
                    if (!RequirementManager.isRequirementMet(state, wateringCan.getRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // get water in can
                    int waterInCan = wateringCan.getCurrentWater(furnitureWrapper.getItemInHand());
                    String clickedFurnitureID = furnitureWrapper.getID();
                    Sprinkler sprinkler = getSprinklerBy3DItemID(clickedFurnitureID);
                    // is a sprinkler
                    if (sprinkler != null) {
                        // check sprinkler requirements
                        if (!RequirementManager.isRequirementMet(state, sprinkler.getUseRequirements())) {
                            return FunctionResult.RETURN;
                        }
                        // check whitelist
                        if (!wateringCan.getSprinklerWhitelist().contains(sprinkler.getKey())) {
                            wateringCan.trigger(ActionTrigger.WRONG_SPRINKLER, state);
                            return FunctionResult.RETURN;
                        }
                        // check amount of water
                        if (waterInCan > 0 || wateringCan.isInfinite()) {
                            // get sprinkler data
                            SimpleLocation location = SimpleLocation.getByBukkitLocation(furnitureWrapper.getLocation());
                            Optional<WorldSprinkler> worldSprinkler = plugin.getWorldManager().getSprinklerAt(location);
                            if (worldSprinkler.isEmpty()) {
                                plugin.debug("Player " + furnitureWrapper.getPlayer().getName() + " tried to interact a sprinkler which not exists in memory. Fixing the data...");
                                wateringCan.updateItem(furnitureWrapper.getItemInHand(), waterInCan - 1);
                                wateringCan.trigger(ActionTrigger.CONSUME_WATER, state);
                                plugin.getWorldManager().addWaterToSprinkler(sprinkler, location, 1);
                            } else if (sprinkler.getStorage() > worldSprinkler.get().getWater()) {
                                wateringCan.updateItem(furnitureWrapper.getItemInHand(), waterInCan - 1);
                                wateringCan.trigger(ActionTrigger.CONSUME_WATER, state);
                                plugin.getWorldManager().addWaterToSprinkler(sprinkler, location, 1);
                            }
                        } else {
                            wateringCan.trigger(ActionTrigger.NO_WATER, state);
                        }
                        return FunctionResult.RETURN;
                    }

                    // get water from furniture and add it to watering-can
                    if (!wateringCan.isInfinite()) {
                        PositiveFillMethod[] methods = wateringCan.getPositiveFillMethods();
                        for (PositiveFillMethod method : methods) {
                            if (method.getId().equals(clickedFurnitureID)) {
                                if (method.canFill(state)) {
                                    if (waterInCan < wateringCan.getStorage()) {
                                        waterInCan += method.getAmount();
                                        waterInCan = Math.min(waterInCan, wateringCan.getStorage());
                                        wateringCan.updateItem(furnitureWrapper.getItemInHand(), waterInCan);
                                        wateringCan.trigger(ActionTrigger.ADD_WATER, state);
                                        method.trigger(state);
                                    } else {
                                        wateringCan.trigger(ActionTrigger.FULL, state);
                                    }
                                }
                                return FunctionResult.RETURN;
                            }
                        }
                    }

                    return FunctionResult.PASS;
                }, CFunction.FunctionPriority.NORMAL),
                /*
                 * Handle clicking a block or air with watering can
                 * The priority of handling this is the lowest,
                 * because the priority of watering is much higher than that of adding water,
                 * and there should be no further judgment on adding water when watering occurs.
                 */
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractWrapper interactWrapper)) {
                        return FunctionResult.PASS;
                    }
                    if (wateringCan.isInfinite()) {
                        return FunctionResult.PASS;
                    }
                    // get the clicked block
                    Block targetBlock = interactWrapper.getPlayer().getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
                    if (targetBlock == null)
                        return FunctionResult.PASS;
                    // check watering-can requirements
                    State state = new State(interactWrapper.getPlayer(), interactWrapper.getItemInHand(), targetBlock.getLocation());
                    if (!RequirementManager.isRequirementMet(state, wateringCan.getRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // get the exact block id
                    String blockID = customProvider.getBlockID(targetBlock);
                    if (targetBlock.getBlockData() instanceof Waterlogged waterlogged && waterlogged.isWaterlogged()) {
                        blockID = "WATER";
                    }
                    int water = wateringCan.getCurrentWater(interactWrapper.getItemInHand());
                    PositiveFillMethod[] methods = wateringCan.getPositiveFillMethods();
                    for (PositiveFillMethod method : methods) {
                        if (method.getId().equals(blockID)) {
                            if (method.canFill(state)) {
                                if (water < wateringCan.getStorage()) {
                                    water += method.getAmount();
                                    water = Math.min(water, wateringCan.getStorage());
                                    wateringCan.updateItem(interactWrapper.getItemInHand(), water);
                                    wateringCan.trigger(ActionTrigger.ADD_WATER, state);
                                    method.trigger(state);
                                } else {
                                    wateringCan.trigger(ActionTrigger.FULL, state);
                                }
                            }
                            return FunctionResult.RETURN;
                        }
                    }
                    return FunctionResult.PASS;
                }, CFunction.FunctionPriority.LOW)
        );
    }

    @SuppressWarnings("DuplicatedCode")
    private void loadSprinkler(String key, ConfigurationSection section) {
        int storage = section.getInt("storage", 4);
        boolean infinite = section.getBoolean("infinite", false);
        int range = section.getInt("range",1);
        int water = section.getInt("water", 1);
        ItemCarrier itemCarrier = ItemCarrier.valueOf(section.getString("type", "ITEM_FRAME").toUpperCase(Locale.ENGLISH));

        SprinklerConfig sprinkler = new SprinklerConfig(
                key,
                itemCarrier,
                section.getString("2D-item"),
                Preconditions.checkNotNull(section.getString("3D-item"), "3D-item can't be null"),
                range,
                storage,
                water,
                infinite,
                section.contains("water-bar") ? WaterBar.of(
                        section.getString("water-bar.left", ""),
                        section.getString("water-bar.empty", ""),
                        section.getString("water-bar.full", ""),
                        section.getString("water-bar.right", "")
                ) : null,
                new HashSet<>(section.getStringList("pot-whitelist")),
                ConfigUtils.getPassiveFillMethods(section.getConfigurationSection("fill-method")),
                ConfigUtils.getActionMap(section.getConfigurationSection("events")),
                ConfigUtils.getRequirements(section.getConfigurationSection("requirements.place")),
                ConfigUtils.getRequirements(section.getConfigurationSection("requirements.break")),
                ConfigUtils.getRequirements(section.getConfigurationSection("requirements.use"))
        );

        if (!this.registerSprinkler(sprinkler)) {
            LogUtils.warn("Failed to register new sprinkler: " + key + " due to duplicated entries.");
            return;
        }

        if (sprinkler.get2DItemID() != null) {
            this.registerItemFunction(sprinkler.get2DItemID(), FunctionTrigger.INTERACT_AT,
                    /*
                     * 2D item -> 3D item
                     */
                    new CFunction(conditionWrapper -> {
                        if (!(conditionWrapper instanceof InteractBlockWrapper interactBlockWrapper)) {
                            return FunctionResult.PASS;
                        }
                        if (interactBlockWrapper.getClickedFace() != BlockFace.UP) {
                            return FunctionResult.PASS;
                        }
                        if (!interactBlockWrapper.getClickedBlock().getType().isSolid()) {
                            return FunctionResult.PASS;
                        }
                        ItemStack itemInHand = interactBlockWrapper.getItemInHand();
                        Location placed = interactBlockWrapper.getClickedBlock().getLocation().clone().add(0,1,0);
                        Player player = interactBlockWrapper.getPlayer();
                        // check if the place is empty
                        if (!customProvider.isAir(placed)) {
                            return FunctionResult.RETURN;
                        }
                        // check place requirements
                        State state = new State(player, itemInHand, placed);
                        if (!RequirementManager.isRequirementMet(state, sprinkler.getPlaceRequirements())) {
                            return FunctionResult.RETURN;
                        }
                        // check limitation
                        if (plugin.getWorldManager().isReachLimit(SimpleLocation.getByBukkitLocation(placed), ItemType.SPRINKLER)) {
                            sprinkler.trigger(ActionTrigger.REACH_LIMIT, state);
                            return FunctionResult.RETURN;
                        }
                        // fire event
                        SprinklerPlaceEvent placeEvent = new SprinklerPlaceEvent(player, itemInHand, placed, sprinkler);
                        if (EventUtils.fireAndCheckCancel(placeEvent)) {
                            return FunctionResult.RETURN;
                        }
                        // place the sprinkler
                        switch (sprinkler.getItemCarrier()) {
                            case ITEM_FRAME, ITEM_DISPLAY -> customProvider.placeFurniture(placed, sprinkler.get3DItemID());
                            case TRIPWIRE -> customProvider.placeBlock(placed, sprinkler.get3DItemID());
                            default -> {
                                LogUtils.warn("Unsupported type for sprinkler: " + sprinkler.getItemCarrier().name());
                                return FunctionResult.RETURN;
                            }
                        }
                        // reduce item
                        if (player.getGameMode() != GameMode.CREATIVE)
                            itemInHand.setAmount(itemInHand.getAmount() - 1);
                        sprinkler.trigger(ActionTrigger.PLACE, state);
                        plugin.getWorldManager().addSprinklerAt(new MemorySprinkler(sprinkler.getKey(), 0), SimpleLocation.getByBukkitLocation(placed));
                        return FunctionResult.PASS;
                    }, CFunction.FunctionPriority.NORMAL)
            );
        }

        this.registerItemFunction(sprinkler.get3DItemID(), FunctionTrigger.PLACE,
                /*
                 * This will only trigger if the sprinkler has only 3D items
                 */
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof PlaceFurnitureWrapper placeFurnitureWrapper)) {
                        return FunctionResult.PASS;
                    }
                    Location location = placeFurnitureWrapper.getLocation();
                    Player player = placeFurnitureWrapper.getPlayer();
                    // check place requirements
                    State state = new State(player, placeFurnitureWrapper.getItemInHand(), location);
                    if (!RequirementManager.isRequirementMet(state, sprinkler.getPlaceRequirements())) {
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;
                    }
                    // check limitation
                    if (plugin.getWorldManager().isReachLimit(SimpleLocation.getByBukkitLocation(location), ItemType.SPRINKLER)) {
                        sprinkler.trigger(ActionTrigger.REACH_LIMIT, state);
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;
                    }
                    // fire event
                    SprinklerPlaceEvent placeEvent = new SprinklerPlaceEvent(player, placeFurnitureWrapper.getItemInHand(), location, sprinkler);
                    if (EventUtils.fireAndCheckCancel(placeEvent)) {
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;
                    }
                    // add data
                    plugin.getWorldManager().addSprinklerAt(new MemorySprinkler(sprinkler.getKey(), 0), SimpleLocation.getByBukkitLocation(location));
                    sprinkler.trigger(ActionTrigger.PLACE, state);
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL)
        );

        this.registerItemFunction(sprinkler.get3DItemID(), FunctionTrigger.BE_INTERACTED,
                /*
                 * Interact the sprinkler
                 */
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractFurnitureWrapper interactFurnitureWrapper)) {
                        return FunctionResult.PASS;
                    }
                    ItemStack itemInHand = interactFurnitureWrapper.getItemInHand();
                    Player player = interactFurnitureWrapper.getPlayer();
                    Location location = interactFurnitureWrapper.getLocation();
                    // check use requirements
                    State state = new State(player, itemInHand, location);
                    if (!RequirementManager.isRequirementMet(state, sprinkler.getUseRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    Optional<WorldSprinkler> optionalSprinkler = plugin.getWorldManager().getSprinklerAt(SimpleLocation.getByBukkitLocation(location));
                    if (optionalSprinkler.isEmpty()) {
                        plugin.debug("Found a sprinkler without data interacted by " + player.getName() + " at " + location + ". " +
                                "You can safely ignore this if you updated the plugin to 3.4+ recently.");
                        plugin.getWorldManager().addSprinklerAt(new MemorySprinkler(sprinkler.getKey(), 0), SimpleLocation.getByBukkitLocation(location));
                        return FunctionResult.RETURN;
                    }
                    if (!optionalSprinkler.get().getKey().equals(sprinkler.getKey())) {
                        LogUtils.warn("Found a sprinkler having inconsistent data interacted by " + player.getName() + " at " + location + ".");
                        plugin.getWorldManager().addSprinklerAt(new MemorySprinkler(sprinkler.getKey(), 0), SimpleLocation.getByBukkitLocation(location));
                        return FunctionResult.RETURN;
                    }
                    // fire the event
                    SprinklerInteractEvent interactEvent = new SprinklerInteractEvent(player, itemInHand, location, optionalSprinkler.get());
                    if (EventUtils.fireAndCheckCancel(interactEvent)) {
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;
                    }
                    // add water to sprinkler
                    String itemID = getItemID(itemInHand);
                    int itemAmount = itemInHand.getAmount();
                    Optional<WorldSprinkler> worldSprinkler = plugin.getWorldManager().getSprinklerAt(SimpleLocation.getByBukkitLocation(location));
                    int waterInSprinkler = worldSprinkler.map(WorldSprinkler::getWater).orElse(0);
                    // if it's not infinite
                    if (!sprinkler.isInfinite()) {
                        for (PassiveFillMethod method : sprinkler.getPassiveFillMethods()) {
                            if (method.getUsed().equals(itemID) && itemAmount >= method.getUsedAmount()) {
                                if (method.canFill(state)) {
                                    if (waterInSprinkler > sprinkler.getStorage()) {
                                        if (player.getGameMode() != GameMode.CREATIVE) {
                                            itemInHand.setAmount(itemAmount - method.getUsedAmount());
                                            if (method.getReturned() != null) {
                                                ItemStack returned = getItemStack(player, method.getReturned());
                                                ItemUtils.giveItem(player, returned, method.getReturnedAmount());
                                            }
                                        }
                                        method.trigger(state);
                                        sprinkler.trigger(ActionTrigger.ADD_WATER, state);
                                        plugin.getWorldManager().addWaterToSprinkler(sprinkler, SimpleLocation.getByBukkitLocation(location), method.getAmount());
                                    } else {
                                        sprinkler.trigger(ActionTrigger.FULL, state);
                                    }
                                }
                                return FunctionResult.RETURN;
                            }
                        }
                    }
                    // trigger interact actions
                    sprinkler.trigger(ActionTrigger.INTERACT, state);
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL)
        );

        this.registerItemFunction(sprinkler.get3DItemID(), FunctionTrigger.BREAK,
                /*
                 * Handle breaking sprinklers
                 */
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof BreakFurnitureWrapper breakFurnitureWrapper)) {
                        return FunctionResult.PASS;
                    }
                    // check break requirements
                    Location location = breakFurnitureWrapper.getLocation();
                    State state = new State(breakFurnitureWrapper.getPlayer(), breakFurnitureWrapper.getItemInHand(), location);
                    if (!RequirementManager.isRequirementMet(state, sprinkler.getBreakRequirements())) {
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;
                    }
                    Optional<WorldSprinkler> optionalSprinkler = plugin.getWorldManager().getSprinklerAt(SimpleLocation.getByBukkitLocation(location));
                    if (optionalSprinkler.isEmpty()) {
                        plugin.debug("Found a sprinkler without data broken by " + state.getPlayer().getName() + " at " + location + ". " +
                                "You can safely ignore this if you updated the plugin to 3.4+ recently.");
                        return FunctionResult.RETURN;
                    }
                    if (!optionalSprinkler.get().getKey().equals(sprinkler.getKey())) {
                        LogUtils.warn("Found a sprinkler having inconsistent data broken by " + state.getPlayer().getName() + " at " + location + ".");
                        plugin.getWorldManager().removeSprinklerAt(SimpleLocation.getByBukkitLocation(location));
                        return FunctionResult.RETURN;
                    }
                    // fire event
                    SprinklerBreakEvent breakEvent = new SprinklerBreakEvent(breakFurnitureWrapper.getPlayer(), location, optionalSprinkler.get());
                    if (EventUtils.fireAndCheckCancel(breakEvent)) {
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;
                    }
                    // remove data
                    plugin.getWorldManager().removeSprinklerAt(SimpleLocation.getByBukkitLocation(location));
                    sprinkler.trigger(ActionTrigger.BREAK, state);
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL)
        );
    }


    @SuppressWarnings("DuplicatedCode")
    private void loadFertilizer(String key, ConfigurationSection section) {
        FertilizerType type = switch (Preconditions.checkNotNull(section.getString("type"), "Fertilizer type can't be null").toUpperCase(Locale.ENGLISH)) {
            case "QUALITY" -> FertilizerType.QUALITY;
            case "SOIL_RETAIN" -> FertilizerType.SOIL_RETAIN;
            case "SPEED_GROW" -> FertilizerType.SPEED_GROW;
            case "VARIATION" -> FertilizerType.VARIATION;
            case "YIELD_INCREASE" -> FertilizerType.YIELD_INCREASE;
            default -> null;
        };

        if (type == null) {
            LogUtils.warn("Fertilizer type: " + section.getString("type") + " is invalid.");
            return;
        }

        String icon = section.getString("icon", "");
        int times = section.getInt("times", 14);
        String itemID = section.getString("item");
        HashSet<String> potWhitelist = new HashSet<>(section.getStringList("pot-whitelist"));
        boolean beforePlant = section.getBoolean("before-plant", false);

        Fertilizer fertilizer;
        switch (type) {
            case QUALITY -> fertilizer = new QualityCropConfig(
                    key, itemID, times,
                    section.getDouble("chance", 1), type, potWhitelist,
                    beforePlant, icon,
                    ConfigUtils.getRequirements(section.getConfigurationSection("requirements")),
                    ConfigUtils.getQualityRatio(Preconditions.checkNotNull(section.getString("ratio"), "Quality ratio should not be null")),
                    ConfigUtils.getActionMap(section.getConfigurationSection("events"))
            );
            case VARIATION -> fertilizer = new VariationConfig(key, itemID, times,
                    section.getDouble("chance", 1), type, potWhitelist,
                    beforePlant, icon,
                    ConfigUtils.getRequirements(section.getConfigurationSection("requirements")),
                    ConfigUtils.getActionMap(section.getConfigurationSection("events"))
            );
            case SOIL_RETAIN -> fertilizer = new SoilRetainConfig(key, itemID, times,
                    section.getDouble("chance", 1), type, potWhitelist,
                    beforePlant, icon,
                    ConfigUtils.getRequirements(section.getConfigurationSection("requirements")),
                    ConfigUtils.getActionMap(section.getConfigurationSection("events"))
            );
            case YIELD_INCREASE -> fertilizer = new YieldIncreaseConfig(key, itemID, times,
                    type, potWhitelist,
                    beforePlant, icon,
                    ConfigUtils.getRequirements(section.getConfigurationSection("requirements")),
                    ConfigUtils.getIntChancePair(section.getConfigurationSection("chance")),
                    ConfigUtils.getActionMap(section.getConfigurationSection("events"))
            );
            case SPEED_GROW -> fertilizer = new SpeedGrowConfig(key, itemID, times,
                    type, potWhitelist,
                    beforePlant, icon,
                    ConfigUtils.getRequirements(section.getConfigurationSection("requirements")),
                    ConfigUtils.getIntChancePair(section.getConfigurationSection("chance")),
                    ConfigUtils.getActionMap(section.getConfigurationSection("events"))
            );
            default -> fertilizer = null;
        }

        if (!registerFertilizer(fertilizer)) {
            LogUtils.warn("Failed to register new fertilizer: " + key + " due to duplicated entries.");
            return;
        }

        this.registerItemFunction(fertilizer.getItemID(), FunctionTrigger.INTERACT_AT,
                /*
                 * Processing logic for players to use fertilizer
                 */
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractBlockWrapper interactBlockWrapper)) {
                        return FunctionResult.PASS;
                    }
                    // is a pot
                    Block clicked = interactBlockWrapper.getClickedBlock();
                    Pot pot = getPotByBlock(clicked);
                    if (pot == null) {
                        return FunctionResult.PASS;
                    }
                    ItemStack itemInHand = interactBlockWrapper.getItemInHand();
                    // check fertilizer requirements
                    State state = new State(interactBlockWrapper.getPlayer(), itemInHand, clicked.getLocation());
                    if (!RequirementManager.isRequirementMet(state, fertilizer.getRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // check pot use requirements
                    if (!RequirementManager.isRequirementMet(state, pot.getUseRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // check whitelist
                    if (!fertilizer.getPotWhitelist().contains(pot.getKey())) {
                        fertilizer.trigger(ActionTrigger.WRONG_POT, state);
                        return FunctionResult.RETURN;
                    }
                    // check before plant
                    if (fertilizer.isBeforePlant()) {
                        Optional<WorldCrop> worldCrop = plugin.getWorldManager().getCropAt(SimpleLocation.getByBukkitLocation(clicked.getLocation().clone().add(0,1,0)));
                        if (worldCrop.isPresent()) {
                            fertilizer.trigger(ActionTrigger.BEFORE_PLANT, state);
                            return FunctionResult.RETURN;
                        }
                    }
                    // add data
                    plugin.getWorldManager().addFertilizerToPot(pot, fertilizer, SimpleLocation.getByBukkitLocation(clicked.getLocation()));
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                    fertilizer.trigger(ActionTrigger.USE, state);
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL)
        );
    }

    @SuppressWarnings("DuplicatedCode")
    private void loadCrop(String key, ConfigurationSection section) {
        ItemCarrier itemCarrier = ItemCarrier.valueOf(section.getString("type"));
        if (itemCarrier != ItemCarrier.TRIPWIRE && itemCarrier != ItemCarrier.ITEM_DISPLAY && itemCarrier != ItemCarrier.ITEM_FRAME) {
            LogUtils.warn("Unsupported crop type: " + itemCarrier.name());
            return;
        }

        String seedItemID = section.getString("seed");
        boolean rotation = section.getBoolean("random-rotation", false);
        int maxPoints = section.getInt("max-points");

        ConfigurationSection pointSection = section.getConfigurationSection("points");
        if (pointSection == null) {
            LogUtils.warn(key + ".points section can't be null");
            return;
        }

        CropConfig crop = new CropConfig(
                key, seedItemID, itemCarrier,
                new HashSet<>(section.getStringList("pot-whitelist")), rotation, maxPoints,
                ConfigUtils.getBoneMeals(section.getConfigurationSection("custom-bone-meal")),
                new Conditions(ConfigUtils.getConditions(section.getConfigurationSection("grow-conditions"))),
                ConfigUtils.getDeathConditions(section.getConfigurationSection("death-conditions"), itemCarrier),
                ConfigUtils.getActionMap(section.getConfigurationSection("events")),
                ConfigUtils.getStageConfigs(pointSection),
                ConfigUtils.getRequirements(section.getConfigurationSection("requirements.plant")),
                ConfigUtils.getRequirements(section.getConfigurationSection("requirements.break")),
                ConfigUtils.getRequirements(section.getConfigurationSection("requirements.interact"))
        );

        if (!this.registerCrop(crop)) {
            LogUtils.warn("Failed to register new crop: " + key + " due to duplicated entries.");
            return;
        }

        this.registerItemFunction(crop.getSeedItemID(), FunctionTrigger.INTERACT_AT,
                /*
                 * Handle crop planting
                 */
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractBlockWrapper blockWrapper)) {
                        return FunctionResult.PASS;
                    }
                    // is a pot
                    Block clicked = blockWrapper.getClickedBlock();
                    Pot pot = getPotByBlock(clicked);
                    if (pot == null) {
                        return FunctionResult.PASS;
                    }
                    // click the upper face
                    if (blockWrapper.getClickedFace() != BlockFace.UP) {
                        return FunctionResult.PASS;
                    }
                    Player player = blockWrapper.getPlayer();
                    ItemStack itemInHand = blockWrapper.getItemInHand();
                    Location seedLocation = clicked.getLocation().clone().add(0,1,0);
                    State state = new State(player, itemInHand, seedLocation);
                    // check whitelist
                    if (!crop.getPotWhitelist().contains(pot.getKey())) {
                        crop.trigger(ActionTrigger.WRONG_POT, state);
                        return FunctionResult.RETURN;
                    }
                    // check plant requirements
                    if (!RequirementManager.isRequirementMet(state, crop.getPlantRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // check limitation
                    if (plugin.getWorldManager().isReachLimit(SimpleLocation.getByBukkitLocation(seedLocation), ItemType.CROP)) {
                        crop.trigger(ActionTrigger.REACH_LIMIT, state);
                        return FunctionResult.RETURN;
                    }
                    // fire event
                    CropPlantEvent plantEvent = new CropPlantEvent(player, itemInHand, seedLocation, crop, 0);
                    if (EventUtils.fireAndCheckCancel(plantEvent)) {
                        return FunctionResult.RETURN;
                    }
                    // place the sprinkler
                    switch (crop.getItemCarrier()) {
                        case ITEM_FRAME, ITEM_DISPLAY -> customProvider.placeFurniture(seedLocation, crop.getStageItemByPoint(plantEvent.getPoint()));
                        case TRIPWIRE -> customProvider.placeBlock(seedLocation, crop.getStageItemByPoint(plantEvent.getPoint()));
                        default -> {
                            LogUtils.warn("Unsupported type for crop: " + crop.getItemCarrier().name());
                            return FunctionResult.RETURN;
                        }
                    }
                    // reduce item
                    if (player.getGameMode() != GameMode.CREATIVE)
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    crop.trigger(ActionTrigger.PLANT, state);
                    plugin.getWorldManager().addCropAt(new MemoryCrop(crop.getKey(), 0), SimpleLocation.getByBukkitLocation(seedLocation));
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL)
        );

        for (Crop.Stage stage : crop.getStages()) {
            if (stage.getStageID() != null) {
                this.registerItemFunction(stage.getStageID(), FunctionTrigger.BE_INTERACTED,
                        /*
                         * Add water to pot if player is clicking a crop
                         * Trigger crop interaction
                         */
                        new CFunction(conditionWrapper -> {
                            if (!(conditionWrapper instanceof InteractWrapper interactWrapper)) {
                                return FunctionResult.PASS;
                            }
                            Location cropLocation = interactWrapper.getLocation().toBlockLocation();
                            Location potLocation = cropLocation.clone().subtract(0,1,0);
                            Block potBlock = potLocation.getBlock();
                            Pot pot = getPotByBlock(potBlock);
                            if (pot == null) {
                                LogUtils.warn("Unexpected issue: Detetced that crops are not planted on a pot: " + potBlock.getLocation());
                                // TODO remove the crop and its data
                                return FunctionResult.RETURN;
                            }
                            Player player = interactWrapper.getPlayer();
                            ItemStack itemInHand = interactWrapper.getItemInHand();
                            State potState = new State(player, itemInHand, potBlock.getLocation());
                            State cropState = new State(player, itemInHand, potBlock.getLocation());
                            // check crop interact requirements
                            if (RequirementManager.isRequirementMet(cropState, crop.getInteractRequirements())) {
                                return FunctionResult.RETURN;
                            }
                            Optional<WorldCrop> optionalCrop = plugin.getWorldManager().getCropAt(SimpleLocation.getByBukkitLocation(cropLocation));
                            if (optionalCrop.isEmpty()) {
                                plugin.debug("Found a crop without data interacted by " + player.getName() + " at " + cropLocation + ". " +
                                        "You can safely ignore this if you updated the plugin to 3.4+ recently.");
                                plugin.getWorldManager().addCropAt(new MemoryCrop(crop.getKey(), stage.getPoint()), SimpleLocation.getByBukkitLocation(cropLocation));
                                return FunctionResult.RETURN;
                            }
                            if (!optionalCrop.get().getKey().equals(crop.getKey())) {
                                LogUtils.warn("Found a crop having inconsistent data interacted by " + player.getName() + " at " + cropLocation + ".");
                                plugin.getWorldManager().addCropAt(new MemoryCrop(crop.getKey(), stage.getPoint()), SimpleLocation.getByBukkitLocation(cropLocation));
                                return FunctionResult.RETURN;
                            }
                            CropInteractEvent interactEvent = new CropInteractEvent(conditionWrapper.getPlayer(), interactWrapper.getItemInHand(), cropLocation, optionalCrop.get());
                            if (EventUtils.fireAndCheckCancel(interactEvent)) {
                                return FunctionResult.CANCEL_EVENT_AND_RETURN;
                            }
                            String itemID = getItemID(itemInHand);
                            int itemAmount = itemInHand.getAmount();
                            // check pot use requirements
                            if (RequirementManager.isRequirementMet(potState, pot.getUseRequirements())) {
                                // get water in pot
                                int waterInPot = plugin.getWorldManager().getPotAt(SimpleLocation.getByBukkitLocation(potLocation)).map(WorldPot::getWater).orElse(0);
                                // water the pot
                                for (PassiveFillMethod method : pot.getPassiveFillMethods()) {
                                    if (method.getUsed().equals(itemID) && itemAmount >= method.getUsedAmount()) {
                                        if (method.canFill(potState)) {
                                            if (waterInPot < pot.getStorage()) {
                                                if (player.getGameMode() != GameMode.CREATIVE) {
                                                    itemInHand.setAmount(itemAmount - method.getUsedAmount());
                                                    if (method.getReturned() != null) {
                                                        ItemStack returned = getItemStack(player, method.getReturned());
                                                        ItemUtils.giveItem(player, returned, method.getReturnedAmount());
                                                    }
                                                }
                                                method.trigger(potState);
                                                pot.trigger(ActionTrigger.ADD_WATER, potState);
                                                plugin.getWorldManager().addWaterToPot(pot, SimpleLocation.getByBukkitLocation(potLocation), method.getAmount());
                                            } else {
                                                pot.trigger(ActionTrigger.FULL, potState);
                                            }
                                        }
                                        return FunctionResult.RETURN;
                                    }
                                }
                            }

                            WorldCrop worldCrop = optionalCrop.get();
                            // if not reached the max point, try detecting bone meals
                            if (worldCrop.getPoint() < crop.getMaxPoints()) {
                                for (BoneMeal boneMeal : crop.getBoneMeals()) {
                                    if (boneMeal.getItem().equals(itemID)) {
                                        if (player.getGameMode() != GameMode.CREATIVE) {
                                            itemInHand.setAmount(itemAmount - boneMeal.getUsedAmount());
                                            if (boneMeal.getReturned() != null) {
                                                ItemStack returned = getItemStack(player, boneMeal.getReturned());
                                                ItemUtils.giveItem(player, returned, boneMeal.getReturnedAmount());
                                            }
                                        }
                                        boneMeal.trigger(cropState);
                                        plugin.getWorldManager().addPointToCrop(crop, SimpleLocation.getByBukkitLocation(cropLocation), boneMeal.getPoint());
                                        return FunctionResult.RETURN;
                                    }
                                }
                            }
                            // trigger interact actions
                            crop.trigger(ActionTrigger.INTERACT, cropState);
                            return FunctionResult.PASS;
                        }, CFunction.FunctionPriority.HIGH)
                );

                this.registerItemFunction(stage.getStageID(), FunctionTrigger.BREAK,
                        /*
                         * Break the crop
                         */
                        new CFunction(conditionWrapper -> {
                            if (!(conditionWrapper instanceof BreakWrapper breakWrapper)) {
                                return FunctionResult.PASS;
                            }
                            Player player = breakWrapper.getPlayer();
                            Location cropLocation = breakWrapper.getLocation().toBlockLocation();
                            State state = new State(player, breakWrapper.getItemInHand(), cropLocation);
                            // check crop break requirements
                            if (RequirementManager.isRequirementMet(state, crop.getBreakRequirements())) {
                                return FunctionResult.CANCEL_EVENT_AND_RETURN;
                            }
                            Optional<WorldCrop> optionalWorldCrop = plugin.getWorldManager().getCropAt(SimpleLocation.getByBukkitLocation(cropLocation));
                            if (optionalWorldCrop.isEmpty()) {
                                plugin.debug("Found a crop without data broken by " + player.getName() + " at " + cropLocation + ". " +
                                        "You can safely ignore this if you updated the plugin to 3.4+ recently.");
                                // to prevent players from suffering loss
                                // event would only be fired for those normal crops
                                crop.trigger(ActionTrigger.BREAK, state);
                                return FunctionResult.RETURN;
                            }
                            if (!optionalWorldCrop.get().getKey().equals(crop.getKey())) {
                                LogUtils.warn("Found a crop having inconsistent data broken by " + player.getName() + " at " + cropLocation + ".");
                                plugin.getWorldManager().removeCropAt(SimpleLocation.getByBukkitLocation(cropLocation));
                                return FunctionResult.RETURN;
                            }
                            // fire event
                            CropBreakEvent breakEvent = new CropBreakEvent(player, cropLocation, optionalWorldCrop.get());
                            if (EventUtils.fireAndCheckCancel(breakEvent))
                                return FunctionResult.CANCEL_EVENT_AND_RETURN;
                            // trigger actions
                            crop.trigger(ActionTrigger.BREAK, state);
                            plugin.getWorldManager().removeCropAt(SimpleLocation.getByBukkitLocation(cropLocation));
                            return FunctionResult.PASS;
                        }, CFunction.FunctionPriority.NORMAL)
                );
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void loadPot(String key, ConfigurationSection section) {
        int storage = section.getInt("max-water-storage", 1);
        String dryModel = Preconditions.checkNotNull(section.getString("base.dry"), "base.dry should not be null");
        String wetModel = Preconditions.checkNotNull(section.getString("base.wet"), "base.wet should not be null");
        boolean enableFertilizedAppearance = section.getBoolean("fertilized-pots.enable", false);

        PotConfig pot = new PotConfig(
                key, storage,
                dryModel, wetModel,
                enableFertilizedAppearance,
                enableFertilizedAppearance ? ConfigUtils.getFertilizedPotMap(section.getConfigurationSection("fertilized-pots")) : new HashMap<>(),
                section.contains("water-bar") ? WaterBar.of(
                        section.getString("water-bar.left", ""),
                        section.getString("water-bar.empty", ""),
                        section.getString("water-bar.full", ""),
                        section.getString("water-bar.right", "")
                ) : null,
                ConfigUtils.getPassiveFillMethods(section.getConfigurationSection("fill-method")),
                ConfigUtils.getActionMap(section.getConfigurationSection("events")),
                ConfigUtils.getRequirements(section.getConfigurationSection("requirements.place")),
                ConfigUtils.getRequirements(section.getConfigurationSection("requirements.break")),
                ConfigUtils.getRequirements(section.getConfigurationSection("requirements.use"))
        );

        if (!this.registerPot(pot)) {
            LogUtils.warn("Failed to register new pot: " + key + " due to duplicated entries.");
            return;
        }

        for (String potItemID : pot.getPotBlocks()) {
            this.registerItemFunction(potItemID, FunctionTrigger.BE_INTERACTED,
                    /*
                     * Interact the pot
                     */
                    new CFunction(conditionWrapper -> {
                        if (!(conditionWrapper instanceof InteractBlockWrapper interactBlockWrapper)) {
                            return FunctionResult.PASS;
                        }

                        ItemStack itemInHand = interactBlockWrapper.getItemInHand();
                        Player player = interactBlockWrapper.getPlayer();
                        Location location = interactBlockWrapper.getClickedBlock().getLocation();
                        // check pot use requirement
                        State state = new State(player, itemInHand, location);
                        if (!RequirementManager.isRequirementMet(state, pot.getUseRequirements())) {
                            return FunctionResult.RETURN;
                        }
                        Optional<WorldPot> optionalPot = plugin.getWorldManager().getPotAt(SimpleLocation.getByBukkitLocation(location));
                        if (optionalPot.isEmpty()) {
                            plugin.debug("Found a pot without data interacted by " + player.getName() + " at " + location + ". " +
                                    "You can safely ignore this if you updated the plugin to 3.4+ recently.");
                            plugin.getWorldManager().addPotAt(new MemoryPot(pot.getKey()), SimpleLocation.getByBukkitLocation(location));
                            return FunctionResult.RETURN;
                        }
                        if (!optionalPot.get().getKey().equals(pot.getKey())) {
                            LogUtils.warn("Found a pot having inconsistent data interacted by " + player.getName() + " at " + location + ".");
                            plugin.getWorldManager().addPotAt(new MemoryPot(pot.getKey()), SimpleLocation.getByBukkitLocation(location));
                            return FunctionResult.RETURN;
                        }
                        // fire the event
                        PotInteractEvent interactEvent = new PotInteractEvent(player, itemInHand, location, optionalPot.get());
                        if (EventUtils.fireAndCheckCancel(interactEvent)) {
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;
                        }
                        String itemID = getItemID(itemInHand);
                        int itemAmount = itemInHand.getAmount();
                        // get water in pot
                        int waterInPot = plugin.getWorldManager().getPotAt(SimpleLocation.getByBukkitLocation(location)).map(WorldPot::getWater).orElse(0);
                        for (PassiveFillMethod method : pot.getPassiveFillMethods()) {
                            if (method.getUsed().equals(itemID) && itemAmount >= method.getUsedAmount()) {
                                if (method.canFill(state)) {
                                    if (waterInPot < pot.getStorage()) {
                                        if (player.getGameMode() != GameMode.CREATIVE) {
                                            itemInHand.setAmount(itemAmount - method.getUsedAmount());
                                            if (method.getReturned() != null) {
                                                ItemStack returned = getItemStack(player, method.getReturned());
                                                ItemUtils.giveItem(player, returned, method.getReturnedAmount());
                                            }
                                        }
                                        method.trigger(state);
                                        pot.trigger(ActionTrigger.ADD_WATER, state);
                                        plugin.getWorldManager().addWaterToPot(pot, SimpleLocation.getByBukkitLocation(location), method.getAmount());
                                    } else {
                                        pot.trigger(ActionTrigger.FULL, state);
                                    }
                                }
                                return FunctionResult.RETURN;
                            }
                        }
                        // trigger actions
                        pot.trigger(ActionTrigger.INTERACT, state);
                        return FunctionResult.PASS;
                    }, CFunction.FunctionPriority.NORMAL)
            );
            this.registerItemFunction(potItemID, FunctionTrigger.BREAK,
                    /*
                     * Break the pot
                     */
                    new CFunction(conditionWrapper -> {
                        if (!(conditionWrapper instanceof BreakBlockWrapper blockWrapper)) {
                            return FunctionResult.PASS;
                        }
                        // check break requirements
                        Location location = blockWrapper.getBrokenBlock().getLocation();
                        State state = new State(blockWrapper.getPlayer(), blockWrapper.getItemInHand(), location);
                        if (!RequirementManager.isRequirementMet(state, pot.getBreakRequirements())) {
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;
                        }
                        Optional<WorldPot> optionalPot = plugin.getWorldManager().getPotAt(SimpleLocation.getByBukkitLocation(location));
                        if (optionalPot.isEmpty()) {
                            LogUtils.warn("Found a pot without data broken by " + state.getPlayer().getName() + " at " + location + "." +
                                    " You can safely ignore this if you updated the plugin to 3.4+ recently.");
                            return FunctionResult.RETURN;
                        }
                        if (!optionalPot.get().getKey().equals(pot.getKey())) {
                            LogUtils.warn("Found a pot having inconsistent data broken by " + state.getPlayer().getName() + " at " + location + ".");
                            plugin.getWorldManager().removePotAt(SimpleLocation.getByBukkitLocation(location));
                            return FunctionResult.RETURN;
                        }
                        // fire event
                        PotBreakEvent breakEvent = new PotBreakEvent(blockWrapper.getPlayer(), location, optionalPot.get());
                        if (EventUtils.fireAndCheckCancel(breakEvent)) {
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;
                        }
                        // remove data
                        plugin.getWorldManager().removePotAt(SimpleLocation.getByBukkitLocation(location));
                        pot.trigger(ActionTrigger.BREAK, state);
                        return FunctionResult.RETURN;
                    }, CFunction.FunctionPriority.NORMAL)
            );

            this.registerItemFunction(potItemID, FunctionTrigger.PLACE,
                    /*
                     * Place the pot
                     */
                    new CFunction(conditionWrapper -> {
                        if (!(conditionWrapper instanceof PlaceBlockWrapper blockWrapper)) {
                            return FunctionResult.PASS;
                        }
                        Location location = blockWrapper.getPlacedBlock().getLocation();
                        Player player = blockWrapper.getPlayer();
                        // check place requirements
                        State state = new State(player, blockWrapper.getItemInHand(), location);
                        if (!RequirementManager.isRequirementMet(state, pot.getPlaceRequirements())) {
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;
                        }
                        // check limitation
                        if (plugin.getWorldManager().isReachLimit(SimpleLocation.getByBukkitLocation(location), ItemType.POT)) {
                            pot.trigger(ActionTrigger.REACH_LIMIT, new State(player, blockWrapper.getItemInHand(), location));
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;
                        }
                        // fire event
                        PotPlaceEvent potPlaceEvent = new PotPlaceEvent(player, location, pot);
                        if (EventUtils.fireAndCheckCancel(potPlaceEvent)) {
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;
                        }
                        // add data
                        plugin.getWorldManager().addPotAt(new MemoryPot(pot.getKey()), SimpleLocation.getByBukkitLocation(location));
                        pot.trigger(ActionTrigger.PLACE, state);
                        return FunctionResult.RETURN;
                    }, CFunction.FunctionPriority.NORMAL));
        }
    }

    private void registerItemFunction(String item, FunctionTrigger trigger, CFunction... function) {
        if (itemID2FunctionMap.containsKey(item)) {
            var previous = itemID2FunctionMap.get(item);
            TreeSet<CFunction> previousFunctions = previous.get(trigger);
            if (previousFunctions == null) {
                previous.put(trigger, new TreeSet<>(List.of(function)));
            } else {
                previousFunctions.addAll(List.of(function));
            }
        } else {
            TreeSet<CFunction> list = new TreeSet<>(List.of(function));
            itemID2FunctionMap.put(item, new HashMap<>(Map.of(trigger, list)));
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public void handlePlayerInteractBlock(
            Player player,
            Block clickedBlock,
            BlockFace clickedFace,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        // check anti-grief
        if (!antiGrief.canInteract(player, clickedBlock.getLocation()))
            return;

        // check pot firstly because events might be cancelled
        var condition = new InteractBlockWrapper(player, clickedBlock, clickedFace);
        String blockID = customProvider.getBlockID(clickedBlock);
        TreeSet<CFunction> blockFunctions = Optional.ofNullable(itemID2FunctionMap.get(blockID))
                .map(map -> map.get(FunctionTrigger.BE_INTERACTED))
                .orElse(null);
        if (handleFunctions(blockFunctions, condition, event)) {
            return;
        }
        // Then check item in hand
        String itemID = customProvider.getItemID(condition.getItemInHand());
        Optional.ofNullable(itemID2FunctionMap.get(itemID))
                .map(map -> map.get(FunctionTrigger.INTERACT_AT))
                .ifPresent(itemFunctions -> handleFunctions(itemFunctions, condition, event));
    }

    public void handlePlayerInteractAir(
            Player player,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        // check anti-grief
        if (!antiGrief.canInteract(player, player.getLocation()))
            return;

        var condition = new InteractWrapper(player, null);
        // check item in hand
        String itemID = customProvider.getItemID(condition.getItemInHand());
        Optional.ofNullable(itemID2FunctionMap.get(itemID))
                .map(map -> map.get(FunctionTrigger.INTERACT_AIR))
                .ifPresent(cFunctions -> handleFunctions(cFunctions, condition, event));
    }

    public void handlePlayerBreakBlock(
            Player player,
            Block brokenBlock,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        /*
          No need to check anti-grief here as the event should be cancelled by the anti-grief plugin
         */

        // check blocks, no need to check item in hand
        String blockID = customProvider.getBlockID(brokenBlock);
        Optional.ofNullable(itemID2FunctionMap.get(blockID))
                .map(map -> map.get(FunctionTrigger.BREAK))
                .ifPresent(cFunctions -> handleFunctions(cFunctions, new BreakBlockWrapper(player, brokenBlock), event));
    }

    @SuppressWarnings("DuplicatedCode")
    public void handlePlayerInteractFurniture(
            Player player,
            Location location,
            String id,
            Entity baseEntity,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        // check anti-grief
        if (!antiGrief.canInteract(player, location))
            return;

        var condition = new InteractFurnitureWrapper(player, location, id, baseEntity);
        // check furniture firstly
        TreeSet<CFunction> functions = Optional.ofNullable(itemID2FunctionMap.get(id)).map(map -> map.get(FunctionTrigger.BE_INTERACTED)).orElse(null);
        if (handleFunctions(functions, condition, event)) {
            return;
        }
        // Then check item in hand
        String itemID = customProvider.getItemID(condition.getItemInHand());
        Optional.ofNullable(itemID2FunctionMap.get(itemID))
                .map(map -> map.get(FunctionTrigger.INTERACT_AT))
                .ifPresent(cFunctions -> handleFunctions(cFunctions, condition, event));
    }

    public void handlePlayerPlaceFurniture(
            Player player,
            Location location,
            String id,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

         /*
          No need to check anti-grief here as the event should be cancelled by the anti-grief plugin
         */

        // check furniture, no need to check item in hand
        Optional.ofNullable(itemID2FunctionMap.get(id))
                .map(map -> map.get(FunctionTrigger.PLACE))
                .ifPresent(cFunctions -> handleFunctions(cFunctions, new PlaceFurnitureWrapper(player, location, id), event));
    }

    public void handlePlayerBreakFurniture(
            Player player,
            Location location,
            String id,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

         /*
          No need to check anti-grief here as the event should be handled by ItemsAdder/Oraxen
         */

        // check furniture, no need to check item in hand
        Optional.ofNullable(itemID2FunctionMap.get(id))
                .map(map -> map.get(FunctionTrigger.BREAK))
                .ifPresent(cFunctions -> handleFunctions(cFunctions, new BreakFurnitureWrapper(player, location, id), event));
    }

    public void handlePlayerPlaceBlock(Player player, Block block, String blockID, Cancellable event) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

         /*
          No need to check anti-grief here as the event should be cancelled by the anti-grief plugin
         */

        // check furniture, no need to check item in hand
        Optional.ofNullable(itemID2FunctionMap.get(blockID))
                .map(map -> map.get(FunctionTrigger.PLACE))
                .ifPresent(cFunctions -> handleFunctions(cFunctions, new PlaceBlockWrapper(player, block, blockID), event));
    }

    private boolean handleFunctions(Collection<CFunction> functions, ConditionWrapper wrapper, @Nullable Cancellable event) {
        if (functions == null) return false;
        for (CFunction function : functions) {
            FunctionResult result = function.apply(wrapper);
            if (result == FunctionResult.CANCEL_EVENT_AND_RETURN) {
                if (event != null) event.setCancelled(true);
                return true;
            }
            if (result == FunctionResult.RETURN)
                return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Collection<Location> getPotInRange(Location baseLocation, int width, int length, float yaw, String potID) {
        ArrayList<Location> potLocations = new ArrayList<>();
        int extend = (width-1) / 2;
        int extra = (width-1) % 2;
        switch ((int) ((yaw + 180) / 45)) {
            case 0 -> {
                // -180 ~ -135
                for (int i = -extend; i <= extend + extra; i++) {
                    for (int j = 0; j < length; j++) {
                        potLocations.add(baseLocation.clone().add(i, 0, -j));
                    }
                }
            }
            case 1 -> {
                // -135 ~ -90
                for (int i = -extend - extra; i <= extend; i++) {
                    for (int j = 0; j < length; j++) {
                        potLocations.add(baseLocation.clone().add(j, 0, i));
                    }
                }
            }
            case 2 -> {
                // -90 ~ -45
                for (int i = -extend; i <= extend + extra; i++) {
                    for (int j = 0; j < length; j++) {
                        potLocations.add(baseLocation.clone().add(j, 0, i));
                    }
                }
            }
            case 3 -> {
                // -45 ~ 0
                for (int i = -extend; i <= extend + extra; i++) {
                    for (int j = 0; j < length; j++) {
                        potLocations.add(baseLocation.clone().add(i, 0, j));
                    }
                }
            }
            case 4 -> {
                // 0 ~ 45
                for (int i = -extend - extra; i <= extend; i++) {
                    for (int j = 0; j < length; j++) {
                        potLocations.add(baseLocation.clone().add(i, 0, j));
                    }
                }
            }
            case 5 -> {
                // 45 ~ 90
                for (int i = -extend; i <= extend + extra; i++) {
                    for (int j = 0; j < length; j++) {
                        potLocations.add(baseLocation.clone().add(-j, 0, i));
                    }
                }
            }
            case 6 -> {
                // 90 ~ 135
                for (int i = -extend - extra; i <= extend; i++) {
                    for (int j = 0; j < length; j++) {
                        potLocations.add(baseLocation.clone().add(-j, 0, i));
                    }
                }
            }
            case 7 -> {
                // 135 ~ 180
                for (int i = -extend - extra; i <= extend; i++) {
                    for (int j = 0; j < length; j++) {
                        potLocations.add(baseLocation.clone().add(i, 0, -j));
                    }
                }
            }
            default -> potLocations.add(baseLocation);
        }
        return potLocations.stream().filter(it -> (customProvider.getBlockID(it.getBlock()).equals(potID))).toList();
    }
}

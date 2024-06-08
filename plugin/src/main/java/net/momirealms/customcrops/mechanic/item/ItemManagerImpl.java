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
import net.momirealms.customcrops.api.mechanic.condition.DeathConditions;
import net.momirealms.customcrops.api.mechanic.item.*;
import net.momirealms.customcrops.api.mechanic.item.custom.AbstractCustomListener;
import net.momirealms.customcrops.api.mechanic.item.custom.CustomProvider;
import net.momirealms.customcrops.api.mechanic.item.water.PassiveFillMethod;
import net.momirealms.customcrops.api.mechanic.item.water.PositiveFillMethod;
import net.momirealms.customcrops.api.mechanic.misc.CRotation;
import net.momirealms.customcrops.api.mechanic.misc.Reason;
import net.momirealms.customcrops.api.mechanic.misc.image.WaterBar;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import net.momirealms.customcrops.api.mechanic.world.CustomCropsBlock;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.*;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.api.util.LocationUtils;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.mechanic.item.custom.itemsadder.ItemsAdderListener;
import net.momirealms.customcrops.mechanic.item.custom.itemsadder.ItemsAdderProvider;
import net.momirealms.customcrops.mechanic.item.custom.oraxenlegacy.LegacyOraxenListener;
import net.momirealms.customcrops.mechanic.item.custom.oraxenlegacy.LegacyOraxenProvider;
import net.momirealms.customcrops.mechanic.item.function.CFunction;
import net.momirealms.customcrops.mechanic.item.function.FunctionResult;
import net.momirealms.customcrops.mechanic.item.function.FunctionTrigger;
import net.momirealms.customcrops.mechanic.item.function.wrapper.*;
import net.momirealms.customcrops.mechanic.item.impl.CropConfig;
import net.momirealms.customcrops.mechanic.item.impl.PotConfig;
import net.momirealms.customcrops.mechanic.item.impl.SprinklerConfig;
import net.momirealms.customcrops.mechanic.item.impl.WateringCanConfig;
import net.momirealms.customcrops.mechanic.item.impl.fertilizer.*;
import net.momirealms.customcrops.mechanic.world.block.*;
import net.momirealms.customcrops.util.ConfigUtils;
import net.momirealms.customcrops.util.ItemUtils;
import net.momirealms.customcrops.util.RotationUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
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
    private final HashSet<String> deadCrops;

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
        this.deadCrops = new HashSet<>();
        if (Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
            if (Bukkit.getPluginManager().getPlugin("Oraxen").getDescription().getVersion().startsWith("2")) {
                try {
                    Class<?> oraxenListenerClass = Class.forName("net.momirealms.customcrops.mechanic.item.custom.oraxen.OraxenListener");
                    Constructor<?> oraxenListenerConstructor = oraxenListenerClass.getDeclaredConstructor(ItemManager.class);
                    oraxenListenerConstructor.setAccessible(true);
                    this.listener = (AbstractCustomListener) oraxenListenerConstructor.newInstance(this);
                    Class<?> oraxenProviderClass = Class.forName("net.momirealms.customcrops.mechanic.item.custom.oraxen.OraxenProvider");
                    Constructor<?> oraxenProviderConstructor = oraxenProviderClass.getDeclaredConstructor(ItemManager.class);
                    oraxenProviderConstructor.setAccessible(true);
                    this.customProvider = (CustomProvider) oraxenProviderConstructor.newInstance();
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            } else {
                listener = new LegacyOraxenListener(this);
                customProvider = new LegacyOraxenProvider();
            }
        } else if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            listener = new ItemsAdderListener(this);
            customProvider = new ItemsAdderProvider();
        } else if (Bukkit.getPluginManager().getPlugin("MythicCrucible") != null) {
//            listener = new CrucibleListener(this);
//            customProvider = new CrucibleProvider();
        } else {
            LogUtils.severe("======================================================");
            LogUtils.severe(" Please install ItemsAdder or Oraxen as dependency.");
            LogUtils.severe(" ItemsAdder: https://www.spigotmc.org/resources/73355/");
            LogUtils.severe(" Oraxen: https://www.spigotmc.org/resources/72448/");
            LogUtils.severe("======================================================");
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
        this.stage2CropStageMap.clear();
        this.id2FertilizerMap.clear();
        this.item2FertilizerMap.clear();
        this.deadCrops.clear();
        CFunction.resetID();
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
        if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getAmount() == 0)
            return "AIR";
        String id;
        id = customProvider.getItemID(itemStack);
        if (id != null) return id;
        else {
            for (ItemLibrary library : itemDetectionArray) {
                id = library.getItemID(itemStack);
                if (id != null)
                    return library.identification() + ":" + id;
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
            ItemLibrary library = itemLibraryMap.get(split[0]);
            if (library == null) {
                LogUtils.warn("Error occurred when building item: " + id + ". Possible causes:");
                LogUtils.warn("① Item library: " + split[0] + " doesn't exist.");
                LogUtils.warn("② If you are using ItemsAdder, " + id + " doesn't exist in your ItemsAdder config");
                return new ItemStack(Material.AIR);
            }
            return library.buildItem(player, split[1]);
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
        if (sprinkler.get3DItemWithWater() != null) {
            if (this.threeDItem2SprinklerMap.put(sprinkler.get3DItemWithWater(), sprinkler) != null) {
                LogUtils.warn("Item " + sprinkler.get3DItemWithWater() + " has more than one sprinkler config.");
                return false;
            }
        }
        return true;
    }

    @Override
    public void placeItem(Location location, ItemCarrier carrier, String id) {
        switch (carrier) {
            case ITEM_DISPLAY, ITEM_FRAME -> {
                customProvider.placeFurniture(location, id);
            }
            case TRIPWIRE, NOTE_BLOCK, CHORUS, MUSHROOM -> {
                customProvider.placeBlock(location, id);
            }
        }
    }

    @Override
    public void placeItem(Location location, ItemCarrier carrier, String id, CRotation rotate) {
        switch (carrier) {
            case ITEM_DISPLAY, ITEM_FRAME -> {
                Entity entity = customProvider.placeFurniture(location, id);
                if (rotate == null || rotate == CRotation.NONE) return;
                if (entity instanceof ItemFrame frame) {
                    frame.setRotation(RotationUtils.getBukkitRotation(rotate));
                } else if (entity instanceof ItemDisplay display) {
                    display.setRotation(RotationUtils.getFloatRotation(rotate), display.getLocation().getPitch());
                }
            }
            case TRIPWIRE, NOTE_BLOCK, CHORUS, MUSHROOM -> customProvider.placeBlock(location, id);
        }
    }

    @Override
    public CRotation removeAnythingAt(Location location) {
        return customProvider.removeAnythingAt(location);
    }

    @Override
    public CRotation getRotation(Location location) {
        return customProvider.getRotation(location);
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
    public Sprinkler getSprinklerByBlock(@NotNull Block block) {
        return Optional.ofNullable(customProvider.getBlockID(block)).map(threeDItem2SprinklerMap::get).orElse(null);
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

    @Override
    public Crop.Stage getCropStageByStageID(String id) {
        return stage2CropStageMap.get(id);
    }

    @SuppressWarnings("DuplicatedCode")
    private void loadItems() {
        for (String item : List.of("watering-cans", "pots", "crops", "sprinklers", "fertilizers")) {
            File folder = new File(plugin.getDataFolder(), "contents" + File.separator + item);
            if (!folder.exists()) {
                plugin.saveResource("contents" + File.separator + item + File.separator + "default.yml", true);
                ConfigUtils.addDefaultNamespace(new File(plugin.getDataFolder(), "contents" + File.separator + item + File.separator + "default.yml"));
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
        this.loadDeadCrops();
        if (ConfigManager.enableGreenhouse())
            this.loadGreenhouse();
        if (ConfigManager.enableScarecrow())
            this.loadScarecrow();
    }

    private void loadDeadCrops() {
        // register functions for dead crops
        // or just keep it empty and let ItemsAdder/Oraxen handle the events
        for (String id : deadCrops) {
            this.registerItemFunction(id, FunctionTrigger.BE_INTERACTED,
                    new CFunction(conditionWrapper -> {
                        if (!(conditionWrapper instanceof InteractFurnitureWrapper furnitureWrapper)) {
                            return FunctionResult.PASS;
                        }
                        Player player = furnitureWrapper.getPlayer();
                        Location cropLocation = furnitureWrapper.getLocation();
                        ItemStack itemInHand = furnitureWrapper.getItemInHand();
                        String itemID = getItemID(itemInHand);
                        int itemAmount = itemInHand.getAmount();
                        Location potLocation = cropLocation.clone().subtract(0,1,0);
                        Pot pot = getPotByBlock(potLocation.getBlock());
                        State potState = new State(player, itemInHand, potLocation);
                        // check pot use requirements
                        if (pot != null && RequirementManager.isRequirementMet(potState, pot.getUseRequirements())) {
                            // get water in pot
                            int waterInPot = plugin.getWorldManager().getPotAt(SimpleLocation.of(potLocation)).map(WorldPot::getWater).orElse(0);
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
                                            plugin.getWorldManager().addWaterToPot(pot, method.getAmount(), SimpleLocation.of(potLocation));
                                        } else {
                                            pot.trigger(ActionTrigger.FULL, potState);
                                        }
                                    }
                                    return FunctionResult.RETURN;
                                }
                            }
                        }
                        return FunctionResult.PASS;
                    }, CFunction.FunctionPriority.NORMAL),
                    new CFunction(conditionWrapper -> {
                        if (!(conditionWrapper instanceof InteractBlockWrapper blockWrapper)) {
                            return FunctionResult.PASS;
                        }
                        Player player = blockWrapper.getPlayer();
                        Location cropLocation = blockWrapper.getLocation();
                        ItemStack itemInHand = blockWrapper.getItemInHand();
                        String itemID = getItemID(itemInHand);
                        int itemAmount = itemInHand.getAmount();
                        Location potLocation = cropLocation.clone().subtract(0,1,0);
                        Pot pot = getPotByBlock(potLocation.getBlock());
                        State potState = new State(player, itemInHand, potLocation);
                        // check pot use requirements
                        if (pot != null && RequirementManager.isRequirementMet(potState, pot.getUseRequirements())) {
                            // get water in pot
                            int waterInPot = plugin.getWorldManager().getPotAt(SimpleLocation.of(potLocation)).map(WorldPot::getWater).orElse(0);
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
                                            plugin.getWorldManager().addWaterToPot(pot, method.getAmount(), SimpleLocation.of(potLocation));
                                        } else {
                                            pot.trigger(ActionTrigger.FULL, potState);
                                        }
                                    }
                                    return FunctionResult.RETURN;
                                }
                            }
                        }
                        return FunctionResult.PASS;
                    }, CFunction.FunctionPriority.NORMAL)
            );
        }
    }

    private void loadGreenhouse() {
        this.registerItemFunction(ConfigManager.greenhouseID(), FunctionTrigger.PLACE,
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof PlaceWrapper placeWrapper)) {
                        return FunctionResult.PASS;
                    }
                    // fire event
                    GreenhouseGlassPlaceEvent event = new GreenhouseGlassPlaceEvent(placeWrapper.getPlayer(), placeWrapper.getLocation());
                    if (EventUtils.fireAndCheckCancel(event))
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;

                    SimpleLocation simpleLocation = SimpleLocation.of(placeWrapper.getLocation());
                    plugin.getWorldManager().addGlassAt(new MemoryGlass(simpleLocation), simpleLocation);
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL)
        );
        this.registerItemFunction(ConfigManager.greenhouseID(), FunctionTrigger.BREAK,
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof BreakWrapper breakWrapper)) {
                        return FunctionResult.PASS;
                    }

                    // get or fix
                    Location location = breakWrapper.getLocation();
                    SimpleLocation simpleLocation = SimpleLocation.of(location);
                    Optional<WorldGlass> optionalWorldGlass = plugin.getWorldManager().getGlassAt(simpleLocation);
                    if (optionalWorldGlass.isEmpty()) {
                        WorldGlass glass = new MemoryGlass(simpleLocation);
                        optionalWorldGlass = Optional.of(glass);
                        plugin.getWorldManager().addGlassAt(glass, simpleLocation);
                    }

                    // fire event
                    GreenhouseGlassBreakEvent event = new GreenhouseGlassBreakEvent(breakWrapper.getPlayer(), location, optionalWorldGlass.get(), Reason.BREAK);
                    if (EventUtils.fireAndCheckCancel(event))
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;

                    plugin.getWorldManager().removeGlassAt(simpleLocation);
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL)
        );
    }

    private void loadScarecrow() {
        this.registerItemFunction(ConfigManager.scarecrowID(), FunctionTrigger.PLACE,
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof PlaceWrapper placeWrapper)) {
                        return FunctionResult.PASS;
                    }
                    // fire event
                    ScarecrowPlaceEvent event = new ScarecrowPlaceEvent(placeWrapper.getPlayer(), placeWrapper.getLocation());
                    if (EventUtils.fireAndCheckCancel(event))
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;

                    SimpleLocation simpleLocation = SimpleLocation.of(placeWrapper.getLocation());
                    plugin.getWorldManager().addScarecrowAt(new MemoryScarecrow(simpleLocation), simpleLocation);
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL)
        );
        this.registerItemFunction(ConfigManager.scarecrowID(), FunctionTrigger.BREAK,
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof BreakWrapper breakWrapper)) {
                        return FunctionResult.PASS;
                    }

                    // get or fix
                    Location location = breakWrapper.getLocation();
                    SimpleLocation simpleLocation = SimpleLocation.of(location);
                    Optional<WorldScarecrow> optionalWorldScarecrow = plugin.getWorldManager().getScarecrowAt(simpleLocation);
                    if (optionalWorldScarecrow.isEmpty()) {
                        WorldScarecrow scarecrow = new MemoryScarecrow(simpleLocation);
                        optionalWorldScarecrow = Optional.of(scarecrow);
                        plugin.getWorldManager().addScarecrowAt(scarecrow, simpleLocation);
                    }

                    // fire event
                    ScarecrowBreakEvent event = new ScarecrowBreakEvent(breakWrapper.getPlayer(), location, optionalWorldScarecrow.get(), Reason.BREAK);
                    if (EventUtils.fireAndCheckCancel(event))
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;

                    plugin.getWorldManager().removeScarecrowAt(simpleLocation);
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL)
        );
    }

    @SuppressWarnings("DuplicatedCode")
    private void loadWateringCan(String key, ConfigurationSection section) {
        String itemID = section.getString("item");
        int width = section.getInt("effective-range.width");
        int length = section.getInt("effective-range.length");
        HashSet<String> potWhiteList = new HashSet<>(section.getStringList("pot-whitelist"));
        HashSet<String> sprinklerWhiteList = new HashSet<>(section.getStringList("sprinkler-whitelist"));
        boolean hasDynamicLore = section.getBoolean("dynamic-lore.enable", false);
        List<String> lore = section.getStringList("dynamic-lore.lore");

        WateringCanConfig wateringCan = new WateringCanConfig(
                key,
                itemID, section.getBoolean("infinite", false), width,
                length, section.getInt("capacity", 3), section.getInt("water", 1),
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
                 * Handle clicking sprinkler with a watering can
                 */
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractBlockWrapper blockWrapper)) {
                        return FunctionResult.PASS;
                    }
                    // is a pot
                    Block block = blockWrapper.getClickedBlock();
                    Sprinkler sprinkler = getSprinklerBy3DItemID(customProvider.getBlockID(block));
                    if (sprinkler == null) {
                        return FunctionResult.PASS;
                    }
                    final Player player = blockWrapper.getPlayer();
                    final ItemStack itemInHand = blockWrapper.getItemInHand();
                    final Location clicked = block.getLocation();
                    State state = new State(player, itemInHand, clicked);
                    // check watering-can requirements
                    if (!RequirementManager.isRequirementMet(state, wateringCan.getRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // check whitelist
                    if (!wateringCan.getSprinklerWhitelist().contains(sprinkler.getKey())) {
                        wateringCan.trigger(ActionTrigger.WRONG_SPRINKLER, state);
                        return FunctionResult.RETURN;
                    }
                    // get water in can
                    int waterInCan = wateringCan.getCurrentWater(itemInHand);

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
                        SimpleLocation simpleLocation = SimpleLocation.of(clicked);
                        Optional<WorldSprinkler> worldSprinkler = plugin.getWorldManager().getSprinklerAt(simpleLocation);
                        if (worldSprinkler.isEmpty()) {
                            plugin.debug("Player " + player.getName() + " tried to interact a sprinkler which not exists in memory. Fixing the data...");
                            WorldSprinkler sp = new MemorySprinkler(simpleLocation, sprinkler.getKey(), 0);
                            plugin.getWorldManager().addSprinklerAt(sp, simpleLocation);
                            worldSprinkler = Optional.of(sp);
                        } else {
                            if (sprinkler.getStorage() <= worldSprinkler.get().getWater()) {
                                return FunctionResult.RETURN;
                            }
                        }

                        // fire the event
                        WateringCanWaterEvent waterEvent = new WateringCanWaterEvent(player, itemInHand, new HashSet<>(Set.of(clicked)), wateringCan, worldSprinkler.get());
                        if (EventUtils.fireAndCheckCancel(waterEvent))
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;

                        state.setArg("{storage}", String.valueOf(wateringCan.getStorage()));
                        state.setArg("{current}", String.valueOf(waterInCan - 1));
                        state.setArg("{water_bar}", wateringCan.getWaterBar() == null ? "" : wateringCan.getWaterBar().getWaterBar(waterInCan - 1, wateringCan.getStorage()));
                        wateringCan.updateItem(player, itemInHand, waterInCan - 1, state.getArgs());
                        wateringCan.trigger(ActionTrigger.CONSUME_WATER, state);
                        plugin.getWorldManager().addWaterToSprinkler(sprinkler, simpleLocation, 1);
                    } else {
                        state.setArg("{storage}", String.valueOf(wateringCan.getStorage()));
                        state.setArg("{current}", "0");
                        state.setArg("{water_bar}", wateringCan.getWaterBar() == null ? "" : wateringCan.getWaterBar().getWaterBar(0, wateringCan.getStorage()));
                        wateringCan.trigger(ActionTrigger.NO_WATER, state);
                    }
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.HIGH),
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
                    Block block = blockWrapper.getClickedBlock();
                    Pot pot = getPotByBlock(block);
                    if (pot == null) {
                        return FunctionResult.PASS;
                    }
                    final Player player = blockWrapper.getPlayer();
                    final ItemStack itemStack = blockWrapper.getItemInHand();
                    final Location clicked = block.getLocation();
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

                        Collection<Location> pots = getPotInRange(clicked, wateringCan.getWidth(), wateringCan.getLength(), player.getLocation().getYaw(), pot.getKey());
                        // get or fix pot
                        SimpleLocation simpleLocation = SimpleLocation.of(clicked);
                        Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(simpleLocation);
                        if (worldPot.isEmpty()) {
                            plugin.debug("Found pot data not exists at " + simpleLocation + ". Fixing it.");
                            MemoryPot memoryPot = new MemoryPot(simpleLocation, pot.getKey());
                            plugin.getWorldManager().addPotAt(memoryPot, simpleLocation);
                            worldPot = Optional.of(memoryPot);
                        }

                        // fire the event
                        WateringCanWaterEvent waterEvent = new WateringCanWaterEvent(player, itemStack, new HashSet<>(pots), wateringCan, worldPot.get());
                        if (EventUtils.fireAndCheckCancel(waterEvent))
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;

                        state.setArg("{storage}", String.valueOf(wateringCan.getStorage()));
                        state.setArg("{current}", String.valueOf(waterInCan - 1));
                        state.setArg("{water_bar}", wateringCan.getWaterBar() == null ? "" : wateringCan.getWaterBar().getWaterBar(waterInCan - 1, wateringCan.getStorage()));
                        wateringCan.updateItem(player, itemStack, waterInCan - 1, state.getArgs());
                        wateringCan.trigger(ActionTrigger.CONSUME_WATER, state);

                        for (Location location : waterEvent.getLocation()) {
                            plugin.getWorldManager().addWaterToPot(pot, wateringCan.getWater(), SimpleLocation.of(location));
                            pot.trigger(ActionTrigger.ADD_WATER, new State(player, itemStack, location));
                        }
                    } else {
                        state.setArg("{storage}", String.valueOf(wateringCan.getStorage()));
                        state.setArg("{current}", "0");
                        state.setArg("{water_bar}", wateringCan.getWaterBar() == null ? "" : wateringCan.getWaterBar().getWaterBar(0, wateringCan.getStorage()));
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
                    String id = customProvider.getBlockID(blockWrapper.getClickedBlock());
                    Crop crop = getCropByStageID(id);
                    if (crop == null && !deadCrops.contains(id)) {
                        return FunctionResult.PASS;
                    }
                    // get pot block
                    Block potBlock = blockWrapper.getClickedBlock().getRelative(BlockFace.DOWN);
                    Pot pot = getPotByBlock(potBlock);
                    if (pot == null) {
                        plugin.debug("Unexpected issue: Detetced that crops are not planted on a pot: " + blockWrapper.getClickedBlock().getLocation());
                        return FunctionResult.RETURN;
                    }

                    final Player player = blockWrapper.getPlayer();
                    final ItemStack itemStack = blockWrapper.getItemInHand();
                    final Location clicked = blockWrapper.getClickedBlock().getLocation();
                    State state = new State(player, itemStack, clicked);
                    // check watering-can use requirements
                    if (!RequirementManager.isRequirementMet(state, wateringCan.getRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // check crop interact requirements
                    if (crop != null && !RequirementManager.isRequirementMet(state, crop.getInteractRequirements())) {
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

                        Collection<Location> pots = getPotInRange(potBlock.getLocation(), wateringCan.getWidth(), wateringCan.getLength(), player.getLocation().getYaw(), pot.getKey());

                        // get or fix pot
                        SimpleLocation simpleLocation = SimpleLocation.of(potBlock.getLocation());
                        Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(simpleLocation);
                        if (worldPot.isEmpty()) {
                            plugin.debug("Found pot data not exists at " + simpleLocation + ". Fixing it.");
                            MemoryPot memoryPot = new MemoryPot(simpleLocation, pot.getKey());
                            plugin.getWorldManager().addPotAt(memoryPot, simpleLocation);
                            worldPot = Optional.of(memoryPot);
                        }

                        // fire the event
                        WateringCanWaterEvent waterEvent = new WateringCanWaterEvent(player, itemStack, new HashSet<>(pots), wateringCan, worldPot.get());
                        if (EventUtils.fireAndCheckCancel(waterEvent))
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;

                        state.setArg("{storage}", String.valueOf(wateringCan.getStorage()));
                        state.setArg("{current}", String.valueOf(waterInCan - 1));
                        state.setArg("{water_bar}", wateringCan.getWaterBar() == null ? "" : wateringCan.getWaterBar().getWaterBar(waterInCan - 1, wateringCan.getStorage()));
                        wateringCan.updateItem(player, itemStack, waterInCan - 1, state.getArgs());
                        wateringCan.trigger(ActionTrigger.CONSUME_WATER, state);

                        for (Location location : waterEvent.getLocation()) {
                            plugin.getWorldManager().addWaterToPot(pot, wateringCan.getWater(), SimpleLocation.of(location));
                            pot.trigger(ActionTrigger.ADD_WATER, new State(player, itemStack, location));
                        }
                    } else {
                        state.setArg("{storage}", String.valueOf(wateringCan.getStorage()));
                        state.setArg("{current}", "0");
                        state.setArg("{water_bar}", wateringCan.getWaterBar() == null ? "" : wateringCan.getWaterBar().getWaterBar(0, wateringCan.getStorage()));
                        wateringCan.trigger(ActionTrigger.NO_WATER, state);
                    }
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL),
                /*
                 * Handle clicking crop with a watering can
                 */
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractFurnitureWrapper furnitureWrapper)) {
                        return FunctionResult.PASS;
                    }
                    // is a crop
                    String id = furnitureWrapper.getID();
                    Crop crop = getCropByStageID(id);
                    if (crop == null && !deadCrops.contains(id)) {
                        return FunctionResult.PASS;
                    }
                    // get pot block
                    Block potBlock = furnitureWrapper.getLocation().getBlock().getRelative(BlockFace.DOWN);
                    Pot pot = getPotByBlock(potBlock);
                    if (pot == null) {
                        LogUtils.warn("Unexpected issue: Detetced that crops are not planted on a pot: " + furnitureWrapper.getLocation());
                        return FunctionResult.RETURN;
                    }

                    final Player player = furnitureWrapper.getPlayer();
                    final ItemStack itemStack = furnitureWrapper.getItemInHand();
                    final Location clicked = furnitureWrapper.getLocation();
                    State state = new State(player, itemStack, clicked);
                    // check watering-can use requirements
                    if (!RequirementManager.isRequirementMet(state, wateringCan.getRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // check crop interact requirements
                    if (crop != null && !RequirementManager.isRequirementMet(state, crop.getInteractRequirements())) {
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

                        Collection<Location> pots = getPotInRange(potBlock.getLocation(), wateringCan.getWidth(), wateringCan.getLength(), player.getLocation().getYaw(), pot.getKey());

                        // get or fix pot
                        SimpleLocation simpleLocation = SimpleLocation.of(potBlock.getLocation());
                        Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(simpleLocation);
                        if (worldPot.isEmpty()) {
                            plugin.debug("Found pot data not exists at " + simpleLocation + ". Fixing it.");
                            MemoryPot memoryPot = new MemoryPot(simpleLocation, pot.getKey());
                            plugin.getWorldManager().addPotAt(memoryPot, simpleLocation);
                            worldPot = Optional.of(memoryPot);
                        }

                        // fire the event
                        WateringCanWaterEvent waterEvent = new WateringCanWaterEvent(player, itemStack, new HashSet<>(pots), wateringCan, worldPot.get());
                        if (EventUtils.fireAndCheckCancel(waterEvent))
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;

                        state.setArg("{storage}", String.valueOf(wateringCan.getStorage()));
                        state.setArg("{current}", String.valueOf(waterInCan - 1));
                        state.setArg("{water_bar}", wateringCan.getWaterBar() == null ? "" : wateringCan.getWaterBar().getWaterBar(waterInCan - 1, wateringCan.getStorage()));
                        wateringCan.updateItem(player, itemStack, waterInCan - 1, state.getArgs());
                        wateringCan.trigger(ActionTrigger.CONSUME_WATER, state);

                        for (Location location : waterEvent.getLocation()) {
                            plugin.getWorldManager().addWaterToPot(pot, wateringCan.getWater(), SimpleLocation.of(location));
                            pot.trigger(ActionTrigger.ADD_WATER, new State(player, itemStack, location));
                        }
                    } else {
                        state.setArg("{storage}", String.valueOf(wateringCan.getStorage()));
                        state.setArg("{current}", "0");
                        state.setArg("{water_bar}", wateringCan.getWaterBar() == null ? "" : wateringCan.getWaterBar().getWaterBar(0, wateringCan.getStorage()));
                        wateringCan.trigger(ActionTrigger.NO_WATER, state);
                    }
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL),
                /*
                 * Handle clicking furniture with a watering can
                 * This furniture may be a sprinkler, or it may be a custom piece of furniture such as a well
                 */
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractFurnitureWrapper furnitureWrapper)) {
                        return FunctionResult.PASS;
                    }
                    // check watering-can requirements
                    Player player = furnitureWrapper.getPlayer();
                    ItemStack itemInHand = furnitureWrapper.getItemInHand();
                    Location location = furnitureWrapper.getLocation();
                    State state = new State(player, itemInHand, location);
                    if (!RequirementManager.isRequirementMet(state, wateringCan.getRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // get water in can
                    int waterInCan = wateringCan.getCurrentWater(itemInHand);
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
                            SimpleLocation simpleLocation = SimpleLocation.of(location);
                            Optional<WorldSprinkler> worldSprinkler = plugin.getWorldManager().getSprinklerAt(simpleLocation);
                            if (worldSprinkler.isEmpty()) {
                                plugin.debug("Player " + player.getName() + " tried to interact a sprinkler which not exists in memory. Fixing the data...");
                                WorldSprinkler sp = new MemorySprinkler(simpleLocation, sprinkler.getKey(), 0);
                                plugin.getWorldManager().addSprinklerAt(sp, simpleLocation);
                                worldSprinkler = Optional.of(sp);
                            } else {
                                if (sprinkler.getStorage() <= worldSprinkler.get().getWater()) {
                                    return FunctionResult.RETURN;
                                }
                            }

                            // fire the event
                            WateringCanWaterEvent waterEvent = new WateringCanWaterEvent(player, itemInHand, new HashSet<>(Set.of(location)), wateringCan, worldSprinkler.get());
                            if (EventUtils.fireAndCheckCancel(waterEvent))
                                return FunctionResult.CANCEL_EVENT_AND_RETURN;

                            state.setArg("{storage}", String.valueOf(wateringCan.getStorage()));
                            state.setArg("{current}", String.valueOf(waterInCan - 1));
                            state.setArg("{water_bar}", wateringCan.getWaterBar() == null ? "" : wateringCan.getWaterBar().getWaterBar(waterInCan - 1, wateringCan.getStorage()));
                            wateringCan.updateItem(player, furnitureWrapper.getItemInHand(), waterInCan - 1, state.getArgs());
                            wateringCan.trigger(ActionTrigger.CONSUME_WATER, state);
                            plugin.getWorldManager().addWaterToSprinkler(sprinkler, simpleLocation, 1);
                        } else {
                            state.setArg("{storage}", String.valueOf(wateringCan.getStorage()));
                            state.setArg("{current}", "0");
                            state.setArg("{water_bar}", wateringCan.getWaterBar() == null ? "" : wateringCan.getWaterBar().getWaterBar(0, wateringCan.getStorage()));
                            wateringCan.trigger(ActionTrigger.NO_WATER, state);
                        }
                        return FunctionResult.RETURN;
                    }

                    // get water from furniture and add it to watering-can
                    if (!wateringCan.isInfinite()) {
                        PositiveFillMethod[] methods = wateringCan.getPositiveFillMethods();
                        for (PositiveFillMethod method : methods) {
                            if (method.getID().equals(clickedFurnitureID)) {
                                if (method.canFill(state)) {
                                    // fire the event
                                    WateringCanFillEvent fillEvent = new WateringCanFillEvent(player, itemInHand, location, wateringCan, method);
                                    if (EventUtils.fireAndCheckCancel(fillEvent))
                                        return FunctionResult.CANCEL_EVENT_AND_RETURN;

                                    if (waterInCan < wateringCan.getStorage()) {
                                        waterInCan += method.getAmount();
                                        waterInCan = Math.min(waterInCan, wateringCan.getStorage());
                                        state.setArg("{storage}", String.valueOf(wateringCan.getStorage()));
                                        state.setArg("{current}", String.valueOf(waterInCan));
                                        state.setArg("{water_bar}", wateringCan.getWaterBar() == null ? "" : wateringCan.getWaterBar().getWaterBar(waterInCan, wateringCan.getStorage()));
                                        wateringCan.updateItem(player, furnitureWrapper.getItemInHand(), waterInCan, state.getArgs());
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
                    Player player = interactWrapper.getPlayer();
                    ItemStack itemInHand = interactWrapper.getItemInHand();
                    // get the clicked block
                    Block targetBlock = player.getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
                    if (targetBlock == null)
                        return FunctionResult.PASS;
                    // check watering-can requirements
                    State state = new State(player, itemInHand, targetBlock.getLocation());
                    if (!RequirementManager.isRequirementMet(state, wateringCan.getRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // get the exact block id
                    String blockID = customProvider.getBlockID(targetBlock);
                    if (targetBlock.getBlockData() instanceof Waterlogged waterlogged && waterlogged.isWaterlogged()) {
                        blockID = "WATER";
                    }
                    int water = wateringCan.getCurrentWater(itemInHand);
                    PositiveFillMethod[] methods = wateringCan.getPositiveFillMethods();
                    for (PositiveFillMethod method : methods) {
                        if (method.getID().equals(blockID)) {
                            if (method.canFill(state)) {
                                if (water < wateringCan.getStorage()) {
                                    // fire the event
                                    WateringCanFillEvent fillEvent = new WateringCanFillEvent(player, itemInHand, state.getLocation(), wateringCan, method);
                                    if (EventUtils.fireAndCheckCancel(fillEvent))
                                        return FunctionResult.CANCEL_EVENT_AND_RETURN;

                                    water += method.getAmount();
                                    water = Math.min(water, wateringCan.getStorage());
                                    state.setArg("{storage}", String.valueOf(wateringCan.getStorage()));
                                    state.setArg("{current}", String.valueOf(water));
                                    state.setArg("{water_bar}", wateringCan.getWaterBar() == null ? "" : wateringCan.getWaterBar().getWaterBar(water, wateringCan.getStorage()));
                                    wateringCan.updateItem(player, itemInHand, water, state.getArgs());
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

        this.registerItemFunction(itemID, FunctionTrigger.INTERACT_AIR,
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractWrapper interactWrapper)) {
                        return FunctionResult.PASS;
                    }
                    if (wateringCan.isInfinite()) {
                        return FunctionResult.PASS;
                    }
                    Player player = interactWrapper.getPlayer();
                    // get the clicked block
                    Block targetBlock = player.getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
                    if (targetBlock == null)
                        return FunctionResult.PASS;
                    // check watering-can requirements
                    ItemStack itemInHand = interactWrapper.getItemInHand();
                    State state = new State(player, itemInHand, targetBlock.getLocation());
                    if (!RequirementManager.isRequirementMet(state, wateringCan.getRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // get the exact block id
                    String blockID = customProvider.getBlockID(targetBlock);
                    if (targetBlock.getBlockData() instanceof Waterlogged waterlogged && waterlogged.isWaterlogged()) {
                        blockID = "WATER";
                    }
                    int water = wateringCan.getCurrentWater(itemInHand);
                    PositiveFillMethod[] methods = wateringCan.getPositiveFillMethods();
                    for (PositiveFillMethod method : methods) {
                        if (method.getID().equals(blockID)) {
                            if (method.canFill(state)) {
                                if (water < wateringCan.getStorage()) {
                                    // fire the event
                                    WateringCanFillEvent fillEvent = new WateringCanFillEvent(player, itemInHand, state.getLocation(), wateringCan, method);
                                    if (EventUtils.fireAndCheckCancel(fillEvent))
                                        return FunctionResult.CANCEL_EVENT_AND_RETURN;

                                    water += method.getAmount();
                                    water = Math.min(water, wateringCan.getStorage());
                                    state.setArg("{storage}", String.valueOf(wateringCan.getStorage()));
                                    state.setArg("{current}", String.valueOf(water));
                                    state.setArg("{water_bar}", wateringCan.getWaterBar() == null ? "" : wateringCan.getWaterBar().getWaterBar(water, wateringCan.getStorage()));
                                    wateringCan.updateItem(player, itemInHand, water, state.getArgs());
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
                }, CFunction.FunctionPriority.NORMAL)
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
                section.getString("3D-item-with-water"),
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
                        SimpleLocation simpleLocation = SimpleLocation.of(placed);
                        if (plugin.getWorldManager().isReachLimit(simpleLocation, ItemType.SPRINKLER)) {
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
                        plugin.getWorldManager().addSprinklerAt(new MemorySprinkler(simpleLocation, sprinkler.getKey(), 0), simpleLocation);
                        return FunctionResult.PASS;
                    }, CFunction.FunctionPriority.NORMAL)
            );
        }

        this.registerItemFunction(new String[]{sprinkler.get3DItemID(), sprinkler.get3DItemWithWater()}, FunctionTrigger.PLACE,
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
                    SimpleLocation simpleLocation = SimpleLocation.of(location);
                    if (plugin.getWorldManager().isReachLimit(simpleLocation, ItemType.SPRINKLER)) {
                        sprinkler.trigger(ActionTrigger.REACH_LIMIT, state);
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;
                    }
                    // fire event
                    SprinklerPlaceEvent placeEvent = new SprinklerPlaceEvent(player, placeFurnitureWrapper.getItemInHand(), location, sprinkler);
                    if (EventUtils.fireAndCheckCancel(placeEvent)) {
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;
                    }
                    // add data
                    plugin.getWorldManager().addSprinklerAt(new MemorySprinkler(simpleLocation, sprinkler.getKey(), 0), simpleLocation);
                    sprinkler.trigger(ActionTrigger.PLACE, state);
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL)
        );

        this.registerItemFunction(new String[]{sprinkler.get3DItemID(), sprinkler.get3DItemWithWater()}, FunctionTrigger.BE_INTERACTED,
                /*
                 * Interact the sprinkler
                 */
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractWrapper interactWrapper)) {
                        return FunctionResult.PASS;
                    }
                    ItemStack itemInHand = interactWrapper.getItemInHand();
                    Player player = interactWrapper.getPlayer();
                    Location location = interactWrapper.getLocation();
                    // check use requirements
                    State state = new State(player, itemInHand, location);
                    if (!RequirementManager.isRequirementMet(state, sprinkler.getUseRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    SimpleLocation simpleLocation = SimpleLocation.of(location);
                    Optional<WorldSprinkler> optionalSprinkler = plugin.getWorldManager().getSprinklerAt(simpleLocation);
                    if (optionalSprinkler.isEmpty()) {
                        plugin.debug("Found a sprinkler without data interacted by " + player.getName() + " at " + location);
                        WorldSprinkler newSprinkler = new MemorySprinkler(simpleLocation, sprinkler.getKey(), 0);
                        plugin.getWorldManager().addSprinklerAt(newSprinkler, simpleLocation);
                        optionalSprinkler = Optional.of(newSprinkler);
                    } else {
                        if (!optionalSprinkler.get().getKey().equals(sprinkler.getKey())) {
                            LogUtils.warn("Found a sprinkler having inconsistent data interacted by " + player.getName() + " at " + location + ".");
                            plugin.getWorldManager().addSprinklerAt(new MemorySprinkler(simpleLocation, sprinkler.getKey(), 0), simpleLocation);
                            return FunctionResult.RETURN;
                        }
                    }

                    // fire the event
                    SprinklerInteractEvent interactEvent = new SprinklerInteractEvent(player, itemInHand, location, optionalSprinkler.get());
                    if (EventUtils.fireAndCheckCancel(interactEvent)) {
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;
                    }
                    // add water to sprinkler
                    String itemID = getItemID(itemInHand);
                    int itemAmount = itemInHand.getAmount();
                    int waterInSprinkler = optionalSprinkler.get().getWater();
                    // if it's not infinite
                    if (!sprinkler.isInfinite()) {
                        for (PassiveFillMethod method : sprinkler.getPassiveFillMethods()) {
                            if (method.getUsed().equals(itemID) && itemAmount >= method.getUsedAmount()) {
                                if (method.canFill(state)) {
                                    if (waterInSprinkler < sprinkler.getStorage()) {
                                        // fire the event
                                        SprinklerFillEvent fillEvent = new SprinklerFillEvent(player, itemInHand, location, method, optionalSprinkler.get());
                                        if (EventUtils.fireAndCheckCancel(fillEvent))
                                            return FunctionResult.CANCEL_EVENT_AND_RETURN;

                                        if (player.getGameMode() != GameMode.CREATIVE) {
                                            itemInHand.setAmount(itemAmount - method.getUsedAmount());
                                            if (method.getReturned() != null) {
                                                ItemStack returned = getItemStack(player, method.getReturned());
                                                ItemUtils.giveItem(player, returned, method.getReturnedAmount());
                                            }
                                        }
                                        int current = Math.min(waterInSprinkler + method.getAmount(), sprinkler.getStorage());
                                        state.setArg("{storage}", String.valueOf(sprinkler.getStorage()));
                                        state.setArg("{current}", String.valueOf(current));
                                        state.setArg("{water_bar}", sprinkler.getWaterBar() == null ? "" : sprinkler.getWaterBar().getWaterBar(current, sprinkler.getStorage()));
                                        method.trigger(state);
                                        sprinkler.trigger(ActionTrigger.ADD_WATER, state);
                                        plugin.getWorldManager().addWaterToSprinkler(sprinkler, simpleLocation, method.getAmount());
                                    } else {
                                        state.setArg("{storage}", String.valueOf(sprinkler.getStorage()));
                                        state.setArg("{current}", String.valueOf(sprinkler.getStorage()));
                                        state.setArg("{water_bar}", sprinkler.getWaterBar() == null ? "" : sprinkler.getWaterBar().getWaterBar(sprinkler.getStorage(), sprinkler.getStorage()));
                                        sprinkler.trigger(ActionTrigger.FULL, state);
                                        return FunctionResult.CANCEL_EVENT_AND_RETURN;
                                    }
                                }
                                return FunctionResult.RETURN;
                            }
                        }
                    }

                    return FunctionResult.PASS;
                }, CFunction.FunctionPriority.NORMAL)
        );

        this.registerItemFunction(new String[]{sprinkler.get3DItemID(), sprinkler.get3DItemWithWater()}, FunctionTrigger.BE_INTERACTED,
                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractWrapper interactWrapper)) {
                        return FunctionResult.PASS;
                    }

                    Location location = interactWrapper.getLocation();
                    // trigger interact actions
                    plugin.getScheduler().runTaskSyncLater(() -> {
                        State state = new State(interactWrapper.getPlayer(), interactWrapper.getItemInHand(), location);
                        Optional<WorldSprinkler> optionalSprinkler = plugin.getWorldManager().getSprinklerAt(SimpleLocation.of(location));
                        if (optionalSprinkler.isEmpty()) {
                            return;
                        }

                        state.setArg("{storage}", String.valueOf(sprinkler.getStorage()));
                        state.setArg("{current}", String.valueOf(optionalSprinkler.get().getWater()));
                        state.setArg("{water_bar}", sprinkler.getWaterBar() == null ? "" : sprinkler.getWaterBar().getWaterBar(optionalSprinkler.get().getWater(), sprinkler.getStorage()));

                        sprinkler.trigger(ActionTrigger.INTERACT, state);
                    }, location, 1);

                    return FunctionResult.PASS;
                }, CFunction.FunctionPriority.LOWEST)
        );

        this.registerItemFunction(new String[]{sprinkler.get3DItemID(), sprinkler.get3DItemWithWater()}, FunctionTrigger.BREAK,
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
                    SimpleLocation simpleLocation = SimpleLocation.of(location);
                    Optional<WorldSprinkler> optionalSprinkler = plugin.getWorldManager().getSprinklerAt(simpleLocation);
                    if (optionalSprinkler.isEmpty()) {
                        plugin.debug("Found a sprinkler without data broken by " + state.getPlayer().getName() + " at " + location);
                        return FunctionResult.RETURN;
                    }
                    if (!optionalSprinkler.get().getKey().equals(sprinkler.getKey())) {
                        LogUtils.warn("Found a sprinkler having inconsistent data broken by " + state.getPlayer().getName() + " at " + location + ".");
                        plugin.getWorldManager().removeSprinklerAt(simpleLocation);
                        return FunctionResult.RETURN;
                    }
                    // fire event
                    SprinklerBreakEvent breakEvent = new SprinklerBreakEvent(breakFurnitureWrapper.getPlayer(), location, optionalSprinkler.get(), Reason.BREAK);
                    if (EventUtils.fireAndCheckCancel(breakEvent)) {
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;
                    }
                    // remove data
                    plugin.getWorldManager().removeSprinklerAt(simpleLocation);
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
                    Location location = clicked.getLocation();
                    // check fertilizer requirements
                    State state = new State(interactBlockWrapper.getPlayer(), itemInHand, location);
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
                        Optional<WorldCrop> worldCrop = plugin.getWorldManager().getCropAt(SimpleLocation.of(location.clone().add(0,1,0)));
                        if (worldCrop.isPresent()) {
                            fertilizer.trigger(ActionTrigger.BEFORE_PLANT, state);
                            return FunctionResult.RETURN;
                        }
                    }
                    SimpleLocation simpleLocation = SimpleLocation.of(location);
                    Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(simpleLocation);
                    boolean hasWater = false;
                    if (worldPot.isEmpty()) {
                        plugin.debug("Found pot data not exists at " + simpleLocation + ". Fixing it.");
                        MemoryPot memoryPot = new MemoryPot(simpleLocation, pot.getKey());
                        plugin.getWorldManager().addPotAt(memoryPot, simpleLocation);
                        worldPot = Optional.of(memoryPot);
                    } else {
                        hasWater = worldPot.get().getWater() > 0;
                    }
                    // fire the event
                    FertilizerUseEvent useEvent = new FertilizerUseEvent(state.getPlayer(), itemInHand, fertilizer, location, worldPot.get());
                    if (EventUtils.fireAndCheckCancel(useEvent))
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;

                    // add data
                    plugin.getWorldManager().addFertilizerToPot(pot, fertilizer, simpleLocation);
                    if (interactBlockWrapper.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    }
                    fertilizer.trigger(ActionTrigger.USE, state);
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL),

                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractBlockWrapper interactBlockWrapper)) {
                        return FunctionResult.PASS;
                    }
                    // is a crop
                    Block clicked = interactBlockWrapper.getClickedBlock();
                    String id = customProvider.getBlockID(clicked);
                    Crop crop = getCropByStageID(id);
                    if (crop == null && !deadCrops.contains(id)) {
                        return FunctionResult.PASS;
                    }
                    ItemStack itemInHand = interactBlockWrapper.getItemInHand();
                    Location location = clicked.getLocation();
                    Player player = interactBlockWrapper.getPlayer();
                    Location potLocation = location.clone().subtract(0,1,0);
                    // check fertilizer requirements
                    State state = new State(player, itemInHand, potLocation);
                    if (!RequirementManager.isRequirementMet(state, fertilizer.getRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // check pot data
                    Pot pot = getPotByBlock(potLocation.getBlock());
                    if (pot == null) {
                        LogUtils.warn("Found a crop without pot interacted by player " + player.getName() + " with a fertilizer at " + location);
                        customProvider.removeAnythingAt(location);
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
                        fertilizer.trigger(ActionTrigger.BEFORE_PLANT, state);
                        return FunctionResult.RETURN;
                    }

                    SimpleLocation simpleLocation = SimpleLocation.of(potLocation);
                    Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(simpleLocation);
                    boolean hasWater = false;
                    if (worldPot.isEmpty()) {
                        plugin.debug("Found pot data not exists at " + simpleLocation + ". Fixing it.");
                        MemoryPot memoryPot = new MemoryPot(simpleLocation, pot.getKey());
                        plugin.getWorldManager().addPotAt(memoryPot, simpleLocation);
                        worldPot = Optional.of(memoryPot);
                    } else {
                        hasWater = worldPot.get().getWater() > 0;
                    }
                    // fire the event
                    FertilizerUseEvent useEvent = new FertilizerUseEvent(state.getPlayer(), itemInHand, fertilizer, location, worldPot.get());
                    if (EventUtils.fireAndCheckCancel(useEvent))
                        return FunctionResult.CANCEL_EVENT_AND_RETURN;

                    // add data
                    plugin.getWorldManager().addFertilizerToPot(pot, fertilizer, simpleLocation);
                    if (interactBlockWrapper.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    }
                    fertilizer.trigger(ActionTrigger.USE, state);
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL),

                new CFunction(conditionWrapper -> {
                    if (!(conditionWrapper instanceof InteractFurnitureWrapper furnitureWrapper)) {
                        return FunctionResult.PASS;
                    }
                    // is a crop
                    String id = furnitureWrapper.getID();
                    Crop crop = getCropByStageID(id);
                    if (crop == null && !deadCrops.contains(id)) {
                        return FunctionResult.PASS;
                    }
                    ItemStack itemInHand = furnitureWrapper.getItemInHand();
                    Location location = furnitureWrapper.getLocation();
                    Player player = furnitureWrapper.getPlayer();
                    Location potLocation = location.clone().subtract(0,1,0);
                    // check fertilizer requirements
                    State state = new State(player, itemInHand, potLocation);
                    if (!RequirementManager.isRequirementMet(state, fertilizer.getRequirements())) {
                        return FunctionResult.RETURN;
                    }
                    // check pot data
                    Pot pot = getPotByBlock(potLocation.getBlock());
                    if (pot == null) {
                        LogUtils.warn("Found a crop without pot interacted by player " + player.getName() + " with a fertilizer at " + location);
                        customProvider.removeAnythingAt(location);
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
                        fertilizer.trigger(ActionTrigger.BEFORE_PLANT, state);
                        return FunctionResult.RETURN;
                    }

                    SimpleLocation simpleLocation = SimpleLocation.of(potLocation);
                    Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(simpleLocation);
                    boolean hasWater = false;
                    if (worldPot.isEmpty()) {
                        plugin.debug("Found pot data not exists at " + potLocation);
                    } else {
                        hasWater = worldPot.get().getWater() > 0;
                    }

                    // add data
                    plugin.getWorldManager().addFertilizerToPot(pot, fertilizer, simpleLocation);
                    if (furnitureWrapper.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    }
                    fertilizer.trigger(ActionTrigger.USE, state);
                    return FunctionResult.RETURN;
                }, CFunction.FunctionPriority.NORMAL)
        );
    }

    @SuppressWarnings("DuplicatedCode")
    private void loadCrop(String key, ConfigurationSection section) {
        ItemCarrier itemCarrier = ItemCarrier.valueOf(section.getString("type").toUpperCase(Locale.ENGLISH));
        if (itemCarrier != ItemCarrier.TRIPWIRE && itemCarrier != ItemCarrier.ITEM_DISPLAY && itemCarrier != ItemCarrier.ITEM_FRAME && itemCarrier != ItemCarrier.NOTE_BLOCK) {
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

        for (DeathConditions deathConditions : crop.getDeathConditions()) {
            if (deathConditions.getDeathItem() != null) {
                deadCrops.add(deathConditions.getDeathItem());
            }
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
                    if (!customProvider.isAir(clicked.getRelative(BlockFace.UP).getLocation())) {
                        return FunctionResult.RETURN;
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
                    SimpleLocation simpleLocation = SimpleLocation.of(seedLocation);
                    if (plugin.getWorldManager().isReachLimit(simpleLocation, ItemType.CROP)) {
                        crop.trigger(ActionTrigger.REACH_LIMIT, state);
                        return FunctionResult.RETURN;
                    }
                    // fire event
                    CropPlantEvent plantEvent = new CropPlantEvent(player, itemInHand, seedLocation, crop, 0);
                    if (EventUtils.fireAndCheckCancel(plantEvent)) {
                        return FunctionResult.RETURN;
                    }
                    // place the crop
                    this.placeItem(seedLocation, crop.getItemCarrier(), crop.getStageItemByPoint(plantEvent.getPoint()), crop.hasRotation() ? CRotation.RANDOM : CRotation.NONE);

                    // reduce item
                    if (player.getGameMode() != GameMode.CREATIVE)
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    crop.trigger(ActionTrigger.PLANT, state);

                    plugin.getWorldManager().addCropAt(new MemoryCrop(simpleLocation, crop.getKey(), plantEvent.getPoint()), simpleLocation);
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

                            Player player = interactWrapper.getPlayer();
                            Location cropLocation = LocationUtils.toBlockLocation(interactWrapper.getLocation());
                            ItemStack itemInHand = interactWrapper.getItemInHand();
                            State cropState = new State(player, itemInHand, cropLocation);

                            // check crop interact requirements
                            if (!RequirementManager.isRequirementMet(cropState, crop.getInteractRequirements())) {
                                return FunctionResult.RETURN;
                            }
                            if (!RequirementManager.isRequirementMet(cropState, stage.getInteractRequirements())) {
                                return FunctionResult.RETURN;
                            }
                            SimpleLocation simpleLocation = SimpleLocation.of(cropLocation);
                            Optional<WorldCrop> optionalCrop = plugin.getWorldManager().getCropAt(simpleLocation);
                            if (optionalCrop.isEmpty()) {
                                plugin.debug("Found a crop without data interacted by " + player.getName() + " at " + cropLocation);
                                WorldCrop newCrop = new MemoryCrop(simpleLocation, crop.getKey(), stage.getPoint());
                                plugin.getWorldManager().addCropAt(newCrop, simpleLocation);
                                optionalCrop = Optional.of(newCrop);
                            } else {
                                if (!optionalCrop.get().getKey().equals(crop.getKey())) {
                                    LogUtils.warn("Found a crop having inconsistent data interacted by " + player.getName() + " at " + cropLocation + ".");
                                    plugin.getWorldManager().addCropAt(new MemoryCrop(simpleLocation, crop.getKey(), stage.getPoint()), simpleLocation);
                                    return FunctionResult.RETURN;
                                }
                            }

                            CropInteractEvent interactEvent = new CropInteractEvent(conditionWrapper.getPlayer(), interactWrapper.getItemInHand(), cropLocation, optionalCrop.orElse(null));
                            if (EventUtils.fireAndCheckCancel(interactEvent)) {
                                return FunctionResult.CANCEL_EVENT_AND_RETURN;
                            }

                            String itemID = getItemID(itemInHand);
                            int itemAmount = itemInHand.getAmount();

                            Location potLocation = cropLocation.clone().subtract(0,1,0);
                            Pot pot = getPotByBlock(potLocation.getBlock());
                            State potState = new State(player, itemInHand, potLocation);

                            // check pot use requirements
                            if (pot != null && RequirementManager.isRequirementMet(potState, pot.getUseRequirements())) {
                                // get water in pot
                                int waterInPot = plugin.getWorldManager().getPotAt(SimpleLocation.of(potLocation)).map(WorldPot::getWater).orElse(0);
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
                                                plugin.getWorldManager().addWaterToPot(pot, method.getAmount(), SimpleLocation.of(potLocation));
                                            } else {
                                                pot.trigger(ActionTrigger.FULL, potState);
                                            }
                                        }
                                        return FunctionResult.RETURN;
                                    }
                                }
                            }

                            // if not reached the max point, try detecting bone meals
                            if (optionalCrop.get().getPoint() < crop.getMaxPoints()) {
                                for (BoneMeal boneMeal : crop.getBoneMeals()) {
                                    if (boneMeal.getItem().equals(itemID)) {
                                        // fire the event
                                        BoneMealUseEvent useEvent = new BoneMealUseEvent(player, itemInHand, cropLocation, boneMeal, optionalCrop.get());
                                        if (EventUtils.fireAndCheckCancel(useEvent))
                                            return FunctionResult.CANCEL_EVENT_AND_RETURN;

                                        if (player.getGameMode() != GameMode.CREATIVE) {
                                            itemInHand.setAmount(itemAmount - boneMeal.getUsedAmount());
                                            if (boneMeal.getReturned() != null) {
                                                ItemStack returned = getItemStack(player, boneMeal.getReturned());
                                                ItemUtils.giveItem(player, returned, boneMeal.getReturnedAmount());
                                            }
                                        }
                                        boneMeal.trigger(cropState);
                                        plugin.getWorldManager().addPointToCrop(crop, boneMeal.getPoint(), SimpleLocation.of(cropLocation));
                                        return FunctionResult.RETURN;
                                    }
                                }
                            } else {
                                crop.trigger(ActionTrigger.RIPE, cropState);
                            }

                            // trigger interact actions
                            crop.trigger(ActionTrigger.INTERACT, cropState);
                            stage.trigger(ActionTrigger.INTERACT, cropState);
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
                            Location cropLocation = LocationUtils.toBlockLocation(breakWrapper.getLocation());
                            State state = new State(player, breakWrapper.getItemInHand(), cropLocation);
                            // check crop break requirements
                            if (!RequirementManager.isRequirementMet(state, crop.getBreakRequirements())) {
                                return FunctionResult.CANCEL_EVENT_AND_RETURN;
                            }
                            if (!RequirementManager.isRequirementMet(state, stage.getBreakRequirements())) {
                                return FunctionResult.CANCEL_EVENT_AND_RETURN;
                            }
                            SimpleLocation simpleLocation = SimpleLocation.of(cropLocation);
                            Optional<WorldCrop> optionalWorldCrop = plugin.getWorldManager().getCropAt(simpleLocation);
                            if (optionalWorldCrop.isEmpty()) {
                                WorldCrop worldCrop = new MemoryCrop(simpleLocation, crop.getKey(), stage.getPoint());
                                plugin.getWorldManager().addCropAt(worldCrop, simpleLocation);
                                optionalWorldCrop = Optional.of(worldCrop);
                                plugin.debug("Found a crop without data broken by " + player.getName() + " at " + cropLocation + ". " +
                                        "You can safely ignore this if the crop is spawned in the wild.");
                            } else {
                                if (!optionalWorldCrop.get().getKey().equals(crop.getKey())) {
                                    LogUtils.warn("Found a crop having inconsistent data broken by " + player.getName() + " at " + cropLocation + ".");
                                    plugin.getWorldManager().removeCropAt(simpleLocation);
                                    return FunctionResult.RETURN;
                                }
                            }
                            // fire event
                            CropBreakEvent breakEvent = new CropBreakEvent(player, cropLocation, optionalWorldCrop.get(), Reason.BREAK);
                            if (EventUtils.fireAndCheckCancel(breakEvent))
                                return FunctionResult.CANCEL_EVENT_AND_RETURN;
                            // trigger actions
                            stage.trigger(ActionTrigger.BREAK, state);
                            crop.trigger(ActionTrigger.BREAK, state);
                            plugin.getWorldManager().removeCropAt(simpleLocation);
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
                key, storage, section.getBoolean("absorb-rainwater", false), section.getBoolean("absorb-nearby-water", false),
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
                        SimpleLocation simpleLocation = SimpleLocation.of(location);
                        Optional<WorldPot> optionalPot = plugin.getWorldManager().getPotAt(simpleLocation);
                        if (optionalPot.isEmpty()) {
                            plugin.debug("Found a pot without data interacted by " + player.getName() + " at " + location);
                            WorldPot newPot = new MemoryPot(simpleLocation, pot.getKey());
                            if (pot.isWetPot(potItemID)) {
                                newPot.setWater(1);
                            }
                            plugin.getWorldManager().addPotAt(newPot, simpleLocation);
                            optionalPot = Optional.of(newPot);
                        } else {
                            if (!optionalPot.get().getKey().equals(pot.getKey())) {
                                LogUtils.warn("Found a pot having inconsistent data interacted by " + player.getName() + " at " + location + ".");
                                plugin.getWorldManager().addPotAt(new MemoryPot(simpleLocation, pot.getKey()), simpleLocation);
                                return FunctionResult.RETURN;
                            }
                        }

                        // fire the event
                        PotInteractEvent interactEvent = new PotInteractEvent(player, itemInHand, location, optionalPot.get());
                        if (EventUtils.fireAndCheckCancel(interactEvent)) {
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;
                        }
                        String itemID = getItemID(itemInHand);
                        int itemAmount = itemInHand.getAmount();
                        // get water in pot
                        int waterInPot = plugin.getWorldManager().getPotAt(simpleLocation).map(WorldPot::getWater).orElse(0);
                        for (PassiveFillMethod method : pot.getPassiveFillMethods()) {
                            if (method.getUsed().equals(itemID) && itemAmount >= method.getUsedAmount()) {
                                if (method.canFill(state)) {
                                    if (waterInPot < pot.getStorage()) {
                                        // fire the event
                                        PotFillEvent waterEvent = new PotFillEvent(player, itemInHand, location, method, optionalPot.get());
                                        if (EventUtils.fireAndCheckCancel(waterEvent))
                                            return FunctionResult.CANCEL_EVENT_AND_RETURN;

                                        if (player.getGameMode() != GameMode.CREATIVE) {
                                            itemInHand.setAmount(itemAmount - method.getUsedAmount());
                                            if (method.getReturned() != null) {
                                                ItemStack returned = getItemStack(player, method.getReturned());
                                                ItemUtils.giveItem(player, returned, method.getReturnedAmount());
                                            }
                                        }
                                        method.trigger(state);
                                        pot.trigger(ActionTrigger.ADD_WATER, state);
                                        plugin.getWorldManager().addWaterToPot(pot, method.getAmount(), simpleLocation);
                                    } else {
                                        pot.trigger(ActionTrigger.FULL, state);
                                        return FunctionResult.CANCEL_EVENT_AND_RETURN;
                                    }
                                }
                                return FunctionResult.RETURN;
                            }
                        }

                        return FunctionResult.PASS;
                    }, CFunction.FunctionPriority.NORMAL)
            );

            this.registerItemFunction(potItemID, FunctionTrigger.BE_INTERACTED,
                    new CFunction(conditionWrapper -> {
                        if (!(conditionWrapper instanceof InteractBlockWrapper interactBlockWrapper)) {
                            return FunctionResult.PASS;
                        }

                        Location location = interactBlockWrapper.getClickedBlock().getLocation();
                        plugin.getScheduler().runTaskSyncLater(() -> {
                            Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(SimpleLocation.of(location));
                            if (worldPot.isEmpty()) {
                                return;
                            }
                            State state = new State(interactBlockWrapper.getPlayer(), interactBlockWrapper.getItemInHand(), location);
                            state.setArg("{current}", String.valueOf(worldPot.get().getWater()));
                            state.setArg("{storage}", String.valueOf(pot.getStorage()));
                            state.setArg("{water_bar}", pot.getWaterBar() == null ? "" : pot.getWaterBar().getWaterBar(worldPot.get().getWater(), pot.getStorage()));
                            state.setArg("{left_times}", String.valueOf(worldPot.get().getFertilizerTimes()));
                            state.setArg("{max_times}", String.valueOf(Optional.ofNullable(worldPot.get().getFertilizer()).map(fertilizer -> fertilizer.getTimes()).orElse(0)));
                            state.setArg("{icon}", Optional.ofNullable(worldPot.get().getFertilizer()).map(fertilizer -> fertilizer.getIcon()).orElse(""));

                            // trigger actions
                            pot.trigger(ActionTrigger.INTERACT, state);
                        }, location, 1);
                        return FunctionResult.PASS;
                    }, CFunction.FunctionPriority.LOWEST)
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
                        Player player = blockWrapper.getPlayer();
                        State state = new State(player, blockWrapper.getItemInHand(), location);
                        if (!RequirementManager.isRequirementMet(state, pot.getBreakRequirements())) {
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;
                        }
                        Location cropLocation = location.clone().add(0,1,0);
                        String cropStageID = customProvider.getSomethingAt(cropLocation);
                        // remove crops
                        Crop.Stage stage = stage2CropStageMap.get(cropStageID);
                        if (stage != null) {
                            // if crops are above, check the break requirements for crops
                            Crop crop = getCropByStageID(cropStageID);
                            if (crop != null) {
                                State cropState = new State(player, blockWrapper.getItemInHand(), cropLocation);
                                if (!RequirementManager.isRequirementMet(cropState, crop.getBreakRequirements())) {
                                    return FunctionResult.CANCEL_EVENT_AND_RETURN;
                                }
                                if (!RequirementManager.isRequirementMet(cropState, stage.getBreakRequirements())) {
                                    return FunctionResult.CANCEL_EVENT_AND_RETURN;
                                }
                                SimpleLocation simpleLocation = SimpleLocation.of(cropLocation);
                                Optional<WorldCrop> optionalWorldCrop = plugin.getWorldManager().getCropAt(simpleLocation);
                                if (optionalWorldCrop.isPresent()) {
                                    if (!optionalWorldCrop.get().getKey().equals(crop.getKey())) {
                                        LogUtils.warn("Found a crop having inconsistent data broken by " + player.getName() + " at " + cropLocation + ".");
                                    }
                                } else {
                                    WorldCrop worldCrop = new MemoryCrop(simpleLocation, crop.getKey(), stage.getPoint());
                                    optionalWorldCrop = Optional.of(worldCrop);
                                    plugin.getWorldManager().addCropAt(worldCrop, simpleLocation);
                                    plugin.debug("Found a crop without data broken by " + player.getName() + " at " + cropLocation + ". " +
                                            "You can safely ignore this if the crop is spawned in the wild.");
                                }
                                // fire event
                                CropBreakEvent breakEvent = new CropBreakEvent(player, cropLocation, optionalWorldCrop.get(), Reason.BREAK);
                                if (EventUtils.fireAndCheckCancel(breakEvent))
                                    return FunctionResult.CANCEL_EVENT_AND_RETURN;
                                // trigger actions
                                stage.trigger(ActionTrigger.BREAK, cropState);
                                crop.trigger(ActionTrigger.BREAK, cropState);
                                plugin.getWorldManager().removeCropAt(simpleLocation);
                                customProvider.removeAnythingAt(cropLocation);
                            } else {
                                LogUtils.warn("Invalid crop stage: " + cropStageID);
                                customProvider.removeAnythingAt(cropLocation);
                            }
                        }
                        // remove dead crops
                        if (deadCrops.contains(cropStageID)) {
                            customProvider.removeAnythingAt(cropLocation);
                        }

                        SimpleLocation simpleLocation = SimpleLocation.of(location);
                        Optional<WorldPot> optionalPot = plugin.getWorldManager().getPotAt(simpleLocation);
                        if (optionalPot.isEmpty()) {
                            plugin.debug("Found a pot without data broken by " + state.getPlayer().getName() + " at " + simpleLocation);
                            return FunctionResult.RETURN;
                        }
                        if (!optionalPot.get().getKey().equals(pot.getKey())) {
                            plugin.debug("Found a pot having inconsistent data broken by " + state.getPlayer().getName() + " at " + simpleLocation + ".");
                            plugin.getWorldManager().removePotAt(simpleLocation);
                            return FunctionResult.RETURN;
                        }
                        // fire event
                        PotBreakEvent breakEvent = new PotBreakEvent(blockWrapper.getPlayer(), location, optionalPot.get(), Reason.BREAK);
                        if (EventUtils.fireAndCheckCancel(breakEvent)) {
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;
                        }
                        // remove data
                        plugin.getWorldManager().removePotAt(simpleLocation);
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
                        SimpleLocation simpleLocation = SimpleLocation.of(location);
                        if (plugin.getWorldManager().isReachLimit(simpleLocation, ItemType.POT)) {
                            pot.trigger(ActionTrigger.REACH_LIMIT, new State(player, blockWrapper.getItemInHand(), location));
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;
                        }
                        // fire event
                        PotPlaceEvent potPlaceEvent = new PotPlaceEvent(player, location, pot);
                        if (EventUtils.fireAndCheckCancel(potPlaceEvent)) {
                            return FunctionResult.CANCEL_EVENT_AND_RETURN;
                        }
                        // add data
                        plugin.getWorldManager().addPotAt(new MemoryPot(simpleLocation, pot.getKey()), simpleLocation);
                        pot.trigger(ActionTrigger.PLACE, state);
                        return FunctionResult.RETURN;
                    }, CFunction.FunctionPriority.NORMAL));
        }
    }

    private void registerItemFunction(String[] items, FunctionTrigger trigger, CFunction... function) {
        for (String item : items) {
            if (item != null) {
                registerItemFunction(item, trigger, function);
            }
        }
    }

    private void registerItemFunction(String item, FunctionTrigger trigger, CFunction... function) {
        if (item == null) return;
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

    @Override
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
        String itemID = getItemID(condition.getItemInHand());
        Optional.ofNullable(itemID2FunctionMap.get(itemID))
                .map(map -> map.get(FunctionTrigger.INTERACT_AT))
                .ifPresent(itemFunctions -> handleFunctions(itemFunctions, condition, event));
    }

    @Override
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
        String itemID = getItemID(condition.getItemInHand());
        Optional.ofNullable(itemID2FunctionMap.get(itemID))
                .map(map -> map.get(FunctionTrigger.INTERACT_AIR))
                .ifPresent(cFunctions -> handleFunctions(cFunctions, condition, event));
    }

    @Override
    public void handlePlayerBreakBlock(
            Player player,
            Block brokenBlock,
            String blockID,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        // check anti-grief
        if (!antiGrief.canBreak(player, brokenBlock.getLocation()))
            return;

        Optional.ofNullable(itemID2FunctionMap.get(blockID))
                .map(map -> map.get(FunctionTrigger.BREAK))
                .ifPresent(cFunctions -> handleFunctions(cFunctions, new BreakBlockWrapper(player, brokenBlock), event));
    }

    @Override
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
        String itemID = getItemID(condition.getItemInHand());
        Optional.ofNullable(itemID2FunctionMap.get(itemID))
                .map(map -> map.get(FunctionTrigger.INTERACT_AT))
                .ifPresent(cFunctions -> handleFunctions(cFunctions, condition, event));
    }

    @Override
    public void handlePlayerPlaceFurniture(
            Player player,
            Location location,
            String id,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        // check anti-grief
        if (!antiGrief.canPlace(player, location))
            return;

        // check furniture, no need to check item in hand
        Optional.ofNullable(itemID2FunctionMap.get(id))
                .map(map -> map.get(FunctionTrigger.PLACE))
                .ifPresent(cFunctions -> handleFunctions(cFunctions, new PlaceFurnitureWrapper(player, location, id), event));
    }

    @Override
    public void handlePlayerBreakFurniture(
            Player player,
            Location location,
            String id,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        // check anti-grief
        if (!antiGrief.canBreak(player, location))
            return;

        // check furniture, no need to check item in hand
        Optional.ofNullable(itemID2FunctionMap.get(id))
                .map(map -> map.get(FunctionTrigger.BREAK))
                .ifPresent(cFunctions -> handleFunctions(cFunctions, new BreakFurnitureWrapper(player, location, id), event));
    }

    @Override
    public void handlePlayerPlaceBlock(Player player, Block block, String blockID, Cancellable event) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        // check anti-grief
        if (!antiGrief.canPlace(player, block.getLocation()))
            return;

        // check furniture, no need to check item in hand
        Optional.ofNullable(itemID2FunctionMap.get(blockID))
                .map(map -> map.get(FunctionTrigger.PLACE))
                .ifPresent(cFunctions -> handleFunctions(cFunctions, new PlaceBlockWrapper(player, block, blockID), event));
    }

    @Override
    public void handleEntityTramplingBlock(Entity entity, Block block, Cancellable event) {
        if (entity instanceof Player player) {
            handlePlayerBreakBlock(player, block, "FARMLAND", event);
        } else {
            // if the block is a pot
            Pot pot = getPotByBlock(block);
            if (pot != null) {
                Location potLocation = block.getLocation();
                // get or fix pot
                SimpleLocation potSimpleLocation = SimpleLocation.of(potLocation);
                Optional<WorldPot> worldPot = plugin.getWorldManager().getPotAt(potSimpleLocation);
                if (worldPot.isEmpty()) {
                    plugin.debug("Found pot data not exists at " + potSimpleLocation + ". Fixing it.");
                    MemoryPot memoryPot = new MemoryPot(potSimpleLocation, pot.getKey());
                    plugin.getWorldManager().addPotAt(memoryPot, potSimpleLocation);
                    worldPot = Optional.of(memoryPot);
                }
                // fire the event
                PotBreakEvent potBreakEvent = new PotBreakEvent(entity, potLocation, worldPot.get(), Reason.TRAMPLE);
                if (EventUtils.fireAndCheckCancel(potBreakEvent)) {
                    event.setCancelled(true);
                    return;
                }

                plugin.getWorldManager().removePotAt(SimpleLocation.of(block.getLocation()));
                pot.trigger(ActionTrigger.BREAK, new State(null, new ItemStack(Material.AIR), block.getLocation()));

                Location cropLocation = block.getLocation().clone().add(0,1,0);
                String cropStageID = customProvider.getSomethingAt(cropLocation);
                Crop.Stage stage = stage2CropStageMap.get(cropStageID);
                if (stage != null) {
                    Crop crop = getCropByStageID(cropStageID);
                    SimpleLocation simpleLocation = SimpleLocation.of(cropLocation);
                    Optional<WorldCrop> optionalWorldCrop = plugin.getWorldManager().getCropAt(simpleLocation);
                    if (optionalWorldCrop.isPresent()) {
                        if (!optionalWorldCrop.get().getKey().equals(crop.getKey())) {
                            LogUtils.warn("Found a crop having inconsistent data broken by " + entity.getType() + " at " + cropLocation + ".");
                        }
                    } else {
                        WorldCrop worldCrop = new MemoryCrop(simpleLocation, crop.getKey(), stage.getPoint());
                        plugin.getWorldManager().addCropAt(worldCrop, simpleLocation);
                        optionalWorldCrop = Optional.of(worldCrop);
                        plugin.debug("Found a crop without data broken by " + entity.getType() + " at " + cropLocation + ". " +
                                "You can safely ignore this if the crop is spawned in the wild.");
                    }
                    // fire the event
                    CropBreakEvent breakEvent = new CropBreakEvent(entity, cropLocation, optionalWorldCrop.get(), Reason.TRAMPLE);
                    if (EventUtils.fireAndCheckCancel(breakEvent)) {
                        event.setCancelled(true);
                        return;
                    }

                    State state = new State(null, new ItemStack(Material.AIR), cropLocation);
                    // trigger actions
                    stage.trigger(ActionTrigger.BREAK, state);
                    crop.trigger(ActionTrigger.BREAK, state);
                    plugin.getWorldManager().removeCropAt(simpleLocation);
                    customProvider.removeAnythingAt(cropLocation);
                }

                if (deadCrops.contains(cropStageID)) {
                    customProvider.removeAnythingAt(cropLocation);
                }
            }
        }
    }

    @Override
    public void handleExplosion(Entity entity, List<Block> blocks, Cancellable event) {
        List<Location> locationsToRemove = new ArrayList<>();
        List<Location> locationsToRemoveBlock = new ArrayList<>();
        HashSet<Location> blockLocations = new HashSet<>(blocks.stream().map(Block::getLocation).toList());
        List<Location> aboveLocations = new ArrayList<>();
        for (Location location : blockLocations) {
            Optional<CustomCropsBlock> optionalCustomCropsBlock = plugin.getWorldManager().getBlockAt(SimpleLocation.of(location));
            if (optionalCustomCropsBlock.isPresent()) {
                Event customEvent = null;
                CustomCropsBlock customCropsBlock = optionalCustomCropsBlock.get();
                switch (customCropsBlock.getType()) {
                    case POT -> {
                        customEvent = new PotBreakEvent(entity, location, (WorldPot) customCropsBlock, Reason.EXPLODE);
                        Location above = location.clone().add(0,1,0);
                        if (!blockLocations.contains(above)) {
                            aboveLocations.add(above);
                        }
                    }
                    case SPRINKLER -> customEvent = new SprinklerBreakEvent(entity, location, (WorldSprinkler) customCropsBlock, Reason.EXPLODE);
                    case CROP -> customEvent = new CropBreakEvent(entity, location, (WorldCrop) customCropsBlock, Reason.EXPLODE);
                    case GREENHOUSE -> customEvent = new GreenhouseGlassBreakEvent(entity, location, (WorldGlass) customCropsBlock, Reason.EXPLODE);
                    case SCARECROW -> customEvent = new ScarecrowBreakEvent(entity, location, (WorldScarecrow) customCropsBlock, Reason.EXPLODE);
                }
                if (customEvent != null && EventUtils.fireAndCheckCancel(customEvent)) {
                    event.setCancelled(true);
                    return;
                }
                locationsToRemove.add(location);
            }
        }

        for (Location location : aboveLocations) {
            Optional<CustomCropsBlock> optionalCustomCropsBlock = plugin.getWorldManager().getBlockAt(SimpleLocation.of(location));
            if (optionalCustomCropsBlock.isPresent()) {
                CustomCropsBlock customCropsBlock = optionalCustomCropsBlock.get();
                if (customCropsBlock.getType() == ItemType.CROP) {
                    if (EventUtils.fireAndCheckCancel(new CropBreakEvent(entity, location, (WorldCrop) customCropsBlock, Reason.EXPLODE))) {
                        event.setCancelled(true);
                        return;
                    }
                    locationsToRemove.add(location);
                    locationsToRemoveBlock.add(location);
                }
            }
        }

        for (Location location : locationsToRemoveBlock) {
            removeAnythingAt(location);
        }

        for (Location location : locationsToRemove) {
            CustomCropsBlock customCropsBlock = plugin.getWorldManager().removeAnythingAt(SimpleLocation.of(location));
            if (customCropsBlock != null) {
                State state = new State(null, new ItemStack(Material.AIR), location);
                switch (customCropsBlock.getType()) {
                    case POT -> {
                        Pot pot = ((WorldPot) customCropsBlock).getConfig();
                        pot.trigger(ActionTrigger.BREAK, state);
                    }
                    case CROP -> {
                        Crop crop = ((WorldCrop) customCropsBlock).getConfig();
                        Crop.Stage stage = crop.getStageByItemID(crop.getStageItemByPoint(((WorldCrop) customCropsBlock).getPoint()));
                        crop.trigger(ActionTrigger.BREAK, state);
                        stage.trigger(ActionTrigger.BREAK, state);
                    }
                    case SPRINKLER -> {
                        Sprinkler sprinkler = ((WorldSprinkler) customCropsBlock).getConfig();
                        sprinkler.trigger(ActionTrigger.BREAK, state);
                    }
                }
            }
        }
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

    @Override
    public void updatePotState(Location location, Pot pot, boolean hasWater, Fertilizer fertilizer) {
        if (pot.isVanillaBlock()) {
            Block block = location.getBlock();
            if (block.getBlockData() instanceof Farmland farmland) {
                farmland.setMoisture(hasWater ? 7 : 0);
                block.setBlockData(farmland);
                return;
            }
        }
        this.customProvider.placeBlock(
                location,
                pot.getBlockState(
                        hasWater,
                        Optional.ofNullable(fertilizer)
                                .map(Fertilizer::getFertilizerType)
                                .orElse(null)
                )
        );
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
        return potLocations.stream().filter(it -> {
            Pot pot = getPotByBlock(it.getBlock());
            if (pot == null) return false;
            return pot.getKey().equals(potID);
        }).toList();
    }
}

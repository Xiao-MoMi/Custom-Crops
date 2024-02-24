package net.momirealms.customcrops.mechanic.item;

import com.google.common.base.Preconditions;
import com.willfp.eco.core.items.Items;
import com.willfp.ecoskills.stats.Stat;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.common.Property;
import net.momirealms.customcrops.api.common.property.SimpleProperty;
import net.momirealms.customcrops.api.integration.ItemLibrary;
import net.momirealms.customcrops.api.manager.ConfigManager;
import net.momirealms.customcrops.api.manager.ItemManager;
import net.momirealms.customcrops.api.manager.RequirementManager;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.*;
import net.momirealms.customcrops.api.mechanic.item.water.PassiveFillMethod;
import net.momirealms.customcrops.api.mechanic.misc.image.WaterBar;
import net.momirealms.customcrops.api.mechanic.requirement.State;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;
import net.momirealms.customcrops.api.mechanic.world.level.WorldSprinkler;
import net.momirealms.customcrops.api.util.LogUtils;
import net.momirealms.customcrops.mechanic.item.custom.AbstractCustomListener;
import net.momirealms.customcrops.mechanic.item.custom.itemsadder.ItemsAdderListener;
import net.momirealms.customcrops.mechanic.item.custom.itemsadder.ItemsAdderProvider;
import net.momirealms.customcrops.mechanic.item.custom.oraxen.OraxenListener;
import net.momirealms.customcrops.mechanic.item.custom.oraxen.OraxenProvider;
import net.momirealms.customcrops.mechanic.item.function.CFunction;
import net.momirealms.customcrops.mechanic.item.function.FunctionResult;
import net.momirealms.customcrops.mechanic.item.function.wrapper.*;
import net.momirealms.customcrops.api.mechanic.item.water.PositiveFillMethod;
import net.momirealms.customcrops.mechanic.item.impl.PotConfig;
import net.momirealms.customcrops.mechanic.item.impl.SprinklerConfig;
import net.momirealms.customcrops.mechanic.item.impl.WateringCanConfig;
import net.momirealms.customcrops.mechanic.item.impl.fertilizer.*;
import net.momirealms.customcrops.utils.ConfigUtils;
import net.momirealms.customcrops.utils.ItemUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
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
    private final HashMap<String, TreeSet<CFunction>> itemID2FunctionMap;
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
    private final HashMap<String, Fertilizer> id2FertilizerMap;
    private final HashMap<String, Fertilizer> item2FertilizerMap;

    public ItemManagerImpl(CustomCropsPlugin plugin) {
        this.plugin = plugin;
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

    private void loadItems() {
        this.loadWateringCans();
        this.loadPots();
        this.loadSprinklers();
        this.loadFertilizers();
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
        return getSprinklerBy3DItemID(customProvider.getEntityID(entity));
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

    @SuppressWarnings("DuplicatedCode")
    private void loadWateringCans() {
        List<File> files = ConfigUtils.getFilesRecursively(new File(plugin.getDataFolder(), "contents" + File.separator + "watering-cans"));
        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection section) {
                    String itemID = section.getString("item");
                    int capacity = section.getInt("capacity");
                    int width = section.getInt("effective-range.width");
                    int length = section.getInt("effective-range.length");
                    HashSet<String> potWhiteList = new HashSet<>(section.getStringList("pot-whitelist"));
                    HashSet<String> sprinklerWhiteList = new HashSet<>(section.getStringList("sprinkler-whitelist"));
                    boolean hasDynamicLore = section.getBoolean("dynamic-lore.enable", false);
                    List<String> lore = section.getStringList("dynamic-lore.lore");

                    WateringCanConfig wateringCan = new WateringCanConfig(
                            itemID, width,
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
                        LogUtils.warn("Failed to register new watering can: " + entry.getKey() + " due to duplicated entries.");
                        continue;
                    }

                    this.registerItemFunction(itemID,
                            /*
                             * Handle clicking pot with a watering can
                             */
                            new CFunction(conditionWrapper -> {
                                if (!(conditionWrapper instanceof InteractBlockWrapper blockWrapper)) {
                                    return FunctionResult.PASS;
                                }

                                Pot pot = getPotByBlock(blockWrapper.getClickedBlock());
                                if (pot == null) {
                                    return FunctionResult.PASS;
                                }

                                final Player player = blockWrapper.getPlayer();;
                                final ItemStack itemStack = blockWrapper.getItemInHand();
                                final Location clicked = blockWrapper.getClickedBlock().getLocation();
                                State state = new State(player, itemStack, clicked);

                                if (!wateringCan.getPotWhitelist().contains(pot.getKey())) {
                                    wateringCan.trigger(ActionTrigger.WRONG_POT, state);
                                    return FunctionResult.RETURN;
                                }

                                int waterInCan = wateringCan.getCurrentWater(itemStack);
                                if (waterInCan > 0) {
                                    wateringCan.updateItem(itemStack, waterInCan - 1);
                                    wateringCan.trigger(ActionTrigger.CONSUME_WATER, state);
                                    Collection<Location> pots = getPotInRange(clicked, wateringCan.getWidth(), wateringCan.getLength(), player.getLocation().getYaw(), pot.getKey());
                                    for (Location location : pots) {
                                        plugin.getWorldManager().addWaterToPot(pot, SimpleLocation.getByBukkitLocation(location), 1);
                                        pot.trigger(ActionTrigger.ADD_WATER, new State(player, itemStack, location));
                                    }
                                    return FunctionResult.RETURN;
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

                                int waterInCan = wateringCan.getCurrentWater(furnitureWrapper.getItemInHand());

                                String clickedFurnitureID = furnitureWrapper.getID();
                                Sprinkler sprinkler = getSprinklerBy3DItemID(clickedFurnitureID);
                                if (    sprinkler != null
                                        && waterInCan > 0
                                ) {
                                    State state = new State(furnitureWrapper.getPlayer(), furnitureWrapper.getItemInHand(), furnitureWrapper.getLocation());
                                    if (!wateringCan.getSprinklerWhitelist().contains(sprinkler.getKey())) {
                                        wateringCan.trigger(ActionTrigger.WRONG_SPRINKLER, state);
                                        return FunctionResult.RETURN;
                                    }

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
                                    return FunctionResult.RETURN;
                                }

                                PositiveFillMethod[] methods = wateringCan.getPositiveFillMethods();
                                for (PositiveFillMethod method : methods) {
                                    if (method.getId().equals(clickedFurnitureID)) {
                                        State state = new State(furnitureWrapper.getPlayer(), furnitureWrapper.getItemInHand(), furnitureWrapper.getLocation());
                                        if (waterInCan < wateringCan.getStorage()) {
                                            waterInCan += method.getAmount();
                                            waterInCan = Math.min(waterInCan, wateringCan.getStorage());
                                            wateringCan.updateItem(furnitureWrapper.getItemInHand(), waterInCan);
                                            wateringCan.trigger(ActionTrigger.ADD_WATER, state);
                                            method.trigger(state);
                                        } else {
                                            wateringCan.trigger(ActionTrigger.FULL, state);
                                        }
                                        return FunctionResult.RETURN;
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
                                Block targetBlock = interactWrapper.getPlayer().getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
                                if (targetBlock == null)
                                    return FunctionResult.PASS;
                                String blockID = customProvider.getBlockID(targetBlock);
                                if (targetBlock.getBlockData() instanceof Waterlogged waterlogged && waterlogged.isWaterlogged()) {
                                    blockID = "WATER";
                                }
                                int water = wateringCan.getCurrentWater(interactWrapper.getItemInHand());
                                PositiveFillMethod[] methods = wateringCan.getPositiveFillMethods();
                                for (PositiveFillMethod method : methods) {
                                    if (method.getId().equals(blockID)) {
                                        if (water < wateringCan.getStorage()) {
                                            water += method.getAmount();
                                            water = Math.min(water, wateringCan.getStorage());
                                            wateringCan.updateItem(interactWrapper.getItemInHand(), water);
                                            State state = new State(interactWrapper.getPlayer(), interactWrapper.getItemInHand(), targetBlock.getLocation());
                                            wateringCan.trigger(ActionTrigger.ADD_WATER, state);
                                            method.trigger(state);
                                        }
                                        return FunctionResult.RETURN;
                                    }
                                }
                                return FunctionResult.PASS;
                            }, CFunction.FunctionPriority.LOW)
                    );
                }
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void loadSprinklers() {
        List<File> files = ConfigUtils.getFilesRecursively(new File(plugin.getDataFolder(), "contents" + File.separator + "sprinklers"));
        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection section) {
                    int storage = section.getInt("storage", 4);
                    boolean infinite = section.getBoolean("infinite", false);
                    int range = section.getInt("range",1);
                    int water = section.getInt("water", 1);
                    ItemCarrier itemCarrier = ItemCarrier.valueOf(section.getString("type", "ITEM_FRAME").toUpperCase(Locale.ENGLISH));

                    SprinklerConfig sprinkler = new SprinklerConfig(
                            entry.getKey(),
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
                            ConfigUtils.getRequirements(section.getConfigurationSection("requirements"))
                    );

                    if (!this.registerSprinkler(sprinkler)) {
                        LogUtils.warn("Failed to register new sprinkler: " + entry.getKey() + " due to duplicated entries.");
                        continue;
                    }

                    if (sprinkler.get2DItemID() != null) {
                        this.registerItemFunction(sprinkler.get2DItemID(),
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
                                    if (!customProvider.isAir(placed)) {
                                        return FunctionResult.RETURN;
                                    }

                                    State state = new State(interactBlockWrapper.getPlayer(), itemInHand, placed);
                                    if (!RequirementManager.isRequirementMet(state, sprinkler.getRequirements())) {
                                        return FunctionResult.RETURN;
                                    }

                                    if (plugin.getWorldManager().isReachLimit(SimpleLocation.getByBukkitLocation(placed), ItemType.SPRINKLER)) {
                                        sprinkler.trigger(ActionTrigger.REACH_LIMIT, state);
                                        return FunctionResult.RETURN;
                                    }

                                    switch (sprinkler.getItemCarrier()) {
                                        case ITEM_FRAME, ITEM_DISPLAY -> customProvider.placeFurniture(placed, sprinkler.get3DItemID());
                                        case TRIPWIRE -> customProvider.placeBlock(placed, sprinkler.get3DItemID());
                                        default -> {
                                            LogUtils.warn("Unsupported type for sprinkler: " + sprinkler.getItemCarrier().name());
                                            return FunctionResult.RETURN;
                                        }
                                    }
                                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                                    sprinkler.trigger(ActionTrigger.PLACE, state);
                                    return FunctionResult.PASS;
                                }, CFunction.FunctionPriority.NORMAL)
                        );
                    }

                    this.registerItemFunction(sprinkler.get3DItemID(),
                            /*
                             * Dispose of used items and add water to sprinklers
                             */
                            new CFunction(conditionWrapper -> {
                                if (!(conditionWrapper instanceof InteractFurnitureWrapper interactFurnitureWrapper)) {
                                    return FunctionResult.PASS;
                                }
                                ItemStack itemInHand = interactFurnitureWrapper.getItemInHand();
                                String itemID = getItemID(itemInHand);
                                int itemAmount = itemInHand.getAmount();
                                Player player = interactFurnitureWrapper.getPlayer();

                                for (PassiveFillMethod method : sprinkler.getPassiveFillMethods()) {
                                    if (method.getUsed().equals(itemID) && itemAmount >= method.getUsedAmount()) {
                                        itemInHand.setAmount(itemAmount - method.getUsedAmount());
                                        if (method.getReturned() != null) {
                                            ItemStack returned = getItemStack(player, method.getReturned());
                                            ItemUtils.giveItem(player, returned, method.getReturnedAmount());
                                        }
                                        method.trigger(new State(player, itemInHand, interactFurnitureWrapper.getLocation()));
                                        return FunctionResult.RETURN;
                                    }
                                }
                                return FunctionResult.PASS;
                            }, CFunction.FunctionPriority.NORMAL),
                            /*
                             * This will only trigger if the sprinkler has only 3D items
                             */
                            new CFunction(conditionWrapper -> {
                                if (!(conditionWrapper instanceof PlaceFurnitureWrapper placeFurnitureWrapper)) {
                                    return FunctionResult.PASS;
                                }
                                Location location = placeFurnitureWrapper.getLocation();
                                State state = new State(placeFurnitureWrapper.getPlayer(), placeFurnitureWrapper.getItemInHand(), location);
                                if (!RequirementManager.isRequirementMet(state, sprinkler.getRequirements())) {
                                    return FunctionResult.CANCEL_EVENT_AND_RETURN;
                                }

                                if (plugin.getWorldManager().isReachLimit(SimpleLocation.getByBukkitLocation(location), ItemType.SPRINKLER)) {
                                    sprinkler.trigger(ActionTrigger.REACH_LIMIT, state);
                                    return FunctionResult.RETURN;
                                }

                                sprinkler.trigger(ActionTrigger.PLACE, state);
                                return FunctionResult.RETURN;
                            }, CFunction.FunctionPriority.NORMAL),
                            /*
                             * Handle breaking sprinklers
                             */
                            new CFunction(conditionWrapper -> {
                                if (!(conditionWrapper instanceof BreakFurnitureWrapper breakFurnitureWrapper)) {
                                    return FunctionResult.PASS;
                                }
                                plugin.getWorldManager().removeSprinklerAt(SimpleLocation.getByBukkitLocation(breakFurnitureWrapper.getLocation()));
                                sprinkler.trigger(ActionTrigger.BREAK, new State(breakFurnitureWrapper.getPlayer(), breakFurnitureWrapper.getItemInHand(), breakFurnitureWrapper.getLocation()));
                                return FunctionResult.RETURN;
                            }, CFunction.FunctionPriority.NORMAL)
                    );
                }
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void loadFertilizers() {
        List<File> files = ConfigUtils.getFilesRecursively(new File(plugin.getDataFolder(), "contents" + File.separator + "fertilizers"));
        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection section) {
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
                        continue;
                    }

                    String icon = section.getString("icon", "");
                    int times = section.getInt("times", 14);
                    String itemID = section.getString("item");
                    HashSet<String> potWhitelist = new HashSet<>(section.getStringList("pot-whitelist"));
                    boolean beforePlant = section.getBoolean("before-plant", false);

                    Fertilizer fertilizer;
                    switch (type) {
                        case QUALITY -> fertilizer = new QualityCropConfig(
                                entry.getKey(), itemID, times,
                                section.getDouble("chance", 1), type, potWhitelist,
                                beforePlant, icon,
                                ConfigUtils.getRequirements(section.getConfigurationSection("requirements")),
                                ConfigUtils.getQualityRatio(Preconditions.checkNotNull(section.getString("ratio"), "Quality ratio should not be null")),
                                ConfigUtils.getActionMap(section.getConfigurationSection("events"))
                        );
                        case VARIATION -> fertilizer = new VariationConfig(entry.getKey(), itemID, times,
                                section.getDouble("chance", 1), type, potWhitelist,
                                beforePlant, icon,
                                ConfigUtils.getRequirements(section.getConfigurationSection("requirements")),
                                ConfigUtils.getActionMap(section.getConfigurationSection("events"))
                        );
                        case SOIL_RETAIN -> fertilizer = new SoilRetainConfig(entry.getKey(), itemID, times,
                                section.getDouble("chance", 1), type, potWhitelist,
                                beforePlant, icon,
                                ConfigUtils.getRequirements(section.getConfigurationSection("requirements")),
                                ConfigUtils.getActionMap(section.getConfigurationSection("events"))
                        );
                        case YIELD_INCREASE -> fertilizer = new YieldIncreaseConfig(entry.getKey(), itemID, times,
                                type, potWhitelist,
                                beforePlant, icon,
                                ConfigUtils.getRequirements(section.getConfigurationSection("requirements")),
                                ConfigUtils.getIntChancePair(section.getConfigurationSection("chance")),
                                ConfigUtils.getActionMap(section.getConfigurationSection("events"))
                        );
                        case SPEED_GROW -> fertilizer = new SpeedGrowConfig(entry.getKey(), itemID, times,
                                type, potWhitelist,
                                beforePlant, icon,
                                ConfigUtils.getRequirements(section.getConfigurationSection("requirements")),
                                ConfigUtils.getIntChancePair(section.getConfigurationSection("chance")),
                                ConfigUtils.getActionMap(section.getConfigurationSection("events"))
                        );
                        default -> fertilizer = null;
                    }

                    if (!registerFertilizer(fertilizer)) {
                        LogUtils.warn("Failed to register new fertilizer: " + entry.getKey() + " due to duplicated entries.");
                        continue;
                    }

                    this.registerItemFunction(fertilizer.getItemID(),
                            /*
                             * Processing logic for players to use fertilizer
                             */
                            new CFunction(conditionWrapper -> {
                                if (!(conditionWrapper instanceof InteractBlockWrapper interactBlockWrapper)) {
                                    return FunctionResult.PASS;
                                }
                                Block clicked = interactBlockWrapper.getClickedBlock();
                                Pot pot = getPotByBlock(clicked);
                                if (pot == null) {
                                    return FunctionResult.PASS;
                                }

                                ItemStack itemInHand = interactBlockWrapper.getItemInHand();
                                State state = new State(interactBlockWrapper.getPlayer(), itemInHand, clicked.getLocation());
                                if (!fertilizer.getPotWhitelist().contains(pot.getKey())) {
                                    fertilizer.trigger(ActionTrigger.WRONG_POT, state);
                                    return FunctionResult.RETURN;
                                }

                                if (fertilizer.isBeforePlant()) {
                                    Optional<WorldCrop> worldCrop = plugin.getWorldManager().getCropAt(SimpleLocation.getByBukkitLocation(clicked.getLocation().clone().add(0,1,0)));
                                    if (worldCrop.isPresent()) {
                                        fertilizer.trigger(ActionTrigger.BEFORE_PLANT, state);
                                        return FunctionResult.RETURN;
                                    }
                                }

                                if (!RequirementManager.isRequirementMet(state, fertilizer.getRequirements())) {
                                    return FunctionResult.RETURN;
                                }

                                plugin.getWorldManager().addFertilizerToPot(pot, fertilizer, SimpleLocation.getByBukkitLocation(clicked.getLocation()));
                                itemInHand.setAmount(itemInHand.getAmount() - 1);
                                fertilizer.trigger(ActionTrigger.USE, state);
                                return FunctionResult.RETURN;
                            }, CFunction.FunctionPriority.NORMAL)
                    );
                }
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void loadCrops() {
        List<File> files = ConfigUtils.getFilesRecursively(new File(plugin.getDataFolder(), "contents" + File.separator + "crops"));
        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection section) {

                }
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void loadPots() {
        List<File> files = ConfigUtils.getFilesRecursively(new File(plugin.getDataFolder(), "contents" + File.separator + "pots"));
        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection section) {
                    int storage = section.getInt("max-water-storage", 1);
                    String dryModel = Preconditions.checkNotNull(section.getString("base.dry"), "base.dry should not be null");
                    String wetModel = Preconditions.checkNotNull(section.getString("base.wet"), "base.wet should not be null");
                    boolean enableFertilizedAppearance = section.getBoolean("fertilized-pots.enable", false);

                    PotConfig pot = new PotConfig(
                            entry.getKey(),
                            storage,
                            dryModel,
                            wetModel,
                            enableFertilizedAppearance,
                            enableFertilizedAppearance ? ConfigUtils.getFertilizedPotMap(section.getConfigurationSection("fertilized-pots")) : new HashMap<>(),
                            section.contains("water-bar") ? WaterBar.of(
                                    section.getString("water-bar.left", ""),
                                    section.getString("water-bar.empty", ""),
                                    section.getString("water-bar.full", ""),
                                    section.getString("water-bar.right", "")
                            ) : null,
                            ConfigUtils.getPassiveFillMethods(section.getConfigurationSection("fill-method")),
                            ConfigUtils.getActionMap(section.getConfigurationSection("events"))
                    );

                    if (!this.registerPot(pot)) {
                        LogUtils.warn("Failed to register new pot: " + entry.getKey() + " due to duplicated entries.");
                        continue;
                    }

                    for (String potItemID : pot.getPotBlocks()) {
                        this.registerItemFunction(potItemID,
                                /*
                                 * Disposal of used items to water pots
                                 */
                                new CFunction(conditionWrapper -> {
                                    if (!(conditionWrapper instanceof InteractBlockWrapper interactBlockWrapper)) {
                                        return FunctionResult.PASS;
                                    }

                                    ItemStack itemInHand = interactBlockWrapper.getItemInHand();
                                    String itemID = getItemID(itemInHand);
                                    int itemAmount = itemInHand.getAmount();
                                    Player player = interactBlockWrapper.getPlayer();

                                    for (PassiveFillMethod method : pot.getPassiveFillMethods()) {
                                        if (method.getUsed().equals(itemID) && itemAmount >= method.getUsedAmount()) {
                                            itemInHand.setAmount(itemAmount - method.getUsedAmount());
                                            if (method.getReturned() != null) {
                                                ItemStack returned = getItemStack(player, method.getReturned());
                                                ItemUtils.giveItem(player, returned, method.getReturnedAmount());
                                            }
                                            method.trigger(new State(player, itemInHand, interactBlockWrapper.getClickedBlock().getLocation()));
                                            return FunctionResult.RETURN;
                                        }
                                    }
                                    return FunctionResult.PASS;
                                }, CFunction.FunctionPriority.NORMAL),
                                /*
                                 * Break the pot
                                 */
                                new CFunction(conditionWrapper -> {
                                    if (!(conditionWrapper instanceof BreakBlockWrapper blockWrapper)) {
                                        return FunctionResult.PASS;
                                    }
                                    plugin.getWorldManager().removePotAt(SimpleLocation.getByBukkitLocation(blockWrapper.getBrokenBlock().getLocation()));
                                    return FunctionResult.RETURN;
                                }, CFunction.FunctionPriority.NORMAL)
                        );
                    }
                }
            }
        }
    }

    private void registerItemFunction(String item, CFunction... function) {
        if (itemID2FunctionMap.containsKey(item)) {
            TreeSet<CFunction> previous = itemID2FunctionMap.get(item);
            previous.addAll(List.of(function));
        } else {
            TreeSet<CFunction> list = new TreeSet<>(List.of(function));
            itemID2FunctionMap.put(item, list);
        }
    }

    public void handlePlayerInteractBlock(
            Player player,
            ItemStack itemInHand,
            Block clickedBlock,
            BlockFace clickedFace,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        String blockID = customProvider.getBlockID(clickedBlock);
        TreeSet<CFunction> blockFunctions = itemID2FunctionMap.get(blockID);
        if (blockFunctions != null) {
            var condition = new InteractBlockWrapper(player, itemInHand, clickedBlock, clickedFace);
            handleFunctions(blockFunctions, condition, event);
        }

        String itemID = customProvider.getItemID(itemInHand);
        TreeSet<CFunction> itemFunctions = itemID2FunctionMap.get(itemID);
        if (itemFunctions != null) {
            var condition = new InteractBlockWrapper(player, itemInHand, clickedBlock, clickedFace);
            handleFunctions(itemFunctions, condition, event);
        }
    }

    public void handlePlayerInteractAir(
            Player player,
            ItemStack itemInHand,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        String itemID = customProvider.getItemID(itemInHand);
        TreeSet<CFunction> itemFunctions = itemID2FunctionMap.get(itemID);
        if (itemFunctions != null) {
            var condition = new InteractWrapper(player, itemInHand);
            handleFunctions(itemFunctions, condition, event);
        }
    }

    public void handlePlayerBreakBlock(
            Player player,
            ItemStack itemInHand,
            Block brokenBlock,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        String blockID = customProvider.getBlockID(brokenBlock);
        TreeSet<CFunction> functions = itemID2FunctionMap.get(blockID);
        if (functions == null)
            return;

        var condition = new BreakBlockWrapper(player, itemInHand, brokenBlock);
        handleFunctions(functions, condition, event);
    }

    public void handlePlayerInteractFurniture(
            Player player,
            Location location,
            String id,
            Entity baseEntity,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        TreeSet<CFunction> functions = itemID2FunctionMap.get(id);
        if (functions == null)
            return;

        var condition = new InteractFurnitureWrapper(player, player.getInventory().getItemInMainHand(), location, id, baseEntity);
        handleFunctions(functions, condition, event);
    }

    public void handlePlayerPlaceFurniture(
            Player player,
            Location location,
            String id,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        TreeSet<CFunction> functions = itemID2FunctionMap.get(id);
        if (functions == null)
            return;

        var condition = new PlaceFurnitureWrapper(player, player.getInventory().getItemInMainHand(), location, id);
        handleFunctions(functions, condition, event);
    }

    public void handlePlayerBreakFurniture(
            Player player,
            Location location,
            String id,
            Cancellable event
    ) {
        if (!plugin.getWorldManager().isMechanicEnabled(player.getWorld()))
            return;

        TreeSet<CFunction> functions = itemID2FunctionMap.get(id);
        if (functions == null)
            return;

        var condition = new BreakFurnitureWrapper(player, player.getInventory().getItemInMainHand(), location, id);
        handleFunctions(functions, condition, event);
    }

    private void handleFunctions(Collection<CFunction> functions, ConditionWrapper wrapper, @Nullable Cancellable event) {
        for (CFunction function : functions) {
            FunctionResult result = function.apply(wrapper);
            if (    result == FunctionResult.CANCEL_EVENT ||
                    result == FunctionResult.CANCEL_EVENT_AND_RETURN)
                if (event != null) event.setCancelled(true);
            if (    result == FunctionResult.RETURN ||
                    result == FunctionResult.CANCEL_EVENT_AND_RETURN)
                break;
        }
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

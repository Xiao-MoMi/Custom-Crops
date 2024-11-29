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

package net.momirealms.customcrops.bukkit.item;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.momirealms.antigrieflib.AntiGriefLib;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.core.*;
import net.momirealms.customcrops.api.core.block.BreakReason;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.item.CustomCropsItem;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedBreakEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractAirEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedPlaceEvent;
import net.momirealms.customcrops.api.integration.ExternalProvider;
import net.momirealms.customcrops.api.integration.ItemProvider;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.api.util.LocationUtils;
import net.momirealms.customcrops.api.util.PluginUtils;
import net.momirealms.customcrops.common.item.Item;
import org.bukkit.Registry;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class BukkitItemManager extends AbstractItemManager {

    private final BukkitCustomCropsPlugin plugin;
    private CustomItemProvider provider;
    private AbstractCustomEventListener eventListener;
    private final HashMap<String, ItemProvider> itemProviders = new HashMap<>();
    private ItemProvider[] itemDetectArray = new ItemProvider[0];
    private final BukkitItemFactory factory;
    private AntiGriefLib antiGriefLib;

    public BukkitItemManager(BukkitCustomCropsPlugin plugin) {
        this.plugin = plugin;
        try {
            this.hookDefaultPlugins();
        } catch (ReflectiveOperationException e) {
            plugin.getPluginLogger().warn("Failed to load CustomItemProvider", e);
        }
        if (this.provider == null) {
            plugin.getPluginLogger().warn("ItemsAdder/Oraxen/Nexo/MythicCrucible are not installed. You can safely ignore this if you implemented the custom item interface with API.");
        }
        this.factory = BukkitItemFactory.create(plugin);
    }

    public void setAntiGriefLib(AntiGriefLib antiGriefLib) {
        this.antiGriefLib = antiGriefLib;
    }

    @Override
    public void load() {
        this.resetItemDetectionOrder();
        for (ItemProvider provider : itemProviders.values()) {
            plugin.debug(() -> "Registered ItemProvider: " + provider.identifier());
        }
        plugin.debug(() -> "Item order: " + Arrays.toString(Arrays.stream(itemDetectArray).map(ExternalProvider::identifier).toList().toArray(new String[0])));
    }

    @Override
    public void setCustomEventListener(@NotNull AbstractCustomEventListener listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        if (this.eventListener != null) {
            HandlerList.unregisterAll(this.eventListener);
        }
        this.eventListener = listener;
        Bukkit.getPluginManager().registerEvents(this.eventListener, plugin.getBootstrap());
        plugin.debug(() -> "Custom event listener set to " + listener.getClass().getName());
    }

    @Override
    public void setCustomItemProvider(@NotNull CustomItemProvider provider) {
        Objects.requireNonNull(provider, "provider cannot be null");
        this.provider = provider;
        plugin.debug(() -> "Custom item provider set to " + provider.getClass().getName());
    }

    public boolean registerItemProvider(ItemProvider item) {
        if (itemProviders.containsKey(item.identifier())) return false;
        itemProviders.put(item.identifier(), item);
        this.resetItemDetectionOrder();
        return true;
    }

    public boolean unregisterItemProvider(String id) {
        boolean success = itemProviders.remove(id) != null;
        if (success)
            this.resetItemDetectionOrder();
        return success;
    }

    private void resetItemDetectionOrder() {
        ArrayList<ItemProvider> list = new ArrayList<>();
        for (String plugin : ConfigManager.itemDetectOrder()) {
            ItemProvider provider = itemProviders.get(plugin);
            if (provider != null)
                list.add(provider);
        }
        this.itemDetectArray = list.toArray(new ItemProvider[0]);
    }

    private void hookDefaultPlugins() throws ReflectiveOperationException {
        if (PluginUtils.isEnabled("Oraxen")) {
            String rVersion;
            if (PluginUtils.getPluginVersion("Oraxen").startsWith("2")) {
                rVersion = "r2";
            } else {
                rVersion = "r1";
            }
            Class<?> oraxenProviderClass = Class.forName("net.momirealms.customcrops.bukkit.integration.custom.oraxen_" + rVersion + ".OraxenProvider");
            Constructor<?> oraxenProviderConstructor = oraxenProviderClass.getDeclaredConstructor();
            oraxenProviderConstructor.setAccessible(true);
            this.provider = (CustomItemProvider) oraxenProviderConstructor.newInstance();

            Class<?> oraxenListenerClass = Class.forName("net.momirealms.customcrops.bukkit.integration.custom.oraxen_" + rVersion + ".OraxenListener");
            Constructor<?> oraxenListenerConstructor = oraxenListenerClass.getDeclaredConstructor(AbstractItemManager.class);
            oraxenListenerConstructor.setAccessible(true);
            this.setCustomEventListener((AbstractCustomEventListener) oraxenListenerConstructor.newInstance(this));

            plugin.getPluginLogger().info("Oraxen hooked!");
        } else if (PluginUtils.isEnabled("Nexo")) {
            String rVersion = "r1";
            Class<?> nexoProviderClass = Class.forName("net.momirealms.customcrops.bukkit.integration.custom.nexo_" + rVersion + ".NexoProvider");
            Constructor<?> nexoProviderConstructor = nexoProviderClass.getDeclaredConstructor();
            nexoProviderConstructor.setAccessible(true);
            this.provider = (CustomItemProvider) nexoProviderConstructor.newInstance();

            Class<?> nexoListenerClass = Class.forName("net.momirealms.customcrops.bukkit.integration.custom.nexo_" + rVersion + ".NexoListener");
            Constructor<?> nexoListenerConstructor = nexoListenerClass.getDeclaredConstructor(AbstractItemManager.class);
            nexoListenerConstructor.setAccessible(true);
            this.setCustomEventListener((AbstractCustomEventListener) nexoListenerConstructor.newInstance(this));

            plugin.getPluginLogger().info("Nexo hooked!");
        } else if (PluginUtils.isEnabled("ItemsAdder")) {
            String rVersion = "r1";
            Class<?> itemsAdderProviderClass = Class.forName("net.momirealms.customcrops.bukkit.integration.custom.itemsadder_" + rVersion + ".ItemsAdderProvider");
            Constructor<?> itemsAdderProviderConstructor = itemsAdderProviderClass.getDeclaredConstructor();
            itemsAdderProviderConstructor.setAccessible(true);
            this.provider = (CustomItemProvider) itemsAdderProviderConstructor.newInstance();

            Class<?> itemsAdderListenerClass = Class.forName("net.momirealms.customcrops.bukkit.integration.custom.itemsadder_" + rVersion + ".ItemsAdderListener");
            Constructor<?> itemsAdderListenerConstructor = itemsAdderListenerClass.getDeclaredConstructor(AbstractItemManager.class);
            itemsAdderListenerConstructor.setAccessible(true);
            this.setCustomEventListener((AbstractCustomEventListener) itemsAdderListenerConstructor.newInstance(this));

            plugin.getPluginLogger().info("ItemsAdder hooked!");
        } else if (PluginUtils.isEnabled("MythicCrucible")) {
            String rVersion = "r1";
            Class<?> crucibleProviderClass = Class.forName("net.momirealms.customcrops.bukkit.integration.custom.crucible_" + rVersion + ".CrucibleProvider");
            Constructor<?> crucibleProviderConstructor = crucibleProviderClass.getDeclaredConstructor();
            crucibleProviderConstructor.setAccessible(true);
            this.provider = (CustomItemProvider) crucibleProviderConstructor.newInstance();

            Class<?> crucibleListenerClass = Class.forName("net.momirealms.customcrops.bukkit.integration.custom.crucible_" + rVersion + ".CrucibleListener");
            Constructor<?> crucibleListenerConstructor = crucibleListenerClass.getDeclaredConstructor(AbstractItemManager.class, crucibleProviderClass);
            crucibleListenerConstructor.setAccessible(true);
            this.setCustomEventListener((AbstractCustomEventListener) crucibleListenerConstructor.newInstance(this, this.provider));

            plugin.getPluginLogger().info("MythicCrucible hooked!");
        }
    }

    @Override
    public void place(@NotNull Location location, @NotNull ExistenceForm form, @NotNull String id, FurnitureRotation rotation) {
        switch (form) {
            case BLOCK -> placeBlock(location, id);
            case FURNITURE -> placeFurniture(location, id, rotation);
            case ANY -> throw new IllegalArgumentException("Invalid existence form: " + form);
        }
    }

    @NotNull
    @Override
    public FurnitureRotation remove(@NotNull Location location, @NotNull ExistenceForm form) {
        switch (form) {
            case BLOCK -> {
                this.removeBlock(location);
                return FurnitureRotation.NONE;
            }
            case FURNITURE -> {
                return this.removeFurniture(location);
            }
            case ANY -> {
                this.removeBlock(location);
                return this.removeFurniture(location);
            }
        }
        return FurnitureRotation.NONE;
    }

    @Override
    public void placeBlock(@NotNull Location location, @NotNull String id) {
        if (id.startsWith("minecraft:")) {
            location.getWorld().getBlockAt(location).setBlockData(Bukkit.createBlockData(id), false);
        } else {
            this.provider.placeCustomBlock(location, id);
        }
    }

    @Override
    public void placeFurniture(@NotNull Location location, @NotNull String id, FurnitureRotation rotation) {
        Entity entity = this.provider.placeFurniture(location, id);
        if (rotation == FurnitureRotation.NONE) return;
        if (entity != null) {
            if (entity instanceof ItemFrame itemFrame) {
                itemFrame.setRotation(rotation.getBukkitRotation());
            } else if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setRotation(rotation.getYaw(), 0);
            }
        }
    }

    @Override
    public void removeBlock(@NotNull Location location) {
        if (!this.provider.removeCustomBlock(location)) {
            location.getBlock().setType(Material.AIR, false);
        }
    }

    @NotNull
    @Override
    public FurnitureRotation removeFurniture(@NotNull Location location) {
        Collection<Entity> entities = location.getWorld().getNearbyEntities(LocationUtils.toSurfaceCenterLocation(location), 0.5,0.25,0.5);
        FurnitureRotation rotation = null;
        for (Entity entity : entities) {
            if (this.provider.removeFurniture(entity) && rotation == null) {
                if (entity instanceof ItemFrame itemFrame) {
                    rotation = FurnitureRotation.getByRotation(itemFrame.getRotation());
                } else {
                    rotation = FurnitureRotation.getByYaw(entity.getYaw());
                }
            }
        }
        return rotation == null ? FurnitureRotation.NONE : rotation;
    }

    @NotNull
    @Override
    public String blockID(@NotNull Block block) {
        String id = this.provider.blockID(block);
        if (id == null) {
            id = block.getBlockData().getAsString();
        }
        return id;
    }

    @Nullable
    @Override
    public String furnitureID(@NotNull Entity entity) {
        return this.provider.furnitureID(entity);
    }

    @Override
    @NotNull
    public String entityID(@NotNull Entity entity) {
        String id = furnitureID(entity);
        if (id == null) {
            id = entity.getType().toString();
        }
        return id;
    }

    @Override
    @Nullable
    public String furnitureID(Location location) {
        Collection<Entity> entities = location.getWorld().getNearbyEntities(LocationUtils.toSurfaceCenterLocation(location), 0.5,0.25,0.5);
        for (Entity entity : entities) {
            if (provider.isFurniture(entity)) {
                return provider.furnitureID(entity);
            }
        }
        return null;
    }

    @NotNull
    @Override
    public String anyID(Location location) {
        Block block = location.getBlock();
        if (block.getType() == Material.AIR) {
            String id = furnitureID(location);
            if (id == null) {
                return "AIR";
            }
            return id;
        } else {
            return blockID(location);
        }
    }

    @Override
    public @Nullable String id(Location location, ExistenceForm form) {
        return switch (form) {
            case BLOCK -> blockID(location);
            case FURNITURE -> furnitureID(location);
            case ANY -> anyID(location);
        };
    }

    @NotNull
    @Override
    public String id(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return "AIR";
        String id = provider.itemID(itemStack);
        if (id != null) return id;
        plugin.debug(() -> "Start checking ID from external plugins");
        for (ItemProvider p : itemDetectArray) {
            plugin.debug(p::identifier);
            id = p.itemID(itemStack);
            if (id != null) return p.identifier() + ":" + id;
        }
        return itemStack.getType().name();
    }

    @Nullable
    @Override
    public ItemStack build(Player player, @NotNull String id) {
        ItemStack itemStack = provider.itemStack(player, id);
        if (itemStack != null) {
            return itemStack;
        }
        if (!id.contains(":")) {
            try {
                return new ItemStack(Material.valueOf(id.toUpperCase(Locale.ENGLISH)));
            } catch (IllegalArgumentException e) {
                Material material = Registry.MATERIAL.get(new NamespacedKey("minecraft", id.toLowerCase(Locale.ENGLISH)));
                if (material != null) {
                    return new ItemStack(material);
                }
                plugin.getPluginLogger().warn("Item " + id + " not exists", e);
                return new ItemStack(Material.PAPER);
            }
        } else {
            String[] split = id.split(":", 2);
            ItemProvider provider = requireNonNull(itemProviders.get(split[0]), "Item provider: " + split[0] + " not found");
            return requireNonNull(provider.buildItem(player, split[1]), "Item: " + split[0] + " not found");
        }
    }

    @Override
    public Item<ItemStack> wrap(@NotNull ItemStack itemStack) {
        return factory.wrap(itemStack);
    }

    @Override
    public void decreaseDamage(Player player, ItemStack itemStack, int amount) {
        if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getAmount() == 0)
            return;
        Item<ItemStack> wrapped = factory.wrap(itemStack);
        if (wrapped.unbreakable()) return;
        wrapped.damage(Math.max(0, wrapped.damage().orElse(0) - amount));
        wrapped.load();
    }

    @Override
    public void increaseDamage(Player player, ItemStack itemStack, int amount) {
        if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getAmount() == 0)
            return;
        Item<ItemStack> wrapped = factory.wrap(itemStack);
        if (wrapped.unbreakable())
            return;
        ItemMeta previousMeta = itemStack.getItemMeta().clone();
        PlayerItemDamageEvent itemDamageEvent = new PlayerItemDamageEvent(player, itemStack, amount);
        if (EventUtils.fireAndCheckCancel(itemDamageEvent)) {
            plugin.debug(() -> "Another plugin modified the item from `PlayerItemDamageEvent` called by CustomCrops");
            return;
        }
        if (!itemStack.getItemMeta().equals(previousMeta)) {
            return;
        }
        int damage = wrapped.damage().orElse(0);
        if (damage + amount >= wrapped.maxDamage().orElse((int) itemStack.getType().getMaxDurability())) {
            plugin.getSenderFactory().getAudience(player).playSound(Sound.sound(Key.key("minecraft:entity.item.break"), Sound.Source.PLAYER, 1, 1));
            itemStack.setAmount(0);
            return;
        }
        wrapped.damage(damage + amount);
        wrapped.load();
    }

    @Override
    public void handlePlayerInteractAir(Player player, EquipmentSlot hand, ItemStack itemInHand) {
        Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(player.getWorld());
        if (optionalWorld.isEmpty()) {
            return;
        }

        String itemID = id(itemInHand);
        CustomCropsItem customCropsItem = Registries.ITEMS.get(itemID);
        if (customCropsItem != null) {
            customCropsItem.interactAir(new WrappedInteractAirEvent(
                    optionalWorld.get(),
                    player,
                    hand,
                    itemInHand,
                    itemID
            ));
        }
    }

    @Override
    public void handlePlayerInteractBlock(Player player, Block block, String blockID, BlockFace blockFace, EquipmentSlot hand, ItemStack itemInHand, Cancellable event) {
        Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(player.getWorld());
        if (optionalWorld.isEmpty()) {
            return;
        }

        if (antiGriefLib != null && !antiGriefLib.canInteract(player, block.getLocation())) {
            return;
        }

        String itemID = id(itemInHand);
        CustomCropsWorld<?> world = optionalWorld.get();
        WrappedInteractEvent wrapped = new WrappedInteractEvent(ExistenceForm.BLOCK, player, world, block.getLocation(), blockID, itemInHand, itemID, hand, blockFace, event);

        handleInteractEvent(blockID, wrapped);
    }

    @Override
    public void handlePlayerInteractFurniture(Player player, Location location, String furnitureID, EquipmentSlot hand, ItemStack itemInHand, Cancellable event) {
        Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(player.getWorld());
        if (optionalWorld.isEmpty()) {
            return;
        }

        if (antiGriefLib != null && !antiGriefLib.canInteract(player, location)) {
            return;
        }

        String itemID = id(itemInHand);
        CustomCropsWorld<?> world = optionalWorld.get();
        WrappedInteractEvent wrapped = new WrappedInteractEvent(ExistenceForm.FURNITURE, player, world, location, furnitureID, itemInHand, itemID, hand, null, event);

        handleInteractEvent(furnitureID, wrapped);
    }

    private void handleInteractEvent(String blockID, WrappedInteractEvent wrapped) {
        plugin.debug(() -> "Player [" + wrapped.player().getName() + "] interacted [" + blockID + "] with [" + wrapped.itemID() + "] at " + wrapped.location());

        CustomCropsItem customCropsItem = Registries.ITEMS.get(wrapped.itemID());
        if (customCropsItem != null) {
            InteractionResult result = customCropsItem.interactAt(wrapped);
            if (result != InteractionResult.PASS)
                return;
        }

        if (wrapped.isCancelled()) return;

        CustomCropsBlock customCropsBlock = Registries.BLOCKS.get(blockID);
        if (customCropsBlock != null) {
            customCropsBlock.onInteract(wrapped);
        }
    }

    @Override
    public void handlePlayerBreak(Player player, Location location, ItemStack itemInHand, String brokenID, Cancellable event) {
        Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(player.getWorld());
        if (optionalWorld.isEmpty()) {
            if (ConfigManager.interveneAntiGrief() && antiGriefLib != null && !antiGriefLib.canBreak(player, location)) {
                event.setCancelled(true);
            }
            return;
        }

        if (antiGriefLib != null && !antiGriefLib.canBreak(player, location)) {
            if (ConfigManager.interveneAntiGrief()) {
                event.setCancelled(true);
            }
            return;
        }

        String itemID = id(itemInHand);
        CustomCropsWorld<?> world = optionalWorld.get();
        WrappedBreakEvent wrapped = new WrappedBreakEvent(player, null, EquipmentSlot.HAND, location, brokenID, itemInHand, itemID, BreakReason.BREAK, world, event);
        CustomCropsBlock customCropsBlock = Registries.BLOCKS.get(brokenID);
        if (customCropsBlock != null) {
            customCropsBlock.onBreak(wrapped);
        }
    }

    @Override
    public void handlePhysicsBreak(Location location, String brokenID, Cancellable event) {
        Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(location.getWorld());
        if (optionalWorld.isEmpty()) {
            return;
        }

        CustomCropsWorld<?> world = optionalWorld.get();
        WrappedBreakEvent wrapped = new WrappedBreakEvent(null, null, null, location, brokenID, null, null, BreakReason.PHYSICS, world, event);
        CustomCropsBlock customCropsBlock = Registries.BLOCKS.get(brokenID);
        if (customCropsBlock != null) {
            customCropsBlock.onBreak(wrapped);
        }
    }

    @Override
    public void handleEntityTrample(Entity entity, Location location, String brokenID, Cancellable event) {
        Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(entity.getWorld());
        if (optionalWorld.isEmpty()) {
            return;
        }

        CustomCropsWorld<?> world = optionalWorld.get();
        WrappedBreakEvent wrapped = new WrappedBreakEvent(entity, null, null, location, brokenID, null, null, BreakReason.TRAMPLE, world, event);
        CustomCropsBlock customCropsBlock = Registries.BLOCKS.get(brokenID);
        if (customCropsBlock != null) {
            customCropsBlock.onBreak(wrapped);
        }
    }

    @Override
    public void handleEntityExplode(Entity entity, Location location, String brokenID, Cancellable event) {
        Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(entity.getWorld());
        if (optionalWorld.isEmpty()) {
            return;
        }

        CustomCropsWorld<?> world = optionalWorld.get();
        WrappedBreakEvent wrapped = new WrappedBreakEvent(entity, null, null, location, brokenID, null, null, BreakReason.EXPLODE, world, event);
        CustomCropsBlock customCropsBlock = Registries.BLOCKS.get(brokenID);
        if (customCropsBlock != null) {
            customCropsBlock.onBreak(wrapped);
        }
    }

    @Override
    public void handleBlockExplode(Block block, Location location, String brokenID, Cancellable event) {
        Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(block.getWorld());
        if (optionalWorld.isEmpty()) {
            return;
        }

        CustomCropsWorld<?> world = optionalWorld.get();
        WrappedBreakEvent wrapped = new WrappedBreakEvent(null, block, null, location, brokenID, null, null, BreakReason.EXPLODE, world, event);
        CustomCropsBlock customCropsBlock = Registries.BLOCKS.get(brokenID);
        if (customCropsBlock != null) {
            customCropsBlock.onBreak(wrapped);
        }
    }

    @Override
    public void handlePlayerPlace(Player player, Location location, String placedID, EquipmentSlot hand, ItemStack itemInHand, Cancellable event) {
        Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(player.getWorld());
        if (optionalWorld.isEmpty()) {
            return;
        }

        if (antiGriefLib != null && !antiGriefLib.canPlace(player, location)) {
            return;
        }

        CustomCropsWorld<?> world = optionalWorld.get();
        Pos3 pos3 = Pos3.from(location);
        Optional<CustomCropsBlockState> optionalState = world.getBlockState(pos3);
        if (optionalState.isPresent()) {
            CustomCropsBlockState customCropsBlockState = optionalState.get();
            String anyFurnitureID = furnitureID(location);
            if (anyFurnitureID != null) {
                if (!customCropsBlockState.type().isInstance(anyFurnitureID)) {
                    world.removeBlockState(pos3);
                    plugin.debug(() -> "[" + location.getWorld().getName() + "] Removed inconsistent block data at " + pos3 + " which used to be " + customCropsBlockState);
                } else {
                    event.setCancelled(true);
                    return;
                }
            }
            String anyBlockID = blockID(location);
            if (!customCropsBlockState.type().isInstance(anyBlockID)) {
                world.removeBlockState(pos3);
                plugin.debug(() -> "[" + location.getWorld().getName() + "] Removed inconsistent block data at " + pos3 + " which used to be " + customCropsBlockState);
            } else {
                event.setCancelled(true);
                return;
            }
        }

        String itemID = id(itemInHand);
        WrappedPlaceEvent wrapped = new WrappedPlaceEvent(player, world, location, placedID, hand, itemInHand, itemID, event);
        CustomCropsBlock customCropsBlock = Registries.BLOCKS.get(placedID);
        if (customCropsBlock != null) {
            customCropsBlock.onPlace(wrapped);
        }
    }
}

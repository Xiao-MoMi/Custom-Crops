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

package net.momirealms.customcrops.api.core.block;

import com.flowpowered.nbt.*;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.BuiltInBlockMechanics;
import net.momirealms.customcrops.api.core.ConfigManager;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.Registries;
import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
import net.momirealms.customcrops.api.core.mechanic.crop.CropStageConfig;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.core.mechanic.pot.PotConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedBreakEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedPlaceEvent;
import net.momirealms.customcrops.api.event.*;
import net.momirealms.customcrops.api.misc.NamedTextColor;
import net.momirealms.customcrops.api.misc.water.WateringMethod;
import net.momirealms.customcrops.api.requirement.RequirementManager;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.api.util.LocationUtils;
import net.momirealms.customcrops.api.util.PlayerUtils;
import net.momirealms.sparrow.heart.SparrowHeart;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PotBlock extends AbstractCustomCropsBlock {

    public PotBlock() {
        super(BuiltInBlockMechanics.POT.key());
    }

    @Override
    public void scheduledTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location, boolean offlineTick) {
        // ignore random tick
        if (world.setting().tickPotMode() == 1) return;
        if (canTick(state, world.setting().tickPotInterval())) {
            tickPot(state, world, location, offlineTick, false);
        }
    }

    @Override
    public void randomTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location, boolean offlineTick) {
        // ignore scheduled tick
        if (world.setting().tickPotMode() == 2) return;
        if (canTick(state, world.setting().tickPotInterval())) {
            tickPot(state, world, location, offlineTick, true);
        }
    }

    @Override
    public void onBreak(WrappedBreakEvent event) {
        CustomCropsWorld<?> world = event.world();
        Location location = LocationUtils.toBlockLocation(event.location());
        Pos3 pos3 = Pos3.from(location);
        PotConfig config = Registries.ITEM_TO_POT.get(event.brokenID());
        if (config == null) {
            if (!BukkitCustomCropsPlugin.isReloading()) {
                world.removeBlockState(pos3);
            }
            return;
        }

        final Player player = event.playerBreaker();
        Context<Player> context = Context.player(player);

        context.updateLocation(location);
        if (!RequirementManager.isSatisfied(context, config.breakRequirements())) {
            event.setCancelled(true);
            return;
        }

        Location upperLocation = location.clone().add(0,1,0);

        String upperID = BukkitCustomCropsPlugin.getInstance().getItemManager().anyID(upperLocation);
        List<CropConfig> cropConfigs = Registries.STAGE_TO_CROP_UNSAFE.get(upperID);

        CropConfig cropConfig = null;
        CropStageConfig stageConfig = null;

        outer: {
            if (cropConfigs != null && !cropConfigs.isEmpty()) {
                CropBlock cropBlock = (CropBlock) BuiltInBlockMechanics.CROP.mechanic();
                CustomCropsBlockState state = cropBlock.fixOrGetState(world, pos3.add(0,1,0), upperID);
                if (state == null) {
                    // remove ambiguous stage
                    BukkitCustomCropsPlugin.getInstance().getItemManager().remove(upperLocation, ExistenceForm.ANY);
                    break outer;
                }

                cropConfig = cropBlock.config(state);
                if (cropConfig == null || !cropConfigs.contains(cropConfig)) {
                    if (cropConfigs.size() != 1) {
                        // remove ambiguous stage
                        BukkitCustomCropsPlugin.getInstance().getItemManager().remove(upperLocation, ExistenceForm.ANY);
                        world.removeBlockState(pos3.add(0,1,0));
                        break outer;
                    }
                    cropConfig = cropConfigs.get(0);
                }

                context.updateLocation(upperLocation);
                if (!RequirementManager.isSatisfied(context, cropConfig.breakRequirements())) {
                    event.setCancelled(true);
                    return;
                }
                stageConfig = cropConfig.stageByID(upperID);
                // should not be null
                assert stageConfig != null;
                if (!RequirementManager.isSatisfied(context, stageConfig.breakRequirements())) {
                    event.setCancelled(true);
                    return;
                }

                CropBreakEvent breakEvent = new CropBreakEvent(event.entityBreaker(), event.blockBreaker(), cropConfig, upperID, upperLocation, state, event.reason());
                if (EventUtils.fireAndCheckCancel(breakEvent)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        CustomCropsBlockState potState = fixOrGetState(world, pos3, config, event.brokenID());

        PotBreakEvent breakEvent = new PotBreakEvent(event.entityBreaker(), event.blockBreaker(), event.location(), config, potState, event.reason());
        if (EventUtils.fireAndCheckCancel(breakEvent)) {
            event.setCancelled(true);
            return;
        }

        if (stageConfig != null) {
            ActionManager.trigger(context, stageConfig.breakActions());
        }
        if (cropConfig != null) {
            ActionManager.trigger(context, cropConfig.breakActions());
            world.removeBlockState(pos3.add(0,1,0));
            BukkitCustomCropsPlugin.getInstance().getItemManager().remove(upperLocation, ExistenceForm.ANY);
        }
        if (cropConfig == null && Registries.ITEM_TO_DEAD_CROP.containsKey(upperID)) {
            BukkitCustomCropsPlugin.getInstance().getItemManager().remove(upperLocation, ExistenceForm.ANY);
        }

        context.updateLocation(location);
        ActionManager.trigger(context, config.breakActions());

        world.removeBlockState(pos3);
    }

    @Override
    public void onPlace(WrappedPlaceEvent event) {
        PotConfig config = Registries.ITEM_TO_POT.get(event.placedID());
        if (config == null) {
            event.setCancelled(true);
            return;
        }

        Context<Player> context = Context.player(event.player());
        if (!RequirementManager.isSatisfied(context, config.placeRequirements())) {
            event.setCancelled(true);
            return;
        }

        CustomCropsWorld<?> world = event.world();
        Pos3 pos3 = Pos3.from(event.location());
        if (world.setting().potPerChunk() >= 0) {
            if (world.testChunkLimitation(pos3, this.getClass(), world.setting().potPerChunk())) {
                event.setCancelled(true);
                ActionManager.trigger(context, config.reachLimitActions());
                return;
            }
        }

        CustomCropsBlockState state = BuiltInBlockMechanics.POT.createBlockState();
        id(state, config.id());
        water(state, config.isWet(event.placedID()) ? 1 : 0);

        PotPlaceEvent placeEvent = new PotPlaceEvent(event.player(), event.location(), config, state, event.item(), event.hand());
        if (EventUtils.fireAndCheckCancel(placeEvent)) {
            event.setCancelled(true);
            return;
        }

        world.addBlockState(pos3, state).ifPresent(previous -> {
            BukkitCustomCropsPlugin.getInstance().debug(() -> "Overwrite old data with " + state +
                    " at location[" + world.worldName() + "," + pos3 + "] which used to be " + previous
            );
        });
        ActionManager.trigger(context, config.placeActions());
    }

    @Override
    public boolean isInstance(String id) {
        return Registries.ITEM_TO_POT.containsKey(id);
    }

    @Override
    public void restore(Location location, CustomCropsBlockState state) {
        updateBlockAppearance(location, state);
    }

    @Override
    public void onInteract(WrappedInteractEvent event) {
        PotConfig potConfig = Registries.ITEM_TO_POT.get(event.relatedID());
        if (potConfig == null) {
            return;
        }

        Location location = LocationUtils.toBlockLocation(event.location());
        Pos3 pos3 = Pos3.from(location);
        CustomCropsWorld<?> world = event.world();

        // fix or get data
        CustomCropsBlockState state = fixOrGetState(world, pos3, potConfig, event.relatedID());

        final Player player = event.player();
        Context<Player> context = Context.player(player);
        context.arg(ContextKeys.SLOT, event.hand());
        context.updateLocation(location);

        // check use requirements
        if (!RequirementManager.isSatisfied(context, potConfig.useRequirements())) {
            return;
        }

        final ItemStack itemInHand = event.itemInHand();
        // trigger event
        PotInteractEvent interactEvent = new PotInteractEvent(player, event.hand(), itemInHand, potConfig, location, state);
        if (EventUtils.fireAndCheckCancel(interactEvent)) {
            return;
        }

        if (tryWateringPot(player, context, state, event.hand(), event.itemID(), potConfig, location, itemInHand))
            return;

        int water = water(state);
        context.arg(ContextKeys.STORAGE, potConfig.storage());
        context.arg(ContextKeys.CURRENT_WATER, water);
        context.arg(ContextKeys.WATER_BAR, Optional.ofNullable(potConfig.waterBar()).map(it -> it.getWaterBar(water, potConfig.storage())).orElse(""));
        Fertilizer[] fertilizers = fertilizers(state);
        // for backward compatibility
        for (Fertilizer latest : fertilizers) {
            FertilizerConfig config = latest.config();
            if (config != null) {
                context.arg(ContextKeys.ICON, config.icon());
                context.arg(ContextKeys.MAX_TIMES, config.times());
                context.arg(ContextKeys.LEFT_TIMES, latest.times());
                break;
            }
        }
        ActionManager.trigger(context, potConfig.interactActions());
    }

    protected boolean tryWateringPot(Player player, Context<Player> context, CustomCropsBlockState state, EquipmentSlot hand, String itemID, PotConfig potConfig, Location potLocation, ItemStack itemInHand) {
        int waterInPot = water(state);
        for (WateringMethod method : potConfig.wateringMethods()) {
            if (method.getUsed().equals(itemID) && method.getUsedAmount() <= itemInHand.getAmount()) {
                if (method.checkRequirements(context)) {
                    if (waterInPot >= potConfig.storage()) {
                        ActionManager.trigger(context, potConfig.fullWaterActions());
                    } else {
                        PotFillEvent waterEvent = new PotFillEvent(player, itemInHand, hand, potLocation, method, state, potConfig);
                        if (EventUtils.fireAndCheckCancel(waterEvent))
                            return true;
                        if (player.getGameMode() != GameMode.CREATIVE) {
                            itemInHand.setAmount(Math.max(0, itemInHand.getAmount() - method.getUsedAmount()));
                            if (method.getReturned() != null) {
                                ItemStack returned = BukkitCustomCropsPlugin.getInstance().getItemManager().build(player, method.getReturned());
                                if (returned != null) {
                                    PlayerUtils.giveItem(player, returned, method.getReturnedAmount());
                                }
                            }
                        }
                        if (addWater(state, method.amountOfWater())) {
                            updateBlockAppearance(potLocation, potConfig, true, fertilizers(state));
                        }
                        method.triggerActions(context);
                        ActionManager.trigger(context, potConfig.addWaterActions());
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public CustomCropsBlockState createBlockState(String itemID) {
        PotConfig potConfig = Registries.ITEM_TO_POT.get(itemID);
        if (potConfig == null) {
            return null;
        }
        CustomCropsBlockState state = createBlockState();
        id(state, potConfig.id());
        water(state, potConfig.isWet(itemID) ? 1 : 0);
        return state;
    }

    public CustomCropsBlockState fixOrGetState(CustomCropsWorld<?> world, Pos3 pos3, PotConfig potConfig, String blockID) {
        if (potConfig == null) return null;
        Optional<CustomCropsBlockState> optionalPotState = world.getBlockState(pos3);
        if (optionalPotState.isPresent()) {
            CustomCropsBlockState potState = optionalPotState.get();
            if (potState.type() instanceof PotBlock potBlock) {
                if (potBlock.id(potState).equals(potConfig.id())) {
                    return potState;
                }
            }
        }
        CustomCropsBlockState state = createBlockState();
        id(state, potConfig.id());
        water(state, potConfig.isWet(blockID) ? 1 : 0);
        world.addBlockState(pos3, state).ifPresent(previous -> {
            BukkitCustomCropsPlugin.getInstance().debug(() -> "Overwrite old data with " + state +
                    " at location[" + world.worldName() + "," + pos3 + "] which used to be " + previous
            );
        });
        return state;
    }

    private void tickPot(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location, boolean offline, boolean tickMode) {
        PotConfig config = config(state);
        BukkitCustomCropsPlugin plugin = BukkitCustomCropsPlugin.getInstance();
        if (config == null) {
            plugin.getPluginLogger().warn("Pot data is removed at location[" + world.worldName() + "," + location + "] because the pot config[" + id(state) + "] has been removed.");
            world.removeBlockState(location);
            return;
        }

        if (tickMode && config.ignoreRandomTick()) return;
        if (!tickMode && config.ignoreScheduledTick()) return;

        World bukkitWorld = world.bukkitWorld();
        if (ConfigManager.doubleCheck()) {
            String blockID = plugin.getItemManager().blockID(location.toLocation(bukkitWorld));
            if (!config.blocks().contains(blockID)) {
                plugin.getPluginLogger().warn("Pot[" + config.id() + "] is removed at location[" + world.worldName() + "," + location + "] because the id of the block is [" + blockID + "]");
                world.removeBlockState(location);
                return;
            }
        }

        // work as vanilla farmland
        if (config.disablePluginMechanism()) return;

        boolean hasNaturalWater = false;
        boolean waterChanged = false;

        Location bukkitLocation = location.toLocation(bukkitWorld);

        if (config.isRainDropAccepted()) {
            if (SparrowHeart.getInstance().isRainingAt(bukkitLocation.clone().add(0,1,0))) {
                if (addWater(state, 1)) {
                    waterChanged = true;
                }
                hasNaturalWater = true;
            }
        }

        if (!hasNaturalWater && config.isNearbyWaterAccepted()) {
            outer: {
                for (int i = -4; i <= 4; i++) {
                    for (int j = -4; j <= 4; j++) {
                        for (int k : new int[]{0, 1}) {
                            BlockData block = bukkitWorld.getBlockData(location.x() + i, location.y() + k, location.z() + j);
                            if (block.getMaterial() == Material.WATER || (block instanceof Waterlogged waterlogged && waterlogged.isWaterlogged())) {
                                if (addWater(state, 1)) {
                                    waterChanged = true;
                                }
                                hasNaturalWater = true;
                                break outer;
                            }
                        }
                    }
                }
            }
        }

        if (!hasNaturalWater) {
            int waterToLose = 1;
            Fertilizer[] fertilizers = fertilizers(state);
            for (Fertilizer fertilizer : fertilizers) {
                FertilizerConfig fertilizerConfig = fertilizer.config();
                if (fertilizerConfig != null) {
                    waterToLose = fertilizerConfig.processWaterToLose(waterToLose);
                }
            }
            if (waterToLose > 0) {
                if (addWater(state, -waterToLose)) {
                    waterChanged = true;
                }
            }
        }

        boolean fertilizerChanged = tickFertilizer(state);

        if (fertilizerChanged || waterChanged) {
            plugin.getScheduler().sync().run(() -> updateBlockAppearance(bukkitLocation, config, water(state) != 0, fertilizers(state)), bukkitLocation);
        }

        ActionManager.trigger(Context.block(state, bukkitLocation).arg(ContextKeys.OFFLINE, offline), config.tickActions());
    }

    public int water(CustomCropsBlockState state) {
        Tag<?> tag = state.get("water");
        if (tag == null) {
            return 0;
        }
        return tag.getAsIntTag().map(IntTag::getValue).orElse(0);
    }

    public boolean addWater(CustomCropsBlockState state, int water) {
        return water(state, water + water(state));
    }

    public boolean addWater(CustomCropsBlockState state, PotConfig config, int water) {
        return water(state, config, water + water(state));
    }

    public boolean consumeWater(CustomCropsBlockState state, int water) {
        return water(state, water(state) - water);
    }

    public boolean consumeWater(CustomCropsBlockState state, PotConfig config, int water) {
        return water(state, config, water(state) - water);
    }

    public boolean water(CustomCropsBlockState state, int water) {
        return water(state, config(state), water);
    }

    /**
     * Set the water for a pot
     *
     * @param state the block state
     * @param config the pot config
     * @param water the amount of water
     * @return whether the moisture state has been changed
     */
    public boolean water(CustomCropsBlockState state, PotConfig config, int water) {
        if (water < 0) water = 0;
        int current = Math.min(water, config.storage());
        int previous = water(state);
        if (water == previous) return false;
        state.set("water", new IntTag("water", current));
        return previous == 0 ^ current == 0;
    }

    public PotConfig config(CustomCropsBlockState state) {
        return Registries.POT.get(id(state));
    }

    /**
     * Get the fertilizers in the pot
     *
     * @param state the block state
     * @return applied fertilizers
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public Fertilizer[] fertilizers(CustomCropsBlockState state) {
        Tag<?> fertilizerTag = state.get("fertilizers");
        if (fertilizerTag == null) return new Fertilizer[0];
        List<CompoundTag> tags = ((List<CompoundTag>) fertilizerTag.getValue());
        Fertilizer[] fertilizers = new Fertilizer[tags.size()];
        for (int i = 0; i < tags.size(); i++) {
            CompoundTag tag = tags.get(i);
            fertilizers[i] = tagToFertilizer(tag.getValue());
        }
        return fertilizers;
    }

    /**
     * Check if the fertilizer can be applied to this pot
     *
     * @param state the block state
     * @param fertilizer the fertilizer to apply
     * @return can be applied or not
     */
    public boolean canApplyFertilizer(CustomCropsBlockState state, Fertilizer fertilizer) {
        Fertilizer[] fertilizers = fertilizers(state);
        boolean hasSameTypeFertilizer = false;
        for (Fertilizer applied : fertilizers) {
            if (fertilizer.id().equals(applied.id())) {
                return true;
            }
            if (fertilizer.type() == applied.type()) {
                return false;
            }
        }
        PotConfig config = config(state);
        return config.maxFertilizers() > fertilizers.length;
    }

    /**
     * Add fertilizer to the pot
     * If the pot contains the fertilizer, the times would be reset.
     *
     * @param state the block state
     * @param fertilizer the fertilizer to apply
     * @return whether to update pot appearance
     */
    @SuppressWarnings("unchecked")
    public boolean addFertilizer(CustomCropsBlockState state, Fertilizer fertilizer) {
        Tag<?> fertilizerTag = state.get("fertilizers");
        if (fertilizerTag == null) {
            fertilizerTag = new ListTag<CompoundTag>("fertilizers", TagType.TAG_COMPOUND, new ArrayList<>());
            state.set("fertilizers", fertilizerTag);
        }
        List<CompoundTag> tags = ((List<CompoundTag>) fertilizerTag.getValue());
        for (CompoundTag tag : tags) {
            CompoundMap map = tag.getValue();
            Fertilizer applied = tagToFertilizer(map);
            if (fertilizer.id().equals(applied.id())) {
                map.put(new IntTag("times", fertilizer.times()));
                return false;
            }
            if (fertilizer.type() == applied.type()) {
                return false;
            }
        }
        PotConfig config = config(state);
        if (config.maxFertilizers() <= tags.size()) {
            return false;
        }
        tags.add(new CompoundTag("", fertilizerToTag(fertilizer)));
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean tickFertilizer(CustomCropsBlockState state) {
        // no fertilizers applied
        Tag<?> fertilizerTag = state.get("fertilizers");
        if (fertilizerTag == null) {
            return false;
        }
        List<CompoundTag> tags = ((List<CompoundTag>) fertilizerTag.getValue());
        if (tags.isEmpty()) {
            return false;
        }
        List<Integer> fertilizerToRemove = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            CompoundMap map = tags.get(i).getValue();
            Fertilizer applied = tagToFertilizer(map);
            if (applied.reduceTimes()) {
                fertilizerToRemove.add(i);
            } else {
                tags.get(i).setValue(fertilizerToTag(applied));
            }
        }
        // no fertilizer is used up
        if (fertilizerToRemove.isEmpty()) {
            return false;
        }
        CompoundTag lastEntry = tags.get(tags.size() - 1);
        Collections.reverse(fertilizerToRemove);
        for (int i : fertilizerToRemove) {
            tags.remove(i);
        }
        // all the fertilizers are used up
        if (tags.isEmpty()) {
            return true;
        }
        // if the most recent applied fertilizer is the same
        CompoundTag newLastEntry = tags.get(tags.size() - 1);
        return lastEntry != newLastEntry;
    }

    public void updateBlockAppearance(Location location, CustomCropsBlockState state) {
        updateBlockAppearance(location, state, fertilizers(state));
    }

    public void updateBlockAppearance(Location location, CustomCropsBlockState state, Fertilizer[] fertilizers) {
        updateBlockAppearance(location, state, water(state) != 0, fertilizers);
    }

    public void updateBlockAppearance(Location location, CustomCropsBlockState state, boolean hasWater, Fertilizer[] fertilizers) {
        updateBlockAppearance(
                location,
                config(state),
                hasWater,
                // always using the latest fertilizer as appearance
                fertilizers.length == 0 ? null : fertilizers[fertilizers.length - 1]
        );
    }

    public void updateBlockAppearance(Location location, PotConfig config, boolean hasWater, Fertilizer[] fertilizers) {
        updateBlockAppearance(
                location,
                config,
                hasWater,
                // always using the latest fertilizer as appearance
                fertilizers.length == 0 ? null : fertilizers[fertilizers.length - 1]
        );
    }

    public void updateBlockAppearance(Location location, PotConfig config, boolean hasWater, @Nullable Fertilizer fertilizer) {
        if (config.disablePluginMechanism()) {
            String appearance = config.blocks().stream().findAny().get();
            BukkitCustomCropsPlugin.getInstance().getItemManager().placeBlock(location, appearance);
        } else {
            String appearance = config.getPotAppearance(hasWater, fertilizer == null ? null : fertilizer.type());
            BukkitCustomCropsPlugin.getInstance().getItemManager().placeBlock(location, appearance);
        }
    }

    private Fertilizer tagToFertilizer(CompoundMap tag) {
        return Fertilizer.builder()
                .id(((StringTag) tag.get("id")).getValue())
                .times(((IntTag) tag.get("times")).getValue())
                .build();
    }

    private CompoundMap fertilizerToTag(Fertilizer fertilizer) {
        CompoundMap tag = new CompoundMap();
        tag.put(new IntTag("times", fertilizer.times()));
        tag.put(new StringTag("id", fertilizer.id()));
        return tag;
    }

    @Override
    public NamedTextColor insightColor() {
        return NamedTextColor.LIGHT_PURPLE;
    }
}

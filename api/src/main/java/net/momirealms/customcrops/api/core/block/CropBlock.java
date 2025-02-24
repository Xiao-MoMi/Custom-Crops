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

import com.flowpowered.nbt.IntTag;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.*;
import net.momirealms.customcrops.api.core.mechanic.crop.*;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.core.mechanic.pot.PotConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedBreakEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedPlaceEvent;
import net.momirealms.customcrops.api.event.BoneMealUseEvent;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.api.event.CropInteractEvent;
import net.momirealms.customcrops.api.misc.NamedTextColor;
import net.momirealms.customcrops.api.requirement.RequirementManager;
import net.momirealms.customcrops.api.util.EventUtils;
import net.momirealms.customcrops.api.util.LocationUtils;
import net.momirealms.customcrops.api.util.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CropBlock extends AbstractCustomCropsBlock {

    public CropBlock() {
        super(BuiltInBlockMechanics.CROP.key());
    }

    @Override
    public void scheduledTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location, boolean offlineTick) {
        // ignore random tick
        if (world.setting().tickCropMode() == 1) return;
        if (canTick(state, world.setting().tickCropInterval())) {
            tickCrop(state, world, location, offlineTick, false);
        }
    }

    @Override
    public void randomTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location, boolean offlineTick) {
        // ignore scheduled tick
        if (world.setting().tickCropMode() == 2) return;
        if (canTick(state, world.setting().tickCropInterval())) {
            tickCrop(state, world, location, offlineTick, true);
        }
    }

    @Override
    public void onBreak(WrappedBreakEvent event) {
        List<CropConfig> configs = Registries.STAGE_TO_CROP_UNSAFE.get(event.brokenID());
        CustomCropsWorld<?> world = event.world();
        Location location = LocationUtils.toBlockLocation(event.location());
        Pos3 pos3 = Pos3.from(location);
        if (configs == null || configs.isEmpty()) {
            if (!BukkitCustomCropsPlugin.isReloading()) {
                world.removeBlockState(pos3);
            }
            return;
        }

        CustomCropsBlockState state = fixOrGetState(world, pos3, event.brokenID());
        if (state == null) {
            return;
        }

        // check data for precise data
        CropConfig cropConfig = config(state);
        // the config not exists or it's a wrong one
        if (cropConfig == null || !configs.contains(cropConfig)) {
            if (configs.size() != 1) {
                return;
            }
            cropConfig = configs.get(0);
        }

        CropStageConfig stageConfig = cropConfig.stageByID(event.brokenID());
        assert stageConfig != null;

        final Player player = event.playerBreaker();
        Context<Player> context = Context.player(player);
        if (event.hand() != null) {
            context.arg(ContextKeys.SLOT, event.hand());
        }

        context.updateLocation(location);

        // check requirements only if it's triggered by direct events
        if (event.reason() == BreakReason.BREAK) {
            if (!RequirementManager.isSatisfied(context, cropConfig.breakRequirements())) {
                event.setCancelled(true);
                return;
            }
            if (!RequirementManager.isSatisfied(context, stageConfig.breakRequirements())) {
                event.setCancelled(true);
                return;
            }
        }

        CropBreakEvent breakEvent = new CropBreakEvent(event.entityBreaker(), event.blockBreaker(), cropConfig, event.brokenID(), event.location(),
                state, event.reason());
        if (EventUtils.fireAndCheckCancel(breakEvent)) {
            event.setCancelled(true);
            return;
        }

        ActionManager.trigger(context, stageConfig.breakActions());
        ActionManager.trigger(context, cropConfig.breakActions());
        world.removeBlockState(pos3);
    }

    /**
     * This can only be triggered admins because players shouldn't get the stages
     */
    @Override
    public void onPlace(WrappedPlaceEvent event) {
        List<CropConfig> configs = Registries.STAGE_TO_CROP_UNSAFE.get(event.placedID());
        if (configs == null || configs.size() != 1) {
            return;
        }

        CropConfig cropConfig = configs.get(0);
        Context<Player> context = Context.player(event.player());
//        if (!RequirementManager.isSatisfied(context, cropConfig.plantRequirements())) {
//            event.setCancelled(true);
//            return;
//        }

        Pos3 pos3 = Pos3.from(event.location());
        CustomCropsWorld<?> world = event.world();
        if (world.setting().cropPerChunk() >= 0) {
            if (world.testChunkLimitation(pos3, this.getClass(), world.setting().cropPerChunk())) {
                ActionManager.trigger(context, cropConfig.reachLimitActions());
                event.setCancelled(true);
                return;
            }
        }

        fixOrGetState(world, pos3, event.placedID());
    }

    @Override
    public boolean isInstance(String id) {
        return Registries.STAGE_TO_CROP_UNSAFE.containsKey(id);
    }

    @Override
    public void restore(Location location, CustomCropsBlockState state) {
        CropConfig config = config(state);
        if (config == null) return;
        int point = point(state);
        CropStageConfig stageConfig = config.stageWithModelByPoint(point);
        if (stageConfig != null) {
            BukkitCustomCropsPlugin.getInstance().getItemManager().place(location, stageConfig.existenceForm(), Objects.requireNonNull(stageConfig.stageID()), config.rotation() ? FurnitureRotation.random() : FurnitureRotation.NONE);
        }
    }

    @Override
    public void onInteract(WrappedInteractEvent event) {
        final Player player = event.player();
        Context<Player> context = Context.player(player);
        context.arg(ContextKeys.SLOT, event.hand());

        // data first
        CustomCropsWorld<?> world = event.world();
        Location location = LocationUtils.toBlockLocation(event.location());
        Pos3 pos3 = Pos3.from(location);
        // fix if possible
        CustomCropsBlockState state = fixOrGetState(world, pos3, event.relatedID());
        if (state == null) return;

        CropConfig cropConfig = config(state);
        if (!RequirementManager.isSatisfied(context, cropConfig.interactRequirements())) {
            return;
        }

        int point = point(state);
        CropStageConfig stageConfig = cropConfig.stageByID(event.relatedID());
        if (stageConfig == null) {
            // fix it if it's a wrong data
            world.removeBlockState(pos3);
            state = fixOrGetState(world, pos3, event.relatedID());
            if (state == null) {
                return;
            }
            cropConfig = config(state);
            stageConfig = cropConfig.stageByID(event.relatedID());
            if (stageConfig == null) {
                return;
            }
        }
        if (!RequirementManager.isSatisfied(context, stageConfig.interactRequirements())) {
            return;
        }

        final ItemStack itemInHand = event.itemInHand();
        // trigger event
        CropInteractEvent interactEvent = new CropInteractEvent(player, itemInHand, location, state, event.hand(), cropConfig, event.relatedID());
        if (EventUtils.fireAndCheckCancel(interactEvent)) {
            return;
        }

        Location potLocation = location.clone().subtract(0,1,0);
        String blockBelowID = BukkitCustomCropsPlugin.getInstance().getItemManager().blockID(potLocation.getBlock());
        PotConfig potConfig = Registries.ITEM_TO_POT.get(blockBelowID);
        if (potConfig != null) {
            context.updateLocation(potLocation);
            PotBlock potBlock = (PotBlock) BuiltInBlockMechanics.POT.mechanic();
            assert potBlock != null;
            // fix or get data
            CustomCropsBlockState potState = potBlock.fixOrGetState(world, Pos3.from(potLocation), potConfig, event.relatedID());
            if (potBlock.tryWateringPot(player, context, potState, event.hand(), event.itemID(), potConfig, potLocation, itemInHand))
                return;
        }

        context.updateLocation(location);
        if (point < cropConfig.maxPoints()) {
            for (BoneMeal boneMeal : cropConfig.boneMeals()) {
                if (boneMeal.requiredItem().equals(event.itemID()) && boneMeal.amountOfRequiredItem() <= itemInHand.getAmount()) {
                    BoneMealUseEvent useEvent = new BoneMealUseEvent(player, itemInHand, location, boneMeal, state, event.hand(), cropConfig);
                    if (EventUtils.fireAndCheckCancel(useEvent))
                        return;
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        itemInHand.setAmount(itemInHand.getAmount() - boneMeal.amountOfRequiredItem());
                        if (boneMeal.returnedItem() != null) {
                            ItemStack returned = BukkitCustomCropsPlugin.getInstance().getItemManager().build(player, boneMeal.returnedItem());
                            if (returned != null) {
                                PlayerUtils.giveItem(player, returned, boneMeal.amountOfReturnItem());
                            }
                        }
                    }
                    boneMeal.triggerActions(context);

                    int afterPoints = Math.min(point + boneMeal.rollPoint(), cropConfig.maxPoints());
                    point(state, afterPoints);

                    CropStageConfig nextStage = cropConfig.stageWithModelByPoint(afterPoints);

                    Context<CustomCropsBlockState> blockContext = Context.block(state, location);
                    if (Objects.equals(nextStage.stageID(), event.relatedID())) {
                        for (int i = point + 1; i <= afterPoints; i++) {
                            CropStageConfig stage = cropConfig.stageByPoint(i);
                            if (stage != null) {
                                ActionManager.trigger(blockContext, stage.growActions());
                            }
                        }
                        return;
                    }
                    FurnitureRotation rotation = BukkitCustomCropsPlugin.getInstance().getItemManager().remove(location, ExistenceForm.ANY);
                    if (rotation == FurnitureRotation.NONE && cropConfig.rotation()) {
                        rotation = FurnitureRotation.random();
                    }
                    BukkitCustomCropsPlugin.getInstance().getItemManager().place(location, nextStage.existenceForm(), Objects.requireNonNull(nextStage.stageID()), rotation);
                    for (int i = point + 1; i <= afterPoints; i++) {
                        CropStageConfig stage = cropConfig.stageByPoint(i);
                        if (stage != null) {
                            ActionManager.trigger(blockContext, stage.growActions());
                        }
                    }
                    return;
                }
            }
        }

        ActionManager.trigger(context, cropConfig.interactActions());
        ActionManager.trigger(context, stageConfig.interactActions());
    }

    @Override
    public CustomCropsBlockState createBlockState(String itemID) {
        List<CropConfig> configList = Registries.STAGE_TO_CROP_UNSAFE.get(itemID);
        if (configList == null || configList.size() != 1) return null;
        CropConfig cropConfig = configList.get(0);
        CustomCropsBlockState state = createBlockState();
        CropStageConfig stageConfig = cropConfig.stageByID(itemID);
        assert stageConfig != null;
        point(state, stageConfig.point());
        id(state, cropConfig.id());
        return state;
    }

    public CustomCropsBlockState fixOrGetState(CustomCropsWorld<?> world, Pos3 pos3, String stageID) {
        List<CropConfig> configList = Registries.STAGE_TO_CROP_UNSAFE.get(stageID);
        if (configList == null) return null;

        Optional<CustomCropsBlockState> optionalPotState = world.getBlockState(pos3);
        if (optionalPotState.isPresent()) {
            CustomCropsBlockState potState = optionalPotState.get();
            if (potState.type() instanceof CropBlock cropBlock) {
                if (configList.stream().map(CropConfig::id).toList().contains(cropBlock.id(potState))) {
                    return potState;
                }
            }
        }

        if (configList.size() != 1) {
            return null;
        }
        CropConfig cropConfig = configList.get(0);
        CropStageConfig stageConfig = cropConfig.stageByID(stageID);
        assert stageConfig != null;
        int point = stageConfig.point();
        CustomCropsBlockState state = createBlockState();
        point(state, point);
        id(state, cropConfig.id());
        world.addBlockState(pos3, state).ifPresent(previous -> {
            BukkitCustomCropsPlugin.getInstance().debug(() -> "Overwrite old data with " + state +
                    " at location[" + world.worldName() + "," + pos3 + "] which used to be " + previous
            );
        });
        return state;
    }

    private void tickCrop(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location, boolean offline, boolean tickMode) {
        CropConfig config = config(state);
        BukkitCustomCropsPlugin plugin = BukkitCustomCropsPlugin.getInstance();
        if (config == null) {
            plugin.getPluginLogger().warn("Crop data is removed at location[" + world.worldName() + "," + location + "] because the crop config[" + id(state) + "] has been removed.");
            world.removeBlockState(location);
            return;
        }

        if (tickMode && config.ignoreRandomTick()) return;
        if (!tickMode && config.ignoreScheduledTick()) return;

        int previousPoint = point(state);
        World bukkitWorld = world.bukkitWorld();
        Location bukkitLocation = location.toLocation(bukkitWorld);

        Runnable task = () -> {
            Context<CustomCropsBlockState> context = Context.block(state, bukkitLocation).arg(ContextKeys.OFFLINE, offline);
            for (DeathCondition deathCondition : config.deathConditions()) {
                if (deathCondition.isMet(context)) {
                    plugin.getScheduler().sync().runLater(() -> {
                        FurnitureRotation rotation = plugin.getItemManager().remove(bukkitLocation, ExistenceForm.ANY);
                        world.removeBlockState(location);
                        Optional.ofNullable(deathCondition.deathStage()).ifPresent(it -> {
                            plugin.getItemManager().place(bukkitLocation, deathCondition.existenceForm(), it, rotation);
                        });
                        ActionManager.trigger(context, config.deathActions());
                    }, deathCondition.deathDelay(), bukkitLocation);
                    return;
                }
            }

            if (previousPoint >= config.maxPoints()) {
                return;
            }

            int pointToAdd = 0;
            GrowCondition[] growConditions = config.growConditions();
            if (growConditions.length == 0) {
                pointToAdd = 1;
            } else {
                for (GrowCondition growCondition : config.growConditions()) {
                    if (growCondition.isMet(context)) {
                        pointToAdd = growCondition.pointToAdd();
                        break;
                    }
                }
            }
            if (pointToAdd == 0) return;

            Optional<CustomCropsBlockState> optionalState = world.getBlockState(location.add(0,-1,0));
            if (optionalState.isPresent()) {
                CustomCropsBlockState belowState = optionalState.get();
                if (belowState.type() instanceof PotBlock potBlock) {
                    for (Fertilizer fertilizer : potBlock.fertilizers(belowState)) {
                        FertilizerConfig fertilizerConfig = fertilizer.config();
                        if (fertilizerConfig != null) {
                            pointToAdd = fertilizerConfig.processGainPoints(pointToAdd);
                        }
                    }
                }
            }

            int afterPoints = Math.min(previousPoint + pointToAdd, config.maxPoints());
            point(state, afterPoints);

            CropStageConfig currentStage = config.stageWithModelByPoint(previousPoint);
            CropStageConfig nextStage = config.stageWithModelByPoint(afterPoints);

            plugin.getScheduler().sync().run(() -> {
                if (currentStage == nextStage) {
                    for (int i = previousPoint + 1; i <= afterPoints; i++) {
                        CropStageConfig stage = config.stageByPoint(i);
                        if (stage != null) {
                            ActionManager.trigger(context, stage.growActions());
                        }
                    }
                    return;
                }
                FurnitureRotation rotation = plugin.getItemManager().remove(bukkitLocation, ExistenceForm.ANY);
                if (rotation == FurnitureRotation.NONE && config.rotation()) {
                    rotation = FurnitureRotation.random();
                }
                plugin.getItemManager().place(bukkitLocation, nextStage.existenceForm(), Objects.requireNonNull(nextStage.stageID()), rotation);
                for (int i = previousPoint + 1; i <= afterPoints; i++) {
                    CropStageConfig stage = config.stageByPoint(i);
                    if (stage != null) {
                        ActionManager.trigger(context, stage.growActions());
                    }
                }
            }, bukkitLocation);
        };

        if (ConfigManager.doubleCheck()) {
            plugin.getScheduler().sync().run(() -> {
                CropStageConfig nearest = config.stageWithModelByPoint(previousPoint);
                if (nearest != null) {
                    String blockID = plugin.getItemManager().id(location.toLocation(bukkitWorld), nearest.existenceForm());
                    if (!config.stageIDs().contains(blockID)) {
                        plugin.getPluginLogger().warn("Crop[" + config.id() + "] is removed at location[" + world.worldName() + "," + location + "] because the id of the block is [" + blockID + "]");
                        world.removeBlockState(location);
                        return;
                    }
                } else {
                    plugin.getPluginLogger().warn("Crop[" + config.id() + "] is removed at location[" + world.worldName() + "," + location + "] because no model found for point[" + previousPoint + "]");
                    world.removeBlockState(location);
                    return;
                }
                world.scheduler().async().execute(task);
            }, bukkitLocation);
        } else {
            task.run();
        }
    }

    public int point(CustomCropsBlockState state) {
        return state.get("point").getAsIntTag().map(IntTag::getValue).orElse(0);
    }

    public void point(CustomCropsBlockState state, int point) {
        state.set("point", new IntTag("point", point));
    }

    public CropConfig config(CustomCropsBlockState state) {
        return Registries.CROP.get(id(state));
    }

    @Override
    public NamedTextColor insightColor() {
        return NamedTextColor.GREEN;
    }
}

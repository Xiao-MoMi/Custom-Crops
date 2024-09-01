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
import net.momirealms.customcrops.api.core.item.Fertilizer;
import net.momirealms.customcrops.api.core.item.FertilizerConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedBreakEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedInteractEvent;
import net.momirealms.customcrops.api.core.wrapper.WrappedPlaceEvent;
import net.momirealms.customcrops.api.event.BoneMealUseEvent;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.api.event.CropInteractEvent;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CropBlock extends AbstractCustomCropsBlock {

    public CropBlock() {
        super(BuiltInBlockMechanics.CROP.key());
    }

    @Override
    public void scheduledTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location) {
        if (!world.setting().randomTickCrop() && canTick(state, world.setting().tickCropInterval())) {
            tickCrop(state, world, location);
        }
    }

    @Override
    public void randomTick(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location) {
        if (world.setting().randomTickCrop() && canTick(state, world.setting().tickCropInterval())) {
            tickCrop(state, world, location);
        }
    }

    @Override
    public void onBreak(WrappedBreakEvent event) {
        List<CropConfig> configs = Registries.STAGE_TO_CROP_UNSAFE.get(event.brokenID());
        CustomCropsWorld<?> world = event.world();
        Pos3 pos3 = Pos3.from(event.location());
        if (configs == null || configs.isEmpty()) {
            world.removeBlockState(pos3);
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
        context.arg(ContextKeys.LOCATION, LocationUtils.toBlockLocation(event.location()));

        // check requirements
        if (!RequirementManager.isSatisfied(context, cropConfig.breakRequirements())) {
            event.setCancelled(true);
            return;
        }
        if (!RequirementManager.isSatisfied(context, stageConfig.breakRequirements())) {
            event.setCancelled(true);
            return;
        }

        CropBreakEvent breakEvent = new CropBreakEvent(event.entityBreaker(), event.blockBreaker(), cropConfig, event.brokenID(), event.location(),
                state, BreakReason.BREAK);
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
    public void onInteract(WrappedInteractEvent event) {
        final Player player = event.player();
        Context<Player> context = Context.player(player);
        context.arg(ContextKeys.SLOT, event.hand());

        // data first
        CustomCropsWorld<?> world = event.world();
        Location location = event.location();
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
        assert stageConfig != null;
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
            context.arg(ContextKeys.LOCATION, LocationUtils.toBlockLocation(potLocation));
            PotBlock potBlock = (PotBlock) BuiltInBlockMechanics.POT.mechanic();
            assert potBlock != null;
            // fix or get data
            CustomCropsBlockState potState = potBlock.fixOrGetState(world, Pos3.from(potLocation), potConfig, event.relatedID());
            if (potBlock.tryWateringPot(player, context, potState, event.hand(), event.itemID(), potConfig, potLocation, itemInHand))
                return;
        }

        context.arg(ContextKeys.LOCATION, LocationUtils.toBlockLocation(location));
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

                    String afterStage = null;
                    ExistenceForm afterForm = null;
                    int tempPoints = afterPoints;
                    while (tempPoints >= 0) {
                        Map.Entry<Integer, CropStageConfig> afterEntry = cropConfig.getFloorStageEntry(tempPoints);
                        CropStageConfig after = afterEntry.getValue();
                        if (after.stageID() != null) {
                            afterStage = after.stageID();
                            afterForm = after.existenceForm();
                            break;
                        }
                        tempPoints = after.point() - 1;
                    }

                    Objects.requireNonNull(afterForm);
                    Objects.requireNonNull(afterStage);

                    Context<CustomCropsBlockState> blockContext = Context.block(state);
                    blockContext.arg(ContextKeys.LOCATION, LocationUtils.toBlockLocation(location));
                    for (int i = point + 1; i <= afterPoints; i++) {
                        CropStageConfig stage = cropConfig.stageByPoint(i);
                        if (stage != null) {
                            ActionManager.trigger(blockContext, stage.growActions());
                        }
                    }

                    if (Objects.equals(afterStage, event.relatedID())) return;
                    Location bukkitLocation = location.toLocation(world.bukkitWorld());
                    FurnitureRotation rotation = BukkitCustomCropsPlugin.getInstance().getItemManager().remove(bukkitLocation, ExistenceForm.ANY);
                    if (rotation == FurnitureRotation.NONE && cropConfig.rotation()) {
                        rotation = FurnitureRotation.random();
                    }
                    BukkitCustomCropsPlugin.getInstance().getItemManager().place(bukkitLocation, afterForm, afterStage, rotation);
                    return;
                }
            }
        }

        ActionManager.trigger(context, cropConfig.interactActions());
        ActionManager.trigger(context, stageConfig.interactActions());
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
        int point = stageConfig.point();
        CustomCropsBlockState state = BuiltInBlockMechanics.CROP.createBlockState();
        point(state, point);
        id(state, cropConfig.id());
        world.addBlockState(pos3, state).ifPresent(previous -> {
            BukkitCustomCropsPlugin.getInstance().debug(
                    "Overwrite old data with " + state.compoundMap().toString() +
                            " at location[" + world.worldName() + "," + pos3 + "] which used to be " + previous.compoundMap().toString()
            );
        });
        return state;
    }

    private void tickCrop(CustomCropsBlockState state, CustomCropsWorld<?> world, Pos3 location) {
        CropConfig config = config(state);
        if (config == null) {
            BukkitCustomCropsPlugin.getInstance().getPluginLogger().warn("Crop data is removed at location[" + world.worldName() + "," + location + "] because the crop config[" + id(state) + "] has been removed.");
            world.removeBlockState(location);
            return;
        }

        int previousPoint = point(state);
        World bukkitWorld = world.bukkitWorld();
        if (ConfigManager.doubleCheck()) {
            Map.Entry<Integer, CropStageConfig> nearest = config.getFloorStageEntry(previousPoint);
            String blockID = BukkitCustomCropsPlugin.getInstance().getItemManager().id(location.toLocation(bukkitWorld), nearest.getValue().existenceForm());
            if (!config.stageIDs().contains(blockID)) {
                BukkitCustomCropsPlugin.getInstance().getPluginLogger().warn("Crop[" + config.id() + "] is removed at location[" + world.worldName() + "," + location + "] because the id of the block is [" + blockID + "]");
                world.removeBlockState(location);
                return;
            }
        }

        Context<CustomCropsBlockState> context = Context.block(state);
        Location bukkitLocation = location.toLocation(bukkitWorld);
        context.arg(ContextKeys.LOCATION, bukkitLocation);
        for (DeathCondition deathCondition : config.deathConditions()) {
            if (deathCondition.isMet(context)) {
                BukkitCustomCropsPlugin.getInstance().getScheduler().sync().runLater(() -> {
                    FurnitureRotation rotation = BukkitCustomCropsPlugin.getInstance().getItemManager().remove(bukkitLocation, ExistenceForm.ANY);
                    world.removeBlockState(location);
                    Optional.ofNullable(deathCondition.deathStage()).ifPresent(it -> {
                        BukkitCustomCropsPlugin.getInstance().getItemManager().place(bukkitLocation, deathCondition.existenceForm(), it, rotation);
                    });
                }, deathCondition.deathDelay(), bukkitLocation);
                return;
            }
        }

        if (previousPoint >= config.maxPoints()) {
            return;
        }

        int pointToAdd = 1;
        for (GrowCondition growCondition : config.growConditions()) {
            if (growCondition.isMet(context)) {
                pointToAdd = growCondition.pointToAdd();
                break;
            }
        }

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

        int tempPoints = previousPoint;
        String preStage = null;
        while (tempPoints >= 0) {
            Map.Entry<Integer, CropStageConfig> preEntry = config.getFloorStageEntry(tempPoints);
            CropStageConfig pre = preEntry.getValue();
            if (pre.stageID() != null) {
                preStage = pre.stageID();
                break;
            }
            tempPoints = pre.point() - 1;
        }

        String afterStage = null;
        ExistenceForm afterForm = null;
        tempPoints = afterPoints;
        while (tempPoints >= 0) {
            Map.Entry<Integer, CropStageConfig> afterEntry = config.getFloorStageEntry(tempPoints);
            CropStageConfig after = afterEntry.getValue();
            if (after.stageID() != null) {
                afterStage = after.stageID();
                afterForm = after.existenceForm();
                break;
            }
            tempPoints = after.point() - 1;
        }

        final String finalPreStage = preStage;
        final String finalAfterStage = afterStage;
        final ExistenceForm finalAfterForm = afterForm;

        Objects.requireNonNull(finalAfterStage);
        Objects.requireNonNull(finalPreStage);
        Objects.requireNonNull(finalAfterForm);

        BukkitCustomCropsPlugin.getInstance().getScheduler().sync().run(() -> {
            for (int i = previousPoint + 1; i <= afterPoints; i++) {
                CropStageConfig stage = config.stageByPoint(i);
                if (stage != null) {
                    ActionManager.trigger(context, stage.growActions());
                }
            }
            if (Objects.equals(finalAfterStage, finalPreStage)) return;
            FurnitureRotation rotation = BukkitCustomCropsPlugin.getInstance().getItemManager().remove(bukkitLocation, ExistenceForm.ANY);
            if (rotation == FurnitureRotation.NONE && config.rotation()) {
                rotation = FurnitureRotation.random();
            }
            BukkitCustomCropsPlugin.getInstance().getItemManager().place(bukkitLocation, finalAfterForm, finalAfterStage, rotation);
        }, bukkitLocation);
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
}

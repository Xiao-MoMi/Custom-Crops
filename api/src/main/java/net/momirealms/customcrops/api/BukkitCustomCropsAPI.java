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

package net.momirealms.customcrops.api;

import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.core.BuiltInBlockMechanics;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.FurnitureRotation;
import net.momirealms.customcrops.api.core.Registries;
import net.momirealms.customcrops.api.core.block.BreakReason;
import net.momirealms.customcrops.api.core.block.CropBlock;
import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
import net.momirealms.customcrops.api.core.mechanic.crop.CropStageConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedBreakEvent;
import net.momirealms.customcrops.api.util.DummyCancellable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class BukkitCustomCropsAPI implements CustomCropsAPI {

    private static CustomCropsAPI instance;

    private final BukkitCustomCropsPlugin plugin;

    public BukkitCustomCropsAPI(BukkitCustomCropsPlugin plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static CustomCropsAPI get() {
        return instance;
    }

    @Override
    public Pos3 adapt(Location location) {
        return Pos3.from(location);
    }

    @Override
    public @Nullable CustomCropsWorld<?> getCustomCropsWorld(String name) {
        return plugin.getWorldManager().getWorld(name).orElse(null);
    }

    @Override
    public @Nullable CustomCropsWorld<?> getCustomCropsWorld(World world) {
        return plugin.getWorldManager().getWorld(world).orElse(null);
    }

    @Override
    public void addPointToCrop(Location location, int point) {
        plugin.getWorldManager().getWorld(location.getWorld()).ifPresent(world -> {
            Pos3 pos3 = Pos3.from(location);
            world.getBlockState(pos3).ifPresent(state -> {
                if (state.type() instanceof CropBlock cropBlock) {
                    CropConfig cropConfig = cropBlock.config(state);
                    if (cropConfig == null) return;
                    int currentPoints = cropBlock.point(state);
                    int afterPoints = Math.min(currentPoints + point, cropConfig.maxPoints());
                    if (afterPoints == currentPoints) return;
                    cropBlock.point(state, afterPoints);
                    CropStageConfig currentStage = cropConfig.stageWithModelByPoint(currentPoints);
                    CropStageConfig nextStage = cropConfig.stageWithModelByPoint(afterPoints);
                    if (currentStage == nextStage) return;
                    FurnitureRotation rotation = plugin.getItemManager().remove(location, ExistenceForm.ANY);
                    if (rotation == FurnitureRotation.NONE && cropConfig.rotation()) {
                        rotation = FurnitureRotation.random();
                    }
                    plugin.getItemManager().place(location, nextStage.existenceForm(), Objects.requireNonNull(nextStage.stageID()), rotation);
                    Context<CustomCropsBlockState> context = Context.block(state, location);
                    for (int i = currentPoints + 1; i <= afterPoints; i++) {
                        CropStageConfig stage = cropConfig.stageByPoint(i);
                        if (stage != null) {
                            ActionManager.trigger(context, stage.growActions());
                        }
                    }
                }
            });
        });
    }

    @Override
    public boolean placeCrop(Location location, String id, int point) {
        CropConfig cropConfig = Registries.CROP.get(id);
        if (cropConfig == null) {
            return false;
        }
        Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(location.getWorld());
        if (optionalWorld.isEmpty()) {
            return false;
        }
        CustomCropsWorld<?> world = optionalWorld.get();
        CropBlock cropBlock = (CropBlock) BuiltInBlockMechanics.CROP.mechanic();
        CustomCropsBlockState state = BuiltInBlockMechanics.CROP.createBlockState();
        cropBlock.id(state, id);
        cropBlock.point(state, point);
        CropStageConfig stageConfigWithModel = cropConfig.stageWithModelByPoint(cropBlock.point(state));
        world.addBlockState(Pos3.from(location), state);
        plugin.getScheduler().sync().run(() -> {
            plugin.getItemManager().remove(location, ExistenceForm.ANY);
            plugin.getItemManager().place(location, stageConfigWithModel.existenceForm(), requireNonNull(stageConfigWithModel.stageID()), cropConfig.rotation() ? FurnitureRotation.random() : FurnitureRotation.NONE);
        }, location);
        return true;
    }

    @Override
    public void simulatePlayerBreakCrop(
            Player player,
            EquipmentSlot hand,
            Location location,
            BreakReason reason
    ) {
        Optional<CustomCropsWorld<?>> optionalWorld = plugin.getWorldManager().getWorld(location.getWorld());
        if (optionalWorld.isEmpty()) {
            return;
        }
        Pos3 pos3 = Pos3.from(location);
        CustomCropsWorld<?> world = optionalWorld.get();
        Optional<CustomCropsBlockState> optionalState = world.getBlockState(pos3);
        if (optionalState.isEmpty()) {
            return;
        }
        CustomCropsBlockState state = optionalState.get();
        if (!(state.type() instanceof CropBlock cropBlock)) {
            return;
        }
        CropConfig config = cropBlock.config(state);
        if (config == null) {
            return;
        }
        CropStageConfig stageConfig = config.stageWithModelByPoint(cropBlock.point(state));
        DummyCancellable dummyCancellable = new DummyCancellable();
        if (player != null) {
            ItemStack itemStack = player.getInventory().getItem(hand);
            state.type().onBreak(new WrappedBreakEvent(player, null, hand, location, stageConfig.stageID(), itemStack, plugin.getItemManager().id(itemStack), reason, world, dummyCancellable));
        } else {
            state.type().onBreak(new WrappedBreakEvent(null, null, null, location, stageConfig.stageID(), null, null, reason, world, dummyCancellable));
        }
        if (dummyCancellable.isCancelled()) {
            return;
        }
        world.removeBlockState(pos3);
        plugin.getItemManager().remove(location, ExistenceForm.ANY);
    }
}

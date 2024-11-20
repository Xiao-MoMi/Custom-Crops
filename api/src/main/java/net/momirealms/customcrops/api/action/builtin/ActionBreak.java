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

package net.momirealms.customcrops.api.action.builtin;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.block.BreakReason;
import net.momirealms.customcrops.api.core.block.CropBlock;
import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
import net.momirealms.customcrops.api.core.mechanic.crop.CropStageConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.core.wrapper.WrappedBreakEvent;
import net.momirealms.customcrops.api.misc.value.MathValue;
import net.momirealms.customcrops.api.util.DummyCancellable;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ActionBreak<T> extends AbstractBuiltInAction<T> {

    private final boolean triggerEvent;

    public ActionBreak(
            BukkitCustomCropsPlugin plugin,
            Object args,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.triggerEvent = Optional.ofNullable(args).map(it -> (boolean) it).orElse(true);
    }

    @Override
    protected void triggerAction(Context<T> context) {
        Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
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
        if (triggerEvent) {
            CropStageConfig stageConfig = config.stageWithModelByPoint(cropBlock.point(state));
            Player player = null;
            if (context.holder() instanceof Player p) {
                player = p;
            }
            DummyCancellable dummyCancellable = new DummyCancellable();
            if (player != null) {
                EquipmentSlot slot = requireNonNull(context.arg(ContextKeys.SLOT));
                ItemStack itemStack = player.getInventory().getItem(slot);
                state.type().onBreak(new WrappedBreakEvent(player, null, context.arg(ContextKeys.SLOT), location, stageConfig.stageID(), itemStack, plugin.getItemManager().id(itemStack), BreakReason.ACTION, world, dummyCancellable));
            } else {
                state.type().onBreak(new WrappedBreakEvent(null, null, null, location, stageConfig.stageID(), null, null, BreakReason.ACTION, world, dummyCancellable));
            }
            if (dummyCancellable.isCancelled()) {
                return;
            }
        }
        world.removeBlockState(pos3);
        plugin.getItemManager().remove(location, ExistenceForm.ANY);
    }

    public boolean shouldBreakTriggerEvent() {
        return triggerEvent;
    }
}

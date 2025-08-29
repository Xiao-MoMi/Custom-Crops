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

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.context.Context;
import net.momirealms.customcrops.api.context.ContextKeys;
import net.momirealms.customcrops.api.core.BuiltInBlockMechanics;
import net.momirealms.customcrops.api.core.CustomForm;
import net.momirealms.customcrops.api.core.ExistenceForm;
import net.momirealms.customcrops.api.core.FurnitureRotation;
import net.momirealms.customcrops.api.core.block.CropBlock;
import net.momirealms.customcrops.api.core.block.PotBlock;
import net.momirealms.customcrops.api.core.mechanic.crop.VariationData;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.Fertilizer;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import net.momirealms.customcrops.api.core.world.Pos3;
import net.momirealms.customcrops.api.misc.value.MathValue;
import org.bukkit.Location;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class ActionConsumeWater<T> extends AbstractBuiltInAction<T> {
    private final int yOffset;
    private final int amount;

    public ActionConsumeWater(
            BukkitCustomCropsPlugin plugin,
            Section section,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        this.yOffset = section.getInt("y-offset", -1);
        this.amount = section.getInt("amount", 1);
    }

    @Override
    protected void triggerAction(Context<T> context) {
        Location location = requireNonNull(context.arg(ContextKeys.LOCATION));
        Optional<CustomCropsWorld<?>> world = plugin.getWorldManager().getWorld(location.getWorld());
        if (world.isEmpty()) {
            return;
        }
        Pos3 pos3 = Pos3.from(location);
        if (context.holder() instanceof CustomCropsBlockState state) {
            if (!(state.type() instanceof CropBlock)) {
                return;
            }
        } else {
            Optional<CustomCropsBlockState> cropBlockState = world.get().getBlockState(pos3);
            if (cropBlockState.isEmpty() || !(cropBlockState.get().type() instanceof CropBlock)) {
                return;
            }
        }
        Pos3 potLocation = pos3.add(0, this.yOffset, 0);
        Optional<CustomCropsBlockState> optionalPotState = world.get().getBlockState(potLocation);
        if (optionalPotState.isEmpty()) {
            return;
        }
        CustomCropsBlockState potState = optionalPotState.get();
        if (!(potState.type() instanceof PotBlock potBlock)) {
            return;
        }

        boolean changed = potBlock.consumeWater(potState, 1);
        if (changed) {
            potBlock.updateBlockAppearance(new Location(location.getWorld(), potLocation.x(), potLocation.y(), potLocation.z()), potState);
        }
    }

    public int amount() {
        return amount;
    }

    public int yOffset() {
        return yOffset;
    }
}

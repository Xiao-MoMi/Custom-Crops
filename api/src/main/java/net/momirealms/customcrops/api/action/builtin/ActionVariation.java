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

public class ActionVariation<T> extends AbstractBuiltInAction<T> {

    private final VariationData[] variations;
    private final boolean ignore;

    public ActionVariation(
            BukkitCustomCropsPlugin plugin,
            Section section,
            MathValue<T> chance
    ) {
        super(plugin, chance);
        ignore = section.getBoolean("ignore-fertilizer", false);
        List<VariationData> variationDataList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
            if (entry.getValue() instanceof Section inner) {
                VariationData variationData = new VariationData(
                        inner.getString("item"),
                        CustomForm.valueOf(inner.getString("type", "BLOCK").toUpperCase(Locale.ENGLISH)).existenceForm(),
                        inner.getDouble("chance")
                );
                variationDataList.add(variationData);
            }
        }
        variations = variationDataList.toArray(new VariationData[0]);
    }

    @Override
    protected void triggerAction(Context<T> context) {
        Fertilizer[] fertilizers = null;
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
        if (!ignoreFertilizers()) {
            Pos3 potLocation = pos3.add(0, -1, 0);
            Optional<CustomCropsBlockState> optionalState = world.get().getBlockState(potLocation);
            if (optionalState.isPresent()) {
                if (optionalState.get().type() instanceof PotBlock potBlock) {
                    fertilizers = potBlock.fertilizers(optionalState.get());
                }
            }
        }
        ArrayList<FertilizerConfig> configs = new ArrayList<>();
        if (fertilizers != null) {
            for (Fertilizer fertilizer : fertilizers) {
                Optional.ofNullable(fertilizer.config()).ifPresent(configs::add);
            }
        }
        for (VariationData variationData : variations()) {
            double variationChance = variationData.chance();
            for (FertilizerConfig fertilizer : configs) {
                variationChance = fertilizer.processVariationChance(variationChance);
            }
            if (Math.random() < variationChance) {
                plugin.getItemManager().remove(location, ExistenceForm.ANY);
                world.get().removeBlockState(pos3);
                plugin.getItemManager().place(location, variationData.existenceForm(), variationData.id(), FurnitureRotation.random());
                ((CropBlock) BuiltInBlockMechanics.CROP.mechanic()).fixOrGetState(world.get(), pos3, variationData.id());
                break;
            }
        }
    }

    public VariationData[] variations() {
        return variations;
    }

    public boolean ignoreFertilizers() {
        return ignore;
    }
}

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

package net.momirealms.customcrops.api.core;

import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.item.CustomCropsItem;
import net.momirealms.customcrops.api.core.mechanic.crop.CropConfig;
import net.momirealms.customcrops.api.core.mechanic.fertilizer.FertilizerConfig;
import net.momirealms.customcrops.api.core.mechanic.pot.PotConfig;
import net.momirealms.customcrops.api.core.mechanic.sprinkler.SprinklerConfig;
import net.momirealms.customcrops.api.core.mechanic.wateringcan.WateringCanConfig;
import net.momirealms.customcrops.common.util.Key;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class Registries {

    public static final ClearableRegistry<String, CustomCropsBlock> BLOCKS = new ClearableMappedRegistry<>(Key.key("internal", "blocks"));
    public static final ClearableRegistry<String, CustomCropsItem> ITEMS = new ClearableMappedRegistry<>(Key.key("internal", "items"));

    public static final ClearableRegistry<String, SprinklerConfig> SPRINKLER = new ClearableMappedRegistry<>(Key.key("config", "sprinkler"));
    public static final ClearableRegistry<String, PotConfig> POT = new ClearableMappedRegistry<>(Key.key("config", "pot"));
    public static final ClearableRegistry<String, CropConfig> CROP = new ClearableMappedRegistry<>(Key.key("config", "crop"));
    public static final ClearableRegistry<String, FertilizerConfig> FERTILIZER = new ClearableMappedRegistry<>(Key.key("config", "fertilizer"));
    public static final ClearableRegistry<String, WateringCanConfig> WATERING_CAN = new ClearableMappedRegistry<>(Key.key("config", "watering_can"));

    public static final ClearableRegistry<String, CropConfig> SEED_TO_CROP = new ClearableMappedRegistry<>(Key.key("fast_lookup", "seed_to_crop"));
    public static final ClearableRegistry<String, List<CropConfig>> STAGE_TO_CROP_UNSAFE = new ClearableMappedRegistry<>(Key.key("fast_lookup", "stage_to_crop"));
    public static final ClearableRegistry<String, FertilizerConfig> ITEM_TO_FERTILIZER = new ClearableMappedRegistry<>(Key.key("fast_lookup", "item_to_fertilizer"));
    public static final ClearableRegistry<String, PotConfig> ITEM_TO_POT = new ClearableMappedRegistry<>(Key.key("fast_lookup", "item_to_pot"));
    public static final ClearableRegistry<String, SprinklerConfig> ITEM_TO_SPRINKLER = new ClearableMappedRegistry<>(Key.key("fast_lookup", "item_to_sprinkler"));
    public static final ClearableRegistry<String, WateringCanConfig> ITEM_TO_WATERING_CAN = new ClearableMappedRegistry<>(Key.key("fast_lookup", "item_to_wateringcan"));
    public static final ClearableRegistry<String, Integer> ITEM_TO_DEAD_CROP = new ClearableMappedRegistry<>(Key.key("fast_lookup", "item_to_dead_crop"));
}

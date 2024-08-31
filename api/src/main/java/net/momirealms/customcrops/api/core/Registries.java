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

import net.momirealms.customcrops.api.core.block.CropConfig;
import net.momirealms.customcrops.api.core.block.CustomCropsBlock;
import net.momirealms.customcrops.api.core.block.PotConfig;
import net.momirealms.customcrops.api.core.block.SprinklerConfig;
import net.momirealms.customcrops.api.core.item.CustomCropsItem;
import net.momirealms.customcrops.api.core.item.FertilizerConfig;
import net.momirealms.customcrops.api.core.item.FertilizerType;
import net.momirealms.customcrops.api.core.item.WateringCanConfig;
import net.momirealms.customcrops.common.annotation.DoNotUse;
import net.momirealms.customcrops.common.util.Key;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class Registries {

    @DoNotUse
    public static final WriteableRegistry<Key, CustomCropsBlock> BLOCK = new MappedRegistry<>(Key.key("mechanic", "block"));
    @DoNotUse
    public static final WriteableRegistry<Key, CustomCropsItem> ITEM = new MappedRegistry<>(Key.key("mechanic", "item"));
    @DoNotUse
    public static final WriteableRegistry<String, FertilizerType> FERTILIZER_TYPE = new ClearableMappedRegistry<>(Key.key("mechanic", "fertilizer_type"));
    @DoNotUse
    public static final ClearableRegistry<String, CustomCropsBlock> BLOCKS = new ClearableMappedRegistry<>(Key.key("internal", "blocks"));
    @DoNotUse
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
}

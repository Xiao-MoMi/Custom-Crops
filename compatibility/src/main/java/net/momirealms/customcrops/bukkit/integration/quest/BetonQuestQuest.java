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

package net.momirealms.customcrops.bukkit.integration.quest;

import net.momirealms.customcrops.bukkit.integration.quest.bq.CustomCropsObjectiveFactory;
import net.momirealms.customcrops.bukkit.integration.quest.bq.SimpleCustomCropsObjectiveFactory;
import net.momirealms.customcrops.bukkit.integration.quest.bq.common.BreakScarecrowObjective;
import net.momirealms.customcrops.bukkit.integration.quest.bq.common.PlaceScarecrowObjective;
import net.momirealms.customcrops.bukkit.integration.quest.bq.crops.HarvestCropObjective;
import net.momirealms.customcrops.bukkit.integration.quest.bq.crops.PlantCropObjective;
import net.momirealms.customcrops.bukkit.integration.quest.bq.fertilizers.FertilizerUseObjective;
import net.momirealms.customcrops.bukkit.integration.quest.bq.pots.BreakPotObjective;
import net.momirealms.customcrops.bukkit.integration.quest.bq.pots.PlacePotObjective;
import net.momirealms.customcrops.bukkit.integration.quest.bq.sprinkler.BreakSprinklerObjective;
import net.momirealms.customcrops.bukkit.integration.quest.bq.sprinkler.PlaceSprinklerObjective;
import net.momirealms.customcrops.bukkit.integration.quest.bq.wateringcans.*;
import org.betonquest.betonquest.BetonQuest;

@SuppressWarnings("DuplicatedCode")
public class BetonQuestQuest {

    public static void register() {
        BetonQuest bq = BetonQuest.getInstance();
        // crops
        bq.getQuestRegistries().objective().register("customcrops_harvest_crop", new SimpleCustomCropsObjectiveFactory(HarvestCropObjective::new));
        bq.getQuestRegistries().objective().register("customcrops_plant_crop", new SimpleCustomCropsObjectiveFactory(PlantCropObjective::new));

        // pots
        bq.getQuestRegistries().objective().register("customcrops_place_pot", new SimpleCustomCropsObjectiveFactory(PlacePotObjective::new));
        bq.getQuestRegistries().objective().register("customcrops_break_pot", new SimpleCustomCropsObjectiveFactory(BreakPotObjective::new));

        // watering cans
        bq.getQuestRegistries().objective().register("customcrops_fill_can", new SimpleCustomCropsObjectiveFactory(FillCanObjective::new));
        bq.getQuestRegistries().objective().register("customcrops_water_pot", new CustomCropsObjectiveFactory(WaterPotObjective::new));
        bq.getQuestRegistries().objective().register("customcrops_water_sprinkler", new CustomCropsObjectiveFactory(WaterSprinklerObjective::new));

        // sprinklers
        bq.getQuestRegistries().objective().register("customcrops_place_sprinkler", new SimpleCustomCropsObjectiveFactory(PlaceSprinklerObjective::new));
        bq.getQuestRegistries().objective().register("customcrops_break_sprinkler", new SimpleCustomCropsObjectiveFactory(BreakSprinklerObjective::new));

        // fertilizers
        bq.getQuestRegistries().objective().register("customcrops_use_fertilizer", new CustomCropsObjectiveFactory(FertilizerUseObjective::new));

        // commons
        bq.getQuestRegistries().objective().register("customcrops_place_scarecrow", new SimpleCustomCropsObjectiveFactory(PlaceScarecrowObjective::new));
        bq.getQuestRegistries().objective().register("customcrops_break_scarecrow", new SimpleCustomCropsObjectiveFactory(BreakScarecrowObjective::new));
    }
}

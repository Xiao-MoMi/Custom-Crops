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

import net.momirealms.customcrops.bukkit.integration.quest.bq.crops.HarvestCropObjectiveFactory;
import net.momirealms.customcrops.bukkit.integration.quest.bq.crops.PlantCropObjectiveFactory;
import net.momirealms.customcrops.bukkit.integration.quest.bq.pots.BreakPotObjectiveFactory;
import net.momirealms.customcrops.bukkit.integration.quest.bq.pots.PlacePotObjectiveFactory;
import net.momirealms.customcrops.bukkit.integration.quest.bq.sprinkler.BreakSprinklerObjectiveFactory;
import net.momirealms.customcrops.bukkit.integration.quest.bq.sprinkler.PlaceSprinklerObjectiveFactory;
import net.momirealms.customcrops.bukkit.integration.quest.bq.wateringcans.*;
import org.betonquest.betonquest.BetonQuest;

@SuppressWarnings("DuplicatedCode")
public class BetonQuestQuest {

    public static void register() {
        BetonQuest bq = BetonQuest.getInstance();
        // crops
        bq.getQuestRegistries().objective().register("customcrops_harvest_crop", new HarvestCropObjectiveFactory());
        bq.getQuestRegistries().objective().register("customcrops_plant_crop", new PlantCropObjectiveFactory());

        // pots
        bq.getQuestRegistries().objective().register("customcrops_place_pot", new PlacePotObjectiveFactory());
        bq.getQuestRegistries().objective().register("customcrops_break_pot", new BreakPotObjectiveFactory());

        // watering cans
        bq.getQuestRegistries().objective().register("customcrops_fill_can", new FillCanObjectiveFactory());
        bq.getQuestRegistries().objective().register("customcrops_water_pot", new WateringObjectiveFactory(WaterPotObjective::new));
        bq.getQuestRegistries().objective().register("customcrops_water_sprinkler", new WateringObjectiveFactory(WaterSprinklerObjective::new));

        // sprinklers
        bq.getQuestRegistries().objective().register("customcrops_place_sprinkler", new PlaceSprinklerObjectiveFactory());
        bq.getQuestRegistries().objective().register("customcrops_break_sprinkler", new BreakSprinklerObjectiveFactory());
    }
}

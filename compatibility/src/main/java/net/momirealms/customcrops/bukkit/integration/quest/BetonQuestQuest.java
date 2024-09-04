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

import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.HashSet;

@SuppressWarnings("DuplicatedCode")
public class BetonQuestQuest {

    public static void register() {
        BetonQuest.getInstance().registerObjectives("customcrops_harvest", HarvestObjective.class);
        BetonQuest.getInstance().registerObjectives("customcrops_plant", PlantObjective.class);
    }

    public static class HarvestObjective extends CountingObjective implements Listener {

        private final VariableLocation playerLocation;
        private final VariableNumber rangeVar;
        private final HashSet<String> crop_ids;

        public HarvestObjective(Instruction instruction) throws InstructionParseException {
            super(instruction, "crop_to_harvest");
            crop_ids = new HashSet<>();
            Collections.addAll(crop_ids, instruction.getArray());
            targetAmount = instruction.getVarNum(VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
            final QuestPackage pack = instruction.getPackage();
            final String loc = instruction.getOptional("playerLocation");
            final String range = instruction.getOptional("range");
            if (loc != null && range != null) {
                playerLocation = new VariableLocation(BetonQuest.getInstance().getVariableProcessor(), pack, loc);
                rangeVar = new VariableNumber(BetonQuest.getInstance().getVariableProcessor(), pack, range);
            } else {
                playerLocation = null;
                rangeVar = null;
            }
        }

        @EventHandler (ignoreCancelled = true)
        public void onBreakCrop(CropBreakEvent event) {
            if (!(event.entityBreaker() instanceof Player player)) {
                return;
            }
            String id = event.cropStageItemID();

            OnlineProfile onlineProfile = PlayerConverter.getID(player);
            if (!containsPlayer(onlineProfile)) {
                return;
            }
            if (isInvalidLocation(player, onlineProfile)) {
                return;
            }
            if (this.crop_ids.contains(id) && this.checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress(1);
                completeIfDoneOrNotify(onlineProfile);
            }
        }

        private boolean isInvalidLocation(Player player, final Profile profile) {
            if (playerLocation == null || rangeVar == null) {
                return false;
            }

            final Location targetLocation;
            try {
                targetLocation = playerLocation.getValue(profile);
            } catch (final org.betonquest.betonquest.exceptions.QuestRuntimeException e) {
                return true;
            }
            int range;
            try {
                range = rangeVar.getValue(profile).intValue();
            } catch (QuestRuntimeException e) {
                throw new RuntimeException(e);
            }
            final Location playerLoc = player.getLocation();
            return !playerLoc.getWorld().equals(targetLocation.getWorld()) || targetLocation.distanceSquared(playerLoc) > range * range;
        }

        @Override
        public void start() {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }

        @Override
        public void stop() {
            HandlerList.unregisterAll(this);
        }
    }

    public static class PlantObjective extends CountingObjective implements Listener {

        private final VariableLocation playerLocation;
        private final VariableNumber rangeVar;
        private final HashSet<String> crops;

        public PlantObjective(Instruction instruction) throws InstructionParseException {
            super(instruction, "crop_to_plant");
            crops = new HashSet<>();
            Collections.addAll(crops, instruction.getArray());
            targetAmount = instruction.getVarNum(VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
            final QuestPackage pack = instruction.getPackage();
            final String loc = instruction.getOptional("playerLocation");
            final String range = instruction.getOptional("range");
            if (loc != null && range != null) {
                playerLocation = new VariableLocation(BetonQuest.getInstance().getVariableProcessor(), pack, loc);
                rangeVar = new VariableNumber(BetonQuest.getInstance().getVariableProcessor(), pack, range);
            } else {
                playerLocation = null;
                rangeVar = null;
            }
        }

        @EventHandler (ignoreCancelled = true)
        public void onPlantCrop(CropPlantEvent event) {
            OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
            if (!containsPlayer(onlineProfile)) {
                return;
            }
            if (isInvalidLocation(event.getPlayer(), onlineProfile)) {
                return;
            }
            if (this.crops.contains(event.cropConfig().id()) && this.checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress(1);
                completeIfDoneOrNotify(onlineProfile);
            }
        }

        private boolean isInvalidLocation(Player player, final Profile profile) {
            if (playerLocation == null || rangeVar == null) {
                return false;
            }

            final Location targetLocation;
            try {
                targetLocation = playerLocation.getValue(profile);
            } catch (final org.betonquest.betonquest.exceptions.QuestRuntimeException e) {
                return true;
            }
            int range;
            try {
                range = rangeVar.getValue(profile).intValue();
            } catch (QuestRuntimeException e) {
                throw new RuntimeException(e);
            }
            final Location playerLoc = player.getLocation();
            return !playerLoc.getWorld().equals(targetLocation.getWorld()) || targetLocation.distanceSquared(playerLoc) > range * range;
        }

        @Override
        public void start() {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }

        @Override
        public void stop() {
            HandlerList.unregisterAll(this);
        }
    }
}

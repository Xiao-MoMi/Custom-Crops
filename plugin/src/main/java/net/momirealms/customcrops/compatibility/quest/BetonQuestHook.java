/*
 *  Copyright (C) <2022> <XiaoMoMi>
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

package net.momirealms.customcrops.compatibility.quest;

import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;
import net.momirealms.customcrops.api.util.LogUtils;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.HashSet;

@SuppressWarnings("DuplicatedCode")
public class BetonQuestHook {

    public static void register() {
        BetonQuest.getInstance().registerObjectives("customcrops_harvest", HarvestObjective.class);
        BetonQuest.getInstance().registerObjectives("customcrops_plant", PlantObjective.class);
    }

    public static class HarvestObjective extends CountingObjective implements Listener {

        private final CompoundLocation playerLocation;
        private final VariableNumber rangeVar;
        private final HashSet<String> crop_ids;

        public HarvestObjective(Instruction instruction) throws InstructionParseException {
            super(instruction, "crop_to_harvest");
            crop_ids = new HashSet<>();
            Collections.addAll(crop_ids, instruction.getArray());
            targetAmount = instruction.getVarNum();
            preCheckAmountNotLessThanOne(targetAmount);
            final QuestPackage pack = instruction.getPackage();
            final String loc = instruction.getOptional("playerLocation");
            final String range = instruction.getOptional("range");
            if (loc != null && range != null) {
                playerLocation = new CompoundLocation(pack, loc);
                rangeVar = new VariableNumber(pack, range);
            } else {
                playerLocation = null;
                rangeVar = null;
            }
        }

        @EventHandler (ignoreCancelled = true)
        public void onBreakCrop(CropBreakEvent event) {
            if (event.getPlayer() == null) {
                return;
            }
            WorldCrop crop = event.getWorldCrop();
            if (crop == null) return;

            OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
            if (!containsPlayer(onlineProfile)) {
                return;
            }
            if (isInvalidLocation(event, onlineProfile)) {
                return;
            }
            if (this.crop_ids.contains(crop.getConfig().getStageItemByPoint(crop.getPoint())) && this.checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress(1);
                completeIfDoneOrNotify(onlineProfile);
            }
        }

        private boolean isInvalidLocation(CropBreakEvent event, final Profile profile) {
            if (playerLocation == null || rangeVar == null) {
                return false;
            }

            final Location targetLocation;
            try {
                targetLocation = playerLocation.getLocation(profile);
            } catch (final org.betonquest.betonquest.exceptions.QuestRuntimeException e) {
                LogUtils.warn(e.getMessage());
                return true;
            }
            final int range = rangeVar.getInt(profile);
            final Location playerLoc = event.getPlayer().getLocation();
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

        private final CompoundLocation playerLocation;
        private final VariableNumber rangeVar;
        private final HashSet<String> loot_groups;

        public PlantObjective(Instruction instruction) throws InstructionParseException {
            super(instruction, "crop_to_plant");
            loot_groups = new HashSet<>();
            Collections.addAll(loot_groups, instruction.getArray());
            targetAmount = instruction.getVarNum();
            preCheckAmountNotLessThanOne(targetAmount);
            final QuestPackage pack = instruction.getPackage();
            final String loc = instruction.getOptional("playerLocation");
            final String range = instruction.getOptional("range");
            if (loc != null && range != null) {
                playerLocation = new CompoundLocation(pack, loc);
                rangeVar = new VariableNumber(pack, range);
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
            if (isInvalidLocation(event, onlineProfile)) {
                return;
            }
            if (this.loot_groups.contains(event.getCrop().getKey()) && this.checkConditions(onlineProfile)) {
                getCountingData(onlineProfile).progress(1);
                completeIfDoneOrNotify(onlineProfile);
            }
        }

        private boolean isInvalidLocation(CropPlantEvent event, final Profile profile) {
            if (playerLocation == null || rangeVar == null) {
                return false;
            }

            final Location targetLocation;
            try {
                targetLocation = playerLocation.getLocation(profile);
            } catch (final org.betonquest.betonquest.exceptions.QuestRuntimeException e) {
                LogUtils.warn(e.getMessage());
                return true;
            }
            final int range = rangeVar.getInt(profile);
            final Location playerLoc = event.getPlayer().getLocation();
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

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

package net.momirealms.customcrops.integration.quest;

import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.util.AdventureUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.logging.Level;

public class LegacyBetonQuestCCQuest extends Objective implements Listener {

    private final HashSet<String> crop_ids = new HashSet<>();
    private final int amount;
    private final boolean notify;
    private final int notifyInterval;

    public LegacyBetonQuestCCQuest(Instruction instruction) throws InstructionParseException {
        super(instruction);
        this.template = CropData.class;
        this.notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        this.notify = instruction.hasArgument("notify") || this.notifyInterval > 1;
        this.amount = instruction.getInt(instruction.getOptional("amount"), 1);
        Collections.addAll(this.crop_ids, instruction.getArray());
    }

    public static void register() {
        BetonQuest.getInstance().registerObjectives("customfishing", LegacyBetonQuestCCQuest.class);
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return Integer.toString(this.amount);
    }

    @Override
    public String getProperty(String name, String playerID) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "amount" ->
                    Integer.toString(this.amount - ((LegacyBetonQuestCCQuest.CropData) this.dataMap.get(playerID)).getAmount());
            case "left" -> Integer.toString(((LegacyBetonQuestCCQuest.CropData) this.dataMap.get(playerID)).getAmount());
            case "total" -> Integer.toString(this.amount);
            default -> "";
        };
    }

    private boolean isValidPlayer(Player player) {
        if (player == null) {
            return false;
        } else {
            return player.isOnline() && player.isValid();
        }
    }

    @EventHandler
    public void onHarvest(CropBreakEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        String playerID = PlayerConverter.getID(player);
        if (this.containsPlayer(playerID)) {
            if (this.crop_ids.contains(event.getCropItemID())) {
                if (this.checkConditions(playerID)) {
                    if (!isValidPlayer(player)) {
                        return;
                    }
                    CropData cropData = (CropData) this.dataMap.get(playerID);
                    cropData.harvest(1);
                    if (cropData.finished()) {
                        this.completeObjective(playerID);
                    }
                    else if (this.notify && cropData.getAmount() % this.notifyInterval == 0) {
                        try {
                            Config.sendNotify(this.instruction.getPackage().getName(), playerID, "crop_to_harvest", new String[]{String.valueOf(cropData.getAmount())}, "crop_to_harvest,info");
                        } catch (QuestRuntimeException e1) {
                            try {
                                LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'crop_to_harvest' category in '" + this.instruction.getObjective().getFullID() + "'. Error was: '" + e1.getMessage() + "'");
                            } catch (InstructionParseException e2) {
                                LogUtils.logThrowableReport(e2);
                            }
                        }
                    }
                }
            }
        }
    }

    public static class CropData extends Objective.ObjectiveData {
        private int amount;

        public CropData(String instruction, String playerID, String objID) {
            super(instruction, playerID, objID);
            try {
                this.amount = Integer.parseInt(instruction);
            }
            catch (NumberFormatException e) {
                AdventureUtils.consoleMessage("[CustomCrops] NumberFormatException");
                this.amount = 1;
            }
        }

        public void harvest(int caughtAmount) {
            this.amount -= caughtAmount;
            this.update();
        }

        public int getAmount() {
            return this.amount;
        }

        public String toString() {
            return String.valueOf(this.amount);
        }

        public boolean finished() {
            return this.amount <= 0;
        }
    }
}
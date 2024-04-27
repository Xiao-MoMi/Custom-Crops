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

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.events.server.PluginReloadEvent;
import net.advancedplugins.bp.impl.actions.ActionRegistry;
import net.advancedplugins.bp.impl.actions.external.executor.ActionQuestExecutor;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import net.momirealms.customcrops.api.mechanic.item.Crop;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class BattlePassHook implements Listener {

    public BattlePassHook() {
        Bukkit.getPluginManager().registerEvents(this, CustomCropsPlugin.get());
    }

    public void register() {
        ActionRegistry actionRegistry = BattlePlugin.getPlugin().getActionRegistry();
        actionRegistry.hook("customcrops", BPHarvestCropsQuest::new);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBattlePassReload(PluginReloadEvent event){
        register();
    }

    private static class BPHarvestCropsQuest extends ActionQuestExecutor {
        public BPHarvestCropsQuest(JavaPlugin plugin) {
            super(plugin, "customcrops");
        }

        @EventHandler (ignoreCancelled = true)
        public void onBreakCrop(CropBreakEvent event){
            Player player = event.getPlayer();
            if (player == null) return;

            WorldCrop worldCrop = event.getWorldCrop();
            if (worldCrop == null) return;
            String id = worldCrop.getConfig().getStageItemByPoint(worldCrop.getPoint());

            // Harvest crops
            this.executionBuilder("harvest")
                    .player(player)
                    .root(id)
                    .progress(1)
                    .buildAndExecute();
        }

        @EventHandler (ignoreCancelled = true)
        public void onPlantCrop(CropPlantEvent event){
            Player player = event.getPlayer();

            Crop crop = event.getCrop();

            // Harvest crops
            this.executionBuilder("plant")
                    .player(player)
                    .root(crop.getKey())
                    .progress(1)
                    .buildAndExecute();
        }
    }
}

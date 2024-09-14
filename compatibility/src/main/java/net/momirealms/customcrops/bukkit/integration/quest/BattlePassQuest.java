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

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.events.server.PluginReloadEvent;
import net.advancedplugins.bp.impl.actions.ActionRegistry;
import net.advancedplugins.bp.impl.actions.external.executor.ActionQuestExecutor;
import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class BattlePassQuest implements Listener {

    public BattlePassQuest() {
        Bukkit.getPluginManager().registerEvents(this, BukkitCustomCropsPlugin.getInstance().getBootstrap());
    }

    public void register() {
        ActionRegistry actionRegistry = BattlePlugin.getPlugin().getActionRegistry();
        actionRegistry.hook("customcrops", BPCropsQuest::new);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBattlePassReload(PluginReloadEvent event){
        register();
    }

    @SuppressWarnings("deprecation")
    private static class BPCropsQuest extends ActionQuestExecutor {
        public BPCropsQuest(JavaPlugin plugin) {
            super(plugin, "customcrops");
        }

        @EventHandler (ignoreCancelled = true)
        public void onBreakCrop(CropBreakEvent event) {
            Entity entity = event.entityBreaker();
            if (!(entity instanceof Player player)) return;
            String id = event.cropStageItemID();
            // remove namespace
            if (id.contains(":")) {
                id = id.split(":")[1];
            }
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
            // Plant crops
            this.executionBuilder("plant")
                    .player(player)
                    .root(event.cropConfig().id())
                    .progress(1)
                    .buildAndExecute();
        }
    }
}

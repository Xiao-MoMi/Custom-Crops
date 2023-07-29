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

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.events.server.PluginReloadEvent;
import io.github.battlepass.quests.service.base.ExternalQuestContainer;
import io.github.battlepass.registry.quest.QuestRegistry;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BattlePassCCQuest implements Listener {

    public static void register() {
        QuestRegistry questRegistry = BattlePlugin.getApi().getQuestRegistry();
        questRegistry.hook("customcrops", CropQuest::new);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBattlePassReload(PluginReloadEvent event) {
        register();
    }

    private static class CropQuest extends ExternalQuestContainer {

        public CropQuest(BattlePlugin battlePlugin) {
            super(battlePlugin, "customcrops");
        }

        @EventHandler
        public void onHarvest(CropBreakEvent event) {
            if (event.isCancelled())
                return;
            if (event.getEntity() instanceof Player player) {
                String id = event.getCropItemID();
                String[] split = id.split(":");
                this.executionBuilder("harvest")
                        .player(player)
                        .root(split[split.length - 1])
                        .progress(1)
                        .buildAndExecute();
            }
        }

        @EventHandler
        public void onPlant(CropPlantEvent event) {
            if (event.isCancelled())
                return;
            String id = event.getCropKey();
            this.executionBuilder("plant")
                    .player(event.getPlayer())
                    .root(id)
                    .progress(1)
                    .buildAndExecute();
        }
    }
}
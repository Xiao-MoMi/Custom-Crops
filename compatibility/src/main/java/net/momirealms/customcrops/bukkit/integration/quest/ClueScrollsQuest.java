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

import com.electro2560.dev.cluescrolls.api.*;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import net.momirealms.customfishing.api.BukkitCustomFishingPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ClueScrollsQuest implements Listener {

    private final CustomClue harvestClue;
    private final CustomClue plantClue;

    public ClueScrollsQuest() {
        harvestClue = ClueScrollsAPI.getInstance().registerCustomClue(BukkitCustomFishingPlugin.getInstance().getBoostrap(), "harvest", new ClueConfigData("id", DataType.STRING));
        plantClue = ClueScrollsAPI.getInstance().registerCustomClue(BukkitCustomFishingPlugin.getInstance().getBoostrap(), "plant", new ClueConfigData("id", DataType.STRING));
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, BukkitCustomFishingPlugin.getInstance().getBoostrap());
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreakCrop(CropBreakEvent event) {
        if (!(event.getEntityBreaker() instanceof Player player)) return;
        harvestClue.handle(
                player,
                1,
                new ClueDataPair("id", event.getStageItemID())
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlantCrop(CropPlantEvent event) {
        plantClue.handle(
                event.getPlayer(),
                1,
                new ClueDataPair("id", event.getCropConfig().id())
        );
    }
}

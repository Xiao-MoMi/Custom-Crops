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

import com.electro2560.dev.cluescrolls.api.*;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import net.momirealms.customcrops.api.event.CropPlantEvent;
import net.momirealms.customcrops.api.mechanic.world.level.WorldCrop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ClueScrollsHook implements Listener {

    private final CustomClue harvestClue;
    private final CustomClue plantClue;

    public ClueScrollsHook() {
        harvestClue = ClueScrollsAPI.getInstance().registerCustomClue(CustomCropsPlugin.getInstance(), "harvest", new ClueConfigData("id", DataType.STRING));
        plantClue = ClueScrollsAPI.getInstance().registerCustomClue(CustomCropsPlugin.getInstance(), "plant", new ClueConfigData("id", DataType.STRING));
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, CustomCropsPlugin.get());
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreakCrop(CropBreakEvent event) {
        final Player player = event.getPlayer();
        if (player == null) return;

        WorldCrop crop = event.getWorldCrop();
        if (crop == null) return;

        harvestClue.handle(
                player,
                1,
                new ClueDataPair("id", crop.getConfig().getStageItemByPoint(crop.getPoint()))
        );
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlantCrop(CropPlantEvent event) {
        plantClue.handle(
                event.getPlayer(),
                1,
                new ClueDataPair("id", event.getCrop().getKey())
        );
    }
}

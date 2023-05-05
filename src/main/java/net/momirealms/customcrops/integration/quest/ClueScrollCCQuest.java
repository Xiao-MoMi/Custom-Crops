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

import com.electro2560.dev.cluescrolls.api.*;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ClueScrollCCQuest implements Listener {

    private final CustomClue commonClue;

    public ClueScrollCCQuest(Plugin plugin) {
        commonClue = ClueScrollsAPI.getInstance().registerCustomClue(plugin, "harvest", new ClueConfigData("crop_id", DataType.STRING));
    }

    @EventHandler
    public void onHarvest(CropBreakEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity() instanceof Player player) {
            commonClue.handle(player, 1, new ClueDataPair("crop_id", event.getCropItemID()));
        }
    }
}

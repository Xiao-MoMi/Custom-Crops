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

package net.momirealms.customcrops.integrations.quest;

import com.electro2560.dev.cluescrolls.api.*;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.event.CropHarvestEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ClueScrollCCQuest implements Listener {

    private final CustomClue commonClue;

    public ClueScrollCCQuest() {
        commonClue = ClueScrollsAPI.getInstance().registerCustomClue(CustomCrops.plugin, "harvest", new ClueConfigData("crop_id", DataType.STRING));
    }

    @EventHandler
    public void onHarvest(CropHarvestEvent event) {
        if (event.isCancelled()) return;
        commonClue.handle(event.getPlayer(), 1, new ClueDataPair("crop_id", event.getCrop().getKey()));
    }
}

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

package net.momirealms.customcrops.api.object.action;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.crop.StageConfig;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class BreakImpl implements Action {

    private final boolean triggerAction;
    private final String stage_id;

    public BreakImpl(boolean triggerAction, @Nullable String stage_id) {
        this.triggerAction = triggerAction;
        this.stage_id = stage_id;
    }

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation crop_loc, ItemMode itemMode) {
        if (crop_loc == null) return;
        CustomCrops.getInstance().getScheduler().runTask(() -> {
            CustomCrops.getInstance().getPlatformInterface().removeCustomItem(crop_loc.getBukkitLocation(), itemMode);
            CustomCrops.getInstance().getWorldDataManager().removeCropData(crop_loc);
        });
        if (triggerAction && stage_id != null) {
            StageConfig stageConfig = CustomCrops.getInstance().getCropManager().getStageConfig(stage_id);
            if (stageConfig != null) {
                Action[] actions = stageConfig.getBreakActions();
                if (actions != null) {
                    for (Action action : actions) {
                        action.doOn(player, crop_loc, itemMode);
                    }
                }
            }
        }
    }
}

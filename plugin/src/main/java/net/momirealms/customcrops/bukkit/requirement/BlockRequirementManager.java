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

package net.momirealms.customcrops.bukkit.requirement;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.action.ActionManager;
import net.momirealms.customcrops.api.core.block.CropBlock;
import net.momirealms.customcrops.api.core.world.CustomCropsBlockState;
import net.momirealms.customcrops.api.requirement.AbstractRequirementManager;

public class BlockRequirementManager extends AbstractRequirementManager<CustomCropsBlockState> {

    public BlockRequirementManager(BukkitCustomCropsPlugin plugin) {
        super(plugin, CustomCropsBlockState.class);
    }

    @Override
    protected void registerBuiltInRequirements() {
        super.registerBuiltInRequirements();
        this.registerPointCondition();
    }

    @Override
    public void load() {
        loadExpansions(CustomCropsBlockState.class);
    }

    private void registerPointCondition() {
        registerRequirement((args, actions, runActions) -> {
            int value = (int) args;
            return (context) -> {
                CustomCropsBlockState state = context.holder();
                if (state.type() instanceof CropBlock cropBlock) {
                    int point = cropBlock.point(state);
                    if (point > value) return true;
                }
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "point_more_than", "point-more-than");
        registerRequirement((args, actions, runActions) -> {
            int value = (int) args;
            return (context) -> {
                CustomCropsBlockState state = context.holder();
                if (state.type() instanceof CropBlock cropBlock) {
                    int point = cropBlock.point(state);
                    if (point < value) return true;
                }
                if (runActions) ActionManager.trigger(context, actions);
                return false;
            };
        }, "point_less_than", "point-less-than");
    }
}

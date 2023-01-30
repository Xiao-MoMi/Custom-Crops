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

package net.momirealms.customcrops.objects.requirements;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequirementYPos extends Requirement implements RequirementInterface {

    public RequirementYPos(@NotNull String[] values, boolean mode, @Nullable String msg) {
        super(values, mode, msg);
    }

    @Override
    public boolean isConditionMet(PlayerCondition playerCondition) {
        int y = playerCondition.getLocation().getBlockY();
        if (mode) {
            for (String value : values) {
                String[] yMinMax = StringUtils.split(value, "~");
                if (!(y > Long.parseLong(yMinMax[0]) && y < Long.parseLong(yMinMax[1]))) {
                    notMetMessage(playerCondition.getPlayer());
                    return false;
                }
            }
            return true;
        }
        else {
            for (String value : values) {
                String[] yMinMax = StringUtils.split(value, "~");
                if (y > Long.parseLong(yMinMax[0]) && y < Long.parseLong(yMinMax[1])) {
                    return true;
                }
            }
            notMetMessage(playerCondition.getPlayer());
            return false;
        }
    }
}
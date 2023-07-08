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

package net.momirealms.customcrops.api.object.requirement;

import net.momirealms.customcrops.api.object.action.Action;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class YPosImpl extends AbstractRequirement implements Requirement {

    private final List<String> yPos;

    public YPosImpl(@Nullable String[] msg, @Nullable Action[] actions, List<String> yPos) {
        super(msg, actions);
        this.yPos = yPos;
    }

    @Override
    public boolean isConditionMet(CurrentState currentState) {
        int y = (int) currentState.getLocation().getY();
        for (String range : yPos) {
            String[] yMinMax = range.split("~");
            if (y > Integer.parseInt(yMinMax[0]) && y < Integer.parseInt(yMinMax[1])) {
                return true;
            }
        }
        notMetActions(currentState);
        return false;
    }
}
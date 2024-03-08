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

package net.momirealms.customcrops.api.mechanic.item;

import net.momirealms.customcrops.api.common.item.KeyItem;
import net.momirealms.customcrops.api.mechanic.action.ActionTrigger;
import net.momirealms.customcrops.api.mechanic.item.water.PassiveFillMethod;
import net.momirealms.customcrops.api.mechanic.misc.image.WaterBar;
import net.momirealms.customcrops.api.mechanic.requirement.Requirement;
import net.momirealms.customcrops.api.mechanic.requirement.State;

import java.util.HashSet;

public interface Sprinkler extends KeyItem {

    String get2DItemID();

    String get3DItemID();

    String get3DItemWithWater();

    int getStorage();

    int getRange();

    boolean isInfinite();

    int getWater();

    HashSet<String> getPotWhitelist();

    ItemCarrier getItemCarrier();

    PassiveFillMethod[] getPassiveFillMethods();

    Requirement[] getPlaceRequirements();

    Requirement[] getBreakRequirements();

    Requirement[] getUseRequirements();

    void trigger(ActionTrigger trigger, State state);

    WaterBar getWaterBar();
}

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

package net.momirealms.customcrops.api.core.mechanic.wateringcan;

import net.momirealms.customcrops.api.action.Action;
import net.momirealms.customcrops.api.misc.value.TextValue;
import net.momirealms.customcrops.api.misc.water.FillMethod;
import net.momirealms.customcrops.api.misc.water.WaterBar;
import net.momirealms.customcrops.api.requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WateringCanConfig {

    String id();

    String itemID();

    int width();

    int length();

    int storage();

    int wateringAmount();

    boolean dynamicLore();

    Set<String> whitelistPots();

    Set<String> whitelistSprinklers();

    List<TextValue<Player>> lore();

    WaterBar waterBar();

    Requirement<Player>[] requirements();

    boolean infinite();

    Integer appearance(int water);

    Action<Player>[] fullActions();

    Action<Player>[] addWaterActions();

    Action<Player>[] consumeWaterActions();

    Action<Player>[] runOutOfWaterActions();

    Action<Player>[] wrongPotActions();

    Action<Player>[] wrongSprinklerActions();

    FillMethod[] fillMethods();

    static Builder builder() {
        return new WateringCanConfigImpl.BuilderImpl();
    }

    interface Builder {

        WateringCanConfig build();

        Builder id(String id);

        Builder itemID(String itemID);

        Builder width(int width);

        Builder length(int length);

        Builder storage(int storage);

        Builder wateringAmount(int wateringAmount);

        Builder dynamicLore(boolean dynamicLore);

        Builder potWhitelist(Set<String> whitelistPots);

        Builder sprinklerWhitelist(Set<String> whitelistSprinklers);

        Builder lore(List<TextValue<Player>> lore);

        Builder waterBar(WaterBar waterBar);

        Builder requirements(Requirement<Player>[] requirements);

        Builder infinite(boolean infinite);

        Builder appearances(Map<Integer, Integer> appearances);

        Builder fullActions(Action<Player>[] fullActions);

        Builder addWaterActions(Action<Player>[] addWaterActions);

        Builder consumeWaterActions(Action<Player>[] consumeWaterActions);

        Builder runOutOfWaterActions(Action<Player>[] runOutOfWaterActions);

        Builder wrongPotActions(Action<Player>[] wrongPotActions);

        Builder wrongSprinklerActions(Action<Player>[] wrongSprinklerActions);

        Builder fillMethods(FillMethod[] fillMethods);
    }
}

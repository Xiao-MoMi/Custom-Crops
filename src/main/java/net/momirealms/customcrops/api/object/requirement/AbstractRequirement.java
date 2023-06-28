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
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.api.util.AdventureUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractRequirement {

    protected String[] msg;
    protected Action[] actions;

    protected AbstractRequirement(@Nullable String[] msg, @Nullable Action[] actions) {
        this.msg = msg;
        this.actions = actions;
    }

    public void notMetActions(CurrentState currentState) {
        Player player = currentState.getPlayer();
        if (msg != null && player != null) {
            for (String str : msg) {
                AdventureUtils.playerMessage(player, str);
            }
        }
        if (actions != null) {
            for (Action action : actions) {
                action.doOn(player, SimpleLocation.getByBukkitLocation(currentState.getLocation()), null);
            }
        }
    }
}

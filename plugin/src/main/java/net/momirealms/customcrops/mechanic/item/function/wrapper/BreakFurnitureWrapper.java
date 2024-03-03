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

package net.momirealms.customcrops.mechanic.item.function.wrapper;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BreakFurnitureWrapper extends BreakWrapper {

    private final Location location;
    private final String id;

    public BreakFurnitureWrapper(Player player, Location location, String id) {
        super(player, location);
        this.location = location;
        this.id = id;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public String getID() {
        return id;
    }
}

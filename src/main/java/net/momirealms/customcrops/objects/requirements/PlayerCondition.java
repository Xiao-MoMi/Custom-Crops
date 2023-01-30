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

import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class PlayerCondition {

    private final Location location;
    private final Player player;
    private HashMap<String, String> papiMap;

    public PlayerCondition(Location location, Player player) {
        this.location = location;
        this.player = player;
        if (CustomCrops.plugin.hasPapi()){
            this.papiMap = new HashMap<>();
            CustomPapi.allPapi.forEach(papi -> this.papiMap.put(papi, CustomCrops.plugin.getPlaceholderManager().parse(player, papi)));
        }
    }

    @Nullable
    public HashMap<String, String> getPapiMap() {
        return papiMap;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }
}
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

package net.momirealms.customcrops.api.object.condition;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.CrowTask;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CrowAttack implements Condition {

    private final double chance;
    private final String fly_model;
    private final String stand_model;

    public CrowAttack(double chance, String fly_model, String stand_model) {
        this.chance = chance;
        this.fly_model = fly_model;
        this.stand_model = stand_model;
    }

    @Override
    public boolean isMet(SimpleLocation simpleLocation) {
        if (Math.random() > chance) return false;
        if (CustomCrops.getInstance().getWorldDataManager().hasScarecrow(simpleLocation)) return false;
        Location location = simpleLocation.getBukkitLocation();
        if (location == null) return false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            SimpleLocation playerLoc = SimpleLocation.getByBukkitLocation(player.getLocation());
            if (playerLoc.isNear(simpleLocation, 48)) {
                new CrowTask(player, location, fly_model, stand_model).runTaskTimerAsynchronously(CustomCrops.getInstance(), 1, 1);
            }
        }
        return true;
    }
}
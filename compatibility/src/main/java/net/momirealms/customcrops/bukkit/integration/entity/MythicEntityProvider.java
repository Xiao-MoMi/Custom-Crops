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

package net.momirealms.customcrops.bukkit.integration.entity;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.momirealms.customcrops.api.integration.EntityProvider;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class MythicEntityProvider implements EntityProvider {

    private MythicBukkit mythicBukkit;

    @Override
    public String identifier() {
        return "MythicMobs";
    }

    @NotNull
    @Override
    public Entity spawn(@NotNull Location location, @NotNull String id, @NotNull Map<String, Object> propertyMap) {
        if (this.mythicBukkit == null || mythicBukkit.isClosed()) {
            this.mythicBukkit = MythicBukkit.inst();
        }
        Optional<MythicMob> mythicMob = mythicBukkit.getMobManager().getMythicMob(id);
        if (mythicMob.isPresent()) {
            MythicMob theMob = mythicMob.get();
            Position position = Position.of(location);
            AbstractLocation abstractLocation = new AbstractLocation(position);
            ActiveMob activeMob = theMob.spawn(abstractLocation, (double) propertyMap.getOrDefault("level", 0d));
            return activeMob.getEntity().getBukkitEntity();
        }
        throw new NullPointerException("MythicMobs: " + id + " doesn't exist.");
    }
}

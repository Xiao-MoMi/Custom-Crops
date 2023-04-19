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

package net.momirealms.customcrops.api;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.crop.GrowingCrop;
import net.momirealms.customcrops.api.object.pot.Pot;
import net.momirealms.customcrops.api.object.sprinkler.Sprinkler;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class CustomCropsAPI {

    private static CustomCropsAPI instance;
    private final CustomCrops plugin;

    public CustomCropsAPI(CustomCrops plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static CustomCropsAPI getInstance() {
        return instance;
    }

    @Nullable
    public Pot getPotAt(Location location) {
        return plugin.getWorldDataManager().getPotData(SimpleLocation.getByBukkitLocation(location));
    }

    @Nullable
    public GrowingCrop getCropAt(Location location) {
        return plugin.getWorldDataManager().getCropData(SimpleLocation.getByBukkitLocation(location));
    }

    public boolean isGreenhouseGlass(Location location) {
        return plugin.getWorldDataManager().isGreenhouse(SimpleLocation.getByBukkitLocation(location));
    }

    public boolean hasScarecrowInChunk(Location location) {
        return plugin.getWorldDataManager().hasScarecrow(SimpleLocation.getByBukkitLocation(location));
    }

    public Sprinkler getSprinklerAt(Location location) {
        return plugin.getWorldDataManager().getSprinklerData(SimpleLocation.getByBukkitLocation(location));
    }
}

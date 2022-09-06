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

package net.momirealms.customcrops.integrations.protection;

import com.plotsquared.core.location.Location;
import org.bukkit.entity.Player;

public class PlotSquaredIntegration implements Integration {

    @Override
    public boolean canBreak(org.bukkit.Location location, Player player) {
        return isAllowed(location, player);
    }

    @Override
    public boolean canPlace(org.bukkit.Location location, Player player) {
        return isAllowed(location, player);
    }

    private boolean isAllowed(org.bukkit.Location location, Player player) {
        Location plotLoc = Location.at(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (plotLoc.isPlotRoad()) return false;
        if (plotLoc.getPlotArea() != null) return plotLoc.getPlotArea().getPlot(plotLoc).isAdded(player.getUniqueId());
        else return true;
    }
}

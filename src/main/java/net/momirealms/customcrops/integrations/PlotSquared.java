package net.momirealms.customcrops.integrations;

import com.plotsquared.core.location.Location;
import org.bukkit.entity.Player;

public class PlotSquared implements Integration {

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
        if (plotLoc.getPlotArea() != null){
            return plotLoc.getPlotArea().getPlot(plotLoc).isAdded(player.getUniqueId());
        }else {
            return true;
        }
    }
}

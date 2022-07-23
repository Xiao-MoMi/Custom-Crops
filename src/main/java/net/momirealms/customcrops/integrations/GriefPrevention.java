package net.momirealms.customcrops.integrations;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefPrevention implements Integration{

    @Override
    public boolean canBreak(Location location, Player player) {
        return me.ryanhamshire.GriefPrevention.GriefPrevention.instance.allowBreak(player, location.getBlock(), location) == null;
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        return me.ryanhamshire.GriefPrevention.GriefPrevention.instance.allowBuild(player, location) == null;
    }
}

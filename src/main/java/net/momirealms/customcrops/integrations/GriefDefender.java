package net.momirealms.customcrops.integrations;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefDefender implements Integration {

    @Override
    public boolean canBreak(Location location, Player player) {
        return com.griefdefender.api.GriefDefender.getCore().getUser(player.getUniqueId()).canBreak(location);
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        return com.griefdefender.api.GriefDefender.getCore().getUser(player.getUniqueId()).canPlace(player.getInventory().getItemInMainHand(), location);
    }
}

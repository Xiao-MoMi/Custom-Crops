package net.momirealms.customcrops.integrations;

import com.griefdefender.api.GriefDefender;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefDefenderIntegrations {

    public static boolean checkGDBreak(Location location, Player player){
        return GriefDefender.getCore().getUser(player.getUniqueId()).canBreak(location);
    }

    public static boolean checkGDBuild(Location location, Player player){
        return GriefDefender.getCore().getUser(player.getUniqueId()).canPlace(player.getInventory().getItemInMainHand(), location);
    }
}

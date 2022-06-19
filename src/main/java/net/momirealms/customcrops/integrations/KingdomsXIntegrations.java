package net.momirealms.customcrops.integrations;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.player.KingdomPlayer;

public class KingdomsXIntegrations {
    public static boolean checkKDBuild(Location location, Player player){
        Land land = Land.getLand(location);
        if (land == null) return true;
        if (land.isClaimed()) {
            KingdomPlayer kp = KingdomPlayer.getKingdomPlayer(player);
            Kingdom cropKingdom = land.getKingdom();
            if (kp.getKingdom() != null) {
                Kingdom kingdom = kp.getKingdom();
                return kingdom != cropKingdom;
            }else {
                return false;
            }
        }
        return true;
    }
}

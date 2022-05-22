package net.momirealms.customcrops.Integrations;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.player.KingdomPlayer;

public class KingdomsXIntegrations {
    public static boolean checkKDBuild(Location location, Player player){
        KingdomPlayer kp = KingdomPlayer.getKingdomPlayer(player);
        Land land = Land.getLand(location);
        if (land == null) return false;
        if (player.isOp()) {
            return false;
        }
        if (land.isClaimed()) {
            Kingdom cropKingdom = land.getKingdom();
            if (kp.getKingdom() != null) {
                Kingdom kingdom = kp.getKingdom();
                return kingdom != cropKingdom;
            }else {
                return true;
            }
        }
        return false;
    }
}

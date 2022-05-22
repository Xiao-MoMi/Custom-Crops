package net.momirealms.customcrops.Integrations;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ResidenceIntegrations {
    public static boolean checkResBuild(Location location, Player player){
        FlagPermissions.addFlag("build");
        ClaimedResidence res = com.bekvon.bukkit.residence.Residence.getInstance().getResidenceManager().getByLoc(location);
        if(res!=null){
            ResidencePermissions perms = res.getPermissions();
            String playerName = player.getName();
            boolean hasPermission = perms.playerHas(playerName, "build", true);
            return !hasPermission;
        }
        return false;
    }
    public static boolean checkResHarvest(Location location, Player player){
        FlagPermissions.addFlag("harvest");
        ClaimedResidence res = com.bekvon.bukkit.residence.Residence.getInstance().getResidenceManager().getByLoc(location);
        if(res!=null){
            ResidencePermissions perms = res.getPermissions();
            String playerName = player.getName();
            boolean hasPermission = perms.playerHas(playerName, "harvest", true);
            return !hasPermission;
        }
        return false;
    }
}

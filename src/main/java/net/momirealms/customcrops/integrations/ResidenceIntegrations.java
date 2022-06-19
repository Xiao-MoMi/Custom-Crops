package net.momirealms.customcrops.integrations;

import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ResidenceIntegrations {
    public static boolean checkResBuild(Location location, Player player){
        ClaimedResidence res = com.bekvon.bukkit.residence.Residence.getInstance().getResidenceManager().getByLoc(location);
        if(res!=null){
            ResidencePermissions perms = res.getPermissions();
            return perms.playerHas(player, Flags.build, true);
        }
        return true;
    }
    public static boolean checkResHarvest(Location location, Player player){
        ClaimedResidence res = com.bekvon.bukkit.residence.Residence.getInstance().getResidenceManager().getByLoc(location);
        if(res!=null){
            ResidencePermissions perms = res.getPermissions();
            return perms.playerHas(player, Flags.harvest, true);
        }
        return true;
    }
}

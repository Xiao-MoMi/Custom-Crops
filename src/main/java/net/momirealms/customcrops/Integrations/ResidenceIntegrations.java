package net.momirealms.customcrops.Integrations;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import org.bukkit.Location;

public class ResidenceIntegrations {
    public static boolean checkResBuild(Location location, CustomBlockInteractEvent event){
        FlagPermissions.addFlag("build");
        ClaimedResidence res = com.bekvon.bukkit.residence.Residence.getInstance().getResidenceManager().getByLoc(location);
        if(res!=null){
            ResidencePermissions perms = res.getPermissions();
            String playerName = event.getPlayer().getName();
            boolean hasPermission = perms.playerHas(playerName, "build", true);
            if(!hasPermission){
                event.setCancelled(true);
                return true;
            }
        }
        return false;
    }
    public static boolean checkResHarvest(Location location, CustomBlockInteractEvent event){
        FlagPermissions.addFlag("harvest");
        ClaimedResidence res = com.bekvon.bukkit.residence.Residence.getInstance().getResidenceManager().getByLoc(location);
        if(res!=null){
            ResidencePermissions perms = res.getPermissions();
            String playerName = event.getPlayer().getName();
            boolean hasPermission = perms.playerHas(playerName, "harvest", true);
            if(!hasPermission){
                event.setCancelled(true);
                return true;
            }
        }
        return false;
    }
}

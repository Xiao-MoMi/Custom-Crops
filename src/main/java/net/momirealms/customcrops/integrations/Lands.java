package net.momirealms.customcrops.integrations;

import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Lands implements Integration{

    @Override
    public boolean canBreak(Location location, Player player) {
        Area area = new LandsIntegration(CustomCrops.instance).getAreaByLoc(location);
        if (area != null){
            return area.hasFlag(player, Flags.BLOCK_BREAK, false);
        }else {
            return true;
        }
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        Area area = new LandsIntegration(CustomCrops.instance).getAreaByLoc(location);
        if (area != null){
            return area.hasFlag(player, Flags.BLOCK_PLACE, false);
        }else {
            return true;
        }
    }
}

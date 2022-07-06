package net.momirealms.customcrops.integrations;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Towny implements Integration{

    @Override
    public boolean canBreak(Location location, Player player) {
        return TownyPermission(player, location, TownyPermission.ActionType.DESTROY);
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        return TownyPermission(player, location, TownyPermission.ActionType.BUILD);
    }

    private boolean TownyPermission(Player player, Location location, TownyPermission.ActionType actionType){
        return PlayerCacheUtil.getCachePermission(player, location, location.getBlock().getType(), actionType);
    }
}

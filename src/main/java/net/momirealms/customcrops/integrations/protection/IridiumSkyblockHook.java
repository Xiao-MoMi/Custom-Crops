package net.momirealms.customcrops.integrations.protection;

import com.iridium.iridiumskyblock.PermissionType;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;
import net.momirealms.customcrops.integrations.AntiGrief;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

public class IridiumSkyblockHook implements AntiGrief {

    private final IridiumSkyblockAPI api;

    public IridiumSkyblockHook() {
        this.api =  IridiumSkyblockAPI.getInstance();
    }

    @Override
    public boolean canBreak(Location location, Player player) {
        Optional<Island> island = api.getIslandViaLocation(location);
        User user = api.getUser(player);
        return island.map(value -> api.getIslandPermission(value, user, PermissionType.BLOCK_BREAK)).orElse(true);
    }

    @Override
    public boolean canPlace(Location location, Player player) {
        Optional<Island> island = api.getIslandViaLocation(location);
        User user = api.getUser(player);
        return island.map(value -> api.getIslandPermission(value, user, PermissionType.BLOCK_PLACE)).orElse(true);
    }
}

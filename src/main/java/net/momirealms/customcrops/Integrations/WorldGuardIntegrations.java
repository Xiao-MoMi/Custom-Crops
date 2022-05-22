package net.momirealms.customcrops.Integrations;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardIntegrations {
    public static boolean checkWGBuild(Player player, Location loc, CustomBlockInteractEvent event){

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if (!query.testState(BukkitAdapter.adapt(loc), localPlayer, Flags.BUILD)) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }
    public static boolean checkWGHarvest(Player player, Location loc, CustomBlockInteractEvent event){

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if (!query.testState(BukkitAdapter.adapt(loc), localPlayer, Flags.BLOCK_BREAK)) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }
}

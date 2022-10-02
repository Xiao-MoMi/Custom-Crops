package net.momirealms.customcrops.utils;

import net.momirealms.customcrops.objects.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class MiscUtils {

    public static SimpleLocation getSimpleLocation(Location location) {
        return new SimpleLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Nullable
    public static Location getLocation(SimpleLocation location) {
        World world = Bukkit.getWorld(location.getWorldName());
        if (world == null) return null;
        return new Location(world, location.getX(), location.getY(), location.getZ());
    }
}

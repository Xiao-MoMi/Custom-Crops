package net.momirealms.customcrops.utils;

import net.momirealms.customcrops.objects.SimpleLocation;
import org.bukkit.Location;

public class LocUtil {
    /**
     * 将Location转换为SimpleLocation
     * @param location Location
     * @return SimpleLocation
     */
    public static SimpleLocation fromLocation(Location location){
        return new SimpleLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}

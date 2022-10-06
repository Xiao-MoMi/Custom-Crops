package net.momirealms.customcrops.api.utils;

import net.momirealms.customcrops.CustomCrops;
import org.bukkit.World;

public class WorldUtils {

    /**
     * load a world's crop data
     * @param world world
     */
    public static void loadCropWorld(World world) {
        CustomCrops.plugin.getCropManager().onWorldLoad(world);
    }

    /**
     * unload a world's crop data
     * @param world world
     * @param disable whether the server is stopping
     */
    public static void unloadCropWorld(World world, boolean disable) {
        CustomCrops.plugin.getCropManager().onWorldUnload(world, disable);
    }
}

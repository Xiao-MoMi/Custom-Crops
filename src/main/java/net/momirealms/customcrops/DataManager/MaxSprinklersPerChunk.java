package net.momirealms.customcrops.DataManager;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.IAFurniture;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class MaxSprinklersPerChunk {
    public static boolean maxSprinklersPerChunk(Location location){
        FileConfiguration config = CustomCrops.instance.getConfig();
        if(!config.getBoolean("config.enable-limit")){
            return false;
        }
        int maxY = config.getInt("config.height.max");
        int minY = config.getInt("config.height.min");
        int maxAmount = config.getInt("config.max-sprinklers");

        int n = 1;

        Location chunkLocation = new Location(location.getWorld(),location.getChunk().getX()*16,minY,location.getChunk().getZ()*16);
        World world = location.getWorld();

        Label_out:
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                final Location square = chunkLocation.clone().add((double)(i+0.5), 0.5, (double)(j+0.5));
                for (int k = minY; k <= maxY; ++k) {
                    square.add(0.0, 1.0, 0.0);
                    if(IAFurniture.getFromLocation(square, world)){
                        if (n++ > maxAmount) {
                            break Label_out;
                        }
                    }
                }
            }
        }
        return n > maxAmount;
    }
}

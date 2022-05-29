package net.momirealms.customcrops.datamanager;

import net.momirealms.customcrops.ConfigManager;
import net.momirealms.customcrops.IAFurniture;
import org.bukkit.Location;
import org.bukkit.World;

public class MaxSprinklersPerChunk {

    public static boolean maxSprinklersPerChunk(Location location){

        if(!ConfigManager.Config.limit){
            return false;
        }
        int maxY = ConfigManager.Config.maxh;
        int minY = ConfigManager.Config.minh;
        int maxAmount = ConfigManager.Config.max_sprinkler;

        int n = 1;

        Location chunkLocation = new Location(location.getWorld(),location.getChunk().getX()*16,minY,location.getChunk().getZ()*16);
        World world = location.getWorld();

        Label_out:
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                Location square = chunkLocation.clone().add(i+0.5, 0.5, j+0.5);
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
    public static boolean alreadyPlaced(Location location){
        return IAFurniture.getFromLocation(location.clone().add(0.5, 1.5, 0.5), location.getWorld());
    }
}

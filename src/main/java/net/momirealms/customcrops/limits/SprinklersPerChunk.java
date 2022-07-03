package net.momirealms.customcrops.limits;

import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.utils.IAFurniture;
import org.bukkit.Location;
import org.bukkit.World;

public class SprinklersPerChunk {

    public static boolean isLimited(Location location){
        if(!ConfigReader.Config.enableLimit){
            return false;
        }
        int n = 1;
        Location chunkLocation = new Location(location.getWorld(),location.getChunk().getX()*16,ConfigReader.Config.yMin,location.getChunk().getZ()*16);
        World world = location.getWorld();
        Label_out:
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                Location square = chunkLocation.clone().add(i + 0.5, 0.5, j + 0.5);
                for (int k = ConfigReader.Config.yMin; k <= ConfigReader.Config.yMax; ++k) {
                    square.add(0.0, 1.0, 0.0);
                    if(IAFurniture.getFromLocation(square, world)){
                        if (n++ > ConfigReader.Config.sprinklerLimit) {
                            break Label_out;
                        }
                    }
                }
            }
        }
        return n > ConfigReader.Config.sprinklerLimit;
    }
}

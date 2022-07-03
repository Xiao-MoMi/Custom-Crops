package net.momirealms.customcrops.limits;

import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.ConfigReader;
import org.bukkit.Location;

public class CropsPerChunk {

    public static boolean isLimited(Location location){
        if(!ConfigReader.Config.enableLimit){
            return false;
        }
        int n = 1;
        Location chunkLocation = new Location(location.getWorld(),location.getChunk().getX()*16,ConfigReader.Config.yMin,location.getChunk().getZ()*16);
        Label_out:
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                Location square = chunkLocation.clone().add(i, 0.0, j);
                for (int k = ConfigReader.Config.yMin; k <= ConfigReader.Config.yMax; ++k) {
                    square.add(0.0, 1.0, 0.0);
                    CustomBlock customBlock = CustomBlock.byAlreadyPlaced(square.getBlock());
                    if(customBlock != null){
                        if (customBlock.getNamespacedID().contains("_stage_")) {
                            if (n++ > ConfigReader.Config.cropLimit) {
                                break Label_out;
                            }
                        }
                    }
                }
            }
        }
        return n > ConfigReader.Config.cropLimit;
    }
}

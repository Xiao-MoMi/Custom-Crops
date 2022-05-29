package net.momirealms.customcrops.datamanager;

import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.ConfigManager;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class MaxCropsPerChunk {

    public static boolean maxCropsPerChunk(Location location){

        if(!ConfigManager.Config.limit){
            return false;
        }
        int maxY = ConfigManager.Config.maxh;
        int minY = ConfigManager.Config.minh;
        int maxAmount = ConfigManager.Config.max_crop;

        int n = 1;

        Location chunkLocation = new Location(location.getWorld(),location.getChunk().getX()*16,minY,location.getChunk().getZ()*16);

        Label_out:
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                Location square = chunkLocation.clone().add(i, 0.0, j);
                for (int k = minY; k <= maxY; ++k) {
                    square.add(0.0, 1.0, 0.0);
                    Block b = square.getBlock();
                    if(CustomBlock.byAlreadyPlaced(b)!= null){
                        if (CustomBlock.byAlreadyPlaced(b).getNamespacedID().contains("stage")) {
                            if (n++ > maxAmount) {
                                break Label_out;
                            }
                        }
                    }
                }
            }
        }
        return n > maxAmount;
    }
}

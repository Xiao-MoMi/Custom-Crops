package net.momirealms.customcrops.DataManager;

import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

public class MaxCropsPerChunk {

    static FileConfiguration config = CustomCrops.instance.getConfig();

    public static boolean maxCropsPerChunk(Location location){

        if(!config.getBoolean("config.enable-limit")){
            return false;
        }
        int maxY = config.getInt("config.height.max");
        int minY = config.getInt("config.height.min");
        int maxAmount = config.getInt("config.max-crops");

        int n = 1;

        Location chunkLocation = new Location(location.getWorld(),location.getChunk().getX()*16,minY,location.getChunk().getZ()*16);

        Label_out:
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                Location square = chunkLocation.clone().add(i, 0.0, j);
                for (int k = minY; k <= maxY; ++k) {
                    square.add(0.0, 1.0, 0.0);
                    Block b = location.getWorld().getBlockAt(square);
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

package net.momirealms.customcrops.DataManager;

import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class MaxCropsPerChunk {

    public static boolean maxCropsPerChunk(Location location){
        FileConfiguration config = CustomCrops.instance.getConfig();
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
                final Location square = chunkLocation.clone().add((double)i, 0.0, (double)j);
                for (int k = minY; k <= maxY; ++k) {
                    square.add(0.0, 1.0, 0.0);
                    if(CustomBlock.byAlreadyPlaced(location.getWorld().getBlockAt(square))!= null){
                        if (CustomBlock.byAlreadyPlaced(location.getWorld().getBlockAt(square)).getNamespacedID().contains("stage")) {
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

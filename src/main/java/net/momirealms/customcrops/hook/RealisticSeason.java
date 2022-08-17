package net.momirealms.customcrops.hook;

import me.casperge.realisticseasons.api.SeasonsAPI;
import org.bukkit.World;

public class RealisticSeason {

    public static String getSeason(World world){
        SeasonsAPI seasonsapi = SeasonsAPI.getInstance();
        switch (seasonsapi.getSeason(world)){
            case SPRING -> {
                return "spring";
            }
            case SUMMER -> {
                return "summer";
            }
            case WINTER -> {
                return "winter";
            }
            case FALL -> {
                return "autumn";
            }
        }
        return "null";
    }
}

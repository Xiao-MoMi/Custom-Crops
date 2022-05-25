package net.momirealms.customcrops.Integrations;

import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class IntegrationCheck {

    static FileConfiguration config = CustomCrops.instance.getConfig();

    //收获权限检测
    public static boolean HarvestCheck(Location location, Player player){
        if(config.getBoolean("config.integration.kingdomsX")){
            if(KingdomsXIntegrations.checkKDBuild(location,player)){
                return true;
            }
        }
        if(config.getBoolean("config.integration.residence")){
            if(ResidenceIntegrations.checkResHarvest(location,player)){
                return true;
            }
        }
        if(config.getBoolean("config.integration.worldguard")){
            return WorldGuardIntegrations.checkWGHarvest(location, player);
        }
        return false;
    }
    //种植等权限检测
    public static boolean PlaceCheck(Location location, Player player){
        if(config.getBoolean("config.integration.kingdomsX")){
            if(KingdomsXIntegrations.checkKDBuild(location,player)){
                return true;
            }
        }
        if(config.getBoolean("config.integration.residence")){
            if(ResidenceIntegrations.checkResBuild(location,player)){
                return true;
            }
        }
        if(config.getBoolean("config.integration.worldguard")){
            return WorldGuardIntegrations.checkWGBuild(location, player);
        }
        return false;
    }
}

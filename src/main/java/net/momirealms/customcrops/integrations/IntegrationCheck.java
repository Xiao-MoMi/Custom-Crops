package net.momirealms.customcrops.integrations;

import net.momirealms.customcrops.ConfigManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class IntegrationCheck {

    //收获权限检测
    public static boolean HarvestCheck(Location location, Player player){
        if(ConfigManager.Config.king){
            if(KingdomsXIntegrations.checkKDBuild(location,player)){
                return true;
            }
        }
        if(ConfigManager.Config.res){
            if(ResidenceIntegrations.checkResHarvest(location,player)){
                return true;
            }
        }
        if(ConfigManager.Config.wg){
            return WorldGuardIntegrations.checkWGHarvest(location, player);
        }
        return false;
    }
    //种植等权限检测
    public static boolean PlaceCheck(Location location, Player player){
        if(ConfigManager.Config.king){
            if(KingdomsXIntegrations.checkKDBuild(location,player)){
                return true;
            }
        }
        if(ConfigManager.Config.res){
            if(ResidenceIntegrations.checkResBuild(location,player)){
                return true;
            }
        }
        if(ConfigManager.Config.wg){
            return WorldGuardIntegrations.checkWGBuild(location, player);
        }
        return false;
    }
}

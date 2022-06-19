package net.momirealms.customcrops.integrations;

import net.momirealms.customcrops.datamanager.ConfigManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class IntegrationCheck {

    //收获权限检测
    public static boolean HarvestCheck(Location location, Player player){
        boolean canH = false;
        if(ConfigManager.Config.res){
            if(ResidenceIntegrations.checkResHarvest(location, player)){
                canH = true;
            }else {
                return false;
            }
        }
        if(ConfigManager.Config.king){
            if(KingdomsXIntegrations.checkKDBuild(location, player)){
                canH = true;
            }else {
                return false;
            }
        }
        if(ConfigManager.Config.wg){
            if(WorldGuardIntegrations.checkWGHarvest(location, player)){
                canH = true;
            }else {
                return false;
            }
        }
        if(ConfigManager.Config.gd){
            if(GriefDefenderIntegrations.checkGDBreak(location, player)){
                canH = true;
            }else {
                return false;
            }
        }
        return canH;
    }
    //种植等权限检测
    public static boolean PlaceCheck(Location location, Player player){
        boolean canB = false;
        if(ConfigManager.Config.res){
            if(ResidenceIntegrations.checkResBuild(location,player)){
                canB = true;
            }else {
                return false;
            }
        }
        if(ConfigManager.Config.king){
            if(KingdomsXIntegrations.checkKDBuild(location,player)){
                canB = true;
            }else {
                return false;
            }
        }
        if(ConfigManager.Config.wg){
            if(WorldGuardIntegrations.checkWGBuild(location, player)){
                canB = true;
            }else {
                return false;
            }
        }
        if(ConfigManager.Config.gd){
            if(GriefDefenderIntegrations.checkGDBuild(location, player)){
                canB = true;
            }else {
                return false;
            }
        }
        return canB;
    }
}
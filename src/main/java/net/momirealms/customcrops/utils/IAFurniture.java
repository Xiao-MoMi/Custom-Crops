package net.momirealms.customcrops.utils;

import dev.lone.itemsadder.api.CustomFurniture;
import net.momirealms.customcrops.ConfigReader;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

public class IAFurniture {

    //放置IA自定义家具
    public static void placeFurniture(String name, Location location){
        CustomFurniture.spawn(name, location.getBlock());
    }

    //根据位置获取盔甲架，如果是洒水器返回true，否则返回false
    public static boolean getFromLocation(Location location, World world){
        for(Entity entity : world.getNearbyEntities(location,0,0,0)){
            if(entity instanceof ArmorStand armorStand){
                if(CustomFurniture.byAlreadySpawned(armorStand) != null){
                    if(ConfigReader.SPRINKLERS.get(CustomFurniture.byAlreadySpawned(armorStand).getId()) != null){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
package net.momirealms.customcrops.utils;

import dev.lone.itemsadder.api.CustomFurniture;
import net.momirealms.customcrops.ConfigReader;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

public class IAFurniture {

    public static void placeFurniture(String name, Location location){
        CustomFurniture.spawn(name, location.getBlock());
    }

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
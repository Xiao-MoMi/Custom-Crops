/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.utils;

import dev.lone.itemsadder.api.CustomFurniture;
import net.momirealms.customcrops.ConfigReader;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class IAFurnitureUtil {

    /**
     * 在指定位置放置家具
     * @param name 物品名
     * @param location 位置
     */
    public static void placeFurniture(String name, Location location){
        CustomFurniture.spawn(name, location.getBlock());
    }

    /**
     * 判断指定位置的盔甲架是不是洒水器
     * 仅限加载中的区块
     * @param location 位置
     * @return 是/否
     */
    public static String getFromLocation(Location location){
        for(Entity entity : location.getWorld().getNearbyEntities(location,0,0,0)){
            CustomFurniture furniture = CustomFurniture.byAlreadySpawned(entity);
            if(furniture != null) return furniture.getNamespacedID();
        }
        return null;
    }

    public static boolean isSprinkler(Location location){
        String furniture = getFromLocation(location);
        if (furniture != null) return ConfigReader.SPRINKLERS.get(furniture) != null;
        else return false;
    }
}
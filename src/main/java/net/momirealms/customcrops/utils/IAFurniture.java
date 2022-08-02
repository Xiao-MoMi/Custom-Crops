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
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

public class IAFurniture {

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
     * @param world 世界
     * @return 是/否
     */
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
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
import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import java.util.Random;

public class FurnitureUtil {

    static Rotation[] rotations4 = {Rotation.NONE, Rotation.FLIPPED, Rotation.CLOCKWISE, Rotation.COUNTER_CLOCKWISE};
    static Rotation[] rotations8 = {Rotation.NONE, Rotation.FLIPPED, Rotation.CLOCKWISE, Rotation.COUNTER_CLOCKWISE,
            Rotation.CLOCKWISE_45, Rotation.CLOCKWISE_135, Rotation.FLIPPED_45, Rotation.COUNTER_CLOCKWISE_45};

    /**
     * 在指定位置放置家具
     * @param name 物品名
     * @param location 位置
     */
    public static void placeFurniture(String name, Location location){
        CustomFurniture.spawn(name, location.getBlock());
    }

    /**
     * 在指定位置放置农作物
     * @param name 农作物命名空间id
     * @param location 位置
     */
    public static void placeCrop(String name, Location location){
        CustomFurniture customFurniture = CustomFurniture.spawn(name, location.getBlock());
        Entity entity = customFurniture.getArmorstand();
        if (ConfigReader.Config.rotation && entity instanceof ItemFrame itemFrame){
            if (ConfigReader.Config.variant4) itemFrame.setRotation(rotations4[new Random().nextInt(rotations4.length-1)]);
            else itemFrame.setRotation(rotations8[new Random().nextInt(rotations8.length-1)]);
        }
    }

    /**
     * 获取指定位置的家具名
     * 仅限加载中的区块
     * @param location 位置
     * @return 是/否
     */
    public static String getNamespacedID(Location location){
        CustomFurniture furniture = getFurniture(location);
        if (furniture != null){
            return furniture.getNamespacedID();
        }else {
            return null;
        }
    }

    /**
     * 获取指定位置的家具名
     * 仅限加载中的区块
     * @param location 位置
     * @return 是/否
     */
    public static CustomFurniture getFurniture(Location location){
        for(Entity entity : location.getWorld().getNearbyEntities(location,0,0,0)){
            CustomFurniture furniture = CustomFurniture.byAlreadySpawned(entity);
            if(furniture != null) return furniture;
        }
        return null;
    }

    /**
     * 判断指定位置的家具是不是洒水器
     * @param location 位置
     * @return 是/否
     */
    public static boolean isSprinkler(Location location){
        String furniture = getNamespacedID(location);
        if (furniture != null) return ConfigReader.SPRINKLERS.get(furniture) != null;
        else return false;
    }
}
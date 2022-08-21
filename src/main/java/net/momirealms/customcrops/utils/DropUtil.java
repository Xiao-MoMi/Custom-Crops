package net.momirealms.customcrops.utils;

import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.objects.Crop;
import org.bukkit.Location;
import org.bukkit.World;

public class DropUtil {

    /**
     * 没有品质肥料下的普通掉落
     * @param cropInstance 农作物
     * @param random 随机农作物数量
     * @param itemLoc 掉落物位置
     * @param world 世界
     */
    public static void normalDrop(Crop cropInstance, int random, Location itemLoc, World world) {
        for (int i = 0; i < random; i++){
            double ran = Math.random();
            if (ran < ConfigReader.Config.quality_1){
                world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_1()).getItemStack());
            }else if(ran > ConfigReader.Config.quality_2){
                world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_2()).getItemStack());
            }else {
                world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_3()).getItemStack());
            }
        }
    }

}

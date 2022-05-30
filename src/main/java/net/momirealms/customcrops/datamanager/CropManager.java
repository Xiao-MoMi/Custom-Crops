package net.momirealms.customcrops.datamanager;

import dev.lone.itemsadder.api.CustomBlock;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.utils.Crop;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CropManager {

    public static ConcurrentHashMap<Location, String> CROPS;
    /*
    开服的时候将文件的数据读入
    */
    public static void loadData() {

        File file = new File(CustomCrops.instance.getDataFolder(), "crop-data.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        CROPS = new ConcurrentHashMap<>();

        for (String world : ConfigManager.Config.worlds) {
            //如果数据文件中有相应世界才进行读取
            if(data.contains(world)){
                for (String coordinate : data.getConfigurationSection(world).getKeys(false)) {
                    Location tempLoc = new Location(Bukkit.getWorld(world), Integer.parseInt(coordinate.split(",")[0]), Integer.parseInt(coordinate.split(",")[1]), Integer.parseInt(coordinate.split(",")[2]));
                    String cropName = data.getString(world + "." + coordinate);
                    CROPS.put(tempLoc, cropName);
                }
            }
        }
    }

    /*
    保存数据
    */
    public static void saveData(){

        File file = new File(CustomCrops.instance.getDataFolder(), "crop-data.yml");
        FileConfiguration data;
        data = YamlConfiguration.loadConfiguration(file);

        //性能更高
        Set<Map.Entry<Location, String>> en = CROPS.entrySet();
        for(Map.Entry<Location, String> entry : en){
            Location loc = entry.getKey();
            data.set(loc.getWorld().getName()+"."+ loc.getBlockX() + "," + loc.getBlockY()+ ","+loc.getBlockZ(), entry.getValue());
        }
        try {
            data.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
            CustomCrops.instance.getLogger().warning("农作物数据保存出错");
        }
    }

    /*
    添加农作物实例
    */
    public static void putInstance(Location location, String crop) {
        CROPS.put(location, crop);
    }

    /*
    生长部分
    */
    public static void CropGrow() {
        /*
        阶段1：更新数据
        */
        long start1 = System.currentTimeMillis();
        File file = new File(CustomCrops.instance.getDataFolder(), "crop-data.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();

        Set<Map.Entry<Location, String>> en = CROPS.entrySet();
        for(Map.Entry<Location, String> entry : en){
            Location key = entry.getKey();
            data.set(key.getWorld().getName() + "." + key.getBlockX() + "," + key.getBlockY()+ ","+ key.getBlockZ(), entry.getValue());
        }
        long finish1 = System.currentTimeMillis();
        MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops] &7农作物数据更新耗时&a" + (finish1 - start1) + "&fms",Bukkit.getConsoleSender());

        /*
        阶段2：清理数据内无效的农作物并让有效农作物生长
        */
        long start2 = System.currentTimeMillis();
        ConfigManager.Config.worlds.forEach(worldName ->{
            if(data.contains(worldName)){

                World world = Bukkit.getWorld(worldName);
                data.getConfigurationSection(worldName).getKeys(false).forEach(key ->{

                    String[] coordinate = StringUtils.split(key,",");
                    //先判断区块是否加载，未加载则不进行下一步计算
                    if (world.isChunkLoaded(Integer.parseInt(coordinate[0])/16, Integer.parseInt(coordinate[2])/16)){

                        Location sLoc = new Location(world,Double.parseDouble(coordinate[0]),Double.parseDouble(coordinate[1]),Double.parseDouble(coordinate[2]));
                        CustomBlock seedBlock = CustomBlock.byAlreadyPlaced(sLoc.getBlock());

                        if(seedBlock == null){
                            CROPS.remove(sLoc);
                            data.set(worldName+"."+coordinate[0]+","+coordinate[1]+","+coordinate[2], null);
                        }else{
                            String namespacedID = seedBlock.getNamespacedID();
                            /*
                            对之前旧版本的一些兼容
                            以及一些意料之外的情况，防止报错
                            */
                            if(namespacedID.equalsIgnoreCase(ConfigManager.Config.dead)){
                                CROPS.remove(sLoc);
                                data.set(worldName+"."+coordinate[0]+","+coordinate[1]+","+coordinate[2], null);
                                return;
                            }
                            if(namespacedID.contains("_stage_")){

                                Location potLoc = sLoc.clone().subtract(0,1,0);
                                Block potBlock = potLoc.getBlock();
                                CustomBlock pot = CustomBlock.byAlreadyPlaced(potBlock);

                                if (pot != null){
                                    String potName = pot.getNamespacedID();
                                    /*
                                    是湿润的种植盆吗
                                    */
                                    if (potName.equalsIgnoreCase(ConfigManager.Config.watered_pot)){

                                        String[] split = StringUtils.split(namespacedID,":");
                                        String[] cropNameList = StringUtils.split(split[1],"_");
                                        Crop crop = ConfigManager.CONFIG.get(cropNameList[0]);

                                        //季节判断
                                        Label_out:
                                        if(ConfigManager.Config.season){
                                            if(ConfigManager.Config.greenhouse){
                                                for(int i = 1; i <= ConfigManager.Config.range; i++){
                                                    CustomBlock cb = CustomBlock.byAlreadyPlaced(sLoc.clone().add(0,i,0).getBlock());
                                                    if (cb != null){
                                                        if(cb.getNamespacedID().equalsIgnoreCase(ConfigManager.Config.glass)){
                                                            break Label_out;
                                                        }
                                                    }
                                                }
                                            }
                                            boolean ws = true;
                                            for(String season : crop.getSeasons()){
                                                if (Objects.equals(season, ConfigManager.Config.current)) {
                                                    ws = false;
                                                    break;
                                                }
                                            }
                                            if(ws){
                                                CROPS.remove(sLoc);
                                                data.set(worldName+"."+coordinate[0]+","+coordinate[1]+","+coordinate[2], null);
                                                bukkitScheduler.callSyncMethod(CustomCrops.instance, () -> {
                                                    CustomBlock.remove(sLoc);
                                                    CustomBlock.place(ConfigManager.Config.dead, sLoc);
                                                    return null;
                                                });
                                                return;
                                            }
                                        }
                                        //下一阶段判断
                                        int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                                        if (CustomBlock.getInstance(split[0] +":"+cropNameList[0] + "_stage_" + nextStage) != null) {
                                            bukkitScheduler.callSyncMethod(CustomCrops.instance, () ->{
                                                CustomBlock.remove(potLoc);
                                                CustomBlock.place(ConfigManager.Config.pot, potLoc);
                                                if(Math.random()< crop.getChance()){
                                                    CustomBlock.remove(sLoc);
                                                    CustomBlock.place(split[0] + ":" + cropNameList[0] + "_stage_" + nextStage, sLoc);
                                                }
                                                return null;
                                            });
                                        }
                                        //巨大化判断
                                        else if(crop.getWillGiant()){
                                            bukkitScheduler.callSyncMethod(CustomCrops.instance, () ->{
                                                CustomBlock.remove(potLoc);
                                                CustomBlock.place(ConfigManager.Config.pot, potLoc);
                                                if(crop.getGiantChance() > Math.random()){
                                                    CustomBlock.remove(sLoc);
                                                    CustomBlock.place(crop.getGiant(), sLoc);
                                                }
                                                return null;
                                            });
                                        }
                                    }
                                    /*
                                    是干燥的种植盆吗
                                    */
                                    else if(potName.equalsIgnoreCase(ConfigManager.Config.pot)){
                                        if(ConfigManager.Config.season) {
                                            if(ConfigManager.Config.greenhouse){
                                                for(int i = 1; i <= ConfigManager.Config.range; i++){
                                                    CustomBlock cb = CustomBlock.byAlreadyPlaced(sLoc.clone().add(0,i,0).getBlock());
                                                    if (cb != null){
                                                        if(cb.getNamespacedID().equalsIgnoreCase(ConfigManager.Config.glass)){
                                                            return;
                                                        }
                                                    }
                                                }
                                            }
                                            boolean ws = true;
                                            Crop crop = ConfigManager.CONFIG.get(StringUtils.split(StringUtils.split(namespacedID,":")[1],"_")[0]);
                                            for (String season : crop.getSeasons()) {
                                                if (Objects.equals(season, ConfigManager.Config.current)) {
                                                    ws = false;
                                                    break;
                                                }
                                            }
                                            if (ws) {
                                                CROPS.remove(sLoc);
                                                data.set(worldName+"."+coordinate[0]+","+coordinate[1]+","+coordinate[2], null);
                                                bukkitScheduler.callSyncMethod(CustomCrops.instance, () -> {
                                                    CustomBlock.remove(sLoc);
                                                    CustomBlock.place(ConfigManager.Config.dead, sLoc);
                                                    return null;
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                CROPS.remove(sLoc);
                                data.set(worldName+"."+coordinate[0]+","+coordinate[1]+","+coordinate[2], null);
                            }
                        }
                    }
                });
            }
        });
        long finish2 = System.currentTimeMillis();
        MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops] &7农作物生长耗时&a" + (finish2 - start2) + "&fms",Bukkit.getConsoleSender());

        /*
        阶段3：保存文件
        */
        long start3 = System.currentTimeMillis();
        try{
            data.save(file);
        }catch (IOException e){
            e.printStackTrace();
            CustomCrops.instance.getLogger().warning("农作物缓存清理保存出错!");
        }
        long finish3 = System.currentTimeMillis();
        MessageManager.consoleMessage("&#ccfbff-#ef96c5&[CustomCrops] &7农作物数据保存耗时&a" + (finish3 - start3) + "&fms",Bukkit.getConsoleSender());
    }
}

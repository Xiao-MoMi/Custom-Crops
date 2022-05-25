package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.DataManager.CropManager;
import net.momirealms.customcrops.DataManager.MaxCropsPerChunk;
import net.momirealms.customcrops.Integrations.IntegrationCheck;
import net.momirealms.customcrops.MessageManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;

public class RightClickCustomBlock implements Listener {

    @EventHandler
    public void rightClickCustomCrop(CustomBlockInteractEvent event){
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        //获取配置文件
        FileConfiguration config = CustomCrops.instance.getConfig();
        Player player = event.getPlayer();
        Block clickedBlock = event.getBlockClicked();
        Location clickedBlockLocation = clickedBlock.getLocation();
        /*
        手里无物品，则进行收获判断
        手里有物品，则进行浇水，骨粉，种植判断
         */
        //手无物品部分
        if (event.getItem() == null) {
            CustomBlock clickedCustomBlock = CustomBlock.byAlreadyPlaced(clickedBlock);
            if(clickedCustomBlock == null) return;
            //兼容性检测
            if(!IntegrationCheck.HarvestCheck(clickedBlockLocation, player)){
                //获取点击方块的命名空间与ID
                String namespacedID = clickedCustomBlock.getNamespacedID();
                //如果ID内有stage则进行下一步
                if (namespacedID.contains("stage")){
                    //是否为枯萎植物
                    if(namespacedID.equalsIgnoreCase(config.getString("config.dead-crop"))) return;
                    //String namespace = clickedCustomBlock.getNamespacedID().split(":")[0];
                    String[] split = StringUtils.split(namespacedID,":");
                    //String[] cropNameList = clickedCustomBlock.getNamespacedID().split(":")[1].split("_");
                    String[] cropNameList = StringUtils.split(split[1],"_");

                    int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                    //农作物是否存在下一阶段
                    if (CustomBlock.getInstance(split[0] + ":" + cropNameList[0] + "_stage_" + nextStage) == null) {
                        //如果不存在下一阶段说明已是最终阶段，可以收获
                        //遍历掉落物并删除方块
                        clickedCustomBlock.getLoot().forEach(itemStack -> {
                            clickedBlockLocation.getWorld().dropItem(clickedBlockLocation.clone().add(0.5,0.2,0.5),itemStack);
                        });
                        CustomBlock.remove(clickedBlockLocation);
                        //如果配置文件中有return项目则放置方块
                        if(config.getConfigurationSection("crops." + cropNameList[0]).getKeys(false).contains("return")){
                            CustomBlock.place(config.getString("crops." + cropNameList[0] + ".return"), clickedBlockLocation);
                        }
                    }
                }
            }
        }else{
            //非空手状态
            //右键的不是自定义方块返回
            CustomBlock clickedCustomBlock = CustomBlock.byAlreadyPlaced(clickedBlock);
            if(clickedCustomBlock == null) return;

            //获取右键物品的namespaceID
            String namespacedId = clickedCustomBlock.getNamespacedID();

            /*
            右键的是特殊作物吗
             */
            if (namespacedId.contains("stage")) {
                    //下方方块不是自定义方块则返回
                    World world = player.getWorld();
                    Block blockUnder = world.getBlockAt(clickedBlockLocation.clone().subtract(0,1,0));
                    if (CustomBlock.byAlreadyPlaced(blockUnder) == null) return;
                    //检测右键的方块下方是否为干燥的种植盆方块
                    if (CustomBlock.byAlreadyPlaced(blockUnder).getNamespacedID().equalsIgnoreCase(config.getString("config.pot"))) {
                        //获取手中的物品
                        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
                        Location locUnder = clickedBlockLocation.clone().subtract(0,1,0);
                        //如果手中的是水桶，那么转干为湿
                        if (mainHandItem.getType() == Material.WATER_BUCKET) {
                            //扣除水桶
                            if (player.getGameMode() != GameMode.CREATIVE) {
                                mainHandItem.setAmount(mainHandItem.getAmount() - 1);
                                player.getInventory().addItem(new ItemStack(Material.BUCKET));
                            }
                            CustomBlock.remove(locUnder);
                            CustomBlock.place(config.getString("config.watered-pot"), locUnder);
                        } else if (mainHandItem.getType() == Material.WOODEN_SWORD) {
                            waterPot(mainHandItem, player, locUnder, config);
                        }
                    }
                    //检测右键的方块下方是否为湿润的种植盆方块
                    else if(CustomBlock.byAlreadyPlaced(blockUnder).getNamespacedID().equalsIgnoreCase(config.getString("config.watered-pot"))){
                        //获取手中的物品
                        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
                        //如果是骨粉
                        if (mainHandItem.getType() == Material.BONE_MEAL){
                            //植物是否具有stage属性
                            if (clickedCustomBlock.getNamespacedID().contains("stage")){
                                //获取点击方块的命名空间与ID
                                String namespacedID = clickedCustomBlock.getNamespacedID();
                                //String namespace = clickedCustomBlock.getNamespacedID().split(":")[0];
                                String[] split = StringUtils.split(namespacedID,":");
                                //String[] cropNameList = clickedCustomBlock.getNamespacedID().split(":")[1].split("_");
                                String[] cropNameList = StringUtils.split(split[1],"_");
                                //下一生长阶段
                                int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                                //植物是否存在下一个stage
                                if (CustomBlock.getInstance(split[0]+ ":" + cropNameList[0] + "_stage_" + nextStage) != null){
                                    if(player.getGameMode() != GameMode.CREATIVE){
                                        mainHandItem.setAmount(mainHandItem.getAmount() - 1);
                                    }
                                    //骨粉的成功率
                                    if (Math.random() < config.getDouble("config.bone-meal-chance")){
                                        CustomBlock.remove(clickedBlockLocation);
                                        CustomBlock.place(split[0] + ":" + cropNameList[0] + "_stage_" + nextStage,clickedBlockLocation);
                                        Particle particleSuccess = Particle.valueOf(config.getString("config.particle.success"));
                                        world.spawnParticle(particleSuccess, clickedBlockLocation.clone().add(0.5, 0.1,0.5), 1 ,0,0,0,0);
                                        //使用骨粉是否消耗水分
                                        if(config.getBoolean("config.bone-meal-consume-water")){
                                            CustomBlock.remove(clickedBlockLocation.clone().subtract(0,1,0));
                                            CustomBlock.place(config.getString("config.pot"), clickedBlockLocation.clone().subtract(0,1,0));
                                        }
                                    }else {
                                        Particle particleFailure = Particle.valueOf(config.getString("config.particle.failure"));
                                        world.spawnParticle(particleFailure, clickedBlockLocation.clone().add(0.5, 0.1,0.5), 1 ,0,0,0,0);
                                    }
                                }
                            }
                        }
                    }
            }

            /*
            右键的是种植盆吗
             */
            else if (event.getBlockFace() == BlockFace.UP){
                    //获取手中的物品
                    ItemStack mainHandItem = player.getInventory().getItemInMainHand();
                    World world = player.getWorld();

                    //检测右键的方块是否为干燥的种植盆方块
                    if (namespacedId.equalsIgnoreCase(config.getString("config.pot"))){
                        //如果手中的是水桶，那么转干为湿

                        if (mainHandItem.getType() == Material.WATER_BUCKET){
                            //扣除水桶
                            if(player.getGameMode() != GameMode.CREATIVE){
                                mainHandItem.setAmount(mainHandItem.getAmount() - 1);
                                player.getInventory().addItem(new ItemStack(Material.BUCKET));
                            }
                            CustomBlock.remove(clickedBlockLocation);
                            CustomBlock.place(config.getString("config.watered-pot"),clickedBlockLocation);
                        } else if (mainHandItem.getType() == Material.WOODEN_SWORD){
                            waterPot(mainHandItem, player,clickedBlockLocation, config);
                        } else {
                            tryPlantSeed(clickedBlockLocation, mainHandItem, player, config);
                        }
                    }
                    //检测右键的方块是否为湿润的种植盆方块
                    else if(namespacedId.equalsIgnoreCase(config.getString("config.watered-pot"))){
                        tryPlantSeed(clickedBlockLocation, mainHandItem, player, config);
                    }
           }
        }
    }
    //尝试种植植物
    private void tryPlantSeed(Location clickedBlockLocation, ItemStack mainHandItem, Player player, FileConfiguration config) {
        //是否为IA物品
        if(CustomStack.byItemStack(mainHandItem) == null) return;
        //获取命名空间名与ID
        String namespaced_id = CustomStack.byItemStack(mainHandItem).getNamespacedID();
        //是否为种子
        if (namespaced_id.endsWith("_seeds")){
            //获取农作物名
            String cropName = StringUtils.split(namespaced_id.replace("_seeds",""),":")[1];
            //String[] crop = CustomStack.byItemStack(mainHandItem).getNamespacedID().toLowerCase().replace("_seeds","").split(":");
            //该种子是否存在于配置文件中
            if(!config.getConfigurationSection("crops").getKeys(false).contains(cropName)){
                MessageManager.playerMessage(config.getString("messages.prefix")+config.getString("messages.no-such-seed"),player);
                return;
            }
            //检测上方为空气
            if(clickedBlockLocation.getWorld().getBlockAt(clickedBlockLocation.clone().add(0, 1, 0)).getType() != Material.AIR){
                return;
            }
            //是否超高超低
            if (clickedBlockLocation.getY() < config.getInt("config.height.min") || clickedBlockLocation.getY() > config.getInt("config.height.max")){
                MessageManager.playerMessage(config.getString("messages.prefix") + config.getString("messages.not-a-good-place"),player);
                return;
            }
            Location locUp = clickedBlockLocation.clone().add(0,1,0);
            //是否启用了季节
            Label_out:
            if(config.getBoolean("enable-season")){
                if(config.getBoolean("config.enable-greenhouse")){
                    World world = player.getWorld();
                    for(int i = 1; i <= config.getInt("config.greenhouse-range"); i++){
                        Location tempLocation = locUp.clone().add(0,i,0);
                        if (CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLocation)) != null){
                            if(CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLocation)).getNamespacedID().equalsIgnoreCase(config.getString("config.greenhouse-glass"))){
                                break Label_out;
                            }
                        }
                    }
                }
                //获取种子适宜生长的季节
                String[] seasons = config.getString("crops."+cropName+".season").split(",");
                boolean wrongSeason = true;
                for(String season : seasons){
                    if(Objects.equals(season, config.getString("current-season"))){
                        wrongSeason = false;
                    }
                }
                if(wrongSeason){
                    MessageManager.playerMessage(config.getString("messages.prefix")+config.getString("messages.wrong-season"),player);
                    return;
                }
            }
            //是否到达区块上限
            if(MaxCropsPerChunk.maxCropsPerChunk(clickedBlockLocation)){
                MessageManager.playerMessage(config.getString("messages.prefix")+config.getString("messages.reach-limit-crop").replace("{Max}", config.getString("config.max-crops")),player);
                return;
            }
            //添加到缓存中
            if(config.getBoolean("enable-season")){
                CropManager.putInstance(locUp, config.getString("crops."+cropName+".season"));
            }else{
                CropManager.putInstance(locUp, "all");
            }
            //减少种子数量
            if(player.getGameMode() != GameMode.CREATIVE){
                mainHandItem.setAmount(mainHandItem.getAmount() -1);
            }
            //放置自定义农作物
            CustomBlock.place(namespaced_id.replace("_seeds","_stage_1"),locUp);
        }
    }
    private void waterPot(ItemStack itemStack, Player player, Location location, FileConfiguration config){
        //是否为IA物品
        if(CustomStack.byItemStack(itemStack) == null) return;

        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();

        bukkitScheduler.runTaskAsynchronously(CustomCrops.instance,()-> {
            //获取IA物品
            CustomStack customStack = CustomStack.byItemStack(itemStack);
            String namespacedId = customStack.getNamespacedID();
            World world = player.getWorld();

            int x;
            int z;

            if (namespacedId.equalsIgnoreCase(config.getString("config.watering-can-1"))) {
                x = 0;
                z = 0;
            } else if (namespacedId.equalsIgnoreCase(config.getString("config.watering-can-2"))) {
                x = 2;
                z = 2;
            } else if (namespacedId.equalsIgnoreCase(config.getString("config.watering-can-3"))) {
                x = 4;
                z = 4;
            } else return;
            //虽Who cares一点水呢？
            if(customStack.getDurability() > 0){
                CustomStack.byItemStack(itemStack).setDurability(CustomStack.byItemStack(itemStack).getDurability() - 1);
            }else return;
            //播放洒水音效

            world.playSound(player.getLocation(),Sound.BLOCK_WATER_AMBIENT,1,1);
            //获取玩家朝向
            float yaw = player.getLocation().getYaw();

            //提前获取ID与命名空间，以免for循环循环get
            String wateredPot = config.getString("config.watered-pot");
            String pot = config.getString("config.pot");

            //根据朝向确定浇水方向
            if (yaw <= 45 && yaw >= -135) {
                if (yaw > -45) {
                    x = 0;
                } else {
                    z = 0;
                }
                for (int i = 0; i <= x; i++) {
                    for (int j = 0; j <= z; j++) {
                        Location tempLoc = location.clone().add(i, 0, j);
                        if(CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLoc)) != null){
                            if(CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLoc)).getNamespacedID().equalsIgnoreCase(pot)){
                                //同步替换方块
                                bukkitScheduler.callSyncMethod(CustomCrops.instance,()->{
                                    CustomBlock.remove(tempLoc);
                                    CustomBlock.place(wateredPot,tempLoc);
                                    return null;
                                });
                            }
                        }
                    }
                }
            } else {
                if (yaw < 135 && yaw > 45) {
                    z= 0;
                } else {
                    x= 0;
                }
                for (int i = 0; i <= x; i++) {
                    for (int j = 0; j <= z; j++) {
                        Location tempLoc = location.clone().subtract(i, 0, j);
                        if(CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLoc)) != null){
                            if(CustomBlock.byAlreadyPlaced(world.getBlockAt(tempLoc)).getNamespacedID().equalsIgnoreCase(pot)){
                                //同步替换方块
                                bukkitScheduler.callSyncMethod(CustomCrops.instance,()->{
                                    CustomBlock.remove(tempLoc);
                                    CustomBlock.place(wateredPot,tempLoc);
                                    return null;
                                });
                            }
                        }
                    }
                }
            }
        });
    }
}

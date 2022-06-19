package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import net.momirealms.customcrops.datamanager.ConfigManager;
import net.momirealms.customcrops.limits.MaxCropsPerChunk;
import net.momirealms.customcrops.integrations.IntegrationCheck;
import net.momirealms.customcrops.datamanager.MessageManager;
import net.momirealms.customcrops.utils.Crop;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

import static net.momirealms.customcrops.datamanager.CropManager.CROPS;

public class RightClickCustomBlock implements Listener {

    private HashMap<Player, Long> coolDown;
    {
        coolDown = new HashMap<Player, Long>();
    }

    @EventHandler
    public void rightClickCustomBlock(CustomBlockInteractEvent event){
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getBlockClicked();
        Location clickedBlockLocation = clickedBlock.getLocation();
        /*
        手里无物品，则进行收获判断
        手里有物品，则进行浇水，骨粉，种植判断
         */
        //空手部分
        if (event.getItem() == null) {
            CustomBlock clickedCustomBlock = CustomBlock.byAlreadyPlaced(clickedBlock);
            Player player = event.getPlayer();
            //兼容性检测
            if(IntegrationCheck.HarvestCheck(clickedBlockLocation, player)){
                //获取点击方块的命名空间与ID
                String namespacedID = event.getNamespacedID();
                //如果ID内有stage则进行下一步
                if (namespacedID.contains("_stage_")){
                    //是否为枯萎植物
                    if(namespacedID.equalsIgnoreCase(ConfigManager.Config.dead)) return;
                    String[] split = StringUtils.split(namespacedID,":");
                    String[] cropNameList = StringUtils.split(split[1],"_");
                    int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                    //农作物是否存在下一阶段
                    if (CustomBlock.getInstance(split[0] + ":" + cropNameList[0] + "_stage_" + nextStage) == null) {
                        //如果不存在下一阶段说明已是最终阶段，可以收获
                        //遍历掉落物并删除方块
                        clickedCustomBlock.getLoot().forEach(itemStack -> clickedBlockLocation.getWorld().dropItem(clickedBlockLocation.clone().add(0.5,0.2,0.5),itemStack));
                        CustomBlock.remove(clickedBlockLocation);
                        //如果配置文件中有return项目则放置方块
                        Crop crop = ConfigManager.CONFIG.get(cropNameList[0]);
                        if(crop.getWillReturn()){
                            CustomBlock.place(crop.getReturnStage(), clickedBlockLocation);
                        }
                    }
                }
            }
        }else{
            //获取右键物品的namespaceID
            String namespacedId = event.getNamespacedID();
            /*
            右键的是特殊作物吗
             */
            if (namespacedId.contains("stage")) {
                //下方方块不是自定义方块则返回
                Player player = event.getPlayer();
                Block blockUnder = clickedBlockLocation.clone().subtract(0,1,0).getBlock();
                CustomBlock cb = CustomBlock.byAlreadyPlaced(blockUnder);
                if (cb == null) return;
                //检测右键的方块下方是否为干燥的种植盆方块
                if (cb.getNamespacedID().equalsIgnoreCase(ConfigManager.Config.pot)) {
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
                        CustomBlock.place(ConfigManager.Config.watered_pot, locUnder);
                    } else if (mainHandItem.getType() == Material.WOODEN_SWORD) {
                        waterPot(mainHandItem, player, locUnder);
                    }
                }
                //检测右键的方块下方是否为湿润的种植盆方块
                else if(cb.getNamespacedID().equalsIgnoreCase(ConfigManager.Config.watered_pot)){
                    //获取手中的物品
                    ItemStack mainHandItem = player.getInventory().getItemInMainHand();
                    //如果是骨粉
                    if (mainHandItem.getType() == Material.BONE_MEAL){
                        //植物是否具有stage属性
                        if (namespacedId.contains("_stage_")){
                            String[] split = StringUtils.split(namespacedId,":");
                            String[] cropNameList = StringUtils.split(split[1],"_");
                            //下一生长阶段
                            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                            //植物是否存在下一个stage
                            if (CustomBlock.getInstance(split[0]+ ":" + cropNameList[0] + "_stage_" + nextStage) != null){
                                if(player.getGameMode() != GameMode.CREATIVE){
                                    mainHandItem.setAmount(mainHandItem.getAmount() - 1);
                                }
                                World world = player.getWorld();
                                //骨粉的成功率
                                if (Math.random() < ConfigManager.Config.bone_chance){
                                    CustomBlock.remove(clickedBlockLocation);
                                    CustomBlock.place(split[0] + ":" + cropNameList[0] + "_stage_" + nextStage,clickedBlockLocation);
                                    Particle particleSuccess = Particle.valueOf(ConfigManager.Config.success);
                                    world.spawnParticle(particleSuccess, clickedBlockLocation.clone().add(0.5, 0.1,0.5), 1 ,0,0,0,0);
                                    //使用骨粉是否消耗水分
                                    if(ConfigManager.Config.need_water){
                                        CustomBlock.remove(clickedBlockLocation.clone().subtract(0,1,0));
                                        CustomBlock.place(ConfigManager.Config.pot, clickedBlockLocation.clone().subtract(0,1,0));
                                    }
                                }else {
                                    Particle particleFailure = Particle.valueOf(ConfigManager.Config.failure);
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
                ItemStack item = event.getItem();
                Player player = event.getPlayer();
                //检测右键的方块是否为干燥的种植盆方块
                if (namespacedId.equalsIgnoreCase(ConfigManager.Config.pot)){
                    //如果手中的是水桶，那么转干为湿
                    if (item.getType() == Material.WATER_BUCKET){
                        //扣除水桶
                        if(player.getGameMode() != GameMode.CREATIVE){
                            item.setAmount(item.getAmount() - 1);
                            player.getInventory().addItem(new ItemStack(Material.BUCKET));
                        }
                        CustomBlock.remove(clickedBlockLocation);
                        CustomBlock.place(ConfigManager.Config.watered_pot,clickedBlockLocation);
                        return;
                    }
                    CustomStack customStack = CustomStack.byItemStack(item);
                    if (customStack != null){
                        String namespacedID = customStack.getNamespacedID();
                        if (namespacedID.endsWith("_seeds")){
                            if(tryPlantSeed(clickedBlockLocation, namespacedID, player)){
                                if(player.getGameMode() != GameMode.CREATIVE){
                                    item.setAmount(item.getAmount() -1);
                                }
                            }
                        }else {
                            waterPot(item, player, clickedBlockLocation);
                        }
                    }
                }
                //检测右键的方块是否为湿润的种植盆方块
                else if(namespacedId.equalsIgnoreCase(ConfigManager.Config.watered_pot)){
                    CustomStack customStack = CustomStack.byItemStack(item);
                    if (customStack != null){
                        String namespacedID = customStack.getNamespacedID();
                        if (namespacedID.endsWith("_seeds")){
                            if(tryPlantSeed(clickedBlockLocation, namespacedID, player)){
                                if(player.getGameMode() != GameMode.CREATIVE){
                                    item.setAmount(item.getAmount() -1);
                                }
                            }
                        }
                    }
                }
           }
        }
    }
    //尝试种植植物
    private boolean tryPlantSeed(Location clickedBlockLocation, String namespacedID, Player player) {
        //获取农作物名
        String key = StringUtils.split(namespacedID.replace("_seeds",""),":")[1];
        //检测上方为空气
        if(clickedBlockLocation.clone().add(0, 1, 0).getBlock().getType() != Material.AIR){
            return false;
        }
        //该种子是否存在于配置文件中
        if(!ConfigManager.CONFIG.containsKey(key)){
            MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.no_such_seed,player);
            return false;
        }
        //是否超高超低
        if (clickedBlockLocation.getY() < ConfigManager.Config.minh || clickedBlockLocation.getY() > ConfigManager.Config.maxh){
            MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.bad_place,player);
            return false;
        }
        Location locUp = clickedBlockLocation.clone().add(0,1,0);
        //是否启用了季节
        Label_out:
        if(ConfigManager.Config.season){
            if(ConfigManager.Config.greenhouse){
                int range = ConfigManager.Config.range;
                for(int i = 1; i <= range; i++){
                    CustomBlock cb = CustomBlock.byAlreadyPlaced(locUp.clone().add(0,i,0).getBlock());
                    if (cb != null){
                        if(cb.getNamespacedID().equalsIgnoreCase(ConfigManager.Config.glass)){
                            break Label_out;
                        }
                    }
                }
            }
            //获取种子适宜生长的季节
            boolean ws = true;
            Crop crop = ConfigManager.CONFIG.get(key);
            for(String season : crop.getSeasons()){
                if (Objects.equals(season, ConfigManager.Config.current)) {
                    ws = false;
                    break;
                }
            }
            if(ws){
                MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.wrong_season,player);
                return false;
            }
        }
        //是否到达区块上限
        if(MaxCropsPerChunk.maxCropsPerChunk(clickedBlockLocation)){
            MessageManager.playerMessage(ConfigManager.Config.prefix + ConfigManager.Config.limit_crop.replace("{Max}", String.valueOf(ConfigManager.Config.max_crop)),player);
            return false;
        }
        //添加到缓存中
        CROPS.put(locUp, key);
        //放置自定义农作物
        CustomBlock.place(namespacedID.replace("_seeds","_stage_1"),locUp);
        return true;
    }
    private void waterPot(ItemStack itemStack, Player player, Location location){
        //是否为IA物品
        if(CustomStack.byItemStack(itemStack) == null) return;
        //获取IA物品
        CustomStack customStack = CustomStack.byItemStack(itemStack);
        String namespacedId = customStack.getNamespacedID();
        World world = player.getWorld();
        int x;
        int z;
        if (namespacedId.equalsIgnoreCase(ConfigManager.Config.watering_can_1)) {
            x = 0;
            z = 0;
        } else if (namespacedId.equalsIgnoreCase(ConfigManager.Config.watering_can_2)){
            x = 2;
            z = 2;
        } else if (namespacedId.equalsIgnoreCase(ConfigManager.Config.watering_can_3)){
            x = 4;
            z = 4;
        } else return;
        //判断耐久度
        if(customStack.getDurability() > 0){
            CustomStack.byItemStack(itemStack).setDurability(CustomStack.byItemStack(itemStack).getDurability() - 1);
        }else return;
        //播放洒水音效
        world.playSound(player.getLocation(),Sound.BLOCK_WATER_AMBIENT,1,1);
        //获取玩家朝向
        float yaw = player.getLocation().getYaw();
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
                    CustomBlock cb = CustomBlock.byAlreadyPlaced(tempLoc.getBlock());
                    if(cb != null){
                        if(cb.getNamespacedID().equalsIgnoreCase(ConfigManager.Config.pot)){
                            CustomBlock.remove(tempLoc);
                            CustomBlock.place(ConfigManager.Config.watered_pot,tempLoc);
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
                    CustomBlock cb = CustomBlock.byAlreadyPlaced(tempLoc.getBlock());
                    if(cb != null){
                        if(cb.getNamespacedID().equalsIgnoreCase(ConfigManager.Config.pot)){
                            CustomBlock.remove(tempLoc);
                            CustomBlock.place(ConfigManager.Config.watered_pot,tempLoc);
                        }
                    }
                }
            }
        }
    }
}

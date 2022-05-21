package net.momirealms.customcrops.listener;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockInteractEvent;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.DataManager.CropManager;
import net.momirealms.customcrops.DataManager.MaxCropsPerChunk;
import net.momirealms.customcrops.MessageManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class RightClickCustomBlock implements Listener {

    FileConfiguration config = CustomCrops.instance.getConfig();

    @EventHandler
    public void rightClickCustomCrop(CustomBlockInteractEvent event){
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block clickedBlock = event.getBlockClicked();
        Location clickedBlockLocation = clickedBlock.getLocation();
        CustomBlock clickedCustomBlock = CustomBlock.byAlreadyPlaced(clickedBlock);
        if (event.getItem() == null) {
            if (clickedCustomBlock.getNamespacedID().contains("stage")){
                if(clickedCustomBlock.getNamespacedID().equalsIgnoreCase(config.getString("config.dead-crop"))) return;
                String namespace = clickedCustomBlock.getNamespacedID().split(":")[0];
                String[] cropNameList = clickedCustomBlock.getNamespacedID().split(":")[1].split("_");
                int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                if (CustomBlock.getInstance(namespace + ":" + cropNameList[0] + "_" +cropNameList[1] +"_" + nextStage) == null) {
                    clickedCustomBlock.getLoot().forEach(itemStack -> {
                        clickedBlockLocation.getWorld().dropItem(clickedBlockLocation.clone().add(0.5,0.2,0.5),itemStack);
                    });
                    CustomBlock.remove(clickedBlockLocation);
                    if(config.getConfigurationSection("crops." + cropNameList[0]).getKeys(false).contains("return")){
                        CustomBlock.place(config.getString("crops." + cropNameList[0] + ".return"), clickedBlockLocation);
                    }
                }
            }
            return;
        }
        Player player = event.getPlayer();
        World world = player.getWorld();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();


        if (CustomBlock.byAlreadyPlaced(world.getBlockAt(clickedBlockLocation.clone().subtract(0,1,0))) != null && clickedBlock.getType() == Material.TRIPWIRE) {
            if(CustomBlock.byAlreadyPlaced(world.getBlockAt(clickedBlockLocation.clone().subtract(0,1,0))).getNamespacedID().equalsIgnoreCase(config.getString("config.pot"))) {
                if (mainHandItem.getType() == Material.WATER_BUCKET) {
                    if(player.getGameMode() != GameMode.CREATIVE){
                        mainHandItem.setAmount(mainHandItem.getAmount() - 1);
                        player.getInventory().addItem(new ItemStack(Material.BUCKET));
                    }
                    CustomBlock.remove(clickedBlockLocation.clone().subtract(0, 1, 0));
                    CustomBlock.place(config.getString("config.watered-pot"), clickedBlockLocation.clone().subtract(0, 1, 0));
                }else if(mainHandItem.getType() == Material.WOODEN_SWORD){
                    waterPot(mainHandItem,player,clickedBlockLocation.clone().subtract(0,1,0));
                }
            }
            else if(CustomBlock.byAlreadyPlaced(world.getBlockAt(clickedBlockLocation.clone().subtract(0,1,0))).getNamespacedID().equalsIgnoreCase(config.getString("config.watered-pot"))){
                if (mainHandItem.getType() == Material.BONE_MEAL){
                    if (clickedCustomBlock.getNamespacedID().contains("stage")){
                        String[] cropNameList = clickedCustomBlock.getNamespacedID().split("_");
                        int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                        if (CustomBlock.getInstance(cropNameList[0] + "_" +cropNameList[1] +"_" + nextStage) != null){
                            if(player.getGameMode() != GameMode.CREATIVE){
                                mainHandItem.setAmount(mainHandItem.getAmount() - 1);
                            }
                            if (Math.random() < config.getDouble("config.bone-meal-chance")){
                                CustomBlock.remove(clickedBlockLocation);
                                CustomBlock.place(cropNameList[0] + "_" +cropNameList[1] +"_" + nextStage,clickedBlockLocation);
                                Particle particleSuccess = Particle.valueOf(config.getString("config.particle.success"));
                                world.spawnParticle(particleSuccess, clickedBlockLocation.clone().add(0.5, 0.1,0.5), 1 ,0,0,0,0);
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
        } else if (CustomBlock.byAlreadyPlaced(world.getBlockAt(clickedBlockLocation)) != null && event.getBlockFace() == BlockFace.UP){
            if (CustomBlock.byAlreadyPlaced(world.getBlockAt(clickedBlockLocation)).getNamespacedID().equalsIgnoreCase(config.getString("config.pot"))){
                if (mainHandItem.getType() == Material.WATER_BUCKET){
                    if(player.getGameMode() != GameMode.CREATIVE){
                        mainHandItem.setAmount(mainHandItem.getAmount() - 1);
                        player.getInventory().addItem(new ItemStack(Material.BUCKET));
                    }
                    CustomBlock.remove(clickedBlockLocation);
                    CustomBlock.place(config.getString("config.watered-pot"),clickedBlockLocation);
                } else if (mainHandItem.getType() == Material.WOODEN_SWORD){
                    waterPot(mainHandItem, player,clickedBlockLocation);
                } else {
                    tryPlantSeed(clickedBlockLocation, mainHandItem, player);
                }
            }else if(CustomBlock.byAlreadyPlaced(world.getBlockAt(clickedBlockLocation)).getNamespacedID().equalsIgnoreCase(config.getString("config.watered-pot"))){

                tryPlantSeed(clickedBlockLocation, mainHandItem, player);
            }
        }
    }
    private void tryPlantSeed(Location clickedBlockLocation, ItemStack mainHandItem, Player player) {
        if(CustomStack.byItemStack(mainHandItem) == null) return;
        if (CustomStack.byItemStack(mainHandItem).getNamespacedID().toLowerCase().endsWith("_seeds")){
            String namespaced_id = CustomStack.byItemStack(mainHandItem).getNamespacedID().toLowerCase();
            String[] crop = CustomStack.byItemStack(mainHandItem).getNamespacedID().toLowerCase().replace("_seeds","").split(":");
            if (clickedBlockLocation.getY() < config.getInt("config.height.min") || clickedBlockLocation.getY() > config.getInt("config.height.max")){
                MessageManager.playerMessage(config.getString("messages.prefix") + config.getString("messages.not-a-good-place"),player);
                return;
            }
            Label_out:
            if(config.getBoolean("enable-season")){
                if(config.getBoolean("config.enable-greenhouse")){
                    for(int i = 1; i <= config.getInt("config.greenhouse-range"); i++){
                        Location tempLocation = clickedBlockLocation.clone().add(0,i+1,0);
                        if (CustomBlock.byAlreadyPlaced(clickedBlockLocation.getWorld().getBlockAt(tempLocation)) != null){
                            if(CustomBlock.byAlreadyPlaced(clickedBlockLocation.getWorld().getBlockAt(tempLocation)).getNamespacedID().equalsIgnoreCase(config.getString("config.greenhouse-glass"))){
                                break Label_out;
                            }
                        }
                    }
                }
                String[] seasons = config.getString("crops."+crop[1]+".season").split(",");
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
            if(!config.contains("crops."+crop[1])){
                MessageManager.playerMessage(config.getString("messages.prefix")+config.getString("messages.no-such-seed"),player);
                return;
            }
            if(MaxCropsPerChunk.maxCropsPerChunk(clickedBlockLocation)){
                MessageManager.playerMessage(config.getString("messages.prefix")+config.getString("messages.reach-limit-crop").replace("{Max}", config.getString("config.max-crops")),player);
                return;
            }
            if(config.getBoolean("enable-season")){
                CropManager.putInstance(clickedBlockLocation.clone().add(0,1,0), config.getString("crops."+crop[1]+".season"));
            }else{
                CropManager.putInstance(clickedBlockLocation.clone().add(0,1,0), "all");
            }
            if(player.getGameMode() != GameMode.CREATIVE){
                mainHandItem.setAmount(mainHandItem.getAmount() -1);
            }
            CustomBlock.place(namespaced_id.replace("_seeds","_stage_1"),clickedBlockLocation.clone().add(0,1,0));
        }
    }
    private void waterPot(ItemStack itemStack, Player player, Location location){

        if(CustomStack.byItemStack(itemStack) == null) return;

        CustomStack customStack = CustomStack.byItemStack(itemStack);

        if(customStack.getDurability() > 0){
            CustomStack.byItemStack(itemStack).setDurability(CustomStack.byItemStack(itemStack).getDurability() - 1);
        }else return;
        Bukkit.getScheduler().runTaskAsynchronously(CustomCrops.instance,()-> {

            int x;
            int z;

            if (customStack.getNamespacedID().equalsIgnoreCase(config.getString("config.watering-can-1"))) {
                x = 0;
                z = 0;
            } else if (customStack.getNamespacedID().equalsIgnoreCase(config.getString("config.watering-can-2"))) {
                x = 2;
                z = 2;
            } else if (customStack.getNamespacedID().equalsIgnoreCase(config.getString("config.watering-can-3"))) {
                x = 4;
                z = 4;
            } else return;
            player.playSound(player,Sound.BLOCK_WATER_AMBIENT,1,1);
            float yaw = player.getLocation().getYaw();
            if (yaw <= 45 && yaw >= -135) {
                if (yaw > -45) {
                    x = 0;
                } else {
                    z = 0;
                }
                for (int i = 0; i <= x; i++) {
                    for (int j = 0; j <= z; j++) {
                        Location tempLoc = location.clone().add(i, 0, j);
                        canWaterPot(tempLoc);
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
                        canWaterPot(tempLoc);
                    }
                }
            }
        });
    }
    private void canWaterPot(Location location){
        if(CustomBlock.byAlreadyPlaced(location.getWorld().getBlockAt(location)) != null){
            if(CustomBlock.byAlreadyPlaced(location.getWorld().getBlockAt(location)).getNamespacedID().equalsIgnoreCase(config.getString("config.pot"))){
                Bukkit.getScheduler().callSyncMethod(CustomCrops.instance,()->{
                    CustomBlock.remove(location);
                    CustomBlock.place(config.getString("config.watered-pot"),location);
                    return null;
                });
            }
        }
    }
}

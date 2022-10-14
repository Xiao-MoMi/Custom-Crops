package net.momirealms.customcrops.managers.listener;

import net.momirealms.customcrops.config.MainConfig;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;

public class VanillaCropHarvestListener implements Listener {

    @EventHandler
    public void onInteractRipeCrop(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        Material material = block.getType();
        if (material == Material.COCOA) return;
        if (block.getBlockData() instanceof Ageable ageable) {
            if (ageable.getMaximumAge() == ageable.getAge()) {
                final Player player = event.getPlayer();
                if (MainConfig.emptyHand) {
                    final PlayerInventory inventory = player.getInventory();
                    if (!(inventory.getItemInMainHand().getType() != Material.AIR || inventory.getItemInOffHand().getType() != Material.AIR)) {
                        if (player.breakBlock(block)) {
                            block.setType(material);
                        }
                    }
                }
                else if (player.breakBlock(block)) {
                    block.setType(material);
                }
            }
        }
    }
}

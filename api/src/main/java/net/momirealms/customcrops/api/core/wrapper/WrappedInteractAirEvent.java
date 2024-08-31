package net.momirealms.customcrops.api.core.wrapper;

import net.momirealms.customcrops.api.core.world.CustomCropsWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class WrappedInteractAirEvent {

    private final CustomCropsWorld<?> world;
    private final ItemStack itemInHand;
    private final String itemID;
    private final EquipmentSlot hand;
    private final Player player;

    public WrappedInteractAirEvent(CustomCropsWorld<?> world, Player player, EquipmentSlot hand, ItemStack itemInHand, String itemID) {
        this.world = world;
        this.itemInHand = itemInHand;
        this.itemID = itemID;
        this.hand = hand;
        this.player = player;
    }

    public CustomCropsWorld<?> world() {
        return world;
    }

    public ItemStack itemInHand() {
        return itemInHand;
    }

    public String itemID() {
        return itemID;
    }

    public EquipmentSlot hand() {
        return hand;
    }

    public Player player() {
        return player;
    }
}

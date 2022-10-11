package net.momirealms.customcrops.managers.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.integrations.customplugin.HandlerP;
import org.bukkit.entity.Player;

public class PlayerContainerListener extends PacketAdapter {

    private final HandlerP handlerP;

    public PlayerContainerListener(HandlerP handlerP) {
        super(CustomCrops.plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.WINDOW_CLICK);
        this.handlerP = handlerP;
    }

    public void onPacketReceiving(PacketEvent event) {
        final Player player = event.getPlayer();
        if (handlerP.coolDownJudge(player)) {
            player.updateInventory();
        }
    }
}
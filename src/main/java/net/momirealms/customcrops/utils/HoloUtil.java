package net.momirealms.customcrops.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.ConfigReader;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

public class HoloUtil {

    public static HashMap<Player, Entity> cache = new HashMap<>();

    public static void showHolo(String text, Player player, Location location, int duration){

        ArmorStand entity = location.getWorld().spawn(location, ArmorStand.class, a -> {
            a.setInvisible(true);
            a.setCollidable(false);
            a.setInvulnerable(true);
            a.setVisible(false);
            a.setCustomNameVisible(false);
            a.setSmall(true);
            a.setGravity(false);
        });
        cache.put(player, entity);

        Component component = MiniMessage.miniMessage().deserialize(text);

        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        wrappedDataWatcher.setEntity(entity);
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Boolean.class);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(component)).getHandle()));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer), true);
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        packetContainer.getIntegers().write(0, entity.getEntityId());
        packetContainer.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
        }
        catch (Exception e) {
            AdventureManager.consoleMessage("<red>[CustomCrops] 无法为玩家 "+ player.getName()+" 展示悬浮信息!</red>");
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskLater(CustomCrops.instance, ()->{
            entity.remove();
            cache.remove(player);
        }, duration * 20L);
    }
}

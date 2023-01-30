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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.objects.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class HologramUtil {

    public static HashMap<SimpleLocation, Integer> cache = new HashMap<>();

    public static void showHolo(String text, Player player, Location location, int duration){

        Integer entityID = cache.remove(MiscUtils.getSimpleLocation(location));
        if (entityID != null) {
            removeHolo(player, entityID);
        }
        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        int id = new Random().nextInt(1000000000);
        cache.put(MiscUtils.getSimpleLocation(location), id);
        spawnPacket.getModifier().write(0, id);
        spawnPacket.getModifier().write(1, UUID.randomUUID());
        spawnPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        spawnPacket.getDoubles().write(0, location.getX());
        spawnPacket.getDoubles().write(1, location.getY());
        spawnPacket.getDoubles().write(2, location.getZ());
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        Component component = MiniMessage.miniMessage().deserialize(text);
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer1 = WrappedDataWatcher.Registry.get(Boolean.class);
        WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(component)).getHandle()));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, serializer1), true);
        byte mask1 = 0x20;
        byte mask2 = 0x01;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), mask1);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, serializer2), mask2);
        metaPacket.getModifier().write(0,id);

        if (CustomCrops.plugin.getVersionHelper().isVersionNewerThan1_19_R2()) {
            List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
            wrappedDataWatcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
                final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
                wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
            });
            metaPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        } else {
            metaPacket.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
        }

        try {
            CustomCrops.protocolManager.sendServerPacket(player, spawnPacket);
            CustomCrops.protocolManager.sendServerPacket(player, metaPacket);
        }
        catch (Exception e) {
            AdventureUtil.consoleMessage("<red>[CustomCrops] Failed to display hologram for " + player.getName() + " !</red>");
            e.printStackTrace();
        }
        Bukkit.getScheduler().runTaskLater(CustomCrops.plugin, ()->{
            removeHolo(player, id);
            cache.remove(MiscUtils.getSimpleLocation(location));
        }, duration * 20L);
    }

    public static void removeHolo(Player player, int entityId){
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntLists().write(0, List.of(entityId));
        try {
            CustomCrops.protocolManager.sendServerPacket(player, packet);
        }
        catch (Exception e) {
            AdventureUtil.consoleMessage("<red>[CustomCrops] Failed to remove hologram for " + player.getName() + " !</red>");
            e.printStackTrace();
        }
    }
}

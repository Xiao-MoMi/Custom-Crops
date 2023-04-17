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

package net.momirealms.customcrops.api.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class FakeEntityUtils {

    public static void playWaterAnimation(Player player, Location location, String animation_id) {
        int id = new Random().nextInt(Integer.MAX_VALUE);
        CustomCrops.getProtocolManager().sendServerPacket(player,   getSpawnPacket(id, location, EntityType.ARMOR_STAND));
        CustomCrops.getProtocolManager().sendServerPacket(player, getMetaPacket(id));
        CustomCrops.getProtocolManager().sendServerPacket(player, getEquipPacket(id, CustomCrops.getInstance().getIntegrationManager().build(animation_id)));
        CustomCrops.getInstance().getScheduler().runTaskAsyncLater(() -> {
            CustomCrops.getProtocolManager().sendServerPacket(player, getDestroyPacket(id));
        }, 10000);
    }

    public static WrappedDataWatcher createInvisibleDataWatcher() {
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer1 = WrappedDataWatcher.Registry.get(Boolean.class);
        WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), false);
        byte flag = 0x20;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), flag);
        return wrappedDataWatcher;
    }

    public static PacketContainer getDestroyPacket(int id) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, List.of(id));
        return destroyPacket;
    }

    public static PacketContainer getSpawnPacket(int id, Location location, EntityType entityType) {
        PacketContainer entityPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        entityPacket.getModifier().write(0, id);
        entityPacket.getModifier().write(1, UUID.randomUUID());
        entityPacket.getEntityTypeModifier().write(0, entityType);
        entityPacket.getDoubles().write(0, location.getX());
        entityPacket.getDoubles().write(1, location.getY());
        entityPacket.getDoubles().write(2, location.getZ());
        return entityPacket;
    }

    public static PacketContainer getMetaPacket(int id) {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getIntegers().write(0, id);
        if (CustomCrops.getInstance().getVersionHelper().isVersionNewerThan1_19_R2()) {
            WrappedDataWatcher wrappedDataWatcher = createInvisibleDataWatcher();
            setWrappedDataValue(metaPacket, wrappedDataWatcher);
        } else {
            metaPacket.getWatchableCollectionModifier().write(0, createInvisibleDataWatcher().getWatchableObjects());
        }
        return metaPacket;
    }

    public static PacketContainer getEquipPacket(int id, ItemStack itemStack) {
        PacketContainer equipPacket = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipPacket.getIntegers().write(0, id);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, itemStack));
        equipPacket.getSlotStackPairLists().write(0, pairs);
        return equipPacket;
    }

    public static PacketContainer getTeleportPacket(int id, Location location, float yaw) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, id);
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());
        packet.getBytes().write(0, (byte) (yaw * (128.0 / 180)));
        return packet;
    }

    public static PacketContainer getMetaPacket(int id, Component component) {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
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
        metaPacket.getModifier().write(0, id);
        if (CustomCrops.getInstance().getVersionHelper().isVersionNewerThan1_19_R2()) {
            setWrappedDataValue(metaPacket, wrappedDataWatcher);
        } else {
            metaPacket.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
        }
        return metaPacket;
    }

    private static void setWrappedDataValue(PacketContainer metaPacket, WrappedDataWatcher wrappedDataWatcher) {
        List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
        wrappedDataWatcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
            final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
            wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
        });
        metaPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
    }
}

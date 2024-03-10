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
import com.comphenix.protocol.wrappers.*;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customcrops.api.manager.AdventureManager;
import net.momirealms.customcrops.api.manager.VersionManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class FakeEntityUtils {

    public static WrappedDataWatcher createInvisibleDataWatcher() {
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        //wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), false);
        byte flag = 0x20;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), flag);
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

    public static PacketContainer getVanishArmorStandMetaPacket(int id) {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getIntegers().write(0, id);
        if (VersionManager.isHigherThan1_19_R2()) {
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

    public static PacketContainer getVanishArmorStandMetaPacket(int id, Component component) {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(component)).getHandle()));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), true);
        byte mask1 = 0x20;
        byte mask2 = 0x01;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), mask1);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, WrappedDataWatcher.Registry.get(Byte.class)), mask2);
        metaPacket.getModifier().write(0, id);
        if (VersionManager.isHigherThan1_19_R2()) {
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

    public static PacketContainer get1_19_4TextDisplayMetaPacket(int id, Component component) {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getModifier().write(0, id);
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(22, WrappedDataWatcher.Registry.getChatComponentSerializer(false)), WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(component)));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(24, WrappedDataWatcher.Registry.get(Integer.class)), AdventureManager.getInstance().rgbaToDecimal("0,0,0,0"));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 3);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(25, WrappedDataWatcher.Registry.get(Byte.class)), (byte) -1);
        int mask = 0;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(26, WrappedDataWatcher.Registry.get(Byte.class)), (byte) mask);
        setWrappedDataValue(metaPacket, wrappedDataWatcher);
        return metaPacket;
    }

    public static PacketContainer get1_20_2TextDisplayMetaPacket(int id, Component component) {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getModifier().write(0, id);
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(23, WrappedDataWatcher.Registry.getChatComponentSerializer(false)), WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(component)));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(25, WrappedDataWatcher.Registry.get(Integer.class)), AdventureManager.getInstance().rgbaToDecimal("0,0,0,0"));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 3);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(26, WrappedDataWatcher.Registry.get(Byte.class)), (byte) -1);
        int mask = 0;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(27, WrappedDataWatcher.Registry.get(Byte.class)), (byte) mask);
        setWrappedDataValue(metaPacket, wrappedDataWatcher);
        return metaPacket;
    }
}

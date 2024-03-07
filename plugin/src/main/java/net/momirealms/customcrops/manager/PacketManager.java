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

package net.momirealms.customcrops.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketManager {

    private static PacketManager instance;
    private final ProtocolManager protocolManager;
    private final CustomCropsPlugin plugin;

    public PacketManager(CustomCropsPlugin plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        instance = this;
    }

    public static PacketManager getInstance() {
        return instance;
    }

    public void send(Player player, PacketContainer packet) {
        this.protocolManager.sendServerPacket(player, packet);
    }

    public void send(Player player, PacketContainer... packets) {
        if (!player.isOnline()) return;
        if (plugin.getVersionManager().isVersionNewerThan1_19_R3()) {
            List<PacketContainer> bundle = new ArrayList<>(Arrays.asList(packets));
            PacketContainer bundlePacket = new PacketContainer(PacketType.Play.Server.BUNDLE);
            bundlePacket.getPacketBundles().write(0, bundle);
            send(player, bundlePacket);
        } else {
            for (PacketContainer packet : packets) {
                send(player, packet);
            }
        }
    }
}

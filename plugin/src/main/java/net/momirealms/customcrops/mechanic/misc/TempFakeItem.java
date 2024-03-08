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

package net.momirealms.customcrops.mechanic.misc;

import com.comphenix.protocol.events.PacketContainer;
import net.momirealms.customcrops.api.CustomCropsPlugin;
import net.momirealms.customcrops.api.mechanic.world.SimpleLocation;
import net.momirealms.customcrops.manager.PacketManager;
import net.momirealms.customcrops.utils.FakeEntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TempFakeItem {

    private final Player[] viewers;
    private final int duration;
    private final int entityID = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    private final Location location;
    private final String item;

    public TempFakeItem(Location location, String item, int duration, Player viewer) {
        SimpleLocation simpleLocation = SimpleLocation.of(location);
        ArrayList<Player> viewers = new ArrayList<>();
        if (viewer == null)
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (simpleLocation.isNear(SimpleLocation.of(player.getLocation()), 48)) {
                    viewers.add(player);
                }
            }
        else {
            viewers.add(viewer);
        }
        this.viewers = viewers.toArray(new Player[0]);
        this.location = location;
        this.item = item;
        this.duration = duration;
    }

    public void start() {
        if (this.viewers.length == 0) return;
        sendPacketToViewers(
                FakeEntityUtils.getSpawnPacket(entityID, location, EntityType.ARMOR_STAND),
                FakeEntityUtils.getVanishArmorStandMetaPacket(entityID),
                FakeEntityUtils.getEquipPacket(entityID, CustomCropsPlugin.get().getItemManager().getItemStack(null, item))
        );
        CustomCropsPlugin.get().getScheduler().runTaskAsyncLater(() -> {
            sendPacketToViewers(FakeEntityUtils.getDestroyPacket(entityID));
        }, duration * 50L, TimeUnit.MILLISECONDS);
    }

    private void sendPacketToViewers(PacketContainer... packet) {
        for (Player viewer : viewers) {
            PacketManager.getInstance().send(viewer, packet);
        }
    }
}

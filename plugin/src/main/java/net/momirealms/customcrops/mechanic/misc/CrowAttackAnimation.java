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
import net.momirealms.customcrops.api.scheduler.CancellableTask;
import net.momirealms.customcrops.manager.PacketManager;
import net.momirealms.customcrops.utils.FakeEntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class CrowAttackAnimation {

    private CancellableTask task;
    private final Location fromLocation;
    private final Vector vectorDown;
    private final Vector vectorUp;
    private final Player[] viewers;
    private int timer;
    private final ItemStack flyModel;
    private final ItemStack standModel;
    private final int entityID = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    private final float yaw = ThreadLocalRandom.current().nextInt(361) - 180;

    public CrowAttackAnimation(SimpleLocation cropSimpleLocation, String flyModel, String standModel) {
        ArrayList<Player> viewers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (cropSimpleLocation.isNear(SimpleLocation.of(player.getLocation()), 48)) {
                viewers.add(player);
            }
        }
        this.viewers = viewers.toArray(new Player[0]);
        Location cropLocation = cropSimpleLocation.getBukkitLocation().add(0.25 + Math.random() * 0.5, 0, 0.25 + Math.random() * 0.5);
        this.flyModel = CustomCropsPlugin.get().getItemManager().getItemStack(null, flyModel);
        this.standModel = CustomCropsPlugin.get().getItemManager().getItemStack(null, standModel);
        this.fromLocation = cropLocation.clone().add((10 * Math.sin((Math.PI * yaw)/180)), 10, (- 10 * Math.cos((Math.PI * yaw)/180)));
        Location relative = cropLocation.clone().subtract(fromLocation);
        this.vectorDown = new Vector(relative.getX() / 100, -0.1, relative.getZ() / 100);
        this.vectorUp = new Vector(relative.getX() / 100, 0.1, relative.getZ() / 100);
    }

    public void start() {
        if (this.viewers.length == 0) return;
        sendPacketToViewers(
                FakeEntityUtils.getSpawnPacket(entityID, fromLocation, EntityType.ARMOR_STAND),
                FakeEntityUtils.getVanishArmorStandMetaPacket(entityID),
                FakeEntityUtils.getEquipPacket(entityID, flyModel)
        );
        this.task = CustomCropsPlugin.get().getScheduler().runTaskAsyncTimer(() -> {
            timer++;
            if (timer < 100) {
                sendPacketToViewers(FakeEntityUtils.getTeleportPacket(entityID, fromLocation.add(vectorDown), yaw));
            } else if (timer == 100){
                sendPacketToViewers(FakeEntityUtils.getEquipPacket(entityID, standModel));
            } else if (timer == 150) {
                sendPacketToViewers(FakeEntityUtils.getEquipPacket(entityID, flyModel));
            } else if (timer > 150) {
                sendPacketToViewers(FakeEntityUtils.getTeleportPacket(entityID, fromLocation.add(vectorUp), yaw));
            }
            if (timer > 300) {
                sendPacketToViewers(FakeEntityUtils.getDestroyPacket(entityID));
                task.cancel();
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
    }

    private void sendPacketToViewers(PacketContainer... packet) {
        for (Player viewer : viewers) {
            PacketManager.getInstance().send(viewer, packet);
        }
    }
}

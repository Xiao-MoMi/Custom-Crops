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

package net.momirealms.customcrops.api.core.block;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;
import net.momirealms.customcrops.api.util.LocationUtils;
import net.momirealms.customcrops.common.plugin.scheduler.SchedulerTask;
import net.momirealms.customcrops.common.util.RandomUtils;
import net.momirealms.sparrow.heart.SparrowHeart;
import net.momirealms.sparrow.heart.feature.entity.armorstand.FakeArmorStand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CrowAttack {

    private SchedulerTask task;
    private final Location dynamicLocation;
    private final Location cropLocation;
    private final Vector vectorDown;
    private final Vector vectorUp;
    private final Player[] viewers;
    private int timer;
    private final ItemStack flyModel;
    private final ItemStack standModel;

    public CrowAttack(Location location, ItemStack flyModel, ItemStack standModel) {
        ArrayList<Player> viewers = new ArrayList<>();
        for (Player player : location.getWorld().getPlayers()) {
            if (LocationUtils.getDistance(player.getLocation(), location) <= 48) {
                viewers.add(player);
            }
        }
        this.viewers = viewers.toArray(new Player[0]);
        this.cropLocation = location.clone().add(RandomUtils.generateRandomDouble(-0.25, 0.25), 0, RandomUtils.generateRandomDouble(-0.25, 0.25));
        float yaw = RandomUtils.generateRandomInt(-180, 180);
        this.cropLocation.setYaw(yaw);
        this.flyModel = flyModel;
        this.standModel = standModel;
        this.dynamicLocation = cropLocation.clone().add((10 * Math.sin((Math.PI * yaw) / 180)), 10, (- 10 * Math.cos((Math.PI * yaw) / 180)));
        this.dynamicLocation.setYaw(yaw);
        Location relative = cropLocation.clone().subtract(dynamicLocation);
        this.vectorDown = new Vector(relative.getX() / 100, -0.1, relative.getZ() / 100);
        this.vectorUp = new Vector(relative.getX() / 100, 0.1, relative.getZ() / 100);
    }

    public void start() {
        if (this.viewers.length == 0) return;
        FakeArmorStand fake1 = SparrowHeart.getInstance().createFakeArmorStand(dynamicLocation);
        fake1.invisible(true);
        fake1.small(true);
        fake1.equipment(EquipmentSlot.HEAD, flyModel);
        FakeArmorStand fake2 = SparrowHeart.getInstance().createFakeArmorStand(cropLocation);
        fake1.invisible(true);
        fake1.small(true);
        fake1.equipment(EquipmentSlot.HEAD, standModel);
        for (Player player : this.viewers) {
            fake1.spawn(player);
        }
        this.task = BukkitCustomCropsPlugin.getInstance().getScheduler().asyncRepeating(() -> {
            timer++;
            if (timer < 100) {
                dynamicLocation.add(vectorDown);
                for (Player player : this.viewers) {
                    SparrowHeart.getInstance().sendClientSideTeleportEntity(player, dynamicLocation, false, fake1.entityID());
                }
            } else if (timer == 100){
                for (Player player : this.viewers) {
                    fake1.destroy(player);
                }
                for (Player player : this.viewers) {
                    fake2.spawn(player);
                }
            } else if (timer == 150) {
                for (Player player : this.viewers) {
                    fake2.destroy(player);
                }
                for (Player player : this.viewers) {
                    fake1.spawn(player);
                }
            } else if (timer > 150) {
                dynamicLocation.add(vectorUp);
                for (Player player : this.viewers) {
                    SparrowHeart.getInstance().sendClientSideTeleportEntity(player, dynamicLocation, false, fake1.entityID());
                }
            }
            if (timer > 300) {
                for (Player player : this.viewers) {
                    fake1.destroy(player);
                }
                task.cancel();
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
    }
}

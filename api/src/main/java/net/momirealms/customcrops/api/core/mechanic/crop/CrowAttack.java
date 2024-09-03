/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customcrops.api.core.mechanic.crop;

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
        Location landLocation = LocationUtils.toBlockCenterLocation(location).add(RandomUtils.generateRandomDouble(-0.25, 0.25), 0, RandomUtils.generateRandomDouble(-0.25, 0.25));
        float yaw = RandomUtils.generateRandomInt(-180, 180);
        landLocation.setYaw(yaw);
        this.flyModel = flyModel;
        this.standModel = standModel;
        this.dynamicLocation = landLocation.clone().add((10 * Math.sin((Math.PI * yaw) / 180)), 10, (- 10 * Math.cos((Math.PI * yaw) / 180)));
        this.dynamicLocation.setYaw(yaw);
        Location relative = landLocation.clone().subtract(dynamicLocation);
        this.vectorDown = new Vector(relative.getX() / 100, -0.1, relative.getZ() / 100);
        this.vectorUp = new Vector(relative.getX() / 100, 0.1, relative.getZ() / 100);
    }

    public void start() {
        if (this.viewers.length == 0) return;
        FakeArmorStand fake = SparrowHeart.getInstance().createFakeArmorStand(dynamicLocation);
        fake.invisible(true);
        fake.small(true);
        fake.equipment(EquipmentSlot.HEAD, flyModel);
        for (Player player : this.viewers) {
            fake.spawn(player);
        }
        this.task = BukkitCustomCropsPlugin.getInstance().getScheduler().asyncRepeating(() -> {
            timer++;
            if (timer < 100) {
                dynamicLocation.add(vectorDown);
                for (Player player : this.viewers) {
                    SparrowHeart.getInstance().sendClientSideTeleportEntity(player, dynamicLocation, false, fake.entityID());
                }
            } else if (timer == 100) {
                fake.equipment(EquipmentSlot.HEAD, standModel);
                for (Player player : this.viewers) {
                    fake.updateEquipment(player);
                }
            } else if (timer == 150) {
                fake.equipment(EquipmentSlot.HEAD, flyModel);
                for (Player player : this.viewers) {
                    fake.updateEquipment(player);
                }
            } else if (timer > 150) {
                dynamicLocation.add(vectorUp);
                for (Player player : this.viewers) {
                    SparrowHeart.getInstance().sendClientSideTeleportEntity(player, dynamicLocation, false, fake.entityID());
                }
            }
            if (timer > 250) {
                for (Player player : this.viewers) {
                    fake.destroy(player);
                }
                task.cancel();
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
    }
}

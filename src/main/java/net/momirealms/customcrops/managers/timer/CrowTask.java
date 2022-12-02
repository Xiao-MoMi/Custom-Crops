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

package net.momirealms.customcrops.managers.timer;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.config.BasicItemConfig;
import net.momirealms.customcrops.utils.ArmorStandUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class CrowTask extends BukkitRunnable {

    private int timer;
    private final int entityID;
    private final ArmorStandUtil armorStandUtil;
    private final Vector vectorDown;
    private final Vector vectorUp;
    private final Location from;
    private final Player player;
    private final float yaw;


    public CrowTask(Player player, Location crop, ArmorStandUtil armorStandUtil) {
        this.timer = 0;
        this.player = player;
        this.armorStandUtil = armorStandUtil;
        this.entityID = new Random().nextInt(10000000);
        this.yaw = new Random().nextInt(361) - 180;
        this.from = crop.clone().add(10 * Math.sin((Math.PI * yaw)/180), 10, - 10 * Math.cos((Math.PI * yaw)/180));
        Location relative = crop.clone().subtract(from);
        this.vectorDown = new Vector(relative.getX() / 100, -0.1, relative.getZ() / 100);
        this.vectorUp = new Vector(relative.getX() / 100, 0.1, relative.getZ() / 100);
        try {
            CustomCrops.protocolManager.sendServerPacket(player, armorStandUtil.getSpawnPacket(entityID, from));
            CustomCrops.protocolManager.sendServerPacket(player, armorStandUtil.getMetaPacket(entityID));
            CustomCrops.protocolManager.sendServerPacket(player, armorStandUtil.getEquipPacket(entityID, armorStandUtil.getCropManager().getCustomInterface().getItemStack(BasicItemConfig.crowFly)));
        }
        catch (InvocationTargetException ignore) {
        }
    }

    @Override
    public void run() {
        timer++;
        if (timer < 100) {
            try {
                CustomCrops.protocolManager.sendServerPacket(player, armorStandUtil.getTeleportPacket(entityID, from.add(vectorDown), yaw));
            }
            catch (InvocationTargetException e) {
                //release
            }
        }
        else if (timer == 100){
            try {
                CustomCrops.protocolManager.sendServerPacket(player, armorStandUtil.getEquipPacket(entityID, armorStandUtil.getCropManager().getCustomInterface().getItemStack(BasicItemConfig.crowLand)));
            }
            catch (InvocationTargetException ignore) {
            }
        }
        else if (timer == 150) {
            try {
                CustomCrops.protocolManager.sendServerPacket(player, armorStandUtil.getEquipPacket(entityID, armorStandUtil.getCropManager().getCustomInterface().getItemStack(BasicItemConfig.crowFly)));
            }
            catch (InvocationTargetException ignore) {
            }
        }
        else if (timer > 150) {
            try {
                CustomCrops.protocolManager.sendServerPacket(player, armorStandUtil.getTeleportPacket(entityID, from.add(vectorUp), yaw));
            }
            catch (InvocationTargetException ignore) {
            }
        }
        if (timer > 300) {
            try {
                CustomCrops.protocolManager.sendServerPacket(player, armorStandUtil.getDestroyPacket(entityID));
            }
            catch (InvocationTargetException ignore) {
            }
            cancel();
        }
    }
}
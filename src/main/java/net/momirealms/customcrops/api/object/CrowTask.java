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

package net.momirealms.customcrops.api.object;

import com.willfp.eco.core.items.Items;
import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.util.ArmorStandUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class CrowTask extends BukkitRunnable {

    private int timer;
    private final int entityID;
    private final Vector vectorDown;
    private final Vector vectorUp;
    private final Location from;
    private final Player player;
    private final float yaw;
    private final ItemStack fly;
    private final ItemStack stand;

    public CrowTask(Player player, Location crop, String fly_model, String stand_model) {
        this.timer = 0;
        this.fly = CustomCrops.getInstance().getIntegrationManager().build(fly_model);
        this.stand = CustomCrops.getInstance().getIntegrationManager().build(stand_model);
        this.player = player;
        this.entityID = new Random().nextInt(10000000);
        this.yaw = new Random().nextInt(361) - 180;
        this.from = crop.clone().add(10 * Math.sin((Math.PI * yaw)/180), 10, - 10 * Math.cos((Math.PI * yaw)/180));
        Location relative = crop.clone().subtract(from);
        this.vectorDown = new Vector(relative.getX() / 100, -0.1, relative.getZ() / 100);
        this.vectorUp = new Vector(relative.getX() / 100, 0.1, relative.getZ() / 100);
        CustomCrops.getProtocolManager().sendServerPacket(player, ArmorStandUtils.getSpawnPacket(entityID, from));
        CustomCrops.getProtocolManager().sendServerPacket(player, ArmorStandUtils.getMetaPacket(entityID));
        CustomCrops.getProtocolManager().sendServerPacket(player, ArmorStandUtils.getEquipPacket(entityID, fly));
    }

    @Override
    public void run() {
        timer++;
        if (timer < 100) {
            CustomCrops.getProtocolManager().sendServerPacket(player, ArmorStandUtils.getTeleportPacket(entityID, from.add(vectorDown), yaw));
        } else if (timer == 100){
            CustomCrops.getProtocolManager().sendServerPacket(player, ArmorStandUtils.getEquipPacket(entityID, stand));
        } else if (timer == 150) {
            CustomCrops.getProtocolManager().sendServerPacket(player, ArmorStandUtils.getEquipPacket(entityID, fly));
        } else if (timer > 150) {
            CustomCrops.getProtocolManager().sendServerPacket(player, ArmorStandUtils.getTeleportPacket(entityID, from.add(vectorUp), yaw));
        }
        if (timer > 300) {
            CustomCrops.getProtocolManager().sendServerPacket(player, ArmorStandUtils.getDestroyPacket(entityID));
            cancel();
        }
    }
}